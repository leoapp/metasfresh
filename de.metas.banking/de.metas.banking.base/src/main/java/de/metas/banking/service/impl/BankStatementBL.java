package de.metas.banking.service.impl;

/*
 * #%L
 * de.metas.banking.base
 * %%
 * Copyright (C) 2015 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.google.common.annotations.VisibleForTesting;
import de.metas.banking.api.BankAccountId;
import de.metas.banking.api.IBPBankAccountDAO;
import de.metas.bpartner.BPartnerId;
import de.metas.document.engine.DocStatus;
import de.metas.payment.PaymentId;
import de.metas.payment.TenderType;
import de.metas.payment.api.DefaultPaymentBuilder;
import de.metas.payment.api.IPaymentBL;
import lombok.NonNull;
import org.adempiere.ad.dao.IQueryBL;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.InterfaceWrapperHelper;
import org.adempiere.service.ClientId;
import org.adempiere.util.LegacyAdapters;
import org.compiere.model.I_C_BankStatement;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_Payment;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MPeriod;
import org.compiere.model.X_C_DocType;
import org.compiere.util.TimeUtil;
import org.slf4j.Logger;

import de.metas.acct.api.IFactAcctDAO;
import de.metas.banking.interfaces.I_C_BankStatementLine_Ref;
import de.metas.banking.model.I_C_BankStatementLine;
import de.metas.banking.service.IBankStatementBL;
import de.metas.banking.service.IBankStatementDAO;
import de.metas.banking.service.IBankStatementListener;
import de.metas.banking.service.IBankStatementListenerService;
import de.metas.currency.CurrencyConversionContext;
import de.metas.currency.ICurrencyBL;
import de.metas.logging.LogManager;
import de.metas.money.CurrencyConversionTypeId;
import de.metas.money.CurrencyId;
import de.metas.organization.OrgId;
import de.metas.util.Check;
import de.metas.util.Services;

public class BankStatementBL implements IBankStatementBL
{
	private final Logger logger = LogManager.getLogger(getClass());

	@Override
	public void handleAfterPrepare(final I_C_BankStatement bankStatement)
	{
		final IBankStatementDAO bankStatementDAO = Services.get(IBankStatementDAO.class);

		final MBankStatement bankStatementPO = LegacyAdapters.convertToPO(bankStatement);
		for (final MBankStatementLine linePO : bankStatementPO.getLines(false))
		{
			final I_C_BankStatementLine line = InterfaceWrapperHelper.create(linePO, I_C_BankStatementLine.class);

			if (line.isMultiplePaymentOrInvoice() && line.isMultiplePayment())
			{
				// Payment in C_BankStatementLine_Ref are mandatory
				for (final I_C_BankStatementLine_Ref refLine : bankStatementDAO.retrieveLineReferences(line))
				{
					if (refLine.getC_Payment_ID() <= 0)
					{
						// TODO -> AD_Message
						throw new AdempiereException("Missing payment in reference line "
								+ refLine.getLine() + " of line "
								+ line.getLine());
					}
				}
			}
		}
	}

	@Override
	public void handleAfterComplete(final I_C_BankStatement bankStatement)
	{
		final IBankStatementDAO bankStatementDAO = Services.get(IBankStatementDAO.class);

		final MBankStatement bankStatementPO = LegacyAdapters.convertToPO(bankStatement);
		for (final MBankStatementLine linePO : bankStatementPO.getLines(false))
		{
			final I_C_BankStatementLine line = InterfaceWrapperHelper.create(linePO, I_C_BankStatementLine.class);

			createFindUnreconciledPaymentsAndLinkToBankStatementLine(line);
			reconcilePaymentsFromBankStatementLine_Ref(bankStatementDAO, line);
		}
	}

	@VisibleForTesting
	void createFindUnreconciledPaymentsAndLinkToBankStatementLine(final I_C_BankStatementLine line)
	{
		// a payment is already linked
		if (line.getC_Payment_ID() > 0)
		{
			return;
		}

		final boolean isReceipt = line.getStmtAmt().signum() >= 0;
		final BigDecimal expectedPaymentAmount = isReceipt ? line.getStmtAmt() : line.getStmtAmt().negate();

		final List<I_C_Payment> possiblePayments = Services.get(IQueryBL.class)
				.createQueryBuilder(I_C_Payment.class)
				.addOnlyActiveRecordsFilter()
				.addEqualsFilter(I_C_Payment.COLUMNNAME_DocStatus, DocStatus.Completed)
				.addEqualsFilter(I_C_Payment.COLUMN_IsReconciled, false)
				.addEqualsFilter(I_C_Payment.COLUMN_IsReceipt, isReceipt)
				.addEqualsFilter(I_C_Payment.COLUMNNAME_C_BPartner_ID, line.getC_BPartner_ID())
				.addEqualsFilter(I_C_Payment.COLUMN_PayAmt, expectedPaymentAmount)
				.addEqualsFilter(I_C_Payment.COLUMNNAME_C_Currency_ID, line.getC_Currency_ID())
				.create()
				.list(I_C_Payment.class);

		// don't create a new Payment and don't link any of the payments.
		// the user must fix this case manually
		if (possiblePayments.size() > 1)
		{
			return;
		}

		if (possiblePayments.size() == 1)
		{
			line.setC_Payment_ID(possiblePayments.get(0).getC_Payment_ID());
			InterfaceWrapperHelper.save(line);
			return;
		}

		//
		// no payment exists for this line, so create one now
		if (line.getC_BPartner_ID() <= 0)
		{
			return;
		}

		final CurrencyId currencyId = CurrencyId.ofRepoId(line.getC_Currency_ID());
		final BPartnerId bpartnerId = BPartnerId.ofRepoId(line.getC_BPartner_ID());
		final OrgId orgId = OrgId.ofRepoId(line.getAD_Org_ID());
		final LocalDate statementLineDate = TimeUtil.asLocalDate(line.getStatementLineDate());

		final Optional<BankAccountId> bankAccountIdOptional = Services.get(IBPBankAccountDAO.class).retrieveFirstIdByBPartnerAndCurrency(bpartnerId, currencyId);
		if (!bankAccountIdOptional.isPresent())
		{
			return;
		}

		final PaymentId createdPaymentId = createAndCompletePaymentNoInvoice(bankAccountIdOptional.get(), statementLineDate, expectedPaymentAmount, isReceipt, orgId, bpartnerId, currencyId);
		line.setC_Payment_ID(createdPaymentId.getRepoId());
		InterfaceWrapperHelper.save(line);
	}

	private PaymentId createAndCompletePaymentNoInvoice(
			@NonNull final BankAccountId bankAccountId,
			@NonNull final LocalDate dateAcct,
			@NonNull final BigDecimal payAmt,
			final boolean isReceipt,
			@NonNull final OrgId adOrgId,
			@NonNull final BPartnerId bpartnerId,
			@NonNull final CurrencyId currencyId)
	{
		final DefaultPaymentBuilder paymentBuilder;

		if (isReceipt)
		{
			paymentBuilder = Services.get(IPaymentBL.class).newInboundReceiptBuilder();
		}
		else
		{
			paymentBuilder = Services.get(IPaymentBL.class).newOutboundPaymentBuilder();
		}

		final I_C_Payment payment = paymentBuilder
				.adOrgId(adOrgId)
				.bpartnerId(bpartnerId)
				.bpBankAccountId(bankAccountId)
				.currencyId(currencyId)
				.payAmt(payAmt)
				.dateAcct(dateAcct)
				.dateTrx(dateAcct)
				.description("Automatically created when BankStatement was completed.")
				.tenderType(TenderType.DirectDeposit)
				.createAndProcess();

		return PaymentId.ofRepoId(payment.getC_Payment_ID());
	}

	private void reconcilePaymentsFromBankStatementLine_Ref(final IBankStatementDAO bankStatementDAO, final I_C_BankStatementLine line)
	{
		if (line.isMultiplePaymentOrInvoice() && line.isMultiplePayment())
		{
			for (final I_C_BankStatementLine_Ref refLine : bankStatementDAO.retrieveLineReferences(line))
			{
				if (refLine.getC_Payment_ID() > 0)
				{
					final I_C_Payment payment = refLine.getC_Payment();
					payment.setIsReconciled(true);
					InterfaceWrapperHelper.save(payment);
				}
			}
		}
	}

	@Override
	public void handleBeforeVoid(final I_C_BankStatement bankStatement)
	{
		final IBankStatementDAO bankStatementDAO = Services.get(IBankStatementDAO.class);
		final IBankStatementListener listeners = Services.get(IBankStatementListenerService.class).getListeners();

		final MBankStatement bankStatementPO = LegacyAdapters.convertToPO(bankStatement);
		for (final MBankStatementLine linePO : bankStatementPO.getLines(false))
		{
			final I_C_BankStatementLine line = InterfaceWrapperHelper.create(linePO, I_C_BankStatementLine.class);

			listeners.onBankStatementLineVoiding(line);

			if (line.isMultiplePaymentOrInvoice() && line.isMultiplePayment())
			{
				// NOTE: for line, the payment is unlinked in MBankStatement.voidIt()

				for (final I_C_BankStatementLine_Ref refLine : bankStatementDAO.retrieveLineReferences(line))
				{
					//
					// Unlink payment
					if (refLine.getC_Payment_ID() > 0)
					{
						final I_C_Payment payment = refLine.getC_Payment();
						payment.setIsReconciled(false);
						InterfaceWrapperHelper.save(payment);
						refLine.setC_Payment(null);

						InterfaceWrapperHelper.save(refLine);
					}
				}
			}
		}

	}

	@Override
	public void recalculateStatementLineAmounts(org.compiere.model.I_C_BankStatementLine bankStatementLine)
	{
		final I_C_BankStatementLine bsl = InterfaceWrapperHelper.create(bankStatementLine, I_C_BankStatementLine.class);
		if (!bsl.isMultiplePaymentOrInvoice())
		{
			return;
		}

		final IBankStatementDAO bankStatementDAO = Services.get(IBankStatementDAO.class);
		final ICurrencyBL currencyConversionBL = Services.get(ICurrencyBL.class);

		//
		// Aggregated amounts from reference lines:
		BigDecimal totalStmtAmt = BigDecimal.ZERO;
		BigDecimal totalTrxAmt = BigDecimal.ZERO;
		BigDecimal totalDiscountAmt = BigDecimal.ZERO;
		BigDecimal totalWriteOffAmt = BigDecimal.ZERO;
		BigDecimal totalOverUnderAmt = BigDecimal.ZERO;

		//
		// Iterate all reference lines and build up the aggregated amounts
		for (final I_C_BankStatementLine_Ref refLine : bankStatementDAO.retrieveLineReferences(bsl))
		{
			if (refLine.getC_Currency_ID() == bsl.getC_Currency_ID())
			{
				totalStmtAmt = totalStmtAmt.add(refLine.getTrxAmt());
				totalTrxAmt = totalTrxAmt.add(refLine.getTrxAmt());
				totalDiscountAmt = totalDiscountAmt.add(refLine.getDiscountAmt());
				totalWriteOffAmt = totalWriteOffAmt.add(refLine.getWriteOffAmt());
				totalOverUnderAmt = totalOverUnderAmt.add(refLine.getOverUnderAmt());
			}
			else
			{
				Check.assume(refLine.getC_Invoice_ID() > 0, "@NotFound@ @C_Invoice_ID@");
				final I_C_Invoice inv = refLine.getC_Invoice();

				final CurrencyConversionContext conversionCtx = currencyConversionBL.createCurrencyConversionContext(
						TimeUtil.asLocalDate(bsl.getDateAcct()), // ConvDate,
						CurrencyConversionTypeId.ofRepoIdOrNull(inv.getC_ConversionType_ID()), // ConversionType_ID,
						ClientId.ofRepoId(bsl.getAD_Client_ID()), // AD_Client_ID
						OrgId.ofRepoId(bsl.getAD_Org_ID()) // AD_Org_ID
				);

				final CurrencyId refLineCurrencyId = CurrencyId.ofRepoId(refLine.getC_Currency_ID());
				final CurrencyId bslCurrencyId = CurrencyId.ofRepoId(bsl.getC_Currency_ID());

				final BigDecimal trxAmt = currencyConversionBL.convert(conversionCtx,
						refLine.getTrxAmt(),
						refLineCurrencyId, // CurFrom_ID,
						bslCurrencyId) // CurTo_ID
						.getAmount();
				final BigDecimal discountAmt = currencyConversionBL.convert(conversionCtx,
						refLine.getDiscountAmt(),
						refLineCurrencyId, // CurFrom_ID,
						bslCurrencyId) // CurTo_ID
						.getAmount();
				final BigDecimal writeOffAmt = currencyConversionBL.convert(conversionCtx,
						refLine.getWriteOffAmt(),
						refLineCurrencyId, // CurFrom_ID,
						bslCurrencyId) // CurTo_ID
						.getAmount();
				final BigDecimal overUnderAmt = currencyConversionBL.convert(conversionCtx,
						refLine.getOverUnderAmt(),
						refLineCurrencyId, // CurFrom_ID,
						bslCurrencyId) // CurTo_ID
						.getAmount();

				totalStmtAmt = totalStmtAmt.add(trxAmt);
				totalTrxAmt = totalTrxAmt.add(trxAmt);
				totalDiscountAmt = totalDiscountAmt.add(discountAmt);
				totalWriteOffAmt = totalWriteOffAmt.add(writeOffAmt);
				totalOverUnderAmt = totalOverUnderAmt.add(overUnderAmt);
			}
		}

		//
		// Update the bank statement line with calculated aggregated amounts
		if (logger.isDebugEnabled())
		{
			logger.debug(" stmtAmt: " + totalStmtAmt + " discountAmt: " + totalDiscountAmt + " writeOffAmt: " + totalWriteOffAmt + " overUnderAmt: " + totalOverUnderAmt);
		}
		bsl.setStmtAmt(totalStmtAmt);
		bsl.setTrxAmt(totalTrxAmt);
		bsl.setDiscountAmt(totalDiscountAmt);
		bsl.setWriteOffAmt(totalWriteOffAmt);
		bsl.setOverUnderAmt(totalOverUnderAmt);
		if (totalOverUnderAmt.signum() != 0)
		{
			bsl.setIsOverUnderPayment(true);
		}
		else
		{
			bsl.setIsOverUnderPayment(false);
		}
		InterfaceWrapperHelper.save(bsl);
	}

	@Override
	public void updateEndingBalance(final I_C_BankStatement bankStatement)
	{
		final BigDecimal endingBalance = bankStatement
				.getBeginningBalance()
				.add(bankStatement.getStatementDifference());

		bankStatement.setEndingBalance(endingBalance);
	}

	@Override
	public void unpost(final I_C_BankStatement bankStatement)
	{
		// Make sure the period is open
		final Properties ctx = InterfaceWrapperHelper.getCtx(bankStatement);
		MPeriod.testPeriodOpen(ctx, bankStatement.getStatementDate(), X_C_DocType.DOCBASETYPE_BankStatement, bankStatement.getAD_Org_ID());

		Services.get(IFactAcctDAO.class).deleteForDocumentModel(bankStatement);

		bankStatement.setPosted(false);
		InterfaceWrapperHelper.save(bankStatement);
	}
}

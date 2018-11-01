package org.adempiere.acct.api.impl;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.adempiere.acct.api.AccountDimension;
import org.adempiere.acct.api.AcctSchemaElementType;
import org.adempiere.acct.api.AcctSchemaId;
import org.adempiere.acct.api.IAccountDimensionValidator;
import org.adempiere.acct.api.IAcctSchemaDAO;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.FillMandatoryException;
import org.adempiere.model.InterfaceWrapperHelper;
import org.compiere.model.I_C_AcctSchema;
import org.compiere.model.I_C_AcctSchema_Element;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.X_C_AcctSchema_Element;

import de.metas.i18n.IMsgBL;
import de.metas.util.Check;
import de.metas.util.Services;
import lombok.NonNull;

/* package */class AccountDimensionValidator implements IAccountDimensionValidator
{
	// Services
	private final transient IAcctSchemaDAO acctSchemaDAO = Services.get(IAcctSchemaDAO.class);
	private final transient IMsgBL msgBL = Services.get(IMsgBL.class);

	private final Properties _ctx;
	private final I_C_AcctSchema _acctSchema;
	private List<I_C_AcctSchema_Element> acctSchemaElements;

	public AccountDimensionValidator(final I_C_AcctSchema acctSchema)
	{
		super();

		Check.assumeNotNull(acctSchema, "acctSchema not null");
		this._acctSchema = acctSchema;
		this._ctx = InterfaceWrapperHelper.getCtx(acctSchema);
	}

	@Override
	public void setAcctSchemaElements(final List<I_C_AcctSchema_Element> acctSchemaElements)
	{
		this.acctSchemaElements = acctSchemaElements;
	}

	private Properties getCtx()
	{
		return _ctx;
	}

	@Override
	public I_C_AcctSchema getC_AcctSchema()
	{
		return _acctSchema;
	}

	@Override
	public List<I_C_AcctSchema_Element> getAcctSchemaElements()
	{
		if (acctSchemaElements == null)
		{
			I_C_AcctSchema acctSchema = getC_AcctSchema();
			acctSchemaElements = acctSchemaDAO.retrieveSchemaElementsDisplayedInEditor(acctSchema);
		}

		return acctSchemaElements;
	}

	@Override
	public void validate(final AccountDimension accountDimension)
	{

		final I_C_AcctSchema acctSchema = getC_AcctSchema();
		final AcctSchemaId acctSchemaId = AcctSchemaId.ofRepoId(acctSchema.getC_AcctSchema_ID());

		final Set<String> mandatoryFieldsNotFilled = new HashSet<>();

		//
		// Validate C_AcctSchema_ID
		if (AcctSchemaId.equals(accountDimension.getAcctSchemaId(), acctSchemaId))
		{
			throw new AdempiereException("C_AcctSchema_ID not matched"
					+ "\n Expected: " + acctSchema
					+ "\n Was: " + accountDimension.getAcctSchemaId());
		}

		//
		// Validate Alias
		if (acctSchema.isHasAlias())
		{
			final String alias = accountDimension.getAlias();

			if (Check.isEmpty(alias, true))
			{
				mandatoryFieldsNotFilled.add(msgBL.translate(getCtx(), I_C_ValidCombination.COLUMNNAME_Alias));
			}
		}

		//
		// Validate: AD_Client_ID
		if (accountDimension.getAD_Client_ID() <= 0)
		{
			mandatoryFieldsNotFilled.add(msgBL.translate(getCtx(), I_C_ValidCombination.COLUMNNAME_AD_Client_ID));
		}

		//
		// Validate segments
		final List<I_C_AcctSchema_Element> elements = acctSchemaDAO.retrieveSchemaElementsDisplayedInEditor(acctSchema);
		for (final I_C_AcctSchema_Element ase : elements)
		{
			final AcctSchemaElementType elementType = AcctSchemaElementType.ofCode(ase.getElementType());
			final int segmentId = getSegmentValueId(accountDimension, elementType);
			if (ase.isMandatory() && segmentId <= 0)
			{
				mandatoryFieldsNotFilled.add(ase.getName());
			}
		}

		//
		// If we found segments not set throw exception
		if (!mandatoryFieldsNotFilled.isEmpty())
		{
			throw new FillMandatoryException(true, mandatoryFieldsNotFilled);
		}

		//
		// C_ElementValue_ID (i.e. the account number) shall be set
		if (accountDimension.getC_ElementValue_ID() <= 0)
		{
			throw new FillMandatoryException(I_C_ValidCombination.COLUMNNAME_Account_ID);
		}
	}

	/**
	 * 
	 * @param accountDimension
	 * @param elementType see {@link X_C_AcctSchema_Element}.ELEMENTTYPE_*
	 * @return segment's value (ID)
	 */
	private final int getSegmentValueId(final AccountDimension accountDimension, @NonNull final AcctSchemaElementType elementType)
	{
		Check.assumeNotNull(elementType, "elementType not null");

		if (elementType.equals(AcctSchemaElementType.Organization))
		{
			return accountDimension.getAD_Org_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.Account))
		{
			return accountDimension.getC_ElementValue_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.SubAccount))
		{
			return accountDimension.getC_SubAcct_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.Product))
		{
			return accountDimension.getM_Product_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.BPartner))
		{
			return accountDimension.getC_BPartner_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.Campaign))
		{
			return accountDimension.getC_Campaign_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.LocationFrom))
		{
			return accountDimension.getC_LocFrom_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.LocationTo))
		{
			return accountDimension.getC_LocTo_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.Project))
		{
			return accountDimension.getC_Project_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.SalesRegion))
		{
			return accountDimension.getC_SalesRegion_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.OrgTrx))
		{
			return accountDimension.getAD_OrgTrx_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.Activity))
		{
			return accountDimension.getC_Activity_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.UserList1))
		{
			return accountDimension.getUser1_ID();
		}
		else if (elementType.equals(AcctSchemaElementType.UserList2))
		{
			return accountDimension.getUser2_ID();
		}
		else
		{
			// Unknown
			return -1;
		}
	}

}

package de.metas.currency;

import java.util.Arrays;

import org.adempiere.exceptions.AdempiereException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

/*
 * #%L
 * de.metas.business
 * %%
 * Copyright (C) 2019 metas GmbH
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

/**
 * Three letter ISO 4217 Code of the Currency
 */
@EqualsAndHashCode
public final class CurrencyCode
{
	@JsonCreator
	public static CurrencyCode ofThreeLetterCode(@NonNull final String threeLetterCode)
	{
		final CurrencyCode cachedCurrencyCode = cache.get(threeLetterCode);
		return cachedCurrencyCode != null ? cachedCurrencyCode : new CurrencyCode(threeLetterCode);
	}

	public static final CurrencyCode EUR = new CurrencyCode("EUR");
	public static final CurrencyCode USD = new CurrencyCode("USD");
	public static final CurrencyCode CHF = new CurrencyCode("CHF");
	private static final ImmutableMap<String, CurrencyCode> cache = Maps.uniqueIndex(
			Arrays.asList(EUR, USD, CHF),
			CurrencyCode::toThreeLetterCode);

	private final String threeLetterCode;

	private CurrencyCode(@NonNull final String threeLetterCode)
	{
		if (threeLetterCode.length() != 3)
		{
			throw new AdempiereException("Invalid currency ISO 4217 code: " + threeLetterCode);
		}

		this.threeLetterCode = threeLetterCode;
	}

	@Override
	@Deprecated
	public String toString()
	{
		return threeLetterCode;
	}

	@JsonValue
	public String toThreeLetterCode()
	{
		return threeLetterCode;
	}

	public boolean isEuro()
	{
		return this.equals(EUR);
	}

	public boolean isCHF()
	{
		return this.equals(CHF);
	}
}

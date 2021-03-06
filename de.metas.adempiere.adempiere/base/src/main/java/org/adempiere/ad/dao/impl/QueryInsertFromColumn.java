package org.adempiere.ad.dao.impl;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */


import java.util.List;

import org.adempiere.model.InterfaceWrapperHelper;
import org.adempiere.util.lang.ObjectUtils;

import de.metas.util.Check;

/**
 * Set the column in target model, using the value of a column from the source model.
 *
 * @author tsa
 *
 */
class QueryInsertFromColumn implements IQueryInsertFromColumn
{
	private final String columnName;

	public QueryInsertFromColumn(final String columnName)
	{
		super();
		Check.assumeNotEmpty(columnName, "columnName not empty");
		this.columnName = columnName;
	}

	@Override
	public String toString()
	{
		return ObjectUtils.toString(this);
	}

	@Override
	public String getSql(final List<Object> sqlParams)
	{
		return columnName;
	}

	@Override
	public boolean update(final Object toModel, final String toColumnName, final Object fromModel)
	{
		final Object value = InterfaceWrapperHelper.getValue(fromModel, toColumnName).orElse(null);
		InterfaceWrapperHelper.setValue(toModel, toColumnName, value);
		return true;
	}
}

package de.metas.dunning.modelvalidator;

/*
 * #%L
 * de.metas.dunning
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


import org.adempiere.ad.modelvalidator.annotations.ModelChange;
import org.adempiere.ad.modelvalidator.annotations.Validator;
import org.compiere.model.ModelValidator;

import de.metas.dunning.api.IDunningBL;
import de.metas.dunning.model.I_C_Dunning_Candidate;
import de.metas.util.Services;

@Validator(I_C_Dunning_Candidate.class)
public class C_Dunning_Candidate
{
	/**
	 * Enforce all class invariants (i.e. rules which shall be respected ON ANY time by given {@link I_C_Dunning_Candidate} object)
	 * 
	 * @param candidate
	 * @see IDunningBL#validate(I_C_Dunning_Candidate)
	 */
	@ModelChange(timings = { ModelValidator.TYPE_BEFORE_NEW, ModelValidator.TYPE_BEFORE_CHANGE })
	public void validate(final I_C_Dunning_Candidate candidate)
	{
		Services.get(IDunningBL.class).validate(candidate);
	}
}

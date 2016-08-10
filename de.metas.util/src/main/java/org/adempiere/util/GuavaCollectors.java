package org.adempiere.util;

import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/*
 * #%L
 * de.metas.util
 * %%
 * Copyright (C) 2016 metas GmbH
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

/**
 *
 * @author based on https://gist.github.com/JakeWharton/9734167
 * @author metas-dev <dev@metasfresh.com>
 */
public final class GuavaCollectors
{
	/**
	 * Collect a stream of elements into an {@link ImmutableList}.
	 */
	public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> toImmutableList()
	{
		return Collector.of(
				ImmutableList.Builder::new // supplier
				, ImmutableList.Builder::add // accumulator
				, (l, r) -> l.addAll(r.build()) // combiner
				, ImmutableList.Builder<T>::build // finisher
		);
	}

	/**
	 * Collect a stream of elements into an {@link ImmutableSet}.
	 */
	public static <T> Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> toImmutableSet()
	{
		return Collector.of(
				ImmutableSet.Builder::new // supplier
				, ImmutableSet.Builder::add // accumulator
				, (l, r) -> l.addAll(r.build()) // combiner
				, ImmutableSet.Builder<T>::build // finisher
				, Collector.Characteristics.UNORDERED // characteristics
		);
	}

	private GuavaCollectors()
	{
	}
}

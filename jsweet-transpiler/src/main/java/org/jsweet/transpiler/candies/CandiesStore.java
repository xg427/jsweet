/* 
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.transpiler.candies;

import java.util.LinkedList;
import java.util.List;

/**
 * A class that is serialized to store information on the processed candies.
 */
class CandiesStore {
	List<CandyDescriptor> candies = new LinkedList<>();

	public CandiesStore() {
		this(new LinkedList<CandyDescriptor>());
	}

	public CandiesStore(List<CandyDescriptor> candiesDescriptors) {
		this.candies = candiesDescriptors;
	}

	@Override
	public int hashCode() {
		return candies.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CandiesStore)) {
			return false;
		}

		CandiesStore other = (CandiesStore) obj;
		return candies.size() == other.candies.size() && candies.containsAll(other.candies);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "=" + candies;
	}
}
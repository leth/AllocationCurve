/*
	Copyright: Marcus Cobden (2011)
	This file is part of AllocationCurve.

	AllocationCurve is free software: you can redistribute it and/or modify
	it under the terms of version 3 of the GNU Lesser General Public License
	as published by the Free Software Foundation.

	AllocationCurve is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public License
	along with AllocationCurve. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.marcuscobden.allocationcurve.allocation;

import java.net.Inet4Address;

public class Inet4NetworkAllocationBlock extends
		InetNetworkAllocationBlock<Inet4Address>
		implements
			Comparable<Inet4NetworkAllocationBlock>
{

	public Inet4NetworkAllocationBlock(final Inet4Address address,
			final int size)
	{
		super(address, size);

		if (size > 32)
			throw new IllegalArgumentException(
					"IPv4 Block size cannot exceed a /32. (" + address + "/"
							+ size + ")");
	}

	public int compareTo(final Inet4NetworkAllocationBlock o)
	{
		return super.compareTo(o);
	}
}

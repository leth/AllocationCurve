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

import java.net.Inet6Address;

public class Inet6NetworkAllocationBlock extends
		InetNetworkAllocationBlock<Inet6Address>
		implements
			Comparable<Inet6NetworkAllocationBlock>
{

	public Inet6NetworkAllocationBlock(final Inet6Address address,
			final int size)
	{
		super(address, size);

		if (size > 128)
			throw new IllegalArgumentException("IPv6 Block size cannot exceed a /128. (" + address + "/" + size + ")");
	}

	public int compareTo(final Inet6NetworkAllocationBlock o)
	{
		return super.compareTo(o);
	}

}

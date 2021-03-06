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
package uk.co.marcuscobden.allocationcurve.inetaddress;

import java.net.Inet6Address;
import java.util.Comparator;

public class Inet6AddressComparator extends InetAddressComparator
		implements
			Comparator<Inet6Address>
{

	public int compare(final Inet6Address a, final Inet6Address b)
	{
		return InetAddressComparator.compare(a, b);
	}

}

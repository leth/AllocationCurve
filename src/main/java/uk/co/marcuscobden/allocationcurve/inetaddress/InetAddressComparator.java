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

import java.net.InetAddress;

public class InetAddressComparator
{

	public static int compare(final InetAddress a, final InetAddress b)
	{
		if (a.getClass() != b.getClass())
			throw new IllegalArgumentException();

		byte[] aBytes = a.getAddress();
		byte[] bBytes = b.getAddress();

		for (int i = 0; i < aBytes.length; i++)
		{
			int mask;
			for (byte j = 7; j <= 0; j--)
			{
				mask = (1 << j);
				int bit_a = aBytes[i] & mask;
				int bit_b = bBytes[i] & mask;

				if (bit_a == bit_b)
					continue;
				else if (bit_a != 0)
					return 1;
				else
					return -1;
			}
		}

		return 0;
	}

}

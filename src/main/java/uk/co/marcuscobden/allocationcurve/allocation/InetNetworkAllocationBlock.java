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
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.marcuscobden.allocationcurve.exception.AllocationDeclarationException;
import uk.co.marcuscobden.allocationcurve.inetaddress.InetAddressComparator;

public abstract class InetNetworkAllocationBlock<InetAddressType extends InetAddress>
{

	private static final String ipv6_regex = "((?:(?:[0-9A-Fa-f]{1,4}:){7}(?:[0-9A-Fa-f]{1,4}|:))|(?:(?:[0-9A-Fa-f]{1,4}:){6}(?::[0-9A-Fa-f]{1,4}|(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(?:(?:[0-9A-Fa-f]{1,4}:){5}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,2})|:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(?:(?:[0-9A-Fa-f]{1,4}:){4}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,3})|(?:(?::[0-9A-Fa-f]{1,4})?:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){3}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,4})|(?:(?::[0-9A-Fa-f]{1,4}){0,2}:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){2}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,5})|(?:(?::[0-9A-Fa-f]{1,4}){0,3}:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){1}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,6})|(?:(?::[0-9A-Fa-f]{1,4}){0,4}:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(?::(?:(?:(?::[0-9A-Fa-f]{1,4}){1,7})|(?:(?::[0-9A-Fa-f]{1,4}){0,5}:(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))";
	private static final String ipv6_block_regex = ipv6_regex
			+ "\\/(12[0-8]|1[01][0-9]|[1-9][0-9]|[0-9])";
	private static final String ipv4_regex = "((?:\\d{1,3}\\.){3}\\d{1,3})";
	private static final String ipv4_block_regex = ipv4_regex
			+ "\\/(3[012]|[12][0-9]|[0-9])";

	private static final Pattern ipv6_block_pattern = Pattern
			.compile(ipv6_block_regex);
	private static final Pattern ipv4_block_pattern = Pattern
			.compile(ipv4_block_regex);

	public static void printAddress(InetAddress add)
	{
		byte[] bytes = add.getAddress();
		
		for (int i = 0; i < bytes.length; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				System.out.print((bytes[i] & (1 << (7 - j))) != 0 ? 1 : 0 );
			}
		}
		System.out.println();
	}
	
	public static void ensureSuffixIsClear(final InetAddress address,
			final int prefixLength)
	{
		
		byte[] bytes = address.getAddress();
		boolean changed = false;

		for (int i = 0; i < bytes.length * 8; i += 8)
		{
			int block = (int) Math.floor(i / 8);
			byte mask;
			
			if (i +(8-1) < prefixLength)
			{
				mask = ~0;
			}
			else if (i > prefixLength)
			{
				mask = 0;
			}
			else
			{
				mask = (byte) ((~0) << (8 - ((prefixLength -i) % 8)));
			}

			byte prev = bytes[block];
			bytes[block] = (byte) (bytes[block] & mask);

			changed = changed || (prev != bytes[block]);
		}
		
		if (! changed)
			return;

		InetAddress out;
		try
		{
			out = InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e)
		{
			throw new IllegalArgumentException(e);
		}
		
//		for (int i = 0; i < 128; i++)
//		{
//			System.out.print(i % 10);
//		}
//		System.out.println();
//		printAddress(address);
//		printAddress(out);
//		System.out.flush();
		throw new AllocationDeclarationException.InetNetworkBlockDeclarationException("Block "+ address.toString().substring(1)+ "/" +prefixLength+ " has bits set in suffix (would become "+ out.toString().substring(1) + "/" + prefixLength +").");
	}

	public static InetNetworkAllocationBlock<? extends InetAddress> create(
			final String block)
	{
		Matcher matcher = ipv6_block_pattern.matcher(block);
		Class<? extends InetNetworkAllocationBlock<? extends InetAddress>> type = null;

		if (matcher.matches())
		{
			type = Inet6NetworkAllocationBlock.class;
		}
		else
		{
			matcher = ipv4_block_pattern.matcher(block);
			if (matcher.matches())
				type = Inet4NetworkAllocationBlock.class;
		}

		if (matcher.matches())
		{
			InetAddress address;
			try
			{
				address = InetAddress.getByName(matcher.group(1));
			} catch (UnknownHostException e)
			{
				throw new IllegalArgumentException(e);
			}
			int size = Integer.parseInt(matcher.group(2));

			if (type == Inet4NetworkAllocationBlock.class)
			{
				return new Inet4NetworkAllocationBlock((Inet4Address) address,
						size);
			}
			else if (type == Inet6NetworkAllocationBlock.class)
			{
				return new Inet6NetworkAllocationBlock((Inet6Address) address,
						size);
			}
			else
				throw new AssertionError();
		}
		else
			throw new IllegalArgumentException(block);
	}

	protected InetAddressType address;

	protected int size;

	protected InetNetworkAllocationBlock(final InetAddressType address,
			final int size)
	{
		if (size < 0)
			throw new IllegalArgumentException("Block size cannot be negative. (" + address + "/" + size + ")");
		
		ensureSuffixIsClear(address, size);
		
		this.address = address;
		this.size = size;
	}

	public int compareTo(final InetNetworkAllocationBlock<InetAddressType> o)
	{
		if (getClass() != o.getClass())
			throw new IllegalArgumentException();

		int result;
		result = this.size - o.size;

		if (result == 0)
			result = InetAddressComparator.compare(this.address, o.address);

		result = (result == 0 ? 0 : (result > 0 ? 1 : -1));

		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		InetNetworkAllocationBlock<?> other = (InetNetworkAllocationBlock<?>) obj;
		if (address == null)
		{
			if (other.address != null)
				return false;
		}
		else if (!address.equals(other.address))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	public InetAddressType getAddress()
	{
		return address;
	}

	public int getSize()
	{
		return size;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + size;
		return result;
	}

	public boolean encompasses(InetNetworkAllocationBlock<InetAddress> other)
	{
		if (this.getClass() != other.getClass())
			return false;
		
		if (this.size >= other.size)
			return false;
		
		return bitsMatch(other, this.size);
	}
	
	public boolean bitsMatch(InetNetworkAllocationBlock<InetAddress> other, int bits)
	{
		return bitsMatch(other, 0, bits);
	}

	public boolean bitsMatch(InetNetworkAllocationBlock<InetAddress> other, int start, int finish)
	{
		byte[] myBytes = this.address.getAddress();
		byte[] otherBytes = other.address.getAddress();
		
		for (int i = start; i < finish; i += 8)
		{
			int block = (int) Math.floor(i / 8);
			byte mask;
			
			if (i +(8-1) < finish)
			{
				mask = ~0;
			}
			else
			{
				mask = (byte) ((~0) << (7 - (i % 8)));
			}
			
			byte result = (byte) ((myBytes[block] ^ otherBytes[block]) & mask);
			
			if (result != 0)
				return false;
		}
		
		return true;
	}
	
	
}

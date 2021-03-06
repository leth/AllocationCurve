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
package uk.co.marcuscobden.allocationcurve.exception;

import java.net.InetAddress;

import uk.co.marcuscobden.allocationcurve.AllocationRecord;
import uk.co.marcuscobden.allocationcurve.allocation.InetNetworkAllocationBlock;

public abstract class AllocationDeclarationException extends Exception
{
	private static final long serialVersionUID = 3405766812640198630L;

	public static class InetNetworkBlockDeclarationException extends
			RuntimeException
	{

		private static final long serialVersionUID = -142674635253047830L;

		public InetNetworkBlockDeclarationException(final String message)
		{
			super(message);
		}

	}

	public static class NonEncompassedBlockException extends
			AllocationDeclarationException
	{
		private static final long serialVersionUID = -1344468265881480587L;
		private InetNetworkAllocationBlock<InetAddress> block;

		public NonEncompassedBlockException(
				final AllocationRecord allocationRecord,
				final InetNetworkAllocationBlock<InetAddress> block)
		{
			super(allocationRecord, "Block (" + block + ") in "
					+ allocationRecord.getLabel()
					+ " not encompassed by parent allocation.");
			this.block = block;
		}
	}

	public static class IPVersionMismatch extends
			AllocationDeclarationException
	{
		private static final long serialVersionUID = 2691642399596387271L;

		public IPVersionMismatch(final AllocationRecord allocationRecord,
				final Class<? extends InetAddress> expected,
				final InetAddress mismatch)
		{
			super(allocationRecord, "IP version mismatch - expected "
					+ expected.getName() + " found " + mismatch + ".");
		}
	}

	public static class NoBlocksDeclaredException extends
			AllocationDeclarationException
	{
		private static final long serialVersionUID = -3739924672537767604L;

		public NoBlocksDeclaredException(final AllocationRecord allocationRecord)
		{
			super(allocationRecord, "No blocks declared.");
		}

	}

	protected AllocationRecord allocationRecord;

	public AllocationDeclarationException(
			final AllocationRecord allocationRecord, final String message)
	{
		super("Error in allocation '" + allocationRecord.getLabel() + "': "
				+ message);
		this.allocationRecord = allocationRecord;
	}
}

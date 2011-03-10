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
package uk.co.marcuscobden.allocationcurve;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Set;

import uk.co.marcuscobden.allocationcurve.allocation.InetNetworkAllocationBlock;
import uk.co.marcuscobden.allocationcurve.exception.AllocationDeclarationException;
import uk.co.marcuscobden.allocationcurve.exception.AllocationDeclarationException.NoBlocksDeclaredException;

public class AllocationRecord
{
	protected String label;
	protected String description;
	protected String includeFile;
	protected Set<InetNetworkAllocationBlock<InetAddress>> blocks;
	protected Set<AllocationRecord> allocations;

	public Set<AllocationRecord> getAllocations()
	{
		return allocations;
	}

	public Set<InetNetworkAllocationBlock<InetAddress>> getBlocks()
	{
		return blocks;
	}

	public String getDescription()
	{
		return description;
	}

	public String getIncludeFile()
	{
		return includeFile;
	}

	public String getLabel()
	{
		return label;
	}

	public void setAllocations(final Set<AllocationRecord> allocations)
	{
		this.allocations = allocations;
	}

	public void setBlocks(
			final Set<InetNetworkAllocationBlock<InetAddress>> blocks)
	{
		this.blocks = blocks;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public void setIncludeFile(final String includeFile)
	{
		this.includeFile = includeFile;
	}

	public void setLabel(final String label)
	{
		this.label = label;
	}
	
	public void validate() throws AllocationDeclarationException
	{
		validate(null);
	}
	
	protected void validate(Class<? extends InetAddress> ipVersion) throws AllocationDeclarationException
	{
		boolean blockVersionsChecked = false;
		
		if (ipVersion == null)
		{
			if (blocks.size() == 0)
			{
				throw new AllocationDeclarationException.NoBlocksDeclaredException(this);
			}
			else if (blocks.size() == 1)
			{
				ipVersion = blocks.toArray(new InetNetworkAllocationBlock[1])[0].getAddress().getClass();
			}
			else
			{
				Class<? extends InetAddress> seen = null;
				for (InetNetworkAllocationBlock<? extends InetAddress> block : blocks)
				{
					if (seen == null)
						seen = block.getAddress().getClass();
					else if (! seen.equals(block.getAddress().getClass()))
						throw new AllocationDeclarationException.IPVersionMismatch(this, seen, block.getAddress());
				}
				ipVersion = seen;
				blockVersionsChecked = true;
			}
		}
		
		if (!blockVersionsChecked)
		{
			for (InetNetworkAllocationBlock<? extends InetAddress> block : blocks)
			{
				if (! ipVersion.equals(block.getAddress().getClass()))
					throw new AllocationDeclarationException.IPVersionMismatch(this, ipVersion, block.getAddress());
			}
		}
		
		if (allocations != null)
		{
			// check sub-allocations fit within at least one block of this allocation.
			for (AllocationRecord alloc : allocations)
			{
				if (alloc.getAllocations() == null)
					continue;

				for (InetNetworkAllocationBlock<InetAddress> allocBlock : alloc.blocks)
				{
					boolean contained = false;
					for (InetNetworkAllocationBlock<InetAddress> block : blocks)
					{
						if (block.encompasses(allocBlock))
						{
							contained = true;
							break;
						}
					}
					
					if (!contained)
					{
						throw new AllocationDeclarationException.NonEncompassedBlockException(this, allocBlock);
					}
				}
			}
		
		
			for (AllocationRecord alloc : allocations)
			{
				alloc.validate(ipVersion);
			}
		}
	}
}

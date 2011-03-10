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
package uk.co.marcuscobden.allocationcurve.yaml;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import uk.co.marcuscobden.allocationcurve.allocation.InetNetworkAllocationBlock;

public class FactoryConstructor extends Constructor
{

	public FactoryConstructor(final Class<? extends Object> c)
	{
		super(c);
	}

	@Override
	protected Object constructObject(final Node node)
	{
		if (node.getType() == InetNetworkAllocationBlock.class)
		{
			if (!(node instanceof ScalarNode))
				throw new YAMLException("Cannot construct " + node.getType()
						+ " from a " + node.getClass());

			return InetNetworkAllocationBlock.create(((ScalarNode) node)
					.getValue());
		}
		else
			return super.constructObject(node);
	}
}

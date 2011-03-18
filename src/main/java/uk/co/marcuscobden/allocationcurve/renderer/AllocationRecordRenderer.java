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
package uk.co.marcuscobden.allocationcurve.renderer;

import java.awt.Color;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import uk.co.marcuscobden.allocationcurve.AllocationRecord;

public abstract class AllocationRecordRenderer
{

	public abstract void render(OutputStream outputStream, AllocationRecord root,
			int depthLimit);
	
	public static Collection<AllocationRecord> findLeaves(
			final AllocationRecord root, final int depthLimit)
	{
		int currentDepth;
		LinkedList<AllocationRecord> stack = new LinkedList<AllocationRecord>();
		ArrayList<AllocationRecord> leaves = new ArrayList<AllocationRecord>();
		Map<AllocationRecord, Integer> depthMap = new HashMap<AllocationRecord, Integer>();

		stack.add(root);
		depthMap.put(root, 0);

		AllocationRecord current;
		while (!stack.isEmpty())
		{
			current = stack.pop();
			currentDepth = depthMap.get(current);

			Collection<AllocationRecord> children = current.getAllocations();
			if (depthLimit == -1 || currentDepth < depthLimit)
			{
				if (children != null)
					for (AllocationRecord c : children)
					{
						stack.addLast(c);
						depthMap.put(c, currentDepth + 1);
					}
			}

			if (children == null || children.size() == 0
					|| currentDepth == depthLimit)
				leaves.add(current);
		}

		return leaves;
	}
	
	public static Map<AllocationRecord, Color> prepareAllocationColors(
			final Collection<AllocationRecord> allocations)
	{
		Map<AllocationRecord, Color> colorMap = new HashMap<AllocationRecord, Color>();
		
		float hueStep = 1f / (allocations.size() + 1);
		float hue = 0;

		for (AllocationRecord a : allocations)
		{
			colorMap.put(a, Color.getHSBColor(hue, 1f, 1f));

			hue += hueStep;
		}
		
		return colorMap;
	}
}

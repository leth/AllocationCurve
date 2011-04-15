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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.marcuscobden.allocationcurve.AllocationRecord;

public abstract class AllocationRecordRenderer
{

	public abstract void render(OutputStream outputStream, AllocationRecord root,
			int depthLimit);
	
	public static Map<AllocationRecord, Color> prepareAllocationColors(
			final Collection<AllocationRecord> allocations)
	{
		Map<AllocationRecord, Color> colorMap = new HashMap<AllocationRecord, Color>();
		
		float hueStep = 1f / (allocations.size());
		float hue = 0;
		
		for (AllocationRecord a : allocations)
		{
			colorMap.put(a, Color.getHSBColor(hue, 1f, 1f));

			hue += hueStep;
		}
		
		return colorMap;
	}
	
	protected static class AllocationRecordRenderCategorisation
	{
		public AllocationRecord root;
		
		public Collection<AllocationRecord> allNodes;
		public Collection<AllocationRecord> internalNodes;
		public Collection<AllocationRecord> leafNodes;
		public Map<AllocationRecord, Collection<AllocationRecord>> inheritedLeaves; 
		
		public AllocationRecordRenderCategorisation(AllocationRecord root, int depthLimit)
		{
			this.root = root;
			
			int currentDepth;
			LinkedList<AllocationRecord> stack = new LinkedList<AllocationRecord>();
			allNodes = new ArrayList<AllocationRecord>();
			internalNodes = new ArrayList<AllocationRecord>();
			leafNodes = new ArrayList<AllocationRecord>();
			inheritedLeaves = new HashMap<AllocationRecord, Collection<AllocationRecord>>();
			
			Map<AllocationRecord, Integer> depthMap = new HashMap<AllocationRecord, Integer>();

			allNodes.add(root);
			stack.add(root);
			depthMap.put(root, 0);

			AllocationRecord current;
			while (!stack.isEmpty())
			{
				current = stack.pop();
				currentDepth = depthMap.get(current);

				
				if (current.hasSubAllocations())
				{
					if (depthLimit == -1 || currentDepth < depthLimit)
					{
						Collection<AllocationRecord> children = current.getAllocations();
						
						for (AllocationRecord c : children)
						{
							allNodes.add(c);
							stack.addLast(c);
							depthMap.put(c, currentDepth + 1);
						}
					}
				}

				if (!current.hasSubAllocations() || currentDepth == depthLimit)
				{
					leafNodes.add(current);
					inheritedLeaves.get(current.getParent()).add(current);
				}
				else
				{
					internalNodes.add(current);
					inheritedLeaves.put(current, new ArrayList<AllocationRecord>());
				}
			}
			
			finishInheritedLeaves(root);
		}
		
		protected void finishInheritedLeaves(AllocationRecord root)
		{
			Collection<AllocationRecord> leaves = inheritedLeaves.get(root);
			if (leaves == null)
				inheritedLeaves.put(root, leaves = new ArrayList<AllocationRecord>());
			
			if (root.hasSubAllocations())
			{
				for (AllocationRecord sub : root.getAllocations())
				{
					finishInheritedLeaves(sub);
					leaves.addAll(inheritedLeaves.get(sub));
				}
			}
		}	
		
	}
	
}

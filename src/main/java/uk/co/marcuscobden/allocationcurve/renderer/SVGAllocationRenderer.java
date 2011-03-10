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
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.co.marcuscobden.allocationcurve.AllocationRecord;
import uk.co.marcuscobden.allocationcurve.allocation.InetNetworkAllocationBlock;
import uk.co.marcuscobden.allocationcurve.exception.AllocationDeclarationException;

public class SVGAllocationRenderer extends HilbertAllocationRenderer
{

	protected Map<AllocationRecord, Color> colorMap = new HashMap<AllocationRecord, Color>();

	public SVGAllocationRenderer(final Dimension size)
	{
		super(size);
	}

	protected Color getAllocationColor(final AllocationRecord alloc)
	{
		return colorMap.get(alloc);
	}

	protected void prepareAllocationColors(
			final Collection<AllocationRecord> allocations)
	{
		float hueStep = 1f / (allocations.size() + 1);
		float hue = 0;

		for (AllocationRecord a : allocations)
		{
			colorMap.put(a, Color.getHSBColor(hue, 1f, 1f));

			hue += hueStep;
		}
	}

	public void render(final OutputStream output, final AllocationRecord root,
			final int depthLimit)
	{
		Set<InetNetworkAllocationBlock<InetAddress>> blocks = root.getBlocks();
		int startBit, finishBit;
		
		if (blocks == null || blocks.size() == 0)
		{
			// Should not happen if the allocs have already been verified.
			throw new AssertionError("Alocation should have failed verification.");
		}
		else if (blocks.size() == 1)
		{
			InetNetworkAllocationBlock rootBlock = blocks.toArray(new InetNetworkAllocationBlock[1])[0];
			startBit = rootBlock.getSize();
		}
		else
		{
			// TODO implement code for multiple root blocks.
			throw new UnsupportedOperationException();
		}
		finishBit = startBit;
		
		PrintWriter out = new PrintWriter(output);

		int currentDepth;
		LinkedList<AllocationRecord> stack = new LinkedList<AllocationRecord>();
		ArrayList<AllocationRecord> leaves = new ArrayList<AllocationRecord>();
		Map<AllocationRecord, Integer> depthMap = new HashMap<AllocationRecord, Integer>();

		stack.add(root);
		depthMap.put(root, 0);

		AllocationRecord current;
		while (! stack.isEmpty())
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
						depthMap.put(c, currentDepth +1);
					}
			}
			
			for (InetNetworkAllocationBlock<InetAddress> b : current.getBlocks())
			{
				finishBit = Math.max(finishBit, b.getSize());
			}
			
			if (children == null || children.size() == 0 || currentDepth == depthLimit)
				leaves.add(current);
		}

		out.println("<?xml version=\"1.0\"?>");
		out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
		out.printf("<svg xmlns='http://www.w3.org/2000/svg' width='%d' height='%d' version='1.1'>\n", size.width, size.height);

		prepareAllocationColors(leaves);
		
		// Draw the curve
		Point2D.Double[] curve = caluclateCurve(size, (int) Math.ceil((finishBit - startBit)/2d));
		out.print("<path fill='none' stroke='black' stroke-width='1' d='M");
		for (int i = 0; i < curve.length; i++)
		{
			out.printf("%f %fL", curve[i].x, curve[i].y);
		}
		out.println("' />");
		
		// Draw the blocks
		for (AllocationRecord r : leaves)
		{
			out.println("<!-- " + r.getLabel() + " -->");

			Color color = getAllocationColor(r);

			for (InetNetworkAllocationBlock<InetAddress> block : r.getBlocks())
			{
				// TODO calculate actual depths
				Rectangle2D.Double bounds = getBlockBounds(block, startBit, finishBit);
				out.printf(
						"<rect x='%f' y='%f' width='%f' height='%f' fill='rgb(%d, %d, %d)' fill-opacity='0.75'/>\n",
						bounds.x, bounds.y, bounds.width, bounds.height,
						color.getRed(), color.getGreen(), color.getBlue());
			}

		}
		out.println("</svg>");
		
		out.close();
	}

}

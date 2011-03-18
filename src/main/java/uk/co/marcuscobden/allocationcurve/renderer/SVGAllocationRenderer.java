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
import java.math.RoundingMode;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

import uk.co.marcuscobden.allocationcurve.AllocationRecord;
import uk.co.marcuscobden.allocationcurve.allocation.InetNetworkAllocationBlock;

public class SVGAllocationRenderer extends HilbertAllocationRenderer
{

	public SVGAllocationRenderer(final Dimension size)
	{
		super(size);
	}

	public void render(final OutputStream output, final AllocationRecord root,
			final int depthLimit)
	{
		PrintWriter out = new PrintWriter(output);
		
		renderDocumentPreamble(out);
		renderSVGContent(out, root, depthLimit);

		out.close();
	}
	
	public void renderSVGContent(final PrintWriter output, final AllocationRecord root,
			final int depthLimit)
	{
		Collection<AllocationRecord> leaves = findLeaves(root, depthLimit);
		
		renderSVGContent(output, root,
				depthLimit, leaves, prepareAllocationColors(leaves));
	}
	
	public void renderSVGContent(final PrintWriter output, final AllocationRecord root,
			final int depthLimit, Collection<AllocationRecord> leaves, Map<AllocationRecord, Color> colorMap)
	{
		int[] range = getBitRange(root, leaves);
		int startBit = range[0];
		int finishBit = range[1];

		renderSVGOpen(output);
		renderHilbertCurve(output, (int) Math.ceil((finishBit - startBit) / 2d));
		renderAllocations(output, leaves, colorMap, startBit, finishBit);

		renderSVGClose(output);
	}

	protected void renderAllocations(final PrintWriter out,
			final Collection<AllocationRecord> leaves, Map<AllocationRecord, Color> colorMap, final int startBit,
			final int finishBit)
	{
		for (AllocationRecord r : leaves)
		{
			out.println("<!-- " + r.getLabel() + " -->");

			Color color = colorMap.get(r);

//			out.printf(
//					"<rect x='%d' y='%d' width='%d' height='%d' fill='rgb(%d,%d,%d)' />\n",
//					xOffset, yOffset, blockSize, blockSize, color.getRed(),
//					color.getGreen(), color.getBlue());
//			out.printf(
//					"<text x='%d' y='%d' font-family='Verdana' font-size='12'>%s</text>\n",
//					xOffset + blockSize + spacing, yOffset + 10, r.getLabel());

			for (InetNetworkAllocationBlock<InetAddress> block : r.getBlocks())
			{

				renderBlockRectangle(out, block, color, startBit, finishBit);
			}
		}
	}
	
	protected void renderBlockRectangle(PrintWriter out, InetNetworkAllocationBlock<InetAddress> block, Color color, int startBit, int finishBit)
	{
		Rectangle2D.Double bounds = getBlockBounds(block, startBit,
				finishBit);
		out.printf(
				"<rect x='%f' y='%f' width='%f' height='%f' fill='rgb(%d, %d, %d)' fill-opacity='0.75'/>\n",
				bounds.x, bounds.y, bounds.width, bounds.height,
				color.getRed(), color.getGreen(), color.getBlue());
	}

	protected void renderHilbertCurve(final PrintWriter out,
			final int iterations)
	{
		Point2D.Double[] curve = caluclateCurve(size, iterations);
		out.print("<path fill='none' stroke='black' stroke-width='1' d='");
		Point2D.Double prev, current, next;
		
		DecimalFormat format = new DecimalFormat("#.000");
		format.setRoundingMode(RoundingMode.HALF_UP);
		
		for (int i = 0; i < curve.length; i++)
		{
			current = curve[i];
			char mode;
			if (i == 0)
			{
				mode = 'M';
			}
			else
			{
				prev = curve[i -1];

				// Don't plot intermediate points in straight lines
				if(i +1 < curve.length)
				{
					next = curve[i +1];
				
					if (prev.x == current.x &&
						current.x == next.x)
						continue;
					else if (prev.y == current.y &&
							current.y == next.y)
						continue;
					// NB does not check for straight lines which are not orthogonal to the x or y axes
				}
				
				// space optimisation for horizontal & vertical lines
				if (prev.x == current.x)
					mode = 'V';
				else if (prev.y == current.y)
					mode = 'H';
				else
					mode = 'L';
			}
			
			out.print(mode);
			switch (mode) {
				case 'L':
				case 'M':
					out.print(format.format(current.x) + " " + format.format(current.y));
					break;

				case 'H':
					out.print(format.format(current.x));
					break;
				case 'V':
					out.print(format.format(current.y));
					break;

				default:
					break;
			}
		}
		out.println("' />");
	}

	protected void renderSVGOpen(final PrintWriter output)
	{
		output.println("<svg xmlns='http://www.w3.org/2000/svg' version='1.1'>");
	}

	protected void renderSVGClose(final PrintWriter out)
	{
		out.println("</svg>");
	}

	protected void renderDocumentPreamble(final PrintWriter out)
	{
		out.println("<?xml version=\"1.0\"?>");
		out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
	}

}

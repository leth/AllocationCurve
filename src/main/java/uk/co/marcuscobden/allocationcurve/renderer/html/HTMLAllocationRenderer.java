package uk.co.marcuscobden.allocationcurve.renderer.html;

import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import uk.co.marcuscobden.allocationcurve.AllocationRecord;
import uk.co.marcuscobden.allocationcurve.allocation.InetNetworkAllocationBlock;
import uk.co.marcuscobden.allocationcurve.renderer.AllocationRecordRenderer;
import uk.co.marcuscobden.allocationcurve.renderer.SVGAllocationRenderer;

public class HTMLAllocationRenderer
		extends
			AllocationRecordRenderer
{

	protected SVGAllocationRenderer svgRenderer;

	public HTMLAllocationRenderer()
	{
		svgRenderer = new SVGAllocationRenderer(new Dimension(500, 500), true);
	}
	
	public void populateInheritedColors(Map<AllocationRecord, Color> colorMap, AllocationRecordRenderCategorisation cat)
	{
		for (AllocationRecord internal : cat.internalNodes)
		{
			float count = 0;
			float r,g,b;
			r = g = b = 0;
			
			for (AllocationRecord sub : cat.inheritedLeaves.get(internal))
			{
				count ++;
				Color c = colorMap.get(sub);
				r += c.getRed();
				g += c.getGreen();
				b += c.getBlue();
			}
			
			r /= count;
			g /= count;
			b /= count;
			
			Color out = new Color((int)r, (int)g, (int)b);
			colorMap.put(internal, out);
		}
	}

	public void render(final OutputStream outputStream, final AllocationRecord root,
			final int depthLimit)
	{
		AllocationRecordRenderCategorisation cat = new AllocationRecordRenderer.AllocationRecordRenderCategorisation(root, depthLimit);
		
		Map<AllocationRecord, Color> colorMap = prepareAllocationColors(cat.leafNodes);
		populateInheritedColors(colorMap, cat);
		
		String output = getTemplateContent();
		
		output = output.replace("ALLOCATION_GRAPHIC", renderSVGGraphic(root, depthLimit, cat.allNodes, colorMap));
		output = output.replace("ALLOCATION_TREE", renderAllocationTree(root, 0, depthLimit, colorMap));
		
		try
		{
			outputStream.write(output.getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	protected CharSequence renderSVGGraphic(final AllocationRecord root,
			final int depthLimit, Collection<AllocationRecord> leaves, Map<AllocationRecord, Color> colorMap)
	{
		ByteArrayOutputStream svgContent = new ByteArrayOutputStream();

		PrintWriter pw = new PrintWriter(svgContent);
		svgRenderer.renderSVGContent(pw, root, depthLimit, leaves, colorMap);
		pw.flush();
		
		return svgContent.toString();
	}
	
	protected CharSequence renderAllocationTree(final AllocationRecord root, final int currentDepth, final int depthLimit, Map<AllocationRecord, Color> colorMap)
	{
		Set<AllocationRecord> subAllocations = root.getAllocations();
		boolean isLeaf = subAllocations != null && subAllocations.size() > 0 && (depthLimit == -1 || currentDepth < depthLimit);
		
		StringBuilder out = new StringBuilder();
		String style = "";
		if (colorMap.containsKey(root))
		{
			Color color = colorMap.get(root);
			style = "style='border-left: 5px solid #"+ Integer.toHexString(color.getRGB()).substring(2) +"' ";
		}
		out.append("<div ")
			.append(style)
			.append("class='allocation_record");
		if (isLeaf)
			out.append(" leaf");
		out.append("'>");

		out.append("<h3>").append(root.getLabel()).append("</h3>");
		
		if (root.getDescription() != null)
			out.append("<p>").append(root.getDescription()).append("</p>");
		
		out.append("<ul class='allocation_blocks'>");
		for (InetNetworkAllocationBlock<InetAddress> block : root.getBlocks())
		{
			out.append("<li class='allocation_block'>");
			out.append(block.toString());
			out.append("</li>");
		}
		out.append("</ul>");
		
		if (isLeaf)
		{
			out.append("<ul class='sub_allocations'>");
		
			for (AllocationRecord sub : subAllocations)
			{
				out.append("<li>");
				out.append(renderAllocationTree(sub, currentDepth +1, depthLimit, colorMap));
				out.append("</li>");
			}
			out.append("</ul>");
		}
		
		out.append("</div>");
		return out;
	}

	protected String getTemplateContent()
	{
		StringWriter writer = new StringWriter();
		try
		{
			IOUtils.copy(getClass().getResourceAsStream("template.xhtml"), writer);
		} catch (IOException e)
		{
			throw new RuntimeException("Unable to load xhtml template.", e);
		}
		return writer.toString();

	}
}

package uk.co.marcuscobden.allocationcurve.renderer.html;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.util.XMLCatalogResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.co.marcuscobden.allocationcurve.AllocationCurve;
import uk.co.marcuscobden.allocationcurve.AllocationRecord;
import uk.co.marcuscobden.allocationcurve.allocation.InetNetworkAllocationBlock;
import uk.co.marcuscobden.allocationcurve.renderer.AllocationRecordRenderer;
import uk.co.marcuscobden.allocationcurve.renderer.SVGAllocationRenderer;

public class HTMLAllocationRenderer
		implements
			AllocationRecordRenderer
{
	protected SVGAllocationRenderer svgRenderer;
	protected DocumentBuilderFactory dbf;
	protected static final String[] entityCatalogs = {AllocationCurve.class.getResource("catalog.xml").toString()}; 
	protected XMLCatalogResolver entityResolver;
	
	public HTMLAllocationRenderer()
	{
		svgRenderer = new SVGAllocationRenderer(new Dimension(500, 500));
		
		dbf = DocumentBuilderFactory.newInstance();
//		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
//        try
//		{
//			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//		} catch (ParserConfigurationException e)
//		{
//			e.printStackTrace();
//		}
		
		entityResolver = new XMLCatalogResolver(entityCatalogs);
	}
	
	protected Document getSVGDocument(AllocationRecord root, int depthLimit) throws ParserConfigurationException, SAXException, IOException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		svgRenderer.render(buffer, root, depthLimit);
		
		DocumentBuilder builder;
		builder = dbf.newDocumentBuilder();
		
		return builder.parse(new ByteArrayInputStream(buffer.toByteArray()));
	}

	public void render(OutputStream output, AllocationRecord root,
			int depthLimit)
	{
		DocumentBuilder builder;
		try
		{
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		builder.setEntityResolver(entityResolver);
		
		Document template;
		try
		{
			InputStream templateStream = this.getClass().getResourceAsStream("template.html");
			template = builder.parse(templateStream);
		} catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		Document svgDoc;
		try
		{
			svgDoc = getSVGDocument(root, depthLimit);
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			return;
		} catch (SAXException e)
		{
			e.printStackTrace();
			return;
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		Node svgRoot = template.importNode(svgDoc.getDocumentElement(), true);
		
		Node body = template.getElementsByTagName("body").item(0);
		body.insertBefore(svgRoot, body.getFirstChild());
		
		template.getElementById("allocation_tree").appendChild(addAllocationRecord(template, root));
		
		DOMImplementationLS implLS =  (DOMImplementationLS) template.getImplementation().getFeature("LS","3.0");
		LSSerializer writer = implLS.createLSSerializer();
		LSOutput lsOutput = implLS.createLSOutput();
		lsOutput.setByteStream(output);
		writer.write(template, lsOutput);
		
		try
		{
			output.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected Element addAllocationRecord(Document doc, AllocationRecord record)
	{
		Element root = doc.createElement("div");
		root.setAttribute("class", "allocation_record");
		
		Element blocks = doc.createElement("ul");
		root.appendChild(blocks);
		blocks.setAttribute("class", "allocation_block");
		for (InetNetworkAllocationBlock<InetAddress> block : record.getBlocks())
		{
			Element blockElem = doc.createElement("li");
			blockElem.setTextContent(block.toString());
			blocks.appendChild(blockElem);
		}
		
		Set<AllocationRecord> subAllocations = record.getAllocations();
		if (subAllocations != null && subAllocations.size() > 0)
		{
			Element allocations = doc.createElement("ul");
			root.appendChild(allocations);
			for (AllocationRecord sub : subAllocations)
			{
				allocations.appendChild(addAllocationRecord(doc, sub));
			}
		}
		
		return root;
	}
	
}

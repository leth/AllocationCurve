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

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.WindowConstants;

import org.yaml.snakeyaml.Yaml;

import uk.co.marcuscobden.allocationcurve.exception.AllocationDeclarationException;
import uk.co.marcuscobden.allocationcurve.renderer.SVGAllocationRenderer;
import uk.co.marcuscobden.allocationcurve.yaml.SubYAMLConstructor;

public class AllocationCurve
{

	public static void generateOutput(final File input, final File output)
			throws FileNotFoundException
	{
		Yaml yamlParser = new Yaml(new SubYAMLConstructor<AllocationRecord>(
				AllocationRecord.class, input));

		FileInputStream inputStream = new FileInputStream(input);

		AllocationRecord root = (AllocationRecord) yamlParser.load(inputStream);
		try
		{
			root.validate();
		} catch (AllocationDeclarationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		OutputStream outputStream = new FileOutputStream(output);
		
		new SVGAllocationRenderer(new Dimension(500, 500)).render(outputStream, root, 1);
		try
		{
			outputStream.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(final String[] args)
	{
//		if (args.length == 0)
//		{
//			startGUI();
//		}
//		else
//		{
			File input = new File("target/test-classes/examples/UoS.yaml");
			File output = new File("target/test-classes/examples/test1.svg");
			try
			{
				generateOutput(input, output);
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
//		}
	}

	protected static void startGUI()
	{
		AllocationCurveGUI gui = new AllocationCurveGUI();

		gui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		gui.setVisible(true);
	}

}

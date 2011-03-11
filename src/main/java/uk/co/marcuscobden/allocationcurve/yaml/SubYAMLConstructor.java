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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import uk.co.marcuscobden.allocationcurve.AllocationRecord;

public class SubYAMLConstructor<T extends AllocationRecord> extends
		FactoryConstructor
{

	protected File workingDir;
	protected Class<T> rootClass;
	private int depthLimit;

	public SubYAMLConstructor(final Class<T> c, final File workingDir, int depthLimit)
	{
		super(c);
		this.rootClass = c;
		this.workingDir = workingDir;
		this.depthLimit = depthLimit;
	}

	@Override
	protected Object constructObject(final Node node)
	{
		if (node.getType() == AllocationRecord.class)
		{
			if (!(node instanceof MappingNode))
				throw new YAMLException("Cannot construct " + node.getType()
						+ " from a " + node.getClass());

			MappingNode map = (MappingNode) node;
			File subFile = null;
			String filename = null;

			for (NodeTuple t : map.getValue())
			{
				Node key = t.getKeyNode();
				Node value = t.getValueNode();
				if (!(key instanceof ScalarNode)
						|| !(value instanceof ScalarNode))
					continue;

				if (!((ScalarNode) key).getValue().equals("includeFile"))
					continue;

				filename = ((ScalarNode) value).getValue();
				subFile = new File(workingDir, filename);
			}

			FileInputStream input = null;
			if (subFile != null && depthLimit > 0)
			{
				try
				{
					input = new FileInputStream(subFile);
				} catch (FileNotFoundException e)
				{
					System.err.println("Unable to open file "
							+ subFile.getPath());
					e.printStackTrace();
					subFile = null;
				}
			}

			if (input == null)
				return super.constructObject(node);
			else
			{
				Yaml yamlParser = new Yaml(new SubYAMLConstructor<T>(rootClass,
						subFile, depthLimit -1));
				@SuppressWarnings("unchecked")
				T out = (T) yamlParser.load(input);
				out.setIncludeFile(filename);

				return out;
			}
		}
		else
			return super.constructObject(node);
	}
}

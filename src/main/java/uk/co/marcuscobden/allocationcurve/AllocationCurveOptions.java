package uk.co.marcuscobden.allocationcurve;

import java.io.File;

import org.kohsuke.args4j.Option;

public class AllocationCurveOptions
{

	@Option(name = "-i", aliases = { "--input" }, usage = "Read input from this file")
	protected File input;

	@Option(name = "-o", aliases = { "--output" }, usage = "Output to this file")
	protected File output;

	@Option(name = "-d", aliases = { "--depth" }, usage = "Limit the depth of allocation records to show. -1 for no limit.")
	protected int depthLimit = -1;

	@Option(name = "-g", aliases = { "--showGUI" }, usage = "Show the render settings GUI.")
	protected boolean showGUI;
}

# AllocationCurve

This utility takes as input an IPv6 subnet and a list of smaller subnets within it.
It then draws the larger subnet as a two dimensional hilbert space-filling curve, and plots the enclosed subnets within it.
The utility outputs this as an XHTML with embedded Scalable Vector Graphics (SVG).

# Current Status

This utility is currently undergoing testing before release.

# Input and Output

By default, if called with no arguments, the program will display a GUI allowing you to set input, output and control parameters. Alternatively, you may use command line flags, which will cause the GUI not to be displayed.

The options are as follows:

	-d (--depth) N     : Limit the depth of allocation records to show. -1 for no limit.
	-g (--showGUI)     : Show the render settings GUI.
	-i (--input) FILE  : Read input from this file
	-o (--output) FILE : Output to this file

The program input is defined using [YAML](http://www.yaml.org/) syntax, and supports nested allocation groups.

## Allocation group syntax

	label: A Label for the allocaion
	description: A description for the allocation
	blocks:                                     # A list of blocks allocated
	    - "2001:630:d0::/48"                    # A single allocated block
	allocations:
	    - label: Label for a sub-allocation     # A sub-allocaion
	      blocks:
	         - "2001:630:d0:f400::/55" 
	    - label: Label for a sub-allocation     # A sub-allocation which references an exteral file
	      includeFile: sub.yaml                 # NB: If the file can be found, the description of this block here
	                                            # will be dropped in favour of the allocation loadded from the sub-file.
	                                            # Consider the label property here to be just for readability.
	
# Examples

You can find examples in the src/test/resources/examples directory.

# Possible future features
* Subnet tree boundary markers

# License

This software is published under the LGPLv3 licence.

# Development

You can follow development at https://github.com/leth/AllocationCurve

# Authors

* Marcus Cobden 
# AllocationCurve

This utility takes as input an IPv6 subnet and a list of smaller subnets within it.
It then draws the larger subnet as a two dimensional hilbert space-filling curve, and plots the enclosed subnets within it.
The utility outputs this in Scalable Vector Graphics (SVG) format.

# Input and Output

By default the program reads from stdin and writes to stdout, you may use the flags `--input` and `--output` to change this.
The first line read from the input is deemed to be the master, encompassing, subnet. Thereafter, each line is assumed to be an allocation within that subnet.
Allocations may include a text label after the network address, separating them with whitespace characters.

# Examples

You can find examples in the examples directory.

# Planned features (aka. TODO List)
* Ignore Comment lines
* IPv4 support
* Different output formats
* Subnet tree boundary markers

# License

Currently undecided. Contact the author.

# Development

You can follow development at https://github.com/leth/AllocationCurve

# Disclaimer

Please forgive my messy C code ;)

# Authors

* Marcus Cobden 
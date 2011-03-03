/*
	Copyright: Marcus Cobden (2011)
	This file is part of AllocationCurve.

	AllocationCurve is free software: you can redistribute it and/or modify
	it under the terms of the GNU Lesser General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	AllocationCurve is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public License
	along with AllocationCurve. If not, see <http://www.gnu.org/licenses/>.
*/
#ifndef SVG_H_
#define SVG_H_

void print_svg_start(FILE* stream, int width, int height);
void print_svg_end(FILE* stream);
void print_svg_hilbert_path(FILE* stream, double * points, long length);
void print_svg_allocations(FILE* stream, char subnet, struct block_alloc * blocks[], int allocations, char grid_size, int width, int height);
void print_svg_allocation_key(FILE* stream, struct block_alloc * blocks[], int allocations, int x_offset, int y_offset, int block_size, int block_spacing);

#endif /* SVG_H_ */

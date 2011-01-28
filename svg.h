#ifndef SVG_H_
#define SVG_H_

void print_svg_start(FILE* stream, int width, int height);
void print_svg_end(FILE* stream);
void print_svg_hilbert_path(FILE* stream, double * points, long length);
void print_svg_allocations(FILE* stream, char subnet, struct block_alloc * blocks[], int allocations, char grid_size, int width, int height);
void print_svg_allocation_key(FILE* stream, struct block_alloc * blocks[], int allocations, int x_offset, int y_offset, int block_size, int block_spacing);

#endif /* SVG_H_ */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include "main.h"
#include "hilbert.h"
#include "colour.h"

void print_svg_start(FILE* stream, int width, int height)
{
	fprintf(stream, "<?xml version=\"1.0\"?>\n");
	fprintf(stream, "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
	fprintf(stream, "<svg xmlns='http://www.w3.org/2000/svg' width='%i' height='%i' version='1.1'>\n", width, height);
}

void print_svg_end(FILE* stream)
{
	fprintf(stream, "</svg>\n");
}

void print_svg_hilbert_path(FILE* stream, double * points, long length)
{
	fprintf(stream, "<path d='");

	long i;
	for(i = 0; i < length; i += 2)
	{
		fprintf(stream, "%c", i == 0 ? 'M' : 'L');
		fprintf(stream, "%f %f", points[i], points[i +1]);
	}

	fprintf(stream, "' fill='none' stroke='black' stroke-width='1' />\n");
}

void print_svg_allocations(FILE* stream, char subnet, struct block_alloc * blocks[], int allocations, char grid_size, int width, int height)
{
	size_t i;
	char* prev_cust = NULL;

	float colour_step = 6 / (float) (allocations +1);
	int colour_count = 0;

	for(i = 0; i < allocations; ++i)
	{
		if (blocks[i]->label == NULL)
			continue;

		double x, y, w, h;
		in6_addr_to_xy(blocks[i]->masked_network, blocks[i]->size,
				subnet, subnet + (grid_size *2),
				width, height,
				&x, &y, &w, &h);

		HSVType hsv;
		hsv.H = ((float)colour_count) * colour_step;
		hsv.S = 1;
		hsv.V = 1;

		RGBType rgb = HSV_to_RGB(hsv);

		fprintf(stream, "<!-- %s -->\n", blocks[i]->label);
		fprintf(stream, "<rect x='%f' y='%f' width='%f' height='%f' fill='rgb(%.0f,%.0f,%.0f)' fill-opacity='0.75'/>\n", x, y, w, h, rgb.R *255, rgb.G *255, rgb.B *255);
		// TODO fix incorrect sizing of odd numbered subnets

		if (((blocks[i]->label == NULL || prev_cust == NULL) && ! (blocks[i]->label == prev_cust)) ||
			strcmp(prev_cust, blocks[i]->label) != 0)
			colour_count ++;

		prev_cust = blocks[i]->label;
	}
}

void print_svg_allocation_key(FILE* stream, struct block_alloc * blocks[], int allocations, int x_offset, int y_offset, int block_size, int block_spacing)
{
	char* prev_cust = NULL;

	float colour_step = 6 / (float) (allocations +1);
	int colour_count = 0;
	int i;
	for(i = 0; i < allocations; ++i)
	{
		if (blocks[i]->label == 0)
			continue;

		HSVType hsv;
		hsv.H = ((float)colour_count) * colour_step;
		hsv.S = 1;
		hsv.V = 1;

		RGBType rgb = HSV_to_RGB(hsv);

		if (((blocks[i]->label == NULL || prev_cust == NULL) && ! (blocks[i]->label == prev_cust)) ||
			strcmp(prev_cust, blocks[i]->label) != 0)
		{
			fprintf(stream, "<rect x='%i' y='%i' width='%i' height='%i' fill='rgb(%.0f,%.0f,%.0f)' />\n",
					x_offset, y_offset + (block_size + block_spacing) * colour_count,
					block_size, block_size,
					rgb.R *255, rgb.G *255, rgb.B *255);
			fprintf(stream, "<text x='%i' y='%i' font-family='Verdana' font-size='12'>%s</text>\n",
					x_offset + block_size + block_spacing,
					y_offset + block_size + (block_size + block_spacing) * colour_count,
					blocks[i]->label);
			colour_count ++;
		}
		prev_cust = blocks[i]->label;
	}
}

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

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <errno.h>
#include <assert.h>
#include <arpa/inet.h>

#include "hilbert.h"

void get_pos(struct in6_addr* ip_addr, char bit, int* pos)
{
	char block1 = (int) floor( (double) bit     / (sizeof(char) * 8));
	char block2 = (int) floor(((double) bit +1) / (sizeof(char) * 8));

	unsigned char * bits1 = ip_addr->s6_addr + block1;
	unsigned char * bits2 = ip_addr->s6_addr + block2;

	char sub_bit1 = (sizeof(char) *8 -1) - ((bit   ) % (sizeof(char) * 8));
	char sub_bit2 = (sizeof(char) *8 -1) - ((bit +1) % (sizeof(char) *8));

	// build the masks
	unsigned char mask1 = 1 << sub_bit1;
	unsigned char mask2 = 1 << sub_bit2;

	*pos = !!(*bits1 & mask1) * 2 + !!(*bits2 & mask2);
}

void in6_addr_to_xy(struct in6_addr* ip_addr, char size,
		char start_bit, char finish_bit,
		int width, int height,
		double *xp, double *yp, double *wp, double *hp)
{
	// State shapes
	// 0 = u, 1 = c, 2 = n, 3 = ]
	// Initial state of lines = 3
	int state = 0;

	double x, y, oX, oY, w, h;
	x = y = 0;
	// inital offset is half the size
	oX = (double)width /2;
	oY = (double)height /2;
	w = width;
	h = height;

	// Which positions cause x/y offset, by state
	char match[4][2][2] = {
		{{2, 3}, {1, 2}},
		{{0, 3}, {0, 1}},
		{{0, 1}, {0, 3}},
		{{1, 2}, {2, 3}}
	};

	// What is the next state, indexed by state, then quad position
	char next[4][4] = {
		{3, 0, 0, 1},
		{2, 1, 1, 0},
		{1, 2, 2, 3},
		{0, 3, 3, 2}
	};

	// 0 = down, 1 = left, 2 = up, 3 = right
	// if we change the initial state, these will need to change
	char direction[4][4] ={
			{0, -1, 2, -1},
			{1, -1, 3, -1},
			{2, -1, 0, -1},
			{3, -1, 1, -1}
	};

	// we compare 2 bits each iteration.
	for(char bit = start_bit; bit +1 <= finish_bit && bit < size; bit += 2)
	{
		int pos;
		get_pos(ip_addr, bit, &pos);

		x += oX * !!(pos == match[state][0][0] || pos == match[state][0][1]);
		y += oY * !!(pos == match[state][1][0] || pos == match[state][1][1]);

		int next_state = next[state][pos];

		// shrink the size of the offsets for the next set of comparisons
		oX /= 2;
		oY /= 2;
		w  /= 2;
		h  /= 2;

		if (bit +1 >= size)
		{
			char d = direction[state][pos];
			assert(d != -1);

			if (direction[state][pos] % 2)
				w *= 2;
			else
				h *= 2;

			if (d == 1)
				x -= w/2;
			else if (d == 2)
				y -= h/2;
		}

		state = next_state;
	}
	// return the result
	*xp = x;
	*yp = y;
	*wp = w;
	*hp = h;
}

void hilbert(double * out, long *pos, double x0, double y0, double xi, double xj, double yi, double yj, int n)
{
	/* x and y are the coordinates of the bottom left corner */
	/* xi & xj are the i & j components of the unit x vector of the frame */
	/* similarly yi and yj */

	if (n <= 0)
	{
		out[(*pos)   ] = x0 + (xi + yi)/2;
		out[(*pos) +1] = y0 + (xj + yj)/2;
		*pos += 2;
	} else {
		hilbert(out, pos, x0,               y0,               yi/2, yj/2, xi/2, xj/2, n - 1);
		hilbert(out, pos, x0 + yi/2,        y0 + yj/2,        xi/2, xj/2, yi/2, yj/2, n - 1);
		hilbert(out, pos, x0 + yi/2 + xi/2, y0 + yj/2 + xj/2, xi/2, xj/2, yi/2, yj/2, n - 1);
		hilbert(out, pos, x0 + yi/2 + xi,   y0 + yj/2 + xj,  -yi/2,-yj/2,-xi/2,-xj/2, n - 1);
	}
}

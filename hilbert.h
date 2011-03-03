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

#ifndef HILBERT_H_
#define HILBERT_H_

void in6_addr_to_xy(struct in6_addr* ip_addr, char size, char start_bit, char finish_bit, int width, int height, double *xp, double *yp, double *wp, double *hp);
void hilbert(double * out, long *pos, double x0, double y0, double xi, double xj, double yi, double yj, int n);

#endif /* HILBERT_H_ */

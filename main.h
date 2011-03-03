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

#ifndef MAIN_H_
#define MAIN_H_

struct block_alloc
{
	struct in6_addr * orig_network;
	struct in6_addr * masked_network;
	char size;
	char * label;
};

typedef struct list_item
{
	struct block_alloc * alloc;
	struct list_item * next;
};

#endif /* MAIN_H_ */

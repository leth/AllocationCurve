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

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <errno.h>
#include <math.h>
#include <string.h>
#include <ctype.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#include <getopt.h>

#include "main.h"
#include "hilbert.h"
#include "svg.h"

/* The name of this program.  */
const char* program_name;
/* Flag set by ‘--verbose’. */
static int verbose_flag;

void * our_allocation;
FILE* in_stream;
FILE* out_stream;

int buffer_size = 45;
char * buffer;

void print_address(struct in6_addr* addr){
	for (int i = 0; i < sizeof(struct in6_addr) * 8; ++i) {
		char pos = (sizeof(struct in6_addr) *8 - i -1 ) % 8;
		char step = floor(i) / 8;
//		if (pos == 0)
//			printf(" ");
		printf("%i", !!(*(addr->s6_addr + step) & (0x1 << pos)));
//		printf("%i %i %i\n", step, pos, i);
	}
	printf("\n");
}

int mask_address(struct in6_addr* subnet_addr, char subnet_length, struct in6_addr* parent_addr, char parent_length)
{
	unsigned char * subnet_bits = subnet_addr->s6_addr;
	unsigned char * parent_bits = parent_addr->s6_addr;
	/*
	for (int var = 0; var < 128; ++var) {
		printf("%i", var % 10);
	}
	printf("\n");*/

//	p(subnet_addr);
	for (char i = 0; i < parent_length; i += sizeof(char)*8, subnet_bits++, parent_bits++) {

		unsigned char mask = 0xff;

		if (i + sizeof(char)*8 > parent_length)
			mask = mask << (i + (sizeof(char)*8) - parent_length);

		// if the prefixes don't match, complain.
		if (!!((*subnet_bits & mask) & ~ (*parent_bits & mask)))
		{
			return -1;
		}

		* subnet_bits = (*subnet_bits) & ~ mask;
	}

	subnet_bits = subnet_addr->s6_addr;
//	p(subnet_addr);
	for (char i = 0 /*subnet_length - (subnet_length % (sizeof(char) * 8))*/; i < sizeof(struct in6_addr) * 8; i += sizeof(char) * 8, subnet_bits++) {

		unsigned char mask;
		if (i + 8 < subnet_length)
		{
			mask = 0xff;
		}
		else if (i < subnet_length && i +8 >= subnet_length)
		{
			char diff = subnet_length - i;
			mask = 0xff << (8 - diff);
		}
		else
		{
			mask = 0x0;
		}

		/*for (int j = 0; j < 8; ++j) {
			printf("%i", !!(mask & (0x1 << (7 - j))));
		}
		fflush(stdout);*/
		*subnet_bits = (*subnet_bits) & mask;
	}
	/*printf("\n");
	p(subnet_addr);
	printf("\n");
	fflush(stdout);*/
	return 0;
}

char *trimwhitespace(char *str)
{
  char *end;

  // Trim leading space
  while(isspace(*str)) str++;

  if(*str == 0)  // All spaces?
    return str;

  // Trim trailing space
  end = str + strlen(str) - 1;
  while(end > str && isspace(*end)) end--;

  // Write new null terminator
  *(end+1) = 0;

  return str;
}

// TODO add comment character support
char * read_line()
{
	char * out = fgets(buffer, buffer_size, in_stream);

	if (out == NULL)
		return out;

	char * nl_pos = strchr(buffer, '\n');

	while (nl_pos == NULL && out != NULL)
	{
		int old_buffer_size = buffer_size;
		int increase = 10;
		buffer_size += increase;

		buffer = realloc(buffer, buffer_size * sizeof(char));
		if (buffer == NULL)
		{
			fprintf(stderr, "Unable to allocate memory");
			exit (1);
		}

		out = fgets (buffer + (old_buffer_size -1), increase +1, in_stream);

		nl_pos = strchr(buffer, '\n');
	}

	// Overwrite the trailing newline
	if (nl_pos != NULL)
		*nl_pos = '\0';

	return buffer;
}

struct block_alloc * gen(int * allocations)
{
	int i;
	struct block_alloc * out = calloc(sizeof(struct block_alloc), 65535);
	char* prev_cust = NULL;

	for(i = 0; i <= 65535; i++)
	{
		char* cust = NULL;

		if(i >= 0 && i < 2)
		{
			// 63
			cust = "\tS";
		}
		else if(i >= 4 && i < 16)
		{
			// 61
			cust = "\tD";
		}
		else if(i >= 16 && i < 20)
		{
			// 62
			cust = "I";
		}
		else if(i >= 20 && i < 24)
		{
			// 62
			cust = "P";
		}
		else if(i >= 32 && i < 48)
		{
			// 60
			cust = "R";
		}
		else if(i >= 64 && i < 96)
		{
			// 59
			cust = "O";
		}
		else if(i >= 96 && i < 128)
		{
			// 59
			cust = "U";
		}
		else if(i >= 128 && i < 160)
		{
			// 59
			cust = "B";
		}
		else if(i >= 256 && i < 512)
		{
			// 56
			cust = "J";
		}
		else if(i >= 512 && i < 1024)
		{
			// 55
			cust = "C";
		}
		else if(i >= 1024 && i < 1280)
		{
			// 56
			cust = "N";
		}
		else if(i >= 1280 && i < 1792)
		{
			// 55
			cust = "K";
		}
		else if(i >= 2048 && i < 4096)
		{
			// 53
			cust = "T";
		}
		else if(i >= 4096 && i < 6144)
		{
			// 53
			cust = "V";
		}
		else if(i >= 6144 && i < 8192)
		{
			// 53
			cust = "M";
		}
		else if(i >= 16384 && i < 24576)
		{
			// 50
			cust = "G";
		}
		else if(i >= 24576 && i < 28672)
		{
			// 52
			cust = "H";
		}
		else if(i >= 28672 && i < 32768)
		{
			// 52
			cust = "Q";
		}
		else if(i >= 32768 && i < 36864)
		{
			// 52
			cust = "W";
		}
		else if(i >= 36864 && i < 40960)
		{
			// 52
			cust = "E";
		}

		if (cust == NULL)
			continue;

		char * address;
		asprintf(&address, "2009:0000:0000:%04X/64", i);

		out[i].orig_network = malloc(sizeof(struct in6_addr));
		int ret = inet_net_pton(AF_INET6, address, out[i].orig_network, sizeof(struct in6_addr));

		if (ret == -1)
		{
			printf("%i %s\n", errno, address);
			free(address);
			exit (1);
		}
		else
		{
			free(address);
			out[i].size = ret;
		}

		out[i].label = cust;

		if (((cust == NULL || prev_cust == NULL) && ! (cust == prev_cust)) ||
			strcmp(prev_cust, cust) != 0)
			* allocations += 1;

		prev_cust = cust;
	}

	return out;
}

struct block_alloc * gen2(int * allocations)
{
	int limit = 256;
	int i;
	struct block_alloc * out = calloc(sizeof(struct block_alloc), limit);
	char* prev_cust = NULL;

	for(i = 0; i < limit; i++)
	{
		char* label;
		asprintf(&label, "%2i (2009:0000:0000:%02X00/56)", i, i);

		char * address;
		asprintf(&address, "2009:0000:0000:%02X00/56", i);

		out[i].orig_network = malloc(sizeof(struct in6_addr));
		int ret = inet_net_pton(AF_INET6, address, out[i].orig_network, sizeof(struct in6_addr));

		if (ret == -1)
		{
			printf("=> %i %s\n", errno, address);
			free(address);
			exit (1);
		}
		else
		{
			free(address);
			out[i].size = ret;
		}

		out[i].label = label;

		if (((label == NULL || prev_cust == NULL) && ! (label == prev_cust)) ||
			strcmp(prev_cust, label) != 0)
			* allocations += 1;

		prev_cust = label;
	}

	return out;
}

void print_usage (FILE* stream, int exit_code)
{
	fprintf(stream, "Usage:  %s options\n", program_name);
	fprintf(stream,
		"	--help             Display this usage information.\n"
		"	--verbose          Turn on verbose output\n"
		"	--input  filename  Read input from this file.\n"
		"	--output filename  Write output to file.\n");
  exit(exit_code);
}

int main (int argc, char *const *argv)
{
	static struct option long_options[] =
	{
		{"verbose", no_argument,      	&verbose_flag, 1},
		{"help",   	no_argument,      	0, 'h'},
		{"input",  	required_argument,	0, 'i'},
		{"output", 	required_argument,	0, 'o'},
		{0, 0, 0, 0}
	};

	program_name = argv[0];
	in_stream = stdin;
	out_stream = stdout;

	while (1)
	{
		/* getopt_long stores the option index here. */
		int option_index = 0;

		int c = getopt_long(argc, argv, "", long_options, &option_index);

		 /* Detect the end of the options. */
		if (c == -1)
			break;

		switch (c)
		{
			case 0:
				break;

	 		case 'i':
	 			in_stream  = fopen(optarg, "r");
	 			if (in_stream == NULL)
	 			{
	 				fprintf(stderr, "Failed to open file '%s' for reading.", optarg);
	 				exit(1);
	 			}
	 			break;

	 		case 'o':
	 			out_stream  = fopen(optarg, "w+");
	 			if (out_stream == NULL)
	 			{
					fprintf(stderr, "Failed to open file '%s' for writing.", optarg);
					exit(1);
	 			}
	 			break;

	 		case 'h':
	 			print_usage(out_stream, 0);
	 			break;

	 		case '?':
	 			/* getopt_long already printed an error message. */
	 			print_usage(stderr, 1);
	 			break;

	 		default:
	 			abort();
	 	}
	}

	buffer = malloc(buffer_size * sizeof(char));
	if (buffer == NULL)
	{
		fprintf(stderr, "Unable to allocate memory");
		exit (1);
	}

	our_allocation = malloc(sizeof(struct in6_addr));
	char* subnet = read_line();
	trimwhitespace(subnet);
	char our_subnet = inet_net_pton(AF_INET6, subnet, our_allocation, sizeof(struct in6_addr));

	if (our_subnet == -1)
	{
		fprintf(stderr, "Unable to parse allocation: inet_net_pton failed with errno: %i for subnet '%s'\n", errno, subnet);
		exit (1);
	}

	int alloc_count = 0;
	char * line;
	struct list_item * block_list = NULL;

	while ((line = read_line()) != NULL)
	{
		struct block_alloc * alloc = malloc(sizeof(struct block_alloc));
		alloc->orig_network = malloc(sizeof(struct in6_addr));
		char* address = strtok(line, " \t");
		trimwhitespace(address);
		int size = inet_net_pton(AF_INET6, address, alloc->orig_network, sizeof(struct in6_addr));

		if (size == -1)
		{
			fprintf(stderr, "Failed to parse '%s', discarding.\n", address);
			free(alloc->orig_network);
			free(alloc);
			continue;
		}
		else
		{
			alloc->size = size;
		}

		char* label = strtok(NULL, "");
		trimwhitespace(label);
		alloc->label = malloc(strlen(label) * sizeof(char));
		strcpy(alloc->label, label);

		alloc->masked_network = malloc(sizeof(struct in6_addr));
		memcpy(alloc->masked_network, alloc->orig_network, sizeof(struct in6_addr));

		if (mask_address(alloc->masked_network, size, our_allocation, our_subnet) == -1)
		{
			fprintf(stderr, "Address '%s' does not match our network mask, discarding.\n", address);
			free(alloc->orig_network);
			free(alloc->masked_network);
			free(alloc);
			continue;
		}

		alloc_count++;
		struct list_item * i = malloc(sizeof(struct list_item));
		i->alloc = alloc;
		i->next = block_list;
		block_list = i;
	}



	struct block_alloc* blocks[alloc_count];

	for (int i = alloc_count -1; i >= 0; --i) {
		blocks[i] = block_list->alloc;
		free(block_list);
		block_list = block_list->next;
	}

	// /1 is a bigger subnet than /2
	int max_subnet = INT_MAX, min_subnet = 0;
	for(int i = 0; i < alloc_count; ++i)
	{
		max_subnet = fmin(max_subnet, blocks[i]->size);
		min_subnet = fmax(min_subnet, blocks[i]->size);
	}

	int n = (int) ceil((double) (min_subnet - our_subnet) /2);
	// printf("%i %i %i", min_subnet, max_subnet, n);
	int width, height;
	width = height = 300; //pow(2, n) * 3;

	long pos = 0;
	double * hilbert_data = malloc(sizeof(double) * 2 * pow(4, n));
	hilbert(hilbert_data, &pos, 0, 0, width, 0, 0, height, n);

	// TODO print markers of varying between units of divisible subnets
	print_svg_start(out_stream, /*width * 10*/ 400, height + alloc_count * (10 + 5) + 5);
	print_svg_hilbert_path(out_stream, hilbert_data, pos);
	print_svg_allocations(out_stream, our_subnet, blocks, alloc_count, n, width, height);
	print_svg_allocation_key(out_stream, blocks, alloc_count, 5, height + 5, 10, 5);
	print_svg_end(out_stream);

	fclose(out_stream);

//	for (i = 0; i < alloc_count; i++) {
//		if (blocks[i].label != NULL) {
//			fprintf(stderr, "%i %i\n", i, blocks[i].label);
//			free(blocks[i].label);
//		}
//	}

	free(block_list);
	free(hilbert_data);

	return 0;
}


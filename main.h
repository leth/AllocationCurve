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

#ifndef HILBERT_H_
#define HILBERT_H_

void in6_addr_to_xy(struct in6_addr* ip_addr, char size, char start_bit, char finish_bit, int width, int height, double *xp, double *yp, double *wp, double *hp);
void hilbert(double * out, long *pos, double x0, double y0, double xi, double xj, double yi, double yj, int n);

#endif /* HILBERT_H_ */

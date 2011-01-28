#ifndef COLOUR_H_
#define COLOUR_H_

#define RETURN_HSV(h, s, b) {HSV.H = h; HSV.S = s; HSV.V = v; return HSV;}
#define RETURN_RGB(r, g, b) {RGB.R = r; RGB.G = g; RGB.B = b; return RGB;}
#define UNDEFINED -1

typedef struct {float R, G, B;} RGBType;
typedef struct {float H, S, V;} HSVType;

HSVType RGB_to_HSV( RGBType RGB );
RGBType HSV_to_RGB( HSVType HSV );

#endif /* COLOUR_H_ */

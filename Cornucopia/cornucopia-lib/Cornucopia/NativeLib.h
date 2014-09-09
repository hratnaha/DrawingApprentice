#ifndef _NATIVELIB_H_
#define _NATIVELIB_H_

#include "SimpleAPI.h" //just the simple API

#ifndef MYAPI
  #define MYAPI
#endif

#ifdef __cplusplus
extern "C" {
#endif

MYAPI int print_line(const char* str);

MYAPI Cornu::BasicPrimitive *getBasicPrimitives(int passin[][2], int size, int parmtype);

MYAPI Cornu::BasicBezier *getBasicBezier(int passin[][2], int size, int parmtype);

#ifdef __cplusplus
}
#endif

#endif // _NATIVELIB_H_
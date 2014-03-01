LOCAL_PATH := $(call my-dir)    
include $(CLEAR_VARS)    
LOCAL_MODULE    := Cornucopia  
LOCAL_SRC_FILES := Interface.cpp Algorithm.cpp Oversketcher.cpp Parameters.cpp  Arc.cpp PathFinder.cpp Bezier.cpp PiecewiseLinearUtils.cpp Clothoid.cpp Polyline.cpp ClothoidProjector.cpp Preprocessing.cpp  Combiner.cpp PrimitiveFitUtils.cpp  CornerDetector.cpp PrimitiveFitter.cpp PrimitiveSequence.cpp  CurvePrimitive.cpp Resampler.cpp Debugging.cpp SimpleAPI.cpp  ErrorComputer.cpp Solver.cpp  Fitter.cpp TwoCurveCombine.cpp  Fresnel.cpp GraphConstructor.cpp Interface.cpp Line.cpp 
LOCAL_CPP_EXTENSION := .c
LOCAL_LDLIBS    := -lz -lm -llog  
LOCAL_CPPFLAGS  := -std=c++0x
include $(BUILD_SHARED_LIBRARY)
/*
 mac: g++ -dynamiclib -o libCornucopia.jnilib -I /System/Library/Frameworks/JavaVM.framework/Versions/A/Headers  -I /usr/local/include/eigen3 Interface.cpp Algorithm.cpp Oversketcher.cpp Parameters.cpp  Arc.cpp PathFinder.cpp Bezier.cpp PiecewiseLinearUtils.cpp Clothoid.cpp Polyline.cpp ClothoidProjector.cpp Preprocessing.cpp  Combiner.cpp PrimitiveFitUtils.cpp  CornerDetector.cpp PrimitiveFitter.cpp PrimitiveSequence.cpp  CurvePrimitive.cpp Resampler.cpp Debugging.cpp SimpleAPI.cpp  ErrorComputer.cpp Solver.cpp  Fitter.cpp TwoCurveCombine.cpp  Fresnel.cpp GraphConstructor.cpp Line.cpp -framework JavaVM
 */

#include "utilities_Cornucopia.h"
#include <Cornucopia.h>
#include <Parameters.h>
#include <SimpleAPI.h>
#include <fstream>

/*
 * Class:     utilities_Cornucopia
 * Method:    getBasicPrimitives
 * Signature: ([FII)[F
 */
JNIEXPORT jfloatArray JNICALL Java_utilities_Cornucopia_getBasicPrimitives
(JNIEnv *env, jobject obj, jfloatArray passin, jint size, jint parmtype){
    //jclass cls = (*env)->GetObjectClass(env, obj);
    
    Cornu::Parameters params = Cornu::Parameters::presets()[(int)parmtype]; //default values
    std::vector<Cornu::Point> pts;
    jboolean isDataInIsCopy;
    jfloat* temp = env->GetFloatArrayElements(passin, &isDataInIsCopy);
    for(int i=0;i<size;i++){
        Cornu::Point point((double)temp[2*i],(double)temp[2*i + 1]);
        pts.push_back(point);
    }
    std::ofstream file;
    file.open("jni.out");
    file << pts.size();
    file.close();
    //file.close();
    //pass it to the fitter and process it
    
    std::vector<Cornu::BasicPrimitive> result = Cornu::fit(pts, params);
    free(temp);
    int resultsize = (int)result.size();
    Cornu::BasicPrimitive* finalresult = new Cornu::BasicPrimitive[resultsize];
    float* tmp = new float[resultsize*2];
    for(int i = 0; i < resultsize; i++)
    {
        finalresult[i] = result[i];
        tmp[2*i] = (float)result[i].start.x;
        tmp[2*i+1] = (float)result[i].start.y;
    }
    jfloatArray gettin = env->NewFloatArray(resultsize * 2);
    if (gettin != NULL)
    {
        //env->SetFloatArrayRegion(gettin, 0, resultsize * 2, tmp);
        env->ReleaseFloatArrayElements(gettin, tmp, JNI_COMMIT);
    }
    free(tmp);
    
    //jfloatArray gettin;
    return gettin;
}



#include "jni_Cornucopia.h" 
#include "jni.h" 
#include "stdio.h"
#include "Cornucopia.h"
#include "SimpleAPI.h"

JNIEXPORT void JNICALL Java_jni_Cornucopia_print(JNIEnv *env, jobject obj) { 
	printf("Hello world\n"); 
	return;
}
/*
 * Class:     jni_Cornucopia
 * Method:    getBasicPrimitives
 * Signature: ([III)[Ljni/BasicPrimitive;
 */
JNIEXPORT jobjectArray JNICALL Java_jni_Cornucopia_getBasicPrimitives
  (JNIEnv *env, jobject jobj, jintArray passin, jint size, jint parmtype){
		
	Cornu::Parameters params = Cornu::Parameters::presets()[(int)parmtype]; //default values
	jboolean isDataInIsCopy;
	jint *xs = env->GetIntArrayElements(passin, &isDataInIsCopy);
		
	std::vector<Cornu::Point> pts;
	for(int i=0;i<size;i++){
		Cornu::Point point((int)xs[2*i],(int)xs[2*i + 1]);
		pts.push_back(point);
	}

	std::vector<Cornu::BasicPrimitive> recresult = Cornu::fit(pts, params);
	int resultsize = (int)recresult.size();
	
	jclass bpClass = env->FindClass("jni/BasicPrimitive");
	jobjectArray finalresult = env->NewObjectArray(resultsize, bpClass, 0);

	for(int i = 0; i < resultsize; ++i)
	{
		jmethodID constructorMethodId = env->GetMethodID(bpClass, "<init>", "()V"); 
		jobject basicprimitive = env->NewObject(bpClass, constructorMethodId);

		/*double length;
		double startAngle;
		double startCurvature;
		double curvatureDerivative;
		int bptype;
		double startX;
		double startY;*/

		jfieldID lengthField = env->GetFieldID(bpClass,"length","D");
		env->SetDoubleField(basicprimitive, lengthField, recresult[i].length);

		jfieldID startAngField = env->GetFieldID(bpClass, "startAngle","D");
		env->SetDoubleField(basicprimitive, startAngField , recresult[i].startAngle);

		jfieldID startCurField = env->GetFieldID(bpClass, "startCurvature","D");
		env->SetDoubleField(basicprimitive, startCurField , recresult[i].startCurvature);

		jfieldID curvDerivativeField = env->GetFieldID(bpClass, "curvatureDerivative","D");
		env->SetDoubleField(basicprimitive, curvDerivativeField , recresult[i].curvatureDerivative);

		jfieldID typeField = env->GetFieldID(bpClass, "bptype","I");
		unsigned value = static_cast<unsigned>(recresult[i].type); 
		env->SetIntField(basicprimitive, typeField , (int)value);

		jfieldID startXField = env->GetFieldID(bpClass, "startX","D");
		env->SetDoubleField(basicprimitive, startXField , recresult[i].start.x);

		jfieldID startYField = env->GetFieldID(bpClass, "startY","D");
		env->SetDoubleField(basicprimitive, startYField , recresult[i].start.y);

		env->SetObjectArrayElement(finalresult, i, basicprimitive);
	}
	return finalresult;
}
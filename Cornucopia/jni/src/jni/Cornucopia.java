package jni;

public class Cornucopia { 
	public native void print();
	public native BasicPrimitive[] getBasicPrimitives(int[] passin, int size, int parmtype);
	
	//native method 
	static //static initializer code 
	{ 
		System.loadLibrary("cornucopia"); 
	}
}
package utilities;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PVector;

public class PVecUtilities 
{
	public static PVector ZERO = new PVector(0.0f, 0.0f, 0.0f);
	public static PVector UPVEC = new PVector(0.0f, 1.0f, 0.0f);
	public static PVector STANDARD_BASIS[] = {	new PVector(1.0f, 0.0f, 0.0f), 
												new PVector(0.0f, 1.0f, 0.0f), 
												new PVector(0.0f, 0.0f, 1.0f)};
	
	public static PVector clone(PVector iVector) {
		return new PVector(iVector.x, iVector.y, iVector.z);
	}
	
	public static boolean nullbasis(PVector[] basis) {
		for (PVector basisvec : basis) {
			if (0.0f == basisvec.magSq()) {
				return true;
			}
		}
		return false;
	}
	
	public static float[] dissolve(PVector target, PVector[] basis) {
		float[] coefficients = new float[basis.length];

		for (int i = 0; i < basis.length; i++) {
			coefficients[i] = PVector.dot(target, basis[i]) / basis[i].magSq();
		}

		return coefficients;
	}

	public static PVector assemble(PVector[] basis, float[] coefficients) {
		PVector result = new PVector(0.0f, 0.0f, 0.0f);

		for (int i = 0; i < basis.length; i++) {
			result.add(PVector.mult(basis[i], coefficients[i]));
		}

		return result;
	}
	
	public static float approxRandomGaussian(int quality, Random random)
	{
	  float rnd = 0.0f;
	  
	  for (int i = 0; i < quality; i++) 
	  {
	    rnd += 2 * random.nextFloat() - 1.0f;
	  }
	  
	  return rnd;
	}

	public static PVector randomDirection(Random random)
	{
	  float x = approxRandomGaussian(4, random);
	  float y = approxRandomGaussian(4, random);
	  float z = approxRandomGaussian(4, random);
	  PVector rndVector = new PVector(x, y, z);
	  rndVector.normalize();
	  return rndVector;
	}
	
	public static PVector normTo3Pt(PVector iPt1,
									PVector iPt2,
									PVector iPt3)
	{
		PVector shoulder1 = PVector.sub(iPt1, iPt2);
		PVector shoulder2 = PVector.sub(iPt3, iPt2);
		PVector ort = shoulder1.cross(shoulder2);
		ort.normalize();
		return ort;
	}
	
	public static PVector ortogonalization (PVector target,
											PVector reference)
	{
		return PVector.sub(target, PVector.mult(reference, PVector.dot(target, reference)/reference.magSq()));
	}
	
	public static PVector ortonormalization(PVector target,
											PVector reference)
	{
		PVector ort = ortogonalization(target, reference);
		ort.normalize();
		return ort;
	}
	
	/*public static PVector reflectPoint( PVector point,
										Plane refPlane)
	{
		PVector delta = PVector.sub(point, refPlane.origin);
		delta = reflectVec(delta, refPlane.norm);
		return PVector.add(refPlane.origin, delta);
	} */
	
	public static PVector reflectVec (  PVector vec,
										PVector refPlaneNorm)
	{
		PVector projn = ortogonalization(vec, refPlaneNorm);
		PVector upvec = PVector.sub(vec, projn);
		return PVector.add(vec, PVector.mult(upvec, -2.0f));
	}
}

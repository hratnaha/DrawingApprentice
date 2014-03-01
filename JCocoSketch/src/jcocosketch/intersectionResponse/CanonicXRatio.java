package jcocosketch.intersectionResponse;

import java.util.*;

public enum CanonicXRatio {
	MIDDLE,
	GOLDEN_EARLY,
	GOLDEN_LATE;
	
	public static CanonicXRatio randomXRatio(Random random) {
		CanonicXRatio canonicRatios[] = CanonicXRatio.values();
		return canonicRatios[random.nextInt(canonicRatios.length)];
	}
}

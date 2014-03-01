package jcocosketch.intersectionResponse;

import java.util.*;

import jcocosketch.*;
import utilities.*;
import processing.core.*;

public class IntersectionResponseMaster {
	public static final float GOLDEN_RATIO = 1.618033987f;
	public static final float INV_GOLDEN_RATIO = 0.618033887f;
	private static float[] CANONIC_ANGLES = { (float) (Math.PI / 6),
		(float) (Math.PI / 4), (float) (Math.PI / 3),
		(float) (Math.PI / 2), (float) (2 * Math.PI / 3),
		(float) (3 * Math.PI / 4), (float) (5 * Math.PI / 6) };

	private static float THRESHOLD_COS = 0.86602540378f; // cos(30 deg)
	
	private Random random = new Random();
	
	public Line response(Line userLine) {
		List<Line> targetLines = new ArrayList<Line>();
		targetLines.add(userLine);
		return response(targetLines);
	}
	
	public Line response(List<Line> targetLines) {
		BezierResponse rawResponse = createResponse(targetLines);
		if (null == rawResponse)
			return null;
		return rawResponse.approximation(30);
	}

	private void considerSplitter(float paramRange, float roundingMin,
			float roundingArgmin, ArrayList<Float> splitters) {
		if (roundingMin < 0.2 * paramRange && roundingArgmin > 0.2 * paramRange
				&& roundingArgmin < 0.8 * paramRange) {
			splitters.add(roundingArgmin);
		}
	}

	private void computeLineSplitters(Line line) {
		// ArrayList<Float> radii = new ArrayList<Float>();
		ArrayList<Float> splitters = new ArrayList<Float>();
		splitters.add(0.0f);
		float paramRange = line.getTotalDistance();
		float roundingStart = -1;
		float roundingMin = Float.MAX_VALUE;
		float roundingArgmin = 0;
		for (int i = 1; i < 100; i++) {
			float param = i * paramRange / 100;
			float roundingR = line.getRoundingRadius(param);
			if (roundingR < 0.4 * paramRange) {
				if (roundingStart < 0) {
					roundingStart = param;
				}

				if (roundingR < roundingMin) {
					roundingMin = roundingR;
					roundingArgmin = param;
				}
			} else {
				if (roundingStart > 0) {
					considerSplitter(paramRange, roundingMin, roundingArgmin,
							splitters);
					roundingStart = -1;
					roundingMin = Float.MAX_VALUE;
				}
			}
		}
		if (roundingStart > 0) {
			considerSplitter(paramRange, roundingMin, roundingArgmin, splitters);
		}
		splitters.add(paramRange);
		line.setSplitters(splitters);
	}

	private CanonicXRatio randomXRatio() {
		CanonicXRatio canonicRatios[] = CanonicXRatio.values();
		return canonicRatios[random.nextInt(canonicRatios.length)];
	}

	private float getXParameter(int segmentN, CanonicXRatio xRatio, List<Float> splitters) {
		float segmentStart = splitters.get(segmentN);
		float segmentEnd = splitters.get(segmentN + 1);
		switch (xRatio) {
		case GOLDEN_EARLY:
			return segmentStart + (1 - INV_GOLDEN_RATIO)
					* (segmentEnd - segmentStart);
		case GOLDEN_LATE:
			return segmentStart + INV_GOLDEN_RATIO
					* (segmentEnd - segmentStart);
		default:
			return (segmentEnd + segmentStart) / 2;
		}
	}

	private BezierResponse createResponse(List<Line> lines) {
		BezierResponse response = new BezierResponse();
		for (Line line : lines) {
			if (!line.isSplitted()) {
				computeLineSplitters(line);
			}
		}
		Set<InflectionSegmentId> visitedSegments = new HashSet<InflectionSegmentId>();
		BezierResponsePart seed = createResponseSeed(lines.get(lines.size() - 1), visitedSegments);
		for (int side = 1; side >= 0; side--) {
			BezierResponsePart tipPart = null;
			if (1 == side) {
				tipPart = seed;
			} else if (null != seed) {
				tipPart = seed.reversed();
			}
			for (int i = 0; null != tipPart; i++) {
				if (side == 1 || i > 0) {
					response.addPart(tipPart, side);
				}
				if (i >= 5)
					break;
				BezierResponsePart next = createResponseContinuation(tipPart, lines, visitedSegments);
				if (null == next)
					break;
				tipPart = next;
			}
			if (null != tipPart) {
				response.addPart(createEnding(tipPart), side);
			}
		}
		response.spliceLeftRight();
		return response;
	}

	private BezierResponsePart createResponseSeed(Line line, Set<InflectionSegmentId> visitedSegments) {
		List<Float> splitters = line.getSplitters();
		if (splitters.size() < 3)
			return null;
		BezierResponsePart theSeed = new BezierResponsePart(line, line);
		int seedSplitter = 1 + random.nextInt(splitters.size() - 2);
		visitedSegments.add(new InflectionSegmentId(line.getLineID(), seedSplitter - 1));
		visitedSegments.add(new InflectionSegmentId(line.getLineID(), seedSplitter));
		theSeed.setXParam(0,
				getXParameter(seedSplitter - 1, randomXRatio(), splitters));
		theSeed.setXParam(1,
				getXParameter(seedSplitter, randomXRatio(), splitters));
		PVector firstCrossDirection = decideFirstCrossDirection(theSeed);
		PVector secondCrossDirection = decideSecondCrossDirection(theSeed,
				firstCrossDirection);
		decideSag(theSeed, firstCrossDirection, secondCrossDirection);
		// if (random.nextBoolean()) {theSeed.reverse();}
		return theSeed;
	}
	
	private Pair<Line, Float> intersectLines(PVector rayStart, PVector rayDir, List<Line> targets) {
		Line bestLine = null;
		float bestX = 0.0f;
		float bestDistance = Float.MAX_VALUE;
		for (Line target : targets) {
			float xParam = target.intersectRay(rayStart, rayDir);
			if (xParam < 0)
				continue;
			float distance = PVector.sub(target.getPointAt(xParam), rayStart).mag();
			if (distance < bestDistance) {
				bestLine = target;
				bestX = xParam;
				bestDistance = distance;
			}
		}
		if (null == bestLine) {
			return null;
		} else {
			return Pair.of(bestLine, bestX);
		}
	}

	private Pair<Line, Float> eyescanIntersection(PVector rayStart, PVector rayDir,
			List<Line> targets, float deviationAngle) {
		PVector rayOrt = new PVector(-rayDir.y, rayDir.x, 0);
		ArrayList<Pair<Line, Float>> sideIntersections = new ArrayList<Pair<Line, Float>>();
		float deviationAngles[] = { -deviationAngle, 0.0f, deviationAngle };
		for (int i = 0; i < deviationAngles.length; i++) {
			float devAngle = deviationAngles[i];
			PVector deviatedDir = PVector.mult(rayDir, (float)Math.cos(devAngle));
			deviatedDir.add(PVector.mult(rayOrt, (float)Math.sin(devAngle)));
			Pair<Line, Float> intersection = intersectLines(rayStart, deviatedDir, targets);
			if (null != intersection)
				sideIntersections.add(intersection);
		}
		if (sideIntersections.isEmpty())
			return null;
		else
			return sideIntersections.get(random.nextInt(sideIntersections.size()));
	}

	private BezierResponsePart createResponseContinuation
			(BezierResponsePart predecessor,
			List<Line> targetLines,
			Set<InflectionSegmentId> visitedSegments) {
		//Line line = predecessor.getXLine(1);
		//List<Float> splitters = line.getSplitters();
		PVector continuationVector = predecessor.getOwnTan(1);
		Pair<Line, Float> eyescanResult = eyescanIntersection(predecessor.getXPoint(1),
															 continuationVector, targetLines, (float) Math.PI / 12);
		if (null == eyescanResult) {
			return null;
		}
		Line target = eyescanResult.first;
		float xParam = eyescanResult.second;
		List<Float> splitters = target.getSplitters();
		int segmentN = Collections.binarySearch(splitters, xParam);
		if (segmentN > 0) {
			return null;
		}
		segmentN = -(segmentN + 2);
		InflectionSegmentId segmentId = new InflectionSegmentId(target.getLineID(), segmentN);
		if (visitedSegments.contains(segmentId)) {
			return null;
		}
		visitedSegments.add(segmentId);
		BezierResponsePart response = new BezierResponsePart(predecessor.getXLine(1), target);
		response.setXParam(0, predecessor.getXParam(1));
		float minXPtDist = Float.MAX_VALUE;
		float bestCanonicXParam = -1.0f;
		CanonicXRatio canonicRatios[] = CanonicXRatio.values();
		for (CanonicXRatio canonicRatio : canonicRatios) {
			float canonicXParam = getXParameter(segmentN, canonicRatio,
					splitters);
			float dist = Math.abs(canonicXParam - xParam);
			if (dist < minXPtDist) {
				minXPtDist = dist;
				bestCanonicXParam = canonicXParam;
			}
		}
		response.setXParam(1, bestCanonicXParam);
		PVector secondCrossDirection = decideSecondCrossDirection(response,
				continuationVector);
		decideSag(response, continuationVector, secondCrossDirection);
		return response;
	}

	private BezierResponsePart createEnding(BezierResponsePart predecessor) {
		Line line = predecessor.getXLine(1);
		BezierResponsePart ending = new BezierResponsePart(line, line);
		ending.setXParam(0, predecessor.getXParam(1));
		PVector scaledSecondDerivative = PVector.mult(predecessor.getControlPoint(1), -2);
		scaledSecondDerivative.add(predecessor.getControlPoint(0));
		scaledSecondDerivative.add(predecessor.getXPoint(1));
		PVector delta = predecessor.getStraight(1);
		PVector control1 = PVector.mult(predecessor.getOwnTan(1), 0.4f * INV_GOLDEN_RATIO * delta.mag());
		control1.add(predecessor.getXPoint(1));
		ending.setControlPoint(0, control1);
		PVector control2 = PVector.mult(control1, 2);
		control2.add(scaledSecondDerivative);
		control2.sub(predecessor.getXPoint(1));
		ending.setControlPoint(1, control2);
		PVector idealtip = PVector.mult(control2, 2);
		idealtip.sub(control1);
		ending.setEndPoint(1, idealtip);
		adjustEndingLength(ending, predecessor);
		return ending;
	}
	
	private void adjustEndingLength(BezierResponsePart ending, BezierResponsePart predecessor) {
		float predecessorLength = predecessor.approximateLength(30);
		float endingLength = ending.approximateLength(30);
		float desiredLength = predecessorLength * INV_GOLDEN_RATIO;
		if (desiredLength < endingLength) {
			ending.setRange(1, desiredLength / endingLength);
		}
	}

	private ArrayList<PVector> directionCandidates(PVector tangent,
			PVector deltaNorm) {
		ArrayList<PVector> dirs = new ArrayList<PVector>();
		PVector ort = PVecUtilities.ortonormalization(deltaNorm, tangent);
		for (int i = 0; i < CANONIC_ANGLES.length; i++) {
			float angle = CANONIC_ANGLES[i];
			PVector dir = PVector.mult(tangent, (float)Math.cos(angle));
			dir.add(PVector.mult(ort, (float)Math.sin(angle)));
			if (Math.abs(dir.dot(deltaNorm)) < THRESHOLD_COS) {
				dirs.add(dir);
			}
		}
		return dirs;
	}

	private PVector decideFirstCrossDirection(BezierResponsePart theSeed) {
		PVector delta = theSeed.getStraight(0);
		PVector tangent = theSeed.getXTan(0);
		PVector deltaNorm = delta.get();
		deltaNorm.normalize();
		ArrayList<PVector> candidates = directionCandidates(tangent, deltaNorm);
		return candidates.get(random.nextInt(candidates.size()));
	}

	private PVector decideSecondCrossDirection(BezierResponsePart theSeed,
			PVector firstDirection) {
		PVector delta = theSeed.getStraight(1);
		PVector deltaNorm = delta.get();
		deltaNorm.normalize();
		PVector tangent = theSeed.getXTan(1);
		float targetSin = deltaNorm.cross(firstDirection).z;
		ArrayList<PVector> candidateDirections = directionCandidates(tangent,
				deltaNorm);
		float minDiff = Float.MAX_VALUE;
		PVector best = null;
		for (int i = 0; i < candidateDirections.size(); i++) {
			PVector direction = candidateDirections.get(i);
			float candidateSin = deltaNorm.cross(direction).z;
			float diff = Math.abs(candidateSin - targetSin);
			if (diff < minDiff) {
				minDiff = diff;
				best = direction;
			}
		}
		return best;
	}

	private void decideSag(BezierResponsePart theSeed, PVector dir1,
			PVector dir2) {
		PVector delta = theSeed.getStraight(0);
		float l = delta.mag();
		float sagl = 0.4f * l;
		theSeed.setControlPoint(0,
				PVector.add(theSeed.getXPoint(0), PVector.mult(dir1, sagl)));
		theSeed.setControlPoint(1,
				PVector.add(theSeed.getXPoint(1), PVector.mult(dir2, sagl)));
	}
}

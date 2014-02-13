package jcocosketch.intersectionResponse;

public class InflectionSegmentId {
	private int lineId;
	private int segmentId;
	
	public InflectionSegmentId(int lineId, int segmentId) {
		this.lineId = lineId;
		this.segmentId = segmentId;
	}
	
	public int hashcode() {
		return lineId ^ segmentId;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof InflectionSegmentId)) return false;
		InflectionSegmentId other = (InflectionSegmentId)o;
		return lineId == other.lineId && segmentId == other.segmentId;
	}

	public int getLineId() {
		return lineId;
	}

	public int getSegmentId() {
		return segmentId;
	}
}

package Algorithms;

public class SegmentLevel {
public static int expandFactor=3;
	public SegmentLevel() {
		// TODO Auto-generated constructor stub
	}
	public int getSegmentLevelByIndex(int index) {
		int level=0;
		if (index-4<=0) {
			level=1;
		}
		else {
			index=index-4;
			level=1;
			do {
				level++;
				index=index-(int)Math.pow(expandFactor, level);
			} while (index>0);
		}
		return level;
		
	}
	public static void main(String[] args) {
		SegmentLevel segmentLevel=new SegmentLevel();
		System.out.println(segmentLevel.getSegmentLevelByIndex(1));
		System.out.println(segmentLevel.getSegmentLevelByIndex(3));
		System.out.println(segmentLevel.getSegmentLevelByIndex(7));
		System.out.println(segmentLevel.getSegmentLevelByIndex(13));
		System.out.println(segmentLevel.getSegmentLevelByIndex(18));
	}
}

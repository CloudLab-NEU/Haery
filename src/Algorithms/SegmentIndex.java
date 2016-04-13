package Algorithms;

import Tools.TimesofExpandFactor;

public class SegmentIndex {
	public static int expandFactor=3;
	
	public SegmentIndex() {
		// TODO Auto-generated constructor stub
	}
	public int[] getSubSegmentindex(int segindex) {
		int level=new SegmentLevel().getSegmentLevelByIndex(segindex);
		int[] subindex={0,0,0};
		if (segindex>1) {
			subindex[0]=(segindex-2-new TimesofExpandFactor().gettargetTimesofExpandfactor(level-1))*3+2+
					new TimesofExpandFactor().gettargetTimesofExpandfactor(level);
			subindex[1]=subindex[0]+1;
			subindex[2]=subindex[0]+2;
		}
		
		return subindex;
	}
	
	public static void main(String[] args) {
		SegmentIndex segmentIndex=new SegmentIndex();
		int[] m=segmentIndex.getSubSegmentindex(2);
		System.out.println(m[0]);
		System.out.println(m[1]);
		System.out.println(m[2]);
		m=segmentIndex.getSubSegmentindex(10);
		System.out.println(m[0]);
		System.out.println(m[1]);
		System.out.println(m[2]);
	}
}

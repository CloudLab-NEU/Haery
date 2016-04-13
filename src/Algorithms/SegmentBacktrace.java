package Algorithms;


import java.util.ArrayList;


import Tools.TimesofExpandFactor;

public class SegmentBacktrace {
	public static int expandFactor=3;
	public SegmentBacktrace(){}
	
	public ArrayList<Integer> allTheBacktraceOnSegment(int index) {
		ArrayList<Integer> results=new ArrayList<Integer>();
		int level=0;
		results.add(index);
		do {
			level=new SegmentLevel().getSegmentLevelByIndex(index);
			index=(int) Math.floor((index-2-new TimesofExpandFactor().gettargetTimesofExpandfactor(level-1))/expandFactor)
					+2+new TimesofExpandFactor().gettargetTimesofExpandfactor(level-2);
			results.add(index);
		} while (index>13);
		level=new SegmentLevel().getSegmentLevelByIndex(index);
		int lastone=(int) Math.floor((index-2-new TimesofExpandFactor().gettargetTimesofExpandfactor(level-2))/expandFactor)
				+1+new TimesofExpandFactor().gettargetTimesofExpandfactor(level-3);
		results.add(lastone);
		return results;
	}
	public int backtraceOnSegment(int index) {
		int results;
		int level=0;
		if (index>13) {
			level=new SegmentLevel().getSegmentLevelByIndex(index);
			results=(int) Math.floor((index-2-new TimesofExpandFactor().gettargetTimesofExpandfactor(level-1))/expandFactor)
					+2+new TimesofExpandFactor().gettargetTimesofExpandfactor(level-2);
		}
		else {
			level=new SegmentLevel().getSegmentLevelByIndex(index);
			results=(int) Math.floor((index-2-new TimesofExpandFactor().gettargetTimesofExpandfactor(level-2))/expandFactor)
					+1+new TimesofExpandFactor().gettargetTimesofExpandfactor(level-3);
		}
		
		
		return results;
	}
	public int backtraceBySegmentLevel(int index,int targrtLevel) {
		int level=new SegmentLevel().getSegmentLevelByIndex(index);
		if (level<targrtLevel) {
			return -1;
		}else if (level==targrtLevel) {
			return index;
		}
		else {
			int newIndex=0;
			while (level>targrtLevel) {
				
				newIndex=backtraceOnSegment(index);
				
				level=new SegmentLevel().getSegmentLevelByIndex(newIndex);
				index=newIndex;
			
			}
			return newIndex;
		}
	}
	public static void main(String[] args) {
		SegmentBacktrace segmentBacktrace=new SegmentBacktrace();
		System.out.println(segmentBacktrace.backtraceOnSegment(18));
		System.out.println(segmentBacktrace.backtraceOnSegment(9));
	}
	
	
}

package Algorithms;

import java.util.ArrayList;

public class WholeThresholdForNonNumericalData {

	
	public ArrayList<String> findThresholdOnData(int index) {
		ArrayList<Integer> segments =new SegmentBacktrace().allTheBacktraceOnSegment(index);
	    ArrayList<String> letters=new ArrayList<String>();
	    for (int i = 0; i < segments.size(); i++) {
			letters.add(new PartThresholdForNonNumericalData().findThresholdOnData(segments.get(i))[0]);
			letters.add(new PartThresholdForNonNumericalData().findThresholdOnData(segments.get(i))[1]);
		}
	    return letters;
	}
	
	public static void main(String[] args) {
		WholeThresholdForNonNumericalData wholeThresholdForNonNumericalData=new WholeThresholdForNonNumericalData();
		ArrayList<String> mArrayList=wholeThresholdForNonNumericalData.findThresholdOnData(18);
		for (String string : mArrayList) {
			System.out.println(string);
		}
	}

}

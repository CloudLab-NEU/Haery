package Algorithms;

public class NumericalDataSegmentation implements Segmentation<Double>{
	public static int expandFactor=3;
	public NumericalDataSegmentation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Double[] segment(Double start, Double end, int index,Double originals, Double originale) {
		Double[] results=new Double[2*expandFactor];
		int level=new SegmentLevel().getSegmentLevelByIndex(index);
		Double gap=new Double((originale-originals)/Math.pow(expandFactor, (level)));
		if (start.isInfinite()) {
			results[0]=Double.NEGATIVE_INFINITY;
			for (int i = 1; i <=(2*expandFactor-1); i++) {
				results[i]=end-gap*(Math.ceil((double) (2*expandFactor-1-i)/2));
			}
		}
		else if (end.isInfinite()) {
			results[2*expandFactor-1]=Double.POSITIVE_INFINITY;
			for (int i = 0; i <(2*expandFactor-1); i++) {
				results[i]=start+gap*Math.ceil((double)i/2);
			}
		}
		else {
			for (int i = 0; i <=(2*expandFactor-1); i++) {
				results[i]=start+gap*Math.ceil((double)i/2);
			}
		}
		return results;
	}
	public static void main(String[] args) {
		NumericalDataSegmentation numericalDataSegmentation=new NumericalDataSegmentation();
		Double[] doubles=new Double[6];
		doubles=numericalDataSegmentation.segment(Double.NEGATIVE_INFINITY, new Double(10), 2, new Double(10), new Double(16));
		System.out.println(doubles[0]);
		System.out.println(doubles[1]);
		System.out.println(doubles[2]);
		System.out.println(doubles[3]);
		System.out.println(doubles[4]);
		System.out.println(doubles[5]);
		doubles=numericalDataSegmentation.segment(new Double(10), new Double(16), 3, new Double(10), new Double(16));
		System.out.println(doubles[0]);
		System.out.println(doubles[1]);
		System.out.println(doubles[2]);
		System.out.println(doubles[3]);
		System.out.println(doubles[4]);
		System.out.println(doubles[5]);
		doubles=numericalDataSegmentation.segment(new Double(16),Double.POSITIVE_INFINITY, 4, new Double(10), new Double(16));
		System.out.println(doubles[0]);
		System.out.println(doubles[1]);
		System.out.println(doubles[2]);
		System.out.println(doubles[3]);
		System.out.println(doubles[4]);
		System.out.println(doubles[5]);
	}
	
}

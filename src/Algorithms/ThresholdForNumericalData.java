package Algorithms;



import Tools.TimesofExpandFactor;

public class ThresholdForNumericalData implements ThresholdOnData<Double> {
	public static int expandFactor=3;
	public ThresholdForNumericalData() {

	}

	@Override
	public Double[] findThresholdOnData(int index,Double originals, Double originale) {
		int level=new SegmentLevel().getSegmentLevelByIndex(index);
		double gap=(originale-originals)/Math.pow(expandFactor,level-1);
		Double[] results=new Double[2];
		int specialIndex=(int) Math.pow(expandFactor, level-1)+new TimesofExpandFactor().gettargetTimesofExpandfactor(level-1)+2;
		int latterIndex=new TimesofExpandFactor().gettargetTimesofExpandfactor(level)+2
				-(int) Math.pow(expandFactor, level-1);
		if (index==1) {
			return null;
		}
		if (index<specialIndex) {
			int differ=Math.abs(index-specialIndex);
			if (index!=(new TimesofExpandFactor().gettargetTimesofExpandfactor(level-1)+2)) {
				results[0]=originals-gap*differ;
				results[1]=originals-gap*(differ-1);
			}
			else {
				results[0]=Double.NEGATIVE_INFINITY;
				results[1]=originals-gap*(differ-1);
			}
		}
		else if (index>=specialIndex & index<latterIndex ) {
			int differ=Math.abs(index-specialIndex);
			results[0]=originals+gap*differ;
			results[1]=originals+gap*(differ+1);
		}
		else {
			int differ=Math.abs(index-latterIndex);
			if (index!=new TimesofExpandFactor().gettargetTimesofExpandfactor(level)+1) {
				results[0]=originale+gap*differ;
				results[1]=originale+gap*(differ+1);
			}else {
				results[0]=originale+gap*differ;
				results[1]=Double.POSITIVE_INFINITY;
			}
		}
		return results;
	}
	public static void main(String[] args) {
		ThresholdForNumericalData thresholdForNumericalData=new ThresholdForNumericalData();
		Double[] m=new Double[2];
		m=thresholdForNumericalData.findThresholdOnData(5, new Double(10), new Double(16));
		System.out.println(m[0]);
		System.out.println(m[1]);
		m=thresholdForNumericalData.findThresholdOnData(6, new Double(10), new Double(16));
		System.out.println(m[0]);
		System.out.println(m[1]);
		m=thresholdForNumericalData.findThresholdOnData(10, new Double(10), new Double(16));
		System.out.println(m[0]);
		System.out.println(m[1]);
		m=thresholdForNumericalData.findThresholdOnData(12, new Double(10), new Double(16));
		System.out.println(m[0]);
		System.out.println(m[1]);
		m=thresholdForNumericalData.findThresholdOnData(13, new Double(10), new Double(16));
		System.out.println(m[0]);
		System.out.println(m[1]);
		m=thresholdForNumericalData.findThresholdOnData(18, new Double(10), new Double(16));
		System.out.println(m[0]);
		System.out.println(m[1]);
		m=thresholdForNumericalData.findThresholdOnData(2, new Double(10), new Double(16));
		System.out.println(m[0]);
		
	}

	@Override
	public Double[] findThresholdOnData(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}

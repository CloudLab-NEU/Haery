package Algorithms;

public class PartThresholdForNonNumericalData implements
		ThresholdOnData<String> {

	public static int expandFactor = 3;

	@Override
	public String[] findThresholdOnData(int index) {
		String[] result = new String[2];
		if (index > 4) {
			if ((index - 2) % expandFactor == 0) {
				result[0] = "";
				result[1] = "h";
			}
			if ((index - 3) % expandFactor == 0) {
				result[0] = "h";
				result[1] = "k";
			}
			if ((index - 4) % expandFactor == 0) {
				result[0] = "k";
				result[1] = "z";
			}
		} else {
			if (index == 2) {
				result[0] = "a";
				result[1] = "h";
			}
			if (index == 3) {
				result[0] = "h";
				result[1] = "k";
			}
			if (index == 4) {
				result[0] = "k";
				result[1] = "z";
			}
		}

		return result;
	}

	@Override
	public String[] findThresholdOnData(int index, String originals,
			String originale) {
		// TODO Auto-generated method stub
		return null;
	}

}

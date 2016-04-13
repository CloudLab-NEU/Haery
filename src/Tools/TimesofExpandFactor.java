package Tools;

public class TimesofExpandFactor {
	public static int expandFactor=3;
	public int gettargetTimesofExpandfactor(int n) {
		int results=0;
		for(int i=1;i<=n;i++){
			results+=Math.pow(expandFactor, i);
		}
		return results;
	}

}

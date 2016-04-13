package Query;


import java.util.ArrayList;
import java.util.List;

import System.Dimension;
import System.SystemInfo;

public class MatchFile {
	SystemInfo systemInfo=new SystemInfo();
	private List<Dimension> dimensions = systemInfo.getDimensions();
	private  double fileSegment = (double)systemInfo.getFileSegemnt();

	public List<String> findSequencefilenum(List<String> targetKey) {
		List<String> seqsList = new ArrayList<String>();

		if (targetKey.get(0).equals("*")) {
			int biggerSeq = (int) Math.floor((dimensions.size()+ fileSegment - 1)/ fileSegment);
			for (int i = 1; i <= biggerSeq; i++) {
				int seq = (int) (fileSegment * i - (fileSegment - 1));
				seqsList.add(seq + "");
			}
			int num=-1;
			seqsList.add(num + "");
		} else {
			for (int i = 0; i < targetKey.size(); i++) {
				boolean isThere=false;
				for (int j = 0; j < dimensions.size(); j++) {
					if (targetKey.get(i).equals(dimensions.get(j).getDemensionName())) {
						int index = j + 1;
						int num = (int) (Math.ceil(index / fileSegment)
								* fileSegment - (fileSegment - 1));
						seqsList.add(num + "");
						isThere=true;
					}
				}
				if (!isThere) {
					int num=-1;
					seqsList.add(num + "");
				}
					
				
			}
		}

		return seqsList;
	}
	
	public List<String> findSequencefile(List<String> targetKey) {
		List<String> seqsList = new ArrayList<String>();

		if (targetKey.get(0).equals("*")) {
			
				seqsList.add(0 + "");
			
			int num=-1;
			seqsList.add(num + "");
		} else {
			for (int i = 0; i < targetKey.size(); i++) {
				boolean isThere=false;
				for (int j = 0; j < dimensions.size(); j++) {
					if (targetKey.get(i).equals(dimensions.get(j).getDemensionName())) {
						if (!seqsList.contains("0")) {
							seqsList.add(0 + "");
						}
						
						isThere=true;
				
					}
				}
				if (!isThere) {
					int num=-1;
					seqsList.add(num + "");
				}
					
				
			}
		}

		return seqsList;
	}
	public static void main(String[] args) {
		MatchFile matchFile=new MatchFile();
		List<String> targetKey=new ArrayList<String>();
	//	targetKey.add("city");
	//	targetKey.add("age");
	//	targetKey.add("name");
		targetKey.add("*");
		 List<String> resList=	matchFile.findSequencefilenum(targetKey);
		for (int i = 0; i < resList.size(); i++) {
			System.out.println(resList.get(i)+" ");
		}
	
		
		 
		
	}

}

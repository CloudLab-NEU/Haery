package Query;


import java.util.ArrayList;
import java.util.List;

import Algorithms.SegmentBacktrace;
import Algorithms.SegmentIndex;
import Algorithms.SegmentLevel;
import Algorithms.ThresholdForNumericalData;
import Exception.NotPositiveException;
import Linearization.FlatZordering;
import Operator.AndOperator;
import Operator.OrOperator;
import System.Dimension;
import System.Segment;
import System.SystemInfo;

public class FillterCell {
	SystemInfo systemInfo=new SystemInfo();
	private  List<Dimension> dimensions = systemInfo
			.getDimensions();

	private  List<Integer> ratios = systemInfo.getRatios();
	
	List<List<Segment>> targetIndexWillBeFiltter = initializeList(dimensions
			.size());
	public List<Segment> segmentsOnDimension(int dimensionIndex) {
		return dimensions.get(dimensionIndex).getSegments();

	}

	public List<List<Integer>> getSegmentIndexesOnDimension(
			List<List<Segment>> dims) {
		List<List<Integer>> segmentIndexes = new ArrayList<List<Integer>>();
		for (int i = 0; i < dims.size(); i++) {
			List<Integer> indexes = new ArrayList<Integer>();
			for (Segment segment : dims.get(i)) {
				indexes.add(segment.getSegmentIndex());
			}
			segmentIndexes.add(indexes);
		}
		return segmentIndexes;
	}
	private void getTheCellId(String queryValue,
			List<List<Segment>> targetIndex, int index, String symble,
			String operator) {
		List<Segment> segIndexes = segmentsOnDimension(index);
		int targetSegemnt = -1;
		System.out.println("start compare");
		if (Character.isDigit(queryValue.charAt(0))) {
			System.out.println("digit");
			targetSegemnt = getTargetSegmentIndexonNumericalDimension(index,
					segIndexes, Double.parseDouble(queryValue));
		} else {
			System.out.println("no  digit");
			targetSegemnt = getTargetSegmentIndexonNonNumericalDimension(index,
					segIndexes, queryValue);
		}
		List<Segment> list = new ArrayList<Segment>();
		if (symble.equals("=")) {
			list.add(getSegmentbyIndex(targetSegemnt, index));
			if (targetIndex.get(index).isEmpty()) {
				targetIndex.set(index, list);
			}
		} else {

			list = this.findSegemntByCondition(targetSegemnt, segIndexes,
					symble);
		
		}

		if (targetIndex.get(index).isEmpty()) {
			targetIndex.set(index, list);
		} else {
			if (operator.equalsIgnoreCase("and")) {
				targetIndex.set(index,
						new AndOperator().And(targetIndex.get(index), list));
			}
			if (operator.equalsIgnoreCase("")) {
				targetIndex.set(index, list);
			}
			if (operator.equalsIgnoreCase("or")) {
				targetIndex.set(index,
						new OrOperator().Or(targetIndex.get(index), list));
			}
		}
	}

	public List<List<String>> findCellByDimensionValue(List<String> queryDimension,
			List<Integer> dimensionIndex, List<String> queryValues,
			List<String> querySymbels, List<String> queryOperator,
			List<String> targetKey,List<String> queryKeyStrings) throws NotPositiveException {
		
		List<List<Segment>> targetIndex = initializeList(dimensions.size());
		List<List<Segment>> targetIndexWillNotBeFiltter = initializeList(dimensions.size());
		queryOperator.add(0, "");
	
		for (int i = 0; i < queryValues.size(); i++) {
			String symble = querySymbels.get(i);
			int index = dimensionIndex.get(i);
			System.out.println(i);
			String operator = queryOperator.get(i);
			this.getTheCellId(queryValues.get(i), targetIndex, index, symble,
					operator);
		}
	
		
		for (int i = 0; i < dimensions.size(); i++) {
	
			if (targetIndex.get(i).isEmpty()) {
				targetIndexWillNotBeFiltter.set(i, segmentsOnDimension(i));
				targetIndexWillBeFiltter.set(i, segmentsOnDimension(i));
			}
			else  {
				List<Segment> filtterData = new ArrayList<Segment>();
				List<Segment> notfiltterData = new ArrayList<Segment>();
				List<Segment> segs = targetIndex.get(i);
				filtterData.add(segs.get(0));
				if (segs.size() > 1) {
					filtterData.add(segs.get(
							segs.size() - 1));
					if (segs.size()>2) {
						notfiltterData.addAll(segs.subList(1, segs.size()-1));
					}
				}
				if (notfiltterData.size()!=0) {
					targetIndexWillNotBeFiltter.set(i, notfiltterData);
				}
				
				targetIndexWillBeFiltter.set(i, filtterData);
			}
		}
		List<List<String>> res=new ArrayList<List<String>>();
		
		List<String> fillerTargetKeyInCheck=new ArrayList<String>();
		List<String> fillerTargetKey=new ArrayList<String>();
		for (String targetKeystring : targetKey) {
			for (String query : queryKeyStrings) {
				if (targetKeystring.equals(query)) {
					fillerTargetKeyInCheck.add(targetKeystring);
				} else {
					fillerTargetKey.add(targetKeystring);
				}
			}
		}
		res.add(linerazeAllTheCell(targetIndexWillBeFiltter, fillerTargetKey,true));
		res.add(linerazeAllTheCell(targetIndexWillBeFiltter, fillerTargetKeyInCheck,true));
		res.add(linerazeAllTheCell(targetIndexWillNotBeFiltter, targetKey,false));

		return res;
	}

	private void matchSegments(List<List<Segment>> one, List<Segment> theOther) {
		List<List<Segment>> mediumList = new ArrayList<List<Segment>>();
		for (int i = 0; i < one.size(); i++) {
			for (int j = 0; j < theOther.size(); j++) {
				List<Segment> seg = new ArrayList<Segment>();
				seg.addAll(one.get(i));
				seg.add(theOther.get(j));
				mediumList.add(seg);
			}
		}
		one.clear();
		one.addAll(mediumList);
	}

	private void initilizeLeft(List<Segment> s, List<List<Segment>> left) {

		for (int i = 0; i < s.size(); i++) {
			List<Segment> segment = new ArrayList<Segment>();
			segment.add(s.get(i));
			left.add(segment);
		}
	}

	private List<String> linerazeAllTheCell(List<List<Segment>> targetIndex,
			List<String> targetKey,boolean isFillter) throws NotPositiveException {
		List<List<Segment>> left = new ArrayList<List<Segment>>();
		initilizeLeft(targetIndex.get(0), left);
		List<String> cellId = new ArrayList<String>();

		for (int i = 0; i < targetIndex.size() - 1; i++) {
			matchSegments(left, targetIndex.get(i + 1));
		}

		if (targetKey!=null & targetKey.size()!=0) {
			List<String> seqnum = new MatchFile()
					.findSequencefilenum(targetKey);
			
			for (int i = 0; i < left.size(); i++) {
				cellId.addAll(findFile(left.get(i), seqnum, isFillter));
				cellId.addAll(backtrace(left.get(i), seqnum));
		
			}
		} 
		return cellId;
	}

	private List<String> backtrace(List<Segment> left, List<String> seqnum)
			throws NotPositiveException {
		List<String> resList = new ArrayList<String>();
		int originalDimensionNum = systemInfo.getOriginalDimensionNUm();
		while (true) {
			List<List<Integer>> res = findLatestSegmentVersionIndex(left);
			int latestVersion = res.get(0).get(0);
		
			List<Integer> lastesVersionSegments = res.get(1);
			System.out.println(latestVersion+"vvvvvvvv");
			System.out.println(lastesVersionSegments.get(0)+"ssssssssss");
			List<Integer> dimensionIndex = res.get(2);
			if (latestVersion == 1
					& lastesVersionSegments.size() == originalDimensionNum) {
				break;
			}
			System.out.println(latestVersion+"latestVersion");
			boolean willFillter=false;
			for (int i = 0; i < lastesVersionSegments.size(); i++) {
			
				int index = dimensionIndex.get(i);
				int segment = lastesVersionSegments.get(i);
				Dimension dimension = dimensions.get(index);
				Segment minimalSegmentVersionOnDimension = dimension
						.getMinimalSegmentVersionOnDimension();
				
				if (latestVersion == minimalSegmentVersionOnDimension
						.getSegmentVersion()) {
					if (left.size() != originalDimensionNum) {
						left.remove(index);
					}
				} else {
				
					Segment backSegment=dimension.backtraceOnSegment(segment);
					System.out.println(backSegment.getSegmentIndex());
					left.set(index, backSegment);
					int level=new SegmentLevel().getSegmentLevelByIndex(backSegment.getSegmentIndex());
					Segment minfillterSegment=targetIndexWillBeFiltter.get(index).get(0);
					Segment maxfillterSegment=targetIndexWillBeFiltter.get(index).get(1);
		
					int flage1=new SegmentBacktrace().backtraceBySegmentLevel(minfillterSegment.getSegmentIndex(), level);
					System.out.println("flage1"+flage1);
					int flage2=new SegmentBacktrace().backtraceBySegmentLevel(maxfillterSegment.getSegmentIndex(), level);
					System.out.println("flage2"+flage2);
					if (flage1==backSegment.getSegmentIndex()||flage2==backSegment.getSegmentIndex()) {
						willFillter=true;
					}
				}
				
			}
		
	
			resList.addAll(findFile(left, seqnum,willFillter));

		
		}
		
		return resList;
	}
	
private List<String> findFile( List<Segment> left,List<String> seqnum,boolean isFillter)throws NotPositiveException  {
	List<String> resList = new ArrayList<String>();
	String file = "";
	for (int i = 0; i < seqnum.size(); i++) {
		int seq = Integer.parseInt(seqnum.get(i));
		if (seq ==-1) {
			if (isFillter) {
				file = new FlatZordering(left.size()).encode(left,
						ratios.subList(0, left.size()))
						+ "st";
			}
			else {
				file = new FlatZordering(left.size()).encode(left,
						ratios.subList(0, left.size()))
						+ "st";
			}
			
		} else {
			if (isFillter) {
				file = new FlatZordering(left.size()).encode(left,
						ratios.subList(0, left.size()))
						+ "s" + seq;
			}else{
				file = new FlatZordering(left.size()).encode(left,
						ratios.subList(0, left.size()))
						+ "s" + seq;
			}
				
			
		}
		resList.add(file);
	}
	return resList;
}
	private List<List<Integer>> findLatestSegmentVersionIndex(
			List<Segment> segments) {
		List<List<Integer>> res = new ArrayList<List<Integer>>();
		List<Integer> vns = new ArrayList<Integer>();// minimalVersion
		List<Integer> ses = new ArrayList<Integer>();// segmentIndex
		List<Integer> ins = new ArrayList<Integer>();// DimensionIndex
		int segmentVersion = segments.get(0).getSegmentVersion();
		for (int i = 0; i < segments.size(); i++) {
			if (segmentVersion < segments.get(i).getSegmentVersion()) {
				segmentVersion = segments.get(i).getSegmentVersion();
			}
		}
		vns.add(segmentVersion);
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getSegmentVersion() == segmentVersion) {
				ses.add(segments.get(i).getSegmentIndex());
				ins.add(i);
			}
		}
		res.add(vns);
		res.add(ses);
		res.add(ins);
		return res;
	}


	private Segment getSegmentbyIndex(int index, int dimensionIndex) {
		for (Segment segment : segmentsOnDimension(dimensionIndex)) {
			if (segment.getSegmentIndex() == index) {
				return segment;
			}
		}
		return null;
	}

	private List<Segment> findSegemntByCondition(int index,
			List<Segment> segIndexes, String operator) {
		List<Segment> resIntegers = new ArrayList<Segment>();
		if (operator.contains(">")) {

			for (int i = 0; i < segIndexes.size(); i++) {
				Segment m = segIndexes.get(i);
				if (index == m.getSegmentIndex()) {
					resIntegers
							.addAll(segIndexes.subList(i, segIndexes.size()));

				}
			}
		} else {
			for (int i = 0; i < segIndexes.size(); i++) {
				Segment m = segIndexes.get(i);
				if (index == m.getSegmentIndex()) {
					int n = i + 1;
					resIntegers.addAll(segIndexes.subList(0, n));
				}
			}
		}
		return resIntegers;
	}

	private List<List<Segment>> initializeList(int size) {
		List<List<Segment>> list = new ArrayList<List<Segment>>();
		for (int i = 0; i < size; i++) {
			list.add(new ArrayList<Segment>());
		}
		return list;
	}

	public int getTargetSegmentIndexonNumericalDimension(int dimensionIndex,
			List<Segment> segmentsOnDimension, double queryValues) {
		List<Integer> segmentIndex = getSegmentIndexes(segmentsOnDimension);
		int low = 0;
		int high = segmentIndex.size() - 1;
		System.out.println("high"+high);
		String[] originalThreadStrings = dimensions.get(dimensionIndex)
				.getOriginalThreadForDimension();
		double originals = Double.parseDouble(originalThreadStrings[0]);
		double originale = Double.parseDouble(originalThreadStrings[1]);

		
		while (low <= high) {
			int mid = (low + high) / 2;
			double end = new ThresholdForNumericalData().findThresholdOnData(
					segmentIndex.get(mid), originals, originale)[1];
			double start = new ThresholdForNumericalData().findThresholdOnData(
					segmentIndex.get(mid), originals, originale)[0];
			if (queryValues > end) {
				low = mid + 1;
			}
			if (queryValues <=start) {
				high = mid - 1;
			}
			if (start <queryValues && queryValues <= end) {
				System.out.println(segmentIndex.get(mid));
				return segmentIndex.get(mid);
			}
		}
		return -1;

	}

	private List<Integer> getSegmentIndexes(List<Segment> segments) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (Segment segment : segments) {
			indexes.add(segment.getSegmentIndex());
		}
		return indexes;
	}

	private int getTargetSegmentIndexonNonNumericalDimension(
			int dimensionIndex, List<Segment> segmentsOnDimension,
			String queryValues) {
		List<Integer> segmentIndexes = getSegmentIndexes(segmentsOnDimension);

		String[] originalThreadStrings = dimensions.get(dimensionIndex)
				.getOriginalThreadForDimension();
		char originals = originalThreadStrings[0].charAt(0);
		char originale = originalThreadStrings[1].charAt(0);
		int segmentIndex = 0;

		if (queryValues.length() == 0) {
			return 1;
		}
		if (Character.toLowerCase(queryValues.toCharArray()[0]) < Character.toLowerCase(originals)) {
			segmentIndex = 2;
		}
		if (Character.toLowerCase(queryValues.toCharArray()[0]) > Character.toLowerCase(originale)) {
			segmentIndex = 4;
		}
		if (Character.toLowerCase(queryValues.toCharArray()[0]) >= Character.toLowerCase(originals) && Character.toLowerCase(queryValues.toCharArray()[0]) <=Character.toLowerCase(originale)) {
			segmentIndex = 3;
		}
		if (segmentIndexes.contains(segmentIndex)) {
			return segmentIndex;
		} else {
			segmentIndex = getPossibleSegmentIndexonNonNumericalData(
					segmentIndex, queryValues, segmentIndexes);
			return segmentIndex;
		}
	}

	private int getPossibleSegmentIndexonNonNumericalData(int index,
			String queryValues, List<Integer> segmentInde) {
		int possibleIndex = index;
		for (int i = 1; i < queryValues.length(); i++) {
			int[] indexes = new SegmentIndex()
					.getSubSegmentindex(possibleIndex);
			if (Character.toLowerCase(queryValues.toCharArray()[i]) >= 'a'
					&& Character.toLowerCase(queryValues.toCharArray()[i]) < 'h') {
				possibleIndex = indexes[0];
			}
			if (Character.toLowerCase(queryValues.toCharArray()[i]) >= 'h'
					&& Character.toLowerCase(queryValues.toCharArray()[i]) < 'q') {
				possibleIndex = indexes[1];
			}
			if (Character.toLowerCase(queryValues.toCharArray()[i]) >= 'q'
					&& Character.toLowerCase(queryValues.toCharArray()[i]) < 'z') {
				possibleIndex = indexes[2];
			}
			if (segmentInde.contains(possibleIndex)) {
				return possibleIndex;
			}
		}
		return -1;

	}

	public static void main1(String[] args) {
		FillterCell cell = new FillterCell();
	}

}

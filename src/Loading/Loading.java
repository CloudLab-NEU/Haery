package Loading;

import java.util.ArrayList;
import java.util.List;

import Algorithms.SegmentIndex;
import Algorithms.ThresholdForNumericalData;
import Exception.NotPositiveException;
import Linearization.FlatZordering;
import Operator.AndOperator;
import Operator.OrOperator;
import System.Dimension;
import System.Segment;
import System.SystemInfo;

public class Loading {
	private static List<Dimension> dimensions ;

	private  static List<Integer> ratios ;


	public Loading(List<Dimension> dimensions ,List<Integer> ratios) {
		// TODO Auto-generated constructor stub
		this.dimensions=dimensions;
		this.ratios=ratios;
		
	}
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

	public List<String> findCellByDimensionValue(List<String> queryDimension,
			Object[] dimensionIndex, List<String> queryValues,
			List<String> querySymbels, List<String> queryOperator) throws NotPositiveException {
		
		List<List<Segment>> targetIndex = initializeList(dimensions.size());
		queryOperator.add(0, "");
	
		for (int i = 0; i < queryValues.size(); i++) {
			String symble = querySymbels.get(i);
			int index = Integer.parseInt(dimensionIndex[i]+"");
			System.out.println(i);
			String operator = queryOperator.get(i);
			this.getTheCellId(queryValues.get(i), targetIndex, index, symble,
					operator);
		}
		
		for (int i = 0; i < dimensions.size(); i++) {
			if (targetIndex.get(i).isEmpty()) {
				targetIndex.set(i, segmentsOnDimension(i));
			} 
		}
		return linerazeAllTheCell(targetIndex);

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

	private List<String> linerazeAllTheCell(List<List<Segment>> targetIndex) throws NotPositiveException {
		List<List<Segment>> left = new ArrayList<List<Segment>>();
		initilizeLeft(targetIndex.get(0), left);

		List<String> cellId = new ArrayList<String>();

		for (int i = 0; i < targetIndex.size() - 1; i++) {
			matchSegments(left, targetIndex.get(i + 1));
		}
		
			for (int i = 0; i < left.size(); i++) {
				String file = new FlatZordering(dimensions.size()).encode(
						left.get(i), ratios)
						+ "";
				cellId.add(file);
			}
		
		return cellId;
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
		int low = 1;
		int high = segmentIndex.size() - 1;
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
}

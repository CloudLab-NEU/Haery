package System;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omg.CORBA.INITIALIZE;

import DB.DB;

import com.sun.org.apache.regexp.internal.recompile;

import Algorithms.PartThresholdForNonNumericalData;
import Algorithms.ThresholdForNumericalData;

public class SystemInfo {
	private static List<String> demensionNames;

	private static List<Dimension> dimensions;

	public static void initialize() {

	

		dataKeys.add("partkey");
		dataKeys.add("name");
		dataKeys.add("mfgr");
		dataKeys.add("brand");
		dataKeys.add("type");
		dataKeys.add("size");
		dataKeys.add("container");
		dataKeys.add("retailprice");
		dataKeys.add("commment");

		demensionNames = new ArrayList<String>();
		DB database = new DB();
		dimensions = database.selectDimension();
		for (Dimension dimension : dimensions) {
			demensionNames.add(dimension.getDemensionName());
			ratios.add(dimension.getRatios());
			dimension.setSegments(database.selectLeaveSegmentsOnDimension(dimension.getDimensionIndex()));
			dimension.setAllThesegments(database.selectAlltheSegmentsOnDimension(dimension.getDimensionIndex()));
		}

		
		
		database.deconnSQL();

	}

	public static List<String> getDemensionnames() {
		return demensionNames;
	}

	private static final List<String> dataKeys = new ArrayList<String>();

	public static List<String> getDatakeys() {
		return dataKeys;
	}
	public static void showDimenionInfo() {
		DB database = new DB();
		dimensions = database.selectDimension();
		System.out.println("-----------------");
		System.out.println("执行结果如下所示:");
		System.out.println("-----------------");
		for (Dimension dimension : dimensions) {
			System.out.println("dimension---------");
			System.out.println(dimension.getDimensionIndex() + " : " + dimension.getDemensionName());
			

		}
		database.deconnSQL();
	}

	public static void showSystemInfo() {
		DB database = new DB();
		dimensions = database.selectDimension();
		System.out.println("-----------------");
		System.out.println("执行结果如下所示:");
		System.out.println("-----------------");
		for (Dimension dimension : dimensions) {
			System.out.println("dimension---------");
			System.out.println(dimension.getDimensionIndex() + " : " + dimension.getDemensionName());
			List<Segment> segments = database.selectLeaveSegmentsOnDimension(dimension.getDimensionIndex());
			System.out.println("	segment--------");

			for (Segment segment : segments) {
				String startthre = dimension.getOriginalThreadForDimension()[0];
				String endthre = dimension.getOriginalThreadForDimension()[1];
				if (Character.isDigit(startthre.charAt(0))) {
					Double[] doubles = new ThresholdForNumericalData().findThresholdOnData(segment.getSegmentIndex(),
							Double.parseDouble(startthre), Double.parseDouble(endthre));
					if (doubles == null) {
						System.out.println("	index: " + segment.getSegmentIndex() + " threshold: empty");
					} else {
						System.out.println("	index: " + segment.getSegmentIndex() + " threshold: " + doubles[0]
								+ "---" + doubles[1]);
					}

				} else {

					String[] strings = new PartThresholdForNonNumericalData()
							.findThresholdOnData(segment.getSegmentIndex());
					if (strings == null) {
						System.out.println("	index: " + segment.getSegmentIndex() + " threshold: empty");
					} else {
						System.out.println("	index: " + segment.getSegmentIndex() + " threshold: " + strings[0]
								+ "---" + strings[1]);
					}

				}
			}
		}
		database.deconnSQL();
	}

	private static final List<Integer> ratios = new ArrayList<Integer>();

	public static List<Integer> getRatios() {
		return ratios;
	}

	private static int originalDimensionNUm = 5;

	public static int getOriginalDimensionNUm() {
		return originalDimensionNUm;
	}

	private static int fileSegemnt = 2;

	public static int getFileSegemnt() {
		return fileSegemnt;
	}

	public static void setFileSegemnt(int fileSegemnt) {
		SystemInfo.fileSegemnt = fileSegemnt;
	}

	public static int getMaxmalSegmentVersion() {
		DB database = new DB();
	int maxmalSegmentVersion=database.selectMaxmalSegmentVersion();
		return maxmalSegmentVersion;
	}

	public static List<Dimension> getDimensions() {
		return dimensions;
	}

	public static void setDimensions(List<Dimension> dimensions) {
		SystemInfo.dimensions = dimensions;
	}

}
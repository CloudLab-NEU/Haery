package application;

import java.util.List;
import java.util.Scanner;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.THEAD;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;

import Algorithms.SegmentIndex;
import DB.DB;
import System.Dimension;
import System.Segment;
import System.SystemInfo;

public class SegmentExpansion {
	private boolean expandSegment(int segmentingSegmentIndex, int dimensionIndex) {
		boolean finished = false;
		DB databaseDb = new DB();
		int[] subIndexes = new SegmentIndex().getSubSegmentindex(segmentingSegmentIndex);
		SystemInfo.initialize();
		int maxmalSegmentVersion = SystemInfo.getMaxmalSegmentVersion();
		int version = maxmalSegmentVersion + 1;

		List<Segment> segments =databaseDb.selectLeaveSegmentsOnDimension(dimensionIndex);
		databaseDb.updateFatherSegment(dimensionIndex, segmentingSegmentIndex);
		int index = 0;
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getSegmentIndex() == segmentingSegmentIndex) {
				index = i;
			}
		}
		Segment s1 = new Segment(subIndexes[0], version);
		Segment s2 = new Segment(subIndexes[1], version);
		Segment s3 = new Segment(subIndexes[2], version);
		if (index == 0) {
			Segment rightSegment = segments.get(index + 1);
			databaseDb.insertSegment(s1, dimensionIndex, 1, s2.getSegmentIndex());
			databaseDb.insertSegment(s2, dimensionIndex, 1, s3.getSegmentIndex());
			databaseDb.insertSegment(s3, dimensionIndex, 1, rightSegment.getSegmentIndex());
		} else if (index == segments.size() - 1) {
			Segment leftSegment = segments.get(index - 1);
			databaseDb.updateRightSegment(dimensionIndex, leftSegment, s1.getSegmentIndex());
			databaseDb.insertSegment(s1, dimensionIndex, 1, s2.getSegmentIndex());
			databaseDb.insertSegment(s2, dimensionIndex, 1, s3.getSegmentIndex());
			databaseDb.insertSegment(s3, dimensionIndex, 1, 0);
		} else {
			Segment rightSegment = segments.get(index + 1);
			Segment leftSegment = segments.get(index - 1);
			databaseDb.updateRightSegment(dimensionIndex, leftSegment, s1.getSegmentIndex());
			databaseDb.insertSegment(s1, dimensionIndex, 1, s2.getSegmentIndex());
			databaseDb.insertSegment(s2, dimensionIndex, 1, s3.getSegmentIndex());
			databaseDb.insertSegment(s3, dimensionIndex, 1, rightSegment.getSegmentIndex());
		}
		/*dimension.getAllThesegments().add(s1);
		dimension.getAllThesegments().add(s2);
		dimension.getAllThesegments().add(s3);
		dimension.getSegments().add(index, s1);
		dimension.getSegments().add(index + 1, s2);
		dimension.getSegments().add(index + 2, s3);*/
		finished=true;
		return finished;
	}
	public void expand() {
		SystemInfo.initialize();
		SystemInfo.showSystemInfo();
		Scanner scanner = new Scanner(System.in);
		System.out.println("please input the segement index and dimension index");
		System.out.println("such as 3 7");
		int segmentIndex = 0;
		int dimensionIndex = 0;
		String inputInfo = scanner.nextLine();
		scanner.close();
		if (Character.isDigit(inputInfo.charAt(0))) {
			String[] infos = inputInfo.split(" ");
			segmentIndex = Integer.parseInt(infos[0]);
			dimensionIndex = Integer.parseInt(infos[1]);
			SegmentExpansion segmentExpansion = new SegmentExpansion();
			boolean isSuccess = segmentExpansion.expandSegment(segmentIndex, dimensionIndex);
			if (isSuccess) {
				System.out.println("expand success!");
			}
			else {
				System.out.println("something wrong!");
			}
		} else {
			System.out.println("the type is wrong!");
		}
		
	}

	public static void main(String[] args) {
		SystemInfo.initialize();
		SystemInfo.showSystemInfo();
		Scanner scanner = new Scanner(System.in);
		System.out.println("please input the segement index and dimension index");
		System.out.println("such as 3 7");
		int segmentIndex = 0;
		int dimensionIndex = 0;
		String inputInfo = scanner.nextLine();
		scanner.close();
		if (Character.isDigit(inputInfo.charAt(0))) {
			String[] infos = inputInfo.split(" ");
			segmentIndex = Integer.parseInt(infos[0]);
			dimensionIndex = Integer.parseInt(infos[1]);
			SegmentExpansion segmentExpansion = new SegmentExpansion();
			boolean isSuccess = segmentExpansion.expandSegment(segmentIndex, dimensionIndex);
			if (isSuccess) {
				System.out.println("expand success!");
			}
			else {
				System.out.println("something wrong!");
			}
		} else {
			System.out.println("the type is wrong!");
		}
		

	}

}

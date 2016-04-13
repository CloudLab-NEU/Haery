package application;

import java.util.List;
import java.util.Scanner;

import DB.DB;
import System.Dimension;
import System.Segment;
import System.SystemInfo;

public class DimensionExpansion {
	SystemInfo systemInfo=new SystemInfo();
	
	private boolean expandDimension(Dimension dimension) {
		boolean finished=false;
		DB db=new DB();
		finished=db.insertDimension(dimension);
		int maxmalSegmentVersion=systemInfo.getMaxmalSegmentVersion();
		Segment s1=new Segment(1, maxmalSegmentVersion+1);
		Segment s2=new Segment(2, maxmalSegmentVersion+1);
		Segment s3=new Segment(3, maxmalSegmentVersion+1);
		Segment s4=new Segment(4, maxmalSegmentVersion+1);
		db.insertSegment(s1, dimension.getDimensionIndex(), 1, 2);
		db.insertSegment(s2, dimension.getDimensionIndex(), 1, 3);
		db.insertSegment(s3, dimension.getDimensionIndex(), 1, 4);
		db.insertSegment(s4, dimension.getDimensionIndex(), 1, 0);
		db.deconnSQL();
		systemInfo.getDimensions().add(dimension);
		systemInfo.getDemensionnames().add(dimension.getDemensionName());
		systemInfo.getRatios().add(dimension.getRatios());
		return finished;
	}
	
	public void expand() {
		SystemInfo.initialize();
		SystemInfo.showDimenionInfo();
		Dimension dimension=new Dimension();
		Scanner scanner = new Scanner(System.in);
		System.out.println("please input the dimension index ");
		dimension.setDimensionIndex(Integer.parseInt(scanner.nextLine()));;
		System.out.println("please input the dimension name ");
		dimension.setDemensionName(scanner.nextLine());
		System.out.println("please input the threadForDimension ");
		System.out.println("such as 3|10 or a|k");
		dimension.setOriginalThreadForDimension(scanner.nextLine().split("\\|"));
		System.out.println("please input the ratios");
		dimension.setRatios(Integer.parseInt(scanner.nextLine()));
		System.out.println("please input the dimensionVersion");
		dimension.setDimensionVersion(Integer.parseInt(scanner.nextLine()));
	    expandDimension(dimension);
		scanner.close();
	}
	
	public static void main(String[] args) {
		SystemInfo.initialize();
		
	}
	

}

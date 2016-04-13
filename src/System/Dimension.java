package System;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.hadoop.io.Writable;

import Algorithms.SegmentBacktrace;

public class Dimension implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dimensionIndex;
	private List<Segment> segments;
	private String demensionName;
	private String[] originalThreadForDimension;
	private int ratios;
	private int dimensionVersion;
	private List<Segment> allThesegments;
	
	public List<Segment> getAllThesegments() {
		return allThesegments;
	}
	public void setAllThesegments(List<Segment> allThesegments) {
		this.allThesegments = allThesegments;
	}
	public Dimension(int index,int version,int ratios,List<Segment> segments,String demensionName,String[] originalThreadForDimension) {
		this.dimensionIndex=index;
		this.segments=segments;
		this.demensionName=demensionName;
		this.originalThreadForDimension=originalThreadForDimension;
		this.ratios=ratios;
		this.dimensionVersion=version;
	}
	public Dimension() {
		// TODO Auto-generated constructor stub
	}
	
	public int getDimensionVersion() {
		return dimensionVersion;
	}
	public void setDimensionVersion(int dimensionVersion) {
		this.dimensionVersion = dimensionVersion;
	}
	public int getDimensionIndex() {
		return dimensionIndex;
	}
	public void setDimensionIndex(int dimensionIndex) {
		this.dimensionIndex = dimensionIndex;
	}
	public List<Segment> getSegments() {
		return segments;
	}
	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}
	public String getDemensionName() {
		return demensionName;
	}
	public void setDemensionName(String demensionName) {
		this.demensionName = demensionName;
	}
	public String[] getOriginalThreadForDimension() {
		return originalThreadForDimension;
	}
	public void setOriginalThreadForDimension(String[] originalThreadForDimension) {
		this.originalThreadForDimension = originalThreadForDimension;
	}
	public int getRatios() {
		return ratios;
	}
	public void setRatios(int ratios) {
		this.ratios = ratios;
	}
	public Segment backtraceOnSegment(int index) {
		int segmentIndex=new SegmentBacktrace().backtraceOnSegment(index);
		for (Segment seg : allThesegments) {
			if (seg.getSegmentIndex()==segmentIndex) {
				return seg;
			}
		}
		return null;
	}
	public Segment getMinimalSegmentVersionOnDimension(){
		int minimalVersion=segments.get(0).getSegmentVersion();
		int index=0;
		for (int i = 0; i <segments.size(); i++) {
			int compare=segments.get(i).getSegmentVersion();
			if (compare<minimalVersion) {
				minimalVersion=compare;
				index++;
			}
		}
		return segments.get(index);
	}
	
	
	
}

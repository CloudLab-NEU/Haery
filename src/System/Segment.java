package System;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Writable;

public class Segment implements Comparable<Segment>,Serializable,Writable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int segmentIndex;
	private int segmentVersion;

	
	public Segment() {
		// TODO Auto-generated constructor stub
	}
	public Segment(int index,int version) {
		this.segmentIndex=index;
		this.segmentVersion=version;
	}
	
	public Segment(int segmentIndex) {
		this.segmentIndex=segmentIndex;
	}
	public int getSegmentIndex() {
		return segmentIndex;
	}
	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
	}
	public int getSegmentVersion() {
		return segmentVersion;
	}
	public void setSegmentVersion(int segmentVersion) {
		this.segmentVersion = segmentVersion;
	}
	   @Override
	    public int compareTo(Segment segment) {
	       
	       if (segment!=null  ) {  
	            if(segmentIndex<=segment.getSegmentIndex()){
	                return -1;
	            }else{
	            return 1;
	        }
	    }else{
	        return -1;
	    }
	   }
	@Override
	public void readFields(DataInput in) throws IOException {
		this.segmentIndex=in.readInt();
		this.segmentVersion=in.readInt();
		
	}
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeInt(segmentIndex);
		out.writeInt(segmentVersion);
	}


}

package Operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import System.Segment;

public class OrOperator {
	public OrOperator() {
		
	}
	public List<Segment> Or (List<Segment> one, List<Segment> theOther) {
		List<Segment> segments=new ArrayList<Segment>();
		for (Segment sone : theOther) {
			for (Segment sother : one) {
				if (sother.getSegmentIndex()==sone.getSegmentIndex()) {
					break;
				}
			}
			segments.add(sone);
		}
		 one.addAll(segments);
		 Collections.sort(one);
		 return one;
	}
	
	  
	

}

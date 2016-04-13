package Operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import System.Segment;

public class AndOperator {
	public AndOperator() {
		
	}
	public List<Segment> And(List<Segment> one, List<Segment> theOther) {
		List<Segment> segments=new ArrayList<Segment>();
		for (Segment sone : theOther) {
			for (Segment sother : one) {
				if (sother.getSegmentIndex()==sone.getSegmentIndex()) {
					segments.add(sone);
					break;
				}
			}
		}
		Collections.sort(segments);
		 return segments;
	}


}

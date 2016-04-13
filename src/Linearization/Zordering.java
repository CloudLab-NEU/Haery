package Linearization;

import java.math.BigInteger;
import java.util.List;

import System.Segment;

public interface Zordering {
	
	public BigInteger encode(List<Segment> point,List<Integer> ratios);

	public List<BigInteger> decode(BigInteger no,List<Integer> ratios);

}

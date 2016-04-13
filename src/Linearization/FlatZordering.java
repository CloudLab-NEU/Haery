package Linearization;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Exception.NotPositiveException;
import Exception.NotSupportedNumberOfDimensionsException;
import System.Segment;

public class FlatZordering implements Zordering{
	private Integer numberOfDimensions;
	private Integer numberOfBites;

	public FlatZordering(Integer numberOfDimensions) throws NotPositiveException{
		if (numberOfDimensions < 0) {
			throw new NotPositiveException(
					"any value of  numberOfDimensions should not be negative");
		}
		
		this.numberOfDimensions = numberOfDimensions;
	}


	public BigInteger encode(List<Segment> point,List<Integer> interactRatios){
		StringBuffer binaryOfPoint= new StringBuffer();
		BigInteger value =new BigInteger("0");
		String[] valueOfDimensions = new String[numberOfDimensions];
		int[] bitsLength=new int[numberOfDimensions];
		int max=(int) Math.ceil((double) this.getBinary(BigInteger.valueOf(point.get(0).getSegmentIndex()), 0).length() /interactRatios.get(0));
		String[] bString=new String[numberOfDimensions];
		for (int i = 0; i < numberOfDimensions; i += 1) {
			BigInteger bigInteger=BigInteger.valueOf(point.get(i).getSegmentIndex());
			bString[i]=this.getBinary(bigInteger, 0);
			bitsLength[i] = (int) Math.ceil((double)this.getBinary(bigInteger, 0).length()/interactRatios.get(i));
			if (max<bitsLength[i]) {
				max=bitsLength[i];
			}
		}
		numberOfBites=max;
		for (int i = 0; i < numberOfDimensions; i += 1) {
			valueOfDimensions[i] = this.getBinary(BigInteger.valueOf(point.get(i).getSegmentIndex()), max*interactRatios.get(i));
		}

		for (int i = 0; i < max; i += 1) {
			for (int j = 0; j < numberOfDimensions; j += 1) {
				int ratio=interactRatios.get(j);
				if (ratio==1) {
					binaryOfPoint.append(valueOfDimensions[j].charAt(i));
				}
				if (ratio>1) {
					
					binaryOfPoint.append(valueOfDimensions[j].substring(ratio*i, ratio*(i+1)));
				}
			}
		}

		int numberOfBite = 0;
		for (int i = binaryOfPoint.length() - 1; i >= 0; i -= 1) {
			String temp = String.valueOf(binaryOfPoint.charAt(i));
			if (false == "0".equals(temp)) {
				value =value.add(this.getValueOfBite(numberOfBite));
			}
			numberOfBite += 1;
		}
		
		return value;
	}

	public List<BigInteger> decode(BigInteger no,List<Integer> interactRatios) {
		List<BigInteger> result = new ArrayList<BigInteger>();
		int biteOfBinary = numberOfBites * this.ratioSum(interactRatios);
		String binary = this.getBinary(no, biteOfBinary);
	
		String[] binaryOfDimensions = new String[numberOfDimensions];
		for (int i = 0; i < numberOfDimensions; i += 1) {
			binaryOfDimensions[i] = "";
		}
		
		int countOfbinary = 0;
		do {
			for (int i = 0; i < numberOfDimensions; i += 1) {
				int ratio=interactRatios.get(i).intValue();
				if (ratio==1) {
				binaryOfDimensions[i] = binaryOfDimensions[i]
						+ binary.charAt(countOfbinary);
				countOfbinary += 1;
				}
				if (ratio>1) {
					binaryOfDimensions[i] = binaryOfDimensions[i]
							+ binary.substring(countOfbinary, countOfbinary+ratio);
					countOfbinary += ratio;
				}
				
			}
		} while (countOfbinary < biteOfBinary);
		
		for (int j = 0; j < numberOfDimensions; j += 1) {
			int numberOfbite = 0;
			BigInteger value = new BigInteger("0");
			for (int i = binaryOfDimensions[j].length() - 1; i >= 0; i -= 1) {
				String temp = String.valueOf(binaryOfDimensions[j].charAt(i));
				if (false=="0".equals(temp)) {
					value =value.add(this.getValueOfBite(numberOfbite)) ;
				}
				numberOfbite += 1;
			}
			result.add(value);
		}
		
		return result;
	}

	private String getBinary(BigInteger number, int numbeOfBites) {
		StringBuffer result = new StringBuffer();
		BigInteger remainder = new BigInteger(number.toByteArray());
		while (!number.equals(new BigInteger("0"))) {
			remainder = number.mod(new BigInteger("2"));
			number = number.divide(new BigInteger("2"));
			result.insert(0, remainder.toString());
		}
		while (result.length() < numbeOfBites) {
			result.insert(0, 0);
		}
		return result.toString();
	}
private int ratioSum(List<Integer> ratios) {
	int m=0;
	for (Integer integer : ratios) {
		m+=integer.intValue();
	}
	return m;
}
	private BigInteger getValueOfBite(Integer bite) {
		/*int value = 1;
		for (int i = 0; i < bite; i += 1) {
			value *= 2;
		}
		return value;*/
		BigInteger value=new BigInteger("2");
		return value.pow(bite);
	}

	public static void main(String[] args)
			throws NotSupportedNumberOfDimensionsException, NotPositiveException {
		// TODO Auto-generated method stub
	/*	List<Segment> endList = new ArrayList<Segment>();
		endList.add(new BigInteger("105"));	// TODO 没有检测numberOfbits范围不够
		endList.add(new BigInteger("106"));
		endList.add(new BigInteger("7"));
		endList.add(new BigInteger("4"));
		endList.add(new BigInteger("3"));
		endList.add(new BigInteger("2"));
		List<Integer> ratios=new ArrayList<Integer>();
		ratios.add(new Integer(2));
		ratios.add(new Integer(2));
		ratios.add(new Integer(1));
		ratios.add(new Integer(1));
		ratios.add(new Integer(1));
		ratios.add(new Integer(1));
		try {
			FlatZordering z = new FlatZordering(endList.size());
			Iterator<BigInteger> it1 = endList.iterator();
			System.out.print("需要编码的数据为[ ");
			while (it1.hasNext()) {
				System.out.print(it1.next()+" ");
			}
			System.out.println("]");
			System.out.print("Z编码后结果为：");
			System.out.println(z.encode(endList,ratios));
			System.out.print("Z解码为：");
			List<BigInteger> list = z.decode(z.encode(endList,ratios),ratios);
			Iterator<BigInteger> it2 = list.iterator();
			System.out.print("解码后的数据为[ ");
			while (it2.hasNext()) {
				System.out.print(it2.next()+" ");
			}
			System.out.println("]");
		} catch (NotPositiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		List<Segment> left = new ArrayList<Segment>();
		left.add(new Segment(3));
		left.add(new Segment(3));
		left.add(new Segment(3));
		left.add(new Segment(3));
		List<Segment> left1 = new ArrayList<Segment>();
		left1.add(new Segment(3));
		left1.add(new Segment(3));
		left1.add(new Segment(3));
		left1.add(new Segment(4));
		List<List<Segment>> lefts = new ArrayList<List<Segment>>();
		lefts.add(left);
		lefts.add(left1);
		/*List<Segment> left1 = new ArrayList<Segment>();
		left1.add(new Segment(4));
		left1.add(new Segment(3));
		left1.add(new Segment(3));
		left1.add(new Segment(3));
		lefts.add(left1);
		List<Segment> left2 = new ArrayList<Segment>();
		left2.add(new Segment(3));
		left2.add(new Segment(3));
		left2.add(new Segment(4));
		left2.add(new Segment(3));
		lefts.add(left2);
		List<Segment> left3 = new ArrayList<Segment>();
		left3.add(new Segment(4));
		left3.add(new Segment(3));
		left3.add(new Segment(4));
		left3.add(new Segment(3));
		lefts.add(left3);*/
		List<Integer> ratios=new ArrayList<Integer>();
		ratios.add(1);ratios.add(2);ratios.add(1);ratios.add(2);
		for (int j = 0; j < lefts.size(); j++) {
			String file = new FlatZordering(4).encode(
					lefts.get(j), ratios)+"";
			System.out.println(file);
		}
	
	}

}

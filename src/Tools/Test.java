package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import DB.DB;
import Exception.NotPositiveException;
import Query.Cell;
import System.Dimension;
import System.Segment;
import System.SystemInfo;

public class Test {
	SystemInfo systemInfo=new SystemInfo();
	private  List<String> dimensionList = systemInfo.getDemensionnames();
	private  List<String> datakeyList = systemInfo.getDatakeys();
	private  int filesegment = systemInfo.getFileSegemnt();

	private  List<Integer> dimensionIndex = new ArrayList<Integer>();
	private  List<Integer> positions = getKeyPositions();
	


	

		
        public void testA(String value) throws IOException,  InterruptedException {  
       
			// select dimenion key from datakey
        //	String value="1|goldenrod lavender spring chocolate lace|Manufacturer#1|Brand#13|PROMO BURNISHED COPPER|7|JUMBO PKG|901.00|ly. slyly ironi|";
					String[] values = value.split("\\|");
					System.out.println(values.length);
					List<String> queryvalue = new ArrayList<String>();
					 List<String> querySymble = new ArrayList<String>();
					List<String> queryOperator = new ArrayList<String>();
					 List<String> queryDimension=new ArrayList<String>();
					
					for (int i = 0; i < positions.size(); i++) {
						queryDimension.add(datakeyList.get(positions.get(i)));
						queryvalue.add(values[positions.get(i)]);
						querySymble.add("=");
						queryOperator.add("and");
					}
					queryOperator.remove(0);
			   String path;
			  
			try {
				 System.out.println("start cell");
				path = new Cell().findCellByDimensionValue(queryDimension, dimensionIndex,queryvalue, querySymble, queryOperator, null).get(0);
				System.out.println(path);
				for (int i = 0; i < Math.ceil(dimensionList.size()
						/ filesegment); i++) {
					int lable = i *filesegment+1;
					String filename=path + "s" + lable;
				File file=new File(filename);
				  FileWriter fileWritter = new FileWriter(file.getName(),true);
		             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		             bufferWritter.write(getDataValueonDimension(value.toString(), i));
		             bufferWritter.close();

		   //      System.out.println("Done");

				}
				String filename=path + "st";
				File file=new File(filename);
				String mString=getDataValueonTrial(value.toString());
				  FileWriter fileWritter = new FileWriter(file.getName(),true);
		             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		             bufferWritter.write(mString);
		             bufferWritter.close();
				
			
			} catch (NotPositiveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
        }
       
	
	private  String getDataValueonDimension(String line, int number) {
		String[] resStrings = line.split("\\|");
		String results = "";
		int start = filesegment * number ;

		for (int i = start; i < start + filesegment; i++) {
			if (!dimensionIndex.contains(new Integer(i))) {
				results += ""+ ",";
			}else{
				results += resStrings[positions.get(i) ] + ",";
			}
		}
		return results;
	}

	private  String getDataValueonTrial(String line) {
		String[] resStrings = line.split("\\|");
		String results = "";
		for (int i = 0; i < resStrings.length; i++) {
			boolean isHere=false;
			for (int j = 0; j < positions.size(); j++) {
				if (i == positions.get(j)){
					isHere=true;
				}
				}
			if (!isHere) {
				
				results += "<" + datakeyList.get(i) + ","
						+ resStrings[i] + ">" + ",";
			}}
		
		
	//	System.out.println(results);
		
		return results;
	}

	private  List<Integer> getKeyPositions() {
		List<Integer> pos = new ArrayList<Integer>();
		for (int i = 0; i < dimensionList.size(); i++) {
			for (int j = 0; j < datakeyList.size(); j++) {
				if (dimensionList.get(i).equals(datakeyList.get(j))) {
					 pos.add(j);
					dimensionIndex.add(i);
				}
			}
		}
		return  pos;
	
		
		
	}
	  public List<String> readFileByLines(String fileName) {
	        File file = new File(fileName);
	        BufferedReader reader = null;
	        List<String> temList=new ArrayList<String>();
	        try {
	            System.out.println("以行为单位读取文件内容，一次读一整行：");
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            int line = 1;
	            // 一次读入一行，直到读入null为文件结束
	            while ((tempString = reader.readLine()) != null) {
	                // 显示行号
	                System.out.println("line " + line + ": " + tempString);
	                line++;
	                temList.add(tempString);
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	        return temList;
	    }

	public static void main(String[] args) throws IOException, InterruptedException {
		
		/*	List<Segment> ses=new ArrayList<Segment>();
		ses.add(new Segment(5));
		ses.add(new Segment(1));
		ses.add(new Segment(3));
		ses.add(new Segment(2));
		List<Integer> rts=new ArrayList<Integer>();
		rts.add(3);
		rts.add(2);
		rts.add(1);
		rts.add(0);
	
        for(Segment mapping: test.selectLeaveSegmentsOnDimension(ses, rts)){
            System.out.println(mapping.getSegmentIndex());
       } */
	/*	Cell cell=new Cell();
		Character.isDigit
		List<Segment> segments=new ArrayList<Segment>();
		Segment s11=new Segment(1, 1);
		Segment s12=new Segment(2, 1);
		Segment s13=new Segment(3, 1);
		Segment s14=new Segment(4, 1);
		segments.add(s11);segments.add(s12);segments.add(s13);segments.add(s14);
		cell.getTargetSegmentIndexonNumericalDimension(0,
				segments, 40);*/
	/*	String[] res={"1|goldenrod lavender spring chocolate lace|Manufacturer#1|Brand#13|PROMO BURNISHED COPPER|7|JUMBO PKG|901.00|ly. slyly ironi|",
				
				"3|spring green yellow purple cornsilk|Manufacturer#4|Brand#42|STANDARD POLISHED BRASS|21|WRAP CASE|903.00|egular deposits hag|",
				"20335|lawn black rose seashell mint|Manufacturer#5|Brand#53|SMALL BRUSHED COPPER|50|WRAP BOX|1255.33|packages wake quickly |",
				"20329|blue yellow coral azure spring|Manufacturer#5|Brand#51|LARGE PLATED STEEL|8|JUMBO BOX|1249.32|ously bol|",
				};*/
	//	String[] res={"3|spring green yellow purple cornsilk|Manufacturer#4|Brand#42|STANDARD POLISHED BRASS|21|WRAP CASE|903.00|egular deposits hag|"};
	
	Test test=new Test();
	List<String> res=test.readFileByLines("../../../data/part.tbl");
	for (int i = 0; i < res.size(); i++) {
		
		test.testA(res.get(i));
	}

	}
		
}

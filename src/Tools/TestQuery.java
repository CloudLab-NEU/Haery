package Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DefaultStringifier;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Exception.NotPositiveException;
import MapReduce.ExtractData;
import System.SystemInfo;
import application.Haery;

public class TestQuery {
	/*private static final List<String> dimensionNames = SystemInfo
			.getDemensionnames();
	private static final double fileSegment = (double)SystemInfo.getFileSegemnt();
	private static final List<String> targetKey = new ArrayList<String>();
	String SQL = "select * from a where size>20";
	  
    private static SequenceFile.Reader reader = null;  
    private static Configuration conf = new Configuration(); 
		public TestQuery() {
			

			getTargetKey(SQL,targetKey);
		}

	public void testDimension(String filename) {
			
			if (Character.isDigit(filename.split("s")[1].charAt(0))) {
				int index = Integer.parseInt(filename.split("s")[1]);
				List<Integer> positions = new ArrayList<Integer>();
				int length = (int) (index + fileSegment);
				List<String> matchedTargetKey =new ArrayList<String>();
					for (int j = 0; j < targetKey.length; j++) {
						for (int i = index; i < length; i++) {
						if (dimensionNames.get(i - 1).equals(targetKey[j].toString())) {
							positions.add(i - index);
							matchedTargetKey.add(targetKey[j].toString());
						}
					}
				}
				String[] values = value.toString().split(",");
				String outs = "";
				for (int i = 0; i < positions.size(); i++) {
					outs += matchedTargetKey.get(i)+":"+values[i] + ",";
				}
				//targetkey1:xxx,targetkey2:xxx,
				output.collect(new Text(key + ""), new Text(outs));
			}
		

		}
	

	public void testTravial (){
		
		
			Writable[] targetKey = DefaultStringifier.load(conf, "targetKey",
					TextArrayWritable.class).get();
	
			FileSplit fileSplit = (FileSplit) reporter.getInputSplit();
			String filename = fileSplit.getPath().getName();
			if (!Character.isDigit(filename.split("s")[1].charAt(0))) {
				String[] values = value.toString().split(",");
				String finalRes="";
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < targetKey.length; j++) {
						String keyvalue=values[i];
						if (keyvalue.contains(targetKey[j].toString())) {
							String middleRes=keyvalue.substring(1, keyvalue.length()-1);
							String res=middleRes.replace(",", ":")+",";
							finalRes+=res;
						}
					}
				}
				output.collect(new Text(key + ""), new Text(finalRes));
			}
		}
	
	public void MultipleReducer (){
		    log.info("================");
		    log.info("         =======");
		    log.info("              ==");
		
		    String finalRes="";
		    String results="";
		   
		   for (Text text:values) {
			results+=text.toString();
		}
		   String[] splits=results.split(",");
		    
		    for (int i = 0; i < targetKey.length; i++) {
		    	  for (int j = 0; j < splits.length; j++) {
				    	if (splits[j].contains(targetKey[i].toString())) {
				    		String[] res=splits[j].split(":");
				    		finalRes+=res[1];
						}
				}
		    		
			}
		  
		    log.info("              ==");
		    log.info("         =======");
		    log.info("================");
		    cxt.write(new Text(""),new Text(finalRes));
		  }
		
	

	    public static void main(String[] args) throws Exception {  
	    	
		
	    	List<String> mList= findPath( SQL);  
	         FileSystem fileSystem = FileSystem.get(conf); 
		for (int i = 0; i < mList.size(); i++) {
			String pathString = mList.get(i);
			Path path = new Path(pathString);
			if (fileSystem.exists(path)) {
				if (pathString.contains("st")) {
					MultipleInputs.addInputPath(job, path,
							SequenceFileInputFormat.class,
							MultiMapForTravial.class);
				} else {
					MultipleInputs.addInputPath(job, path,
							SequenceFileInputFormat.class,
							MultiMapForDimension.class);
				}
				 
			}
		}
		FileOutputFormat.setOutputPath(job, new Path(args[1]));  
	    	job.submit();
			job.waitForCompletion(true);
	       
	    }  
	     public  void getTargetKey(String sql,List<String> targetKey ) {
	    		if (!sql.contains("*")) {
	    			String s = sql.substring(sql.indexOf("select") + 6,
	    					sql.indexOf("from")).trim();
	    			String str[] = s.split(",");
	    			for (String st : str) {
	    				targetKey.add(st);

	    			}
	    		} else {
	    			targetKey.add("*");
	    		}
		}
	  
	    private static List<String> findPath(String SQL) throws NotPositiveException {
	    	Haery haery = new Haery();
			String sqlString = SQL;

			List<String> mList = haery.query(sqlString);
			return mList;
		}
	  
*/
	public static void main(String[] args) {
		TestQuery testQuery=new TestQuery();
		String SQL = "select name,size from a where size>20";
		String targetKeys = "";
		targetKeys=testQuery.getTargetKey(SQL, targetKeys);
		System.out.println(targetKeys.split(",").length);
	}
	private static String getTargetKey(String sql, String targetKey) {
		if (!sql.contains("*")) {
			String s = sql.substring(sql.indexOf("select") + 6,
					sql.indexOf("from")).trim();
			String str[] = s.split(",");
			for (String st : str) {
				targetKey += st + ",";

			}
		} else {
			targetKey += "*";
		}
		return targetKey;
	}
}

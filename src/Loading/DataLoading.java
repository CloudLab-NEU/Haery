package Loading;

import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DefaultStringifier;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.sun.xml.bind.v2.runtime.reflect.ListIterator;

import Exception.NotPositiveException;
import Query.Cell;
import System.Dimension;
import System.Segment;
import System.SystemInfo;
import Tools.Test1;

public class DataLoading {
	//private static Configuration conf = null;

	public DataLoading() {
		
	}

	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		

		private TextArrayWritable datakeyListArrayWritable;
		private TextArrayWritable positionsArrayWritable;
		private TextArrayWritable dimensionIndexArrayWritable;

		private  TextArrayWritable dimensionsArrayWritable;
		

		private Object[] datakeyList;
		private Object[] dimensions;
		private Object[] positions;
		private Object[] dimensionIndex;
		public void setup(Context context) throws IOException {
			Configuration conf=context.getConfiguration();
			
			 datakeyListArrayWritable= DefaultStringifier.load(conf, "datakeyList", TextArrayWritable.class);
			 positionsArrayWritable= DefaultStringifier.load(conf, "positions", TextArrayWritable.class);
			 dimensionIndexArrayWritable= DefaultStringifier.load(conf, "dimensionIndex", TextArrayWritable.class);
			 
			
			  dimensionsArrayWritable= DefaultStringifier.load(conf, "dimensions", TextArrayWritable.class); 
			  
			  dimensions= (Object[]) dimensionsArrayWritable.toArray(); 
		datakeyList= (Object[]) datakeyListArrayWritable.toArray();
		positions= (Object[]) positionsArrayWritable.toArray();
		 dimensionIndex= (Object[]) dimensionIndexArrayWritable.toArray();
			
		}
		
		
        public void map(LongWritable key, Text value, Context context) throws IOException,  InterruptedException {  
        	String[] values = value.toString().split("\\|");
					 List<String> queryvalue = new ArrayList<String>();
					 List<String> querySymble = new ArrayList<String>();
					List<String> queryOperator = new ArrayList<String>();
					 List<String> queryDimension=new ArrayList<String>();
			List<Dimension> realdimensions=new ArrayList<Dimension>();
			List<Integer> ratios=new ArrayList<Integer>();
				
			
			//change string to dimension
					 for (int i = 0; i < dimensions.length; i++) {
						 Dimension dimension=new Dimension();
					    String dimensionInfo=dimensions[i].toString();
					    String[] originalThreads=dimensionInfo.split("\\|")[0].split(" ");
						 dimension.setOriginalThreadForDimension(originalThreads);
						 
						Integer ratio=Integer.parseInt(dimensionInfo.split("\\|")[1].split("\\.")[0]);
						 String[] segmentInfo=dimensionInfo.split("\\|")[1].split("\\.")[1].split("\\*");
						 List<Segment> segments=new ArrayList<Segment>();
						 for (int j = 0; j < segmentInfo.length; j++) {
							 segments.add(new Segment(Integer.parseInt(segmentInfo[j].split(" ")[0]), Integer.parseInt(segmentInfo[j].split(" ")[1])));
						}
						 
						 dimension.setSegments(segments);
						 ratios.add(ratio);
						realdimensions.add(dimension);
					}
					 
					 
					for (int i = 0; i < positions.length; i++) {
						queryDimension.add(datakeyList[Integer.parseInt(positions[i]+"")].toString());
						queryvalue.add(values[Integer.parseInt(positions[i]+"")]);
						querySymble.add("=");
						queryOperator.add("and");
					}
					queryOperator.remove(0);
					
		   String path;
			try {
				path = new Loading(realdimensions,ratios).findCellByDimensionValue(queryDimension, dimensionIndex,queryvalue, querySymble, queryOperator).get(0);
			
					context.write(new Text(path+"|"+key.toString()), value);
			} catch (NotPositiveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
        }
	}
      public static class Reduce extends Reducer<Text, Text, Text, Text> {
    	  private TextArrayWritable dimensionlistArrayWritable;
  		private TextArrayWritable filesegmentArrayWritable;
  		private TextArrayWritable datakeyListArrayWritable;
  		private TextArrayWritable positionsArrayWritable;
  		private TextArrayWritable dimensionIndexArrayWritable;
  		
  		private Object[] dimensionlist;
  		private Object[] datakeyList;
		private Object[] filesegment;
  		private Object[] positions;
  		private	Object[] dimensionIndex;
  		private int intfilesegment;
    	  
        	private MultipleOutputs<Text, Text> mos;

    		public void setup(Context context) throws IOException,InterruptedException {
    			mos=new MultipleOutputs<Text, Text>(context);
    			Configuration conf=context.getConfiguration();
				
				 dimensionlistArrayWritable= DefaultStringifier.load(conf, "dimensionlist", TextArrayWritable.class);
				 filesegmentArrayWritable= DefaultStringifier.load(conf, "filesegment", TextArrayWritable.class);
				 datakeyListArrayWritable= DefaultStringifier.load(conf, "datakeyList", TextArrayWritable.class);
				 positionsArrayWritable= DefaultStringifier.load(conf, "positions", TextArrayWritable.class);
				 dimensionIndexArrayWritable= DefaultStringifier.load(conf, "dimensionIndex", TextArrayWritable.class);
				// ratios= DefaultStringifier.load(conf, "ratios", TextArrayWritable.class); 
				
				dimensionlist= (Object[]) dimensionlistArrayWritable.toArray();
				
				 filesegment= (Object[]) filesegmentArrayWritable.toArray();
				datakeyList= (Object[]) datakeyListArrayWritable.toArray();
			 positions= (Object[]) positionsArrayWritable.toArray();
				  dimensionIndex= (Object[]) dimensionIndexArrayWritable.toArray();
				 intfilesegment=Integer.parseInt(filesegment[0]+"");
    		}
    		public void reduce(Text key,Iterable<Text> values,Context context) throws IOException, InterruptedException{
    	String[] ids=key.toString().split("\\|");
    
    			for(Text val:values){
    				 for (int i = 0; i < Math.ceil(Double.parseDouble(dimensionlist.length+"")
    							/ intfilesegment); i++) {
    					 int lable = i *intfilesegment+1;
    						String filename=ids[0] + "s" + lable;
    						mos.write("haeryExpand", new Text(ids[1]), new Text(
    								getDataValueonDimension(val.toString(), i)),filename );
    					}
    				 String filename=ids[0] + "st";
    					
    					mos.write("haeryExpand", new Text(ids[1]), new Text(
    							getDataValueonTrial(val.toString())),filename );
    					
    					
    			 }
    		}
    		
    		private  String getDataValueonDimension(String line, int number) {
    			String[] resStrings = line.split("\\|");
    			String results = "";
    			int start = intfilesegment * number ;

    			for (int i = start; i < start + intfilesegment; i++) {
    				boolean isContains=false;
    				for (int j = 0; j < dimensionIndex.length; j++) {
    					if (dimensionIndex[j].toString().equals(new Integer(i).toString())){
    						isContains=true;
    						break;
    					}
    						
					}
    				if (!isContains) {
    					results += ""+ ",";
    				}else{
    					results += resStrings[Integer.parseInt(positions[i]+"" )] + "|";
    				}
    			}
    			return results;
    		}
    		
    		private  String getDataValueonTrial(String line) {
    			String[] resStrings = line.split("\\|");
    			String results = "";
    			for (int i = 0; i < resStrings.length; i++) {
    				boolean isHere=false;
    				for (int j = 0; j < positions.length; j++) {
    					if (i == Integer.parseInt(positions[j]+"")){
    						isHere=true;
    					}
    					}
    				if (!isHere) {
    					
    					results += "<" + datakeyList[i] + ","
    							+ resStrings[i] + ">" + "|";
    				}}
    			return results;
    		}
    		 public void cleanup(Context context) throws IOException,InterruptedException {
    				mos.close();
    			}
    	}
       
	



	

	public static class TextArrayWritable  extends ArrayWritable {
		public TextArrayWritable() {
			// TODO Auto-generated constructor stub
			super(Text.class);
		}
		public TextArrayWritable(List<String> strings) {
			super(Text.class);
			Text[] texts=new Text[strings.size()];
			for (int i = 0; i < strings.size(); i++) {
				texts[i]=new Text(strings.get(i));
			}
			set(texts);
		} 
	}
	
	public static List<String> changeDimensionToString (List<Dimension> dimensions) {
		List<String> reStrings=new ArrayList<String>();
		for (Dimension dimension : dimensions) {
			String[] threads=dimension.getOriginalThreadForDimension();
			List<Segment> segments=dimension.getSegments();
			String segs="";
			for (Segment segment : segments) {
				segs+=segment.getSegmentIndex()+" "+segment.getSegmentVersion()+"*";
			}
			String ratio=dimension.getRatios()+"";
			String m=threads[0]+" "+threads[1]+"|"+ratio+"."+segs;
			reStrings.add(m);
		}
		return reStrings;
		
	}
	
	//return position, dimensionIndex
	private static List<List<String>> getKeyPositionsandDimenisonIndex(List<String> dimensionList, List<String> datakeyList) {
		List<String> pos = new ArrayList<String>();
		List<String> dimensionIndex = new ArrayList<String>();
		List<List<String>> res=new ArrayList<List<String>>();
		for (int i = 0; i < dimensionList.size(); i++) {
			for (int j = 0; j < datakeyList.size(); j++) {
				if (dimensionList.get(i).equals(datakeyList.get(j))) {
					 pos.add(j+"");
					 int index=i;
					dimensionIndex.add(index+"");
				}
			}
		}
		res.add(pos);
		res.add(dimensionIndex);
		return  res;
	}

	public static void mergeFile(FileStatus[] newloadfileStatus, FileStatus[] oldfileStatus, FileSystem fileSystem) {

		for (FileStatus file : newloadfileStatus) {
			double size = (double) file.getLen() / 1024 / 1024;
			if (size < 16.0) {
				int filePathIndex = file.getPath().toString().split("/").length - 1;
				String filename = file.getPath().toString().split("/")[filePathIndex];
				for (FileStatus oldfile : oldfileStatus) {
					double oldFilesize = oldfile.getLen() / 1024 / 1024;
					if (oldfile.getPath().toString().contains(filename) && oldFilesize < 16.0) {

						InputStream inputStream;
						try {
							System.out.println(file.getPath().toString()+"file.getPath().toString()");
							inputStream = fileSystem.open(file.getPath());
				
							OutputStream outputStream = fileSystem.append(oldfile.getPath());
						
							IOUtils.copyBytes(inputStream, outputStream, 4096, true);
							inputStream.close();
							outputStream.close();
							fileSystem.delete(file.getPath());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		}
	}
	
	/*
	 * the file will be loaded in /user/haeryData
	 */
	
	public void loading(String filepath) throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException {

		Job job = Job.getInstance();
		String outputFile = "hdfs://cloud0000:9000/user/haeryData";
		
		
		Configuration configuration=job.getConfiguration();
		SystemInfo.initialize();
		TextArrayWritable dimensionlist=new TextArrayWritable(SystemInfo.getDemensionnames());
		TextArrayWritable dimensions=new TextArrayWritable(changeDimensionToString(SystemInfo.getDimensions()));
		TextArrayWritable datakeyList =new TextArrayWritable(SystemInfo.getDatakeys());
		List<List<String>> infos = getKeyPositionsandDimenisonIndex(SystemInfo.getDemensionnames(),SystemInfo.getDatakeys());
	List<String> filesegmentList=	new ArrayList<String>();
	filesegmentList.add(SystemInfo.getFileSegemnt()+"");
		TextArrayWritable filesegment = new TextArrayWritable(filesegmentList);

		TextArrayWritable positions = new TextArrayWritable(infos.get(0));
		TextArrayWritable dimensionIndex =new TextArrayWritable(infos.get(1)) ;
		
		DefaultStringifier.store(configuration, dimensionlist, "dimensionlist");
		DefaultStringifier.store(configuration, dimensions, "dimensions");
		DefaultStringifier.store(configuration, datakeyList, "datakeyList");
		DefaultStringifier.store(configuration, filesegment, "filesegment");
		DefaultStringifier.store(configuration, positions, "positions");
		DefaultStringifier.store(configuration, dimensionIndex, "dimensionIndex");
		job.setJobName("Data Loading");
		job.setJarByClass(DataLoading.class);
		job.setMapperClass(Map.class);
	
	    job.setReducerClass(Reduce.class);
	
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(20);
		MultipleOutputs.addNamedOutput(job,"haeryExpand",SequenceFileOutputFormat.class,Text.class,Text.class);
		SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);
		SequenceFileOutputFormat.setOutputCompressorClass(job,GzipCodec.class);
		FileInputFormat.addInputPath(job, new Path(filepath));
		FileSystem fileSystem = FileSystem.get(
				URI.create("hdfs://cloud0000:9000"), configuration);
		int num = fileSystem.globStatus(new Path(outputFile+"/*")).length;
		if (fileSystem.exists(new Path(outputFile))) {
	
			int wnum = num + 1;
			Path path = new Path(outputFile + "/" + wnum);
			FileOutputFormat.setOutputPath(job, path);
		}else {
			Path path = new Path(outputFile + "/1");
			FileOutputFormat.setOutputPath(job, path);
		}
		
		
		
		job.submit();
		job.waitForCompletion(true);
	}

	  public static void main(String[] args) throws Exception{
		

			Job job = Job.getInstance();
			
			Configuration configuration=job.getConfiguration();
			SystemInfo.initialize();
			TextArrayWritable dimensionlist=new TextArrayWritable(SystemInfo.getDemensionnames());
			TextArrayWritable dimensions=new TextArrayWritable(changeDimensionToString(SystemInfo.getDimensions()));
			TextArrayWritable datakeyList =new TextArrayWritable(SystemInfo.getDatakeys());
			List<List<String>> infos = getKeyPositionsandDimenisonIndex(SystemInfo.getDemensionnames(),SystemInfo.getDatakeys());
		List<String> filesegmentList=	new ArrayList<String>();
		filesegmentList.add(SystemInfo.getFileSegemnt()+"");
			TextArrayWritable filesegment = new TextArrayWritable(filesegmentList);

			TextArrayWritable positions = new TextArrayWritable(infos.get(0));
			TextArrayWritable dimensionIndex =new TextArrayWritable(infos.get(1)) ;
			
			DefaultStringifier.store(configuration, dimensionlist, "dimensionlist");
			DefaultStringifier.store(configuration, dimensions, "dimensions");
			DefaultStringifier.store(configuration, datakeyList, "datakeyList");
			DefaultStringifier.store(configuration, filesegment, "filesegment");
			DefaultStringifier.store(configuration, positions, "positions");
			DefaultStringifier.store(configuration, dimensionIndex, "dimensionIndex");
			job.setJobName("Data Loading");
			job.setJarByClass(DataLoading.class);
			job.setMapperClass(Map.class);
		
		job.setReducerClass(Reduce.class);
		
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setNumReduceTasks(20);
			 
			FileInputFormat.addInputPath(job, new Path(args[0]));
			FileSystem fileSystem = FileSystem.get(
					URI.create("hdfs://Cloud0000:9000"), configuration);
			if (fileSystem.exists(new Path(args[1]))) {
				int num=fileSystem.globStatus(new Path(args[1]+"/*")).length;
				int wnum=num+1;
				Path path= new Path(args[1]+"/"+wnum);
				FileOutputFormat.setOutputPath(job,path);
		
			}else {
				Path path= new Path(args[1]+"/1");
				FileOutputFormat.setOutputPath(job,path);
		
			}
			
			MultipleOutputs.addNamedOutput(job,"haeryExpand",SequenceFileOutputFormat.class,Text.class,Text.class);
			
			job.submit();
			job.waitForCompletion(true);
			int newnum = fileSystem.globStatus(new Path(args[1] + "/*")).length;
			FileStatus[] newloadfileStatus = fileSystem.globStatus(new Path(args[1] + "/" + newnum + "/*"));
			String pattern = "[1-" + 1 + "]";
			FileStatus[] oldfileStatus = fileSystem.globStatus(new Path(args[1] + "/" + pattern + "/*"));
			mergeFile(newloadfileStatus, oldfileStatus, fileSystem);

			
		} 
		

}
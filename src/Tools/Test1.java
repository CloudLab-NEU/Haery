package Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DefaultStringifier;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.conf.Configuration;
import Exception.NotPositiveException;
import Loading.DataLoading;
import Loading.Loading;
import System.Dimension;
import System.Segment;
import System.SystemInfo;
public class Test1 {


	private  static List<String> dimensionList = SystemInfo.getDemensionnames();
//	private static List<String> datakeyList = SystemInfo.getDatakeys();
	private  int filesegment = SystemInfo.getFileSegemnt();

	private  List<Integer> dimensionIndex = new ArrayList<Integer>();
	
	public static class Map extends Mapper<LongWritable, Text, Text, SegArrayWritable> {
		
		
        public void map(LongWritable key, Text value, Context context) throws IOException,  InterruptedException {  
        	String[] values = value.toString().split("\\|");
        	
			 List<String> queryvalue = new ArrayList<String>();
			 List<String> querySymble = new ArrayList<String>();
			List<String> queryOperator = new ArrayList<String>();
			 List<String> queryDimension=new ArrayList<String>();
			 List<Integer> positions =new ArrayList<Integer>();
				Configuration conf=context.getConfiguration();
		
				SegArrayWritable textArrayWritable= DefaultStringifier.load(conf, "dimensionlist", SegArrayWritable.class);
	
				for (int i = 0; i < textArrayWritable.get().length; i++) {
					
					//context.write(new Text(i+" "), textArrayWritable.get()[i].readFields(conf.g));
				}
				
			//	Object[] strings= (Object[]) textArrayWritable.toArray();
		
			//	for (int i = 0; i < strings.size(); i++) {
			//		context.write(new Text(i+" "), new Text(strings.get(i)));
			//	}
		
			
        }
	}

	
		public static class SegArrayWritable  extends ArrayWritable {

			public SegArrayWritable() {
				// TODO Auto-generated constructor stub
				super(Segment.class);
			}
			public SegArrayWritable(List<Segment> strings) {
				super(Segment.class);
				Segment[] texts=new Segment[strings.size()];
				for (int i = 0; i < strings.size(); i++) {
					texts[i]=strings.get(i);
				}
			
				set(texts);
				
			} 
			
		}
	public static void main(String[] args) throws Exception{

		Text in =new Text("test");
		List<Segment> segments=new ArrayList<Segment>();
		segments.add(new Segment(1, 1));
		segments.add(new Segment(2, 1));
		segments.add(new Segment(3, 1));
		SegArrayWritable dimensionlist=new SegArrayWritable(segments);
		
	
		
		Job job = Job.getInstance();
		Configuration configuration=job.getConfiguration();
		DefaultStringifier.store(configuration, dimensionlist, "dimensionlist");
		DefaultStringifier.store(configuration, in, "text");
		job.setJobName("Data Loading");
		job.setJarByClass(Test1.class);
		job.setMapperClass(Map.class);
	
	
	//job.setReducerClass(Reduce.class);
	
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(0);
		 
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.submit();
		job.waitForCompletion(true);
		//Test1 test1=new Test1();
		
		
	}

}

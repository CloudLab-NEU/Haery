package MapReduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



public class CountKeys {
	
	//get the information about the range of given key
	
	private static Configuration conf = null;

	public CountKeys() {
		conf = new Configuration();
		//conf.addResource(new Path("/hadoop/etc/hadoop/core-site.xml"));
	}
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
		
			String[] values = value.toString().split("\\|");
			//wordcount
			context.write(new Text(values[6]+""), new Text("1"));
		}
	}
	 public static class Reduce extends Reducer<Text, Text, Text, Text> {
		
 		public void reduce(Text key,Iterable<Text> values,Context context) throws IOException, InterruptedException{
 			int count=0;
 		
 			for (Text text : values) 
				count++;
 		
 			context.write(key, new Text(count+""));
 		}
 		
 	}
	 public void count() throws IOException, ClassNotFoundException, InterruptedException {
		 Scanner scanner = new Scanner(System.in);
			System.out.println("please input the filepath which the data are stored");
			String filepath = scanner.nextLine();
			
			System.out.println("input the location where the results will be :");
			String location = scanner.nextLine();
			scanner.close();
		 
			Job job = Job.getInstance();
			job.setJobName("Data Loading");
			job.setJarByClass(CountKeys.class);
			job.setMapperClass(Map.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
		job.setReducerClass(Reduce.class);
		//job.setCombinerClass(Reduce.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			//job.setNumReduceTasks(0);
			 
			FileInputFormat.addInputPath(job, new Path(filepath));
			FileOutputFormat.setOutputPath(job, new Path(location));
		
			
			job.submit();
			job.waitForCompletion(true);
		
	}
	 public static void main(String[] args) throws Exception{

			Scanner scanner = new Scanner(System.in);
			System.out.println("please input the filepath which the data are stored");
			String filepath = scanner.nextLine();
			
			System.out.println("input the location where the results will be :");
			String location = scanner.nextLine();
			scanner.close();
		 
			Job job = Job.getInstance();
			job.setJobName("Data Loading");
			job.setJarByClass(CountKeys.class);
			job.setMapperClass(Map.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
		job.setReducerClass(Reduce.class);
		//job.setCombinerClass(Reduce.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			//job.setNumReduceTasks(0);
			 
			FileInputFormat.addInputPath(job, new Path(filepath));
			FileOutputFormat.setOutputPath(job, new Path(location));
		
			
			job.submit();
			job.waitForCompletion(true);
			
		} 
    

}

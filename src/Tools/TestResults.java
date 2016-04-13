package Tools;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import MapReduce.CountKeysrange;
import MapReduce.CountKeysrange.Map;
import MapReduce.CountKeysrange.Reduce;

public class TestResults {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
		
			String[] values = value.toString().split("\\|");
			int size=Integer.parseInt(values[5]);
			if (size>20) {
				context.write(new Text(20+""), new Text("1"));
			}
			
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
	 public static void main(String[] args) throws Exception{

			Job job = Job.getInstance();
			job.setJobName("Data Loading");
			job.setJarByClass(TestResults.class);
			job.setMapperClass(Map.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
		job.setReducerClass(Reduce.class);
		//job.setCombinerClass(Reduce.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			//job.setNumReduceTasks(0);
			 
			FileInputFormat.addInputPath(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
			
			job.submit();
			job.waitForCompletion(true);
			
		} 
    

}

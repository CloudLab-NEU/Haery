package Loading;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import Exception.NotPositiveException;
import Query.Cell;
import System.SystemInfo;

public class DataLoadingforLine {
/*	

	public DataLoadingforLine() {
	
	}

	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		private MultipleOutputs<Text, Text> mos;
		private SystemInfo systemInfo ;
		private List<String> datakeyList ;
		private List<Integer> dimensionIndex ;
		private List<Integer> positions ;
		private List<String> dimensionList ;

		public void setup(Context context) throws IOException, InterruptedException {
			mos = new MultipleOutputs<Text, Text>(context);
			systemInfo = new SystemInfo();
			 datakeyList = systemInfo.getDatakeys();
			  dimensionIndex = new ArrayList<Integer>();
			  positions = getKeyPositions();
			  dimensionList = systemInfo.getDemensionnames();
		}

		private List<Integer> getKeyPositions() {
			List<Integer> pos = new ArrayList<Integer>();
			for (int i = 0; i < dimensionList.size(); i++) {
				for (int j = 0; j < datakeyList.size(); j++) {
					if (dimensionList.get(i).equals(datakeyList.get(j))) {
						pos.add(j);
						dimensionIndex.add(i);
					}
				}
			}
			return pos;
		}
		private String getDataValueonDimension(String line) {
			String[] resStrings = line.split("\\|");
			String results = "";

			for (int i = 0; i < dimensionList.size(); i++) {
				if (!dimensionIndex.contains(new Integer(i))) {
					results += "" + ",";
				} else {
					results += resStrings[positions.get(i)] + "|";
				}
			}
			return results;
		}

		private String getDataValueonTrial(String line) {
			String[] resStrings = line.split("\\|");
			String results = "";
			for (int i = 0; i < resStrings.length; i++) {
				boolean isHere = false;
				for (int j = 0; j < positions.size(); j++) {
					if (i == positions.get(j)) {
						isHere = true;
					}
				}
				if (!isHere) {

					results += "<" + datakeyList.get(i) + "," + resStrings[i] + ">" + "|";
				}
			}
			return results;
		}
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String[] values = value.toString().split("\\|");
			List<String> queryvalue = new ArrayList<String>();
			List<String> querySymble = new ArrayList<String>();
			List<String> queryOperator = new ArrayList<String>();
			List<String> queryDimension = new ArrayList<String>();

			for (int i = 0; i < positions.size(); i++) {
				queryDimension.add(datakeyList.get(positions.get(i)));
				queryvalue.add(values[positions.get(i)]);
				querySymble.add("=");
				queryOperator.add("and");
			}
			queryOperator.remove(0);

			String path;
			try {
				path = new Loading().findCellByDimensionValue(queryDimension, dimensionIndex, queryvalue, querySymble,
						queryOperator).get(0);

				String dimensionFile = path + "sd";
				String trivalFile = path + "st";
				mos.write("haery", new Text(key.toString()), new Text(getDataValueonDimension(value.toString())),
						dimensionFile);
				mos.write("haery", new Text(key.toString()), new Text(getDataValueonTrial(value.toString())),
						trivalFile);
			} catch (NotPositiveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void cleanup(Context context) throws IOException, InterruptedException {
			mos.close();
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		private MultipleOutputs<Text, Text> mos;
		private SystemInfo systemInfo ;
		private List<String> dimensionList;
		private int filesegment;

		private List<String> datakeyList;
		private List<Integer> positions;
		private List<Integer> dimensionIndex ;

		public void setup(Context context) throws IOException, InterruptedException {
			mos = new MultipleOutputs<Text, Text>(context);
			 systemInfo = new SystemInfo();
			dimensionList = systemInfo.getDemensionnames();
			filesegment = systemInfo.getFileSegemnt();
            datakeyList = systemInfo.getDatakeys();
			positions = getKeyPositions();
			dimensionIndex = new ArrayList<Integer>();
		}

		private List<Integer> getKeyPositions() {
			List<Integer> pos = new ArrayList<Integer>();
			for (int i = 0; i < dimensionList.size(); i++) {
				for (int j = 0; j < datakeyList.size(); j++) {
					if (dimensionList.get(i).equals(datakeyList.get(j))) {
						pos.add(j);
						dimensionIndex.add(i);
					}
				}
			}
			return pos;
		}

		private String getDataValueonDimension(String line) {
			String[] resStrings = line.split("\\|");
			String results = "";

			for (int i = 0; i < dimensionList.size(); i++) {
				if (!dimensionIndex.contains(new Integer(i))) {
					results += "" + ",";
				} else {
					results += resStrings[positions.get(i)] + "|";
				}
			}
			return results;
		}

		private String getDataValueonTrial(String line) {
			String[] resStrings = line.split("\\|");
			String results = "";
			for (int i = 0; i < resStrings.length; i++) {
				boolean isHere = false;
				for (int j = 0; j < positions.size(); j++) {
					if (i == positions.get(j)) {
						isHere = true;
					}
				}
				if (!isHere) {

					results += "<" + datakeyList.get(i) + "," + resStrings[i] + ">" + "|";
				}
			}
			return results;
		}

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String[] ids = key.toString().split("\\|");
			for (Text val : values) {
				for (int i = 0; i < Math.ceil(dimensionList.size() / filesegment); i++) {
					int lable = i * filesegment + 1;
					String filename = ids[0] + "s" + lable;

					mos.write("haery", new Text(ids[1]), new Text(getDataValueonDimension(val.toString())), filename);
				}
				String filename = ids[0] + "st";

				mos.write("haery", new Text(ids[1]), new Text(getDataValueonTrial(val.toString())), filename);

			}
		}

		public void cleanup(Context context) throws IOException, InterruptedException {
			mos.close();
		}
	}

	public static void main(String[] args) throws Exception {

		Job job = Job.getInstance();
		job.setJobName("Data Loading");
		job.setJarByClass(DataLoadingforLine.class);
		job.setMapperClass(Map.class);

		// job.setReducerClass(Reduce.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(0);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		MultipleOutputs.addNamedOutput(job, "haery", TextOutputFormat.class, Text.class, Text.class);

		job.submit();
		job.waitForCompletion(true);

	}*/

}

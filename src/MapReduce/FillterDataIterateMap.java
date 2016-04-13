package MapReduce;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;

import Query.MatchFile;
import System.SystemInfo;

public class FillterDataIterateMap {
	/*private static final double fileSegment = (double) SystemInfo
			.getFileSegemnt();
	private static final List<String> dimensionNames = SystemInfo
			.getDemensionnames();

	public static class MultiMapForDimensionMapperwithfillterCheck extends
			Mapper<Text, Text, Text, Text> {

		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] targetKey = context.getConfiguration().get("Key")
					.split("\\|");
			String[] queryKeyStrings = context.getConfiguration()
					.get("queryKey").split("\\|");
			String[] queryValueStrings = context.getConfiguration()
					.get("queryValue").split("\\|");
			String[] querySymble = context.getConfiguration()
					.get("querySymble").split("\\|");
			String[] queryOperator = context.getConfiguration()
					.get("queryOperator").split("\\|");
			String[] values = value.toString().split("\\|");

			String filename = getFilePath(context.getInputSplit());
			String fileSeq = filename.split("s")[1].split("-")[0];
			if (Character.isDigit(fileSeq.charAt(0))) {
				int index = Integer.parseInt(fileSeq);
				List<Integer> positions = new ArrayList<Integer>();
				int length = (int) (index + fileSegment);

				int count = 0;
				for (int i = index; i < length; i++) {
					for (int j = 0; j < queryKeyStrings.length; j++) {
						if (dimensionNames.get(i - 1)
								.equals(queryKeyStrings[j])) {
							count += checkCondition(values[i - 1],
									querySymble[j], queryValueStrings[j]);
						}
					}

				}

				if (count == queryKeyStrings.length) {
					String fillterKey = key.toString() + "|";
					context.write(new Text(fillterKey), new Text("1"));
				}
			}
		}
	}
		public static class MultiMapForTrivalMapperwithfillterCheck extends
		Mapper<Text, Text, Text, Text> {

	public void map(Text key, Text value, Context context)
			throws IOException, InterruptedException {
		String[] targetKey = context.getConfiguration().get("Key")
				.split("\\|");
		String[] queryKeyStrings = context.getConfiguration()
				.get("queryKey").split("\\|");
		String[] queryValueStrings = context.getConfiguration()
				.get("queryValue").split("\\|");
		String[] querySymble = context.getConfiguration()
				.get("querySymble").split("\\|");
		String[] queryOperator = context.getConfiguration()
				.get("queryOperator").split("\\|");
		String[] values = value.toString().split("\\|");

		String filename = getFilePath(context.getInputSplit());
		String fileSeq = filename.split("s")[1].split("-")[0];
		if (!Character.isDigit(fileSeq.charAt(0))) {

			int count = 0;
			for (int i = 0; i < values.length; i++) {
				for (int j = 0; j < queryKeyStrings.length; j++) {
					String[] info=values[i].substring(1,
							values[i].length()).split(",");
					String trivalKey=info[0];
					String trivalValue=info[1];
					if (trivalKey
							.equals(queryKeyStrings[j])) {
						count += checkCondition(trivalValue,
								querySymble[j], queryValueStrings[j]);
					}
				}

			}

			if (count == queryKeyStrings.length) {
				String fillterKey = key.toString() + "|";
				context.write(new Text(fillterKey), new Text("1"));
			}
		}
	}
}

		private static String getFilePath(InputSplit split) throws IOException {
			// InputSplit split = context.getInputSplit();
			Class<? extends InputSplit> splitClass = split.getClass();
			FileSplit fileSplit = null;
			if (splitClass.equals(FileSplit.class)) {
				fileSplit = (FileSplit) split;
			} else if (splitClass.getName().equals(
					"org.apache.hadoop.mapreduce.lib.input.TaggedInputSplit")) {

				try {
					Method getInputSplitMethod = splitClass
							.getDeclaredMethod("getInputSplit");
					getInputSplitMethod.setAccessible(true);
					fileSplit = (FileSplit) getInputSplitMethod.invoke(split);
				} catch (Exception e) {
					throw new IOException(e);

				}
			}
			return fileSplit.getPath().getName();
		}
	

	public static int checkCondition(String value, String symble,
			String queryValue) {
		int count = 0;
		if (Character.isDigit(value.charAt(0))) {
			Double double1 = Double.parseDouble(value);
			if (double1 > Double.parseDouble(queryValue)) {
				if (symble.contains(">")) {
					count++;
				}
			} else if (double1 == Double.parseDouble(queryValue)) {
				if (symble.contains("=")) {
					count++;
				}
			} else {
				if (symble.contains("<")) {
					count++;
				}
			}
		} else {
			if (value.compareTo(queryValue) > 0) {
				if (symble.contains(">")) {
					count++;
				}
			} else if (value.compareTo(queryValue) == 0) {
				if (symble.contains("=")) {
					count++;
				}
			} else {
				if (symble.contains("<")) {
					count++;
				}
			}
		}
		return count;

	}

	public static void main(String[] args) throws IOException {
		String SQL = "select * from a where size>20";

		List<String> targetKey = new ArrayList<String>();
		List<String> queryKeyStrings = new ArrayList<String>();
		List<String> queryValueStrings = new ArrayList<String>();
		List<String> querySymble = new ArrayList<String>();
		List<String> queryOperator = new ArrayList<String>();

		List<List<String>> mList = findPath(SQL, targetKey, queryKeyStrings,
				queryValueStrings, querySymble, queryOperator);
		List<String> fillterList = mList.get(0);
		List<String> notfillterList = mList.get(1);

		Job job = Job.getInstance();
		job.setJarByClass(FillterData.class);
		job.setReducerClass(MultipleReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.getConfiguration().set("Key", changeListToString(targetKey));
		job.getConfiguration().set("queryKey",
				changeListToString(queryKeyStrings));
		job.getConfiguration().set("queryValue",
				changeListToString(queryValueStrings));
		job.getConfiguration().set("querySymble",
				changeListToString(querySymble));
		job.getConfiguration().set("queryOperator",
				changeListToString(queryOperator));
		FileSystem fileSystem = FileSystem.get(
				URI.create("hdfs://Cloud00:9000"), new Configuration());

		for (int i = 0; i < fillterList.size(); i++) {
			String fileString = fillterList.get(i).split("s")[0];

			List<String> filenum = new MatchFile()
					.findSequencefilenum(queryKeyStrings);
			for (int j = 0; j < filenum.size(); j++) {
				String fileIndex = "";
				if (Integer.parseInt(filenum.get(j)) != -1) {
					fileIndex = "s" + filenum.get(j);
				} else {
					fileIndex = "st";
				}

				String pathString = args[0] + "/" + fileString + fileIndex
						+ "-r-00000";

				Path path = new Path(pathString);
				if (fileSystem.exists(path)) {
					System.out.println("exists");
					if (!pathString.contains("st")) {
						MultipleInputs
								.addInputPath(
										job,
										path,
										SequenceFileInputFormat.class,
										MultiMapForDimensionMapperwithfillterCheck.class);

					} else {
						MultipleInputs.addInputPath(job, path,
								SequenceFileInputFormat.class,
								MultiMapForTravialMapperwithfillterCheck.class);
					}

				}
			}
		}

		job.waitForCompletion(true);

		Job job1 = new Job(conf1, "job1");

		FileInputFormat.addInputPath(job1, InputPaht1);

		FileOutputFromat.setOoutputPath(job1, Outpath1);

		job1.waitForCompletion(true);
	}

	private static String changeListToString(List<String> list) {
		String res = "";
		for (String string : list) {
			res += string + "|";
		}
		return res;
	}
*/
}

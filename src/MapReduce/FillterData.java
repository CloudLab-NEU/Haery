package MapReduce;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DefaultStringifier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;

import Loading.DataLoading.TextArrayWritable;
import Query.MatchFile;
import System.SystemInfo;

public class FillterData {

	public static class MultiMapForDimensionMapperwithfillterCheck extends
			Mapper<Text, Text, Text, Text> {
	
		  private TextArrayWritable dimensionlistArrayWritable;
		  private Object[] dimensionlist;
			private TextArrayWritable filesegmentArrayWritable;
			private Object[] filesegment;
			private int intfilesegment;
		
		  public void setup(Context context) throws IOException {
			  Configuration conf=context.getConfiguration();
			  dimensionlistArrayWritable= DefaultStringifier.load(conf, "dimensionlist", TextArrayWritable.class);
			  dimensionlist= (Object[]) dimensionlistArrayWritable.toArray();
			  filesegmentArrayWritable= DefaultStringifier.load(conf, "filesegment", TextArrayWritable.class);
				 filesegment= (Object[]) filesegmentArrayWritable.toArray();
				 intfilesegment=Integer.parseInt(filesegment[0]+"");
		  }
		 

		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			
			
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
				int length = (int) (index + intfilesegment);

				int count = 0;
				for (int i = index; i < length; i++) {
					for (int j = 0; j < queryKeyStrings.length; j++) {
						if (dimensionlist[i - 1].toString()
								.equals(queryKeyStrings[j])) {
							count += checkCondition(values[i - 1],
									querySymble[j], queryValueStrings[j]);
						}
					}
				}
			
				if (count == queryKeyStrings.length) {
					String[] targetKey= context.getConfiguration().get("fillerTargetKeyInCheck").split("\\|");
					List<Integer> positions = new ArrayList<Integer>();
					List<String> matchedTargetKey = new ArrayList<String>();
					for (int i = index; i < length; i++) {
								for (int j = 0; j < targetKey.length; j++) {
									if (dimensionlist[i - 1].toString().equals(
											targetKey[j].toString())) {
										positions.add(i - index);
										matchedTargetKey.add(targetKey[j].toString());
									}
									
										
									}
								}
						String outs = "";
						for (int i = 0; i < positions.size(); i++) {
							
							outs += matchedTargetKey.get(i) + ":"
									+ values[positions.get(i)] + "|";
							
							
						}
						// targetkey1:xxx|targetkey2:xxx|si|
						outs += fileSeq + "|";
						String fillterKey = key.toString() + "|";
					context.write(new Text(fillterKey), new Text(outs));
			
					context.write(new Text(fillterKey), new Text("|"));

					}
					
				}
			}
		}
	
		public static class MultiMapForTrivalMapperwithfillterCheck extends
		Mapper<Text, Text, Text, Text> {

	public void map(Text key, Text value, Context context)
			throws IOException, InterruptedException {
	
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
				context.write(new Text(fillterKey), new Text("|"));
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
		
	}

}

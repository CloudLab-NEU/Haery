package MapReduce;

import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections.map.StaticBucketMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DefaultStringifier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.internal.ws.api.ha.StickyFeature;

import org.apache.hadoop.mapreduce.Counter;

import Exception.NotPositiveException;
import Loading.DataLoading.TextArrayWritable;
import Query.MatchFile;
import System.SystemInfo;
import Tools.ParseSQL;
import application.Haery;
import sun.security.krb5.internal.PAData;

public class FillterDataIntegrate {

	public Log LOG = LogFactory.getLog(NodeManager.class);

	public FillterDataIntegrate() {

	}

	public static class MultiMapForDimensionMappernofillter extends Mapper<Text, Text, Text, Text> {
		SystemInfo systemInfo = new SystemInfo();
		private TextArrayWritable dimensionlistArrayWritable;
		private Object[] dimensionlist;
		private TextArrayWritable filesegmentArrayWritable;
		private Object[] filesegment;
		private int intfilesegment;

		public void setup(Context context) throws IOException {
			Configuration conf = context.getConfiguration();
			dimensionlistArrayWritable = DefaultStringifier.load(conf, "dimensionlist", TextArrayWritable.class);
			dimensionlist = (Object[]) dimensionlistArrayWritable.toArray();
			filesegmentArrayWritable = DefaultStringifier.load(conf, "filesegment", TextArrayWritable.class);
			filesegment = (Object[]) filesegmentArrayWritable.toArray();
			intfilesegment = Integer.parseInt(filesegment[0] + "");
		}

		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			dimensionMapTemplate(value.toString(), context, context.getConfiguration().get("Key"), key, intfilesegment,
					dimensionlist);

		}
	}

	public static class MultiMapForDimensionMapperwithfillter extends Mapper<Text, Text, Text, Text> {
		SystemInfo systemInfo = new SystemInfo();
		private TextArrayWritable dimensionlistArrayWritable;
		private Object[] dimensionlist;
		private TextArrayWritable filesegmentArrayWritable;
		private Object[] filesegment;
		private int intfilesegment;

		public void setup(Context context) throws IOException {
			Configuration conf = context.getConfiguration();
			dimensionlistArrayWritable = DefaultStringifier.load(conf, "dimensionlist", TextArrayWritable.class);
			dimensionlist = (Object[]) dimensionlistArrayWritable.toArray();
			filesegmentArrayWritable = DefaultStringifier.load(conf, "filesegment", TextArrayWritable.class);
			filesegment = (Object[]) filesegmentArrayWritable.toArray();
			intfilesegment = Integer.parseInt(filesegment[0] + "");

		}

		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			String fillterkey = key.toString() + "|";
			dimensionMapTemplate(value.toString(), context, context.getConfiguration().get("fillerTargetKey"),
					new Text(fillterkey), intfilesegment, dimensionlist);

		}
	}

	private static void dimensionMapTemplate(String value, org.apache.hadoop.mapreduce.Mapper.Context context,
			String target, Text key, double fileSegment, Object[] dimensionNames)
					throws IOException, InterruptedException {

		String[] targetKey;
		boolean findAll = false;

		if (target.contains("*")) {
			targetKey = new String[1];
			targetKey[0] = "*";
			findAll = true;
		} else {
			targetKey = target.split("\\|");
		
		}
		String filename = getFilePath(context.getInputSplit());
		String fileSeq = filename.split("s")[1].split("-")[0];
		if (Character.isDigit(fileSeq.charAt(0))) {
			int index = Integer.parseInt(fileSeq);
			List<Integer> positions = new ArrayList<Integer>();
			int length = (int) (index + fileSegment);
			List<String> matchedTargetKey = new ArrayList<String>();
			for (int i = index; i < length; i++) {
				if (findAll) {
					positions.add(i - index);
					matchedTargetKey.add(dimensionNames[i - 1].toString());
				} else {

					for (int j = 0; j < targetKey.length; j++) {
						if (dimensionNames[i - 1].toString().equals(targetKey[j].toString())) {

							positions.add(i - index);
							matchedTargetKey.add(targetKey[j].toString());
							
						}

					}
				}
			}

			String[] values = value.split("\\|");
			String outs = "";
			for (int i = 0; i < positions.size(); i++) {

				outs += matchedTargetKey.get(i) + ":" + values[positions.get(i)] + "|";

			}

			// targetkey1:xxx|targetkey2:xxx|si|
			outs += fileSeq + "|";
			context.write(key, new Text(outs));

		}
	}

	public static class MultiMapForTravialMappernofillter extends Mapper<Text, Text, Text, Text> {

		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			trivalMapTemplate(context, value.toString(), key);
		}

	}

	public static class MultiMapForTravialMapperwithfillter extends Mapper<Text, Text, Text, Text> {

		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			String fillterkey = key.toString() + "|";
			trivalMapTemplate(context, value.toString(), new Text(fillterkey));
		}

	}

	private static void trivalMapTemplate(org.apache.hadoop.mapreduce.Mapper.Context context, String value, Text key)
			throws IOException, InterruptedException {
		String target = "";
		target = context.getConfiguration().get("Key");
		String[] targetKey;
		boolean findAll = false;

		if (target.contains("*")) {
			targetKey = new String[1];
			targetKey[0] = "*";
			findAll = true;
		} else {
			targetKey = target.split("\\|");
		}

		String filename = getFilePath(context.getInputSplit());
		String fileSeq = filename.split("s")[1].split("-")[0];
		if (!Character.isDigit(fileSeq.charAt(0))) {

			String[] values = value.toString().split("\\|");
			String finalRes = "";

			for (int i = 0; i < values.length; i++) {
				if (findAll) {
					String middleRes = values[i].substring(1, values[i].length() - 1);
					String res = middleRes.replace(",", ":") + "|";
					finalRes += res;
				} else {
					for (int j = 0; j < targetKey.length; j++) {
						String keyvalue = values[i];
						if (keyvalue.contains(targetKey[j].toString())) {
							String middleRes = keyvalue.substring(1, keyvalue.length());

							String res = middleRes.replace(",", ":") + "|";
							finalRes += res;
						}
					}
				}
			}
			finalRes += fileSeq + "|";
			context.write(key, new Text(finalRes));
		}
	}

	public static String getFilePath(InputSplit split) throws IOException {
		// InputSplit split = context.getInputSplit();
		Class<? extends InputSplit> splitClass = split.getClass();
		FileSplit fileSplit = null;
		if (splitClass.equals(FileSplit.class)) {
			fileSplit = (FileSplit) split;
		} else if (splitClass.getName().equals("org.apache.hadoop.mapreduce.lib.input.TaggedInputSplit")) {

			try {
				Method getInputSplitMethod = splitClass.getDeclaredMethod("getInputSplit");
				getInputSplitMethod.setAccessible(true);
				fileSplit = (FileSplit) getInputSplitMethod.invoke(split);
			} catch (Exception e) {
				throw new IOException(e);

			}
		}
		return fileSplit.getPath().getName();
	}

	public static class MultipleReducer extends Reducer<Text, Text, Text, Text> {

		private TextArrayWritable dimensionlistArrayWritable;
		private Object[] dimensionlist;

		public void setup(Context context) throws IOException {
			Configuration conf = context.getConfiguration();
			dimensionlistArrayWritable = DefaultStringifier.load(conf, "dimensionlist", TextArrayWritable.class);
			dimensionlist = (Object[]) dimensionlistArrayWritable.toArray();

		}

		public void reduce(Text key, Iterable<Text> values, Context cxt) throws IOException, InterruptedException {
			// retailprice:901.00|type:PROMO BURNISHED COPPER|t|
			// retailprice:901.00|type:PROMO BURNISHED COPPER|i|

			boolean isFillter = false;

			if (key.toString().contains("|")) {
				List<Text> texts = new ArrayList<Text>();
				for (Text value : values) {
					if (value.toString().equals("|")) {
						isFillter = true;
					} else {
						texts.add(new Text(value.toString()));

					}
				}
				if (isFillter) {
					matchResult(cxt, texts, dimensionlist);

				}
			} else {
				matchResult(cxt, values, dimensionlist);
			}
		}

	}

	private static void matchResult(org.apache.hadoop.mapreduce.Reducer.Context cxt, Iterable<Text> values,
			Object[] dimensionNames) throws IOException, InterruptedException {
		String target = cxt.getConfiguration().get("Key");
		String[] targetKey;
		if (target.contains("*")) {
			targetKey = new String[1];
			targetKey[0] = "*";
		} else {
			targetKey = target.split("\\|");
		}

		String finalRes = "";
		List<String> splits = new ArrayList<String>();
		List<String> trivialdata = new ArrayList<String>();
		List<String> dimensiondata = new ArrayList<String>();

		for (Text text : values) {
			if (!text.toString().equals("|")) {
				String[] withSymble = text.toString().split("\\|");
				for (int i = 0; i < withSymble.length - 1; i++) {
					splits.add(withSymble[i]);
					if (Character.isDigit(withSymble[withSymble.length - 1].charAt(0))) {
						dimensiondata.add(withSymble[i]);
					} else {
						trivialdata.add(withSymble[i]);
					}
				}
			}

		}
		if (targetKey[0].equals("*")) {
			for (int i = 0; i < dimensionNames.length; i++) {
				for (int j = 0; j < dimensiondata.size(); j++) {
					if (dimensiondata.get(j).contains(dimensionNames[i].toString())) {
						String[] res = dimensiondata.get(j).split(":");
						finalRes += res[1] + ",";
					}
				}
			}
			Collections.sort(trivialdata);
			for (int i = 0; i < trivialdata.size(); i++) {
				finalRes += trivialdata.get(i) + ",";
			}

		} else {
			for (int i = 0; i < targetKey.length; i++) {
				for (int j = 0; j < splits.size(); j++) {
					if (splits.get(j).contains(targetKey[i].toString())) {
						String[] res = splits.get(j).split(":");
						finalRes += res[1] + ",";
					}

				}

			}
		}
		cxt.write(new Text(), finalRes);

	}

	private static String changeListToString(List<String> list) {
		String res = "";
		for (String string : list) {
			res += string + "|";
		}
		return res;
	}

	public static class TextArrayWritable extends ArrayWritable {
		public TextArrayWritable() {
			// TODO Auto-generated constructor stub
			super(Text.class);
		}

		public TextArrayWritable(List<String> strings) {
			super(Text.class);
			Text[] texts = new Text[strings.size()];
			for (int i = 0; i < strings.size(); i++) {
				texts[i] = new Text(strings.get(i));
			}
			set(texts);
		}
	}

	private static List<List<String>> findPath(String SQL, List<String> targetKey, List<String> queryKeyStrings,
			List<String> queryValueStrings, List<String> querySymble, List<String> queryOperator,List<String> fillerTargetKey,List<String> fillerTargetKeyInCheck)
					throws NotPositiveException {
		Haery haery = new Haery();

		ParseSQL parseSQL = new ParseSQL();
		parseSQL.parserSQL(SQL, targetKey, queryKeyStrings, queryValueStrings, querySymble, queryOperator);

		for (String targetKeystring : targetKey) {
			for (String query : queryKeyStrings) {
				if (targetKeystring.equals(query)) {
					fillerTargetKeyInCheck.add(targetKeystring);
				} else {
					fillerTargetKey.add(targetKeystring);
				}
			}
		}
		List<List<String>> mList = haery.queryCell(queryKeyStrings, queryValueStrings, querySymble, queryOperator,
				targetKey);
		return mList;
	}

	public void query(String SQL) throws IllegalArgumentException, IOException, NotPositiveException,
			ClassNotFoundException, InterruptedException {

		String inputFile = "hdfs://cloud0000:9000/user/haeryData";
		String outputFile = "hdfs://cloud0000:9000/user/haery/result";

		SystemInfo.initialize();
		List<String> targetKey = new ArrayList<String>(); // all the
															// targetkeys=fillerkey+fillerTargetKeyInCheck
		List<String> queryKeyStrings = new ArrayList<String>();
		List<String> queryValueStrings = new ArrayList<String>();
		List<String> querySymble = new ArrayList<String>();
		List<String> queryOperator = new ArrayList<String>();
		List<String> fillerTargetKey = new ArrayList<String>();
		List<String> fillerTargetKeyInCheck = new ArrayList<String>();

		List<List<String>> mList = findPath(SQL, targetKey, queryKeyStrings, queryValueStrings, querySymble,
				queryOperator, fillerTargetKey, fillerTargetKeyInCheck);

		List<String> fillterList = mList.get(0);
		List<String> fillterListInCheck = mList.get(1);

		List<String> notfillterList = mList.get(2);
		Job job = Job.getInstance();
		Configuration configuration = job.getConfiguration();

		TextArrayWritable dimensionlist = new TextArrayWritable(SystemInfo.getDemensionnames());
		DefaultStringifier.store(configuration, dimensionlist, "dimensionlist");

		List<String> filesegmentList = new ArrayList<String>();
		filesegmentList.add(SystemInfo.getFileSegemnt() + "");
		TextArrayWritable filesegment = new TextArrayWritable(filesegmentList);
		DefaultStringifier.store(configuration, filesegment, "filesegment");

		job.setJarByClass(FillterData.class);
		job.setReducerClass(MultipleReducer.class);
		job.setNumReduceTasks(20);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.getConfiguration().set("fillerTargetKey", changeListToString(fillerTargetKey));

		job.getConfiguration().set("fillerTargetKeyInCheck", changeListToString(fillerTargetKeyInCheck));
		job.getConfiguration().set("Key", changeListToString(targetKey));
		job.getConfiguration().set("queryKey", changeListToString(queryKeyStrings));
		job.getConfiguration().set("queryValue", changeListToString(queryValueStrings));
		job.getConfiguration().set("querySymble", changeListToString(querySymble));
		job.getConfiguration().set("queryOperator", changeListToString(queryOperator));
		FileSystem fileSystem = FileSystem.get(URI.create("hdfs://Cloud0000:9000"), new Configuration());

		for (int i = 0; i < notfillterList.size(); i++) {
			// String pathString = inputFile + "/*/" + notfillterList.get(i) +
			// "-r-00000";
			String pathString = inputFile + "/3/" + notfillterList.get(i) + "*";
			FileStatus[] files = fileSystem.globStatus(new Path(pathString));
			if (files != null) {

				for (FileStatus file : files) {
					Path path = file.getPath();

					if (fileSystem.exists(path)) {
						// System.out.println("nnnnnnnnnnnnnn" + pathString);
						if (!pathString.contains("st")) {
							// System.out.println(path.toString());
							MultipleInputs.addInputPath(job, path, SequenceFileInputFormat.class,
									MultiMapForDimensionMappernofillter.class);

						} else {
							MultipleInputs.addInputPath(job, path, SequenceFileInputFormat.class,
									MultiMapForTravialMappernofillter.class);
						}
					}
				}
			}
		}
		for (int i = 0; i < fillterList.size(); i++) {
			// String pathString = inputFile+ "/*/" + fillterList.get(i) +

			// "-r-00000";
			String pathString = inputFile + "/3/" + fillterList.get(i) +

			"*";
			FileStatus[] files = fileSystem.globStatus(new Path(pathString));
			if (files != null) {
				for (FileStatus file : files) {

					Path path = file.getPath();
					if (fileSystem.exists(path)) {
						// System.out.println("ffffffffffffffff");
						// System.out.println(pathString);
						if (!pathString.contains("st")) {
							// System.out.println("ddddddddddd");
							MultipleInputs.addInputPath(job, path, SequenceFileInputFormat.class,
									MultiMapForDimensionMapperwithfillter.class);

						} else {
							MultipleInputs.addInputPath(job, path, SequenceFileInputFormat.class,
									MultiMapForTravialMapperwithfillter.class);
						}

					}
				}
			}
		}
		for (int i = 0; i < fillterListInCheck.size(); i++) {
			String fileString = fillterListInCheck.get(i).split("s")[0];
			List<String> filenum = new MatchFile().findSequencefilenum(queryKeyStrings);
			for (int j = 0; j < filenum.size(); j++) {
				String fileIndex = "";
				if (Integer.parseInt(filenum.get(j)) != -1) {
					fileIndex = "s" + filenum.get(j);
				} else {
					fileIndex = "st";
				}
				// String pathString = inputFile + "/*/" + fileString +
				// fileIndex + "-r-00000";
				String pathString = inputFile + "/3/" + fileString + fileIndex + "*";
				FileStatus[] files = fileSystem.globStatus(new Path(pathString));
				if (files != null) {
					for (FileStatus file : files) {

						Path path = file.getPath();
						if (fileSystem.exists(path)) {
							// System.out.println("ccccccccccccccc" +
							// pathString);
							if (!pathString.contains("st")) {
								MultipleInputs.addInputPath(job, path, SequenceFileInputFormat.class,
										FillterData.MultiMapForDimensionMapperwithfillterCheck.class);

							} else {

								MultipleInputs.addInputPath(job, path, SequenceFileInputFormat.class,
										FillterData.MultiMapForTrivalMapperwithfillterCheck.class);
							}

						}
					}
				}
			}
		}

		if (fileSystem.exists(new Path(outputFile))) {
			fileSystem.delete(new Path(outputFile));
		}
		FileOutputFormat.setOutputPath(job, new Path(outputFile));
		job.setOutputFormatClass(TextOutputFormat.class);
		job.submit();
		job.waitForCompletion(true);

	}

	

}

package MapReduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;

import Exception.NotPositiveException;

import System.SystemInfo;
import application.Haery;

public class FiltterDataForLIne {/*
	private static final List<String> dimensionNames = SystemInfo
			.getDemensionnames();
	private static String targetKeys = "";
	public static final Log LOG = LogFactory.getLog(NodeManager.class);
	public FiltterDataForLIne() {

	}

	public static class MultiMapForDimensionMapper extends
			Mapper<Text, Text, Text, Text> {

		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			String target = ""; String condition="";
			target=context.getConfiguration().get("Key");
			condition=context.getConfiguration().get("Condition");
			String[] targetKey;
			boolean findAll = false;
			
			if (target.contains("*")) {
				targetKey = new String[1];
				targetKey[0] = "*";
				findAll = true;
			} else {
				targetKey = target.split("\\|");
			}
			
				List<Integer> targetpositions = new ArrayList<Integer>();
				
				List<String> matchedTargetKey = new ArrayList<String>();
				for (int i = 0; i < dimensionNames.size(); i++) {
					if (findAll) {
						targetpositions.add(i);
						matchedTargetKey.add(dimensionNames.get(i));
					} else {
						for (int j = 0; j < targetKey.length; j++) {
							if (dimensionNames.get(i ).equals(
									targetKey[j].toString())) {
								targetpositions.add(i );
								matchedTargetKey.add(targetKey[j].toString());
							}
						}
					}
				}
				String[] values = value.toString().split(",");
				String outs = "";
				for (int i = 0; i < targetpositions.size(); i++) {
					outs += matchedTargetKey.get(i) + ":" + values[i] + "|";
				}

				// targetkey1:xxx|targetkey2:xxx|si|
				outs+=fileSeq+ "|";
				context.write(new Text(key), new Text(outs));

			
			
		}
	}

	public static class MultiMapForTravialMapper extends
			Mapper<Text, Text, Text, Text> {

		public void map(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			String target = "";
			target=context.getConfiguration().get("Key");
			String[] targetKey ;
			boolean findAll = false;
			
			if (target.contains("*")) {
				targetKey = new String[1];
				targetKey[0] = "*";
				findAll = true;
			} else {
				targetKey = target.split(",");
			}
	
			String filename = getFilePath(context.getInputSplit());
			String fileSeq=filename.split("s")[1].split("-")[0];
			if (!Character.isDigit(fileSeq.charAt(0))) {
				
				String[] values = value.toString().split(">,");
				String finalRes = "";
			
				
				for (int i = 0; i < values.length; i++) {
					if (findAll) {
						String middleRes = values[i].substring(1,
								values[i].length());
					String res = middleRes.replace(",", ":") + "|";
						finalRes += res;
					} else {
						for (int j = 0; j < targetKey.length; j++) {
							String keyvalue = values[i];
							if (keyvalue.contains(targetKey[j].toString())) {
								String middleRes = keyvalue.substring(1,
										keyvalue.length() - 1);
								String res = middleRes.replace(",", ":") + "|";
								finalRes += res;
							}
						}
					}
				}
				finalRes+=fileSeq+ "|";
				context.write(new Text(key), new Text(finalRes));
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
	public class TextContent implements Writable {
        private static final long serialVersionUID = 1L;
        private Text content;
        public TextContent(){
               this.content = new Text();
        }
        public Text getContent() {
               return content;
        }
        public void setContent(Text content) {
               this.content = content;
        }
      
        @Override
        public void readFields(DataInput in) throws IOException {
        	content.readFields(in);
               
        }
        @Override
        public void write(DataOutput out) throws IOException {
        	content.write(out);
        }
        
} 
	public static class MultipleReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context cxt)
				throws IOException, InterruptedException {
			//retailprice:901.00|type:PROMO BURNISHED COPPER|t|
			//retailprice:901.00|type:PROMO BURNISHED COPPER|i|
			String target = cxt.getConfiguration().get("Key");
			String[] targetKey;
			if (target.contains("*")) {
				targetKey = new String[1];
				targetKey[0] = "*";
			} else {
				targetKey = target.split(",");
			}

			String finalRes = "";
			List<String> splits=new ArrayList<String>();
			List<String> trivialdata=new ArrayList<String>();
			List<String> dimensiondata=new ArrayList<String>();
			for (Text text : values) {
						String[] withSymble=text.toString().split("\\|");
				for (int i = 0; i < withSymble.length-1; i++) {
					splits.add(withSymble[i]);
					if (Character.isDigit(withSymble[withSymble.length-1].charAt(0))) {
						dimensiondata.add(withSymble[i]);
					}
					else {
						trivialdata.add(withSymble[i]);
					}
				}
			}
			if (targetKey[0].equals("*")) {
				for (int i = 0; i < dimensionNames.size(); i++) {
					for (int j = 0; j < dimensiondata.size(); j++){
						if (dimensiondata.get(j).contains(dimensionNames.get(i))) {
							String[] res = dimensiondata.get(j).split(":");
							finalRes += res[1]+",";
						}
					}
				}
				Collections.sort(trivialdata);  
				for (int i = 0; i < trivialdata.size(); i++) {
					finalRes += trivialdata.get(i)+",";
				}
				
			}else {
				for (int i = 0; i < targetKey.length; i++) {
					for (int j = 0; j < splits.size(); j++) {
						if (splits.get(j).contains(targetKey[i].toString())) {
							String[] res = splits.get(j).split(":");
							finalRes += res[1]+",";
						}
					}
				}
			}
			cxt.write(new Text(""), new Text(finalRes));
		}
	}

	public static void main(String[] args) throws Exception {
		String SQL = "select * from a where size>20";

		List<String> mList = findPath(SQL);
		String condition=getQueryCondition(SQL);
		targetKeys=getTargetKey(SQL, targetKeys);
		System.out.println(targetKeys.length());
	
				
		Job job = Job.getInstance();
		job.setJarByClass(FiltterDataForLIne.class);
	job.setReducerClass(MultipleReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.getConfiguration().set("Key", targetKeys);
		job.getConfiguration().set("Condition", condition);

		FileSystem fileSystem = FileSystem.get(URI.create("hdfs://Cloud00:9000"),new Configuration());

		for (int i = 0; i < mList.size(); i++) {
			String pathString = args[0] + "/" + mList.get(i) + "-r-00000";
			Path path = new Path(pathString);

			if (fileSystem.exists(path)) {
				System.out.println("exists");
				if (!pathString.contains("st")) {
					MultipleInputs.addInputPath(job, path,
							SequenceFileInputFormat.class,
							MultiMapForDimensionMapper.class);

				} else {
					MultipleInputs.addInputPath(job, path,
							SequenceFileInputFormat.class,
							MultiMapForTravialMapper.class);
				}

			}
		}
		job.setOutputFormatClass(TextOutputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.submit();
		job.waitForCompletion(true);

	}

	private static String  getTargetKey(String sql, String targetKey) {
		if (!sql.contains("*")) {
			String s = sql.substring(sql.indexOf("select") + 6,
					sql.indexOf("from")).trim();
			String str[] = s.split(",");
			for (String st : str) {
				targetKey += st + "|";
			}
		} else {
			targetKey += "*";
		}
		return targetKey;
	}
	private static String  getQueryCondition(String sql) {
		String querys = sql.substring(sql.indexOf("where") + 5, sql.length())
				.trim();
		return querys;

	

	}

	private static List<String> findPath(String SQL)
			throws NotPositiveException {
		Haery haery = new Haery();
		String sqlString = SQL;

		List<String> mList = haery.query(sqlString);
		return mList;
	}*/

}

package Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParseSQL {


	public ParseSQL() {
		
	}

	public void parserSQL(String sql,List<String> targetKey, List<String> queryKeyStrings,
			List<String> queryValueStrings, List<String> querySymble,
			List<String> queryOperator) {
		// mapTable获得表名和别名的一个map

		// select name,officedesc from person where officeID=
		// select * from person
		if (!sql.contains("*")) {
			// System.out.println("1:"+sql.substring(sql.indexOf("from")+4,sql.indexOf("where")).trim());

			String s = sql.substring(sql.indexOf("select") + 6,
					sql.indexOf("from")).trim();
			String str[] = s.split(",");
			for (String st : str) {
				targetKey.add(st);

			}
		} else {
			targetKey.add("*");
		}
		String querys = sql.substring(sql.indexOf("where") + 5, sql.length())
				.trim();
		String[] args = querys.split(" ");
		for (int i = 0; i < args.length; i++) {
			String queryCondixtion = args[i];
			String[] resString = {};
			if (queryCondixtion.contains("=")) {
				if (queryCondixtion.contains(">")) {
					resString = queryCondixtion.split(">=");
					querySymble.add(">=");
				} else if (queryCondixtion.contains("<")) {
					resString = queryCondixtion.split("<=");
					querySymble.add("<=");
				} else {
					resString = queryCondixtion.split("=");
					querySymble.add("=");
				}
				queryKeyStrings.add(resString[0]);
				queryValueStrings.add(resString[1]);
			} else if (queryCondixtion.contains(">")) {
				if (!queryCondixtion.contains("=")) {
					resString = queryCondixtion.split(">");
					querySymble.add(">");
				}
				queryKeyStrings.add(resString[0]);
				queryValueStrings.add(resString[1]);
			} else if (queryCondixtion.contains("<")) {
				if (!queryCondixtion.contains("=")) {
					resString = queryCondixtion.split("<");
					querySymble.add("<");
				}
				queryKeyStrings.add(resString[0]);
				queryValueStrings.add(resString[1]);
			} else {
				queryOperator.add(queryCondixtion);
			}

		}

	}
	

	public static void main(String[] args) {
		List<String> targetKey = new ArrayList<String>();
		List<String> queryKeyStrings = new ArrayList<String>();
		List<String> queryValueStrings = new ArrayList<String>();
		List<String> querySymble = new ArrayList<String>();
		List<String> queryOperator = new ArrayList<String>();
		ParseSQL parseSQL = new ParseSQL();
		parseSQL.parserSQL("select name,officedesc from person where officeID=h and officename>j",targetKey, queryKeyStrings,
				queryValueStrings, querySymble, queryOperator);

		for (String string : querySymble) {
			System.out.println(string + " symble");
		}
		for (String string : targetKey) {
			System.out.println(string + " targetkey");
		}
		for (String string : queryKeyStrings) {
			System.out.println(string + " queryKeyStrings");
		}
		for (String string : queryValueStrings) {
			System.out.println(string + " queryValueStrings");
		}
		for (String string : queryOperator) {
			System.out.println(string + " queryOperator");
		}

	}

}

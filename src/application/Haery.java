package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.omg.CORBA.INTERNAL;

import Exception.NotPositiveException;
import Loading.DataLoading;
import MapReduce.CountKeys;
import MapReduce.CountKeysrange;
import MapReduce.FillterDataIntegrate;
import Query.Cell;
import Query.FillterCell;
import System.Dimension;
import System.SystemInfo;
import Tools.ParseSQL;

public class Haery {
	SystemInfo systemInfo = new SystemInfo();
	private List<Dimension> dimensions = systemInfo.getDimensions();
	private List<String> dimensionNames = systemInfo.getDemensionnames();

	public List<List<String>> queryCell(List<String> queryKeyStrings, List<String> queryValueStrings,
			List<String> querySymbles, List<String> queryOperator, List<String> targetKey) throws NotPositiveException {
		List<List<String>> cellid = new ArrayList<List<String>>();
		FillterCell queryCell = new FillterCell();
		List<String> queryDimension = new ArrayList<String>();
		List<String> queryvalue = new ArrayList<String>();
		List<Integer> dimensionIndex = new ArrayList<Integer>();
		for (int i = 0; i < queryKeyStrings.size(); i++) {
			for (int j = 0; j < dimensions.size(); j++) {
				if (queryKeyStrings.get(i).equals(dimensionNames.get(j))) {
					queryDimension.add(queryKeyStrings.get(i));
					queryvalue.add(queryValueStrings.get(i));
					dimensionIndex.add(j);
					break;
				}
			}
		}
		if (queryDimension.size() != 0) {
			cellid = queryCell.findCellByDimensionValue(queryDimension, dimensionIndex, queryvalue, querySymbles,
					queryOperator, targetKey,queryKeyStrings);
		}
		return cellid;
	}

	public static void main(String[] args) throws Exception {

		Scanner scanner = new Scanner(System.in);
		String choice = "";
		do {

			System.out.println("input 1 to count key");
			System.out.println("input 2 to get the range of key");
			System.out.println("input 3 to initial");
			System.out.println("input 4 to loading data");
			System.out.println("input 5 to query");
			System.out.println("input 6 to expanded segment");
			System.out.println("input 7 to expanded dimension");
			System.out.println("input 8 to quit haery");
			choice = scanner.nextLine();
			if (choice.equals("1")) {
				CountKeys countKeys = new CountKeys();
				countKeys.count();
			}
			if (choice.equals("2")) {
				CountKeysrange countKeysrange = new CountKeysrange();
				countKeysrange.countRange();
			}

			if (choice.equals("3")) {
				InitialSystem initialSystem = new InitialSystem();
				initialSystem.InitialSystemWithInput();
			}
			if (choice.equals("4")) {
		
				System.out.println("please the location where the file  is loaded:");
				String filepath = scanner.nextLine();
				DataLoading dataLoading = new DataLoading();
				long startTime = System.currentTimeMillis();
				dataLoading.loading(filepath);
				long endTime = System.currentTimeMillis();

				System.out.println("Spending: " + (endTime - startTime));
				System.out.println("loading done");
			}
			if (choice.equals("5")) {
		

				System.out.println("input query language: ");
				System.out.println("such as : select name,size from a where size>40 and name>=w");
				String SQL = scanner.nextLine();
				FillterDataIntegrate fillterDataIntegrate = new FillterDataIntegrate();
				long startTime = System.currentTimeMillis();
				fillterDataIntegrate.query(SQL);
				long endTime = System.currentTimeMillis();

				System.out.println("Spending: " + (endTime - startTime));
				System.out.println("query done");
			}
			if (choice.equals("6")) {
				SegmentExpansion segmentExpansion = new SegmentExpansion();
				segmentExpansion.expand();
				System.out.println("expand segment done");
			}

			if (choice.equals("7")) {
				DimensionExpansion dimensionExpansion = new DimensionExpansion();
				dimensionExpansion.expand();
				System.out.println("expand dimension done");
			}
			if (choice.equals("8")) {
                   System.out.println("quit");
				break;
			}
		} while (choice.equals("9"));

		scanner.close();

	}

}

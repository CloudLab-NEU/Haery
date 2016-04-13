package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.naming.InitialContext;

import DB.DB;
import MapReduce.CountKeys;
import System.Dimension;
import System.Segment;

public class InitialSystem {

	public void Initial() throws SQLException {
		DB db = new DB();
		db.initialDatabase();
		String[] oriThreadStrings = { "a", "K" };
		List<Segment> segments = new ArrayList<Segment>();
		Segment s11 = new Segment(1, 1);
		Segment s12 = new Segment(2, 1);
		Segment s13 = new Segment(3, 1);
		Segment s14 = new Segment(4, 1);
		segments.add(s11);
		segments.add(s12);
		segments.add(s13);
		segments.add(s14);
		Dimension d1 = new Dimension(1, 1, 1, segments, "name", oriThreadStrings);
		db.insertSegment(s11, d1.getDimensionIndex(), 1, 2);
		db.insertSegment(s12, d1.getDimensionIndex(), 1, 3);
		db.insertSegment(s13, d1.getDimensionIndex(), 1, 4);
		db.insertSegment(s14, d1.getDimensionIndex(), 1, 0);
		String[] oriThreadStrings2 = { "1", "50" };
		Segment s21 = new Segment(1, 1);
		Segment s22 = new Segment(2, 1);
		Segment s23 = new Segment(3, 1);
		Segment s24 = new Segment(4, 1);
		List<Segment> segments2 = new ArrayList<Segment>();
		segments2.add(s21);
		segments2.add(s22);
		segments2.add(s23);
		segments2.add(s24);
		Dimension d2 = new Dimension(2, 1, 1, segments2, "size", oriThreadStrings2);
		db.insertSegment(s21, d2.getDimensionIndex(), 1, 2);
		db.insertSegment(s22, d2.getDimensionIndex(), 1, 3);
		db.insertSegment(s23, d2.getDimensionIndex(), 1, 4);
		db.insertSegment(s24, d2.getDimensionIndex(), 1, 0);
		String[] oriThreadStrings3 = { "0", "1000" };
		Segment s31 = new Segment(1, 1);
		Segment s32 = new Segment(2, 1);
		Segment s33 = new Segment(3, 1);
		Segment s34 = new Segment(4, 1);
		List<Segment> segments3 = new ArrayList<Segment>();
		segments3.add(s31);
		segments3.add(s32);
		segments3.add(s33);
		segments3.add(s34);
		Dimension d3 = new Dimension(3, 1, 1, segments3, "retailprice", oriThreadStrings3);
		db.insertSegment(s31, d3.getDimensionIndex(), 1, 2);
		db.insertSegment(s32, d3.getDimensionIndex(), 1, 3);
		db.insertSegment(s33, d3.getDimensionIndex(), 1, 4);
		db.insertSegment(s34, d3.getDimensionIndex(), 1, 0);
		String[] oriThreadStrings4 = { "b", "x" };
		Segment s41 = new Segment(1, 1);
		Segment s42 = new Segment(2, 1);
		Segment s43 = new Segment(3, 1);
		Segment s44 = new Segment(4, 1);
		List<Segment> segments4 = new ArrayList<Segment>();
		segments4.add(s41);
		segments4.add(s42);
		segments4.add(s43);
		segments4.add(s44);
		Dimension d4 = new Dimension(4, 1, 1, segments4, "type", oriThreadStrings4);
		db.insertSegment(s41, d4.getDimensionIndex(), 1, 2);
		db.insertSegment(s42, d4.getDimensionIndex(), 1, 3);
		db.insertSegment(s43, d4.getDimensionIndex(), 1, 4);
		db.insertSegment(s44, d4.getDimensionIndex(), 1, 0);
		// db.insertDimension(d1);
		// db.insertDimension(d2);
		// db.insertDimension(d3);
		// db.insertDimension(d4);
		db.deconnSQL();
	}

	public void InitialSystemWithInput() throws SQLException {
		DB db = new DB();
		db.initialDatabase();
		Scanner scanner = new Scanner(System.in);

		String isCreate = "c";
		int dimenionnum = 1;
		while (isCreate.equals("c")) {
			System.out.println("input c to create dimension:");
			System.out.println("input q to break");

			isCreate = scanner.nextLine();
		if (isCreate.equals("c")) {
		

				System.out.println("please input the dimension name : ");
				String dimensionName = scanner.nextLine();
				System.out.println("and the range of this dimension: ");
				System.out.println("such as a|k of 2|100");
				String[] oriThreadStrings = scanner.nextLine().split("\\|");

				List<Segment> segments = new ArrayList<Segment>();
				Segment s11 = new Segment(1, 1);
				Segment s12 = new Segment(2, 1);
				Segment s13 = new Segment(3, 1);
				Segment s14 = new Segment(4, 1);
				segments.add(s11);
				segments.add(s12);
				segments.add(s13);
				segments.add(s14);
				Dimension d1 = new Dimension(dimenionnum, 1, 1, segments, dimensionName, oriThreadStrings);
				db.insertSegment(s11, d1.getDimensionIndex(), 1, 2);
				db.insertSegment(s12, d1.getDimensionIndex(), 1, 3);
				db.insertSegment(s13, d1.getDimensionIndex(), 1, 4);
				db.insertSegment(s14, d1.getDimensionIndex(), 1, 0);
				db.insertDimension(d1);
				dimenionnum++;
				System.out.println("create successful");
		}
		else if (isCreate.equals("c")) {
			
		}
		else {
			System.out.println("the input os wrong!");
			break;
		}
				
			

		}

		scanner.close();
		db.deconnSQL();

	}

	public static void main(String[] args) {
		//InitialSystem initialDimension = new InitialSystem();

	}

}

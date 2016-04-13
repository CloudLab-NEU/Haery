package DB;

import System.Dimension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jasper.tagplugins.jstl.core.If;

import System.Segment;

import com.sun.java_cup.internal.runtime.virtual_parse_stack;

public class DB {
	
	    private  Connection conn;  
	    public DB() {
			// TODO Auto-generated constructor stub
	    	conn=connSQL();
	    	try {
				create_database();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    public  DB(boolean renew) {
	    	conn=connSQL();
		}
	    public void initialDatabase() throws SQLException {
	    	createTable_dimension();
	    	createTable_segment();
		}
	  
	    // connect to MySQL  
	   private   Connection connSQL() {  
	        String urle = "jdbc:mysql://localhost:3306?useUNicode=true&characterEncoding=utf-8&useSSL=false";//port：3306 database:testdb  
	        String username = "root";//user  
	        String password = "123456";//password  
	        try {   
	            Class.forName("com.mysql.jdbc.Driver" );//加载驱动，连接数据库  
	          //  System.err.println(DriverManager.getConnection(urle,username, password ));
	    
	           return DriverManager.getConnection(urle,username, password );   
	            }  
	        //捕获加载驱动程序异常  
	         catch ( ClassNotFoundException cnfex ) {  
	             System.err.println(  
	             "装载 JDBC/ODBC 驱动程序失败。" );  
	             cnfex.printStackTrace();   
	             return null;
	         }   
	         //捕获连接数据库异常  
	         catch ( SQLException sqlex ) {  
	             System.err.println( "无法连接数据库" );  
	             sqlex.printStackTrace();  
	             return null;
	         }  
	    }  
	   private void create_database() throws SQLException {
		String sql="create database if not exists haeryMetaData";
		PreparedStatement pstmt = conn.prepareStatement(sql);
    	pstmt.executeUpdate(); 
    	sql="use haeryMetaData";
    	pstmt = conn.prepareStatement(sql);
    	pstmt.executeUpdate(); 
    	
	}
	   
	   private void renew_database() throws SQLException {
			String sql="drop database if  exists haeryMetaData";
			PreparedStatement pstmt = conn.prepareStatement(sql);
	    	pstmt.executeUpdate(); 
	    	sql="create database haeryMetaData";
	    	pstmt = conn.prepareStatement(sql);
	    	pstmt.executeUpdate(); 
	    	sql="use haeryMetaData";
	    	pstmt = conn.prepareStatement(sql);
	    	pstmt.executeUpdate(); 
	    	
		}
	  
	    // disconnect to MySQL  
	    public void deconnSQL() {  
	        try {  
	            if (conn != null)  
	                conn.close();  
	        } catch (Exception e) {  
	            System.out.println("关闭数据库问题 ：");  
	            e.printStackTrace();  
	        }  
	    }  
	
		
	   private void createTable_dimension() throws SQLException {
	    	String sql = "CREATE TABLE if not exists Dimension (id int not null AUTO_INCREMENT, dimensionIndex int not null,demensionName varchar(300) not null, "
	    			+ "startThreadForDimension varchar(200) not null,endThreadForDimension varchar(200) not null,"
	    			+ "ratios int null, dimensionVersion int null,"
	    			+ "primary key (id));";
	    	PreparedStatement pstmt = conn.prepareStatement(sql);
	    	pstmt.executeUpdate(); 
		}
	   
		
	   private void renewTable_dimension() throws SQLException {
		   String sql="drop table if exists Dimension;";
	    	 sql = "CREATE TABLE  Dimension (id int not null AUTO_INCREMENT, dimensionIndex int not null,demensionName varchar(300) not null, "
	    			+ "startThreadForDimension varchar(200) not null,endThreadForDimension varchar(200) not null,"
	    			+ "ratios int null, dimensionVersion int null,"
	    			+ "primary key (id));";
	    	PreparedStatement pstmt = conn.prepareStatement(sql);
	    	pstmt.executeUpdate(); 
		}
	   
	    private void renewTable_segment() throws SQLException {
	    	String sql = "CREATE TABLE if not exists Segment (id int not null AUTO_INCREMENT, segmentIndex int not null,"
	    			+ "segmentVersion int null, dimensionIndex int not null, isRoot int null, rightSegment int null,"
	                  +"primary key(id));";
	    			
	    	PreparedStatement pstmt = conn.prepareStatement(sql);
	    	pstmt.executeUpdate(); 
		}
	    
	    private void createTable_segment() throws SQLException {
	    	String sql = "CREATE TABLE if not exists Segment (id int not null AUTO_INCREMENT, segmentIndex int not null,"
	    			+ "segmentVersion int null, dimensionIndex int not null, isRoot int null, rightSegment int null,"
	                  +"primary key(id));";
	    			
	    	PreparedStatement pstmt = conn.prepareStatement(sql);
	    	pstmt.executeUpdate(); 
		}
	
	    // execute selection language  
	  public List<Dimension> selectDimension() {  
		  ResultSet rSet=null;
		  List<Dimension> dimensions=new ArrayList<Dimension>();
		  try {  
	            String insert = "select * from Dimension order by dimensionIndex;";
	          
	           PreparedStatement   statement = conn.prepareStatement(insert);  
	    
	           rSet= statement.executeQuery(); 
	            while (rSet.next()) {
					Dimension dimension=new Dimension();
					dimension.setDimensionIndex(Integer.parseInt(rSet.getString("dimensionIndex")));
					dimension.setDemensionName(rSet.getString("demensionName"));
					dimension.setDimensionVersion(Integer.parseInt(rSet.getString("dimensionVersion")));
					String[] OriginalThreadForDimension={rSet.getString("startThreadForDimension"),rSet.getString("endThreadForDimension")};
					dimension.setOriginalThreadForDimension(OriginalThreadForDimension);
					dimension.setRatios(Integer.parseInt(rSet.getString("ratios")));
					dimensions.add(dimension);
				}

	        
	            return dimensions;
	        } catch (SQLException e) {  
	           e.printStackTrace();  
	        }  
	        return null;  
	   }
	  public Dimension selectDimensionByIndex(int dimensionIndex) {  
		  ResultSet rSet=null;
		  try {  
	            String insert = "select * from Dimension where dimensionIndex= ?;";
	            PreparedStatement  statement = conn.prepareStatement(insert);  
	            statement.setString(1, dimensionIndex+"");
	            rSet= statement.executeQuery(); 
	            Dimension dimension=new Dimension();
	            while (rSet.next()) {
					
					dimension.setDimensionIndex(Integer.parseInt(rSet.getString("dimensionIndex")));
					dimension.setDemensionName(rSet.getString("demensionName"));
					dimension.setDimensionVersion(Integer.parseInt(rSet.getString("dimensionVersion")));
					String[] OriginalThreadForDimension={rSet.getString("startThreadForDimension"),rSet.getString("endThreadForDimension")};
					dimension.setOriginalThreadForDimension(OriginalThreadForDimension);
					dimension.setRatios(Integer.parseInt(rSet.getString("ratios")));
		
				}
	            return dimension;
	        } catch (SQLException e) {  
	           e.printStackTrace();  
	        }  
	        return null;  
	   }
	  public List<Segment> selectAlltheSegmentsOnDimension(int dimentionIndex) {  
		  ResultSet rSet=null;
		  List<Segment> segments=new ArrayList<Segment>();
		  try {  
	            String insert = "select * from Segment where dimensionIndex= ? ;";  
	            PreparedStatement  statement = conn.prepareStatement(insert);  
	            statement.setString(1, dimentionIndex+"");
	            rSet= statement.executeQuery();  
	            while (rSet.next()) {
					Segment segment=new Segment();
					segment.setSegmentIndex(Integer.parseInt(rSet.getString("segmentIndex")));
					segment.setSegmentVersion(Integer.parseInt(rSet.getString("segmentVersion")));
					segments.add(segment);
				}
	            return segments;
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	   }
	  public int selectMaxmalSegmentVersion() {  
		  ResultSet rSet=null;
		  int res=0;
		  try {  
	            String insert = "select MAX(segmentVersion) from Segment ;";  
	            PreparedStatement  statement = conn.prepareStatement(insert);  
	            rSet= statement.executeQuery();  
	            while (rSet.next()) {
					res=rSet.getInt(1);
				}
	            return res;
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }  
	        return -1;  
	   }
	  public List<Segment> selectLeaveSegmentsOnDimension(int dimentionIndex) {  
		  ResultSet rSet=null;
			List<Segment> res=new ArrayList<Segment>();
			List<Segment> segments=new ArrayList<Segment>();
			List<Integer> rights=new ArrayList<Integer>();
		  try {  
	            String insert = "select * from Segment where dimensionIndex=? and isRoot= ?;";  
	            PreparedStatement    statement = conn.prepareStatement(insert);  
	            statement.setString(1, dimentionIndex+"");
	            statement.setString(2, 1+"");
	            rSet= statement.executeQuery();  
	            while (rSet.next()) {
					Segment segment=new Segment();
					segment.setSegmentIndex(Integer.parseInt(rSet.getString("segmentIndex")));
					segment.setSegmentVersion(Integer.parseInt(rSet.getString("segmentVersion")));
				    rights.add(Integer.parseInt(rSet.getString("rightSegment")));
				    segments.add(segment);
				}
	            int length=rights.size();
	            for (int i = 0; i <length; i++) {
	    			res.add(new Segment());
	    		}
	            res.set(length-1, segments.get(rights.indexOf(0)));
	            for (int i = length-2; i >=0; i--) {
	            	Segment segment=segments.get(rights.indexOf(res.get(i+1).getSegmentIndex()));
					res.set(i, segment);
				}
	            return res;
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	   }
	  
	  public boolean insertDimension(Dimension dimension){ 
	        try {  
	            String insert = "insert into Dimension(dimensionIndex,demensionName,startThreadForDimension,"
	            		+ "endThreadForDimension,ratios,dimensionVersion) values(? , ? , ? , ? , ? , ?);";  
	            PreparedStatement    statement = conn.prepareStatement(insert);  
	            String[] threadString= dimension.getOriginalThreadForDimension();
	            statement.setString(1, dimension.getDimensionIndex()+"");
	            statement.setString(2, dimension.getDemensionName());
	            statement.setString(3,threadString[0]);
	            statement.setString(4, threadString[1]);
	            statement.setString(5, dimension.getRatios()+"");
	            statement.setString(6, dimension.getDimensionVersion()+"");
	            statement.executeUpdate();
	            return true;
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }  
	        return false;  
	    }  
	  public boolean updateFatherSegment(int dimensionIndex,int segmentIndex) {
	    	String sql = "update Segment set isRoot=?where dimensionIndex=? and segmentIndex=?;";
	    	PreparedStatement pstmt;
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, 0+"");
				pstmt.setString(2,dimensionIndex+"");
				pstmt.setString(3, segmentIndex+"");
				pstmt.executeUpdate(); 
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return false;
		}
	 /* public Segment getLeftSegment(Dimension dimention,int rightSegmentIndex) {
		  ResultSet rSet=null;
		   String insert = "select * from Segment where dimensionIndex=? and rightSegment= ?;";  
           try {
			statement = conn.prepareStatement(insert);
			 statement.setString(1, dimention.getDimensionIndex()+"");
	           statement.setString(2, rightSegmentIndex+"");
	           rSet= statement.executeQuery(insert);  
	           while (rSet.next()) {
					Segment segment=new Segment();
					segment.setSegmentIndex(Integer.parseInt(rSet.getString("segmentIndex")));
					segment.setSegmentVersion(Integer.parseInt(rSet.getString("segmentVersion")));
		            return segment;
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
          
		return null;
	}*/
	  public boolean updateRightSegment(int dimensionINdex,Segment segment, int rightSegment) {
	    	String sql = "update Segment set rightSegment=? where dimensionIndex=? and segmentIndex=?;";
	    	PreparedStatement pstmt;
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, rightSegment+"");
				pstmt.setString(2, dimensionINdex+"");
				pstmt.setString(3, segment.getSegmentIndex()+"");
				pstmt.executeUpdate(); 
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return false;
		}
	    // execute insertion language  
	  public boolean insertSegment(Segment segment,int dimensionIndex,int isRoot,int rightSegment) {  
	        try {  
	        	 String insert = "insert into Segment(segmentIndex,segmentVersion,dimensionIndex,isRoot,rightSegment) values(?,?,?,?,?);";
	        	 PreparedStatement   statement = conn.prepareStatement(insert);  
	            statement.setString(1, segment.getSegmentIndex()+"");
	            statement.setString(2, segment.getSegmentVersion()+"");
	            statement.setString(3,dimensionIndex+"");
	            statement.setString(4, isRoot+"");
	            statement.setString(5, rightSegment+"");
	            statement.executeUpdate();  
	            return true;  
	        } catch (SQLException e) {  
	            System.out.println("插入数据库时出错：");  
	            e.printStackTrace();  
	        } catch (Exception e) {  
	            System.out.println("插入时出错：");  
	            e.printStackTrace();  
	        }  
	        return false;  
	    }  
	
	
	    // show data in ju_users  
	  public void layoutStyle2(ResultSet rs) {  
	        System.out.println("-----------------");  
	        System.out.println("执行结果如下所示:");  
	        System.out.println("-----------------");  
	        System.out.println(" id" + "\t\t" + "name" +"\t\t" + "age" + "\t\t" +"work"+ "\t\t" + "others");  
	        System.out.println("-----------------");  
	        try {  
	            while (rs.next()) {  
	                System.out.println(rs.getInt("_id") + "\t\t"  
	                        + rs.getString("name") + "\t\t"  
	                        +rs.getInt("age") + "\t\t"  
	                        + rs.getString("work")+ "\t\t"  
	                        + rs.getString("others"));  
	            }  
	        } catch (SQLException e) {  
	            System.out.println("显示时数据库出错。");  
	            e.printStackTrace();  
	        } catch (Exception e) {  
	            System.out.println("显示出错。");  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    public static void main(String args[]) throws SQLException {  
	  
	        DB h = new DB();  
	        h.connSQL();  
	     
	     h.createTable_dimension();
	   h.createTable_segment();
	        h.deconnSQL();  
	    } 
 

}

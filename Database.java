import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class Database {
	// JDBC driver name and database URL
	   private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   private static final String DB_URL = "jdbc:mysql://localhost:3306/";

	   //  Database credentials
	   private static final String USER = "root";
	   private static final String PASS = "";
	   private static String BBDD_NAME = "";
	   private StringBuilder statement;
	   private int id = 1;
	   private String sql;
	   private static Statement stmt = null;
	   private Connection conn = null;
	   
	   public Database(String name) {
	   
	   try{
	      //STEP 2: Register JDBC driver
	      Class.forName("com.mysql.jdbc.Driver");

	      //STEP 3: Open a connection
	      //System.out.println("Connecting to database...");
	      BBDD_NAME = name;
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);
	      statement = new StringBuilder();
	      //STEP 4: Execute a query
	      //System.out.println("Creating database...");
	      stmt = conn.createStatement();
	      
	      sql = "CREATE DATABASE IF NOT EXISTS " + BBDD_NAME.toUpperCase();
	      stmt.executeUpdate(sql);
	      //System.out.println("Database created successfully...");
	      conn = DriverManager.getConnection(DB_URL + BBDD_NAME, USER, PASS);
	      stmt = conn.createStatement();
	   }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }
	   //System.out.println("Goodbye!");
	}//end main
	   
	   public void updateTable(String table_name, ArrayList<?> values){
		   //System.out.println(values.size());
		   String time = new Date(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis()).toString() + " " +
				   	     new Time(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis()).toString();
		   for(int i = 0; i < values.size(); i++){
		    	if(i != values.size()-1)statement.append("'" + values.get(i) + "', ");
		    	else statement.append("'" + values.get(i) + "'");
		     }
		   sql = "INSERT INTO " + table_name + 
                 " VALUES (" + id + ", " + "'" + time + "'" + ", " + statement.toString() + ")";
      try {
    	  //System.out.println(sql);
		stmt.executeUpdate(sql);
		id++;
	} catch (SQLException e) {
		try {
			
			ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS maximo FROM " + table_name);
			if(rs.next()){
				id += rs.getInt("maximo");
			}
			 sql = "INSERT INTO " + table_name + 
				   " VALUES (" + id + ", " + "'" + time + "'" + ", " + statement.toString() + ")";
			 System.out.println(sql);
			stmt.executeUpdate(sql);
			id++;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	   }
	   public void createTable(String table_name, int index, int[] config, String[] args){
		      for(int i = 0; i < index; i++){
			    	 for(int j = 0; j < config[i]; j++){
			    			statement.append(args[i].substring(0, 1)+ j + " VARCHAR(255), ");
			    	 }
			     }
		      sql = "CREATE TABLE IF NOT EXISTS " + table_name +
	                  "(id INTEGER not NULL, hora DATETIME, " +
	                  statement.toString() +
	                  " PRIMARY KEY (id))";
		      try {
		    	 // System.out.println(sql);
		  		stmt.executeUpdate(sql);
		  		 System.out.println("Table created successfully...");
		  	} catch (SQLException e) {
		  		e.printStackTrace();
		  	}
	   }
	   public String getTables() throws SQLException{
		   String tables = "";
		   ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME AS names "
				   						  + "FROM INFORMATION_SCHEMA.TABLES "
				   						  +	"WHERE TABLE_TYPE =  'BASE TABLE' "
				   						  +	"AND TABLE_SCHEMA =  'parametros'");
		   while(rs.next()){
		    	tables += rs.getString("names") + "\n";
		     }
		   return tables;
	   }
	   public String getValues(String table_name){
		   String values = "";
		   ResultSet rs;
		try {
			rs = stmt.executeQuery("SELECT COLUMN_NAME AS valores "
					   						+	"FROM INFORMATION_SCHEMA.COLUMNS "
					   						+	"WHERE DATA_TYPE =  'VARCHAR' "
					   						+	"AND TABLE_NAME =  '" + table_name + "'");
			   
			   while(rs.next()){
			    	values += rs.getString("valores") + "\n";
			     }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		   return values;
	   }
	   public int getRowsCount(String table_name){
			ResultSet rs;
			try {
				rs = stmt.executeQuery("SELECT COUNT(id) AS count FROM " + table_name);
				if(rs.next())id += rs.getInt("count");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   return id;
	   }
	   
}
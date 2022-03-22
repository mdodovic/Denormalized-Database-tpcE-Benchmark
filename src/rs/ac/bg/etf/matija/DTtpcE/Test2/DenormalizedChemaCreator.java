package rs.ac.bg.etf.matija.DTtpcE.Test2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;

import rs.ac.bg.etf.matija.DTtpcE.Test2.MainDTtpcE;


public class DenormalizedChemaCreator {

	
	public static void createDenormalizedDatabaseChema(Connection conn) {
		
		try {
			dropDenormalizedDatabaseChema(conn);

			String createChemaQuery = "";
			Collections.reverse(Arrays.asList(MainDTtpcE.denormalizedTableNames));
			for(String tableName: MainDTtpcE.denormalizedTableNames) {
				
				createChemaQuery += createTableQuerry(tableName) + "\r\n";
				
			}
						
			Statement stmt;
			stmt = conn.createStatement();
			stmt.executeUpdate(createChemaQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(String tableName: MainDTtpcE.denormalizedTableNames) {

			System.out.println("Table " + tableName + " successfully created");
			
		} 
		System.out.println("------------------------------------------------------------");

	}
	
	public static void dropDenormalizedDatabaseChema(Connection conn) throws SQLException  {
		
		String dropQueryPattern = "IF EXISTS ( SELECT * FROM tpcE.INFORMATION_SCHEMA.TABLES where TABLE_NAME = '###' AND TABLE_SCHEMA = 'dbo')\r\n" + 
				"DROP TABLE tpcE.dbo.###;";

		String dropQuery;
		Statement stmt;
		
		for(String tableName: MainDTtpcE.denormalizedTableNames) {
			dropQuery = dropQueryPattern.replace("###",  tableName);
		
			stmt = conn.createStatement();
			stmt.executeUpdate(dropQuery);

			System.out.println("Table: " + tableName + " successfully deleted");

		}
		System.out.println("------------------------------------------------------------");
	}

	private static String createTableQuerry(String tableName) {
		
		switch(tableName) {
			case "DTT2T3T8F2": return "CREATE TABLE tpcE.dbo.[DTT2T3T8F2] (\r\n" + 
					"	[DT_CA_ID] bigint Not Null,\r\n" + 
					"	[DT_HS_S_SYMB] CHAR(15) Not Null,\r\n" + 
					"	[DT_HS_QTY] int Not Null,\r\n" + 
					"	[DT_CA_C_ID] bigint Not Null,\r\n" + 
					"	[DT_LT_PRICE] decimal(10,2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE tpcE.dbo.[DTT2T3T8F2] ADD CONSTRAINT DTT2T3T8F2_PK PRIMARY KEY (DT_CA_ID, DT_HS_S_SYMB);"; 

			case "DTT8F6": return "CREATE TABLE tpcE.dbo.[DTT8F6] (\r\n" + 
				"	[DT_CA_ID] bigint Not Null,\r\n" + 
				"	[DT_CA_BAL] decimal(12,2) Not Null,\r\n" + 
				");\r\n" + 
				"ALTER TABLE tpcE.dbo.[DTT8F6] ADD CONSTRAINT DTT8F6_PK PRIMARY KEY (DT_CA_ID);"; 
			
			default: return null;
		}
	}

	public static void createIndexes(Connection connection) {

		String indexName;
		String tableName;
		String createIndex;
		String columnName;
		String createIndexPattern;
		Statement stmt;

		
		columnName = "[DT_CA_C_ID]";
		indexName = "[DTT2T3T8F2_Index_DT_CA_C_ID]";
		tableName = "[DTT2T3T8F2]";
		createIndexPattern = "drop index if exists #2# on [tpcE].[dbo].#1#;\r\n" + 
				"		create nonclustered index #2# on [tpcE].[dbo].#1# (#3#);";
		
		createIndex = createIndexPattern.replace("#1#",  tableName);
		createIndex = createIndex.replace("#2#",  indexName);
		createIndex = createIndex.replace("#3#",  columnName);
		
		try {
			stmt = connection.createStatement();

			stmt.execute(createIndex);

			System.out.println("Index: " + indexName + " successfully created");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		columnName = "[DT_HS_S_SYMB], [DT_CA_ID]";
		indexName = "[DTT2T3T8F2_Index_DT_HS_S_SYMB__DT_CA_ID]";
		tableName = "[DTT2T3T8F2]";
		createIndexPattern = "drop index if exists #2# on [tpcE].[dbo].#1#;\r\n" + 
				"		create nonclustered index #2# on [tpcE].[dbo].#1# (#3#);";
		
		createIndex = createIndexPattern.replace("#1#",  tableName);
		createIndex = createIndex.replace("#2#",  indexName);
		createIndex = createIndex.replace("#3#",  columnName);

		try {
			stmt = connection.createStatement();

			stmt.execute(createIndex);

			System.out.println("Index: " + indexName + " successfully created");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		


		
	}

	
}

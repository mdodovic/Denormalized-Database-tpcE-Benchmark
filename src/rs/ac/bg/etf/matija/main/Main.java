package rs.ac.bg.etf.matija.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.matija.DTtpcE.Full.FullDenormalizedChemaCreator;
import rs.ac.bg.etf.matija.DTtpcE.Full.FullDenormalizedChemaLoader;
import rs.ac.bg.etf.matija.DTtpcE.Full.MainFullDTtpcE;
import rs.ac.bg.etf.matija.DTtpcE.Partial.MainPartialDTtpcE;
import rs.ac.bg.etf.matija.DTtpcE.Partial.PartialDenormalizedChemaCreator;
import rs.ac.bg.etf.matija.DTtpcE.Partial.PartialDenormalizedChemaLoader;
import rs.ac.bg.etf.matija.MWtpcE.IndexedViewsCreator;
import rs.ac.bg.etf.matija.MWtpcE.MainMWtpcE;
import rs.ac.bg.etf.matija.NTtpcE.MainNTtpcE;
import rs.ac.bg.etf.matija.NTtpcE.NormalizedChemaCreator;
import rs.ac.bg.etf.matija.NTtpcE.NormalizedChemaLoader;

public class Main {

	public static final String USER = "sa";
	public static final String PASSWORD = "matija";

	private Connection connection;

	public static final String pathToResultFolderNormalized = "./src/time_results/normalized/";
	public static final String pathToResultFolderIndexes = "./src/time_results/indexes/";
	public static final String pathToResultFolderFullDenormalized = "./src/time_results/full_denormalized/";
	public static final String pathToResultFolderPartialDenormalized = "./src/time_results/partial_denormalized/";

	public static List<String> inputDataFileList = new ArrayList<String>();
	public static List<String> outputResultFileList = new ArrayList<String>();
	
	static {
		inputDataFileList.add("inputData/T2T3T8_T2F1_read_130k.sql");
		inputDataFileList.add("inputData/T2T3T8_T3F1_write_130k.sql");
		inputDataFileList.add("inputData/T2T3T8_T8F2_write_130k.sql");
		inputDataFileList.add("inputData/T2T3T8_T8F6_write_130k.sql");
		inputDataFileList.add("inputData/T2T3T8_T3T8_all_write_130k.sql");

		outputResultFileList.add("T2F1_read_130k");
		outputResultFileList.add("T3F1_write_130k");
		outputResultFileList.add("T8F2_write_130k");
		outputResultFileList.add("T8F6_write_130k");
		outputResultFileList.add("T3T8_all_write_130k");

	}
	
	public static String inputDataFile;
	public static String outputResultFile;
	
//	public static final String inputDataFile = "inputData/T2T3T8_T2F1_read_130k.sql";
//	public static final String outputResultFile = "T2F1_read_130k";
	
//	public static final String inputDataFile = "inputData/T2T3T8_T3F1_write_130k.sql";
//	public static final String outputResultFile = "T3F1_write_130k"; 		
	
	//public static final String inputDataFile = "inputData/T2T3T8_T8F2_write_130k.sql";
	//public static final String outputResultFile = "T8F2_write_130k";
	
	//public static final String inputDataFile = "inputData/T2T3T8_T8F6_write_130k.sql";
	//public static final String outputResultFile = "T8F6_write_130k";
	
	//public static final String inputDataFile = "inputData/T2T3T8_T3T8_all_write_130k.sql";
	//public static final String outputResultFile = "T3T8_all_write_130k";
	
	public Connection getConnection() {
		return connection;
	}
	
	public void connectToMSSQL() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		
			String x = "jdbc:sqlserver://localhost:1433;databaseName=tpcE;";
	
			connection = DriverManager.getConnection(x, USER, PASSWORD);

			System.out.println("Connected to MS SQL Server");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void disconnectFromMSSQL() {
		try {
			
			if(connection != null) {
				connection.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void dropSchema() {
		Main database = new Main();		
		database.connectToMSSQL();
		
		//Drop whole database schematic
		
		try {
			IndexedViewsCreator.dropIndexes(database.getConnection());

			NormalizedChemaCreator.dropNormalizedDatabaseForeignKeysConstraints(database.getConnection());

			NormalizedChemaCreator.dropNormalizedDatabaseChema(database.getConnection());

			FullDenormalizedChemaCreator.dropDenormalizedDatabaseForeignKeysConstraints(database.getConnection());

			FullDenormalizedChemaCreator.dropDenormalizedDatabaseChema(database.getConnection());

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			database.disconnectFromMSSQL();
		}

		
	}
	
	public static void tpcENormalized(String inputDataFile, String outputResultFile) {
		long start = System.nanoTime();

		
		Main database = new Main();		
		database.connectToMSSQL();
		
		try (FileWriter fw1 = new FileWriter(pathToResultFolderNormalized + outputResultFile +"_timestamp.txt");
			PrintWriter timestamp = new PrintWriter(fw1);
				FileWriter fw2 = new FileWriter(pathToResultFolderNormalized + outputResultFile + "_difference.txt");
				PrintWriter difference = new PrintWriter(fw2)){
		
			// Drop all indexed views if exists
			IndexedViewsCreator.dropIndexes(database.getConnection());
			System.out.println("All indexes dropped ... finished");
	
			// Tpce Normalized schema:
			MainNTtpcE tpcEOriginalSchema = new MainNTtpcE(database.getConnection());
			
			// Create normalized schema (first drop schema, then create full schema)
			NormalizedChemaCreator.createNormalizedDatabaseChema(database.getConnection());
			//$System.out.println(System.nanoTime() - start);
			System.out.println("Database schema creation ... finished");
			
			NormalizedChemaLoader.loadData(database.getConnection());
			//$System.out.println(System.nanoTime() - start);
			
			NormalizedChemaCreator.createIndexes(database.getConnection());
			System.out.println("Loading data ... finished");
			long coldStart = System.nanoTime() - start;
			System.out.println("Cold start ... finished after " + (coldStart) + " nanoseconds");
			
			tpcEOriginalSchema.startTransactionMixture(inputDataFile, timestamp, difference);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			database.disconnectFromMSSQL();
		}
		
	}
	
	public static void tpcEIndexed(String inputDataFile, String outputResultFile) {
		long start = System.nanoTime();

		Main database = new Main();		
		database.connectToMSSQL();

		
		try (FileWriter fw1 = new FileWriter(pathToResultFolderIndexes + outputResultFile +"_timestamp.txt");
				PrintWriter timestamp = new PrintWriter(fw1);
					FileWriter fw2 = new FileWriter(pathToResultFolderIndexes + outputResultFile + "_difference.txt");
					PrintWriter difference = new PrintWriter(fw2)){
			
			MainMWtpcE tpcEIndexedSchema = new MainMWtpcE(database.getConnection());

			IndexedViewsCreator.dropIndexes(database.getConnection());
			System.out.println("All indexes dropped ... finished");
			
			NormalizedChemaCreator.createNormalizedDatabaseChema(database.getConnection());
			System.out.println("Database schema creation ... finished");
			
			NormalizedChemaLoader.loadData(database.getConnection());
			System.out.println("Loading data ... finished");

			IndexedViewsCreator.createIndexView(database.getConnection()); //........database...
			System.out.println("Create materialized view ... finished");

			
			long coldStart = System.nanoTime() - start;
			System.out.println("Cold start ... finished after " + (coldStart) + " nanoseconds");
			
			tpcEIndexedSchema.startTransactionMixture(inputDataFile, timestamp, difference);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			database.disconnectFromMSSQL();
		}
		

	}
	
	
	public static void tpcEFullDenormalized(String inputDataFile, String outputResultFile) {
		long start = System.nanoTime();
		
		Main database = new Main();		
		database.connectToMSSQL();

		try (FileWriter fw1 = new FileWriter(pathToResultFolderFullDenormalized + outputResultFile +"_timestamp.txt");
				PrintWriter timestamp = new PrintWriter(fw1);
					FileWriter fw2 = new FileWriter(pathToResultFolderFullDenormalized + outputResultFile + "_difference.txt");
					PrintWriter difference = new PrintWriter(fw2)){

			// Tpce Normalized schema:
			MainFullDTtpcE tpcEDTSchema = new MainFullDTtpcE(database.getConnection());
			
			IndexedViewsCreator.dropIndexes(database.getConnection());
			System.out.println("All indexes dropped ... finished");

			NormalizedChemaCreator.createNormalizedDatabaseChema(database.getConnection());
			System.out.println("Database schema creation ... finished");
			
			NormalizedChemaLoader.loadData(database.getConnection());
			System.out.println("Loading data ... finished");

			FullDenormalizedChemaCreator.createDenormalizedDatabaseChema(database.getConnection());
			System.out.println("Denormalized schemas creation ... finished");

			FullDenormalizedChemaLoader.loadData(database.getConnection());
			System.out.println("Loading data to denormalized schema ... finished");
			
			FullDenormalizedChemaCreator.createIndexes(database.getConnection());
			System.out.println("Rising indexes on columns in denormalized schema ... finished");
			
			long coldStart = System.nanoTime() - start;
			System.out.println("Cold start ... finished after " + (coldStart) + " nanoseconds");

			tpcEDTSchema.startTransactionMixture(inputDataFile, timestamp, difference);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			database.disconnectFromMSSQL();
		}
		
	}	

	public static void tpcEPartialDenormalized(String inputDataFile, String outputResultFile) {
		long start = System.nanoTime();
		
		Main database = new Main();		
		database.connectToMSSQL();

		try (FileWriter fw1 = new FileWriter(pathToResultFolderPartialDenormalized + outputResultFile +"_timestamp.txt");
				PrintWriter timestamp = new PrintWriter(fw1);
					FileWriter fw2 = new FileWriter(pathToResultFolderPartialDenormalized + outputResultFile + "_difference.txt");
					PrintWriter difference = new PrintWriter(fw2)){

			// Tpce Normalized schema:
			MainPartialDTtpcE tpcEDTSchema = new MainPartialDTtpcE(database.getConnection());
			
			IndexedViewsCreator.dropIndexes(database.getConnection());
			System.out.println("All indexes dropped ... finished");

			NormalizedChemaCreator.createNormalizedDatabaseChema(database.getConnection());
			System.out.println("Database schema creation ... finished");
			
			NormalizedChemaLoader.loadData(database.getConnection());
			System.out.println("Loading data ... finished");

			PartialDenormalizedChemaCreator.createDenormalizedDatabaseChema(database.getConnection());
			System.out.println("Denormalized schemas creation ... finished");

			PartialDenormalizedChemaLoader.loadData(database.getConnection());
			System.out.println("Loading data to denormalized schema ... finished");
			
			PartialDenormalizedChemaCreator.createIndexes(database.getConnection());
			System.out.println("Rising indexes on columns in denormalized schema ... finished");
			
			long coldStart = System.nanoTime() - start;
			System.out.println("Cold start ... finished after " + (coldStart) + " nanoseconds");

			tpcEDTSchema.startTransactionMixture(inputDataFile, timestamp, difference);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			database.disconnectFromMSSQL();
		}
		
	}	
	

	public static void main(String[] args) {
		
//		dropSchema();
		
		// Normalized schema
//		for(int i = 0; i < Main.inputDataFileList.size(); i++) {
//			
//			Main.inputDataFile = Main.inputDataFileList.get(i);
//			Main.outputResultFile = Main.outputResultFileList.get(i);
//			
//			tpcENormalized(Main.inputDataFileList.get(i), Main.outputResultFileList.get(i));
//			
//		}
		
		// Materialized view 
//		for(int i = 0; i < Main.inputDataFileList.size(); i++) {
//			
//			Main.inputDataFile = Main.inputDataFileList.get(i);
//			Main.outputResultFile = Main.outputResultFileList.get(i);
//			
//			tpcEIndexed(Main.inputDataFileList.get(i), Main.outputResultFileList.get(i));
//		
//		}

		// Full Denormalized schema
//		for(int i = 0; i < Main.inputDataFileList.size(); i++) {
//			
//			Main.inputDataFile = Main.inputDataFileList.get(i);
//			Main.outputResultFile = Main.outputResultFileList.get(i);
//			
//			tpcEFullDenormalized(Main.inputDataFileList.get(i), Main.outputResultFileList.get(i));
//		
//		}
		
		// Partial Denormalized schema
		for(int i = 0; i < 1; /*Main.inputDataFileList.size(); */i++) {
			
			Main.inputDataFile = Main.inputDataFileList.get(i);
			Main.outputResultFile = Main.outputResultFileList.get(i);
			
			tpcEPartialDenormalized(Main.inputDataFileList.get(i), Main.outputResultFileList.get(i));
		
		}

		
	}

}

package rs.ac.bg.etf.matija.DTtpcE.Partial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PartialDenormalizedChemaLoader {

	public static void loadData(Connection databaseConnection) {
		// This will fill Denormalize schema with data from Normalized schema
		
		String getDataForDTT2T3T8F2 = "SELECT CA_ID, HS_CA_ID, HS_S_SYMB, LT_S_SYMB, HS_QTY, C_ID, CA_C_ID, LT_PRICE \r\n" + 
				"\r\n" + 
				"FROM dbo.CUSTOMER_ACCOUNT left outer join dbo.HOLDING_SUMMARY on HS_CA_ID = CA_ID, \r\n" + 
				"	dbo.LAST_TRADE, dbo.CUSTOMER\r\n" + 
				"\r\n" + 
				"WHERE LT_S_SYMB = HS_S_SYMB and C_ID = CA_C_ID ";
		
		int total = 0;
		int successfull = 0;
		
		try (Statement stmt = databaseConnection.createStatement();
			ResultSet rs = stmt.executeQuery(getDataForDTT2T3T8F2)){

			System.out.println("Fetch data for denormalized table [DTT2T3T8F2] ... finished");
				
			while(rs.next()) {
				
				boolean successfullyInserted = insertIntoDatabaseDTT2T3T8F2(databaseConnection, 
					rs.getLong("CA_ID"), rs.getString("HS_S_SYMB"), rs.getInt("HS_QTY"), 
					rs.getLong("C_ID"), rs.getDouble("LT_PRICE"));
				
				total += 1;

				if(successfullyInserted) {
					successfull += 1;
				}
				
				if(successfull % 10000 == 0) {
					System.out.println("Loaded rows: " + successfull);
				}

			}
			
		} catch(SQLException e) { 
			e.printStackTrace(); 
		}

		System.out.println("Total loaded rows: " + successfull + " (" + 
				String.format("%.2f", 100. * successfull / total) + "%) in table [DTT2T3T8F2]");


		total = 0;
		successfull = 0;
		
		String getDataForDTT8F6 = "SELECT CA_ID, CA_BAL\r\n" + 
				"\r\n" + 
				"FROM dbo.CUSTOMER_ACCOUNT left outer join dbo.HOLDING_SUMMARY on HS_CA_ID = CA_ID, dbo.LAST_TRADE, dbo.CUSTOMER\r\n" + 
				"\r\n" + 
				"WHERE LT_S_SYMB = HS_S_SYMB and C_ID = CA_C_ID \r\n" + 
				"\r\n" + 
				"GROUP BY CA_ID, CA_BAL";
		
		try (Statement stmt = databaseConnection.createStatement();
				ResultSet rs = stmt.executeQuery(getDataForDTT8F6)){

				System.out.println("Fetch data for denormalized table [DTT8F6] ... finished");
			
				while(rs.next()) {
					
					boolean successfullyInserted = insertIntoDatabaseDTT8F6(databaseConnection, 
						rs.getLong("CA_ID"), rs.getDouble("CA_BAL"));
					
					total += 1;

					if(successfullyInserted) {
						successfull += 1;
					}
					
					if(successfull % 1000 == 0) {
						System.out.println("Loaded rows: " + successfull);
					}

				}
			} catch(SQLException e) { 
				e.printStackTrace(); 
			}

		System.out.println("Total loaded rows: " + successfull + " (" + 
				String.format("%.2f", 100. * successfull / total) + "%) in table [DTT8F6]");
					
	
	}

	private static boolean insertIntoDatabaseDTT2T3T8F2(Connection databaseConnection, 
			long ca_id, String hs_s_symb, int hs_qty, long c_id, double lt_price) {

		String insertIntoDenormalizedTable = "INSERT INTO [tpcE].[dbo].[DTT2T3T8F2]\r\n" + 
				"           ([DT_CA_ID]\r\n" + 
				"           ,[DT_HS_S_SYMB]\r\n" + 
				"           ,[DT_HS_QTY]\r\n" + 
				"           ,[DT_CA_C_ID]\r\n" + 
				"           ,[DT_LT_PRICE])\r\n" + 
				"     VALUES\r\n" + 
				"           (?,?,?,?,?)";
		
		try (PreparedStatement stmt = databaseConnection.prepareStatement(insertIntoDenormalizedTable)){

			databaseConnection.setAutoCommit(false);
			
			stmt.setLong(1, ca_id);
			stmt.setString(2, hs_s_symb);
			stmt.setInt(3, hs_qty);			
			stmt.setLong(4, c_id);
			stmt.setDouble(5, lt_price);			

			stmt.executeUpdate();				

			databaseConnection.commit();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
			
				databaseConnection.rollback();
				return false;

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally {
			try {
				databaseConnection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;
		
	}
	
	
	private static boolean insertIntoDatabaseDTT8F6(Connection databaseConnection, 
			long ca_id, double ca_bal) {

		String insertIntoDenormalizedTable = "INSERT INTO [tpcE].[dbo].[DTT8F6]\r\n" + 
				"           ([DT_CA_ID]\r\n" + 
				"           ,[DT_CA_BAL])\r\n" + 
				"     VALUES\r\n" + 
				"           (?,?)";

		
		try (PreparedStatement stmt = databaseConnection.prepareStatement(insertIntoDenormalizedTable)){

			databaseConnection.setAutoCommit(false);
			
			stmt.setLong(1, ca_id);
			stmt.setDouble(2, ca_bal);			

			stmt.executeUpdate();				

			databaseConnection.commit();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
			
				databaseConnection.rollback();
				return false;

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally {
			try {
				databaseConnection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		
		return true;
		
	}
	
}

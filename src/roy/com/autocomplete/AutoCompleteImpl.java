package roy.com.autocomplete;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AutoCompleteImpl implements AutoComplete{
	
	private Object userName;
	private Object password;
	private String dbms;
	private String serverName;
	private String portNumber;
	private String dbName;
	Connection conn = null;

	public Connection getConnection() throws SQLException {

	    Properties connectionProps = new Properties();
	    connectionProps.put("user", this.userName);
	    connectionProps.put("password", this.password);

	    if (this.dbms.equals("mysql")) {
	        conn = DriverManager.getConnection(
	                   "jdbc:" + this.dbms + "://" +
	                   this.serverName +
	                   ":" + this.portNumber + "/",
	                   connectionProps);
	    } else if (this.dbms.equals("derby")) {
	        conn = DriverManager.getConnection(
	                   "jdbc:" + this.dbms + ":" +
	                   this.dbName +
	                   ";create=true",
	                   connectionProps);
	    }
	    System.out.println("Connected to database");
	    return conn;
	}

	@Override
	public List<Response> getCountries(String input) {
		if((input == null) ||  (input.length() < 2)) {
			return null;
		}
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("Select country_name From countries Where country_name Like '" + input + "%'");
			List<Response> list = new ArrayList<Response>();
			
			while(rs.next()) {
				Response response = new Response(rs.getString("country_name"));
				list.add(response);
			}
			
			return list;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Response> getSettlements(String country, String input) {
		if((country == null) || (input == null) ||  (input.length() < 2)) {
			return null;
		}
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("Select settlement_name, has_postcode, postcode From settlements Where "
					+ "country_id IN (Select country_id From countries Where country_name = " + country + ") "
							+ "AND name Like '" + input + "%'");
			List<Response> list = new ArrayList<Response>();
			
			while(rs.next()) {
				boolean hasPostcode = rs.getBoolean("has_postcode");
				Response response;
				if(hasPostcode) {
					response = new Response(rs.getString("settlement_name"),rs.getInt("postcode"));
				}
				else { 
					response = new Response(rs.getString("settlement_name"));
				}
				list.add(response);
			}
			
			return list;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Response> getStreets(String settlement, String input) {
		if((settlement == null) || (input == null) ||  (input.length() < 2)) {
			return null;
		}
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("Select street_name From streets Where "
					+ "settlement_id IN (Select settlement_id From settlements Where settlement_name = " + settlement + ") "
							+ "AND street_name Like '" + input + "%'");
			
			List<Response> list = new ArrayList<Response>();
			
			while(rs.next()) {
				int postcode = getPostCode(settlement);
				Response response;
				if(postcode != 0) {
					response = new Response(rs.getString("street_name"),postcode);
				}
				else { 
					response = new Response(rs.getString("street_name"));
				}
				list.add(response);
			}
			
			return list;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void printResponses(List<Response> responses) {
		for(Response response: responses) {
			if(response.getPostCode() == 0) {
				System.out.println(response.getName());
			}
			else {
				System.out.println(response.getName() + " " + response.getPostCode());
			}
		}
		
	}
	
	private int getPostCode(String settlement){
		int defaultValue = 0;
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("Select has_postcode, postcode From settlements Where settlement_name = " + settlement);
			
			if(rs.next()) {
				boolean hasPostcode = rs.getBoolean("has_postcode");
				
				if(hasPostcode) {
					return rs.getInt("postcode");
				}
				return defaultValue;
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();		
		}
		
		return defaultValue;

	}

	@Override
	public int getPostcode(String settlement, String street, int houseNumber) {
		int settlementPostcode = getPostCode(settlement);
		
		if(settlementPostcode != 0) {
			return settlementPostcode;
		}
		else {
			int streetId = getStreetId(settlement, street);
			
			if(streetId != 0) {
				try {
					Statement stmt = conn.createStatement();
					
					ResultSet rs = stmt.executeQuery("Select postcode_number From postcode Where "
							+ "street_id = " + streetId + " AND house_number = " + houseNumber);
					
					if(rs.next()) {
						return rs.getInt("postcode_number");
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
			else {
				System.out.println("can not find street on DataBase");
			}
		}
		
		return 0;
	}
	
	private int getStreetId(String settlement, String street) {
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("Select street_id From streets Where "
					+ "settlement_id IN (Select settlement_id From settlements Where settlement_name = " + settlement + ")");
			
			if(rs.next()) {
				return rs.getInt("street_id");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return 0;
	}
}

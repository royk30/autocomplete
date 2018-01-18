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
			
			ResultSet rs = stmt.executeQuery("Select name From countries Where name Like '" + input + "%'");
			List<Response> list = new ArrayList<Response>();
			
			while(rs.next()) {
				Response response = new Response(rs.getString("name"));
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
			
			ResultSet rs = stmt.executeQuery("Select name, has_postcode, postcode From settlements Where "
					+ "country_id IN (Select country_id From countries Where name = " + country + ") "
							+ "AND name Like '" + input + "%'");
			List<Response> list = new ArrayList<Response>();
			
			while(rs.next()) {
				boolean hasPostcode = rs.getBoolean("has_postcode");
				Response response;
				if(hasPostcode) {
					response = new Response(rs.getString("name"),rs.getInt("postcode"));
				}
				else { 
					response = new Response(rs.getString("name"));
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
			
			ResultSet rs = stmt.executeQuery("Select name From streets Where "
					+ "settlement_id IN (Select id From settlements Where name = " + settlement + ") "
							+ "AND name Like '" + input + "%'");
			
			List<Response> list = new ArrayList<Response>();
			
			while(rs.next()) {
				int postcode = getPostCode(settlement);
				Response response;
				if(postcode != 0) {
					response = new Response(rs.getString("name"),postcode);
				}
				else { 
					response = new Response(rs.getString("name"));
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
			
			ResultSet rs = stmt.executeQuery("Select has_postcode, postcode From settlements Where name = " + settlement);
			
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
}

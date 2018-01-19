package roy.com.autocomplete;

import java.util.List;

public interface AutoComplete {
	
	public List<Response> getCountries(String input);
	public List<Response> getSettlements(String country, String input);
	public List<Response> getStreets(String settlement, String input);
	public List<Response> getHouseNumbers(String settlement, String street);
	public int getPostcode(String settlement, String street, int houseNumber);
	public int getPostcode(String settlement, String street);
	public int getPostCode(String settlement);
	public void printResponses (List<Response> responses);
	
}

package roy.com.autocomplete;

import java.util.List;

public interface AutoComplete {
	
	public List<Response> getCountries(String input);
	public List<Response> getSettlements(String country, String input);
	public List<Response> getStreets(String settlement, String input);
	public void printResponses (List<Response> responses);
	
}

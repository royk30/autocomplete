package roy.com.autocomplete;

public class Response {
	private String name;
	private int postCode=0;
	
	public Response(String name) {
		this.name = name;
	}
	
	public Response(String name, int postCode) {
		this.name = name;
		this.postCode = postCode;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPostCode() {
		return postCode;
	}
	public void setPostCode(int postCode) {
		this.postCode = postCode;
	}
}

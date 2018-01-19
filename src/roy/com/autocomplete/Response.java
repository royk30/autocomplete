package roy.com.autocomplete;

public class Response {
	private Object name;
	private int postCode=0;
	
	public Response(Object name) {
		this.name = name;
	}
	
	public Response(Object name, int postCode) {
		this.name = name;
		this.postCode = postCode;
	}

	public Object getName() {
		return name;
	}
	public void setName(Object name) {
		this.name = name;
	}
	public int getPostCode() {
		return postCode;
	}
	public void setPostCode(int postCode) {
		this.postCode = postCode;
	}
}

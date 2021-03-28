package blonski_CSCI201L_Assignment3;

import java.util.ArrayList;
import java.util.List;

public class Data {
	
	public List<Company> data;
	String fileName;
	
	Data() {
		this.data = new ArrayList<Company>();
	}
	public List<Company> getData() {
		return this.data;
	}
	public void setData(List<Company> data) {
		this.data = data;
	}

}

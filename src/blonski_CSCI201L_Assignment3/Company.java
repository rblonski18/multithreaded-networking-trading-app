package blonski_CSCI201L_Assignment3;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Company {

	private String ticker;
	private double close;

	//transient public Broker broker;
	
	public Company(String ticker, double price) {
		this.ticker = ticker;
		this.close = price;
	}
	
	public double getPrice() {
		return close;
	}
	
	public void setPrice(double price) {
		this.close = price;
	}
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public Date formatDate(String startDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date formattedDate = new Date();
		
		// eclipse is telling me to handle case of parsing error. 
		try {
			formattedDate = format.parse(startDate);
		}catch(Exception e) {
			System.out.println("There was trouble parsing the date. ");
		}
		return formattedDate;
	}
	
	
	public boolean allClear() {
		if(this.ticker == null) return false;
		else return true;
	}
}

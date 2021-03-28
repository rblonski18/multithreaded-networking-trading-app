package blonski_CSCI201L_Assignment3;

import java.io.Serializable;

public class Trader implements Serializable {

	private static final long serialVersionUID = 1L;
	public int id;
	public double balance;
	
	Trader(int id, double balance) {
		this.id = id;
		this.balance = balance;
	}

}

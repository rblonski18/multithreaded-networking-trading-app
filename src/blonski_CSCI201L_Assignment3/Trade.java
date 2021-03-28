package blonski_CSCI201L_Assignment3;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Trade extends Thread {
	
	public int seconds;
	public String ticker;
	public int numStock;
	public Date date;
	public double cost;
	public Vector<ClientThread> clientThreads;
	public static List<Trade> trNotCompleted;
	
	Trade(int time, String tick, double close, int num, Date date) {
		this.seconds = time;
		this.ticker = tick;
		this.numStock = num;
		this.cost = close;
		this.date = date;
	}
	
	public void run() {
		
		int numBroke = 0;
		int numBusy = 0;
		double tradeCost = (numStock*cost);
		
		for(int i = 0; i < clientThreads.size(); i++) {
			
			if(!clientThreads.get(i).isAsleep) {
			
				if(clientThreads.get(i).balance < tradeCost) {
					numBroke++;
					if(numBroke == clientThreads.size()) {
						trNotCompleted.add(this);
						return; // temporary, need to add somewhere to return at end
					}
				} else { // trade can and will be executed. 
					clientThreads.get(i).assignTrade(this);
					break;
				}
			} else {
				numBusy++;
				if(numBusy == clientThreads.size()) i = 0;
			}
			
		}
		
		
	}
}

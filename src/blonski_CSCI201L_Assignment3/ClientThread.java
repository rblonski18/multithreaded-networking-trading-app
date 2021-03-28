package blonski_CSCI201L_Assignment3;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Queue;
import java.util.Vector;


public class ClientThread extends Thread {
	
	public int id;
	public double balance;
	public double profit;
	public int traderIT = 0;
	
	public Schedule tradeSchedule;
	public static Vector<Trader> incomingTraders;
	public Instant first;
	public Queue<Trade> tradeQueue;
	
	private PrintWriter pw;
	public boolean isAsleep;
	public Socket s;
	
	public ClientThread(int id, double balance, Instant first, Socket s){
		this.id = id;
		this.balance = balance;
		this.s = s;
		this.start();
		
		try {
			pw = new PrintWriter(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// have server broadcast -> client executes "trade" function. 
	public void sendMessage(String message) { // sends message to client;
		pw.println(message);
		pw.flush();
	}
	public void assignTrade(Trade tr) {
		
		DecimalFormat numberformat = new DecimalFormat("#.00");
		
		String pOrS;
		int num;
		if(tr.numStock > 0) {
			pOrS = "purchase";
			num = tr.numStock;
		} else {
			pOrS = "sale";
			num = tr.numStock*(-1);
		}
		double cost = tr.cost*num;
		String message = "[" + this.id + "] Assigned " + pOrS + 
				" of " + num + " stocks of " + 
				tr.ticker + ". Total cost estimate= $"
				+ tr.cost + "*" + num + "= $" + numberformat.format(cost);
		sendMessage(Util.printMessage(message, first));
		executeTrade(tr);
	}
	
	public synchronized void executeTrade(Trade tr) {
		
		DecimalFormat numberformat = new DecimalFormat("#.00");
		
		try {
			if(tr.numStock < 0) {
				String sale = "[" + this.id + "] Starting sale of " 
						+ tr.numStock*(-1) + " shares of " 
						+ tr.ticker + ". Total cost estimate= $" 
						+ tr.cost + "*" + tr.numStock*(-1) + "= $" + numberformat.format(tr.cost*tr.numStock*(-1));
				sendMessage(Util.printMessage(sale, first));
				this.isAsleep = true;
				Thread.sleep(1000);
				this.isAsleep = false;
				sale = "[" + this.id + "] Finished sale of " + tr.numStock*(-1) + " shares of " + tr.ticker + 
						". Total gain estimate= $" 
						+ numberformat.format(profit) + "+" + numberformat.format(tr.cost*tr.numStock*(-1)) + " = $" + 
						numberformat.format((profit+=tr.cost*tr.numStock*(-1)));
				sendMessage(Util.printMessage(sale, first));
			} else if(tr.numStock > 0) {
				String purchase = "[" + this.id + "] Starting purchase of " + tr.numStock + 
						" shares of " + tr.ticker + ". Total cost estimate= $" 
						+ tr.cost + "*" + tr.numStock + "= $" + numberformat.format(tr.cost*tr.numStock);
				sendMessage(Util.printMessage(purchase, first));
				this.isAsleep = true;
				Thread.sleep(1000);
				this.isAsleep = false;
				purchase = "[" + this.id + "] Finished purchase of " + tr.numStock + " shares of " + tr.ticker
						+ ". Remaining balance = $" + balance + "- $" + tr.cost*tr.numStock +
						"= $" + numberformat.format(balance-=(tr.cost*tr.numStock));
				sendMessage(Util.printMessage(purchase, first));
			}
		} catch (InterruptedException e) {
			System.out.println("IE on executeTrade of " + tr.ticker);
		} 
	}
	
}

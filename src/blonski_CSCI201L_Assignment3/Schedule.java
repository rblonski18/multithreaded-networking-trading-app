package blonski_CSCI201L_Assignment3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Schedule extends Thread {
	
	public Data companyList;
	public Vector<ClientThread> clients;
	public static List<Trade> tradeSchedule;
	public static List<Trade> notCompleted;
	public Server s;
	
	public Schedule(List<Trade> sched, Data companies) {
		tradeSchedule = sched;
		this.companyList = companies;
		notCompleted = Collections.synchronizedList(new ArrayList<Trade>());
	}
	
	public void populate() {
		for(int i = 0; i < tradeSchedule.size(); i++) {
			tradeSchedule.get(i).clientThreads = clients;
			Trade.trNotCompleted = notCompleted;
		}
	}
	
	public void run() {
		
		System.out.println("Starting trade execution ");
		
		int counter = 0;
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		while(!tradeSchedule.isEmpty()) {
			
			if(tradeSchedule.get(0).seconds == counter) {
				
				Trade current = tradeSchedule.remove(0);

				executor.execute(current);
				//current.start();
				
			} else {
				
				try {
					
					Thread.sleep(1000);
					
					counter++;
					
				} catch(InterruptedException e) {
					
					System.out.println("IE when sleepin ");
					
				}
			}
		}

		executor.shutdown();
		while(!executor.isTerminated()) {
			Thread.yield();
		}

		String tradesNotCompleted = "Trades not completed: ";
		if(notCompleted.size() == 0) tradesNotCompleted += "NONE";
		
		for(int i = 0; i < notCompleted.size(); i++) {
			tradesNotCompleted += "" + notCompleted.get(i).seconds + ", "
					+ notCompleted.get(i).ticker + ", " + notCompleted.get(i).numStock;
		}

		for(ClientThread ct : clients) {
			ct.sendMessage(tradesNotCompleted + ". Total profit earned: $" + ct.profit);
			ct.sendMessage("#Trading process complete. ");
		}
		
		Server.broadcast(tradesNotCompleted);
		Server.broadcast("All trades complete. ");
		
	}

}
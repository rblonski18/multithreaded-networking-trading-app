package blonski_CSCI201L_Assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Client extends Thread {
	
	public static int traderIT = 0;
	public int id;
	public Vector<Trader> traders;
	public BufferedReader br;
	
	@SuppressWarnings("unchecked")
	public Client() {
		
		Socket s = connectToHost();
		
		try {
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			traders = (Vector<Trader>) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.start();
		
	}
	


	public static void main(String [] args) {
	
		@SuppressWarnings("unused")
		Client newClient = new Client();
	
	}
	
	public Socket connectToHost() {
		
		Socket s = null;
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		
		try {
			System.out.println("Welcome to SalStocks v2.0! Enter the server hostname: ");
			String host = input.next();
			System.out.println("Enter the server port: ");
			int port = Integer.parseInt(input.next());
			System.out.println("Trying to connect to " + host + ":" + port);
			s = new Socket(host, port);
			System.out.println("Connected to " + host + ":" + port);
		} catch(IOException ioe) {
			System.out.println("Connection refused. Ensure you have the correct hostname and port. ");
			return connectToHost();
		} 
		return s;
	}
	
	public void run() {
		try {
			while(true) {
				String line = br.readLine();
				if(line.charAt(0) == '#') {
					String newSub = line.substring(1);
					System.out.println(newSub);
					break;
				}
				System.out.println(line);
			}
		} catch(IOException ioe) {
			System.out.println("IOException in Client run");
		}
	}


}

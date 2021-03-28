package blonski_CSCI201L_Assignment3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

public class Server {
	
	public static Vector<Trader> incomingTraders;
	public static Schedule tradeSchedule;
	public static Instant first;
	public static Vector<ClientThread> clientThreads;
	
	public Server(int port, Vector<Trader> traders, Schedule trSched) {
		
		incomingTraders = traders;
		tradeSchedule = trSched;
		ServerSocket ss = null;
		first = Instant.now();
		clientThreads = new Vector<ClientThread>();
		
		try {
			
			ss = new ServerSocket(port);
			System.out.println("Listening on port 3456, waiting for traders.");
			
			for(int i = 0; i < incomingTraders.size(); i++) {

				Socket s = ss.accept();
				System.out.println("Connection from: " + s.getInetAddress());
				
				OutputStream os = s.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(incomingTraders);
				
				ClientThread ct = new ClientThread(incomingTraders.get(i).id, incomingTraders.get(i).balance, first, s);
				clientThreads.add(ct);
				
				// broadcast message to client to say "x traders needed"
				if(i != incomingTraders.size()-1) {
					broadcast(incomingTraders.size()-1 + " traders left.");
				} else {
					broadcast("All traders present. Starting trades. ");
				}
				
			}
			
			for(ClientThread thread : clientThreads) {
				thread.first = Instant.now();
			}
			
			tradeSchedule.s = this;
			tradeSchedule.clients = clientThreads;
			tradeSchedule.populate();
			tradeSchedule.start();
			
		} catch (IOException ioe) {
			System.out.println("IOException in TradingFloor constructor: " + ioe.getMessage());
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void broadcast(String message) {
		if(message != null) {
			System.out.println(message);
			for(ClientThread threads : clientThreads) {
				threads.sendMessage(message);
			}
		}
	}
	
	public static void main(String[] args) {
		 
		Scanner input = new Scanner(System.in);
		
		Schedule tradeSchedule = readCSVFile(input); // i have my schedule of trades, i need my traders - what are the traders? clients
		
		System.out.println("Trade schedule read correctly. ");
		
		Vector<Trader> traders = readTraders(input); // grab my traders
		
		System.out.println("Traders file read correctly. ");
		
		@SuppressWarnings("unused")
		Server server = new Server(3456, traders, tradeSchedule);
		
	}
	
	public static Vector<Trader> readTraders(Scanner input) {
		
		System.out.println("What is the name of the traders csv file? ");
		String csvFileName = input.next();
		
		String row;
		
		Vector<Trader> traders = new Vector<Trader>();
		
		try {
			BufferedReader csvReader = new BufferedReader(new FileReader(csvFileName));
			while((row = csvReader.readLine()) != null) {
				String[] data = row.split(",");
				int traderID = Integer.parseInt(data[0]);
				double traderBalance = Double.parseDouble(data[1]);
				Trader tr = new Trader(traderID, traderBalance);
				traders.add(tr);
				row = "";
			}
			csvReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File wasn't found. Make sure file is in correct place, and try again. ");
			return readTraders(input);
		} catch (IOException e) {
			System.out.println("File not formatted properly. Format correctly and try again. ");
			return readTraders(input);
		}
		
		return traders;
	}
	
	public static Company priceCall(String ticker, Date date) {
	
		// i need to convert this to make an API call.
	
		Company[] companyArray = new Company[1];
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String d = df.format(date);
		
		String url = "https://api.tiingo.com/tiingo/daily/" + ticker + 
				"/prices?startDate=" + d + "&endDate=" + d + "&token=50d4d927db8d992794daab2956218a0384f75434";
		
		
		try {
			URL actualURL = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) actualURL.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			Gson gson = new Gson();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			String json = buffer.toString();
			companyArray = gson.fromJson(json, Company[].class);
			reader.close();
			
		} catch(NoSuchFileException e) {
			
			System.out.println("The ticker price on " + d + " could not be found. ");
			System.out.println("Please reformat the schedule to pass in valid dates. ");
			
		} catch(IOException e) {
			
			System.out.println("The file is not formatted properly. There was trouble parsing the file. ");
	
		} 
		
		// just a loop that goes through and calls a method that ensures all data
		// is entered in for each company - if not, file not formatted correctly. 
		
		return companyArray[0];
	}

	public static Schedule readCSVFile(Scanner input) {
		// file to read in CSV input. 
		
		// some of the code to read the csv came from a website. 
		// https://stackabuse.com/reading-and-writing-csvs-in-java/
		
		Data companyList = new Data();
		
		String csvFileName;
		String row;
		List<Trade> schedule = Collections.synchronizedList(new ArrayList<Trade>());
		Schedule retSched = new Schedule(schedule, companyList);
		
		System.out.println("What is the name of the schedule csv file? ");
		csvFileName = input.next();

		try {
			BufferedReader csvReader = new BufferedReader(new FileReader(csvFileName));
			while((row = csvReader.readLine()) != null) {
				String[] data = row.split(",");
				int currentTime = Integer.parseInt(data[0]);
				String ticker = data[1];
				Date stockDate = new SimpleDateFormat("yyyy-MM-dd").parse(data[3]);
				int trades = Integer.parseInt(data[2]);
				Company current = priceCall(ticker, stockDate);
				retSched.companyList.data.add(current);
				Trade currentTrade = new Trade(currentTime, ticker, current.getPrice(), trades, stockDate);
				Schedule.tradeSchedule.add(currentTrade);
				
				row = "";
			}
			csvReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File wasn't found. Make sure file is in correct place, and try again. ");
			return readCSVFile(input);
		} catch (IOException e) {
			System.out.println("File not formatted properly. Format correctly and try again. ");
			return readCSVFile(input);
		} catch (ParseException e) {
			System.out.println("Parsing exception on date, format correctly and try again. ");
			return readCSVFile(input);
		} 
		return retSched;
	}


}

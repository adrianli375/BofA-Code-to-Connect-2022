import java.awt.event.WindowEvent;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author adrianli375
 * Portfolio tracker is the main class of the whole project.
 */
public class PortfolioTracker {

	/**
	 * trackerFrame is the UI frame.
	 */
	TrackerFrame trackerFrame;
	/**
	 * bondDetails stores (bondID, currency) key-value pairs.
	 */
	static HashMap<String, String> bondCurrencyDetails;
	/**
	 * currencyBondMap stores (currency, list of bond ID) key-value pairs.
	 */
	static HashMap<String, ArrayList<String>> currencyBondsMap;
	/**
	 * deskCash stores (desk, cash) key-value pairs.
	 */
	static HashMap<String, Double> deskCash;
	/**
	 * bondPrices stores (bondID, price) key-value pairs
	 */
	static HashMap<String, Double> bondPrices;
	/**
	 * fxPrices stores (currency, rate) key-value pairs.
	 */
	static HashMap<String, Double> fxPrices;
	/**
	 * deskInformation stores (desk name, desk object) key-value pairs.
	 */
	static HashMap<String, Desk> deskInformation;

	/**
	 * Default constructor of a PortfolioTracker object.
	 * @throws IOException
	 */
	PortfolioTracker() throws IOException {
		new File("log").mkdir();
		writeToLogFile(getCurrentTime() + " Logfile initialized.\n", true);
		new File("output_files").mkdir();
		writeToLogFile(getCurrentTime() + " File directory for output_files opened.\n", false);
		bondCurrencyDetails = new HashMap<String, String>();
		currencyBondsMap = new HashMap<String, ArrayList<String>>();
		deskCash = new HashMap<String, Double>();
		bondPrices = new HashMap<String, Double>();
		fxPrices = new HashMap<String, Double>();
		deskInformation = new HashMap<String, Desk>();
		this.initializeData();
		this.initializeDesks();
		this.openTrackerFrame();
	}
	
	/**
	 * Gets input data. The input data folder should be stored in PortfolioTracker\input_data directory. 
	 * @throws IOException 
	 */
	private void initializeData() throws IOException {
		String line;
		BufferedReader bondDetailsReader = new BufferedReader(new FileReader("input_data/bond_details.csv"));
		bondDetailsReader.readLine();
		while((line = bondDetailsReader.readLine()) != null) {
			line = line.replaceAll(" ", "");
			String[] row = line.split(",");
			String bondID = row[0];
			String currency = row[1];//.substring(1);
			bondCurrencyDetails.put(bondID, currency);
			
			ArrayList<String> listOfBondsSameCurrency = currencyBondsMap.get(currency);
			if (listOfBondsSameCurrency != null) {
				listOfBondsSameCurrency.add(bondID);
			}
			else {
				listOfBondsSameCurrency = new ArrayList<String>();
				listOfBondsSameCurrency.add(bondID);
			}
			currencyBondsMap.put(currency, listOfBondsSameCurrency);
		}
		bondDetailsReader.close();
		for (String ccy : currencyBondsMap.keySet()) {
			Collections.sort(currencyBondsMap.get(ccy));
		}
		writeToLogFile(getCurrentTime() + " Bond details loaded.\n", false);
		
		BufferedReader initialCashReader = new BufferedReader(new FileReader("input_data/initial_cash.csv"));
		initialCashReader.readLine();
		while((line = initialCashReader.readLine()) != null) {
			line = line.replaceAll(" ", "");
			String[] row = line.split(",");
			deskCash.put(row[0], Double.parseDouble(row[1]));
		}
		initialCashReader.close();
		writeToLogFile(getCurrentTime() + " Initial cash for all desks loaded.\n", false);
		
		BufferedReader initialFXReader = new BufferedReader(new FileReader("input_data/initial_fx.csv"));
		initialFXReader.readLine();
		while ((line = initialFXReader.readLine()) != null) {
			line = line.replaceAll(" ", "");
			String[] row = line.split(",");
			fxPrices.put(row[0], Double.parseDouble(row[1]));
		}
		initialFXReader.close();
		writeToLogFile(getCurrentTime() + " Initial FX prices loaded.\n", false);
	}
	
	/**
	 * Initializes the desks available as per initial_cash.csv file.
	 * @throws IOException
	 */
	private void initializeDesks() throws IOException {
		Set<String> desks = deskCash.keySet();
		for (String deskName : desks) {
			deskInformation.put(deskName, new Desk(deskName));
		}
		writeToLogFile(getCurrentTime() + " All desks loaded.\n", false);
	}
	
	/**
	 * Opens the UI frame.
	 * @throws IOException
	 */
	private void openTrackerFrame() throws IOException {
		try {
			this.trackerFrame = new TrackerFrame();
			this.trackerFrame.setVisible(true);
			writeToLogFile(getCurrentTime() + " Frame opened.\n", false);
		} catch (Exception e) {
			writeToLogFile(getCurrentTime() + " Exception thrown at openTrackerFrame().\n", false);
		}
	}
	
	/**
	 * Closes the UI frame.
	 * @param frame UI frame
	 * @throws IOException
	 */
	private void closeTrackerFrame(TrackerFrame frame) throws IOException {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		writeToLogFile(getCurrentTime() + " Frame closed.\n", false);
	}
	
	/**
	 * Writes a given text to the log file.
	 * @param text text to be written
	 * @param reset if set to true, the existing file will be overwritten (if a file with the same name at the same directory exists)
	 * @throws IOException
	 */
	public static void writeToLogFile(String text, boolean reset) throws IOException {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("log/EventLog.log"), !reset));
			writer.write(text);
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * getCurrentTime is a method to obtain the current time recorded.
	 * @return The formatted string of the current time.
	 */
	public static String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd '-' HH:mm:ss.SSS z");
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}
	
	/**
	 * Main method.
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		PortfolioTracker port = new PortfolioTracker();
		//methods to be written here
		PortfolioTracker.writeToLogFile(getCurrentTime() + " Processing events from json file.\n", false);
		EventProcessor processor = new EventProcessor();
		ArrayList<Event> eventsList = processor.processEvents();
		PortfolioTracker.writeToLogFile(getCurrentTime() + " All events data loaded from json.\n", false);
		processor.processEvents(eventsList, port.trackerFrame);
		//port.closeTrackerFrame(port.trackerFrame);
		PortfolioTracker.writeToLogFile(getCurrentTime() + " Process finished.", false);
	}

}

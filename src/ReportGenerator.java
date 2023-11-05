import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * @author adrianli375
 * ReportGenerator is the object involved in generating reports.
 */
public class ReportGenerator {
	
	int latestID;
	/**
	 * The file directory in the output folder <br>
	 * e.g. if the latestID of this object is 100, the previous file directory is "output_files/output_100".
	 */
	String fileDirectory;
	/**
	 * The previous file directory in the output folder. <br>
	 * e.g. if the latestID of this object is 100, the previous file directory is "output_files/output_99".
	 */
	String previousFileDirectory = null;
	
	/**
	 * Constructor of an eventID object
	 * @param eventID the event ID
	 */
	ReportGenerator(int eventID) {
		this.latestID = eventID;
		this.fileDirectory = "output_files/output_" + String.valueOf(latestID);
		if (eventID > 1) {
			this.previousFileDirectory = "output_files/output_" + String.valueOf(latestID - 1);
		}
		new File(fileDirectory).mkdir();
		
	}
	
	/**
	 * Writes the output to csv files, except the exclusion csv file.
	 * @throws IOException
	 */
	public void writeToCSVFiles() throws IOException {
		writeCashLevelCSVFile();
		writePositionLevelCSVFile();
		writeBondLevelCSVFile();
		writeCurrencyLevelCSVFile();
	}
	
	/**
	 * Writes the output to the exclusion csv file.
	 * @param eve the event object involved.
	 * @param isExclusion a boolean variable stating whether or not the object raises an exclusion.
	 * @throws IOException
	 */
	public void writeExclusionCSVFile(Event eve, boolean isExclusion) throws IOException {
		BufferedWriter csvWriter = new BufferedWriter(new FileWriter(
													new File(fileDirectory + "/exclusions_" + latestID + ".csv"), false));
		
		if (latestID == 1) {
			//Write the first line of the file
			csvWriter.write("EventID,Desk,Trader,Book,BuySell,Quantity,BondID,Price,ExclusionType\n");
		}
		
		else {
			BufferedReader reader = new BufferedReader(new FileReader(previousFileDirectory + "/exclusions_" + (latestID - 1) + ".csv"));
			String line;
			
			while((line = reader.readLine()) != null) {
				csvWriter.write(line + "\n");
			}
			reader.close();
			
		}
		
		if (isExclusion) {
			TradeEvent exc = (TradeEvent) eve;
			String eventID = String.valueOf(exc.id);
			csvWriter.write(eventID + ",");
			String desk = exc.desk;
			csvWriter.write(desk + ",");
			String trader = exc.trader;
			csvWriter.write(trader + ",");
			String book = exc.book;
			csvWriter.write(book + ",");
			String buySell = exc.buySell;
			csvWriter.write(buySell + ",");
			String quantity = String.valueOf(exc.quantity);
			csvWriter.write(quantity + ",");
			String bondID = exc.bondID;
			csvWriter.write(bondID + ",");
			
			String exclusionType = exc.exclusionType;
			String price = "";
			
			if (exclusionType.equals("QUANTITY_OVERLIMIT")) {
				price = String.valueOf(PortfolioTracker.bondPrices.get(exc.bondID));
			}
			
			csvWriter.write(price + ",");
			csvWriter.write(exclusionType + "\n");
			
		}
		
		csvWriter.close();
		
	}
	
	private void writeCashLevelCSVFile() throws IOException {
		BufferedWriter csvWriter = new BufferedWriter(new FileWriter(
				new File(fileDirectory + "/cash_level_portfolio_" + latestID + ".csv"), false));
		csvWriter.write("Desk,Cash\n");
		
		HashMap<String, Double> cashLevel = PortfolioTracker.deskCash;
		Set<String> deskSet = cashLevel.keySet();
		ArrayList<String> deskList = new ArrayList<String>(deskSet);
		Collections.sort(deskList);
		
		for (String desk : deskList) {
			csvWriter.write(desk + ",");
			String cash = String.valueOf(Event.df.format(cashLevel.get(desk)));
			csvWriter.write(cash + "\n");
		}
		
		csvWriter.close();
		
	}
	
	private void writePositionLevelCSVFile() throws IOException {
		BufferedWriter csvWriter = new BufferedWriter(new FileWriter(
				new File(fileDirectory + "/position_level_portfolio_" + latestID + ".csv"), false));
		csvWriter.write("Desk,Trader,Book,Positions,NV\n");
		
		ArrayList<String> outputText = new ArrayList<String>();
		
		//Get all desks
		HashMap<String, Double> cashLevel = PortfolioTracker.deskCash;
		Set<String> deskSet = cashLevel.keySet();
		ArrayList<String> deskList = new ArrayList<String>(deskSet);
		
		//Iterate through all desks
		for (String deskName : deskList) {
			Desk desk = PortfolioTracker.deskInformation.get(deskName);
			//Get all traders
			HashMap<String, Trader> traders = desk.traders;
			Set<String> traderSet = traders.keySet();
			//Iterate through all traders
			for (String traderName : traderSet) {
				Trader trader = traders.get(traderName);
				//Get all books
				HashMap<String, Book> books = trader.books;
				Set<String> bookSet = books.keySet();
				//Iterate through all books
				for (String bookName : bookSet) {
					Book book = books.get(bookName);
					String text = deskName + "," + traderName + "," + bookName + "," + 
										String.valueOf(book.getTotalPositions()) + "," + 
										Event.df.format(book.getTotalNV()) + "\n";
					outputText.add(text);
				}
			}
		}
		
		Collections.sort(outputText);
		for (String text : outputText) {
			csvWriter.write(text);
		}
		
		csvWriter.close();
		
	}
	
	private void writeBondLevelCSVFile() throws IOException {
		BufferedWriter csvWriter = new BufferedWriter(new FileWriter(
				new File(fileDirectory + "/bond_level_portfolio_" + latestID + ".csv"), false));
		csvWriter.write("Desk,Trader,Book,BondID,Positions,NV\n");
		
		ArrayList<String> outputText = new ArrayList<String>();
		
		//Get all desks
		HashMap<String, Double> cashLevel = PortfolioTracker.deskCash;
		Set<String> deskSet = cashLevel.keySet();
		ArrayList<String> deskList = new ArrayList<String>(deskSet);
		
		//Iterate through all desks
		for (String deskName : deskList) {
			Desk desk = PortfolioTracker.deskInformation.get(deskName);
			//Get all traders
			HashMap<String, Trader> traders = desk.traders;
			Set<String> traderSet = traders.keySet();
			//Iterate through all traders
			for (String traderName : traderSet) {
				Trader trader = traders.get(traderName);
				//Get all books
				HashMap<String, Book> books = trader.books;
				Set<String> bookSet = books.keySet();
				//Iterate through all books
				for (String bookName : bookSet) {
					Book book = books.get(bookName);
					//Get all bonds
					HashMap<String, BondHeldByTrader> bonds = book.bonds;
					Set<String> bondSet = bonds.keySet();
					for (String bondID : bondSet) {
						BondHeldByTrader bond = bonds.get(bondID);
						String text = bond.desk + "," + bond.trader + "," + bond.book + "," + bondID + "," + 
										String.valueOf(bond.position) + "," + 
										Event.df.format(bond.NV) + "\n";
						outputText.add(text);
					}
				}
			}
		}
		
		Collections.sort(outputText);
		for (String text : outputText) {
			csvWriter.write(text);
		}
		
		csvWriter.close();
	}
	
	private void writeCurrencyLevelCSVFile() throws IOException {
		BufferedWriter csvWriter = new BufferedWriter(new FileWriter(
				new File(fileDirectory + "/currency_level_portfolio_" + latestID + ".csv"), false));
		csvWriter.write("Desk,Currency,Positions,NV\n");
		
		ArrayList<String> outputText = new ArrayList<String>();
		
		//Get all desks
		HashMap<String, Double> cashLevel = PortfolioTracker.deskCash;
		Set<String> deskSet = cashLevel.keySet();
		ArrayList<String> deskList = new ArrayList<String>(deskSet);
		
		//Iterate through all desks
		for (String deskName : deskList) {
			HashMap<String, Integer> currencyPosition = new HashMap<String, Integer>();
			HashMap<String, Double> currencyNV = new HashMap<String, Double>();
			Desk desk = PortfolioTracker.deskInformation.get(deskName);
			//Get all traders
			HashMap<String, Trader> traders = desk.traders;
			Set<String> traderSet = traders.keySet();
			//Iterate through all traders
			for (String traderName : traderSet) {
				Trader trader = traders.get(traderName);
				//Get all books
				HashMap<String, Book> books = trader.books;
				Set<String> bookSet = books.keySet();
				//Iterate through all books
				for (String bookName : bookSet) {
					Book book = books.get(bookName);
					//Get all bonds
					HashMap<String, BondHeldByTrader> bonds = book.bonds;
					Set<String> bondSet = bonds.keySet();
					for (String bondID : bondSet) {
						BondHeldByTrader bond = bonds.get(bondID);
						String currency = bond.currency;
						if (currencyPosition.get(currency) == null && currencyNV.get(currency) == null) {
							currencyPosition.put(currency, bond.position);
							currencyNV.put(currency, bond.NV);
						}
						else {
							int currPosition = currencyPosition.get(currency);
							double currNV = currencyNV.get(currency);
							currencyPosition.put(currency, bond.position + currPosition);
							currencyNV.put(currency, bond.NV + currNV);
						}
					}
				}
			}
			
			for(String ccy : currencyPosition.keySet()) {
				String text = deskName + "," + ccy + "," + String.valueOf(currencyPosition.get(ccy)) + "," + 
								Event.df.format(currencyNV.get(ccy)) + "\n";
				outputText.add(text);
			}
			
		}
		
		Collections.sort(outputText);
		for (String text : outputText) {
			csvWriter.write(text);
		}
		
		csvWriter.close();
	}
	
}

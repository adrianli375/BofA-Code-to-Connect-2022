import java.io.*;
import java.util.*;

/**
 * @author adrianli375
 * EventProcessor is the main object to process events from the JSON file.
 */
public class EventProcessor {
	
	/**
	 * Constructor of an EventProcessor object.
	 * @throws IOException
	 */
	EventProcessor() throws IOException {}

	/**
	 * Stores an list of Events.
	 */
	ArrayList<Event> eventList;
	
	/**
	 * Reads the JSON file and returns a processed JSON text.
	 * @return JSON text
	 */
	public String getTextFromJSON() {
		String JSONText = "";
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("input_data/events.json"));
			String line;
			
			while((line = reader.readLine()) != null) {
				JSONText += line + "\n";
			}
			
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSONText;
		
	}
	
	/**
	 * Reads the JSON text processed and returns a list of event texts.
	 * @param text JSON Text
	 * @return a list containing strings of event text.
	 */
	public ArrayList<String> parseText(String text) {
		ArrayList<String> eventsText = new ArrayList<String>();
		String[] entries = text.split("}");
		for(int i=0; i<entries.length; i++) {
			try {
				String eventTextStripped = entries[i].replaceAll(" ", "").replaceAll("\n", "").strip();
				String eventText = eventTextStripped.substring(eventTextStripped.indexOf("\""));
				//System.out.println(eventText);
				eventsText.add(eventText);
			}
			catch (Exception e) {};
		}
		return eventsText;
	}
	
	/**
	 * From the processed events text, process each event String and create a corresponding event object.
	 * @return An array list storing all the event objects
	 */
	public ArrayList<Event> processEvents() {
		String jsonText = getTextFromJSON();
		ArrayList<String> eventsText = parseText(jsonText);
		//System.out.println(eventsText.size());
		this.eventList = new ArrayList<Event>();
		for(String eventString : eventsText) {
			String[] eventEntries = eventString.split(",");
			int eventID = Integer.parseInt(eventEntries[0].substring(eventEntries[0].indexOf(":")+":".length()));
			//System.out.println(eventID);
			String eventType = eventEntries[1].substring(eventEntries[1].indexOf(":\"")+":\"".length(), eventEntries[1].length()-1);
			//System.out.println(eventType);
			
			if (eventType.equals("PriceEvent")) {
				String bondIDPriceEvent = eventEntries[2].substring(eventEntries[2].indexOf(":\"")+":\"".length(), eventEntries[2].length()-1);
				double marketPrice = Double.parseDouble(
						eventEntries[3].substring(eventEntries[3].indexOf(":")+":".length(), eventEntries[3].length()));
				PriceEvent pe = new PriceEvent(eventID, bondIDPriceEvent, marketPrice);
				this.eventList.add(pe);
			}
			
			else if (eventType.equals("TradeEvent")) {
				String desk = eventEntries[2].substring(eventEntries[2].indexOf(":\"")+":\"".length(), eventEntries[2].length()-1);
				String trader = eventEntries[3].substring(eventEntries[3].indexOf(":\"")+":\"".length(), eventEntries[3].length()-1);
				String book = eventEntries[4].substring(eventEntries[4].indexOf(":\"")+":\"".length(), eventEntries[4].length()-1);
				String buySell = eventEntries[5].substring(eventEntries[5].indexOf(":\"")+":\"".length(), eventEntries[5].length()-1);
				int quantity = Integer.parseInt(
						eventEntries[6].substring(eventEntries[6].indexOf(":")+":".length(), eventEntries[6].length()));
				String bondIDTradeEvent = eventEntries[7].substring(eventEntries[7].indexOf(":\"")+":\"".length(), eventEntries[7].length()-1);
				TradeEvent te = new TradeEvent(eventID, desk, trader, book, buySell, quantity, bondIDTradeEvent);
				this.eventList.add(te);
			}
				
			else if (eventType.equals("FXEvent")) {
				String currency = eventEntries[2].substring(eventEntries[2].indexOf(":\"")+":\"".length(), eventEntries[2].length()-1);
				double rate = Double.parseDouble(
						eventEntries[3].substring(eventEntries[3].indexOf(":")+":".length(), eventEntries[3].length()));
				FXEvent fe = new FXEvent(eventID, currency, rate);
				this.eventList.add(fe);
			}

		}
		return this.eventList;
	}
	
	/**
	 * From the list of events, get the necessary details to update different event objects
	 * @param eventList
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void processEvents(ArrayList<Event> eventList, TrackerFrame frame) throws InterruptedException, IOException {
		int eventCounter = 0;
		for (Event eve : eventList) {
			eventCounter++;
			frame.lblEventCounter.setText(String.valueOf(eventCounter));
			
			//Create report generator for each event
			ReportGenerator generator = new ReportGenerator(eventCounter);
			
			//Sleeps for a random time between 1 to 5 seconds.
			Random rand = new Random();
			int rand_time = rand.nextInt(4000) + 1000;
			Thread.sleep(rand_time);
			
			//Process the data from event objects and outputs to the market data producer.
			if (eve instanceof PriceEvent || eve instanceof FXEvent) {
				if (eve instanceof PriceEvent) {
					PriceEvent pe = (PriceEvent) eve;
					pe.updateBondPrice();
					pe.updateBondNV();
				}
				else {
					FXEvent fe = (FXEvent) eve;
					fe.updateFXPrice();
					fe.updateBondNV();
				}
				String existingText = frame.scrollPaneMarketDataText.getText();
				frame.scrollPaneMarketDataText.setText("[Event ID " + String.valueOf(eventCounter) + "] " + 
						eve.printToMarketData() + (!existingText.equals("") ? ("\n" + existingText) : ""));
				generator.writeExclusionCSVFile(eve, false);
			}
			
			//Process the data from event objects and outputs to the trade event data producer.
			else if (eve instanceof TradeEvent) {
				((TradeEvent) eve).processTradeEvent();
				String existingText = frame.scrollPaneTradeDataText.getText();
				if (!((TradeEvent) eve).exclusionRaised) {
					frame.scrollPaneTradeDataText.setText("[Event ID " + String.valueOf(eventCounter) + "] " + 
							eve.printToMarketData() + (!existingText.equals("") ? ("\n" + existingText) : ""));
					generator.writeExclusionCSVFile((TradeEvent) eve, false);
				}
				else {
					generator.writeExclusionCSVFile((TradeEvent) eve, true);
					
				}
			}
			
			generator.writeToCSVFiles();
			frame.showCashText();
			frame.showBondValueNAVText();
			
			PortfolioTracker.writeToLogFile(
					PortfolioTracker.getCurrentTime() + " Event ID " + eventCounter + " (type: " + eve.type + ") processed. \n", false);
			
//			System.out.println("Event ID " + eventCounter);
//			for (String ccy : PortfolioTracker.fxPrices.keySet()) {
//				System.out.println(ccy + ": " + String.valueOf(PortfolioTracker.fxPrices.get(ccy)));
//			}
		}
	}
	
}

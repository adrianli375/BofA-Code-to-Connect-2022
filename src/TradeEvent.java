import java.io.IOException;
import java.util.HashMap;

/**
 * @author adrianli375
 * TradeEvent is a subclass of event.
 */
public class TradeEvent extends Event {
	
	String desk;
	String trader;
	String book;
	String buySell;
	int quantity;
	String bondID;
	
	/**
	 * A boolean variable to indicate whether an exclusion is raised or not in the current trade event object.
	 */
	boolean exclusionRaised;
	/**
	 * Stores the type of exclusion, given that the boolean variable exclusionRaised is set to be true.
	 */
	String exclusionType;

	/**
	 * Constructor of a TradeEvent object.
	 * @param id the event ID
	 * @param desk the desk of the trade happening, e.g. NY
	 * @param trader the trader involved in the trade, e.g. T689954
	 * @param book the book involved in the trade, e.g. NY00
	 * @param buySell the type of order involved, it can be only "buy" or "sell"
	 * @param quantity the quantity of the order
	 * @param bondID the bond ID of the trade order, e.g. B41888
	 */
	TradeEvent(int id, String desk, String trader, String book, String buySell, int quantity, String bondID) {
		super(id);
		this.type = "Trade";
		this.desk = desk;
		this.trader = trader;
		this.book = book;
		this.buySell = buySell;
		this.quantity = quantity;
		this.bondID = bondID;
		this.exclusionType = null;
	}
	
	/**
	 * Check if the trade event raises an exclusion or not, if not, we open the trade and process it.
	 */
	public void processTradeEvent() {
		this.exclusionRaised = checkIfExclusionRaised();
		if(!this.exclusionRaised) {
			this.openTrade();
		}
//		else {
//			System.out.println("Event ID " + String.valueOf(this.id) + ", Exclusion raised: " + this.exclusionType);
//		}
	}
	
	private boolean checkIfExclusionRaised() {
		
		//condition 1: if the price of the bond is unavailable, return true, exclusionType = "NO_MARKET_PRICE"
		//System.out.println(PortfolioTracker.bondPrices.get(this.bondID));
		if (PortfolioTracker.bondPrices.get(this.bondID) == null) {
			this.exclusionType = "NO_MARKET_PRICE";
			return true;
		}
		
		//condition 2: if the cash of the desk is being exhausted, return true, exclusionType = "CASH_OVERLIMIT". (not finished)
		else if (buySell.equals("buy") && PortfolioTracker.deskCash.get(desk)
											< quantity * PortfolioTracker.bondPrices.get(this.bondID)) {
			this.exclusionType = "CASH_OVERLIMIT";
			return true;
		}
		
		//condition 3: first check if the trader exists or not.
		//if the bondID in the book does not exist, or quantity to be traded > no of positions of the bond in the book, 
		//return true, exclusionType = "QUANTITY_OVERLIMIT".
		else if (buySell.equals("sell")) {
			if (getBondToBeTraded(false) == null) {
				this.exclusionType = "QUANTITY_OVERLIMIT";
				return true;
			}
			else if (buySell.equals("sell") && getBondToBeTraded(false).position < this.quantity) {
				this.exclusionType = "QUANTITY_OVERLIMIT";
				return true;
			}
			else return false;
		}
		
		//otherwise, return false.
		else return false;
	}
	
	/**
	 * Gets the bond object to be traded which is involved in the current trade.
	 * @param trading is a boolean variable, if set to true, then trading procedures will also be processed.
	 * @return the BondHeldByTrader object
	 */
	public BondHeldByTrader getBondToBeTraded(boolean trading) {
		//From the given information, find the desk of the event.
		Desk currentDesk = PortfolioTracker.deskInformation.get(this.desk);
		
		//First, we check if the trader is inside the desk or not.
		//If the trader is not inside the desk, we add he/she to the desk.
		if (currentDesk.traders.get(this.trader) == null && trading) {
			currentDesk.addTrader(this.trader);
		}
		Trader currentTrader = currentDesk.traders.get(this.trader);
		
		//Next, we check if the book exists for the trader or not.
		//If the trader's book does not exist, we create a new book.
		Book currentBook = null;
		if (currentTrader != null) {
			if (currentTrader.books.get(this.book) == null && trading) {
				currentTrader.addBook(this.book);
			}
			currentBook = currentTrader.books.get(this.book);
		}
		
		//Then, we check if the bond to be purchased exists in the book or not.
		//If the bond does not exist in the book, we create a new position.
		if (currentBook != null) {
			if (currentBook.bonds.get(this.bondID) == null && trading) {
				currentBook.addBond(this.bondID, this.quantity);
			}
			return currentBook.bonds.get(this.bondID);
		}
		else return null;
	}
	
	/**
	 * Opens the trade and processes the trade event.
	 */
	public void openTrade() {
		BondHeldByTrader bondToBeTraded = getBondToBeTraded(true);
		String bondCurrency = PortfolioTracker.bondCurrencyDetails.get(this.bondID);
		System.out.println("Opening Trade. Currency: " + bondCurrency + "\n");
		double fxPrice = PortfolioTracker.fxPrices.get(bondCurrency);
		double amountTraded = this.quantity * PortfolioTracker.bondPrices.get(this.bondID) / fxPrice;
		//1. Update the FX price in the bond object.
		//2. Adjust amountTraded in desk.
		//3. Update NV accordingly (NV = Positions Held * Price)
		bondToBeTraded.fxRate = fxPrice;
		if (this.buySell.equals("buy")) {
			bondToBeTraded.position += this.quantity;
			PortfolioTracker.deskCash.put(desk, PortfolioTracker.deskCash.get(desk) - amountTraded);
		}
		else if (this.buySell.equals("sell")) {
			bondToBeTraded.position -= this.quantity;
			PortfolioTracker.deskCash.put(desk, PortfolioTracker.deskCash.get(desk) + amountTraded);
		}
		bondToBeTraded.NV = bondToBeTraded.position * PortfolioTracker.bondPrices.get(this.bondID) / fxPrice;
		//update NV accordingly. (NV = Quantity * Positions)
		System.out.println("Event ID: " + this.id);
		System.out.println("Bond: " + this.bondID + ", Price: " + PortfolioTracker.bondPrices.get(this.bondID));
		System.out.println("Quantity: " + this.quantity + ", Current Position: " + bondToBeTraded.position);
		System.out.println("Currency: " + PortfolioTracker.bondCurrencyDetails.get(this.bondID) + 
							", Price: " + PortfolioTracker.fxPrices.get(PortfolioTracker.bondCurrencyDetails.get(this.bondID)));
		System.out.println("Bond NV: " + df.format(bondToBeTraded.NV) + "\n");
		
		//Update the trading information to the corresponding desk
		Desk desk = PortfolioTracker.deskInformation.get(bondToBeTraded.desk);
		//get the corresponding desk, update the bondPosition hash map. 
		HashMap<String, Integer> deskBondPositions = desk.bondPosition;
		//If any bond price = 0, remove from the map
		if (deskBondPositions.containsValue(0)) {
			for (String bID : deskBondPositions.keySet()) {
				if (deskBondPositions.get(bID).equals(0)) {
					deskBondPositions.remove(bID);
				}
			}
		}
		//Get current bond value. If bondID does not exist, value = 0. Else get value.
		int bondPosition = 0;
		if (deskBondPositions.containsKey(this.bondID)) {
			//new value = old value + amount traded (if buy) OR old value - amount traded (if sell)
			bondPosition = deskBondPositions.get(this.bondID);
		}
		if (this.buySell.equals("buy")) {
			bondPosition += this.quantity;
		}
		else if (this.buySell.equals("sell") ) {
			bondPosition -= this.quantity;
		}
		//Put key-value pairs. Key = bondID, Value
		deskBondPositions.put(bondID, bondPosition);
	}
	
	/**
	 * Updates the latest trades in the trade event data dashboard. Also updates information in the log file.
	 * @throws IOException 
	 */
	public String printToMarketData() throws IOException {
		String logFileText = PortfolioTracker.getCurrentTime() + " Event ID " + String.valueOf(this.id) + ": " +
				(this.buySell.equals("buy") ? "Buy " : "Sell ")  + "order" + ", Trader ID: " + this.trader + 
				", Quantity: " + String.valueOf(this.quantity) + "\n";
		PortfolioTracker.writeToLogFile(logFileText, false);
		return (this.buySell.equals("buy") ? "Buy " : "Sell ") + "order: " + String.valueOf(this.bondID) + 
				", Price: " + PortfolioTracker.bondPrices.get(this.bondID) + ", Quantity: " + String.valueOf(this.quantity);
		
	}

}

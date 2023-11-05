import java.util.ArrayList;

/**
 * @author adrianli375
 * FXEvent is a subclass of Event.
 */
public class FXEvent extends Event {
	
	String currency;
	double rate;

	/**
	 * Constructor of an FXEvent object.
	 * @param id event ID
	 * @param currency currency, e.g. SGX
	 * @param rate rate, e.g. 7.75
	 */
	FXEvent(int id, String currency, double rate) {
		super(id);
		this.type = "FX";
		this.currency = currency;
		this.rate = rate;
	}
	
	/**
	 * Prints and updates text to the MarketData text box in the UI frame.
	 */
	public String printToMarketData() {
		return "FX EVENT: Currency " + this.currency + " is currently trading at " + 
				String.valueOf(df.format(this.rate)) + (!this.currency.equals("USX") ? " per USX" : "");
	}
	
	/**
	 * Updates the latest FX price in the portfolio tracker.
	 */
	public void updateFXPrice() {
		PortfolioTracker.fxPrices.put(this.currency, this.rate);
	}
	
	/**
	 * Updates the bond NV for all bonds involved.
	 */
	public void updateBondNV() {
		ArrayList<String> listOfBonds = PortfolioTracker.currencyBondsMap.get(currency);
		for (String desk : PortfolioTracker.deskInformation.keySet()) {
			Desk d = PortfolioTracker.deskInformation.get(desk);
			for (String trader : d.traders.keySet()) {
				Trader t = d.traders.get(trader);
				for (String book : t.books.keySet()) {
					Book b = t.books.get(book);
					for (String currentBond : b.bonds.keySet()) {
						BondHeldByTrader bond = b.bonds.get(currentBond);
						if (listOfBonds.contains(bond.bondID)) {
							bond.NV = bond.position * PortfolioTracker.bondPrices.get(bond.bondID) 
										/ PortfolioTracker.fxPrices.get(bond.currency);
						}
					}
				}
			}
		}
	}

}

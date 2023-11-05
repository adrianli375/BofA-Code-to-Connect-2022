import java.util.ArrayList;

/**
 * @author adrianli375
 * Price event is a subclass of event.
 */
public class PriceEvent extends Event {
	
	String bondID;
	double marketPrice;

	/**
	 * Constructor of a PriceEvent object.
	 * @param id the event ID
	 * @param bondID the bond ID, e.g. B41888
	 * @param marketPrice market price of the bond
	 */
	PriceEvent(int id, String bondID, double marketPrice) {
		super(id);
		this.type = "Price";
		this.bondID = bondID;
		this.marketPrice = marketPrice;
	}
	
	/**
	 * Prints and updates text to the MarketData text box in the UI frame.
	 */
	public String printToMarketData() {
		return "PRICE EVENT: Bond " + this.bondID + " is currently trading at " + 
				String.valueOf(Event.df.format(marketPrice)) + " " + PortfolioTracker.bondCurrencyDetails.get(bondID);
	}
	
	/**
	 * Updates the latest FX price in the portfolio tracker.
	 */
	public void updateBondPrice() {
		PortfolioTracker.bondPrices.put(this.bondID, this.marketPrice);
	}
	
	/**
	 * Updates the bond NV for all bonds involved.
	 */
	public void updateBondNV() {
		for (String desk : PortfolioTracker.deskInformation.keySet()) {
			Desk d = PortfolioTracker.deskInformation.get(desk);
			for (String trader : d.traders.keySet()) {
				Trader t = d.traders.get(trader);
				for (String book : t.books.keySet()) {
					Book b = t.books.get(book);
					for (String currentBond : b.bonds.keySet()) {
						BondHeldByTrader bond = b.bonds.get(currentBond);
						if (this.bondID.equals(bond.bondID)) {
							bond.NV = bond.position * PortfolioTracker.bondPrices.get(bond.bondID) 
									/ PortfolioTracker.fxPrices.get(bond.currency);
						}
					}
				}
			}
		}
	}

}

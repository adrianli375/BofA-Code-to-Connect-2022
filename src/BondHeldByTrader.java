
/**
 * @author adrianli375
 * BondHeldByTrader stores the details of the bond held by a trader
 */
public class BondHeldByTrader {
	String bondID, book, trader, desk;
	int position;
	/**
	 * currency: the type of the currency of the bond.
	 */
	String currency;
	/**
	 * NV: the NV of the bond held, calculated using the formula given in the handout.
	 */
	double NV;
	/**
	 * fxRate: given the ID of the bond, find the corresponding currency, thus the fxRate.
	 */
	double fxRate;
	/**
	 * price: given the ID of the bond, find the corresponding price as at the event ID.
	 */
	double price;
	
	/**
	 * Default constructor of a BondHeldByTrader object.
	 * @param bondID the bond ID, e.g. B41888
	 * @param book the book of the trader, e.g. NY00
	 * @param trader the trader ID, e.g. T6899554
	 * @param desk the desk of the trader, e.g. NY
	 * @param position the number of bonds to be bought or sold
	 */
	BondHeldByTrader(String bondID, String book, String trader, String desk, int position) {
		this.bondID = bondID;
		this.book = book;
		this.trader = trader;
		this.desk = desk;
		this.position = 0;
		this.NV = 0;
		this.currency = PortfolioTracker.bondCurrencyDetails.get(this.bondID);
		this.price = PortfolioTracker.bondPrices.get(this.bondID);
	}
	
	
}

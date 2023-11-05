import java.util.*;

/**
 * @author adrianli375
 * Desk object stores transactions/events involved in the tracker.
 */
public class Desk {
	String name;
	/**
	 * The HashMap traders stores the details of each trader in a desk.
	 */
	HashMap<String, Trader> traders;
	/**
	 * The HashMap bondPosition stores the details of the total positions of each bond in a desk.
	 */
	HashMap<String, Integer> bondPosition;
	
	/**
	 * Constructor of a desk object.
	 * @param name the name of the desk, e.g. NY
	 */
	Desk(String name) {
		this.name = name;
		this.traders = new HashMap<String, Trader>();
		this.bondPosition = new HashMap<String, Integer>();
		//this.currencyPosition = new HashMap<String, Double>();
	}
	
	/**
	 * Adds a trader object to the desk.
	 * @param trader the new trader object
	 */
	public void addTrader(String trader) {
		traders.put(trader, new Trader(trader, name));
	}
	
	
}

import java.util.*;

/**
 * @author adrianli375
 * Book object stores the details of each book owned by traders.
 */
public class Book {
	String name, trader, desk;
	/**
	 * The HashMap bonds stores details of each individual bond held in a trader's book.
	 */
	HashMap<String, BondHeldByTrader> bonds;
	
	/**
	 * Constructor of a book object.
	 * @param name the book of the trader, e.g. NY00
	 * @param trader the trader ID, e.g. T6899554
	 * @param desk the desk of the trader, e.g. NY
	 */
	Book(String name, String trader, String desk) {
		this.name = name;
		this.trader = trader;
		this.desk = desk;
		this.bonds = new HashMap<String, BondHeldByTrader>();
	}
	
	/**
	 * Adds a BondHeldByTrader object to the book.
	 * @param bondID the bond ID, e.g. B41888
	 * @param position the number of bonds to be bought or sold
	 */
	public void addBond(String bondID, int position) {
		this.bonds.put(bondID, new BondHeldByTrader(bondID, this.name, this.trader, this.desk, position));
	}
	
	/**
	 * Calculates the total positions of the trader in the book.
	 * @return the total long positions held by the trader.
	 */
	public int getTotalPositions() {
		Set<String> bondIDs = bonds.keySet();
		int position = 0;
		
		for (String bID : bondIDs) {
			BondHeldByTrader bond = bonds.get(bID);
			position += bond.position;
		}
		
		return position;
	}
	
	/**
	 * Calculates the total NV of the trader in the book.
	 * @return the total bond NV of the trader.
	 */
	public double getTotalNV() {
		Set<String> bondIDs = bonds.keySet();
		float totalNV = 0;
		
		for (String bID : bondIDs) {
			BondHeldByTrader bond = bonds.get(bID);
			totalNV += bond.NV;
		}
		
		return totalNV;
	}
	
}

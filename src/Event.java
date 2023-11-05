import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @author adrianli375
 * Abstract class of an event object.
 */
public abstract class Event {
	
	int id;
	/**
	 * type is the type of the event. It can be either "FX", "Price" or "Trade".
	 */
	String type;
	
	/**
	 * df is the DecimalFormat used when processing outputs to the UI and to the csv files.
	 */
	protected static final DecimalFormat df = new DecimalFormat("0.00");
	
	/**
	 * Constructor of an event object.
	 * @param id the event ID number
	 */
	Event(int id) {
		this.id = id;
	}
	
	/**
	 * Prints suitable text to Market Data/Trade Event Data text box.
	 * @return
	 * @throws IOException 
	 */
	public String printToMarketData() throws IOException {
		return "";
	}
	
}

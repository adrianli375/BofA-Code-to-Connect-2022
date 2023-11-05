import java.util.*;

/**
 * @author adrianli375
 * Trader object stores the details of a trader.
 */
public class Trader {
	String name, desk;
	/**
	 * The HashMap bonds stores details of each individual book held by a trader.
	 */
	HashMap<String, Book> books;
	
	/**
	 * Constructor for a trader object.
	 * @param name the ID of the trader, e.g. T6899554
	 * @param desk the desk of the trader involved, e.g. NY
	 */
	Trader(String name, String desk) {
		this.name = name;
		this.desk = desk;
		this.books = new HashMap<String, Book>();
	}
	
	/**
	 * Adds a book object to the trader.
	 * @param book the name of the book to be added, e.g. NY00
	 */
	public void addBook(String book) {
		books.put(book, new Book(book, name, desk));
	}
	
}

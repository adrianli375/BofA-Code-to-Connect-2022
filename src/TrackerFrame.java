import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;

/**
 * @author adrianli375
 * TrackerFrame object is the main UI interface.
 */
public class TrackerFrame extends JFrame {

	private JPanel contentPane;
	/**
	 * The label event counter, e.g. 100
	 */
	JLabel lblEventCounter;
	/**
	 * The text involved in the market data of the dashboard.
	 */
	JTextArea scrollPaneMarketDataText;
	/**
	 * The text involved in the trade data of the dashboard.
	 */
	JTextArea scrollPaneTradeDataText;
	/**
	 * The currently selected desk in the portfolio engine of the dashboard.
	 */
	String selectedDesk;
	/**
	 * The combo box which allow users to choose the desks available.
	 */
	JComboBox<String> comboBoxDesk;
	/**
	 * The label which shows "Bond ID" in the dashboard.
	 */
	JLabel lblBondID;
	/**
	 * The label which shows the list of bond IDs for the desk in the dashboard.
	 */
	JLabel lblBondIDText;
	/**
	 * The label which shows "Base Currency" in the dashboard.
	 */
	JLabel lblBondCurrency;
	/**
	 * The label which shows the list of base currencies of the corresponding bond IDs for the desk in the dashboard.
	 */
	JLabel lblBondCurrencyText;
	/**
	 * The label which shows "Bond Value" in the dashboard.
	 */
	JLabel lblBondValue;
	/**
	 * The label which shows the list of bond values of the corresponding bond IDs for the desk in the dashboard.
	 */
	JLabel lblBondValueText;
	/**
	 * The label which shows the text "Cash Available: " in the dashboard.
	 */
	JLabel lblCashAvailableLabel;
	/**
	 * The label which shows the amount of cash available for the desk in the dashboard.
	 */
	JLabel lblCashLabel;
	/**
	 * The label which shows the percentage change of cash available for the desk in the dashboard.
	 */
	JLabel lblCashpctChangeLabel;
	/**
	 * The label which stores the text "Net Asset Value (NAV): " in the dashboard.
	 */
	JLabel lblNAVTextLabel;
	/**
	 * The label which stores the amount of NAV for the desk. It is the sum of the cash available and the bond value.
	 */
	JLabel lblNAVLabel;
	/**
	 * The label which shows the percentage change of cash available for the desk in the dashboard.
	 */
	JLabel lblNAVpctChangeLabel;
	/**
	 * The map which stores the information of the previous cash balance available for each desk.
	 */
	HashMap<String, Double> initialCashBalance;

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("unchecked")
	public TrackerFrame() {
		setTitle("Portfolio Tracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1080, 960);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblMarketData = new JLabel("Market Data");
		lblMarketData.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblMarketData.setBounds(279, 35, 108, 16);
		contentPane.add(lblMarketData);
		
		JLabel lblTradeEventData = new JLabel("Trade Event Data");
		lblTradeEventData.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblTradeEventData.setBounds(272, 387, 148, 16);
		contentPane.add(lblTradeEventData);
		
		JLabel lblPortfolioEngine = new JLabel("Portfolio Engine");
		lblPortfolioEngine.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblPortfolioEngine.setBounds(798, 35, 131, 16);
		contentPane.add(lblPortfolioEngine);
		
		JLabel lblDashboard = new JLabel("Dashboard");
		lblDashboard.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblDashboard.setBounds(497, 7, 91, 16);
		contentPane.add(lblDashboard);
		
		JLabel lblEvent = new JLabel("Event: ");
		lblEvent.setBounds(21, 8, 42, 16);
		contentPane.add(lblEvent);
		
		this.lblEventCounter = new JLabel("0");
		this.lblEventCounter.setBounds(63, 8, 42, 16);
		contentPane.add(this.lblEventCounter);
		
		this.scrollPaneMarketDataText = new JTextArea();
		this.scrollPaneMarketDataText.setText("");
		
		JScrollPane scrollPaneMarketData = new JScrollPane(this.scrollPaneMarketDataText);
		scrollPaneMarketData.setBounds(48, 64, 561, 291);
		scrollPaneMarketData.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPaneMarketData);
		
		this.scrollPaneTradeDataText = new JTextArea();
		this.scrollPaneTradeDataText.setText("");
		
		JScrollPane scrollPaneTradeData = new JScrollPane(this.scrollPaneTradeDataText);
		scrollPaneTradeData.setBounds(48, 415, 561, 291);
		scrollPaneTradeData.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPaneTradeData);
		
		JLabel lblChooseDeskLabel = new JLabel("Choose Desk: ");
		lblChooseDeskLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblChooseDeskLabel.setBounds(683, 65, 117, 16);
		contentPane.add(lblChooseDeskLabel);
		
		Set<String> deskSet = PortfolioTracker.deskCash.keySet();
		ArrayList<String> deskList = new ArrayList<String>();
		deskList.addAll(deskSet);
		Collections.sort(deskList);
		deskList.add(0, null);
		String[] deskArray = new String[deskList.size()];
		deskList.toArray(deskArray);
		this.comboBoxDesk = new JComboBox(deskArray);
		this.comboBoxDesk.setBounds(832, 62, 117, 27);
		contentPane.add(this.comboBoxDesk);
		
		this.lblBondID = new JLabel("Bond ID");
		this.lblBondID.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		this.lblBondID.setVisible(false);
		this.lblBondID.setBounds(723, 148, 57, 27);
		contentPane.add(this.lblBondID);
		
		this.lblBondIDText = new JLabel("");
		this.lblBondIDText.setVerticalAlignment(SwingConstants.TOP);
		this.lblBondIDText.setBounds(713, 181, 67, 572);
		contentPane.add(this.lblBondIDText);
		
		this.lblBondCurrency = new JLabel("Base Currency");
		this.lblBondCurrency.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		this.lblBondCurrency.setVisible(false);
		this.lblBondCurrency.setBounds(807, 150, 107, 22);
		contentPane.add(this.lblBondCurrency);
		
		this.lblBondCurrencyText = new JLabel("");
		this.lblBondCurrencyText.setVerticalAlignment(SwingConstants.TOP);
		this.lblBondCurrencyText.setBounds(817, 181, 86, 572);
		contentPane.add(this.lblBondCurrencyText);
		
		this.lblBondValue = new JLabel("Bond Value");
		this.lblBondValue.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		this.lblBondValue.setVisible(false);
		this.lblBondValue.setBounds(926, 153, 81, 16);
		contentPane.add(this.lblBondValue);
		
		this.lblBondValueText = new JLabel("");
		this.lblBondValueText.setVerticalAlignment(SwingConstants.TOP);
		this.lblBondValueText.setBounds(921, 181, 86, 572);
		contentPane.add(this.lblBondValueText);
		
		this.initialCashBalance = (HashMap<String, Double>) PortfolioTracker.deskCash.clone();
		
		this.lblCashAvailableLabel = new JLabel("Cash Available: ");
		this.lblCashAvailableLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		this.lblCashAvailableLabel.setVisible(false);
		this.lblCashAvailableLabel.setBounds(683, 120, 131, 16);
		contentPane.add(this.lblCashAvailableLabel);
		
		this.lblCashLabel = new JLabel("");
		this.lblCashLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		this.lblCashLabel.setVisible(false);
		this.lblCashLabel.setBounds(863, 120, 131, 16);
		contentPane.add(this.lblCashLabel);
		
		this.lblCashpctChangeLabel = new JLabel("(0.00%)");
		this.lblCashpctChangeLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		this.lblCashpctChangeLabel.setVisible(false);
		this.lblCashpctChangeLabel.setBounds(993, 120, 74, 16);
		contentPane.add(this.lblCashpctChangeLabel);
		
		this.lblNAVTextLabel = new JLabel("Net Asset Value (NAV): ");
		this.lblNAVTextLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		this.lblNAVTextLabel.setVisible(false);
		this.lblNAVTextLabel.setBounds(683, 93, 185, 16);
		contentPane.add(this.lblNAVTextLabel);
		
		this.lblNAVLabel = new JLabel("");
		this.lblNAVLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		this.lblNAVLabel.setVisible(false);
		this.lblNAVLabel.setBounds(863, 93, 131, 16);
		contentPane.add(this.lblNAVLabel);
		
		this.lblNAVpctChangeLabel = new JLabel("(0.00%)");
		this.lblNAVpctChangeLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		this.lblNAVpctChangeLabel.setVisible(false);
		this.lblNAVpctChangeLabel.setBounds(993, 94, 74, 16);
		contentPane.add(this.lblNAVpctChangeLabel);
		
		ActionListener updateDashboardText = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCashText();
				showBondValueNAVText();
			}
		};
		
		comboBoxDesk.addActionListener(updateDashboardText);
		
	}
	
	/**
	 * Get the cash text.
	 * @param deskName the name of the desk, e.g. NY
	 * @param previousCashLabelText the existing cash label text, e.g. $100000000.00
	 * @param pctChangeLabel the cash percentage change JLabel object in the UI dashboard
	 * @return the cash text to be shown in the UI
	 */
	public String[] getCashText(String deskName, String previousCashLabelText, JLabel cashpctChangeLabel) {
		String[] outputText = new String[2];
		
		double initialCashAvailable = this.initialCashBalance.get(deskName);
		double cashAvailable = PortfolioTracker.deskCash.get(deskName);
		outputText[0] = "$" + Event.df.format(cashAvailable);
		
		//Percentage change refers to the % change between the current cash balance and the initial cash balance 
		//(before the start of event 1)
		double pctChange = (cashAvailable - initialCashAvailable) / initialCashAvailable * 100;
		if (pctChange == 0) {
			outputText[1] = "(0.00%)";
			cashpctChangeLabel.setForeground(Color.GRAY);
		}
		else {
			outputText[1] = "(" + (pctChange > 0 ? "+" : "") + Event.df.format(pctChange) + "%)";
			cashpctChangeLabel.setForeground(pctChange > 0 ? Color.GREEN : Color.RED);
		}
		
		return outputText;
	}
	
	/**
	 * Shows the cash text, which contains the amount of cash available in the desk and the respective percentage change. <br>
	 * It will be shown upon refresh or when the program proceeds to the next event.
	 */
	public void showCashText() {
		String selectedDeskName = (String) comboBoxDesk.getSelectedItem();
		
		if (selectedDeskName == null) {
			lblCashAvailableLabel.setVisible(false);
			lblCashLabel.setVisible(false);
			lblCashpctChangeLabel.setVisible(false);
		}
		else {
			lblCashAvailableLabel.setVisible(true);
			lblCashLabel.setVisible(true);
			lblCashpctChangeLabel.setVisible(true);
			
			String[] cashText = getCashText(selectedDeskName, lblCashLabel.getText(), lblCashpctChangeLabel);
			lblCashLabel.setText(cashText[0]);
			lblCashpctChangeLabel.setText(cashText[1]);
		}
	}
	
	/**
	 * Get the bond table text, and also the NAV text.
	 * @param desk the current desk object
	 * @param navpctChangeLabel the label storing the % change of the NAV
	 * @return the bond table text to be shown in the UI
	 */
	public String[] getBondValueNAVText(Desk desk, JLabel navpctChangeLabel) {
		HashMap<String, Integer> deskBondPosition = desk.bondPosition;
		Set<String> bondKeySet = deskBondPosition.keySet();
		ArrayList<String> bondIDList = new ArrayList<String>(bondKeySet);
		Collections.sort(bondIDList);
		Iterator<String> iter = bondIDList.iterator();
		String[] outputText = new String[5];
		
		double totalBondValue = 0;
		outputText[0] = "";
		outputText[1] = "";
		outputText[2] = "";
		
		while (iter.hasNext()) {
			String bondID = iter.next();
			String bondCurrency = PortfolioTracker.bondCurrencyDetails.get(bondID);
			double bondValue = deskBondPosition.get(bondID) * PortfolioTracker.bondPrices.get(bondID) 
								/ PortfolioTracker.fxPrices.get(bondCurrency);
			String roundedBondValue = Event.df.format(bondValue);
			totalBondValue += bondValue;
			//System.out.println(bondID);
			//System.out.println(roundedBondValue);
			if (bondID != null) {
				outputText[0] += bondID + "<br>";
			}
			outputText[1] += bondCurrency + "<br>";
			outputText[2] += roundedBondValue + "<br>";
		}
		
		//Calculation of the total NAV: Sum of the total bond value and the cash available on the desk. 
		double cashAvailable = PortfolioTracker.deskCash.get(desk.name);
		double totalNAV = totalBondValue + cashAvailable;
		
		//Percentage change refers to the % change between the NAV and the initial cash balance 
		//(before the start of event 1)
		double initialCashAvailable = this.initialCashBalance.get(desk.name);
		double pctChange = (totalNAV - initialCashAvailable) / initialCashAvailable * 100;
		if (pctChange == 0) {
			outputText[4] = "(0.00%)";
			navpctChangeLabel.setForeground(Color.GRAY);
		}
		else {
			outputText[4] = "(" + (pctChange > 0 ? "+" : "") + Event.df.format(pctChange) + "%)";
			navpctChangeLabel.setForeground(pctChange > 0 ? Color.GREEN : Color.RED);
		}
		
		outputText[0] = "<html>" + outputText[0] + "</html>";
		outputText[1] = "<html>" + outputText[1] + "</html>";
		outputText[2] = "<html>" + outputText[2] + "</html>";
		outputText[3] = "$" + Event.df.format(totalNAV);
		
		return outputText;
	}
	
	/**
	 * Shows the bond table and NAV text, which contains the bond ID, base currency and NV. <br>
	 * It will be shown upon refresh or when the program proceeds to the next event.
	 */
	public void showBondValueNAVText() {
		String selectedDeskName = (String) comboBoxDesk.getSelectedItem();
		//System.out.println(selectedDeskName);
		if (selectedDeskName == null) {
			lblBondID.setVisible(false);
			lblBondCurrency.setVisible(false);
			lblBondValue.setVisible(false);
			lblBondIDText.setVisible(false);
			lblBondCurrencyText.setVisible(false);
			lblBondValueText.setVisible(false);
			lblNAVTextLabel.setVisible(false);
			lblNAVLabel.setVisible(false);
			lblNAVpctChangeLabel.setVisible(false);
		}
		else {
			lblBondID.setVisible(true);
			lblBondCurrency.setVisible(true);
			lblBondValue.setVisible(true);
			lblNAVTextLabel.setVisible(true);
			//set bond ID, bond currency, bond value text, NAV text and NAV % change text
			Desk selectedDesk = PortfolioTracker.deskInformation.get(selectedDeskName);
			String[] bondNAVText = getBondValueNAVText(selectedDesk, this.lblNAVpctChangeLabel);
			lblBondIDText.setVisible(true);
			lblBondCurrencyText.setVisible(true);
			lblBondValueText.setVisible(true);
			lblNAVLabel.setVisible(true);
			lblNAVpctChangeLabel.setVisible(true);
			lblBondIDText.setText(bondNAVText[0]);
			lblBondCurrencyText.setText(bondNAVText[1]);
			lblBondValueText.setText(bondNAVText[2]);
			lblNAVLabel.setText(bondNAVText[3]);
			lblNAVpctChangeLabel.setText(bondNAVText[4]);
		}
	}
}

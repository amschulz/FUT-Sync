package Card;

import framework.Card;

public class CardImplementation implements Card {

	private int id;
	private	int lowest;
	private int average;
	private String lastUpdated;
	
	private CardImplementation() {
		// TODO Auto-generated constructor stub
	}
	
	public CardImplementation(int id, int lowest, int average, String lastUpdated) {
		this.id = id;
		this.lowest = lowest;
		this.average = average;
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String getName() {
		return "Kein Name";
	}

	@Override
	public String getLastUpdate() {
		return this.lastUpdated;
	}

	@Override
	public int getLowestPrice() {
		return lowest;
	}

	@Override
	public int getAveragePrice() {
		return average;
	}

}

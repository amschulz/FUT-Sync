package Card;

import framework.Card;

public class CardImplementation implements Card {

	private long id;
	private	long lowest;
	private long average;
	private String lastUpdated;
	private String lastUpdatedScript;
	private String name;
	
	public CardImplementation(long id, long lowest, long average, String lastUpdated, String lastUpdatedScript) {
		this.id = id;
		this.lowest = lowest;
		this.average = average;
		this.lastUpdated = lastUpdated;
		this.lastUpdatedScript = lastUpdatedScript;
		this.name = "";
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getLastUpdate() {
		return this.lastUpdated;
	}

	@Override
	public long getLowestPrice() {
		return lowest;
	}

	@Override
	public long getAveragePrice() {
		return average;
	}

	@Override
	public String getLastUpdateScript() {
		return lastUpdatedScript;
	}

	@Override
	public long getCardId() {
		return id;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
}

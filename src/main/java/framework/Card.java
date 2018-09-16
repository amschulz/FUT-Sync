package framework;

public interface Card {
	public long getCardId();
	public long getLowestPrice();
	public long getAveragePrice();
	public String getName();
	public String getLastUpdate();
	public String getLastUpdateScript();
	public void setName(String name);
}

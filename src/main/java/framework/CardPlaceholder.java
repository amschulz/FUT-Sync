package framework;

public interface CardPlaceholder {
	
	public long getCardId();
	public String getPlayerName();
	public void setCurrentPrice(long currentPrice);
	public void setCurrentPrice(long currentPrice, String lastUpdated);
}

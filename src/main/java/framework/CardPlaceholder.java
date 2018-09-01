package framework;

public interface CardPlaceholder {
	
	public int getCardId();
	public String getPlayerName();
	public void setCurrentPrice(int currentPrice);
	public void setCurrentPrice(int currentPrice, String lastUpdated);
}

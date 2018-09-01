package framework;


public interface PriceProvider {
	
	public int getPriceOfPlayer(int playerId, int year);
	public Card getExtendedPriceOfPlayer(int playerId, int year);
}

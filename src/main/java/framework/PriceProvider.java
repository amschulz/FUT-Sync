package framework;


public interface PriceProvider {
	
	public long getPriceOfPlayer(long playerId, int year);
	public Card getExtendedPriceOfPlayer(long playerId, int year);
}

package PriceProvider.FutHead;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import Card.CardImplementation;
import framework.Card;
import framework.CardPlaceholder;

public class FutHead implements framework.PriceProvider {
	
	private static String url = "https://www.futhead.com/prices/api/";

	public FutHead() {}

	@Override
	public int getPriceOfPlayer(int cardId, int fifaVersion) {
		JSONObject jsonobj = getPrice(cardId, fifaVersion);
		try{
			JSONObject props = (jsonobj.getJSONObject(new Integer(cardId).toString()));
			JSONArray arr = props.getJSONArray("psLowFive");
			return arr.getInt(0) ;
		} catch (org.json.JSONException e){
			return 0;
		}
	}

	@Override
	public Card getExtendedPriceOfPlayer(int cardId, int fifaVersion) {
		JSONObject jsonobj = getPrice(cardId, fifaVersion);
		JSONObject props = null;
		try {
			props = (jsonobj.getJSONObject(new Integer(cardId).toString()));
		} catch (org.json.JSONException e){
			return null;
		}
		
		long dt = props.getLong("psTime");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastUpdated = dateFormat.format(dt);
		int average = props.getInt("psAvg");
		int lowest = props.getInt("ps");
		
		Card c = new CardImplementation(cardId, average, lowest, lastUpdated);
		return c;
	}
	
	private JSONObject getPrice(int cardId, int fifaVersion) {
		Client client = ClientBuilder.newClient();
		// https://www.futhead.com/prices/api/?year=18&id=67129665
		WebTarget target = ((WebTarget) client.target(url)
		        .queryParam("year", new Integer(fifaVersion).toString())
		        .queryParam("id", new Integer(cardId).toString()));
		Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
		String s = builder.get(String.class); 
		return new JSONObject(s);		
	}

}

import PriceProvider.FutHead.FutHead;
import CardProvider.GoogleSheets.GoogleSheetsList;
import framework.Card;
import framework.PriceProvider;
import framework.CardList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

public class Starter {
	
	private static int fifaVersion;

	/**
	 * Main method which starts the programm.
	 * 
	 * IMPORTANT: To make it work you have to add the following command-line option.
	 * --add-modules java.xml.bind
	 * In Eclipse: Tab "Run" -> "Run configurations" -> Choose the configuration -> Tab "Arguments" -> Insert into field "VM Arguments"
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		String timeStamp;
		
        String jsoninput = null;
		try {
			jsoninput = readFile("src\\main\\java\\config\\ProgrammConfiguration.json");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        JSONObject jsonObject = new JSONObject(jsoninput);
        int waitInMinutes = Integer.parseInt(jsonObject.getString("wait_after_batch"));
        fifaVersion = Integer.parseInt(jsonObject.getString("fifa_version"));
		
		PriceProvider futhead = new FutHead();
		while(true){
			timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			System.out.println(timeStamp + " Starting to update transferlist.");
			CardList transferlist = new GoogleSheetsList();
			for(Card c: transferlist){
				int lowestPrice = futhead.getPriceOfPlayer(c.getCardId(), fifaVersion);
				c.setCurrentPrice(lowestPrice);
			}
			timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			System.out.println(timeStamp + " Waiting for " + waitInMinutes + " minutes.");
			try {
				TimeUnit.MINUTES.sleep(waitInMinutes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
    private static String readFile(String path) throws IOException {
    	BufferedReader reader = new BufferedReader(new FileReader(path));
    	StringBuilder stringBuilder = new StringBuilder();
    	String line = null;
    	String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
    	// delete the last new line separator
    	stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    	reader.close();

    	return stringBuilder.toString();
    }
}

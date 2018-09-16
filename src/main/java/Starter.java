import PriceProvider.FutHead.FutHead;
import View.DefaultView;
import CardProvider.GoogleSheets.GoogleSheetsList;
import framework.Card;
import framework.CardPlaceholder;
import framework.PriceProvider;
import framework.View;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import framework.CardPlaceholderList;
import framework.Controller;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import javafx.application.Application;

public class Starter extends Application  implements Controller {
	
	private static Controller controller;
	private View view;
	private static PriceProvider futhead;
	private static int fifaVersion;
	private static int waitInMinutes;
	private boolean stopSynchronization;
	
	public Starter(){};

	/**
	 * Main method which starts the programm.
	 * 
	 * IMPORTANT: To make it work you have to add the following command-line option.
	 * --add-modules java.xml.bind
	 * In Eclipse: Tab "Run" -> "Run configurations" -> Choose the configuration -> Tab "Arguments" -> Insert into field "VM Arguments"
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		controller = new Starter();
		futhead = new FutHead();
		String jsoninput = null;
		try {
			jsoninput = readFile("src\\main\\java\\config\\ProgrammConfiguration.json");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        JSONObject jsonObject = new JSONObject(jsoninput);
        waitInMinutes = Integer.parseInt(jsonObject.getString("wait_after_batch"));
        fifaVersion = Integer.parseInt(jsonObject.getString("fifa_version"));
        launch(args);
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
    
    @Override
	public void start(Stage primaryStage) throws Exception {
		new DefaultView(controller, primaryStage);
	}
	
	public void startSynchronization() {
		stopSynchronization = false;
		String timeStamp;
		timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println(timeStamp + " Starting to update transferlist.");
		CardPlaceholderList transferlist = new GoogleSheetsList();
		this.view.setAmountOfCards(transferlist.size());
		for(CardPlaceholder c: transferlist){
			// System.out.println(c.getCardId());
			if (stopSynchronization) {
				return;
			}
			Card card = futhead.getExtendedPriceOfPlayer(c.getCardId(), fifaVersion);
			if (card == null) {
				continue;
			}
			card.setName(c.getPlayerName());
			c.setCurrentPrice(card.getLowestPrice(), card.getLastUpdate());
			this.view.addCard(card);
		}
		timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println(timeStamp + " Waiting for " + waitInMinutes + " minutes.");
	}

	@Override
	public void setView(View view) {
		this.view = view;
	}

	@Override
	public void run() {
		startSynchronization();
	}

	@Override
	public void setExit() {
		stopSynchronization = true;		
	}
}

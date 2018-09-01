package CardProvider.GoogleSheets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import framework.Card;
import framework.CardPlaceholder;
import framework.CardPlaceholderList;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GoogleSheetsList extends LinkedList<CardPlaceholder> implements CardPlaceholderList {
	
	private Sheets service;
	
    private final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private String credentialsFolder = null; // Directory to store user credentials.

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved credentials/ folder.
     */
    private final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private String clientSecretDir;
    
    private String spreadsheetId;
    
    private String tabName;
    private String columnCardName;
    private String columnCardId;
    private String columnPrice;
 
	
	public GoogleSheetsList(){
		
        String jsoninput = null;
		try {
			jsoninput = this.readFile("src\\main\\java\\CardProvider\\GoogleSheets\\config\\Configuration.json");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        JSONObject jsonObject = new JSONObject(jsoninput);
        clientSecretDir = jsonObject.getString("client_secret_dir");
        credentialsFolder = jsonObject.getString("credentials_folder");
        spreadsheetId = jsonObject.getString("spreadsheet_id");
        tabName = jsonObject.getString("tab_name");
        columnCardName = jsonObject.getString("column_card_name");
        columnCardId = jsonObject.getString("column_card_id");
        columnPrice = jsonObject.getString("column_card_price");
        
		List<List<Object>> values = null;
		try {
			values = this.getValues();
		} catch (GeneralSecurityException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {
            	if (row.size() == 1){
            		String name = (String) row.get(0);
            		CardPlaceholder c = new CardImplementation(-1, name);
            		this.add(c);
            	}
            	if(row.size() == 2){
            		String name = (String) row.get(0);
            		int id = Integer.parseInt(((String)row.get(1)));
            		CardPlaceholder c = new CardImplementation(id, name);
            		this.add(c);
            	}
            }
        }
	}
	
	private Sheets getService() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        if(this.service == null){
        	this.service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
        return this.service;
	}
	
	private List<List<Object>> getValues() throws GeneralSecurityException, IOException {
        String range = tabName + "!" + columnCardName + ":" + columnCardId;
		Sheets service = this.getService();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues();
	}
	
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        //InputStream in = GoogleSheetsList.class.getResourceAsStream(CLIENT_SECRET_DIR);
    	InputStream in = new FileInputStream(clientSecretDir);  
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        File f = new java.io.File(credentialsFolder);
        
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(f))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private void setPriceOfCard(CardPlaceholder cardPlaceholder, int value){
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
		setPriceOfCard(cardPlaceholder, value, timeStamp);
    }
    
    private void setPriceOfCard(CardPlaceholder cardPlaceholder, int value, String lastUpdated){
    	Integer rownumber = new Integer(this.indexOf(cardPlaceholder) + 1);
        String range = tabName + "!" + columnPrice + rownumber.toString();
		Sheets service = null;
		List<ValueRange> values;
		ValueRange v = new ValueRange()
				.setValues(Arrays.asList(Arrays.asList(value, lastUpdated)));
		
		try {
			TimeUnit.SECONDS.sleep(2);  // Google API: Max 100 Requests per 100 seconds
			service = getService();
		    UpdateValuesResponse result = service.spreadsheets().values()
		    	      .update(this.spreadsheetId, range, v)
		    	      .setValueInputOption("USER_ENTERED")
		    	      .execute();
		} catch (GeneralSecurityException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
    }    
    private String readFile(String path) throws IOException {
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
    
    private class CardImplementation implements framework.CardPlaceholder {

    	private int cardId;
    	private String playerName;
    	
    	public CardImplementation(int cardId, String playerName) {
    		this.cardId = cardId;
    		this.playerName = playerName;
    	}

    	@Override
    	public int getCardId() {
    		return this.cardId;
    	}

    	@Override
    	public String getPlayerName() {
    		return this.playerName;
    	}

    	@Override
    	public void setCurrentPrice(int currentPrice) {
    		if(this.cardId != -1) {
    			setPriceOfCard(this, currentPrice);
    		}
    		
    	}

		@Override
		public void setCurrentPrice(int currentPrice, String lastUpdated) {
    		if(this.cardId != -1) {
    			setPriceOfCard(this, currentPrice, lastUpdated);
    		}			
		}

    }

	@Override
	public void setPriceOfCards(List<Card> cards) {
		// TODO Auto-generated method stub
		
	}
}

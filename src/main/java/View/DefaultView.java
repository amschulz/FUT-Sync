package View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import framework.Card;
import framework.Controller;
import framework.View;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;


public class DefaultView extends VBox implements View {
	
	private Thread t;
	private Controller controller;
	private ToolBar toolBar;
	private Button buttonStartSync;
	private Button buttonStopSync;
	private ComboBox<String> comboBox;
	private ObservableList<String> optionsSynchronization;
	private ProgressBar progressBar;
	private Label labelState;
	private Label labelStateText;

	private TabPane tabPane;
	private TableView tableSynchronization;
	private List<String> tabsText;
	private List<String> tableColumnTextSynchronization;
	private List<Tab> tabs;
	private List<TableColumn> tableColumnSynchronization;
	private ObservableList<TableCard> cardObjectList;
	private SimpleDoubleProperty amountOfCardsProperty;
	private SimpleDoubleProperty amountOfUpdatedCardsProperty;
	private Timer timer;

	public DefaultView(Controller controller, Stage primaryStage) {
		Scene scene = new Scene(this, 500, 400);
		primaryStage.setTitle("FUT-Script");
		primaryStage.setScene(scene);
		primaryStage.show();
	    primaryStage.setMinHeight(515);
	    primaryStage.setMinWidth(650);
		this.controller = controller;
		controller.setView(this);
		amountOfCardsProperty = new SimpleDoubleProperty(0);
		amountOfUpdatedCardsProperty = new SimpleDoubleProperty(0);
		optionsSynchronization = FXCollections.observableArrayList(
				"Automatisch (alle 30min)",
			    "Manuell"   
		);
		tabsText = new ArrayList<String>();
		tabsText.add("Synchronisation");
		tabsText.add("Transferliste");
		tabs = new ArrayList<Tab>();
		tableColumnTextSynchronization = new ArrayList<String>();
		tableColumnTextSynchronization.add("Karten ID");
		tableColumnTextSynchronization.add("Name");
		tableColumnTextSynchronization.add("Preis");
		tableColumnTextSynchronization.add("Update-Zeit CardProvider");
		tableColumnTextSynchronization.add("Update-Zeit Script");
		cardObjectList = FXCollections.observableArrayList();

		tableColumnSynchronization = new ArrayList<TableColumn>();
		
        initializeControls();
        layoutControls();
        addEventHandlers();
        addValueChangeListeners();
        addBindings();
        /*
        t = new Thread(controller);
        t.start();*/
        timer = new Timer();
        timer.schedule(new TimeTask(), 0, 1800000);
	}

	private void addBindings() {
		
	}

	private void addValueChangeListeners() {
		comboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue == oldValue) {
					return;
				}
				stopSynchronizing();
				if (newValue == "Manuell") {
					buttonStartSync.setDisable(false);
					buttonStopSync.setDisable(false);
					timer.cancel();
					timer.purge();
				} else if (newValue != "Manuell"){
					buttonStartSync.setDisable(true);
					buttonStopSync.setDisable(true);
					timer = new Timer();
					timer.schedule(new TimeTask(), 0, 1800000);
				}
			}
	    });
		
		amountOfUpdatedCardsProperty.addListener(new ChangeListener<Number>() {
			@Override 
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				syncProgress();
		    }
		});		
	}

	private void addEventHandlers() {
		buttonStartSync.setOnAction(new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		        if(t.getState() == Thread.State.TERMINATED || t.getState() == Thread.State.NEW) {
		        	startSynchronizing();
		        }
		    }
		});
		buttonStopSync.setOnAction(new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		        if(t.getState() != Thread.State.TERMINATED && t.getState() != Thread.State.NEW) {
		        	stopSynchronizing();
		        }
		    }
		});
	}

	private void layoutControls() {
		buttonStartSync.setDisable(true);
		buttonStopSync.setDisable(true);
		labelState.getStyleClass().add("big-label");
		labelStateText.getStyleClass().add("big-label");
		labelStateText.setVisible(false);
		
		toolBar.getItems().addAll(
				buttonStartSync,
				buttonStopSync,
				comboBox,
				labelState,
				labelStateText,
				progressBar
		);
		TableColumn tab0 = tableColumnSynchronization.get(0);
		tab0.setCellValueFactory(new PropertyValueFactory<Card, Long>("cardId"));
		TableColumn tab1 = tableColumnSynchronization.get(1);
		tab1.setCellValueFactory(new PropertyValueFactory<Card, String>("name"));
		tab1.getStyleClass().add("name-table-column");
		TableColumn tab2 = tableColumnSynchronization.get(2);
		tab2.setCellValueFactory(new PropertyValueFactory<Card, Long>("price"));
		tab2.getStyleClass().add("price-table-column");
		TableColumn tab3 = tableColumnSynchronization.get(3);
		tab3.setCellValueFactory(new PropertyValueFactory<Card, String>("lastUpdate"));
		tab3.getStyleClass().add("date-table-column");
		TableColumn tab4 = tableColumnSynchronization.get(4);
		tab4.setCellValueFactory(new PropertyValueFactory<Card, String>("lastUpdateScript"));
		tab4.getStyleClass().add("date-table-column");
		tableSynchronization.setItems(cardObjectList);
        tableSynchronization.getColumns().addAll(tableColumnSynchronization);
        tabs.get(0).setContent(tableSynchronization);
		
		tabPane.getTabs().addAll(tabs);
        getChildren().add(toolBar);
        getChildren().add(tabPane);
        
        getStylesheets().add(getClass().getResource("DefaultView.css").toExternalForm());

	}

	private void initializeControls() {
		toolBar = new ToolBar();
		buttonStartSync = new Button("Start");
		buttonStopSync = new Button("Stop");
		comboBox = new ComboBox<String>(optionsSynchronization);
		comboBox.getSelectionModel().selectFirst();
		
		labelState = new Label("Status: ");
		progressBar = new ProgressBar();
		labelStateText = new Label("Not Running");
		
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        for(String s: tabsText) {
            Tab tab = new Tab();
            tab.setText(s);
            tabs.add(tab);
        }
        
        tableSynchronization = new TableView<Card>();
        tableSynchronization.setEditable(false);
        
        for(String s: tableColumnTextSynchronization) {
            tableColumnSynchronization.add(new TableColumn(s));
        }
	}

	@Override
	public void addCard(Card c) {
		TableCard tc = new TableCard(c);
		cardObjectList.add(tc);
		this.amountOfUpdatedCardsProperty.set(this.amountOfUpdatedCardsProperty.get() + 1);
	}
	
	@Override
	public void setAmountOfCards(int amount) {
		this.amountOfCardsProperty.set(amount);
	}
	
	
	private void startSynchronizing() {
    	amountOfUpdatedCardsProperty.set(0);
    	cardObjectList.clear();
    	t = new Thread(controller);
    	t.start();
    	labelStateText.setVisible(false);
    	progressBar.setVisible(true);
	}
	
	private void stopSynchronizing() {
    	controller.setExit();
    	progressBar.setVisible(false);
    	labelStateText.setVisible(true);
    	amountOfCardsProperty.set(0);
    	amountOfUpdatedCardsProperty.set(0);
	}
	
	private void syncProgress() {
        new Thread(){
            public void run() {
        		if (amountOfUpdatedCardsProperty.get() == 0 && amountOfUpdatedCardsProperty.get() == 0) {
                    Platform.runLater(() -> progressBar.setProgress(0.0));
        		} else {
        			Platform.runLater(() -> progressBar.setProgress(amountOfUpdatedCardsProperty.get() / amountOfCardsProperty.get()));
        		}
            }
        }.start();
	}
	
	
	public class TableCard {

		private final SimpleLongProperty cardId;
		private final SimpleLongProperty price;
	    private final SimpleStringProperty name;
	    private final SimpleStringProperty lastUpdate;
	    private final SimpleStringProperty lastUpdateScript;

	    
	    private TableCard(long cardId, long price, String name, String lastUpdate, String lastUpdateScript) {
	        this.cardId = new SimpleLongProperty(cardId);
	        this.price = new SimpleLongProperty(price);
	        this.name = new SimpleStringProperty(name);
	        this.lastUpdate = new SimpleStringProperty(lastUpdate);
	        this.lastUpdateScript = new SimpleStringProperty(lastUpdateScript);
	    }
	 
	    public TableCard(Card c) {
	        this.cardId = new SimpleLongProperty(c.getCardId());
	        this.price = new SimpleLongProperty(c.getLowestPrice());
	        this.name = new SimpleStringProperty(c.getName());
	        this.lastUpdate = new SimpleStringProperty(c.getLastUpdate());
	        this.lastUpdateScript = new SimpleStringProperty(c.getLastUpdateScript());
	    }

		public long getCardId() {
	        return cardId.get();
	    }
	    
	    public long getPrice() {
	        return price.get();
	    }
	    
	    public String getName() {
	        return name.get();
	    }
	    
	    public String getLastUpdate() {
	        return lastUpdate.get();
	    }
	    
	    public String getLastUpdateScript() {
	        return lastUpdateScript.get();
	    } 
	}
	
	public class TimeTask extends TimerTask {
		public void run() {
			startSynchronizing();
	    }
	}
}



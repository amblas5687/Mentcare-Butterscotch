package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import application.DBConfig;
import application.MainFXApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Psychologist;

public class psychController {
	MainFXApp main = new MainFXApp();
	//needed for changing views
	Parent root;
	Stage stage;
	Scene scene;
	//links FXML ids
	@FXML private Button searchBtn;
	@FXML private TableView<Psychologist> psychTable;
	@FXML private TableColumn<Psychologist, String> PsychNoteCol;
	@FXML private TableColumn<Psychologist, String> PNumCol;
	@FXML private TableColumn<Psychologist, String> DocNumCol;
	@FXML private TextField PnumTF;
	@FXML private Button backBtn;
	@FXML private Label lblErrInvalidInput;
	@FXML private Label lblErrUserNotFound;
	//creates lists for psychologist
	static List<Psychologist> list = new ArrayList<Psychologist>();
	static ObservableList<Psychologist> appList = FXCollections.observableList(list);
	
	//sets main in Main.java 
	public void setMain(MainFXApp mainIn)
	{
		main = mainIn;
	}
	//retrieves and displays doctor notes
	@FXML
	void ClickSearchButton(ActionEvent event) throws Exception{
		System.out.println("Search Button pressed.");
		lblErrInvalidInput.setText("");
		lblErrUserNotFound.setText("");
		try{
			//Dumps list to clear view.
			appList.clear();
			
    		String enterPnum = PnumTF.getText();
    		
    		//whitelist only numeric inputs
    		//executes query if numeric input is met.
    		if(PnumTF.getText().matches("[0-9]+"))
    		{
    		String query = ("select * from mentcare.Psych_Notes where Pnum='" + enterPnum + "'"); //Grabs all columns based on Pnum textfield.
    		   
    		Connection conn = DBConfig.getConnection();
    		PreparedStatement statement = conn.prepareStatement(query);
        	ResultSet RS = null;
        	String Pnum = null, DocID = null, PsychNotes = null;

        	//this will execute the String 'query' exactly as if you were in SQL console
        	//and it returns a result set which contains everything we want, but we need to decode it first
        	RS = statement.executeQuery(query);
        	//if the query goes through, RS will no longer be null
        		while (RS.next()){	
        			//ToDo Create Database tables with mentioned parameters.
        			Pnum = Integer.toString(RS.getInt("Pnum"));
        			DocID = RS.getString("DocID");
        			PsychNotes = RS.getString("Notes");
        			System.out.println("Pnum : " + Pnum + "\nDocID: " + DocID + "\nPsychNotes: " + PsychNotes);
        			Psychologist psych = new Psychologist(Pnum, DocID, PsychNotes);
  		      		appList.add(psych);
      		    }
        		//numeric input is entered, but user doesn't exist.
        		if(Pnum==null)
            	{	lblErrUserNotFound.setText("No User Exists.");
            		System.out.println("No such user.");
            		
            	}
        		//Updates table in psychView
        		PNumCol.setCellValueFactory(cellData -> cellData.getValue().getPNumber());
                DocNumCol.setCellValueFactory(cellData -> cellData.getValue().getDocID());
                PsychNoteCol.setCellValueFactory(cellData -> cellData.getValue().getPsychNotes());
                psychTable.setItems(appList);
    		}
    		//whitelisted numeric input, if any other character is found then error label appears.
    		else
    		{
    			lblErrInvalidInput.setText("Invalid Input. Please Enter Patient number.");
    			System.out.println("Invalid Input.");
    		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//takes user back to main view page
	@FXML
	void ClickBackBtn (ActionEvent event) throws Exception{
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
		scene = new Scene(root);
		stage.setScene(scene);
	}
}

package org.vourp.gwt.editor.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class VO_URP_Editor implements EntryPoint {
    private static final String JSON_URL = "http://localhost:8089/vo-urp.googlecode.com-browser/" +
    "Show.do?entity=___type___&id=___id___&view=json";
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private VerticalPanel requestPanel = new VerticalPanel();
	private Button refreshButton = new Button("Refresh");
	private TextBox typeField = new TextBox();
	private TextBox idField = new TextBox();
	
	private VerticalPanel resultPanel = new VerticalPanel();
	private TextArea resultArea = new TextArea();
    private Label errorMsgLabel = new Label();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		typeField.setText("Simulator");
		idField.setText("1");

		requestPanel.add(typeField);
		requestPanel.add(idField);
		requestPanel.add(refreshButton);

		RootPanel.get("requestContainer").add(requestPanel);

        // Assemble Main panel.
		resultArea.setWidth("100em");
		resultArea.setHeight("50em");
        resultPanel.add(errorMsgLabel);
        resultPanel.add(resultArea);
		RootPanel.get("resultContainer").add(resultPanel);
		errorMsgLabel.setVisible(false);

		// Focus the cursor on the name field when the app loads
//		typeField.setFocus(true);
//		typeField.selectAll();
        refreshButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                retrieveObject();
            }
        });
	}

	/**
	 * Generate random stock prices.
	 */
	private void retrieveObject() {
	    	
	      String url = JSON_URL.replaceAll("___type___", typeField.getText()).replaceAll("___id___", idField.getText());
	
	      url = URL.encode(url);
	
	      // Send request to server and catch any errors.
	      RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
	
	      try {
	        Request request = builder.sendRequest(null, new RequestCallback() {
	          public void onError(Request request, Throwable exception) {
	            displayError("Couldn't retrieve JSON");
	          }
	
	          public void onResponseReceived(Request request, Response response) {
	            if (200 == response.getStatusCode()) {
	              resultArea.setText(response.getText());
	              errorMsgLabel.setVisible(false);
	            } else {
	              displayError("Couldn't retrieve JSON (" + response.getStatusText()
	                  + ")");
	            }
	          }
	        });
	      } catch (RequestException e) {
	        displayError("Couldn't retrieve JSON");
	      }
	
	}
    /**
     * If can't get JSON, display error message.
     * @param error
     */
    private void displayError(String error) {
      errorMsgLabel.setText("Error: " + error);
      errorMsgLabel.setVisible(true);
    }
}

package edu.sdsu.cs645.Billboard.client;

import org.apache.tools.ant.taskdefs.LoadProperties;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import edu.sdsu.cs645.Billboard.shared.FieldVerifier;
public class Billboard implements EntryPoint {
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

		private final BillboardServiceAsync billboardService = GWT.create(BillboardService.class);

	private HTML status;
	private PasswordTextBox password;
	private RichTextArea board;
	public void onModuleLoad() {
		status = new HTML();
		buildLogin();
		password.setFocus(true);		
	}
	
	private void buildLogin()
	{
		FlowPanel loginPanel = new FlowPanel();
		loginPanel.getElement().setId("log-panel");
		password = new PasswordTextBox();
		loginPanel.add(new HTML("<h1>Please Enter Your Password</h1>"));
		loginPanel.add(new Label("Password"));
		loginPanel.add(password);
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("button-log-panel");
		Button loginButton = new Button("Login");
		Button clearButton = new Button("Clear");
		loginButton.setStyleName("log-button");
		clearButton.setStyleName("clear-button");
		
		clearButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			status.setText("");	
			password.setText("");	
			password.setFocus(true);
			}
		});
		
		loginButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			validateLogin(password.getText());
			/*status.setText("");	
			password.setText("");	*/
			password.setFocus(true);
			}
		});
		
		buttonPanel.add(clearButton);
		buttonPanel.add(loginButton);
		loginPanel.add(buttonPanel);
		loginPanel.add(status);
		
		RootPanel.get().add(loginPanel);
	}
	
	private void validateLogin(String login)
	{
		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object results)
			{
				String answer = (String) results;
				if(answer.equals("OK"))
				{
					status.setText("");
					buildMainPanel();
					loadPanel();	
					//doAlert("Logged In");
				}
				else
				{
					status.setText("Invalid Password");
				}
			}
			public void onFailure(Throwable err)
			{
				status.setText(err.toString());
				err.printStackTrace();
			}
		};
		billboardService.validateLogin(login,callback);
	}
	
	private native void doAlert(String s)
	/*-{
		alert(s);
	}-*/;
	private void buildMainPanel()
	{
		FlowPanel main = new FlowPanel();
		main.add(new HTML("<h1>Online Billboard</h1>"));
		main.add(getButtonPanel());
		board = new RichTextArea();
		main.add(board);
		main.add(status);
		RootPanel.get().clear();
		RootPanel.get().add(main);
		board.setFocus(true);
		//loadPanel();
	}
	
	private FlowPanel getButtonPanel()
	{
		FlowPanel p = new FlowPanel();
		Button clr = new Button("Clear");
		Button save = new Button("Save");
		Button load = new Button("Load");
		
		clr.setStyleName("my-button1");
		save.setStyleName("my-button2");
		load.setStyleName("my-button3");
		
		clr.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				board.setHTML("");
				
			}
		});
		
		save.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				savePanel();	
			}
		});
		
        load.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				loadPanel();	
			}
		});
		
		
		p.setStyleName("button-panel");
		p.add(clr);
		p.add(save);
		p.add(load);
		return p;
	}
	
	private void savePanel()
	{
		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object results)
			{
				status.setText((String) results);
				board.setHTML("");
				
			}
			public void onFailure(Throwable err)
			{				
			}
		};
		billboardService.save(board.getHTML(), callback);
	}
		private void loadPanel()
		{
			AsyncCallback callback = new AsyncCallback() {
				public void onSuccess(Object results)
				{
					board.setHTML((String) results);
					
				}
				public void onFailure(Throwable err)
				{	
					status.setText(""+err);
				}
			};
		
		billboardService.load(callback);
	}
}

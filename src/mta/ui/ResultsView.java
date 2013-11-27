package mta.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mta.pearson.API;
import mta.pearson.Messages;
import mta.pearson.Messages.Message;
import mta.qt.KeyValueModel;
import mta.qt.LoadingModel;
import mta.qt.NullModel;
import mta.util.Errors;
import mta.util.Future;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.*;

public class ResultsView extends QObject {
	private QListView submissionsListView;
	
	public ResultsView(QWidget container) {
		QGridLayout grid = new QGridLayout();
		container.setLayout(grid);
		
		submissionsListView = new QListView(container);
		grid.addWidget(submissionsListView, 0, 1);
	}

	public Signal0 readyStateChange = new Signal0();
	public boolean isReady() {return readyState;}
	
	public Messages getSubmissions() {
		return lastSubmissionList;
	}
	
	public void setAssignment(String selectedAssignment) {
		setReadyState(false);
		this.selectedAssignment = selectedAssignment;
		if (selectedAssignment != null) {
			submissionsListView.setModel(LoadingModel.model);
			submissionList.start();
		}
		else
			submissionsListView.setModel(NullModel.model);
	}
	
	private String selectedAssignment;
	
	private Future<Messages> submissionList
		= new Future<Messages>(this, "displaySubmissions()") {
		
		public Messages evaluate() {
			try {
				URL assgnUrl = new URL(selectedAssignment + "/dropboxBasket");
				URL basketUrl = new URL(API.getRequest(assgnUrl).get("dropboxBasket")
						.get("links").get(0).get("href").asText() + "/messages");
				return API.getRequest(basketUrl, Messages.class);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	//must be in a field in case the Future changed after we checked it 
	private Messages lastSubmissionList;
	
	@SuppressWarnings("unused")
	private void displaySubmissions() {
		try {
			lastSubmissionList = submissionList.get();
			submissionsListView.setModel(NullModel.model);
			
			List<String> submnStudent = new ArrayList<String>();
			List<Message> submn = new ArrayList<Message>();
			
			for (Message message : lastSubmissionList.messages) {
				submnStudent.add(message.submissionStudent.lastName + ", " +
						message.submissionStudent.firstName);
				submn.add(message);
			}
			submissionsListView.setModel(new KeyValueModel<String, Message>(submnStudent, submn));
			setReadyState(lastSubmissionList.messages.length != 0);
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	private boolean readyState = false;
	private void setReadyState(boolean state) {
		readyState = state;
		readyStateChange.emit();
	}
}

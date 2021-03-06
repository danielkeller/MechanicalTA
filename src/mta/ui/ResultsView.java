package mta.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.runner.notification.Failure;

import mta.pearson.API;
import mta.pearson.Messages;
import mta.pearson.Messages.*;
import mta.qt.KeyValueModel;
import mta.qt.LoadingModel;
import mta.qt.NullModel;
import mta.test.TestRunner.Score;
import mta.ui.CourseView.GradebookLink;
import mta.util.Errors;
import mta.util.Future;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class ResultsView extends QObject {
	private QListView submissionsListView;
	private QLabel scoreLabel;
	private QPlainTextEdit diags;
	private QListView failures;
	
	public ResultsView(QWidget container) {
		QGridLayout grid = new QGridLayout();
		container.setLayout(grid);
		
		submissionsListView = new QListView(container);
		submissionsListView.setMaximumWidth(150);
		submissionsListView.setSelectionMode(QAbstractItemView.SelectionMode.NoSelection);
		grid.addWidget(submissionsListView, 0, 0, Qt.AlignmentFlag.AlignLeft);
		
		QFrame resFrame = new QFrame(container);
		grid.addWidget(resFrame, 0, 1);
		QGridLayout resGrid = new QGridLayout();
		resFrame.setLayout(resGrid);
		
		scoreLabel = new QLabel("Score:", resFrame);
		resGrid.addWidget(scoreLabel, 0, 0, Qt.AlignmentFlag.AlignTop, Qt.AlignmentFlag.AlignCenter);
		
		diags = new QPlainTextEdit("Error messages...", resFrame);
		diags.setFont(new QFont("Courier"));
		diags.setReadOnly(true);
		resGrid.addWidget(diags, 1, 0);
		
		failures = new QListView(resFrame);
		failures.setSelectionMode(QAbstractItemView.SelectionMode.NoSelection);
		resGrid.addWidget(failures, 2, 0);
	}

	public Signal0 readyStateChange = new Signal0();
	public boolean isReady() {return readyState;}
	
	public Messages getSubmissions() {
		return lastSubmissionList;
	}

	
	public URL getBasketURL() {
		return basketUrl.get();
	}
	
	public void setAssignment(GradebookLink gradebookLink) {
		setReadyState(false);
		this.selectedAssignment = gradebookLink;
		if (gradebookLink != null) {
			submissionsListView.setModel(LoadingModel.model);
			basketUrl.start();
		}
		else
			submissionsListView.setModel(NullModel.model);
	}
	
	public void setResult(Map<Message, Score> result) {
		this.result = result;
		submissionsListView.setSelectionMode(QAbstractItemView.SelectionMode.SingleSelection);
	}
	
	private GradebookLink selectedAssignment;
	
	private Future<Messages> submissionList
		= new Future<Messages>(this, "displaySubmissions()") {
		public Messages evaluate() {
			return API.getRequest(basketUrl.get(), Messages.class);
		}
	};
	
	private Future<URL> basketUrl = new Future<URL>(submissionList, "start()") {
		public URL evaluate() {
			try {
				URL assgnUrl = new URL(selectedAssignment.assignmentLink + "/dropboxBasket");
				return new URL(API.getRequest(assgnUrl).get("dropboxBasket")
						.get("links").get(0).get("href").asText() + "/messages");
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
				if (message.attachments.length > 0) {
					submnStudent.add(message.submissionStudent.lastName + ", " +
							message.submissionStudent.firstName);
					submn.add(message);
				}
			}
			submissionsListView.setModel(new KeyValueModel<String, Message>(submnStudent, submn));
			submissionsListView.selectionModel().selectionChanged.connect(this, "resultSelected(QItemSelection, QItemSelection)");
			setReadyState(lastSubmissionList.messages.length != 0);
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	private Map<Message, Score> result;
	
	@SuppressWarnings("unused")
	private void resultSelected(QItemSelection current, QItemSelection previous) {
		Message msg = (Message)current.indexes().get(0).data(Qt.ItemDataRole.UserRole);
		Score score = result.get(msg);
		scoreLabel.setText("Score: " + score.earnedPoints + "/" + score.totalPoints);
		
		diags.setPlainText("");
		for (Diagnostic<? extends JavaFileObject> diag : score.diagnostics.getDiagnostics())
			if (!diag.getMessage(null).contains("package-info.java"))
				diags.appendPlainText(diag.toString());
		
		List<String> fails = new ArrayList<String>(score.result.getFailures().size()); 
		for (Failure fail : score.result.getFailures())
			fails.add(fail.toString());
		failures.setModel(new QStringListModel(fails));
	}
	
	private boolean readyState = false;
	private void setReadyState(boolean state) {
		readyState = state;
		readyStateChange.emit();
	}
}

package mta.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import mta.pearson.*;
import mta.pearson.Messages.Message;
import mta.qt.*;
import mta.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.trolltech.qt.*;
import com.trolltech.qt.core.Qt.ItemDataRole;
import com.trolltech.qt.gui.*;

public class CourseView extends QSignalEmitter {
	QListView courseListView, assignmentListView, submissionsListView;
	
	//Methods are laid out in approximate chronological order
	
	CourseView(QWidget container) {
		QGridLayout grid = new QGridLayout();
		container.setLayout(grid);

		courseListView = new QListView(container);
		grid.addWidget(courseListView, 0, 0);
		assignmentListView = new QListView(container);
		grid.addWidget(assignmentListView, 1, 0);
		submissionsListView = new QListView(container);
		grid.addWidget(submissionsListView, 0, 1, 2, 1);
		//this one is informational only
		submissionsListView.setSelectionMode(QListView.SelectionMode.NoSelection);
		
		QPushButton download = new QPushButton("Download", container);
		download.clicked.connect(this, "download()");
		grid.addWidget(download, 2, 0, 1, 2);
	}
	
	public void update() {
		courseListView.setModel(LoadingModel.model);
		courseList.start();
	}
	
	private Future<KeyValueModel<String, String>> courseList
		= new Future<KeyValueModel<String, String>>(this, "displayCourses()") {
		public KeyValueModel<String, String> evaluate() {
			JsonNode courseJson = API.getRequest("me/courses").get("courses");
			List<String> courseNames = new ArrayList<String>();
			List<String> courseIds = new ArrayList<String>();
			
			for (JsonNode course : courseJson) {
				URL url;
				try {
					url = new URL(course.get("links").get(0).get("href").asText());
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
				JsonNode descrJson = API.getRequest(url).get("courses").get(0);
				String courseID = descrJson.get("id").asText();
				
				JsonNode roleJson = API.getRequest("me/courses/" + courseID + "/role").get("role");
				
				if (roleJson.get("type").asText().equals("TAST")
						|| roleJson.get("type").asText().equals("PROF")) {
					courseNames.add(descrJson.get("title").asText());
					courseIds.add(courseID);
				}
			}
			return new KeyValueModel<String, String>(courseNames, courseIds);
		}
	};
	
	@SuppressWarnings("unused")
	private void displayCourses() {
		try {
			courseListView.setModel(courseList.get());
			courseListView.selectionModel().selectionChanged.connect(
					this, "courseSelected(QItemSelection, QItemSelection)");
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	String selectedCourse;
	
	@SuppressWarnings("unused")
	private void courseSelected(QItemSelection current, QItemSelection previous) {
		try {
			assignmentListView.setModel(LoadingModel.model);
			selectedCourse = (String)current.indexes().get(0).data(ItemDataRole.UserRole);
			gradeableList.start();
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	private Future<KeyValueModel<String, String>> gradeableList
		= new Future<KeyValueModel<String, String>>(this, "displayAssignments()") {
		
		public KeyValueModel<String, String> evaluate() {
			List<String> assignmentNames = new ArrayList<String>();
			List<String> assignmentLinks = new ArrayList<String>();
			
			JsonNode items = API.getRequest("courses/" + selectedCourse + "/gradebookItems")
					.get("gradebookItems");
			
			for (JsonNode item : items) {
				for (JsonNode link : item.get("links"))
					if (link.get("rel").asText().contains("dropboxbasket")) {
						assignmentNames.add(item.get("title").asText());
						for (JsonNode link2 : item.get("links"))
							if (link2.get("rel").asText().equals("related"))
								assignmentLinks.add(link2.get("href").asText());
					}
			}
			return new KeyValueModel<String, String>(assignmentNames, assignmentLinks);
		}
	};
	
	@SuppressWarnings("unused")
	private void displayAssignments() {
		try {
			assignmentListView.setModel(gradeableList.get());
			assignmentListView.selectionModel().selectionChanged.connect(
					this, "assignmentSelected(QItemSelection, QItemSelection)");
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	String selectedAssignment;
	
	@SuppressWarnings("unused")
	private void assignmentSelected(QItemSelection current, QItemSelection previous) {
		try {
			submissionsListView.setModel(LoadingModel.model);
			selectedAssignment = (String)current.indexes().get(0).data(ItemDataRole.UserRole);
			submissionList.start();
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
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
	
	@SuppressWarnings("unused")
	private void displaySubmissions() {
		try {
			List<String> submnStudent = new ArrayList<String>();
			List<Message> submn = new ArrayList<Message>();
			for (Message message : submissionList.get().messages) {
				submnStudent.add(message.submissionStudent.lastName + ", " +
						message.submissionStudent.firstName);
				submn.add(message);
			}
			submissionsListView.setModel(new KeyValueModel<String, Message>(submnStudent, submn));
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}

	@SuppressWarnings("unused")
	private void download() {
		
	}
}

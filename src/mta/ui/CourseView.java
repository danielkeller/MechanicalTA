package mta.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import mta.pearson.*;
import mta.qt.KeyValueModel;
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
			selectedAssignment = (String)current.indexes().get(0).data(ItemDataRole.UserRole);
			submissionList.start();
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
	
	private Future<KeyValueModel<String, String>> submissionList
		= new Future<KeyValueModel<String, String>>(this, "displaySubmissions()") {
		
		public KeyValueModel<String, String> evaluate() {
			List<String> submnStudent = new ArrayList<String>();
			List<String> submnId = new ArrayList<String>();
			try {
				URL assgnUrl = new URL(selectedAssignment + "/dropboxBasket");
				URL basketUrl = new URL(API.getRequest(assgnUrl).get("dropboxBasket")
						.get("links").get(0).get("href").asText() + "/messages");
				Messages msgs = API.getRequest(basketUrl, Messages.class);
				JsonNode messages = API.getRequest(basketUrl).get("messages");
				for (JsonNode message : messages) {
					JsonNode student = message.get("submissionStudent");
					submnStudent.add(student.get("lastName").asText() + ", " +
							student.get("firstName").asText());
					submnId.add(message.get("id").asText());
				}

			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			return new KeyValueModel<String, String>(submnStudent, submnId);
		}
	};
	
	@SuppressWarnings("unused")
	private void displaySubmissions() {
		try {
			submissionsListView.setModel(submissionList.get());
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}

	@SuppressWarnings("unused")
	private void download() {
		
	}
}

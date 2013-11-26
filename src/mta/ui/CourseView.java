package mta.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import mta.pearson.API;
import mta.util.Errors;

import com.fasterxml.jackson.databind.JsonNode;
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class CourseView extends QSignalEmitter {
	QStringListModel courseList;
	QListView courseListView;
	
	CourseView(QWidget container) {
		QGridLayout grid = new QGridLayout();
		container.setLayout(grid);

		courseListView = new QListView(container);
		grid.addWidget(courseListView, 0, 0);
	}
	
	private Signal1<QStringListModel> done = new Signal1<QStringListModel>();
	
	void update() {
		final 
		Runnable r = new Runnable() {
			public void run() {
				courseList = new QStringListModel();
				JsonNode courseJson = API.getRequest("me/courses").get("courses");
				List<String> courses = new ArrayList<String>();
				
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
					
					if (roleJson.get("type").asText().equals("TAST"))
						courses.add(descrJson.get("title").asText());
				}
				courseList.setStringList(courses);
				done.emit(courseList);
			}
		};
		QThread th = new QThread(r);
		done.connect(this, "displayCourses(QStringListModel)", Qt.ConnectionType.QueuedConnection);
		th.start();
	}
	
	@SuppressWarnings("unused")
	private void displayCourses(QStringListModel courseList) {
		try {
			courseListView.setModel(courseList);
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
}

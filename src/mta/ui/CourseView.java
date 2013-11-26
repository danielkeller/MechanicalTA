package mta.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import mta.pearson.API;
import mta.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.trolltech.qt.*;
import com.trolltech.qt.gui.*;

public class CourseView extends QSignalEmitter {
	QListView courseListView;
	
	CourseView(QWidget container) {
		QGridLayout grid = new QGridLayout();
		container.setLayout(grid);

		courseListView = new QListView(container);
		grid.addWidget(courseListView, 0, 0);
	}
	
	public void update() {
		courseList.start();
	}
	
	private Future<QStringListModel> courseList = new Future<QStringListModel>(this, "displayCourses()") {
		public QStringListModel evaluate() {
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
			return new QStringListModel(courses);
		}
	};
	
	@SuppressWarnings("unused")
	private void displayCourses() {
		try {
			courseListView.setModel(courseList.get());
		} catch (Throwable e) {
			Errors.dieGracefully(e);
		}
	}
}

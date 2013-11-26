package mta.pearson;

public class Messages {
	public Message[] messages;
	
	public static class Message {
		public String id;
		public SubmissionStudent submissionStudent;
		public Attachment[] attachments;
	}
	
	public static class SubmissionStudent {
		public String id;
		public String firstName;
		public String lastName;
	}
	
	public static class Attachment {
		public String id;
		public String name;
		public String contentUrl;
	}
}

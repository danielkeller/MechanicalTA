package mta.pearson;

public class Messages {
	public Message[] messages;
	
	public static class Message implements Comparable<Message> {
		public String id;
		public Student submissionStudent;
		public Attachment[] attachments;
		
		@Override
		public int compareTo(Message o) {
			return id.compareTo(o.id);
		}
	}
	
	public static class Attachment {
		public String id;
		public String name;
		public String contentUrl;
	}
}

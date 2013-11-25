package mta.pearson;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.net.ssl.*;

import com.fasterxml.jackson.databind.*;

public class Auth {
	public static String accessToken = null;
	
	public static boolean Authenticate(String domain, String client, String user, String pass) {
		try {
			URL url = new URL("https://" + domain + "/token");
			
	        StringBuilder data = new StringBuilder();
	        data.append("grant_type=" + URLEncoder.encode("password", "UTF-8"));
	        data.append("&client_id=" +
	            URLEncoder.encode("c670ad06-8b47-490e-a604-884a862f051e", "UTF-8"));
	        data.append("&username=" + URLEncoder.encode(client + "\\" + user, "UTF-8"));
	        data.append("&password=" + URLEncoder.encode(pass, "UTF-8"));
	        
	        byte[] byteArray = data.toString().getBytes("UTF-8");
	        
	        HttpURLConnection conn = (HttpsURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        conn.setRequestProperty("Content-Length", "" + byteArray.length);
	        conn.setDoOutput(true);
	        
	        OutputStream postStream = conn.getOutputStream();
	        postStream.write(byteArray, 0, byteArray.length);
	        postStream.close();
	        
	        if(conn.getResponseCode() >= 300) {
	        	try (Scanner err = new Scanner(conn.getErrorStream());) {
		        	err.useDelimiter("\\A");
		        	System.out.println(err.next());
	        	}
	        	return false;
	        }
	        
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode node = mapper.readTree(conn.getInputStream());
	        accessToken = node.get("access_token").asText();
	        System.out.println(accessToken);
	        return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
}

package com.bigbass.gitcordbot.github;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.json.JsonReader;

import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;

public class EventsMonitor {
	
	private final String endpoint;
	private Response resp;
	
	private boolean apiInitialServed = false;
	
	public EventsMonitor(String endpoint){
		this.endpoint = endpoint;
		
		serveAPI();
	}
	
	public void serveAPI(){
		serveAPI(null);
	}
	
	public void serveAPI(Map<String, String> headers){
		GithubController github = GithubController.getInstance();
		Request req = new JdkRequest("https://api.github.com/" + endpoint)
				.header("User-Agent", github.USER_AGENT)
				.header("Authorization", "token " + github.TOKEN);
		
		if(apiInitialServed){
			req = req.header("If-None-Match", getETag());
		}
		
		if(headers != null && !headers.isEmpty()){
			for(String key : headers.keySet()){
				
				if(key != null && !key.isEmpty()){
					String val = headers.get(key);
					if(val != null && !val.isEmpty()){
						req = req.header(key, headers.get(key));
					}
				}
				
			}
		}
		
		try {
			resp = req.fetch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(resp != null){
			apiInitialServed = true;
		}
	}
	
	/**
	 * Can return null
	 * 
	 * @return Response from when serving the API request
	 */
	public Response getResponse(){
		if(apiInitialServed){
			return resp;
		}
		
		return null;
	}
	
	/**
	 * Can return null
	 * 
	 * @return ETag from the API request's response headers
	 */
	public String getETag(){
		if(apiInitialServed){
			Map<String, List<String>> map = resp.headers();
			
			List<String> etagList = map.get("ETag");
			if(etagList.size() == 1){
				return etagList.get(0);
			}
		}
		
		return null;
	}
	
	public JsonReader getJsonReader(){
		if(apiInitialServed && resp != null){
			return resp.as(JsonResponse.class).json();
		}
		
		return null;
	}
}

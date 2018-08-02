package com.dnastack.collections.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.dnastack.collections.model.Ga4ghDataBundle;
import com.dnastack.collections.model.Ga4ghDataObject;
import com.google.gson.Gson;

@Service
public class CollectionsService {

	public final String dos_server_url = "http://localhost:8080";
	
	// Data Bundle methods, which maps to the notion of a "Collection"
	
	public List<Ga4ghDataBundle> getDataBundles() throws IOException, JSONException {
		List<Ga4ghDataBundle> dataBundles = new ArrayList<Ga4ghDataBundle>();
		int next_page = 1;
		while (next_page != 0) {
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(dos_server_url + "/databundles?page=" + String.valueOf(next_page - 1));
			HttpResponse response = httpClient.execute(get);
			
			JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
							.lines().collect(Collectors.joining()) );
			
			for (int i = 0; i < json.getJSONArray("data_bundles").length(); i++) {
				dataBundles.add(new Ga4ghDataBundle(json.getJSONArray("data_bundles").getJSONObject(i)));
			}
			next_page = Integer.valueOf(json.getString("next_page_token")).intValue();
		
		}
		return dataBundles;
	}
	
	public List<String> getDataBundleIds() throws IOException, JSONException {
		List<String> dataBundleIds = new ArrayList<>();
		int next_page = 1;
		while (next_page != 0) {
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(dos_server_url + "/databundles?page=" + String.valueOf(next_page - 1));
			HttpResponse response = httpClient.execute(get);
			
			JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
							.lines().collect(Collectors.joining()) );
			
			for (int i = 0; i < json.getJSONArray("data_bundles").length(); i++) {
				dataBundleIds.add(json.getJSONArray("data_bundles").getJSONObject(i).getString("id"));
			}
			next_page = Integer.valueOf(json.getString("next_page_token")).intValue();
		
		}
		return dataBundleIds;
	}
	
	public Ga4ghDataBundle getDataBundleById(String data_bundle_id) throws MalformedURLException, JSONException, IOException {
		
		// Authentication
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		//CredentialsProvider provider = new BasicCredentialsProvider();
		//UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "testuser");
		//provider.setCredentials(AuthScope.ANY, credentials);

		//HttpClientContext context = HttpClientContext.create();
		//context.setCredentialsProvider(provider);
		//context.setAuthSchemeRegistry(authRegistry);
		//context.setAuthCache(authCache);
		
		//HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(dos_server_url + "/databundles/" + data_bundle_id);
		HttpResponse response = httpClient.execute(get);
		
		JSONObject jsonDataBundle = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining()) );
		
		return new Ga4ghDataBundle(jsonDataBundle.getJSONObject("data_bundle"));
	}
	
	public List<Ga4ghDataBundle> getDataBundlesContainingDataObjectId(String data_object_id) throws MalformedURLException, JSONException, IOException {
		List<Ga4ghDataBundle> dataBundles = new ArrayList<Ga4ghDataBundle>();
		
		int next_page = 1;
		while (next_page != 0) {
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(dos_server_url + "/databundles?page=" + String.valueOf(next_page - 1));
			HttpResponse response = httpClient.execute(get);
			
			// Response from Get request
			JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
							.lines().collect(Collectors.joining()) );
			
			// Iterating over all objects from GET response and adding them to dataBundles List
			for (int i = 0; i < json.getJSONArray("data_bundles").length(); i++) {
				if (json.getJSONArray("data_bundles").getJSONObject(i).getString("id").equals(data_object_id)) {
					dataBundles.add(new Ga4ghDataBundle(json.getJSONArray("data_bundles").getJSONObject(i)));
				}
			}
			next_page = Integer.valueOf(json.getString("next_page_token")).intValue();
		}
		return dataBundles;
	}
	
	public String addDataBundle(Ga4ghDataBundle dataBundle) throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(dos_server_url + "/databundles");

		Gson gson = new Gson();
		String json = gson.toJson(dataBundle);
		json = "{\"data_bundle\":" + json + "}";

		post.setEntity(new StringEntity(json));
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		return new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining());
	}
	
	public String updateDataBundle(String data_bundle_id, Ga4ghDataObject dataObject)
			throws ClientProtocolException, IOException, JSONException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPut put = new HttpPut(dos_server_url + "/databundles/" + data_bundle_id);

		Ga4ghDataBundle dataBundle = getDataBundleById(data_bundle_id);
		dataBundle.addData_object_id(dataObject.getId());

		Gson gson = new Gson();
		String json = gson.toJson(dataBundle);
		json = "{\"data_bundle_id\": \"" + data_bundle_id + "\", \"data_bundle\":" + json + "}}";
		
		put.setEntity(new StringEntity(json));
		put.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(put);
		return new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining());
	}
	
	public String deleteDataBundle(String data_bundle_id) throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpDelete delete = new HttpDelete(dos_server_url + "/databundles/" + data_bundle_id);
		HttpResponse response = httpClient.execute(delete);
		return new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining());
	}
	
	public String removeDataObjectFromDataBundle(String data_bundle_id, String data_object_id)
			throws MalformedURLException, JSONException, IOException {
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPut put = new HttpPut(dos_server_url + "/databundles/" + data_bundle_id);
		
		Ga4ghDataBundle dataBundle = getDataBundleById(data_bundle_id);
		dataBundle.removeData_object_id(data_object_id);
		
		Gson gson = new Gson();
		String json = gson.toJson(dataBundle);
		json = "{\"data_bundle_id\": \"" + data_bundle_id + "\", \"data_bundle\":" + json + "}}";

		put.setEntity(new StringEntity(json));
		put.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(put);
		return new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining());
	}
	
	// Data Object methods, which maps to the actually data in the database
	
	public Ga4ghDataObject getDataObjectById(String data_object_id) throws MalformedURLException, JSONException, IOException {
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(dos_server_url + "/dataobjects/" + data_object_id);
		HttpResponse response = httpClient.execute(get);
		
		JSONObject jsonDataObject = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining()) );
		
		return new Ga4ghDataObject(jsonDataObject.getJSONObject("data_object"));
	}
	
	public String addDataObject(Ga4ghDataObject dataObject) throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(dos_server_url + "/dataobjects");

		Gson gson = new Gson();
		String json = gson.toJson(dataObject);
		json = "{\"data_object\":" + json + "}";

		post.setEntity(new StringEntity(json));
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		return new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining());
	}
	
	public String updateDataObject(String data_object_id, Ga4ghDataObject dataObject)
			throws ClientProtocolException, IOException, JSONException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPut put = new HttpPut(dos_server_url + "/dataobjects/" + data_object_id);

		Gson gson = new Gson();
		String json = gson.toJson(dataObject);
		json = "{\"data_object_id\": \"" + data_object_id + "\", \"data_object\":" + json + "}}";
		
		put.setEntity(new StringEntity(json));
		put.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(put);
		return new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining());
	}
	
}

package com.dnastack.collections.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
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
	
	// GET - list of all data bundles
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
	
	// GET - list of all data bundles Paginated
	public Map<String, List<String>> getDataBundlesPaginated(String page_token) throws IOException, JSONException {
		Map<String, List<String>> dataBundles = new HashMap<String, List<String>>();
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(dos_server_url + "/databundles?page=" + page_token);
		HttpResponse response = httpClient.execute(get);
		
		JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
						.lines().collect(Collectors.joining()) );
		
		for (int i = 0; i < json.getJSONArray("data_bundles").length(); i++) {
			List<String> dataObjectIds = new ArrayList<String>();
			for (int j = 0; j < json.getJSONArray("data_bundles").getJSONObject(i).getJSONArray("data_object_ids").length(); j++) {
				dataObjectIds.add(json.getJSONArray("data_bundles").getJSONObject(i).getJSONArray("data_object_ids").getString(j));
			}
			
			dataBundles.put(json.getJSONArray("data_bundles").getJSONObject(i).getString("id"), dataObjectIds);
		}
		
		return dataBundles;
	}
	
	// GET - list of all data bundle ids
	public List<String> getDataBundleIds() throws IOException, JSONException {
		List<String> dataBundleIds = new ArrayList<String>();
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
	
	// GET - specific data bundle
	public Ga4ghDataBundle getDataBundleById(String data_bundle_id) throws MalformedURLException, JSONException, IOException {
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(dos_server_url + "/databundles/" + data_bundle_id);
		HttpResponse response = httpClient.execute(get);
		
		JSONObject jsonDataBundle = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining()) );
		
		return new Ga4ghDataBundle(jsonDataBundle.getJSONObject("data_bundle"));
	}
	
	// GET - list of data bundles containing some data object id
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
	
	// GET - list of data bundles an alias
	public List<Ga4ghDataBundle> getDataBundlesByAlias(String alias) throws MalformedURLException, JSONException, IOException {
		List<Ga4ghDataBundle> dataBundles = new ArrayList<Ga4ghDataBundle>();
		
		int next_page = 1;
		while (next_page != 0) {
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(dos_server_url + "/databundles?alias=" + alias + "&page=" + String.valueOf(next_page - 1));
			HttpResponse response = httpClient.execute(get);
			
			// Response from Get request
			JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
							.lines().collect(Collectors.joining()) );
			
			// Iterating over all objects from GET response and adding them to dataBundles List
			for (int i = 0; i < json.getJSONArray("data_bundles").length(); i++) {
				dataBundles.add(new Ga4ghDataBundle(json.getJSONArray("data_bundles").getJSONObject(i)));
			}
			next_page = Integer.valueOf(json.getString("next_page_token")).intValue();
		}
		return dataBundles;
	}
	
	// GET - list of data bundles containing any in a list of aliases
	public List<Ga4ghDataBundle> getDataBundlesContainingAliases(List<String> aliases) throws MalformedURLException, JSONException, IOException {
		
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
				JSONArray object_aliases = json.getJSONArray("data_bundles").getJSONObject(i).getJSONArray("aliases");
				for (int j = 0; j < object_aliases.length(); j++) {
					if (aliases.contains(object_aliases.getString(j))) {
						dataBundles.add(new Ga4ghDataBundle(json.getJSONArray("data_bundles").getJSONObject(i)));
					}
				}
			}
			next_page = Integer.valueOf(json.getString("next_page_token")).intValue();
		}
		return dataBundles;
	}
	
	
	// POST - Data Bundle
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
	
	// PUT - Data Bundle
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
	
	// DELETE - Data Bundle
	public String deleteDataBundle(String data_bundle_id) throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpDelete delete = new HttpDelete(dos_server_url + "/databundles/" + data_bundle_id);
		HttpResponse response = httpClient.execute(delete);
		return new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining());
	}
	
	// PUT - Data Bundle specifically the list of data objects
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
	
	// GET - Data Object by id
	public Ga4ghDataObject getDataObjectById(String data_object_id) throws MalformedURLException, JSONException, IOException {
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(dos_server_url + "/dataobjects/" + data_object_id);
		HttpResponse response = httpClient.execute(get);
		
		JSONObject jsonDataObject = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining()) );
		
		return new Ga4ghDataObject(jsonDataObject.getJSONObject("data_object"));
	}
	
	// GET - list all data objects
	public List<List<String>> getDataObjects() throws IOException, JSONException {
		List<List<String>> dataObjects = new ArrayList<List<String>>();
		int next_page = 1;
		while (next_page != 0) {
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(dos_server_url + "/dataobjects?page=" + String.valueOf(next_page - 1));
			HttpResponse response = httpClient.execute(get);
			
			JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
							.lines().collect(Collectors.joining()) );
			
			for (int i = 0; i < json.getJSONArray("data_objects").length(); i++) {
				List<String> temp = new ArrayList<String>();
				temp.add(json.getJSONArray("data_objects").getJSONObject(i).getString("id"));
				
				JSONArray urls = json.getJSONArray("data_objects").getJSONObject(i).getJSONArray("urls");
				for (int j = 0; j < urls.length(); j++) {
					temp.add(urls.getJSONObject(i).getString("url"));
				}
				dataObjects.add(temp);
			}
			next_page = Integer.valueOf(json.getString("next_page_token")).intValue();
		
		}
		return dataObjects;
	}
	
	// GET - list all data objects Paginated
	public List<List<String>> getDataObjectsPaginated(String page_token) throws IOException, JSONException {
		List<List<String>> dataObjects = new ArrayList<List<String>>();
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(dos_server_url + "/dataobjects?page=" + page_token);
		HttpResponse response = httpClient.execute(get);
		
		JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
						.lines().collect(Collectors.joining()) );
		
		for (int i = 0; i < json.getJSONArray("data_objects").length(); i++) {
			List<String> temp = new ArrayList<String>();
			temp.add(json.getJSONArray("data_objects").getJSONObject(i).getString("id"));
			
			JSONArray urls = json.getJSONArray("data_objects").getJSONObject(i).getJSONArray("urls");
			for (int j = 0; j < urls.length(); j++) {
				temp.add(urls.getJSONObject(j).getString("url"));
			}
			dataObjects.add(temp);
		}
		
		return dataObjects;
	}
	
	// GET - list of data objects in a data bundle
	public List<Ga4ghDataObject> getDataObjectsInDataBundle(String data_bundle_id) throws MalformedURLException, JSONException, IOException {
		
		Ga4ghDataBundle dataBundle = getDataBundleById(data_bundle_id);
		List<Ga4ghDataObject> dataObjects = new ArrayList<Ga4ghDataObject>();
		dataBundle.getData_object_ids().forEach(id -> {
			try {
				dataObjects.add(getDataObjectById(id));
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		});
		
		return dataObjects;
	}

	// GET - list of data objects containing an alias
	public List<Ga4ghDataObject> getDataObjectsByAlias(String alias) throws MalformedURLException, JSONException, IOException {
		List<Ga4ghDataObject> dataObjects = new ArrayList<Ga4ghDataObject>();
		
		int next_page = 1;
		while (next_page != 0) {
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(dos_server_url + "/dataobjects?alias=" + alias + "&page=" + String.valueOf(next_page - 1));
			HttpResponse response = httpClient.execute(get);
			
			// Response from Get request
			JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
							.lines().collect(Collectors.joining()) );
			
			// Iterating over all objects from GET response and adding them to dataBundles List
			for (int i = 0; i < json.getJSONArray("data_objects").length(); i++) {
				dataObjects.add(new Ga4ghDataObject(json.getJSONArray("data_objects").getJSONObject(i)));
			}
			next_page = Integer.valueOf(json.getString("next_page_token")).intValue();
		}
		return dataObjects;
	}
	
	// GET - list of data objects containing any in a list of aliases
	public List<Ga4ghDataObject> getDataObjectsContainingAliases(List<String> aliases) throws MalformedURLException, JSONException, IOException {
		
		List<Ga4ghDataObject> dataObjects = new ArrayList<Ga4ghDataObject>();
		
		int next_page = 1;
		while (next_page != 0) {
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(dos_server_url + "/dataobjects?page=" + String.valueOf(next_page - 1));
			HttpResponse response = httpClient.execute(get);
			
			// Response from Get request
			JSONObject json = new JSONObject( new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
							.lines().collect(Collectors.joining()) );
			
			// Iterating over all objects from GET response and adding them to dataBundles List
			for (int i = 0; i < json.getJSONArray("data_objects").length(); i++) {
				JSONArray object_aliases = json.getJSONArray("data_objects").getJSONObject(i).getJSONArray("aliases");
				for (int j = 0; j < object_aliases.length(); j++) {
					if (aliases.contains(object_aliases.getString(j))) {
						dataObjects.add(new Ga4ghDataObject(json.getJSONArray("data_objects").getJSONObject(i)));
					}
				}
			}
			next_page = Integer.valueOf(json.getString("next_page_token")).intValue();
		}
		return dataObjects;
	}
	
	// POST - Data Object
	public String addDataObject(Ga4ghDataObject dataObject) throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(dos_server_url + "/dataobjects");

		Gson gson = new Gson();
		String json = gson.toJson(dataObject);
		json = "{\"data_object\":" + json + "}";
		System.out.println(json);
		
		post.setEntity(new StringEntity(json));
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		return new BufferedReader(new InputStreamReader( response.getEntity().getContent() ))
				.lines().collect(Collectors.joining());
	}
	
	// PUT - Data Object
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

package com.dnastack.collections.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.Data;

@Data
public class Ga4ghDataObject {

	private String id;
	private String name;
	private String size;
	private String created;
	private String updated;
	private String version;
	private String mimeType;
	private List<Checksum> checksums = new ArrayList<Checksum>();
	private List<URL> urls = new ArrayList<URL>();
	private String description;
	private List<String> aliases = new ArrayList<String>();
	
	public Ga4ghDataObject() {
		
	}
	
	public Ga4ghDataObject(String id, String name, String size, String created, String updated, String version,
			String mimeType, List<Checksum> checksums, List<URL> urls, String description, List<String> aliases) {
		super();
		this.id = id;
		this.name = name;
		this.size = size;
		this.created = created;
		this.updated = updated;
		this.version = version;
		this.mimeType = mimeType;
		this.checksums = checksums;
		this.urls = urls;
		this.description = description;
		this.aliases = aliases;
	}
	
	public Ga4ghDataObject(CollectionData data) {
		super();
		this.id = data.getId();
		this.name = data.getName();
		this.size = data.getSize();
		this.created = new DateTime().toString();
		this.updated = new DateTime().toString();
		this.version = data.getVersion();
		this.checksums = new ArrayList<Checksum>();
		this.mimeType = data.getMimeType();
		this.urls = data.getUrls();
		this.description = data.getDescription();
		this.aliases = new ArrayList<String>();
	}
	
	public Ga4ghDataObject(JSONObject json) throws JSONException {
		super();
		this.id = json.getString("id");
		this.name = json.getString("name");
		this.created = json.getString("created");
		this.updated = json.getString("updated");
		this.version = json.getString("version");
		this.mimeType = json.getString("mimeType");
		this.description = json.getString("description");
		
		this.checksums = new ArrayList<Checksum>();
		for (int i = 0; i < json.getJSONArray("checksums").length(); i++) {
			Checksum c = new Checksum();
			c.setChecksum(json.getJSONArray("checksums").getJSONObject(i).getString("checksum"));
			c.setType(json.getJSONArray("checksums").getJSONObject(i).getString("type"));
			this.checksums.add(c);
		}
		
		this.aliases = new ArrayList<String>();
		for (int i = 0; i < json.getJSONArray("aliases").length(); i++) {
			this.aliases.add(json.getJSONArray("aliases").getString(i));
		}
		
		this.urls = new ArrayList<URL>();
		for (int i = 0; i < json.getJSONArray("urls").length(); i++) {
			URL r = new URL();
			r.setUrl(json.getJSONArray("urls").getJSONObject(i).getString("url"));
			
			Map<String, String> system_metadata = new HashMap<String, String>();
			Iterator<String> iterator = json.getJSONArray("urls").getJSONObject(i).getJSONObject("system_metadata").keys();
		    while (iterator.hasNext()) {
		    	String currentKey = iterator.next();
		    	system_metadata.put(currentKey, json.getJSONObject("system_metadata").getString(currentKey));
		    }
		    r.setSystem_metadata(system_metadata);
			
		    Map<String, String> user_metadata = new HashMap<String, String>();
			iterator = json.getJSONArray("urls").getJSONObject(i).getJSONObject("user_metadata").keys();
		    while (iterator.hasNext()) {
		    	String currentKey = iterator.next();
		    	user_metadata.put(currentKey, json.getJSONObject("user_metadata").getString(currentKey));
		    }
		    r.setUser_metadata(user_metadata);
		    
			this.urls.add(r);
		}
		
		
		
		
	}
	
}

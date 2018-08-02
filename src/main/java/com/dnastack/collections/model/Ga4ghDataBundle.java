package com.dnastack.collections.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.joda.time.DateTime;

import lombok.Data;

@Data
public class Ga4ghDataBundle {

	private String id;
	private List<String> data_object_ids;
	private String created;
	private String updated;
	private String version;
	private List<Checksum> checksums;
	private String description;
	private List<String> aliases;
	private Map<String, String> system_metadata;
	private Map<String, String> user_metadata;
	
	public void addData_object_id(String data_object_id) {
		data_object_ids.add(data_object_id);
	}
	
	public void removeData_object_id(String data_object_id) {
		data_object_ids.remove(data_object_id);
	}
	
	public Ga4ghDataBundle() {
		
	}
	
	public Ga4ghDataBundle(String id, List<String> data_object_ids, String created, String updated, String version,
			List<Checksum> checksums, String description, List<String> aliases, Map<String, String> system_metadata,
			Map<String, String> user_metadata) {
		super();
		this.id = id;
		this.data_object_ids = data_object_ids;
		this.created = created;
		this.updated = updated;
		this.version = version;
		this.checksums = checksums;
		this.description = description;
		this.aliases = aliases;
		this.system_metadata = system_metadata;
		this.user_metadata = user_metadata;
	}
	
	public Ga4ghDataBundle(Collection collection) {
		super();
		this.id = collection.getId();
		this.data_object_ids = collection.getObjects();
		this.created = new DateTime().toString();
		this.updated = new DateTime().toString();
		this.version = "1.0.0";
		this.checksums = new ArrayList<Checksum>();
		this.aliases = new ArrayList<String>();
		this.system_metadata = new HashMap<String, String>();
		system_metadata.put("name", collection.getName());
		this.user_metadata = new HashMap<String, String>();
	}
	
	public Ga4ghDataBundle(JSONObject json) throws JSONException {
		super();
		this.id = json.getString("id");
		this.created = json.getString("created");
		this.updated = json.getString("updated");
		this.version = json.getString("version");
		
		this.data_object_ids = new ArrayList<String>();
		for (int i = 0; i < json.getJSONArray("data_object_ids").length(); i++) {
			this.data_object_ids.add(json.getJSONArray("data_object_ids").getString(i));
		}
		
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
		
		this.system_metadata = new HashMap<String, String>();
		Iterator<String> iterator = json.getJSONObject("system_metadata").keys();
	    while (iterator.hasNext()) {
	    	String currentKey = iterator.next();
	    	system_metadata.put(currentKey, json.getJSONObject("system_metadata").getString(currentKey));
	    }
		
		this.user_metadata = new HashMap<String, String>();
		iterator = json.getJSONObject("user_metadata").keys();
	    while (iterator.hasNext()) {
	    	String currentKey = iterator.next();
	    	system_metadata.put(currentKey, json.getJSONObject("user_metadata").getString(currentKey));
	    }
	}
	
}

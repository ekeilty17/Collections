package com.dnastack.collections.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnastack.collections.model.Collection;
import com.dnastack.collections.model.CollectionData;
import com.dnastack.collections.model.Ga4ghDataBundle;
import com.dnastack.collections.model.Ga4ghDataObject;
import com.dnastack.collections.service.CollectionsService;

@RestController
public class CollectionsController {

	public final String dos_server_url = "http://localhost:8080";

	@Autowired
	CollectionsService collectionsService;
	
	@RequestMapping(value = "/collections", method = RequestMethod.GET)
	public Map<String, List<String>> getCollections(
			@RequestParam(value = "tag", required = false) String tag,
			@RequestParam(value = "page", required = false) String page
	) throws IOException, JSONException {
		if (page == null) {
			page = "0";
		}
		// Filter by tags
		if (tag != null) {
			List<Ga4ghDataBundle> ga4gh = collectionsService.getDataBundlesByAlias(tag);
			Map<String, List<String>> collections = new HashMap<String, List<String>>();
			ga4gh.forEach(g -> collections.put(g.getId(), g.getData_object_ids()));
			return collections;
		}
		return collectionsService.getDataBundlesPaginated(page);
	}

	@RequestMapping(value = "/collections", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String addCollection(@RequestBody Collection collection) throws ClientProtocolException, IOException {
		return collectionsService.addDataBundle(new Ga4ghDataBundle(collection));
	}
	
	@RequestMapping("/collections/{collection_id}")
	public Ga4ghDataBundle getCollectionById(@PathVariable String collection_id) throws MalformedURLException, JSONException, IOException {
		return collectionsService.getDataBundleById(collection_id);
	}

	@RequestMapping(value = "/collections/{collection_id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String addObjectToCollection(@PathVariable String collection_id, @RequestBody CollectionData data)
			throws ClientProtocolException, IOException, JSONException {
		//String response = collectionsService.addDataObject(new Ga4ghDataObject(data));
		collectionsService.addDataObject(new Ga4ghDataObject(data));
		return collectionsService.updateDataBundle(collection_id, new Ga4ghDataObject(data));
	}
	
	@RequestMapping(value = "/collections/{collection_id}", method = RequestMethod.DELETE)
	public String deleteCollection(@PathVariable String collection_id) throws ClientProtocolException, IOException {
		return collectionsService.deleteDataBundle(collection_id);
	}

	@RequestMapping(value = "/collections/{collection_id}/{data_id}", method = RequestMethod.DELETE)
	public String deleteObjectFromCollection(@PathVariable String collection_id, @PathVariable String data_id)
			throws MalformedURLException, JSONException, IOException {
		return collectionsService.removeDataObjectFromDataBundle(collection_id, data_id);
	}
	
	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public List<List<String>> getDataObjects(@RequestParam(value = "page_token", required = true) String page_token) throws IOException, JSONException {
		return collectionsService.getDataObjectsPaginated(page_token);
	}
	
}

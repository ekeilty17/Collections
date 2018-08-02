package com.dnastack.collections.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<String> getCollections() throws IOException, JSONException {
		return collectionsService.getDataBundleIds();
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String addCollection(@RequestBody Collection collection) throws ClientProtocolException, IOException {
		return collectionsService.addDataBundle(new Ga4ghDataBundle(collection));
	}

	@RequestMapping("/collection/{collection_id}")
	public Ga4ghDataBundle getCollectionById(@PathVariable String collection_id) throws MalformedURLException, JSONException, IOException {
		return collectionsService.getDataBundleById(collection_id);
	}

	@RequestMapping(value = "/collection/{collection_id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String addObjectToCollection(@PathVariable String collection_id, @RequestBody CollectionData data)
			throws ClientProtocolException, IOException, JSONException {
		return collectionsService.updateDataBundle(collection_id, new Ga4ghDataObject(data));
	}

	@RequestMapping(value = "/collection/{collection_id}", method = RequestMethod.DELETE)
	public String deleteCollection(@PathVariable String collection_id) throws ClientProtocolException, IOException {
		return collectionsService.deleteDataBundle(collection_id);
	}

	@RequestMapping(value = "/collection/{collection_id}/{data_id}", method = RequestMethod.DELETE)
	public String deleteObjectFromCollection(@PathVariable String collection_id, @PathVariable String data_id)
			throws MalformedURLException, JSONException, IOException {
		return collectionsService.removeDataObjectFromDataBundle(collection_id, data_id);
	}

}

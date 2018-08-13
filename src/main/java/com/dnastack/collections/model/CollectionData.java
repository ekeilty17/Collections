package com.dnastack.collections.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionData {
	
	private String id;
	private String name;
	private String size;
	private String version;
	private String mimeType;
	private List<String> urls = new ArrayList<String>();
	private String description;
	
}

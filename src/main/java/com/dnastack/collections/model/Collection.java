package com.dnastack.collections.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collection {
	
	private String id;
	private String name;
	private List<String> data_object_ids = new ArrayList<String>();
	private List<String> tags = new ArrayList<String>();
	
}

package com.dnastack.collections.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Checksum {

	private String checksum;
	private String type;
	
	public Checksum() {
		
	}
	
	public Checksum(String checksum, String type) {
		super();
		this.checksum = checksum;
		this.type = type;
	}
	
}

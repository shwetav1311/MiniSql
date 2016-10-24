package com.iiit.sql;

import java.util.Vector;

public class TableMetadata {
	
	String tableName;
	int attNum;
	Vector attributes;
	
	
	public TableMetadata() {
		// TODO Auto-generated constructor stub
		tableName = new String();
		attNum  = 0;
		attributes = new Vector<>();
	}


	public TableMetadata(String tableName, int attNum, Vector attributes) {
		super();
		this.tableName = tableName;
		this.attNum = attNum;
		this.attributes = attributes;
	}


	public TableMetadata(TableMetadata tableMetadata) {
		// TODO Auto-generated constructor stub
		this.attNum = tableMetadata.attNum;
		this.tableName = tableMetadata.tableName;
		this.attributes = new Vector<>(tableMetadata.attributes);
	}


	public TableMetadata(String tableName, Vector attributes) {
		// TODO Auto-generated constructor stub
		this.tableName = tableName;
		this.attributes = new Vector<>(attributes);
	}
	
	

}

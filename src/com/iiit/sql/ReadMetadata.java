package com.iiit.sql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class ReadMetadata {
	
	//Vector<TableMetadata> tables;
	HashMap<String, TableMetadata> tables;
	
	
	
	
	public ReadMetadata() {
		super();
		
		tables  = new HashMap<>();
	}




	void readFile(String fileName) throws FileNotFoundException,IOException
	{
	
			BufferedReader db = new BufferedReader(new FileReader(fileName));
		
			String line;
			boolean begin_table=false;
		
			while((line=db.readLine())!= null)
			{
				//System.out.println(line);
				if(line.equals(Constants.BEGIN_TABLE))
				{
					TableMetadata meta = new TableMetadata();
					meta.tableName = db.readLine();
					int cnt=0;
					while(!(line=db.readLine()).equals(Constants.END_TABLE))
					{
						meta.attributes.add(line);
						cnt++;
						
					}
					meta.attNum=cnt;
//					System.out.println(meta.tableName);
					tables.put(meta.tableName, meta);
				}
			}
		
		
	}




	public HashMap<String, TableMetadata> getTables() {
		return tables;
	}




	public void setTables(HashMap<String, TableMetadata> tables) {
		this.tables = tables;
	}
	
	

}

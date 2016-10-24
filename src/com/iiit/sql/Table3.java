package com.iiit.sql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

public class Table3 {
	
		Vector attributes;
		int colNum;
		Vector<Vector> tab;
		HashMap<String, Integer> colMap;
		HashMap<String, HashMap> joinedTables;
		
		
		public Table3(Vector attributes, int colNum, Vector<Vector> tab) {
			super();
			this.attributes = attributes;
			this.colNum = colNum;
			this.tab = tab;
		}
		
		
		public Table3() {
			super();
			this.attributes = new Vector<>();
			this.colNum = 0;
			this.tab = new Vector<>();
			this.colMap = new HashMap<>();
			this.joinedTables = new HashMap<>();
		}
		
		
		public void createTable(TableMetadata meta,String fileName) throws FileNotFoundException,IOException
		{
			attributes = meta.attributes;
			colNum = meta.attNum;
			
			
			for(int i =0;i<meta.attributes.size();i++)
			{
				colMap.put(meta.tableName + meta.attributes.get(i).toString(),i);
			}
			
			joinedTables.put(meta.tableName, colMap);
			
			BufferedReader buff = new BufferedReader(new FileReader(fileName));
			String line;
			
			while((line = buff.readLine())!=null)
			{
				String tokens[] = line.split(",");
				Vector tuple = new Vector<>(colNum);
				
				 for (int i = 0; i <tokens.length; i++) {
				        tuple.add(tokens[i]);
				    } 
				 
				 tab.add(tuple);
			}
		
		
			
		}
		
		public void join(Table3 tab2)
		{
			Vector<Vector> temp = new Vector();
			
			for(int i=this.colNum;i<this.colNum+tab2.colNum;i++)
			{
				colMap.put(tab2.attributes.get(i-tab2.colNum).toString(),i);
			}
			
			//joinedTables.put(tab2., colMap);
			
			for(int i=0;i<this.tab.size();i++)
			{
				Vector tuple1 = (Vector) this.tab.get(i);
				
				Vector total = null;
				for(int j = 0;j<tab2.tab.size();j++)
				{
					total = new Vector<>(tuple1);
					total.addAll((Vector) tab2.tab.get(j));
					temp.add(total);
				}		
			}
			
			this.tab = new Vector<>(temp);
			this.attributes.addAll(tab2.attributes);
			this.colNum = this.colNum + tab2.colNum;
		}
		
		
		public void printTable()
		{
			for(int i = 0;i<this.attributes.size();i++)
			{
				System.out.print(this.attributes.get(i)+"\t");
			}
			System.out.println();
			
			for(int i = 0;i<this.tab.size();i++)
			{
				Vector tuple = this.tab.get(i);
				for(int j = 0;j<tuple.size();j++)
				{
					System.out.print(tuple.get(j)+"\t");
				}
				System.out.println();
			}
		}
		
		
	
}

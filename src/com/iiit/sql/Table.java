package com.iiit.sql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

public class Table {
	
		TableMetadata meta;
		Vector<Vector> tab;
		HashMap<String, Integer> colMap;
		Vector joinedTables;
		
		
		public Table() {
			super();
			
		}
		
		
		public void createTable(TableMetadata meta,String fileName) throws FileNotFoundException,IOException
		{
			this.meta = meta;
			this.tab = new Vector<>();
			this.colMap = new HashMap<>();
			this.joinedTables = new Vector();
			
			for(int i =0;i<meta.attributes.size();i++)
			{
				colMap.put(meta.tableName + "."+ meta.attributes.get(i).toString(),i);
			}
			
			
			joinedTables.add(meta.tableName);
			
			BufferedReader buff = new BufferedReader(new FileReader(fileName));
			String line;
			
			while((line = buff.readLine())!=null)
			{
				String tokens[] = line.split(",");
				Vector tuple = new Vector<>(meta.attNum);
				
				 for (int i = 0; i <tokens.length; i++) {
				        tuple.add(tokens[i].replace("\"",""));
				    } 
				 
				 tab.add(tuple);
			}
		
			
		}
		
		public void join(Table tab2)
		{
			Vector<Vector> temp = new Vector();
			
			for(int i=this.meta.attNum;i<this.meta.attNum+tab2.meta.attNum;i++)
			{
				String colName = tab2.meta.tableName +"." + tab2.meta.attributes.get(i-this.meta.attNum).toString();
				colMap.put(tab2.meta.tableName +"." + tab2.meta.attributes.get(i-this.meta.attNum).toString(),i);
			}
			
			joinedTables.add(tab2.meta.tableName);
			
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
			this.meta.attributes.addAll(tab2.meta.attributes);
			this.meta.attNum = this.meta.attNum + tab2.meta.attNum;
		}
		
		
		public void printTable()
		{
			int len = 0;
			System.out.println();
			for(int i = 0;i<this.meta.attributes.size();i++)
			{
				if(i!= this.meta.attributes.size()-1)
					System.out.printf("| %-15s",this.meta.attributes.get(i));
				else
					System.out.printf("| %-15s |",this.meta.attributes.get(i));
				
				len = len+17;
			}
			System.out.println();
			
			
			for(int i = 0;i<len+2;i++)
			{
				System.out.print("-");
			}
			System.out.println();
			if(this.tab.size()==0)
			{
				return;
			}
			
			
			
			for(int i = 0;i<this.tab.size();i++)
			{
				Vector tuple = this.tab.get(i);
				for(int j = 0;j<tuple.size();j++)
				{
					if(j!=tuple.size()-1)
						System.out.printf("| %-15s",tuple.get(j).toString());
					else
						System.out.printf("| %-15s |",tuple.get(j).toString());
				}
				System.out.println();
			}
			
			
			for(int i = 0;i<len+2;i++)
			{
				System.out.print("-");
			}
			
			System.out.println();
		}
		
		
		public boolean isValidOperand(String colName)
		{
			
				boolean flag = false;
				
				if(colMap.containsKey(colName))
				{
					return true;
				}
				
				for(int i=0;i<joinedTables.size();i++)
				{
					String thetaColName = joinedTables.get(i)+"."+colName; 
					if(colMap.containsKey(thetaColName))
					{
						
						if(!flag)
						{
							flag=true;
						}else
						{
							System.out.println("Column '"+colName+"' in field list is ambiguous");
							return false;
						}
					}
				}
				
				if(flag)
					return true;
				else
				{
					System.out.println("Unknown '"+colName+"' column in field list");
					return false;
				}
					
		}
		
		
		public int getColumnIndex(String colName)
		{
			
			if(colMap.containsKey(colName))
			{
				return colMap.get(colName);
			}
			
			for(int i=0;i<joinedTables.size();i++)
			{
				String thetaColName = joinedTables.get(i)+"."+colName; 
				
				if(colMap.containsKey(thetaColName))
				{
					return colMap.get(thetaColName);
				}
			}
			
			return -1;
		}
	
}

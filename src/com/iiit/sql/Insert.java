package com.iiit.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.gibello.zql.ZInsert;
import org.gibello.zql.ZStatement;

public class Insert {

	

	public  ReadMetadata meta;
	public  Table finalTable;
	
	
	public Insert(ReadMetadata meta, Table finalTable) {
		super();
		this.meta = meta;
		this.finalTable = finalTable;
	}


	public void processQuery(ZStatement st)
	{
		  String tableName= ((ZInsert)st).getTable();
		  Vector columns = ((ZInsert)st).getValues();
		  
		   if(!meta.tables.containsKey(tableName))
			{
				printError(tableName + " table not found");
				return ;
			}
			
		   
		   TableMetadata t = new TableMetadata(meta.tables.get(tableName));
		   
		   if(t.attributes.size()!=columns.size())
		   {
			   printError("Invalid no.of columns");
			   return;
		   }
			
		    insertIntoTable(tableName, columns);
		    
					
		
	}
	
	
	public void insertIntoTable(String tableName,Vector cols) 
	{
		PrintWriter pw = null;
		StringBuilder sb = new StringBuilder();
		try {
			pw = new PrintWriter(new FileWriter(Constants.HOME+tableName+".csv",true));
			
			BufferedReader br = new BufferedReader(new FileReader(Constants.HOME+tableName+".csv"));     
			if (br.readLine() != null) {
//			    System.out.println("No errors, and file empty");
			    sb.append("\n"); 
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			printError("File not found");
			//e.printStackTrace();
			return;
		}
		
		
		 	
	    sb.append(cols.get(0).toString().replace(" ","").replace(")","").replace("(",""));
		
		for(int i=1;i<cols.size();i++)
		{
			sb.append(",");
			sb.append(cols.get(i).toString().replace(" ","").replace(")","").replace("(",""));
		}
		//sb.append("\n");
		

        pw.write(sb.toString());
        pw.close();
		printError("Insert Success");
	}

	

	public  void printError(Object error)
	{
		System.out.println(error);
	}
}

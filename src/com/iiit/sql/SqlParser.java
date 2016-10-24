package com.iiit.sql;

import java.io.IOException;
import java.io.InputStream;

import org.gibello.zql.ParseException;
import org.gibello.zql.ZDelete;
import org.gibello.zql.ZInsert;
import org.gibello.zql.ZQuery;
import org.gibello.zql.ZStatement;
import org.gibello.zql.ZqlParser;

public class SqlParser {

	static ReadMetadata meta;
	static Table finalTable;
	
	public static void main(String args[]) 
	{
		meta = new ReadMetadata();
		finalTable = null;
        try {
        	
			meta.readFile(Constants.META_FILE);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Metadata File Not Found");
			return;
		}
	   
		 ZqlParser p = null;
        
		 while(true)
		 {
			 System.out.print("mysql> ");
			 finalTable = null;
			 
			 InputStream in = System.in;			 
			 p = new ZqlParser(in);
			 	 
			 try {
				checkInput(p.readStatement());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				
				String query=String.valueOf(p.originalString());
			
				if(!checkOthers(query.trim()))
				{
					printError(Constants.QUERY_ERROR);
				}
				
			}catch(NullPointerException e)
			 {
				break;
			 }
			
		 }
        
		
	}	
	
	public static void checkInput(ZStatement st) throws NullPointerException
	{
	
		try{
			 ZQuery x = (ZQuery)st;
			 Query Q = new Query(meta,null);
			 Q.processQuery(st);
			 
			
		}catch(ClassCastException e)
		{
			try
			{
				ZInsert x = (ZInsert)st;
				 Insert I = new Insert(meta,null);
				 I.processQuery(st);
				 
			}catch(ClassCastException e1)
			{
				
				 try
				 {
					 ZDelete x = (ZDelete)st;
					 Delete D = new Delete(meta,null);
					 D.processQuery(st);
					 
				 }catch(ClassCastException e2)
				 {
					 printError(Constants.QUERY_ERROR);
				 }
				 
			}
			
			
			
		}
		
	}
	
	public static boolean checkOthers(String input)
	{
		input = input.replaceAll("\\s+"," ");
		
		OtherOps ops = new OtherOps(meta);
		
		if(input.toUpperCase().startsWith("DROP TABLE "))
		{
			ops.drop(input);
			
		}else if(input.toUpperCase().startsWith("CREATE TABLE "))
		{
			ops.create(input);
			
		}else if(input.toUpperCase().startsWith("TRUNCATE TABLE "))
		{
			ops.truncate(input);
			
		}else
		{
			return false;
		}
		
		return true;
		
		
	}
	
	public static void printError(String error)
	{
		System.out.println(error);
	}
}

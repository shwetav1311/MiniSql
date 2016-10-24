package com.iiit.sql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class OtherOps {
	
	
	public  ReadMetadata meta;
	public OtherOps(ReadMetadata meta) {
		super();
		this.meta = meta;
	}

	public OtherOps()
	{
		
	}
	
	public void truncate(String line)
	{
		String tableName  = getTableName(line);
		
		if(tableName==null)
		{
			return;
		}else
		{
			if(!meta.tables.containsKey(tableName))
			{
				printError(tableName+ " table not found");
				return;
			}
			
			PrintWriter pw = null;
			
			try {
				pw = new PrintWriter(new FileWriter(Constants.HOME+tableName+".csv"));
				printError("Success");
				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				printError("File Not Found");
				//e.printStackTrace();
			}
			
			
		}
		
	}
	public void drop(String line)
	{
		String tableName  = getTableName(line);
		
		if(tableName==null)
		{
			return;
		}else
		{
			if(!meta.tables.containsKey(tableName))
			{
				printError(tableName+ " table not found");
				return;
			}else
			{
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(Constants.HOME+tableName+".csv"));
					if (br.readLine() != null) {
						printError("Table is not empty");
						br.close();
						return;
					}else
					{
						meta.tables.remove(tableName);
						writeToMetaFile();
						br.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					printError("File not found");
				}     
				
				
				
			}
		}
	}
	
	public void create(String line)
	{
		int index  = "CREATE TABLE ".length();
		
		//index++;
		
		String tableAtt = line.substring(index);
		
		int begin = tableAtt.indexOf("(");
		int end = tableAtt.indexOf(")");
		
		if(begin==-1 || end==-1)
		{
			printError("Brackets missing");
			return;
		}
		
		String remaining = tableAtt.substring(end+1).replace(" ","");

		if((remaining.length()==1 && remaining.equals(";")) || remaining.length()==0)
		{
			
		}else
		{
			printError(Constants.QUERY_ERROR);
			return;
		}
			

		
		
		String tableName = tableAtt.substring(0,begin).trim();
		
		if(tableName.contains(" "))
		{
			printError("Invalid table name");
			return;
		}else
		{
			if(meta.tables.containsKey(tableName))
			{
				printError("Table "+tableName+ " already exists");
				return;
			}else
			{
			
				String atts = tableAtt.substring(begin+1,end).trim();
				
				Vector finalAtributes = new Vector<>();
				
				String tokens[] = atts.split(",");
				
				for(int i=0;i<tokens.length;i++)
				{
					String inTokens[] = tokens[i].trim().replace("\"","").split(" ");
					
					if(inTokens.length!=2)
					{
						printError("Invalid attributes");
						return;
					}else
					{	
						if(!inTokens[1].trim().equalsIgnoreCase("integer"))
						{
							printError("Invalid data type");
							return;
						}
						
						finalAtributes.add(inTokens[0].trim());
					}
				}
				
				TableMetadata newMeta =  new TableMetadata(tableName,finalAtributes);
				meta.tables.put(tableName, newMeta);
				writeToMetaFile();
				
				try {
					PrintWriter writer = new PrintWriter(tableName+".csv");
					writer.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					printError("Could not create file");
					
				}
				
				
				
			}
		}
		
		
		printError("Table created successfully");
		
		
	}
	
	public static void printError(Object error)
	{
		System.out.println(error);
	}
	
	
	public void writeToMetaFile()
	{
		
		if(meta.tables.values().size()==0)
		{
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(new FileWriter(Constants.META_FILE));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pw.close();
			return;
		}
		
		
		StringBuilder sb = new StringBuilder();
		
		for (TableMetadata value : meta.tables.values()) {
			sb.append(Constants.BEGIN_TABLE);
			sb.append("\n");
			sb.append(value.tableName);
			sb.append("\n");
			
			for(Object att: value.attributes)
			{
				sb.append(att.toString());
				sb.append("\n");
			}
			
			sb.append(Constants.END_TABLE);
			sb.append("\n");
			
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(Constants.META_FILE));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			printError("Metadata File not found");
			//e.printStackTrace();
			return;
		}

	    pw.write(sb.toString());
	    pw.close();
	}
	
	public String getTableName(String line)
	{
		
		String tableName = null;
		String tokens[] = line.split(" ");
		if((tokens.length==4 && tokens[3].equals(";") )  )
		{
			System.out.println("Valid");
			//tableName
			tableName = tokens[2];
			printError("tableNAme"+tableName);
			
		}else if(tokens.length==3)
		{
			tableName = tokens[2];
			if(tableName.lastIndexOf(";")==tableName.length()-1)
			{
				tableName =tableName.substring(0, tableName.length()-1);
			}
			
		}
		else{
			printError(Constants.QUERY_ERROR);
			return null;
		}
		
		return tableName;
	}

}



package com.iiit.sql;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.gibello.zql.ZDelete;
import org.gibello.zql.ZExpression;
import org.gibello.zql.ZStatement;

public class Delete {

	public  ReadMetadata meta;
	public  Table finalTable;
	
	
	public Delete(ReadMetadata meta, Table finalTable) {
		super();
		this.meta = meta;
		this.finalTable = finalTable;
	}


	public void processQuery(ZStatement st)
	{
		  String tableName = ((ZDelete)st).getTable();
		  ZExpression where = (ZExpression)((ZDelete)st).getWhere();

		  if(!meta.tables.containsKey(tableName))
			{
				printError(tableName + " table not found");
				return ;
			}
			
		  TableMetadata t = new TableMetadata(meta.tables.get(tableName));
		  
		  Table table = new Table();
		  try {
			table.createTable(t,Constants.HOME+tableName+".csv");
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			printError(" File not found");
			e.printStackTrace();
		}
		  
		  finalTable = table;
		  if(where!=null)
		  {
			  processWhere(where);
		  }else
		  {
			  OtherOps ops = new OtherOps(meta);
			  ops.truncate("TRUNCATE TABLE "+tableName+";");
		  }
		  
	}
	
	
	private   boolean processWhere(ZExpression where)
	{
		
		if(where.getOperator().equals("AND") || where.getOperator().equals("OR"))
		{
			ZExpression exp1 = (ZExpression)where.getOperands().get(0);
			ZExpression exp2 = (ZExpression)where.getOperands().get(1);
			
			String op1 = exp1.getOperand(0).toString();
			String op2 = exp1.getOperand(1).toString().replace(" ","").replace(")","").replace("(","");
			String op = exp1.getOperator();
			
			Vector<Exp> exps = new Vector<>();
			
			Exp exp = null;
			if(finalTable.isValidOperand(op1))
			{
				exp = new Exp(finalTable.getColumnIndex(op1),op,Integer.parseInt(op2));
			}else
			{
				printError("Invalid Column Name in where");
				return false;
			}
			
			exps.add(exp);
			
			op1 = exp2.getOperand(0).toString();
			op2 = exp2.getOperand(1).toString().replace(" ","").replace(")","").replace("(","");
			op = exp2.getOperator();
			
			if(finalTable.isValidOperand(op1))
			{
				exp = new Exp(finalTable.getColumnIndex(op1),op,Integer.parseInt(op2));
			}else
			{
				printError("Invalid Column Name in where");
				return false;
			}
			
			exps.add(exp);
			
			filterTableForWhere(exps, where.getOperator());
			
		}else
		{
			Vector<Exp> exps = new Vector<>();
			
			String op1 = where.getOperand(0).toString();
			String op2 = where.getOperand(1).toString().replace(" ","").replace(")","").replace("(","");
			String op = where.getOperator();
			
			
			
			if(finalTable.isValidOperand(op1))
			{
				Exp exp = new Exp(finalTable.getColumnIndex(op1),op,Integer.parseInt(op2));
				exps.add(exp);
				filterTableForWhere(exps,"NULL");
			}else
			{
				printError("Invalid Column Name in where");
				return false;
			}
			
		}
			
			
		return true;
			
		
	}
	
	
	private  void filterTableForWhere(Vector<Exp> exps,String logical)
	{
		Vector<Vector> tempTable =  new Vector<>();
		 
		
		for(int i=0;i<finalTable.tab.size();i++)
		{
			Vector tuple = finalTable.tab.get(i);
			boolean flag1=false;
			boolean flag2=false;
			
			int colIndex = exps.get(0).index;
			
			flag1=exps.get(0).evaluateExp(Integer.parseInt((String) tuple.get(colIndex)));
			
			if(exps.size()>1)
			{
				colIndex = exps.get(1).index;
				flag2=exps.get(1).evaluateExp(Integer.parseInt((String) tuple.get(colIndex)));
			}
			if(logical.equals("AND"))
			{
				if(flag1 && flag2)
				{
					
				}else
				{
					tempTable.add(tuple);
				}
				
			}else if (logical.equals("OR"))
			{
				if(flag1)
				{
					
				}else if(flag2)
				{
					
				}else
				{
					tempTable.add(tuple);
				}
			}else
			{
				if(!flag1)
				{
					tempTable.add(tuple);
				}
			}
			
		}
		
		if(finalTable.tab.size()!=tempTable.size())
		{
			finalTable.tab=tempTable;
			writeToFile();
		}else
		{
			printError("No rows deleted");
		}
		
		
		
	}
	
	public void writeToFile() 
	{
		String tableName = finalTable.meta.tableName;
		
		if(finalTable.tab.size()==0)
		{
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(new FileWriter(Constants.HOME+tableName+".csv"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pw.close();
			printError("Delete Success");
			return;
		}
		
		
		for(int i = 0;i<finalTable.tab.size();i++)
		{
			Vector tuple = finalTable.tab.get(i);
			
			StringBuilder sb = new StringBuilder();
			 
	        sb.append(tuple.get(0).toString());
		
			for(int j=1;j<tuple.size();j++)
			{
				sb.append(",");
				sb.append(tuple.get(j).toString());
			}
			sb.append("\n");
			
			PrintWriter pw = null;
		
			try {
				
				if(i==0)
					pw = new PrintWriter(new FileWriter(Constants.HOME+tableName+".csv"));
				else
					pw = new PrintWriter(new FileWriter(Constants.HOME+tableName+".csv",true));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				printError("File not found");
				//e.printStackTrace();
				return;
			}

	        pw.write(sb.toString());
	        pw.close();
			
		}
		printError("Delete Success");
	}
	
	public  void printError(Object error)
	{
		System.out.println(error);
	}
	
}

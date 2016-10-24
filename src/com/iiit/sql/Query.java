package com.iiit.sql;

import java.util.Vector;

import org.gibello.zql.ParseException;
import org.gibello.zql.ZExpression;
import org.gibello.zql.ZFromItem;
import org.gibello.zql.ZQuery;
import org.gibello.zql.ZSelectItem;
import org.gibello.zql.ZStatement;
import org.gibello.zql.ZqlParser;

public class Query {

	public Query(ReadMetadata meta, Table finalTable) {
		super();
		this.meta = meta;
		this.finalTable = finalTable;
	}




	public  ReadMetadata meta;
	public  Table finalTable;
	
	
	public Query()
	{
		
	}
	
	public void processQuery(ZqlParser p)
	{
		try {
			ZStatement st = p.readStatement();
			 Vector<ZSelectItem> sel = ((ZQuery)st).getSelect(); // SELECT part of the query
			 Vector<ZFromItem> from = ((ZQuery)st).getFrom();  // FROM part of the query
			 ZExpression where = (ZExpression)(((ZQuery)st).getWhere()); 
			 
			 boolean isDistinct = ((ZQuery)st).isDistinct();

			 if(processFrom(from))
			 {
				 if(where!=null)
				 {
					 if(processWhere(where))
					 {
						 processSelect(sel,isDistinct);
					 }
				 }else
				 {
					 processSelect(sel,isDistinct);
				 }
				
			 }
			
	        
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void processQuery(ZStatement st)
	{
		Vector<ZSelectItem> sel = ((ZQuery)st).getSelect(); // SELECT part of the query
		 Vector<ZFromItem> from = ((ZQuery)st).getFrom();  // FROM part of the query
		 ZExpression where = (ZExpression)(((ZQuery)st).getWhere()); 
		 
		 boolean isDistinct = ((ZQuery)st).isDistinct();

		 if(processFrom(from))
		 {
			 if(where!=null)
			 {
				 if(processWhere(where))
				 {
					 processSelect(sel,isDistinct);
				 }
			 }else
			 {
				 processSelect(sel,isDistinct);
			 }
			
		 }
	}
	
	public  boolean processFrom(Vector from)
	{
		Table table = new  Table();
		int i;
		for(i = 0;i<from.size();i++)
		{
			if(!meta.tables.containsKey(from.get(i).toString()))
			{
				printError("Table '"+from.get(i).toString()+"' doesn't exist");
				return false;
			}
		}
		//All tables are present in metadata;
		
		
		try {
			
			TableMetadata t = new TableMetadata(meta.tables.get(from.get(0).toString()));
			
			
			table.createTable(t,Constants.HOME+from.get(0).toString()+".csv");
			
			if(from.size()>1)
			{
				for(i = 1;i<from.size();i++)
				{
					Table table1 = new  Table();
					TableMetadata t1 = new TableMetadata(meta.tables.get(from.get(i).toString()));
					table1.createTable(t1,Constants.HOME+from.get(i).toString()+".csv");
					table.join(table1);
				}
			}
			
			
			//this is the final table;
			finalTable = table; 

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			printError("File not found");
			return false;
			
		}	
		return true;
		
	}
	
	public  void processSelect(Vector<ZSelectItem> select, boolean isDistinct)
	{
		Vector columns = new Vector<>();
		
		
		if(select.size()==1)
		{
			if(select.get(0).toString().equals("*"))
			{
				displayTable();
				return;
			}else
			{
				String name;
				if(select.get(0).getTable()==null)
				{
					name = select.get(0).getColumn();
				}else
				{
					name = select.get(0).getTable() + "." + select.get(0).getColumn();
				}
				
				
				columns.add(name);
			}
			
		}else
		{
			for(int i = 0;i<select.size();i++)
			{
				String name;
				if(select.get(i).getTable()==null)
				{
					name = select.get(i).getColumn();
				}else
				{
					name = select.get(i).getTable() + "."+select.get(i).getColumn();
				}
				columns.add(name);
			}
		
			
		}
		
		if(!filterColumnsForSelect(columns))
		{
			return;
		}
		
		boolean aggFlag=false;
		
		int size = columns.size();
		
		Vector<Float> aggResult = new Vector<>(size);
		
		for(int i = 0;i<select.size();i++)
		{
			if(select.get(i).getAggregate()!=null)
			{
				aggFlag=true;
				aggResult.add(computeAggregate(i, select.get(i).getAggregate().toUpperCase()));
			}else
			{
				aggResult.add(null);
			}
		}
		
		if(aggFlag==true)
		{
			Vector<Vector> temp = new Vector();
			for(int i = 0;i<aggResult.size();i++)
			{
				if(aggResult.get(i)==null)
				{
					float val = Float.parseFloat((String) this.finalTable.tab.get(0).get(i));
					aggResult.set(i, (float) val);
				}
			}
			
			temp.add(aggResult);
			
			Vector newAtt = new Vector<>();
			
			for(int i = 0;i<select.size();i++)
			{
				
				newAtt.add( select.get(i).toString());
			}
			
			
			finalTable.tab = temp;
			finalTable.meta.attributes = newAtt;
			
		}
//		
		
		if(isDistinct)
		{
			Vector<Vector> temp = new Vector(finalTable.tab);
			for(int i=0;i<temp.size();i++)
			{
				Vector<Integer> tuple = temp.get(i);
				
				boolean flag=false;
				for(int j = i+1;j<temp.size();j++)
				{
					Vector<Integer> tuple2 = temp.get(j);
					if(tuple.equals(tuple2))
					{
						flag=true;
//						System.out.println("Size before remove"+temp.size());
						temp.remove(j);
						j--;
//						System.out.println("Size after remove"+temp.size());
						
					}
				}
			}
			
			finalTable.tab = temp;
			
		}
		displayTable();
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
	
	
	
	private   void filterTableForWhere(Vector<Exp> exps,String logical)
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
//				System.out.println(colIndex);
				flag2=exps.get(1).evaluateExp(Integer.parseInt((String) tuple.get(colIndex)));
			}
			if(logical.equals("AND"))
			{
				if(flag1 && flag2)
				{
					tempTable.add(tuple);
				}
				
			}else if (logical.equals("OR"))
			{
				if(flag1 || flag2)
				{
					tempTable.add(tuple);
				}
			}else
			{
				if(flag1)
				{
					tempTable.add(tuple);
				}
			}
			
		}
		
		
		finalTable.tab=tempTable;
		
	}
	
	private   boolean filterColumnsForSelect(Vector columns)
	{
		
		Vector<Vector> tempTable =  new Vector<>();
		
		Vector<Integer>index = new Vector<>();
		
		
		for(int i = 0;i<columns.size();i++)
		{
			if(finalTable.isValidOperand(columns.get(i).toString()))
			{
				index.add(finalTable.getColumnIndex(columns.get(i).toString()));
				
			}else
			{
				return false;
			}
		}
		
		for(int i=0;i<finalTable.tab.size();i++)
		{
			Vector tuple = finalTable.tab.get(i);
			Vector newTuple = new Vector<>();
			for(int j = 0;j<index.size();j++)
			{
				newTuple.add(tuple.get(index.get(j)));
			}
			
			tempTable.add(newTuple);
		}
		
		
		finalTable.tab=tempTable;
		finalTable.meta.attributes = columns;
		finalTable.meta.attNum = columns.size();
		
		return true;
		
	}
	
	
	 public  float computeAggregate(int colIndex,String fname) {
		// TODO Auto-generated method stub
		
		if(fname.equals("AVG"))
		{
			float sum=0;
			int size = finalTable.tab.size();
			for(int i=0;i<size;i++)
			{
				int val = Integer.parseInt((String) finalTable.tab.get(i).get(colIndex));
				sum = sum + val;
			}
			
			return sum/size;
			
		}else if(fname.equals("MIN"))
		{
			int min= Integer.parseInt((String) finalTable.tab.get(0).get(colIndex));
			int size = finalTable.tab.size();
			for(int i=0;i<size;i++)
			{
				int val = Integer.parseInt((String) finalTable.tab.get(i).get(colIndex));
				if(min>val)
				{
					min=val;
				}
			}
			
			return min;
			
			
		}else if(fname.equals("MAX"))
		{
			int max= Integer.parseInt((String) finalTable.tab.get(0).get(colIndex));
			int size = finalTable.tab.size();
			for(int i=0;i<size;i++)
			{
				int val = Integer.parseInt((String) finalTable.tab.get(i).get(colIndex));
				if(max<val)
				{
					max=val;
				}
			}
			
			return max;
		}else 
		{
			float sum=0;
			int size = finalTable.tab.size();
			for(int i=0;i<size;i++)
			{
				int val = Integer.parseInt((String) finalTable.tab.get(i).get(colIndex));
				sum = sum + val;
			}
			
			return sum;
		}
		
		
	}
	
	

	
	
	public  void displayTable() {
		// TODO Auto-generated method stub
		
		finalTable.printTable();
	}

	
	

	public  void printError(String error)
	{
		System.out.println(error);
	}
}



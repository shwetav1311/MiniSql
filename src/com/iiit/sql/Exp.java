package com.iiit.sql;

public class Exp {
	
	int index;
	String Op;
	int op2;
	
	
	public Exp() {
		super();
	}


	public Exp(int index, String op, int op2) {
		super();
		this.index = index;
		Op = op;
		this.op2 = op2;
	}
	
	
	public boolean evaluateExp(int val)
	{
		//System.out.println(op2+" "+val);
		
		if(Op.equals("="))
		{
			if(val==op2)
			{
				return true;
			}
			
		}else if(Op.equals("<="))
		{
			if(val<=op2)
			{
				return true;
			}
		}else if(Op.equals(">="))
		{
			if(val>=op2)
			{
				return true;
			}
			
		}else if(Op.equals("<"))
		{
			if(val<op2)
			{
				return true;
			}
		}else if(Op.equals(">"))
		{
			if(val>op2)
			{
				return true;
			}
		}
		
		return false;
		
	}
	
}

import java.util.*;
import java.io.*;
// class interpreting
public class Interpreting
{
	// arraylist of tokens
	ArrayList<Token> tokens;
	// pos is used to interpret the program until token size
	int pos = 0;
	// arraylist of expression
	public ArrayList<Expression> expressions = new ArrayList<Expression>();
	// arraylist of statements
	public ArrayList<Statement> statements = new ArrayList<Statement>();
	// map for varibles 
	public HashMap<String, Value> variables = new HashMap<String, Value>();

	public HashMap<String, Integer> labels = new HashMap<String, Integer>();

	public HashMap<String, Function> functions = new HashMap<String, Function>();

	private ArrayList<Character> operators = new ArrayList<Character>(Arrays.asList('(', ')', '+', '-', '*', '%', '/', '='));

	private Stack<Integer> lastPosition = new Stack<Integer>();
	//constructor
	public Interpreting ( ArrayList<Token> tmp )
	{
		// passing all tokens into tokens 
		this.tokens = tmp;
		
		try
		{
			// calling the method startinterpreting 
			startInterpreting();
		}
		catch(Exception e)
		{
			System.err.println ( "Syntex Error : Please use correct syntex" );
			//e.printStackTrace();
		}
		// loop for pos
		while ( pos < tokens.size())
		{
			int current = pos;
			if ( current >= statements.size() )
				break;
				
			pos++;
			statements.get(current).execute();
		}
		
	}
	
	
	private void printVars()
	{
		System.out.println ( " *** VARS");
		Iterator it = variables.entrySet().iterator();
		while ( it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			System.out.println ( pairs.getKey() + " = " + pairs.getValue() );
			//it.remove();
		}
	}
	private void startInterpreting()
		throws Exception
	{
		// calls a loop until tokens size
		while(pos < tokens.size())
		{
		
			// calls a method match	
			if (match(TokenTag.print))
			{
				statements.add( new Print (op()) );
				
			}
			else if ( match(TokenTag.read))
			{
				consume(TokenTag.LPARA);
				statements.add ( new Input ( consume(TokenTag.VARIABLE).getValue()));
				consume(TokenTag.RPARA);
			}
			else if ( match(TokenTag.VARIABLE, TokenTag.ASSIGN))
			{
				String tmp = lookBack(2).getValue();
				Expression value = op();
				statements.add ( new Assignment(tmp, value) );
				
				 

			}
			else if ( match(TokenTag.VARIABLE, TokenTag.LPARA))
			{
				matchFunctionCall();
			}
			
			else
				break;
		}
			
	}
	// method matchFunctionCall
	private void matchFunctionCall ( ) throws Exception
	{
		FunctionCall function = new FunctionCall();

		if ( lookAhead(0).getTag() == TokenTag.LPARA)
			consume(TokenTag.LPARA);

		if ( functions.containsKey(lookBack(2).getValue()))
		{
			Function f = functions.get(lookBack(2).getValue());
			function = new FunctionCall (lookBack(2).getValue(),f.getPosition());
		}
		while(match(TokenTag.VARIABLE) || match(TokenTag.INT)) 
		{
			function.addVar ( lookBack(1) );
		}
		statements.add ( function );
		consume(TokenTag.RPARA);
	}
	// mathod match check for current token
	// if false pos++ and return true
	private boolean match (TokenTag tag )
	{
	
		if ( tokens.get(pos).getTag() != tag )
		return false;
		pos++;
		return true;
		
	}
	// method match double parameter 
	private boolean match ( TokenTag t1, TokenTag t2)
	{
		if ( lookAhead(0).getTag() != t1 ) 
			return false;
		if ( lookAhead(1).getTag() != t2 )
			return false;

		pos += 2;
		return true;
	}
	
	// method consume
	private Token consume ( TokenTag tag ) throws Exception
	{
		if ( tokens.get(pos).getTag() != tag ) throw new Exception();
		return tokens.get(pos++);
	}
	// method lookAhead hold a int value
	private Token lookAhead(int offset)
	{
		if ( (pos + offset) >= tokens.size() ){
			System.out.println ( "ERROR:" );
			return null;
		}

		return tokens.get(pos + offset);
	}
	// method lookBack hold a int value	
	private Token lookBack(int offset)
	{
		return tokens.get(pos - offset);
	}

	//method matchOp check for any left operator 	
	private boolean matchOp()
	{
		if ( pos < tokens.size() )
		{
			if ( match(TokenTag.PLUS) || match(TokenTag.MINUS) ||
			      match(TokenTag.MULTIPLY) || match(TokenTag.DIVIDE) ||
			      match(TokenTag.MOD) || match(TokenTag.ASSIGN))
			{
				return true;	
			}
		}
		return false;
	}
	// method op check for given expression
	private Expression op()
		throws Exception
	{
		Expression expression = getVar();
		while ( matchOp() )
		{
			char op = lookBack(1).getValue().charAt(0);
			Expression right = getVar();
			expression = new Operator(expression, op, right);
		}
		return expression;
	}
	// method getVar
	private Expression getVar()
		throws Exception
	{
		if ( match(TokenTag.VARIABLE) )
		{
			return new Variable(lookBack(1).getValue());
							
		}
		else if ( match(TokenTag.INT))
		{
			return new IntType(Integer.parseInt(lookBack(1).getValue()));
		}
		else if (match(TokenTag.LPARA))
		{
			Expression expression = op();
			consume(TokenTag.RPARA);
			return expression;
		}
		
		throw new Exception();
	}
	// interface Expression
	public interface Expression
	{
		Value evaluate();
	}
	// class variable
	public class Variable implements Expression
	{
		private String var;
		public Variable(String var)
		{
			this.var = var;
		}

		public Value evaluate()
		{
			if(variables.containsKey(var))
			{
				return variables.get(var);
			}
			System.out.println("ERROR:");
			System.out.println("Undefined Variable : please intialize your variable");
			return variables.get(var);
		}
		public String getVar()
		{
			return var;
		}
	}
	// class operator	
	public class Operator implements Expression
	{
		private Expression left;
		private Expression right;
		private char op;

		public Operator(Expression left, char op, Expression right)
		{
			this.left = left;
			this.right = right;
			this.op = op;
		}

		public Value evaluate()
		{
			
			Value lval = left.evaluate();
			Value rval = right.evaluate();

			switch(op)
			{
				case '=':
					if ( lval instanceof IntType)
						return new IntType((lval.toInt() == rval.toInt()) ? 1 : 0);
					else
						return new IntType(lval.toString().equals(rval.toString()) ? 1 : 0);
				case '/':
					return new IntType(lval.toInt() / rval.toInt());
				case '*':
					return new IntType(lval.toInt() * rval.toInt());
				case '-':
					return new IntType(lval.toInt() - rval.toInt());
				case '+':
					return new IntType(lval.toInt() + rval.toInt());
			}
			return null;
		}
	}
	
		public interface Statement
	{
		void execute();
	}
	// print print	
	// handels print call
	public class Print implements Statement
	{
		
		private Expression expression;
		
		public Print ( Expression e ) 
		{
		
			this.expression = e;
			execute();
		}
	
		public void execute()
		{
			System.out.println ( expression.evaluate().toString() );
			 

		}
	}
	// class input
	// handles read call
	class Input implements Statement
	{
		private String value;
		public Input ( String v )
		{
			this.value = v;
			execute();
		}

		public void execute ()
		{
			String input = "";

			try
			{
				BufferedReader reader = new BufferedReader (new InputStreamReader ( System.in ));
				input = reader.readLine();

				int tmp = Integer.parseInt(input);
				variables.put(value.toLowerCase(), new IntType(tmp));
			}
			catch(Exception e)
			{
				variables.put(value.toLowerCase(), new StringType(input));
			}
		}
	}			
	// class assignment
	class Assignment implements Statement
	{
		private String variable_name;
		private Expression expression;
		public Assignment ( String v, Expression value )
		{
			this.variable_name = v;
			this.expression = value;
			execute();
		}

		public void execute()
		{
			// evaluate the expression and assign it to the lhs
			variables.put(variable_name, expression.evaluate() );
		}
	}
	
	
	// class function call
	public class FunctionCall implements Statement
	{
		private String func;
		private int p;
		private Stack<Token> call_vars = new Stack<Token>();
		private ArrayList<Token> call_vars_stat = new ArrayList<Token>();

		public FunctionCall() {}

		public FunctionCall ( String f, int p )
		{
			this.func = f;
			this.p = p;
		}
		
		public void addVar ( Token t )
		{
			call_vars.push ( t );
			call_vars_stat.add ( t );
		}
		
		public void updatePosition ( int p )
		{
			this.p = p;
		}
		
		public String getFunc(){
			return func;
		}
		
		public void execute()
		{
			if ( call_vars.empty() )
			{
				for ( Token t : call_vars_stat )
					call_vars.push(t);
			}
			
			Function f = functions.get(func);
			if ( f.getVarCount() <= 0 )
			{
				ArrayList<Token> tmp = f.getCallVars();
				for ( Token t : tmp )
					f.addVar ( t );
			}

			while ( !call_vars.empty() && call_vars.peek() != null )
			{
				Token call_var = call_vars.pop(); 
				Token func_var = (functions.get(func)).getVar();
				if ( call_var.getTag() == TokenTag.INT )
				{
					variables.put(func_var.getValue().toLowerCase(), 
					new IntType(Integer.parseInt(call_var.getValue())));	
				}
				else if ( call_var.getTag() == TokenTag.VARIABLE )
				{
					IntType tmp = new IntType ( 
					variables.get(call_var.getValue().toLowerCase()).toInt() );
					variables.put(func_var.getValue().toLowerCase(), tmp);
				}
			}

			lastPosition.push(pos);
			pos = p;
		}
	}

	
	public interface Value extends Expression
	{
		String toString();
		int toInt();
	}

	class IntType implements Value
	{
		private int value;
		public IntType ( int v )
		{
			this.value = v;
		}

		public String toString()
		{
			return Integer.toString(value);
		}
		public int toInt()
		{
			return value;
		}
		public Value evaluate()
		{	
				
			return this;
		}
	}

	class StringType implements Value
	{
		private String value;
		public StringType ( String v )
		{
			this.value = v;
		}
		public String toString()
		{
			return value;
		}
		public int toInt()
		{
			return Integer.parseInt(value);
		}
		public Value evaluate()
		{
			
			return this;
		}
	}

	
} 
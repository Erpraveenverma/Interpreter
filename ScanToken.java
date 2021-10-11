import java.util.*;
import java.util.regex.*;
import java.io.*;
// this calss is used to scan the token that we possible used in our program 
public class ScanToken
{
	// aaraylist of tokens
	private ArrayList<Token> tokens = new ArrayList<Token>();
	// list of all possible operaters in our program
	private ArrayList<Character> operators = new ArrayList<Character>(Arrays.asList('(', ')', '+', '-', '*', '%', '/', '='));

	private String data; 

	public ScanToken ( String text ) throws IOException, Exception
	{
		// data caontain the data of text
		this.data = text;
		// calls the methos interpret
		interpret();
	}
	private void interpret()
	{ 	
		//colleting all the tokens
 		tokenize();
		// creating object of interpreting class 
		Interpreting inter = new Interpreting(tokens);
	}
	//adding the operater in token
	private void addOperator ( char c )
	{
		TokenTag tag = TokenTag.NONE;
		
		switch ( c )
		{
			case '(':
				tag = TokenTag.LPARA;
				break;
			case ')':
				tag = TokenTag.RPARA;
				break;
			case '+':
				tag = TokenTag.PLUS;
				break;
			case '-':
				tag = TokenTag.MINUS;
				break;
			case '*':
				tag = TokenTag.MULTIPLY;
				break;
			case '/':
				tag = TokenTag.DIVIDE;
				break;
			case '%':
				tag = TokenTag.MOD;
				break;
			case '=':
				tag = TokenTag.ASSIGN;
				break;
			default:
				return;
		}
		tokens.add(new Token(Character.toString(c), tag));
	}
	// reading the program letter by letter and collect all tokens
	private void tokenize()
	{
		TokenTag tag = TokenTag.NONE;

		String current = "";

		for ( int i = 0; i < data.length(); ++i )
		{
			char c = data.charAt(i);

			if ( c == '/') 
			{
				current += c;
				if ( (c = data.charAt(++i)) == '/' )
				{
					current = "";
					while ( ( c = data.charAt(i++)) != '\n' ) {}
					tag = TokenTag.NONE;
				}
				else{
					tag = TokenTag.OP;
					c = current.charAt(0);
				}
				--i; 			}
			
			
			else if ( operators.contains(c))
			{
				tag = TokenTag.OP;
			}
			else if ( tag == TokenTag.NONE && Character.isDigit(c))
			{
				tag = TokenTag.INT;
			}
			else if ( Character.isLetter(c) ||(tag == TokenTag.VARIABLE && Character.isDigit(c)))
			{
				current += c;
				tag = TokenTag.VARIABLE;
			}
			switch ( tag )
			{
				case OP:
					addOperator(c);
					current = "";
					tag = TokenTag.NONE;
					break;
				case INT:
					if ( Character.isDigit(c))
					{
						while ( Character.isDigit(c)) 
						{
							current += c;
							c = data.charAt(++i);
						}
						
						tokens.add(new Token (current, TokenTag.INT));
						current = "";
						tag = TokenTag.NONE;
						i--;
					}
					else
					{
						tokens.add ( new Token (current, TokenTag.INT));
						current = "";
						tag = TokenTag.NONE;
						i--; 					
					}
					break;
				// handles varible
				case VARIABLE:
					// if call for keyword print
					 if ( current.equalsIgnoreCase("print"))
					{
						//add 'Write' token
						tokens.add(new Token(current, TokenTag.print));
						current = "";
						tag = TokenTag.NONE;
					}
					// if we call for keyword read
					else if ( current.equalsIgnoreCase("read"))
					{
						tokens.add(new Token(current, TokenTag.read));
						current = "";
						tag = TokenTag.NONE;
					}
										else
					{
						char tmpc = data.charAt(++i);
						if ( Character.isLetter(tmpc) ||
							 Character.isDigit(tmpc)){
							i--;
							continue;
						}
					
						tokens.add(new Token(current, TokenTag.VARIABLE));
						current = "";
						tag = TokenTag.NONE;
						i--;
					}
					break;
					}
		}
	}
}

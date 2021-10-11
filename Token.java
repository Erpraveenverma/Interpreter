//class token 
// this calss is used in ScanToken class
public class Token
{
	private String value;
	private TokenTag tag;

	public Token ( String text, TokenTag tag ) 
	{
		this.tag = tag;
		this.value = text;
	}

	public TokenTag getTag()
	{
		//return the tag 
		return tag;
	}

	public void setTag(TokenTag t)
	{
		this.tag = t;
	}

	public String getValue()
	{
		// return the value
		return value;
	}
}

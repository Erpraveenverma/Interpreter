import java.io.*;
import java.util.*;
// class ESPInterpreter contain  main  mathod 
public class ESPInterpreter
{
	// main mathod
	public static void main ( String args[] ) throws FileNotFoundException,IOException, Exception
	{
		
		String text = readFile(args[0]);
		ScanToken scanner = new ScanToken(text);
	}
	// read the given program line by line and create a stringbuilder data
	private static String readFile ( String file )
		throws IOException
	{
		BufferedReader br;
		StringBuilder data = new StringBuilder();

		if ( file != null )
		{
			br = new BufferedReader(new FileReader(file));
			String line;
			while ( (line = br.readLine()) != null)
			data.append(line + "\n");
		}

		return data.toString();
	}
}

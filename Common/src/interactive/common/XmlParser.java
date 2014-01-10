package interactive.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

public class XmlParser
{
	private SaxParserHandler	saxHandler	= null;

	public XmlParser()
	{
		super();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	public void parse(File file, ConfigData configData)
	{
		Logs.showTrace("XML parser parse file: " + file.toString());

		try
		{
			InputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			saxHandler = new SaxParserHandler(configData);
			sp.parse(is, saxHandler);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}

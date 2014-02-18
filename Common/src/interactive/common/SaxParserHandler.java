package interactive.common;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("unused")
public class SaxParserHandler extends DefaultHandler
{
	private boolean		mbPackage			= false;
	private boolean		mbMetaData			= false;
	private boolean		mbEditor			= false;
	private boolean		mbVersion_id		= false;
	private boolean		mbApp_name			= false;
	private boolean		mbAuthor			= false;
	private boolean		mbPublisher			= false;
	private boolean		mbPlugin			= false;
	private boolean		mbPlugin_version	= false;
	private boolean		mbManifest			= false;
	private boolean		mbLandscape			= false;
	private boolean		mbPortrait			= false;
	private boolean		mbResources			= false;
	private boolean		mbFlow				= false;
	private boolean		mbChapter			= false;
	private boolean		mbPage				= false;
	private boolean		mbUncharted			= false;
	private boolean		mbItem				= false;
	private ConfigData	theConfigData		= null;
	private int			mnChapterKey		= Type.INVALID;

	public SaxParserHandler()
	{
		super();
	}

	public SaxParserHandler(ConfigData configData)
	{
		super();
		theConfigData = configData;
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (qName.equalsIgnoreCase("package"))
		{
			mbPackage = true;
		}

		if (qName.equalsIgnoreCase("metadata"))
		{
			mbMetaData = true;
		}

		if (qName.equalsIgnoreCase("editor"))
		{
			mbEditor = true;
		}

		if (qName.equalsIgnoreCase("version_id"))
		{
			mbVersion_id = true;
		}

		if (qName.equalsIgnoreCase("app_name"))
		{
			mbApp_name = true;
		}

		if (qName.equalsIgnoreCase("author"))
		{
			mbAuthor = true;
		}

		if (qName.equalsIgnoreCase("publisher"))
		{
			mbPublisher = true;
		}

		if (qName.equalsIgnoreCase("plugin"))
		{
			mbPlugin = true;
		}

		if (qName.equalsIgnoreCase("plugin_version"))
		{
			mbPlugin_version = true;
		}

		if (qName.equalsIgnoreCase("manifest"))
		{
			mbManifest = true;
		}

		if (qName.equalsIgnoreCase("landscape"))
		{
			mbLandscape = true;
		}

		if (qName.equalsIgnoreCase("portrait"))
		{
			mbPortrait = true;
		}

		if (qName.equalsIgnoreCase("resources"))
		{
			mbResources = true;
		}

		if (qName.equalsIgnoreCase("flow"))
		{
			mbFlow = true;
			getFlowAttr(attributes);
		}

		if (qName.equalsIgnoreCase("chapter"))
		{
			mbChapter = true;
			getChapterAttr(attributes);
		}

		if (qName.equalsIgnoreCase("page"))
		{
			mbPage = true;
			getPageAttr(attributes);
		}

		if (qName.equalsIgnoreCase("uncharted"))
		{
			mbUncharted = true;
		}

		if (qName.equalsIgnoreCase("item"))
		{
			mbItem = true;
			getItemAttr(attributes);
		}

		super.startElement(uri, localName, qName, attributes);
	}

	// get tag value
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		String strValue = new String(ch, start, length);

		if (mbEditor)
		{
			theConfigData.thePackage.metaData.strEditor = strValue;
		}

		if (mbVersion_id)
		{
			theConfigData.thePackage.metaData.strVersionId = strValue;
		}

		if (mbApp_name)
		{
			theConfigData.thePackage.metaData.strAppName = strValue;
		}

		if (mbAuthor)
		{
			theConfigData.thePackage.metaData.strAuthor = strValue;
		}

		if (mbPublisher)
		{
			theConfigData.thePackage.metaData.strPublisher = strValue;
		}

		if (mbPlugin)
		{
			theConfigData.thePackage.metaData.strPlugin = strValue;
		}

		if (mbPlugin_version)
		{
			theConfigData.thePackage.metaData.strPluginVersion = strValue;
		}
		super.characters(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equalsIgnoreCase("package"))
		{
			mbPackage = false;
		}

		if (qName.equalsIgnoreCase("metadata"))
		{
			mbMetaData = false;
		}

		if (qName.equalsIgnoreCase("editor"))
		{
			mbEditor = false;
		}

		if (qName.equalsIgnoreCase("version_id"))
		{
			mbVersion_id = false;
		}

		if (qName.equalsIgnoreCase("app_name"))
		{
			mbApp_name = false;
		}

		if (qName.equalsIgnoreCase("author"))
		{
			mbAuthor = false;
		}

		if (qName.equalsIgnoreCase("publisher"))
		{
			mbPublisher = false;
		}

		if (qName.equalsIgnoreCase("plugin"))
		{
			mbPlugin = false;
		}

		if (qName.equalsIgnoreCase("plugin_version"))
		{
			mbPlugin_version = false;
		}

		if (qName.equalsIgnoreCase("manifest"))
		{
			mbManifest = false;
		}

		if (qName.equalsIgnoreCase("landscape"))
		{
			mbLandscape = false;
		}

		if (qName.equalsIgnoreCase("portrait"))
		{
			mbPortrait = false;
		}

		if (qName.equalsIgnoreCase("resources"))
		{
			mbResources = false;
		}

		if (qName.equalsIgnoreCase("flow"))
		{
			mbFlow = false;
		}

		if (qName.equalsIgnoreCase("chapter"))
		{
			mbChapter = false;
		}

		if (qName.equalsIgnoreCase("page"))
		{
			mbPage = false;
		}

		if (qName.equalsIgnoreCase("uncharted"))
		{
			mbUncharted = false;
		}

		if (qName.equalsIgnoreCase("item"))
		{
			mbItem = false;
		}
		super.endElement(uri, localName, qName);
	}

	private void getItemAttr(Attributes atts)
	{
		String strHeight = null;
		if (mbManifest)
		{
			if (mbLandscape)
			{

				strHeight = atts.getValue("Height");
				if (null == strHeight)
				{
					strHeight = atts.getValue("Hieght");
				}

				theConfigData.thePackage.manifest.landscape.addItem(strHeight, atts.getValue("Width"),
						atts.getValue("href"), atts.getValue("id"), atts.getValue("media-type"), atts.getValue("name"),
						atts.getValue("snapshot_l"), atts.getValue("snapshot_t"));
			}

			if (mbPortrait)
			{
				strHeight = atts.getValue("Height");
				if (null == strHeight)
				{
					strHeight = atts.getValue("Hieght");
				}
				theConfigData.thePackage.manifest.portrait.addItem(strHeight, atts.getValue("Width"),
						atts.getValue("href"), atts.getValue("id"), atts.getValue("media-type"), atts.getValue("name"),
						atts.getValue("snapshot_l"), atts.getValue("snapshot_t"));
			}

			if (mbResources)
			{
				theConfigData.thePackage.manifest.resources.addItem(null, null, atts.getValue("href"),
						atts.getValue("id"), atts.getValue("media-type"), null, null, null);
			}
		}

		if (mbUncharted)
		{
			if (mbLandscape)
			{
				theConfigData.thePackage.uncharted.landscape.addItem(null, null, atts.getValue("href"),
						atts.getValue("id"), atts.getValue("media-type"), null, atts.getValue("snapshot_l"),
						atts.getValue("snapshot_t"));
			}
			if (mbPortrait)
			{
				theConfigData.thePackage.uncharted.portrait.addItem(null, null, atts.getValue("href"),
						atts.getValue("id"), atts.getValue("media-type"), null, atts.getValue("snapshot_l"),
						atts.getValue("snapshot_t"));
			}
		}
	}

	private void getFlowAttr(Attributes atts)
	{
		theConfigData.thePackage.flow.strBrowsing_mode = atts.getValue("browsing_mode");
		theConfigData.thePackage.flow.strDefault_orientation = atts.getValue("default_orientation");
	}

	private void getChapterAttr(Attributes atts)
	{
		mnChapterKey = theConfigData.thePackage.flow.chaptersSize();
		theConfigData.thePackage.flow.addChapter(atts.getValue("name"), atts.getValue("description"),
				atts.getValue("type"));
	}

	private void getPageAttr(Attributes atts)
	{
		theConfigData.thePackage.flow.chapters.get(mnChapterKey).addPage(atts.getValue("pref"), atts.getValue("lref"));
	}

}

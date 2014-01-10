package interactive.common;

import android.util.SparseArray;

public class ConfigData
{
	public Package	thePackage	= null;

	public class Package
	{
		public MetaData		metaData	= null;
		public Manifest		manifest	= null;
		public Flow			flow		= null;
		public Uncharted	uncharted	= null;

		public class MetaData
		{
			public String	strEditor			= null;
			public String	strVersionId		= null;
			public String	strAppName			= null;
			public String	strAuthor			= null;
			public String	strPublisher		= null;
			public String	strPlugin			= null;
			public String	strPluginVersion	= null;

			public MetaData()
			{

			}
		}

		public class Manifest
		{
			public Landscape	landscape	= null;
			public Portrait		portrait	= null;
			public Resources	resources	= null;

			public class Landscape
			{
				public SparseArray<Item>	items	= null;

				public Landscape()
				{
					items = new SparseArray<Item>();
				}

				@Override
				protected void finalize() throws Throwable
				{
					items.clear();
					items = null;
					super.finalize();
				}

				public int size()
				{
					return items.size();
				}

				public void addItem(String strHieght, String strWidth, String strHref, String strId,
						String strMediaType, String strName, String strSnapshotlarge, String strsnapshotTiny)
				{
					items.put(items.size(), new Item(strHieght, strWidth, strHref, strId, strMediaType, strName,
							strSnapshotlarge, strsnapshotTiny));
				}

				public Item getItemById(String strId)
				{
					for (int i = 0; i < size(); ++i)
					{
						if (items.get(i).mstrId.equalsIgnoreCase(strId))
						{
							return items.get(i);
						}
					}
					return null;
				}

			}

			public class Portrait
			{
				public SparseArray<Item>	items	= null;

				public Portrait()
				{
					items = new SparseArray<Item>();
				}

				@Override
				protected void finalize() throws Throwable
				{
					items.clear();
					items = null;
					super.finalize();
				}

				public int size()
				{
					return items.size();
				}

				public void addItem(String strHieght, String strWidth, String strHref, String strId,
						String strMediaType, String strName, String strSnapshotlarge, String strsnapshotTiny)
				{
					items.put(items.size(), new Item(strHieght, strWidth, strHref, strId, strMediaType, strName,
							strSnapshotlarge, strsnapshotTiny));
				}

				public Item getItemById(String strId)
				{
					for (int i = 0; i < size(); ++i)
					{
						if (items.get(i).mstrId.equalsIgnoreCase(strId))
						{
							return items.get(i);
						}
					}
					return null;
				}
			}

			public class Resources
			{
				public SparseArray<Item>	items	= null;

				public Resources()
				{
					items = new SparseArray<Item>();
				}

				@Override
				protected void finalize() throws Throwable
				{
					items.clear();
					items = null;
					super.finalize();
				}

				public int size()
				{
					return items.size();
				}

				public void addItem(String strHieght, String strWidth, String strHref, String strId,
						String strMediaType, String strName, String strSnapshotlarge, String strsnapshotTiny)
				{
					items.put(items.size(), new Item(strHieght, strWidth, strHref, strId, strMediaType, strName,
							strSnapshotlarge, strsnapshotTiny));
				}

			}

			public Manifest()
			{
				landscape = new Landscape();
				portrait = new Portrait();
				resources = new Resources();
			}

			@Override
			protected void finalize() throws Throwable
			{
				landscape = null;
				portrait = null;
				resources = null;
				super.finalize();
			}

		}

		public class Flow
		{
			public String				strBrowsing_mode		= null;
			public String				strDefault_orientation	= null;
			public SparseArray<Chapter>	chapters				= null;

			public class Chapter
			{
				public String				mstrName		= null;
				public String				mstrDescription	= null;
				public String				mstrType		= null;
				public SparseArray<Page>	pages			= null;

				public class Page
				{
					public String	mstrPref	= null;
					public String	mstrLref	= null;

					public Page(String strPref, String strLref)
					{
						mstrPref = strPref;
						mstrLref = strLref;
					}
				}

				public Chapter()
				{
				}

				public Chapter(String strName, String strDescription, String strType)
				{
					mstrName = strName;
					mstrDescription = strDescription;
					mstrType = strType;
					pages = new SparseArray<Page>();
				}

				@Override
				protected void finalize() throws Throwable
				{
					pages.clear();
					pages = null;
					super.finalize();
				}

				public int pageSize()
				{
					return pages.size();
				}

				public void addPage(String strPref, String strLref)
				{
					pages.put(pages.size(), new Page(strPref, strLref));
				}
			}

			public Flow()
			{
				chapters = new SparseArray<Chapter>();
			}

			@Override
			protected void finalize() throws Throwable
			{
				chapters.clear();
				chapters = null;
				super.finalize();
			}

			public int chaptersSize()
			{
				return chapters.size();
			}

			public void addChapter(String strName, String strDescription, String strType)
			{
				chapters.put(chapters.size(), new Chapter(strName, strDescription, strType));
			}
		}

		public class Uncharted
		{
			public Landscape	landscape	= null;
			public Portrait		portrait	= null;

			public class Landscape
			{
				public SparseArray<Item>	items	= null;

				public Landscape()
				{
					items = new SparseArray<Item>();
				}

				@Override
				protected void finalize() throws Throwable
				{
					items.clear();
					items = null;
					super.finalize();
				}

				public int size()
				{
					return items.size();
				}

				public void addItem(String strHieght, String strWidth, String strHref, String strId,
						String strMediaType, String strName, String strSnapshotlarge, String strsnapshotTiny)
				{
					items.put(items.size(), new Item(strHieght, strWidth, strHref, strId, strMediaType, strName,
							strSnapshotlarge, strsnapshotTiny));
				}

			}

			public class Portrait
			{
				public SparseArray<Item>	items	= null;

				public Portrait()
				{
					items = new SparseArray<Item>();
				}

				@Override
				protected void finalize() throws Throwable
				{
					items.clear();
					items = null;
					super.finalize();
				}

				public int size()
				{
					return items.size();
				}

				public void addItem(String strHieght, String strWidth, String strHref, String strId,
						String strMediaType, String strName, String strSnapshotlarge, String strsnapshotTiny)
				{
					items.put(items.size(), new Item(strHieght, strWidth, strHref, strId, strMediaType, strName,
							strSnapshotlarge, strsnapshotTiny));
				}
			}

			public Uncharted()
			{
				landscape = new Landscape();
				portrait = new Portrait();
			}

			@Override
			protected void finalize() throws Throwable
			{
				landscape = null;
				portrait = null;
				super.finalize();
			}

		}

		public Package()
		{
			metaData = new MetaData();
			manifest = new Manifest();
			flow = new Flow();
			uncharted = new Uncharted();

		}

		@Override
		protected void finalize() throws Throwable
		{
			metaData = null;
			manifest = null;
			flow = null;
			uncharted = null;
			super.finalize();
		}

	} // package

	public class Item
	{
		public String	mstrHeight			= null;
		public String	mstrWidth			= null;
		public String	mstrHref			= null;
		public String	mstrId				= null;
		public String	mstrMediaType		= null;
		public String	mstrName			= null;
		public String	mstrSnapshotlarge	= null;
		public String	mstrsnapshotTiny	= null;

		public Item()
		{

		}

		public Item(String strHeight, String strWidth, String strHref, String strId, String strMediaType,
				String strName, String strSnapshotlarge, String strsnapshotTiny)
		{
			mstrHeight = strHeight;
			mstrWidth = strWidth;
			mstrHref = strHref;
			mstrId = strId;
			mstrMediaType = strMediaType;
			mstrName = strName;
			mstrSnapshotlarge = strSnapshotlarge;
			mstrsnapshotTiny = strsnapshotTiny;
		}
	}

	public ConfigData()
	{
		super();
		thePackage = new Package();
	}

	@Override
	protected void finalize() throws Throwable
	{
		thePackage = null;
		super.finalize();
	}

}

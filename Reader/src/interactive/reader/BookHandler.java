package interactive.reader;

import interactive.common.ConfigData;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.FileHandler;
import interactive.common.Logs;
import interactive.common.ThreadHandler;
import interactive.common.XmlParser;

import java.io.File;

import android.app.Activity;
import android.os.Handler;
import android.util.SparseArray;

public class BookHandler
{

	private ThreadHandler	threadHandler	= null;
	private String			mstrBookPath	= null;
	private Handler			notifyHandler	= null;

	public BookHandler()
	{
		super();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	private String checkBook(Activity activity)
	{
		String strBookPath = null;
		FileHandler fileHandler = new FileHandler();
		SparseArray<String> saResInfo = new SparseArray<String>();
		String strSdPath = fileHandler.getSdcardPath();

		/**
		 * 讀取asset/resource
		 */
		if (0 >= fileHandler.getBookInfo(activity, saResInfo) || null == strSdPath)
		{
			return null;
		}
		String strBookName = saResInfo.get(0) + "." + saResInfo.get(1);

		if (checkAssets(activity, fileHandler, strSdPath, saResInfo.get(0), saResInfo.get(1))
				|| checkObb(activity, fileHandler, strSdPath, strBookName)
				|| checkExpressBook(fileHandler, strSdPath, strBookName))
		{
			/** delete all file that exist as the same book name */
			fileHandler.delete(strSdPath + "download" + File.separator + saResInfo.get(0));

			/** start unexpress file */
			EventHandler.notify(notifyHandler, EventMessage.MSG_START_UNEXPRESS, 0, 0, null);
			if (fileHandler.UnExpressBook(strSdPath + "download" + File.separator, saResInfo.get(0), saResInfo.get(1)))
			{
				fileHandler.delete(strSdPath + "download" + File.separator + saResInfo.get(0) + "." + saResInfo.get(1));
			}
		}

		/** check book exist */
		strBookPath = fileHandler.getExistBookPath(saResInfo.get(0));

		saResInfo.clear();
		saResInfo = null;
		fileHandler = null;

		mstrBookPath = strBookPath;
		return strBookPath;
	}

	private boolean checkAssets(Activity activity, FileHandler fileHandler, String strSdPath, String strFileName,
			String strFormat)
	{
		if (null == activity || null == fileHandler || null == strSdPath || null == strFileName || null == strFormat)
		{
			return false;
		}

		String strFile = strFileName + "." + strFormat;

		if (fileHandler.checkAssetFile(activity, strFile))
		{
			try
			{
				fileHandler.copyFileFromAssets(activity, strFile, strSdPath + "download" + File.separator + strFile);
				if (checkExpressBook(fileHandler, strSdPath, strFile))
				{
					Logs.showTrace("Copy assets book to book path success");
					return true;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean checkObb(Activity activity, FileHandler fileHandler, String strSdPath, String strFile)
	{
		if (null == activity || null == fileHandler || null == strSdPath || null == strFile)
		{
			return false;
		}

		String strObbPath = strSdPath + "Android/obb/";

		if (fileHandler.checkPath(strObbPath, true))
		{
			Logs.showTrace("Obb path = " + strObbPath);
			String strPackName = activity.getApplicationContext().getPackageName();
			String strObbFile = strPackName + ".obb";
			String strObbBook = fileHandler.getObbFile(strObbPath, strObbFile);
			if (null != strObbBook)
			{
				if (fileHandler.fileRename(strObbPath, strObbBook, strSdPath + "download", strFile))
				{
					if (checkExpressBook(fileHandler, strSdPath, strFile))
					{
						Logs.showTrace("Copy obb book to book path success");
						return true;
					}
				}
			}
			else
			{
				strObbPath += strPackName;
				strObbPath += File.separator;
				strObbBook = fileHandler.getObbFile(strObbPath, strObbFile);
				if (null != strObbBook)
				{
					if (fileHandler.fileRename(strObbPath, strObbBook, strSdPath + "download", strFile))
					{
						fileHandler.delete(strObbPath);
						if (checkExpressBook(fileHandler, strSdPath, strFile))
						{
							Logs.showTrace("Copy obb book to book path success");
							return true;
						}
					}
					else
					{
						Logs.showTrace("Copy obb book to book path Fail!!");
					}
				}
			}
		}

		fileHandler = null;
		return false;
	}

	private boolean checkExpressBook(FileHandler fileHandler, String strSdPath, String strFile)
	{
		if (null == fileHandler || null == strSdPath || null == strFile)
		{
			return false;
		}

		String strZipFilePath = fileHandler.searchFilePath(strSdPath + "download" + File.separator, strFile);
		if (null != strZipFilePath)
		{
			Logs.showTrace("zip file in " + strZipFilePath);
			return true;
		}

		fileHandler = null;
		return false;
	}

	public String getBookPath()
	{
		return mstrBookPath;
	}

	/** thread mode book check */
	public void startBookCheck(final Activity activity)
	{
		if (null != threadHandler)
		{
			threadHandler = null;
		}

		threadHandler = new ThreadHandler(new Runnable()
		{
			public void run()
			{
				try
				{
					mstrBookPath = checkBook(activity);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				finally
				{
					Logs.showTrace("Book check finish");
					EventHandler.notify(notifyHandler, EventMessage.MSG_CHECKED_BOOK, 0, 0, null);
				}
			}
		});
		threadHandler.start();
	}

	public void setNotifyHandler(Handler handler)
	{
		notifyHandler = handler;
	}

	public boolean parseBook(final String strPath, ConfigData configData)
	{
		String strParseFile = strPath + File.separator + "config.xml";
		String strDefinitionFile = strPath + File.separator + "definition.xml";

		File file = new File(strDefinitionFile);

		if (file.exists())
		{
			strParseFile = strDefinitionFile;
		}
		else
		{
			file = new File(strParseFile);
			if (!file.exists())
			{
				Logs.showTrace("No any config file");
				return false;
			}
		}

		XmlParser parser = new XmlParser();
		file = new File(strParseFile);
		parser.parse(file, configData);
		parser = null;
		return true;
	}
}

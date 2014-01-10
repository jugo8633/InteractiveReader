package interactive.reader;

import interactive.common.ConfigData;
import interactive.common.Device;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.SqliteHandler;
import interactive.common.Type;
import interactive.common.SqliteHandler.FavoriteData;
import interactive.view.data.PageData;
import interactive.view.global.Global;
import interactive.view.pagereader.DisplayPage;
import interactive.view.pagereader.PageReader;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReaderActivity extends Activity
{

	private PageReader					pageReader			= null;
	private RelativeLayout				rlLayoutHeader		= null;
	private ProgressDialog				progressDialog		= null;
	private BookHandler					bookHandler			= null;
	private SparseArray<FavoriteData>	listFavoriteData	= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/** init global*/
		Global.theActivity = this;
		Global.interactiveHandler.initMediaView(this);

		/** load reader layout */
		int nResId = getResourceId("reader", "layout");
		this.setContentView(nResId);

		/** initialize header bar */
		initHeader();

		/** initialize page reader from reader layout*/
		initPageReader();

		/** get book information and path */
		nResId = getResourceId("loading_book", "string");
		String strTmp = getString(nResId);
		showProgreeDialog(strTmp);
		bookHandler = new BookHandler();
		bookHandler.setNotifyHandler(activityHandler);
		bookHandler.startBookCheck(this);

		Logs.showTrace("Reader Activity Create");
	}

	@Override
	protected void onResume()
	{
		Logs.showTrace("Reader activity resume");
		Global.interactiveHandler.initMediaView(this);
		super.onResume();
	}

	public int getResourceId(String name, String defType)
	{
		return getResources().getIdentifier(name, defType, getPackageName());
	}

	private void initPageReader()
	{
		int nResId = getResourceId("readerPageReader", "id");
		pageReader = (PageReader) findViewById(nResId);
	}

	private void initHeader()
	{
		int nResId = getResourceId("readerHeaderMain", "id");
		rlLayoutHeader = (RelativeLayout) findViewById(nResId);
	}

	private void showProgreeDialog(final String strMsg)
	{
		closeProgressDialog();
		progressDialog = ProgressDialog.show(this, "", strMsg, true, false);
		Logs.showTrace("show progress dialog :" + strMsg);
	}

	private void closeProgressDialog()
	{
		if (null != progressDialog)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private void initBook(String strBookPath)
	{
		if (null == strBookPath)
		{
			// TODO show notify dialog
			closeProgressDialog();
			return;
		}

		Logs.showTrace("Start init book: " + strBookPath);
		ConfigData configData = new ConfigData();
		if (bookHandler.parseBook(strBookPath, configData))
		{
			setHeaderBookName(configData.thePackage.metaData.strAppName);
			getFavoriteData(this);
			loadDisplayPage(configData, strBookPath);
		}
		else
		{
			// TODO show parse fail dialog 
		}
		closeProgressDialog();
	}

	private void getFavoriteData(Context context)
	{
		SqliteHandler sqlteHandler = new SqliteHandler(context);
		listFavoriteData = new SparseArray<FavoriteData>();
		sqlteHandler.getFavoriteData(listFavoriteData);
		sqlteHandler.close();
		sqlteHandler = null;
	}

	private void setHeaderBookName(String strBookName)
	{
		TextView txBookName = (TextView) findViewById(getResourceId("textViewBookName", "id"));
		txBookName.setText(strBookName);
	}

	private void loadDisplayPage(ConfigData configData, String strBookPath)
	{
		PageData.listPageData.clear();
		SparseArray<SparseArray<DisplayPage>> book = new SparseArray<SparseArray<DisplayPage>>();
		int nBookOrientation = createDisplayPage(book, configData, strBookPath);
		pageReader.createBook(book);
		book.clear();
		book = null;
		// TODO		initOption();
		if (Configuration.ORIENTATION_UNDEFINED == nBookOrientation)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

	private int createDisplayPage(SparseArray<SparseArray<DisplayPage>> book, ConfigData configData, String strBookPath)
	{
		if (null == book || null == configData || null == strBookPath)
		{
			Logs.showTrace("Reader create display page fail: invalid params");
			return Type.INVALID;
		}
		int nBookOrientation = Configuration.ORIENTATION_UNDEFINED;
		if (configData.thePackage.flow.strBrowsing_mode.equalsIgnoreCase("vertical"))
		{
			nBookOrientation = Configuration.ORIENTATION_PORTRAIT;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if (configData.thePackage.flow.strBrowsing_mode.equalsIgnoreCase("horizontal"))
		{
			nBookOrientation = Configuration.ORIENTATION_LANDSCAPE;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		if (configData.thePackage.flow.strBrowsing_mode.equalsIgnoreCase("vertical/horizontal"))
		{
			nBookOrientation = Configuration.ORIENTATION_UNDEFINED;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

			// we lock first, when wen load page
			Device device = new Device(this);
			int nOrientation = device.getOrientation();
			device = null;
			if (Configuration.ORIENTATION_LANDSCAPE == nOrientation)
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			else
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
		SparseArray<PageData.Data> listPageData = null;
		PageData pageData = null;
		for (int nChapter = 0; nChapter < configData.thePackage.flow.chaptersSize(); ++nChapter)
		{
			SparseArray<DisplayPage> pages = new SparseArray<DisplayPage>();
			int nPages = configData.thePackage.flow.chapters.get(nChapter).pageSize();
			listPageData = new SparseArray<PageData.Data>();
			for (int nPage = 0; nPage < nPages; ++nPage)
			{
				DisplayPage dpage = new DisplayPage(this);
				dpage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				dpage.setBookPath(strBookPath);
				pageData = new PageData();
				PageData.Data data = pageData.createData();
				if (getPageData(nChapter, nPage, configData, data, strBookPath))
				{
					for (int nFavorite = 0; nFavorite < listFavoriteData.size(); ++nFavorite)
					{
						FavoriteData favoriteData = listFavoriteData.get(nFavorite);
						if (favoriteData.mnChapter == nChapter && favoriteData.mnPage == nPage)
						{
							data.bIsFavorite = true;
						}
					}
					dpage.setPageData(pageReader.getHandler(), data);
					listPageData.put(nPage, data);
				}
				else
				{
					// TODO show open book fail dialog
					//AppCrossApplication.showToast("開啟書本失敗");
					return Type.INVALID;
				}
				data = null;
				pageData = null;
				pages.put(pages.size(), dpage);
			}
			PageData.listPageData.put(nChapter, listPageData);
			listPageData = null;
			book.put(book.size(), pages);
			pages = null;
		}
		return nBookOrientation;
	}

	private boolean getPageData(int nChapter, int nPage, ConfigData configData, PageData.Data pageData,
			String strBookPath)
	{
		String strPref = null;
		ConfigData.Item item = null;

		Device device = new Device(this);
		switch (device.getOrientation())
		{
		case Configuration.ORIENTATION_PORTRAIT:
			strPref = configData.thePackage.flow.chapters.get(nChapter).pages.get(nPage).mstrPref;
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			strPref = configData.thePackage.flow.chapters.get(nChapter).pages.get(nPage).mstrLref;
			break;
		}

		if (null == strPref)
		{
			Logs.showTrace("Error: get Book Reference Fail!!");
			return false;
		}
		if (Configuration.ORIENTATION_PORTRAIT == device.getOrientation())
		{
			item = configData.thePackage.manifest.portrait.getItemById(strPref);
		}
		if (Configuration.ORIENTATION_LANDSCAPE == device.getOrientation())
		{
			item = configData.thePackage.manifest.landscape.getItemById(strPref);
		}

		device = null;

		if (null != item)
		{
			pageData.nWidth = Integer.parseInt(item.mstrWidth);
			pageData.nHeight = Integer.parseInt(item.mstrHeight);
			pageData.strPath = strBookPath + item.mstrHref;
			pageData.nChapter = nChapter;
			pageData.nPage = nPage;
			pageData.strName = item.mstrHref;
			pageData.strShapTiny = strBookPath + item.mstrsnapshotTiny;
			pageData.strShapLarge = strBookPath + item.mstrSnapshotlarge;
			pageData.strChapterName = configData.thePackage.flow.chapters.get(nChapter).mstrName;
			pageData.strDescript = configData.thePackage.flow.chapters.get(nChapter).mstrDescription;
			return true;
		}

		Logs.showTrace("Error: get Book Item Fail!!");

		return false;
	}

	private Handler	activityHandler	= new Handler()
									{
										@Override
										public void handleMessage(Message msg)
										{
											switch (msg.what)
											{
											case EventMessage.MSG_START_UNEXPRESS:
												int nResId = getResourceId("start_unexpress", "string");
												String strTmp = getString(nResId);
												showProgreeDialog(strTmp);
												break;
											case EventMessage.MSG_CHECKED_BOOK:
												String strBookPath = bookHandler.getBookPath();
												initBook(strBookPath);
												break;
											}
											super.handleMessage(msg);
										}
									};
}

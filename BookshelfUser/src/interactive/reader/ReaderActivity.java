package interactive.reader;

import interactive.bookshelfuser.BookshelfUserActivity;
import interactive.bookshelfuser.FootbarHandler;
import interactive.common.ClearCache;
import interactive.common.ConfigData;
import interactive.common.Device;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.IntentHandler;
import interactive.common.Logs;
import interactive.common.SqliteHandler;
import interactive.common.Type;
import interactive.common.SqliteHandler.FavoriteData;
import interactive.common.VersionHandler;
import interactive.common.WordHandler;
import interactive.view.data.PageData;
import interactive.view.gallery.GalleryView;
import interactive.view.global.Global;
import interactive.view.pagereader.DisplayPage;
import interactive.view.pagereader.PageReader;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReaderActivity extends Activity
{
	public static final String			BOOK_PATH				= "book_path";
	private PageReader					pageReader				= null;
	private RelativeLayout				rlLayoutHeader			= null;
	private RelativeLayout				rlLayoutFootbar			= null;
	private ProgressDialog				progressDialog			= null;
	private BookHandler					bookHandler				= null;
	private SparseArray<FavoriteData>	listFavoriteData		= null;
	private boolean						mbIsShowOption			= false;
	private OptionHandler				optionHandler			= null;
	private int							mnOrientation			= Type.INVALID;
	private ConfigData					configData				= null;
	private String						mstrBookPath			= null;
	private boolean						mbIsActive				= true;
	private FootbarHandler				footbar					= null;
	private int							mnCurrentFootbarItem	= 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/** set cache clean */
		ClearCache.setCacheClean(false);

		/** init global */
		Global.theActivity = this;
		Global.handlerActivity = selfHandler;

		/** load reader layout */
		int nResId = Global.getResourceId(this, "reader", "layout");
		this.setContentView(nResId);

		/** get book information and path */
		bookHandler = new BookHandler();
		bookHandler.setNotifyHandler(selfHandler);

		initLayout();
		Intent intent = getIntent();
		mstrBookPath = intent.getStringExtra(BOOK_PATH);
		if (null == mstrBookPath)
		{
			finish();
		}
		loadBook();
		Logs.showTrace("Reader Activity Create");
	}

	private void initLayout()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// do loading data or whatever hard here

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						/** initialize header bar */
						initHeader();

						/** initialize page reader from reader layout */
						initPageReader();

						/** init option handler */
						optionHandler = new OptionHandler(ReaderActivity.this);

						/** init footbar handler */
						initFootbar();
					}
				});
			}
		}).start();
	}

	private void loadBook()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// do loading data or whatever hard here

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						initBook(mstrBookPath);
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (KeyEvent.KEYCODE_BACK == keyCode)
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume()
	{
		mbIsActive = true;
		Logs.showTrace("Reader activity resume");
		Global.theActivity = this;
		Global.interactiveHandler.initMediaView(this);
		if (null != optionHandler)
		{
			optionHandler.clearHeaderSelected(pageReader.getCurrentChapter(), pageReader.getCurrentPage());
		}
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		mbIsActive = false;
		Logs.showTrace("Main Activity pause");
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		if (newConfig.orientation != mnOrientation && mbIsActive)
		{
			mnOrientation = newConfig.orientation;
			loadDisplayPage(configData, mstrBookPath);
			pageReader.jumpPage(pageReader.getCurrentChapter(), pageReader.getCurrentPage(), true);
			Logs.showTrace("Orientation change: " + newConfig.orientation);
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy()
	{
		Global.interactiveHandler.releaseAllAudio();
		Logs.showTrace("Redaer activity destory");
		super.onDestroy();
	}

	@Override
	public void onLowMemory()
	{
		ClearCache clearCache = new ClearCache();
		clearCache.trimCache(this);
		clearCache = null;
		System.gc();
		super.onLowMemory();
	}

	private void initPageReader()
	{
		int nResId = Global.getResourceId(this, "readerPageReader", "id");
		pageReader = (PageReader) findViewById(nResId);
		if (null != pageReader)
		{
			Global.pageReader = pageReader;
			pageReader.setOnPageSwitchedListener(new PageReader.OnPageSwitchedListener()
			{
				@Override
				public void onPageSwitched()
				{
					if (mbIsShowOption)
					{
						hideOption();
					}
				}
			});
		}
	}

	private void initHeader()
	{
		int nResId = Global.getResourceId(this, "readerHeaderMain", "id");
		rlLayoutHeader = (RelativeLayout) findViewById(nResId);
		if (null != rlLayoutHeader)
		{
			rlLayoutHeader.setVisibility(View.GONE);
			mbIsShowOption = false;
		}
	}

	private void initFootbar()
	{
		footbar = new FootbarHandler(this);
		footbar.setDefaultSelected(Global.getResourceId(this, "readerBtn", "id"));
		footbar.setOnItemSelectedListener(new FootbarHandler.OnItemSelectedListener()
		{
			@Override
			public void OnItemSelected(int nIndexSelected)
			{
				switchMode(nIndexSelected);
			}
		});

		int nResId = Global.getResourceId(this, "FootbarMain", "id");
		rlLayoutFootbar = (RelativeLayout) findViewById(nResId);
		if (null != rlLayoutFootbar)
		{
			rlLayoutFootbar.setVisibility(View.GONE);
		}
	}

	private void showOption()
	{
		mbIsShowOption = mbIsShowOption ? false : true;
		if (mbIsShowOption)
		{
			rlLayoutHeader.setVisibility(View.VISIBLE);
			rlLayoutHeader.bringToFront();
			optionHandler.updateFavoriteHeaderIcon(pageReader.getCurrentChapter(), pageReader.getCurrentPage());
			rlLayoutFootbar.setVisibility(View.VISIBLE);
		}
		else
		{
			hideOption();
		}
	}

	private void hideOption()
	{
		mbIsShowOption = false;
		rlLayoutHeader.setVisibility(View.GONE);
		rlLayoutFootbar.setVisibility(View.GONE);
		optionHandler.clearHeaderSelected(pageReader.getCurrentChapter(), pageReader.getCurrentPage());
		optionHandler.closeFlipView();
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
			closeProgressDialog();
			Global.showToast("Book Path Invalid");
			finish();
			return;
		}
		mstrBookPath = strBookPath;
		Logs.showTrace("Start init book: " + mstrBookPath);
		configData = new ConfigData();
		if (bookHandler.parseBook(mstrBookPath, configData))
		{
			setHeaderBookName(configData.thePackage.metaData.strAppName);
			getFavoriteData(this);
			if (checkOrientation(configData))
			{
				loadDisplayPage(configData, mstrBookPath);
			}
			else
			{
				Logs.showTrace("Orientation check Fail!!");
			}
		}
		else
		{
			Global.showToast("�貊�閫��憭望�");
		}
		closeProgressDialog();
	}

	/**
	 * 憒�checkOrientation return false, �configure change�蜷oad display
	 * 
	 * @param configData
	 * @return
	 */
	private boolean checkOrientation(ConfigData configData)
	{
		Device device = new Device(this);
		int nOrientation = device.getOrientation();
		device = null;

		String strDefaultOrientation = configData.thePackage.flow.strDefault_orientation;
		if (strDefaultOrientation.contains("portrait") && strDefaultOrientation.contains("landscape"))
		{
			return true;
		}
		else if (strDefaultOrientation.contains("portrait"))
		{
			if (Configuration.ORIENTATION_PORTRAIT == nOrientation)
			{
				return true;
			}
			else
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
		else if (strDefaultOrientation.contains("landscape"))
		{
			if (Configuration.ORIENTATION_LANDSCAPE == nOrientation)
			{
				return true;
			}
			else
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}

		return false;
	}

	private void initOption()
	{
		optionHandler.initCategory(this);
		optionHandler.initChapOption(this);
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
		TextView txBookName = (TextView) findViewById(Global.getResourceId(this, "textViewBookName", "id"));
		txBookName.setText(strBookName);
		txBookName.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View arg0)
			{
				String strVersionValue = null;
				String strVersionName = null;
				WordHandler word = new WordHandler();
				strVersionName = word.getString(ReaderActivity.this, "reader_version");
				word = null;
				VersionHandler version = new VersionHandler();
				strVersionValue = version.getVersionName(ReaderActivity.this);

				String strVersion = strVersionName + " : V" + strVersionValue;
				Logs.showTrace("==== Interactive Reader Version: " + strVersion + " ====");
				Toast.makeText(ReaderActivity.this, strVersion, Toast.LENGTH_LONG).show();
				strVersionValue = null;
				strVersionName = null;
				return true;
			}
		});
	}

	private void loadDisplayPage(ConfigData configData, String strBookPath)
	{
		if (null == configData || null == strBookPath)
		{
			return;
		}
		Logs.showTrace("Start load display page");
		PageData.listPageData.clear();
		SparseArray<SparseArray<DisplayPage>> book = new SparseArray<SparseArray<DisplayPage>>();
		int nBookOrientation = createDisplayPage(book, configData, strBookPath);
		pageReader.createBook(book);
		book.clear();
		book = null;
		if (Configuration.ORIENTATION_UNDEFINED == nBookOrientation)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		initOption();
		pageReader.setCurrentPosition();
	}

	private int createDisplayPage(SparseArray<SparseArray<DisplayPage>> book, ConfigData configData, String strBookPath)
	{
		if (null == book || null == configData || null == strBookPath)
		{
			Logs.showTrace("Reader create display page fail: invalid params");
			return Type.INVALID;
		}

		int nBookOrientation = Configuration.ORIENTATION_UNDEFINED;

		String strDefaultOrientation = configData.thePackage.flow.strDefault_orientation;
		if (strDefaultOrientation.contains("portrait") && strDefaultOrientation.contains("landscape"))
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
		else if (strDefaultOrientation.contains("portrait"))
		{
			nBookOrientation = Configuration.ORIENTATION_PORTRAIT;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else if (strDefaultOrientation.contains("landscape"))
		{
			nBookOrientation = Configuration.ORIENTATION_LANDSCAPE;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
					dpage.setPageData(data);
					listPageData.put(nPage, data);
				}
				else
				{
					Global.showToast("���豢憭望�");
					return Type.INVALID;
				}
				data = null;
				pageData = null;
				pages.put(pages.size(), dpage);
				dpage = null;
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
		switch(device.getOrientation())
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

	private void getDeviceInfo(Activity activity)
	{
		Device device = new Device(activity);
		Logs.showTrace("==== Device Information ====");
		Logs.showTrace(
				"Display width = " + device.getDisplayWidth() + " Display height = " + device.getDisplayHeight());
		Logs.showTrace("Device width = " + device.getDeviceWidth() + " Device height = " + device.getDeviceHeight());
		Logs.showTrace("Display scale size = " + device.getScaleSize());
		Logs.showTrace("Device orientation = " + device.getOrientation());
		Logs.showTrace("Device inches = " + device.getDisplayIncheSize());
		Logs.showTrace("Device type = " + device.getDeviceType()); // 0:phone
																	// 1:tablet
		device = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Logs.showTrace("onActivityResult : requestCode=" + requestCode + " resultCode=" + resultCode + " data=" + data);
		super.onActivityResult(requestCode, resultCode, data);
		IntentHandler intentHandler = new IntentHandler();
		Bitmap bitmap = intentHandler.activityResult(this, requestCode, resultCode, data);
		if (null != bitmap)
		{
			EventHandler.notify(Global.handlerPostcard, EventMessage.MSG_ACTIVITY_RESULT, 0, 0, bitmap);
		}
	}

	private void switchMode(int nIndex)
	{
		if (mnCurrentFootbarItem == nIndex)
		{
			return;
		}

		mnCurrentFootbarItem = nIndex;
		if (2 != mnCurrentFootbarItem)
		{
			Bundle bundle = new Bundle();
			bundle.putInt("FOOT_SELECT", nIndex);
			setResult(RESULT_OK, (new Intent()).putExtras(bundle));
			finish();
		}

	}

	private Handler selfHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case EventMessage.MSG_START_UNEXPRESS:
				int nResId = Global.getResourceId(ReaderActivity.this, "start_unexpress", "string");
				String strTmp = getString(nResId);
				showProgreeDialog(strTmp);
				break;
			case EventMessage.MSG_CHECKED_BOOK:
				initBook(bookHandler.getBookPath());
				break;
			case EventMessage.MSG_DOUBLE_CLICK:
				showOption();
				Logs.showTrace("Activity receive double click");
				break;
			case EventMessage.MSG_FLIPPER_CLOSE:
				optionHandler.clearHeaderSelected(pageReader.getCurrentChapter(), pageReader.getCurrentPage());
				break;
			case EventMessage.MSG_GO_FORWARD:
				pageReader.goForward();
				break;
			case EventMessage.MSG_OPTION_ITEM_SELECTED:
				showOption();
				pageReader.jumpPage(msg.arg1, msg.arg2, true);
				break;
			case GalleryView.MSG_WND_CLICK:
				optionHandler.closeFlipView();
				break;
			case GalleryView.MSG_IMAGE_CLICK:
				showOption();
				pageReader.jumpPage(msg.arg1, msg.arg2, true);
				break;
			case EventMessage.MSG_LOCK_PAGE:
				pageReader.lockScroll(true);
				break;
			case EventMessage.MSG_UNLOCK_PAGE:
				pageReader.lockScroll(false);
				break;
			case EventMessage.MSG_LOCK_HORIZON:
				pageReader.lockHorizonScroll(true);
				break;
			case EventMessage.MSG_UNLOCK_HORIZON:
				pageReader.lockHorizonScroll(false);
				break;
			case EventMessage.MSG_LOCK_VERTICAL:
				pageReader.lockVerticalScroll(true);
				break;
			case EventMessage.MSG_UNLOCK_VERTICAL:
				pageReader.lockVerticalScroll(false);
				break;
			case EventMessage.MSG_JUMP:
				pageReader.jumpPage(msg.arg1, msg.arg2, false);
				break;
			case EventMessage.MSG_JUMP_FADE:
				pageReader.jumpPage(msg.arg1, msg.arg2, true);
				break;
			}
			super.handleMessage(msg);
		}
	};
}

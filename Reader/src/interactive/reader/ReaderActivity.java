package interactive.reader;

import interactive.common.ConfigData;
import interactive.common.Device;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.SqliteHandler;
import interactive.common.Type;
import interactive.common.SqliteHandler.FavoriteData;
import interactive.view.data.PageData;
import interactive.view.gallery.GalleryView;
import interactive.view.global.Global;
import interactive.view.pagereader.DisplayPage;
import interactive.view.pagereader.PageReader;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
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
	private boolean						mbIsShowOption		= false;
	private OptionHandler				optionHandler		= null;
	private int							mnOrientation		= Type.INVALID;
	private ConfigData					configData			= null;
	private String						mstrBookPath		= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/** show device information */
		getDeviceInfo(this);

		/** init global*/
		Global.theActivity = this;
		Global.handlerActivity = activityHandler;

		/** load reader layout */
		int nResId = getResourceId("reader", "layout");
		this.setContentView(nResId);

		/** initialize header bar */
		initHeader();

		/** initialize page reader from reader layout*/
		initPageReader();

		/** init option handler */
		optionHandler = new OptionHandler(this);

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
		optionHandler.clearHeaderSelected(pageReader.getCurrentChapter(), pageReader.getCurrentPage());
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		if (newConfig.orientation != mnOrientation)
		{
			mnOrientation = newConfig.orientation;
			loadDisplayPage(configData, mstrBookPath);
			pageReader.jumpPage(pageReader.getCurrentChapter(), pageReader.getCurrentPage());
			Logs.showTrace("Orientation change: " + newConfig.orientation);
		}
		super.onConfigurationChanged(newConfig);
	}

	public int getResourceId(String name, String defType)
	{
		return getResources().getIdentifier(name, defType, getPackageName());
	}

	private void initPageReader()
	{
		int nResId = getResourceId("readerPageReader", "id");
		pageReader = (PageReader) findViewById(nResId);
		if (null != pageReader)
		{
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
		int nResId = getResourceId("readerHeaderMain", "id");
		rlLayoutHeader = (RelativeLayout) findViewById(nResId);
		if (null != rlLayoutHeader)
		{
			rlLayoutHeader.setVisibility(View.GONE);
			mbIsShowOption = false;
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
			// TODO show notify dialog
			closeProgressDialog();
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
		}
		else
		{
			// TODO show parse fail dialog 
		}
		closeProgressDialog();
	}

	private boolean checkOrientation(ConfigData configData)
	{
		Device device = new Device(this);
		int nOrientation = device.getOrientation();
		device = null;

		if (configData.thePackage.flow.strBrowsing_mode.equalsIgnoreCase("vertical"))
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

		if (configData.thePackage.flow.strBrowsing_mode.equalsIgnoreCase("horizontal"))
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

		if (configData.thePackage.flow.strBrowsing_mode.equalsIgnoreCase("vertical/horizontal"))
		{
			return true;
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
		TextView txBookName = (TextView) findViewById(getResourceId("textViewBookName", "id"));
		txBookName.setText(strBookName);
	}

	private void loadDisplayPage(ConfigData configData, String strBookPath)
	{
		if (null == configData || null == strBookPath)
		{
			return;
		}
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

	private void getDeviceInfo(Activity activity)
	{
		Device device = new Device(activity);
		Logs.showTrace("==== Device Information ====");
		Logs.showTrace("Display width = " + device.getDisplayWidth() + " Display height = " + device.getDisplayHeight());
		Logs.showTrace("Device width = " + device.getDeviceWidth() + " Device height = " + device.getDeviceHeight());
		Logs.showTrace("Display scale size = " + device.getScaleSize());
		Logs.showTrace("Device orientation = " + device.getOrientation());
		Logs.showTrace("Device inches = " + device.getDisplayIncheSize());
		Logs.showTrace("Device type = " + device.getDeviceType()); // 0:phone 1:tablet
		device = null;
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
											case EventMessage.MSG_DOUBLE_CLICK:
												showOption();
												Logs.showTrace("Activity receive double click");
												break;
											case EventMessage.MSG_FLIPPER_CLOSE:
												optionHandler.clearHeaderSelected(pageReader.getCurrentChapter(),
														pageReader.getCurrentPage());
												break;
											case EventMessage.MSG_GO_FORWARD:
												pageReader.goForward();
												break;
											case EventMessage.MSG_OPTION_ITEM_SELECTED:
												showOption();
												pageReader.jumpPage(msg.arg1, msg.arg2);
												break;
											case GalleryView.MSG_WND_CLICK:
												optionHandler.closeFlipView();
												break;
											case GalleryView.MSG_IMAGE_CLICK:
												showOption();
												pageReader.jumpPage(msg.arg1, msg.arg2);
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
											}
											super.handleMessage(msg);
										}
									};
}

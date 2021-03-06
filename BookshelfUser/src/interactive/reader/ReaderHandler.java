package interactive.reader;

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
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReaderHandler
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
	private boolean						mbIsActive			= true;
	private Activity					theActivity			= null;

	public ReaderHandler(Activity activity)
	{
		super();
		theActivity = activity;

		/** initialize header bar */
		initHeader();

		/** initialize page reader from reader layout */
		initPageReader();

		/** init option handler */
		optionHandler = new OptionHandler(activity);

		/** init book handler */
		bookHandler = new BookHandler();
		bookHandler.setNotifyHandler(selfHandler);
	}

	public void Resume()
	{
		mbIsActive = true;
		optionHandler.clearHeaderSelected(pageReader.getCurrentChapter(), pageReader.getCurrentPage());
	}

	public void pause()
	{
		mbIsActive = false;
	}

	public void ConfigurationChanged(Configuration newConfig)
	{
		if (newConfig.orientation != mnOrientation && mbIsActive)
		{
			mnOrientation = newConfig.orientation;
			loadDisplayPage(configData, mstrBookPath);
			pageReader.jumpPage(pageReader.getCurrentChapter(), pageReader.getCurrentPage(), true);
			Logs.showTrace("Orientation change: " + newConfig.orientation);
		}
	}

	private void initPageReader()
	{
		int nResId = Global.getResourceId(theActivity, "readerPageReader", "id");
		pageReader = (PageReader) theActivity.findViewById(nResId);
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
		int nResId = Global.getResourceId(theActivity, "readerHeaderMain", "id");
		rlLayoutHeader = (RelativeLayout) theActivity.findViewById(nResId);
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
		progressDialog = ProgressDialog.show(theActivity, "", strMsg, true, false);
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

	/**
	 * 頛�貊�
	 * 
	 * @param strBookPath
	 *            :�貊�頝臬�
	 */
	public void initBook(String strBookPath)
	{
		if (null == strBookPath)
		{
			Global.showToast("Book Path Invalid");
			return;
		}
		mstrBookPath = strBookPath;
		Logs.showTrace("Start init book: " + mstrBookPath);
		configData = new ConfigData();
		if (bookHandler.parseBook(mstrBookPath, configData))
		{
			setHeaderBookName(configData.thePackage.metaData.strAppName);
			getFavoriteData(theActivity);
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
		Device device = new Device(theActivity);
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
				theActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
				theActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}

		return false;
	}

	private void initOption()
	{
		optionHandler.initCategory(theActivity);
		optionHandler.initChapOption(theActivity);
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
		TextView txBookName = (TextView) theActivity
				.findViewById(Global.getResourceId(theActivity, "textViewBookName", "id"));
		txBookName.setText(strBookName);
		txBookName.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View arg0)
			{
				String strVersionValue = null;
				String strVersionName = null;
				WordHandler word = new WordHandler();
				strVersionName = word.getString(theActivity, "reader_version");
				word = null;
				VersionHandler version = new VersionHandler();
				strVersionValue = version.getVersionName(theActivity);

				String strVersion = strVersionName + " : V" + strVersionValue;
				Logs.showTrace("==== Interactive Reader Version: " + strVersion + " ====");
				Toast.makeText(theActivity, strVersion, Toast.LENGTH_LONG).show();
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
			theActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
			theActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

			// we lock first, when wen load page
			Device device = new Device(theActivity);
			int nOrientation = device.getOrientation();
			device = null;
			if (Configuration.ORIENTATION_LANDSCAPE == nOrientation)
			{
				theActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			else
			{
				theActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
		else if (strDefaultOrientation.contains("portrait"))
		{
			nBookOrientation = Configuration.ORIENTATION_PORTRAIT;
			theActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else if (strDefaultOrientation.contains("landscape"))
		{
			nBookOrientation = Configuration.ORIENTATION_LANDSCAPE;
			theActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
				DisplayPage dpage = new DisplayPage(theActivity);
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

		Device device = new Device(theActivity);
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

	public void ActivityResult(int requestCode, int resultCode, Intent data)
	{
		Logs.showTrace("onActivityResult : requestCode=" + requestCode + " resultCode=" + resultCode + " data=" + data);

		IntentHandler intentHandler = new IntentHandler();
		Bitmap bitmap = intentHandler.activityResult(theActivity, requestCode, resultCode, data);
		if (null != bitmap)
		{
			EventHandler.notify(Global.handlerPostcard, EventMessage.MSG_ACTIVITY_RESULT, 0, 0, bitmap);
		}
	}

	private Handler selfHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
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

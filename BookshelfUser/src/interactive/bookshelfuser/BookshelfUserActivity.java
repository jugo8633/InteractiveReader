package interactive.bookshelfuser;

import interactive.common.Device;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.gcm.GcmRegister;
import interactive.gcm.ShareExternalServer;
import interactive.service.httpclient.CommuData;
import interactive.service.httpclient.HttpClientService;
import interactive.service.httpclient.HttpClientServiceAPI;
import interactive.view.flip.AnimationType;
import interactive.view.global.Global;
import interactive.widget.PullToRefreshListView;
import interactive.widget.PullToRefreshListView.OnRefreshListener;
import interactive.widget.TabButton;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;

public class BookshelfUserActivity extends Activity
{

	private FootbarHandler			footbar					= null;
	private ViewFlipper				flipper					= null;
	private ViewFlipper				flipperBookCityBookList	= null;
	private int						mnCurrentFootbarItem	= 0;
	private WelcomePage				welcomePage				= null;
	private int						mnListMenuBtnId			= Type.INVALID;
	private DrawerLayout			drawerLayout			= null;
	private ImageView				listMenuBtn				= null;
	private TabButton				tabButton				= null;
	private PullToRefreshListView	pullRefreshList			= null;
	private MenuOptionHandler		menuOptionHandler		= null;
	private BookListHandler			bookListHandler			= null;
	private BillingHandler			billingHandler			= null;
	public static int				ICON_ID					= Type.INVALID;
	private HttpClientHandler		httpClientHandler		= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/** init orientation*/
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		/** show device information */
		getDeviceInfo(this);

		/** init global*/
		Global.theActivity = this;
		Global.handlerActivity = selfHandler;
		ICON_ID = Global.getResourceId(this, "ic_launcher", "drawable");

		/** show welcome page */
		welcomePage = new WelcomePage(this);
		welcomePage.show();

		/** load book layout */
		int nResId = Global.getResourceId(this, "activity_main", "layout");
		this.setContentView(nResId);

		/** init flip */
		flipper = (ViewFlipper) this.findViewById(Global.getResourceId(this, "book_list_flipper", "id"));
		AnimationType animationType = new AnimationType();
		flipper.setOutAnimation(animationType.outToLeftAnimation(200));
		flipper.setInAnimation(animationType.inFromRightAnimation(500));

		/** init footbar handler */
		footbar = new FootbarHandler(this);
		footbar.setDefaultSelected(Global.getResourceId(this, "bookCityBtn", "id"));
		footbar.setOnItemSelectedListener(new FootbarHandler.OnItemSelectedListener()
		{
			@Override
			public void OnItemSelected(int nIndexSelected)
			{
				switchMode(nIndexSelected);
				Logs.showTrace("Footbar item selected index=" + nIndexSelected);
			}
		});

		/** init book city book list */
		flipperBookCityBookList = (ViewFlipper) this
				.findViewById(Global.getResourceId(this, "book_city_flipper", "id"));
		flipperBookCityBookList.setOutAnimation(animationType.outToLeftAnimation(200));
		flipperBookCityBookList.setInAnimation(animationType.inFromRightAnimation(500));
		animationType = null;

		/** init list menu button */
		mnListMenuBtnId = Global.getResourceId(this, "listMenuBtn", "id");
		listMenuBtn = (ImageView) this.findViewById(mnListMenuBtnId);
		listMenuBtn.setOnClickListener(buttonClick);

		/** init drawer layout */
		drawerLayout = (DrawerLayout) this.findViewById(Global.getResourceId(this, "drawer_layout", "id"));
		drawerLayout.setDrawerShadow(Global.getResourceId(this, "drawer_shadow", "drawable"), GravityCompat.START);
		drawerLayout.setDrawerListener(new DrawerListener()
		{
			@Override
			public void onDrawerClosed(View arg0)
			{
				drawerHandler(false);
			}

			@Override
			public void onDrawerOpened(View arg0)
			{
				drawerHandler(true);
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1)
			{

			}

			@Override
			public void onDrawerStateChanged(int arg0)
			{

			}
		});

		/** init menu option */
		menuOptionHandler = new MenuOptionHandler(this);
		menuOptionHandler.setNotifyHandler(selfHandler);

		/** init pull to refresh listview */
		pullRefreshList = (PullToRefreshListView) this.findViewById(Global.getResourceId(this, "pull_to_refresh_list",
				"id"));
		DrawerMenuAdapter menuAdapter = new DrawerMenuAdapter(this);
		pullRefreshList.setAdapter(menuAdapter);
		menuAdapter = null;
		pullRefreshList.setOnRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				Logs.showTrace("Refresh.......");
				new GetDataTask().execute();
			}
		});

		pullRefreshList.setOnItemSelectedListener(new PullToRefreshListView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(int nIndex)
			{
				Logs.showTrace("Menu item selected index=" + nIndex);
				switch (nIndex)
				{
				case DrawerMenuAdapter.INDEX_LOGIN:
					menuOptionHandler.showLogin();
					break;
				case DrawerMenuAdapter.INDEX_SETTING:
					menuOptionHandler.showSetting();
					break;
				case DrawerMenuAdapter.INDEX_NEWS:
					menuOptionHandler.showNews();
					break;
				case DrawerMenuAdapter.INDEX_SUBSCRIPT:
					break;
				}
			}
		});

		/** init tab button */
		tabButton = (TabButton) this.findViewById(Global.getResourceId(this, "tabButton", "id"));
		tabButton.addTextButton(this.getString(Global.getResourceId(this, "all_book", "string")));
		tabButton.addTextButton(this.getString(Global.getResourceId(this, "free_book", "string")));
		tabButton.addTextButton(this.getString(Global.getResourceId(this, "special_book", "string")));
		tabButton.addTextButton(this.getString(Global.getResourceId(this, "previous_book", "string")));
		tabButton.setItemSelect(0);
		tabButton.setOnItemSwitchedListener(new TabButton.OnItemSwitchedListener()
		{
			@Override
			public void onItemSwitched(int nIndex)
			{
				switchBookCityBookList(nIndex);
				Logs.showTrace("tab button item switch index=" + nIndex);
			}
		});

		/** init book city book gallery */
		bookListHandler = new BookListHandler();
		bookListHandler.setNotifyHandler(selfHandler);
		bookListHandler.initAllBookList(this);
		bookListHandler.initFreeBookList(this);
		bookListHandler.initSpecialBookList(this);
		bookListHandler.initPreviousBookList(this);

		/** init billing handler */
		billingHandler = new BillingHandler(this);

		/** register GCM */
		setRegisteringGCM();

		/** init http client service */
		httpClientHandler = new HttpClientHandler(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Logs.showTrace("onActivityResult(" + requestCode + "," + resultCode + "," + data);

		// Pass on the activity result to the helper for handling
		if (!billingHandler.handleActivityResult(requestCode, resultCode, data))
		{
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
		else
		{
			Logs.showTrace("onActivityResult handled by IABUtil.");
		}
	}

	@Override
	protected void onDestroy()
	{
		billingHandler.closeService();
		billingHandler = null;
		setUnregisteringGCM();
		httpClientHandler.unBindService(this);
		httpClientHandler = null;

		super.onDestroy();
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]>
	{

		@Override
		protected String[] doInBackground(Void... params)
		{
			// Simulates a background job.
			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result)
		{
			pullRefreshList.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	private void drawerHandler(boolean bOpen)
	{
		if (bOpen)
		{
			listMenuBtn.setImageResource(Global.getResourceId(this, "list_click", "drawable"));
		}
		else
		{
			listMenuBtn.setImageResource(Global.getResourceId(this, "list_normal", "drawable"));
		}
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
		Logs.showTrace("Android SDK Version = " + device.getAndroidSdkVersion());
		Logs.showTrace("Android Release Version = " + device.getAndroidReleaseVersion());
		Logs.showTrace("=======================================================");
		device = null;
	}

	private void switchMode(int nIndex)
	{
		if (mnCurrentFootbarItem == nIndex)
		{
			return;
		}

		mnCurrentFootbarItem = nIndex;
		flipper.setDisplayedChild(mnCurrentFootbarItem);

		Logs.showTrace("Flipper show child index=" + nIndex);
	}

	private void switchBookCityBookList(int nIndex)
	{
		flipperBookCityBookList.setDisplayedChild(nIndex);
	}

	private void handleButtonClick(View view)
	{
		if (view.getId() == mnListMenuBtnId)
		{
			if (drawerLayout.isDrawerOpen(Gravity.LEFT))
			{
				drawerLayout.closeDrawers();
			}
			else
			{
				drawerLayout.openDrawer(Gravity.LEFT);
			}
		}
	}

	private OnClickListener	buttonClick	= new OnClickListener()
										{
											@Override
											public void onClick(View view)
											{
												handleButtonClick(view);
											}
										};

	private Handler			selfHandler	= new Handler()
										{
											@Override
											public void handleMessage(Message msg)
											{
												switch (msg.what)
												{
												case EventMessage.MSG_FLIPPER_CLOSE:
													pullRefreshList.clearSelected();
													break;
												case BookListHandler.MSG_SUBSCRIBT:
													billingHandler.launchPurchase(BookshelfUserActivity.this, "book");

													break;
												case EventMessage.MSG_GCM_REGISTERED:
													Logs.showTrace("Share GCM Register Id " + msg.obj
															+ " with application server.");
													shareRegIdWithAppServer((String) msg.obj);
													break;
												case EventMessage.MSG_LOGIN:
													if (null != msg.obj)
													{
														CommuData data = (CommuData) msg.obj;
														httpClientHandler.login(data.mstrAccount, data.mstrPassword);
													}
													break;
												}
											}
										};

	public void setRegisteringGCM()
	{
		//註冊推播
		GcmRegister gcmReg = new GcmRegister(this);
		gcmReg.setOnRegisterFinishedListener(new GcmRegister.OnRegisterFinishedListener()
		{
			@Override
			public void onRegisterFinished(String strRegId)
			{
				if (null != strRegId)
				{
					EventHandler.notify(selfHandler, EventMessage.MSG_GCM_REGISTERED, 0, 0, strRegId);
				}
			}
		});
		gcmReg.register();
		gcmReg = null;
	}

	public void setUnregisteringGCM()
	{
		//取消註冊推播
		GcmRegister gcmReg = new GcmRegister(this);
		gcmReg.unregister();
		gcmReg = null;
	}

	private void shareRegIdWithAppServer(final String strRegId)
	{
		AsyncTask<Void, Void, String> shareRegidTask = new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				ShareExternalServer appServer = new ShareExternalServer();
				String strResult = appServer.shareRegIdWithAppServer(BookshelfUserActivity.this, strRegId);
				appServer = null;

				Logs.showTrace("GCM share register id with application server result: " + strResult);
				return null;
			}

			@Override
			protected void onPostExecute(String result)
			{

			}

		};
		shareRegidTask.execute(null, null, null);
	}

}

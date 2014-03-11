package interactive.bookshelfuser;

import interactive.common.Device;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.flip.AnimationType;
import interactive.view.global.Global;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.app.Activity;

public class BookshelfUserActivity extends Activity
{

	private FootbarHandler	footbar					= null;
	private ViewFlipper		flipper					= null;
	private int				mnCurrentFootbarItem	= 0;
	private WelcomePage		welcomePage				= null;
	private int				mnListMenuBtnId			= Type.INVALID;
	private DrawerLayout	drawerLayout			= null;
	private ImageView		listMenuBtn				= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/** show device information */
		getDeviceInfo(this);

		/** init global*/
		Global.theActivity = this;
		Global.handlerActivity = selfHandler;

		/** show welcome page */
		welcomePage = new WelcomePage(this);
		welcomePage.show();

		/** load book layout */
		int nResId = Global.getResourceId(this, "activity_main", "layout");
		this.setContentView(nResId);

		/** init flip */
		flipper = (ViewFlipper) this.findViewById(Global.getResourceId(this, "viewflipper", "id"));
		AnimationType animationType = new AnimationType();
		flipper.setOutAnimation(animationType.outToLeftAnimation(200));
		flipper.setInAnimation(animationType.inFromRightAnimation(500));
		animationType = null;

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

		/** init list menu button */
		mnListMenuBtnId = Global.getResourceId(this, "listMenuBtn", "id");
		listMenuBtn = (ImageView) this.findViewById(mnListMenuBtnId);
		listMenuBtn.setOnClickListener(buttonClick);

		/** init drawer layout */
		drawerLayout = (DrawerLayout) this.findViewById(Global.getResourceId(this, "drawer_layout", "id"));
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
												super.handleMessage(msg);
											}

										};

}
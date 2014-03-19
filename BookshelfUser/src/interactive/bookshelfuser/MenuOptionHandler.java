package interactive.bookshelfuser;

import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.flip.FlipperView;
import interactive.view.global.Global;
import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class MenuOptionHandler
{

	private FlipperView		flipperView			= null;
	private MenuID			menuId				= null;
	private RelativeLayout	settingMainLayout	= null;
	private RelativeLayout	loginMainLayout		= null;

	private class MenuID
	{
		public int	mnLoginId		= Type.INVALID;
		public int	mnSettingId		= Type.INVALID;
		public int	mnNewsId		= Type.INVALID;
		public int	mnSubscribeId	= Type.INVALID;
	}

	public MenuOptionHandler(Activity activity)
	{
		super();
		init(activity);
	}

	private void init(Activity activity)
	{
		menuId = new MenuID();

		flipperView = (FlipperView) activity.findViewById(Global.getResourceId(activity, "fliper_menu_option", "id"));
		if (null == flipperView)
		{
			Logs.showTrace("Flipper view is invalid");
			return;
		}

		menuId.mnLoginId = flipperView.addChild(Global.getResourceId(activity, "login", "layout"));
		menuId.mnSettingId = flipperView.addChild(Global.getResourceId(activity, "setting", "layout"));

		settingMainLayout = (RelativeLayout) flipperView.findViewById(Global.getResourceId(activity,
				"setting_main_layout", "id"));

		loginMainLayout = (RelativeLayout) flipperView.findViewById(Global.getResourceId(activity, "login_main_layout",
				"id"));

		loginMainLayout.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					return true;
				}
				return false;
			}
		});

		settingMainLayout.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					return true;
				}
				return false;
			}
		});
	}

	public void setNotifyHandler(Handler handler)
	{
		flipperView.setNotifyHandler(handler);
	}

	public void showLogin()
	{
		loginMainLayout.setVisibility(View.VISIBLE);
		flipperView.showView(menuId.mnLoginId);
	}

	public void showSetting()
	{
		settingMainLayout.setVisibility(View.VISIBLE);
		flipperView.showView(menuId.mnSettingId);
	}

	public void hideAllView()
	{
		loginMainLayout.setVisibility(View.GONE);
		settingMainLayout.setVisibility(View.GONE);
	}
}

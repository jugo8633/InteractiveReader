package interactive.bookshelfuser;

import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.flip.FlipperView;
import interactive.view.global.Global;
import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuOptionHandler
{

	private FlipperView		flipperView			= null;
	private MenuID			menuId				= null;
	private RelativeLayout	loginMainLayout		= null;
	private RelativeLayout	accountMainLayout	= null;

	private class MenuID
	{
		public int	mnLoginId		= Type.INVALID;
		public int	mnSettingId		= Type.INVALID;
		public int	mnNewsId		= Type.INVALID;
		public int	mnSubscribeId	= Type.INVALID;
		public int	mnAccountAdd	= Type.INVALID;
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
		menuId.mnNewsId = flipperView.addChild(Global.getResourceId(activity, "news", "layout"));
		menuId.mnAccountAdd = flipperView.addChild(Global.getResourceId(activity, "account_add", "layout"));

		flipperView.findViewById(Global.getResourceId(activity, "setting_main_layout", "id")).setOnTouchListener(
				mainLayoutTouch);

		loginMainLayout = (RelativeLayout) flipperView.findViewById(Global.getResourceId(activity, "login_main_layout",
				"id"));
		loginMainLayout.setOnTouchListener(mainLayoutTouch);

		flipperView.findViewById(Global.getResourceId(activity, "news_main_layout", "id")).setOnTouchListener(
				mainLayoutTouch);

		accountMainLayout = (RelativeLayout) flipperView.findViewById(Global.getResourceId(activity,
				"account_add_main_layout", "id"));
		accountMainLayout.setOnTouchListener(mainLayoutTouch);

		loginHandle(activity);
		accountAddHandle(activity);

	}

	public void setNotifyHandler(Handler handler)
	{
		flipperView.setNotifyHandler(handler);
	}

	public void showLogin()
	{
		flipperView.showView(menuId.mnLoginId);
	}

	public void showSetting()
	{
		flipperView.showView(menuId.mnSettingId);
	}

	public void showNews()
	{
		flipperView.showView(menuId.mnNewsId);
	}

	private void loginHandle(Activity activity)
	{
		TextView addAccount = (TextView) loginMainLayout.findViewById(Global.getResourceId(activity, "add_account",
				"id"));
		addAccount.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				flipperView.showView(menuId.mnAccountAdd);
			}
		});
	}

	private void accountAddHandle(Activity activity)
	{
		TextView cancel = (TextView) accountMainLayout.findViewById(Global.getResourceId(activity, "button_cancel",
				"id"));
		cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				flipperView.showView(menuId.mnLoginId);
			}
		});
	}

	private OnTouchListener	mainLayoutTouch	= new OnTouchListener()
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
											};
}

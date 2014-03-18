package interactive.bookshelfuser;

import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.flip.FlipperView;
import interactive.view.global.Global;
import android.app.Activity;
import android.os.Handler;

public class MenuOptionHandler
{

	private FlipperView	flipperView	= null;
	private MenuID		menuId		= null;

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
	}

	public void setNotifyHandler(Handler handler)
	{
		flipperView.setNotifyHandler(handler);
	}

	public void showLogin()
	{
		flipperView.showView(menuId.mnLoginId);
	}

}

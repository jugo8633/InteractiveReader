package interactive.view.global;

import interactive.common.Device;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.handler.InteractiveHandler;
import interactive.view.pagereader.PageReader;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class Global
{
	public static Activity					theActivity			= null;
	public static InteractiveHandler		interactiveHandler	= new InteractiveHandler();
	public static Handler					handlerActivity		= null;
	public static int						currentChapter		= 0;
	public static int						currentPage			= 0;
	public static PageReader				pageReader			= null;
	public static Handler					handlerPostcard		= null;
	public static SparseArray<ActiveNotify>	listActiveNotify	= new SparseArray<ActiveNotify>();
	public static SparseArray<ActiveNotify>	listUnActiveNotify	= new SparseArray<ActiveNotify>();
	public static int						mnUserId			= 2048;

	public static int getUserId()
	{
		return (++mnUserId);
	}

	public static class ActiveNotify
	{
		public int		mnChapter	= Type.INVALID;
		public int		mnPage		= Type.INVALID;
		public Handler	mHandler	= null;

		public ActiveNotify(int nChapter, int nPage, Handler handler)
		{
			mnChapter = nChapter;
			mnPage = nPage;
			mHandler = handler;
		}
	}

	public static int getResourceId(Context context, String name, String defType)
	{
		return context.getResources().getIdentifier(name, defType, context.getPackageName());
	}

	public static int ScaleSize(int nSize)
	{
		Device device = new Device(theActivity);
		float fScale = device.getScaleSize();
		device = null;

		int nResultSize = (int) Math.floor(nSize * fScale);
		return nResultSize;
	}

	/** notify when page is current page*/
	public static void addActiveNotify(int nChapter, int nPage, Handler handler)
	{
		listActiveNotify.put(listActiveNotify.size(), new Global.ActiveNotify(nChapter, nPage, handler));
	}

	/** notify when page is not current page*/
	public static void addUnActiveNotify(int nChapter, int nPage, Handler handler)
	{
		listUnActiveNotify.put(listUnActiveNotify.size(), new Global.ActiveNotify(nChapter, nPage, handler));
	}

	public static void notifyActive(int nChapter, int nPage)
	{
		for (int i = 0; i < listActiveNotify.size(); ++i)
		{
			if (listActiveNotify.get(i).mnChapter == nChapter && listActiveNotify.get(i).mnPage == nPage)
			{
				EventHandler.notify(listActiveNotify.get(i).mHandler, EventMessage.MSG_CURRENT_ACTIVE, 0, 0, null);
			}
		}

		for (int j = 0; j < listUnActiveNotify.size(); ++j)
		{
			if (listUnActiveNotify.get(j).mnChapter != nChapter || listUnActiveNotify.get(j).mnPage != nPage)
			{
				EventHandler
						.notify(listUnActiveNotify.get(j).mHandler, EventMessage.MSG_NOT_CURRENT_ACTIVE, 0, 0, null);
			}
		}
	}

	public static void showToast(String strMsg)
	{
		if (null == theActivity)
		{
			return;
		}
		Toast toast = Toast.makeText(theActivity, strMsg, Toast.LENGTH_LONG);
		LinearLayout toastLayout = (LinearLayout) toast.getView();
		TextView toastTV = (TextView) toastLayout.getChildAt(0);
		toastTV.setTextSize(30);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}

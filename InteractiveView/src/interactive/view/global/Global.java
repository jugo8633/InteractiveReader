package interactive.view.global;

import interactive.common.Device;
import interactive.view.handler.InteractiveHandler;
import interactive.view.pagereader.PageReader;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public class Global
{
	public static Activity				theActivity			= null;
	public static InteractiveHandler	interactiveHandler	= new InteractiveHandler();
	public static Handler				handlerActivity		= null;
	public static int					currentChapter		= 0;
	public static int					currentPage			= 0;
	public static PageReader			pageReader			= null;
	public static Handler				handlerPostcard		= null;

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
}

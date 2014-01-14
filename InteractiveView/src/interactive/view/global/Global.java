package interactive.view.global;

import interactive.view.handler.InteractiveHandler;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;

public class Global
{
	public static Activity				theActivity			= null;
	public static InteractiveHandler	interactiveHandler	= new InteractiveHandler();
	public static Handler				handlerActivity		= null;

	public static void setActivity(Activity activity)
	{
		theActivity = activity;
	}

	public static int getResourceId(Context context, String name, String defType)
	{
		return context.getResources().getIdentifier(name, defType, context.getPackageName());
	}
}

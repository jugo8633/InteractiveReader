package interactive.view.global;

import interactive.view.json.InteractiveHandler;
import android.app.Activity;
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
}

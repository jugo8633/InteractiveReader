package interactive.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// Start the service, keeping the device awake while the service is  
		// launching. This is the Intent to deliver to the service.  
		//Intent service = new Intent(context, GCMNotificationIntentService.class);  
		//startWakefulService(context, service);  

		ComponentName comp = new ComponentName(context.getPackageName(), GCMNotificationIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}

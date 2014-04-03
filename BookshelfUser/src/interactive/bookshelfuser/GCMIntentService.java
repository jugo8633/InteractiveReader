package interactive.bookshelfuser;

import java.util.Calendar;

import interactive.common.Logs;
import interactive.view.global.Global;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService
{

	public static final String	SENDER_ID	= "15722213180";

	public GCMIntentService()
	{
		super(SENDER_ID);
	}

	public GCMIntentService(String... senderIds)
	{
		super(senderIds);
	}

	@Override
	protected void onError(Context arg0, String arg1)
	{

	}

	@Override
	protected void onMessage(Context arg0, Intent arg1)
	{
		NotifycationHandler.createNotification(arg0, Calendar.getInstance().getTimeInMillis(), "AppCross Bookshelf",
				"Notifycation...", "", BookshelfUserActivity.class);
	}

	@Override
	protected void onRegistered(Context arg0, String strRegisterId)
	{
		//arg1 GCM 回傳回來的Registration ID 
		//在此可以將Registration ID 回傳 User Server
		Logs.showTrace("GCM registered Id=" + strRegisterId);
	}

	@Override
	protected void onUnregistered(Context arg0, String strRegisterId)
	{
		Logs.showTrace("GCM unregistered Id=" + strRegisterId);
	}

}

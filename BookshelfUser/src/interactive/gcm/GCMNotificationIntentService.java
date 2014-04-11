package interactive.gcm;

import interactive.bookshelfuser.BookshelfUserActivity;
import interactive.bookshelfuser.NotifycationHandler;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService
{

	//public static final int	NOTIFICATION_ID	= 1;
	//private NotificationManager	mNotificationManager;
	//NotificationCompat.Builder	builder;

	public GCMNotificationIntentService()
	{
		super("GcmIntentService");
	}

	public static final String	TAG	= "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty())
		{
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
			{
				sendNotification("Send error: " + extras.toString());
			}
			else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
			{
				sendNotification("Deleted messages on server: " + extras.toString());
			}
			else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
			{

				for (int i = 0; i < 3; i++)
				{
					Log.i(TAG, "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
					try
					{
						Thread.sleep(5000);
					}
					catch (InterruptedException e)
					{
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				sendNotification("Message Received from Google GCM Server: " + extras.get(Config.MESSAGE_KEY));
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg)
	{
		NotifycationHandler.createNotification(this, System.currentTimeMillis(), "GCM Notification", msg, msg,
				BookshelfUserActivity.class);

		//		Log.d(TAG, "Preparing to send notification...: " + msg);
		//		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		//
		//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, BookshelfUserActivity.class),
		//				0);
		//
		//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		//				.setSmallIcon(BookshelfUserActivity.ICON_ID).setContentTitle("GCM Notification")
		//				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);
		//
		//		mBuilder.setContentIntent(contentIntent);
		//		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		//		Log.d(TAG, "Notification sent successfully.");
	}
}

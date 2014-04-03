package interactive.bookshelfuser;

import interactive.view.global.Global;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class NotifycationHandler
{

	public static final String	NOTIFICATION_DATA	= "NOTIFICATION_DATA";

	public NotifycationHandler()
	{

	}

	public static void createNotification(Context context, long when, String notificationTitle,
			String notificationContent, String notificationData, Class<?> intentAction)
	{
		//large icon for notification,normally use App icon
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
				Global.getResourceId(context, "ic_launcher", "drawable"));
		int smalIcon = Global.getResourceId(context, "ic_launcher", "drawable");

		/*
		 * create intent for show notification details when user clicks
		 * notification
		 */
		Intent intent = new Intent(context.getApplicationContext(), intentAction);
		intent.putExtra(NOTIFICATION_DATA, notificationData);

		/* create unique this intent from other intent using setData */
		intent.setData(Uri.parse("content://" + when));
		/*
		 * create new task for each notification with pending intent so we set
		 * Intent.FLAG_ACTIVITY_NEW_TASK
		 */
		PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

		/* get the system service that manage notification NotificationManager */
		NotificationManager notificationManager = (NotificationManager) context.getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		/* build the notification */
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext())
				.setWhen(when).setContentText(notificationContent).setContentTitle(notificationTitle)
				.setSmallIcon(smalIcon).setAutoCancel(true).setTicker(notificationTitle).setLargeIcon(largeIcon)
				.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
				.setContentIntent(pendingIntent);

		/* Create notification with builder */
		Notification notification = notificationBuilder.build();

		/*
		 * sending notification to system.Here we use unique id (when)for making
		 * different each notification if we use same id,then first notification
		 * replace by the last notification
		 */
		notificationManager.notify((int) when, notification);
	}

}

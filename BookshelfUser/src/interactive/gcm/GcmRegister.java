package interactive.gcm;

import java.io.IOException;

import interactive.common.Logs;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmRegister
{
	private GoogleCloudMessaging	gcm		= null;
	private String					regId	= null;

	public GcmRegister(Context context)
	{
		super();
		gcm = GoogleCloudMessaging.getInstance(context.getApplicationContext());
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	public String register()
	{
		registerInBackground();

		if (!TextUtils.isEmpty(regId))
		{
			Logs.showTrace("registerGCM - successfully registered with GCM server - regId: " + regId);
		}
		return regId;
	}

	public void unregister()
	{

		new AsyncTask<Void, Void, String>()
		{

			@Override
			protected String doInBackground(Void... params)
			{
				try
				{
					gcm.unregister();
					Logs.showTrace("unregisterGCM - successfully");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				return null;
			}

		}.execute(null, null, null);
	}

	private void registerInBackground()
	{
		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				String msg = "";
				try
				{
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Logs.showTrace("registerGCM - successfully registered with GCM server - regId: " + regId);
					msg = "Device registered, registration ID=" + regId;
				}
				catch (IOException ex)
				{
					msg = "Error :" + ex.getMessage();
					Logs.showTrace(msg);
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg)
			{

			}
		}.execute(null, null, null);
	}

}

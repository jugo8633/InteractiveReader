package interactive.gcm;

import java.io.IOException;

import interactive.common.Logs;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmRegister
{
	private GoogleCloudMessaging		gcm							= null;
	private OnRegisterFinishedListener	mOnRegisterFinishedListener	= null;

	public GcmRegister(Context context)
	{
		super();
		gcm = GoogleCloudMessaging.getInstance(context.getApplicationContext());
	}

	public static interface OnRegisterFinishedListener
	{
		public void onRegisterFinished(String strRegId);
	}

	public void setOnRegisterFinishedListener(GcmRegister.OnRegisterFinishedListener listener)
	{
		if (null != listener)
		{
			mOnRegisterFinishedListener = listener;
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	public void register()
	{
		registerInBackground();
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
				String strRegId = null;
				try
				{
					strRegId = gcm.register(Config.GOOGLE_PROJECT_ID);

				}
				catch (IOException ex)
				{
					Logs.showTrace("Error :" + ex.getMessage());
				}
				return strRegId;
			}

			@Override
			protected void onPostExecute(String msg)
			{
				Logs.showTrace("registerGCM - register finish with GCM server - regId: " + msg);
				mOnRegisterFinishedListener.onRegisterFinished(msg);
			}
		}.execute(null, null, null);
	}

}

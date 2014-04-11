package interactive.service.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import interactive.bookshelfuser.BookshelfUserActivity;
import interactive.common.Logs;
import interactive.gcm.ShareExternalServer;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;

public class HttpClientService extends Service
{

	private String						mstrAccount	= "jugo";

	private HttpClientServiceAPI.Stub	apiEndpoint	= new HttpClientServiceAPI.Stub()
													{
														@Override
														public void Login(String strAccount, String strPassword)
																throws RemoteException
														{
															mstrAccount = strAccount;
															Logs.showTrace("do Login account=" + mstrAccount
																	+ " password=" + strPassword);
															doLogin(strAccount, strPassword);
														}
													};

	@Override
	public void onCreate()
	{
		super.onCreate();
		Logs.showTrace("Http client service create");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Logs.showTrace("Http client service destory");
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		Logs.showTrace("Http client service start");
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Logs.showTrace("Bind AIDL ");
		return apiEndpoint;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		super.onUnbind(intent);
		return true;
	}

	private void doLogin(final String strName, final String strPasswd)
	{
		AsyncTask<Void, Void, String> shareRegidTask = new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				HttpURLConnection conn = null;
				String strUrl = "http://appcross.ideas.iii.org.tw:2196/auth/login?username=" + strName + "&password="
						+ strPasswd;
				Logs.showTrace("GCM application server connect: " + strUrl);
				URL url = null;
				try
				{
					url = new URL(strUrl);
				}
				catch (MalformedURLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try
				{
					conn = (HttpURLConnection) url.openConnection();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				try
				{
					conn.setRequestMethod("POST");
				}
				catch (ProtocolException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try
				{
					conn.connect();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 讀取資料
				BufferedReader reader = null;
				try
				{
					reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				}
				catch (UnsupportedEncodingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String result = null;

				try
				{
					if (null != reader)
					{
						result = reader.readLine();
						String line;
						while ((line = reader.readLine()) != null)
						{
							result += line;
						}
						reader.close();
					}

				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				Logs.showTrace("result=" + result + " ##########################################");
				// 讀取結果
				int status = 0;
				try
				{
					status = conn.getResponseCode();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (200 == status)
				{
					result = "RegId shared with Application Server. RegId: ";

				}
				else
				{
					result = "Post Failure." + " Status: " + status;
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result)
			{

			}

		};
		shareRegidTask.execute(null, null, null);
	}
}

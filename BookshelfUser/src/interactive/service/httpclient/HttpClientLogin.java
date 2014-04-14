package interactive.service.httpclient;

import interactive.common.Logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.os.AsyncTask;

public class HttpClientLogin
{
	private OnLoginFinishListener	onLoginFinishListener	= null;

	public HttpClientLogin()
	{
		super();
	}

	public static interface OnLoginFinishListener
	{
		public void onLoginFinish(String strResult);
	}

	public void setOnLoginFinishListener(HttpClientLogin.OnLoginFinishListener listener)
	{
		if (null != listener)
		{
			onLoginFinishListener = listener;
		}
	}

	public void doLogin(final String strName, final String strPasswd)
	{
		AsyncTask<Void, Void, String> shareRegidTask = new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				String result = null;
				HttpURLConnection conn = null;
				URL url = null;

				String strUrl = HttpClientConfig.LOGIN_URL + HttpClientConfig.LOGIN_NAME + "=" + strName + "&"
						+ HttpClientConfig.LOGIN_PASSWORD + "=" + strPasswd;

				Logs.showTrace("GCM application server connect: " + strUrl);

				try
				{
					url = new URL(strUrl);
					conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(10000);
					conn.setConnectTimeout(15000);
					conn.setRequestMethod("POST");
					conn.connect();

					// http讀取結果
					if (HttpStatus.SC_OK == conn.getResponseCode())
					{
						// 讀取資料
						BufferedReader reader = null;
						reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

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

					return result;

				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
				catch (ProtocolException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result)
			{
				Logs.showTrace("Http client login result: " + result);
				onLoginFinishListener.onLoginFinish(result);
			}

		};
		shareRegidTask.execute(null, null, null);
	}
}

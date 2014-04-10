package interactive.gcm;

import interactive.common.Logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;

public class ShareExternalServer
{

	private final String	ENCODING		= "UTF-8";
	private final String	FILE_SERVER_URL	= "push_server_url";

	public ShareExternalServer()
	{
		super();
	}

	public String shareRegIdWithAppServer(final Context context, final String regId)
	{
		String strServerUrl = getApplicationServerUrl(context);
		if (null == strServerUrl)
		{
			Logs.showTrace("Share GCM register id fail, not get server url");
			return null;
		}

		Logs.showTrace("Get GCM application server: " + strServerUrl);

		String result = "";
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("regId", regId);
		try
		{
			StringBuilder postBody = new StringBuilder();
			Iterator<Entry<String, String>> iterator = paramsMap.entrySet().iterator();

			while (iterator.hasNext())
			{
				Entry<String, String> param = iterator.next();
				postBody.append(param.getKey()).append('=').append(param.getValue());
				if (iterator.hasNext())
				{
					postBody.append('&');
				}
			}
			String body = postBody.toString();

			// 建立連線
			HttpURLConnection conn = null;
			String strUrl = strServerUrl + "?" + body;
			Logs.showTrace("GCM application server connect: " + strUrl);
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.connect();

			// 讀取資料
			//			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			//			result = reader.readLine();
			//			reader.close();

			// 讀取結果
			int status = conn.getResponseCode();
			if (200 == status)
			{
				result = "RegId shared with Application Server. RegId: " + regId;
			}
			else
			{
				result = "Post Failure." + " Status: " + status;
			}
		}
		catch (IOException e)
		{
			result = "Post Failure. Error in sharing with App Server.";
			Log.e("AppUtil", "Error in sharing with App Server: " + e);
		}

		return result;
	}

	private String getApplicationServerUrl(Context context)
	{
		String strServerUrl = null;
		try
		{
			InputStreamReader inputStream = new InputStreamReader(context.getAssets().open(FILE_SERVER_URL), ENCODING);
			if (null != inputStream)
			{
				BufferedReader bufReader = new BufferedReader(inputStream);

				if (null != bufReader)
				{
					strServerUrl = bufReader.readLine();
					bufReader.close();
					bufReader = null;
				}

				inputStream.close();
				inputStream = null;
			}
		}
		catch (Exception e)
		{
			Logs.showTrace(e.getMessage());
		}

		return strServerUrl;
	}
}

package interactive.service.httpclient;

import org.apache.http.HttpStatus;

import interactive.common.Logs;
import interactive.service.httpclient.HttpClientService;
import interactive.service.httpclient.HttpClientServiceAPI;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class HttpClientHandler
{
	private ServiceConnection			serviceConnection	= null;
	private HttpClientServiceAPI		httpClientApi		= null;

	private HttpClientResponseAPI.Stub	httpResponse		= new HttpClientResponseAPI.Stub()
															{
																@Override
																public void ResponseLogin(int nHttpCode)
																		throws RemoteException
																{
																	HttpClientResponseData respData = httpClientApi
																			.getHttpClientResult();
																	if (HttpStatus.SC_OK == respData
																			.getHttpReturnCode())
																	{
																		Logs.showTrace("Http Client login result="
																				+ respData.getResult());
																	}
																}

																@Override
																public void ResponseBook(int nHttpCode)
																		throws RemoteException
																{
																	// TODO Auto-generated method stub
																	
																}

															};

	public HttpClientHandler(Activity activity)
	{
		super();

		serviceConnection = new ServiceConnection()
		{
			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				Logs.showTrace("Http client Service connection established");

				// that's how we get the client side of the IPC connection
				httpClientApi = HttpClientServiceAPI.Stub.asInterface(service);
				if (null != httpClientApi)
				{
					Logs.showTrace("Http client service API build success.");
					try
					{
						httpClientApi.addResponse(httpResponse);
					}
					catch (RemoteException e)
					{
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				Logs.showTrace("Http client Service connection closed");
			}
		};

		Intent intent = new Intent("interactive.service.httpclient.HttpClientService.REMOTE_SERVICE");
		activity.startService(intent);
		activity.bindService(intent, serviceConnection, Context.BIND_ADJUST_WITH_ACTIVITY | Context.BIND_AUTO_CREATE);
	}

	public void unBindService(Activity activity)
	{
		if (null != activity && null != serviceConnection)
		{
			activity.unbindService(serviceConnection);
			Intent intent = new Intent(HttpClientService.class.getName());
			activity.stopService(intent);
		}
	}

	public void login(String strAccount, String strPassword)
	{
		try
		{
			httpClientApi.Login(strAccount, strPassword);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
}

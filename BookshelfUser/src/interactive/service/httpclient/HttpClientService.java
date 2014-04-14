package interactive.service.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpStatus;

import interactive.common.Logs;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;

public class HttpClientService extends Service
{
	private HttpClientResponseAPI		responseAPI	= null;
	private HttpClientResponseData		respData	= null;

	private HttpClientServiceAPI.Stub	apiEndpoint	= new HttpClientServiceAPI.Stub()
													{
														@Override
														public void Login(String strAccount, String strPassword)
																throws RemoteException
														{

															Logs.showTrace("do Login account=" + strAccount
																	+ " password=" + strPassword);
															HttpClientLogin login = new HttpClientLogin();
															login.setOnLoginFinishListener(new HttpClientLogin.OnLoginFinishListener()
															{
																@Override
																public void onLoginFinish(String strResult)
																{
																	int nStatusCode = HttpStatus.SC_OK;
																	if (null == strResult)
																	{
																		nStatusCode = HttpStatus.SC_BAD_REQUEST;
																	}

																	respData = new HttpClientResponseData(strResult,
																			nStatusCode);
																	try
																	{
																		responseAPI.ResponseLogin(nStatusCode);
																	}
																	catch (RemoteException e)
																	{
																		e.printStackTrace();
																	}
																}
															});

															login.doLogin(strAccount, strPassword);
															login = null;

														}

														@Override
														public void addResponse(HttpClientResponseAPI response)
																throws RemoteException
														{
															synchronized (response)
															{
																responseAPI = response;
															}
														}

														@Override
														public HttpClientResponseData getHttpClientResult()
																throws RemoteException
														{
															return respData;
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
}

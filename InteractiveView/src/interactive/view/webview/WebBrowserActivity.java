package interactive.view.webview;

import interactive.common.EventMessage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class WebBrowserActivity extends Activity
{

	public WebBrowserActivity()
	{
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		WebBrowser webBrowser = new WebBrowser(this);
		webBrowser.setNotifyHandler(handler);
		this.setContentView(webBrowser);

		Intent intent = getIntent();
		String strURL = intent.getStringExtra(InteractiveWebView.EXTRA_URL);
		if (null != strURL)
		{
			webBrowser.loadURL(strURL);
		}
	}

	private Handler	handler	= new Handler()
							{

								@Override
								public void handleMessage(Message msg)
								{
									switch (msg.what)
									{
									case EventMessage.MSG_BROWSER_CLOSE:
										WebBrowserActivity.this.finish();
										break;
									}
									super.handleMessage(msg);
								}

							};

}

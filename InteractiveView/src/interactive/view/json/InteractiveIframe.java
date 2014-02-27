package interactive.view.json;

import interactive.view.global.Global;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.ViewGroup;

public class InteractiveIframe extends InteractiveObject
{

	public InteractiveIframe(Context context)
	{
		super(context);
	}

	@Override
	public boolean createInteractive(ViewGroup container, String strBookPath, JSONObject jsonAll, int nChapter,
			int nPage) throws JSONException
	{
		String strKey = null;
		if (!isCreateValid(container, strBookPath, jsonAll, JSON_IFRAME))
		{
			return false;
		}

		strKey = getValidKey(jsonAll, JSON_IFRAME);
		if (null == strKey)
		{
			return false;
		}

		JSONArray jsonArrayIframe = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayIframe.length(); ++i)
		{
			JSONObject jsonIframe = jsonArrayIframe.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonIframe jsonBody = new JsonIframe();
			if (parseJsonHeader(jsonIframe, jsonHeader) && parseJsonIframe(jsonIframe, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{
					InteractiveWebView webView = new InteractiveWebView(getContext());
					webView.setDisplay(Global.ScaleSize(jsonHeader.mnX), Global.ScaleSize(jsonHeader.mnY),
							Global.ScaleSize(jsonHeader.mnWidth), Global.ScaleSize(jsonHeader.mnHeight));
					webView.loadUrl(jsonBody.mstrUrl);
					container.addView(webView);
				}
			}
			jsonHeader = null;
			jsonBody = null;
			jsonIframe = null;
		}
		return true;
	}

}

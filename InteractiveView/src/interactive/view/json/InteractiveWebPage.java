package interactive.view.json;

import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.ViewGroup;

public class InteractiveWebPage extends InteractiveObject
{

	public InteractiveWebPage(Context context)
	{
		super(context);
	}

	@Override
	public boolean createInteractive(ViewGroup container, String strBookPath, JSONObject jsonAll, int nChapter,
			int nPage) throws JSONException
	{
		String strKey = null;
		if (!isCreateValid(container, strBookPath, jsonAll, JSON_WEB_PAGE))
		{
			return false;
		}

		strKey = getValidKey(jsonAll, JSON_WEB_PAGE);
		if (null == strKey)
		{
			return false;
		}

		JSONArray jsonArrayWebPage = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayWebPage.length(); ++i)
		{
			JSONObject jsonWebPage = jsonArrayWebPage.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonWebPage jsonBody = new JsonWebPage();
			if (parseJsonHeader(jsonWebPage, jsonHeader) && parseJsonWebPage(jsonWebPage, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{
					((InteractiveWebView) container).setAutoPlay(jsonBody.options.mbAutoplay);
				}
			}
			jsonHeader = null;
			jsonBody = null;
			jsonWebPage = null;
		}

		return true;
	}

}

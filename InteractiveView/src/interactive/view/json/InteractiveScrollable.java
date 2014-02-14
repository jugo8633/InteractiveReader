package interactive.view.json;

import interactive.view.scrollable.ScrollableView;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class InteractiveScrollable extends InteractiveObject
{

	public InteractiveScrollable(Context context)
	{
		super(context);
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	@Override
	public boolean createInteractive(InteractiveWebView webView, String strBookPath, JSONObject jsonAll)
			throws JSONException
	{
		if (!isCreateValid(webView, strBookPath, jsonAll, JSON_SCROLLABLE))
		{
			return false;
		}

		JSONArray jsonArrayScrollable = jsonAll.getJSONArray(JSON_SCROLLABLE);
		for (int i = 0; i < jsonArrayScrollable.length(); ++i)
		{
			JSONObject jsonScrollable = jsonArrayScrollable.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonScrollable jsonBody = new JsonScrollable();
			if (parseJsonHeader(jsonScrollable, jsonHeader) && parseJsonScrollable(jsonScrollable, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{
					ScrollableView scrollableImageView = new ScrollableView(getContext());
					scrollableImageView.setPosition(webView.getChapter(), webView.getPage());
					scrollableImageView.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
							ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight));
					scrollableImageView.setImage(jsonHeader.mstrName, strBookPath + jsonHeader.mstrSrc,
							ScaleSize(jsonBody.imgBBox.mnWidth), ScaleSize(jsonBody.imgBBox.mnHeight),
							jsonBody.mnOverflow, ScaleSize(jsonBody.offSet.mnX), ScaleSize(jsonBody.offSet.mnY),
							webView);
					scrollableImageView.initNotifyHandler(webView.getWebHandler());
				}
			}
			jsonBody = null;
			jsonHeader = null;
			jsonScrollable = null;
		}
		return false;
	}
}

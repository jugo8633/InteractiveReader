package interactive.view.json;

import interactive.common.Device;
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
		Device device = new Device(getContext());
		int nScaleSize = device.getScaleSize();
		device = null;

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
					scrollableImageView.setDisplay(jsonHeader.mnX * nScaleSize, jsonHeader.mnY * nScaleSize,
							jsonHeader.mnWidth * nScaleSize, jsonHeader.mnHeight * nScaleSize);
					scrollableImageView.setImage(strBookPath + jsonHeader.mstrSrc, jsonBody.imgBBox.mnWidth
							* nScaleSize, jsonBody.imgBBox.mnHeight * nScaleSize, jsonBody.mnOverflow,
							jsonBody.offSet.mnX * nScaleSize, jsonBody.offSet.mnY * nScaleSize);
					scrollableImageView.initNotifyHandler(webView.getWebHandler());
					webView.addView(scrollableImageView);
				}
			}
			jsonBody = null;
			jsonHeader = null;
			jsonScrollable = null;
		}
		return false;
	}
}

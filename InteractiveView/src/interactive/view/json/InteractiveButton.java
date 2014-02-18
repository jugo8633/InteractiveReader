package interactive.view.json;

import interactive.view.button.ButtonView;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveImageData;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;

public class InteractiveButton extends InteractiveObject
{
	public InteractiveButton(Context context)
	{
		super(context);
	}

	@Override
	public boolean createInteractive(InteractiveWebView webView, String strBookPath, JSONObject jsonAll)
			throws JSONException
	{
		String strKey = null;
		if (!isCreateValid(webView, strBookPath, jsonAll, JSON_BUTTON))
		{
			return false;
		}

		/** get image */
		strKey = getValidKey(jsonAll, JSON_IMAGE);
		SparseArray<InteractiveImageData> listImageData = null;
		if (null != strKey)
		{
			listImageData = new SparseArray<InteractiveImageData>();
			if (getInteractiveImage(strBookPath, jsonAll, listImageData))
			{
				if (0 >= listImageData.size())
				{
					listImageData = null;
				}
			}
		}

		strKey = getValidKey(jsonAll, JSON_BUTTON);
		if (null == strKey)
		{
			return false;
		}
		JSONArray jsonArrayButton = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayButton.length(); ++i)
		{
			final JSONObject jsonButton = jsonArrayButton.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonButton jsonBody = new JsonButton();
			if (parseJsonHeader(jsonButton, jsonHeader) && parseJsonButton(jsonButton, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{
					ButtonView acButton = new ButtonView(getContext());
					acButton.setTag(jsonHeader.mstrName);
					acButton.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
							ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight));
					acButton.setPosition(webView.getChapter(), webView.getPage());
					acButton.setGroupId(jsonHeader.mstrGroupId);
					acButton.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
					acButton.setImageSrc(strBookPath + jsonHeader.mstrSrc, strBookPath + jsonBody.mstrTouchDown,
							strBookPath + jsonBody.mstrTouchUp, ScaleSize(jsonHeader.mnWidth),
							ScaleSize(jsonHeader.mnHeight));
					for (int j = 0; j < jsonBody.listEvent.size(); ++j)
					{
						Event event = jsonBody.listEvent.get(j);

						acButton.setButtonClickType(event.mnType);

						acButton.addEvent(event.mnType, event.mstrTypeName, event.mnEvent, event.mstrEventName,
								event.mnTargetType, event.mstrTargetId, event.mnDisplay);
						event = null;
					}
					webView.addView(acButton);
					acButton = null;
				}
			}
			jsonHeader = null;
			jsonBody = null;
		}

		if (null != listImageData)
		{
			listImageData.clear();
			listImageData = null;
		}

		return true;
	}

	private boolean getInteractiveImage(String strBookPath, JSONObject jsonAll,
			SparseArray<InteractiveImageData> listImageData) throws JSONException
	{
		boolean bResult = false;
		InteractiveImage interactiveImage = new InteractiveImage(getContext());
		bResult = interactiveImage.getInteractiveImage(strBookPath, jsonAll, listImageData);
		interactiveImage = null;
		return bResult;
	}
}

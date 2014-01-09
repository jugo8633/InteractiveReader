package interactive.view.json;

import interactive.view.button.ButtonView;
import interactive.view.global.Global;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

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
					acButton.setImageSrc(strBookPath + jsonHeader.mstrSrc, strBookPath + jsonBody.mstrTouchDown,
							strBookPath + jsonBody.mstrTouchUp);
					acButton.setDisplay(getScaleUnit(jsonHeader.mnX), getScaleUnit(jsonHeader.mnY),
							getScaleUnit(jsonHeader.mnWidth), getScaleUnit(jsonHeader.mnHeight));
					acButton.setGroupId(jsonHeader.mstrGroupId);
					acButton.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
					for (int j = 0; j < jsonBody.listEvent.size(); ++j)
					{
						Event event = jsonBody.listEvent.get(j);
						acButton.addEvent(event.mnType, event.mstrTypeName, event.mnEvent, event.mstrEventName,
								event.mnTargetType, event.mstrTargetId, event.mnDisplay);
						webView.setItemHide(event.mstrTargetId, jsonHeader.mstrGroupId);
						event = null;
					}
					webView.addView(acButton);
					acButton = null;
				}
			}
			jsonHeader = null;
			jsonBody = null;
		}
		return true;
	}
}

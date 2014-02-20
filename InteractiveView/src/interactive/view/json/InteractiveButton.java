package interactive.view.json;

import interactive.view.button.ButtonView;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveDefine;
import interactive.view.handler.InteractiveGoogleMapData;
import interactive.view.handler.InteractiveImageData;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;

public class InteractiveButton extends InteractiveObject
{
	private String	mstrBackground	= null;

	public InteractiveButton(Context context)
	{
		super(context);
	}

	public void setBackground(String strBackground)
	{
		mstrBackground = strBackground;
	}

	@Override
	public boolean createInteractive(InteractiveWebView webView, String strBookPath, JSONObject jsonAll, int nChapter,
			int nPage) throws JSONException
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

		/** get google map */
		strKey = getValidKey(jsonAll, JSON_MAP);
		SparseArray<InteractiveGoogleMapData> listMapData = null;
		if (null != strKey)
		{
			listMapData = new SparseArray<InteractiveGoogleMapData>();
			if (getInteractiveMap(jsonAll, listMapData, nChapter, nPage))
			{
				if (0 >= listMapData.size())
				{
					listMapData = null;
				}
			}
		}

		/** start button init */
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
					acButton.setPosition(nChapter, nPage);
					acButton.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
					acButton.setImageSrc(strBookPath + jsonHeader.mstrSrc, strBookPath + jsonBody.mstrTouchDown,
							strBookPath + jsonBody.mstrTouchUp, ScaleSize(jsonHeader.mnWidth),
							ScaleSize(jsonHeader.mnHeight));

					Global.interactiveHandler.addButton(getContext(), jsonHeader.mstrName, jsonHeader.mstrGroupId,
							webView, acButton.getButtonHandler());

					for (int j = 0; j < jsonBody.listEvent.size(); ++j)
					{
						Event event = jsonBody.listEvent.get(j);
						acButton.setButtonClickType(event.mnType);
						Global.interactiveHandler.addButtonEvent(jsonHeader.mstrName, event.mnType, event.mstrTypeName,
								event.mnEvent, event.mstrEventName, event.mnTargetType, event.mstrTargetId,
								event.mnDisplay);
						if (InteractiveDefine.BUTTON_EVENT_SHOW_ITEM == event.mnEvent
								&& InteractiveDefine.OBJECT_CATEGORY_IMAGE == event.mnTargetType
								&& null != listImageData)
						{
							addButtonImage(listImageData, jsonHeader.mstrName, event.mstrTargetId);
						}

						if (InteractiveDefine.BUTTON_EVENT_SHOW_ITEM == event.mnEvent
								&& InteractiveDefine.OBJECT_CATEGORY_MAP == event.mnTargetType && null != listMapData)
						{
							addButtonMap(listMapData, jsonHeader.mstrName, event.mstrTargetId);
						}
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

		if (null != listMapData)
		{
			listMapData.clear();
			listMapData = null;
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

	private boolean getInteractiveMap(JSONObject jsonAll, SparseArray<InteractiveGoogleMapData> listMapData,
			int nChapter, int nPage) throws JSONException
	{
		boolean bResult = false;
		InteractiveMap interactiveMap = new InteractiveMap(getContext());
		interactiveMap.setBackground(mstrBackground);
		bResult = interactiveMap.getInteractiveGoogleMap(jsonAll, listMapData, nChapter, nPage);
		interactiveMap = null;
		return bResult;
	}

	private void addButtonImage(SparseArray<InteractiveImageData> listImageData, String strButtonTag, String strImageTag)
	{
		if (null == listImageData || null == strButtonTag || null == strImageTag)
		{
			return;
		}

		for (int i = 0; i < listImageData.size(); ++i)
		{
			if (listImageData.get(i).mstrName.equals(strImageTag))
			{
				Global.interactiveHandler.addButtonImage(strButtonTag, strImageTag, listImageData.get(i).mnWidth,
						listImageData.get(i).mnHeight, listImageData.get(i).mnX, listImageData.get(i).mnY,
						listImageData.get(i).mstrSrc, listImageData.get(i).mstrGroupId,
						listImageData.get(i).mbIsVisible);
			}
		}
	}

	private void addButtonMap(SparseArray<InteractiveGoogleMapData> listMapData, String strButtonTag, String strMapTag)
	{
		if (null == listMapData || null == strButtonTag || null == strMapTag)
		{
			return;
		}

		for (int i = 0; i < listMapData.size(); ++i)
		{
			if (listMapData.get(i).mstrTag.equals(strMapTag))
			{
				Global.interactiveHandler.addButtonMap(strButtonTag, strMapTag, listMapData.get(i).mnMapType,
						listMapData.get(i).mdLatitude, listMapData.get(i).mdLongitude, listMapData.get(i).mnZoomLevel,
						listMapData.get(i).mstrMarker, listMapData.get(i).mnX, listMapData.get(i).mnY,
						listMapData.get(i).mnWidth, listMapData.get(i).mnHeight,
						listMapData.get(i).mstrBackgroundImage, listMapData.get(i).mbIsVisible);
			}
		}
	}
}

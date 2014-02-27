package interactive.view.json;

import interactive.view.define.InteractiveDefine;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveMediaData;
import interactive.view.handler.InteractiveMediaLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

public class InteractiveVideo extends InteractiveObject
{
	public InteractiveVideo(Context context)
	{
		super(context);
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	@Override
	public boolean createInteractive(ViewGroup container, String strBookPath, JSONObject jsonAll, int nChapter,
			int nPage) throws JSONException
	{
		if (!isCreateValid(container, strBookPath, jsonAll, JSON_VIDEO))
		{
			return false;
		}

		String strKey = getValidKey(jsonAll, JSON_VIDEO);
		if (null == strKey)
		{
			return false;
		}

		JSONArray jsonArrayVideo = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayVideo.length(); ++i)
		{
			JSONObject jsonObjVideo = jsonArrayVideo.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonVideo jsonBody = new JsonVideo();
			if (parseJsonHeader(jsonObjVideo, jsonHeader) && parseJsonVideo(jsonObjVideo, jsonBody))
			{
				String strMediaSrc = jsonBody.mstrMediaSrc;
				if (InteractiveDefine.MEDIA_TYPE_LOCAL == jsonBody.mnMediaType)
				{
					strMediaSrc = strBookPath + jsonBody.mstrMediaSrc;
				}
				ViewGroup viewContainer = container;
				if (jsonHeader.mbIsVisible)
				{
					viewContainer = createMediaLayout(jsonHeader, jsonBody, container, strBookPath, nChapter, nPage);
				}
				Global.interactiveHandler.addMediaData(jsonHeader.mstrName, ScaleSize(jsonHeader.mnWidth),
						ScaleSize(jsonHeader.mnHeight), ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
						strBookPath + jsonHeader.mstrSrc, jsonBody.mnMediaType, strMediaSrc, jsonBody.options.mnStart,
						jsonBody.options.mnEnd, jsonBody.options.mbAutoPlay, jsonBody.options.mbLoop,
						jsonBody.appearance.mbPlayerControls, jsonHeader.mbIsVisible, viewContainer, false);

			}
			jsonBody = null;
			jsonHeader = null;
		}
		return false;
	}

	private InteractiveMediaLayout createMediaLayout(JsonHeader jsonHeader, JsonVideo jsonBody, ViewGroup viewParent,
			String strBookPath, int nChapter, int nPage)
	{
		InteractiveMediaLayout mediaLayout = new InteractiveMediaLayout(getContext());
		mediaLayout.setMediaTag(jsonHeader.mstrName);
		mediaLayout.setPosition(nChapter, nPage);
		mediaLayout.setAutoplay(jsonBody.options.mbAutoPlay);
		mediaLayout.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY), ScaleSize(jsonHeader.mnWidth),
				ScaleSize(jsonHeader.mnHeight));
		mediaLayout.setBackground(strBookPath + jsonHeader.mstrSrc, ScaleSize(jsonHeader.mnWidth),
				ScaleSize(jsonHeader.mnHeight));
		mediaLayout.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
		viewParent.addView(mediaLayout);
		return mediaLayout;
	}

	public boolean getInteractiveVideo(String strBookPath, JSONObject jsonAll,
			SparseArray<InteractiveMediaData> listMediaData, ViewGroup viewContainer) throws JSONException
	{
		if (null == listMediaData || null == strBookPath || null == jsonAll)
		{
			return false;
		}
		String strKey = getValidKey(jsonAll, JSON_VIDEO);
		if (null == strKey)
		{
			return false;
		}
		JSONArray jsonArrayVideo = jsonAll.getJSONArray(strKey);
		String strMediaSrc = null;
		for (int i = 0; i < jsonArrayVideo.length(); ++i)
		{
			JSONObject jsonObjVideo = jsonArrayVideo.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonVideo jsonBody = new JsonVideo();
			if (parseJsonHeader(jsonObjVideo, jsonHeader) && parseJsonVideo(jsonObjVideo, jsonBody))
			{
				switch (jsonBody.mnMediaType)
				{
				case InteractiveDefine.MEDIA_TYPE_LOCAL:
					strMediaSrc = strBookPath + jsonBody.mstrMediaSrc;
					break;
				case InteractiveDefine.MEDIA_TYPE_YOUTUBE:
					strMediaSrc = jsonBody.mstrMediaSrc;
					break;
				case InteractiveDefine.MEDIA_TYPE_URL:
					strMediaSrc = jsonBody.mstrMediaSrc;
					break;
				default:
					strMediaSrc = jsonBody.mstrMediaSrc;
					break;
				}

				listMediaData.put(listMediaData.size(), new InteractiveMediaData(jsonHeader.mstrName,
						ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight), ScaleSize(jsonHeader.mnX),
						ScaleSize(jsonHeader.mnY), strBookPath + jsonHeader.mstrSrc, jsonBody.mnMediaType, strMediaSrc,
						jsonBody.options.mnStart, jsonBody.options.mnEnd, jsonBody.options.mbAutoPlay,
						jsonBody.options.mbLoop, jsonBody.appearance.mbPlayerControls, jsonHeader.mbIsVisible,
						viewContainer, false));
			}
		}
		return true;
	}
}

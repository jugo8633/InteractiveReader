package interactive.view.json;

import interactive.common.Logs;
import interactive.view.define.InteractiveDefine;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveMediaData;
import interactive.view.handler.InteractiveMediaLayout;
import interactive.view.video.VideoPlayer;
import interactive.view.webview.InteractiveWebView;

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
	public boolean createInteractive(InteractiveWebView webView, String strBookPath, JSONObject jsonAll, int nChapter,
			int nPage) throws JSONException
	{
		if (!isCreateValid(webView, strBookPath, jsonAll, JSON_VIDEO))
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
				Global.interactiveHandler.addMediaData(jsonHeader.mstrName, ScaleSize(jsonHeader.mnWidth),
						ScaleSize(jsonHeader.mnHeight), ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
						strBookPath + jsonHeader.mstrSrc, jsonBody.mnMediaType, jsonBody.mstrMediaSrc,
						jsonBody.options.mnStart, jsonBody.options.mnEnd, jsonBody.options.mbAutoPlay,
						jsonBody.options.mbLoop, jsonBody.appearance.mbPlayerControls, jsonHeader.mbIsVisible, webView,
						false);

				if (jsonHeader.mbIsVisible)
				{
					switch (jsonBody.mnMediaType)
					{
					case InteractiveDefine.MEDIA_TYPE_LOCAL:
						playLocalVideo(jsonHeader, jsonBody, webView, strBookPath, nChapter, nPage);
						break;
					case InteractiveDefine.MEDIA_TYPE_YOUTUBE:
						playYoutubeVideo(jsonHeader, jsonBody, webView, strBookPath);
						break;
					case InteractiveDefine.MEDIA_TYPE_URL:
						playUrlVideo(jsonHeader, jsonBody, webView, strBookPath, nChapter, nPage);
						break;
					default:
						Logs.showTrace("Unknow video type?");
						break;
					}
				}
			}
			jsonBody = null;
			jsonHeader = null;
		}
		return false;
	}

	private void playLocalVideo(JsonHeader jsonHeader, JsonVideo jsonBody, InteractiveWebView webView,
			String strBookPath, int nChapter, int nPage)
	{
		VideoPlayer player = new VideoPlayer(getContext());
		player.setTag(jsonHeader.mstrName);
		player.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY), ScaleSize(jsonHeader.mnWidth),
				ScaleSize(jsonHeader.mnHeight));
		player.setVideo(strBookPath + jsonBody.mstrMediaSrc);
		player.setLoop(jsonBody.options.mbLoop);
		player.setAutoplay(jsonBody.options.mbAutoPlay);
		player.showController(jsonBody.appearance.mbPlayerControls);
		player.setPosition(nChapter, nPage);
		webView.addView(player);
		if (jsonBody.options.mbAutoPlay)
		{
			player.play();
		}
		player = null;
	}

	private void playYoutubeVideo(JsonHeader jsonHeader, JsonVideo jsonBody, ViewGroup viewParent, String strBookPath)
	{
		InteractiveMediaLayout youtubeLayout = new InteractiveMediaLayout(getContext());
		youtubeLayout.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY), ScaleSize(jsonHeader.mnWidth),
				ScaleSize(jsonHeader.mnHeight));
		youtubeLayout.setBackground(strBookPath + jsonHeader.mstrSrc, ScaleSize(jsonHeader.mnWidth),
				ScaleSize(jsonHeader.mnHeight));
		youtubeLayout.setTag(jsonHeader.mstrName);
		youtubeLayout.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
		Global.interactiveHandler.addMediaData(jsonHeader.mstrName, ScaleSize(jsonHeader.mnWidth),
				ScaleSize(jsonHeader.mnHeight), ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY), strBookPath
						+ jsonHeader.mstrSrc, jsonBody.mnMediaType, jsonBody.mstrMediaSrc, jsonBody.options.mnStart,
				jsonBody.options.mnEnd, jsonBody.options.mbAutoPlay, jsonBody.options.mbLoop,
				jsonBody.appearance.mbPlayerControls, jsonHeader.mbIsVisible, viewParent, false);

		viewParent.addView(youtubeLayout);
	}

	private void playUrlVideo(JsonHeader jsonHeader, JsonVideo jsonBody, ViewGroup viewParent, String strBookPath,
			int nChapter, int nPage)
	{
		VideoPlayer player = new VideoPlayer(getContext());
		player.setTag(jsonHeader.mstrName);
		player.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY), ScaleSize(jsonHeader.mnWidth),
				ScaleSize(jsonHeader.mnHeight));
		player.setVideo(jsonBody.mstrUrl);
		player.setLoop(jsonBody.options.mbLoop);
		player.showController(jsonBody.appearance.mbPlayerControls);
		player.setPosition(nChapter, nPage);
		viewParent.addView(player);
		if (jsonBody.options.mbAutoPlay)
		{
			player.play();
		}
		player = null;
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

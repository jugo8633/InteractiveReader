package interactive.view.json;

import interactive.common.Device;
import interactive.common.Logs;
import interactive.view.global.Global;
import interactive.view.video.VideoPlayer;
import interactive.view.webview.InteractiveWebView;
import interactive.view.youtube.YoutubeFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;

public class InteractiveVideo extends InteractiveObject
{

	private int	mnScaleSize	= 1;

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
	public boolean createInteractive(InteractiveWebView webView, String strBookPath, JSONObject jsonAll)
			throws JSONException
	{
		if (!isCreateValid(webView, strBookPath, jsonAll, JSON_VIDEO))
		{
			return false;
		}

		Device device = new Device(getContext());
		mnScaleSize = device.getScaleSize();
		device = null;

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
				if (jsonHeader.mbIsVisible)
				{
					switch (jsonBody.mnVideoType)
					{
					case InteractiveEvent.VIDEO_TYPE_LOCAL:
						playLocalVideo(jsonHeader, jsonBody, webView, strBookPath);
						break;
					case InteractiveEvent.VIDEO_TYPE_TOUTUBE:
						playYoutubeVideo(jsonHeader, jsonBody, webView, strBookPath);
						break;
					case InteractiveEvent.VIDEO_TYPE_URL:
						playUrlVideo(jsonHeader, jsonBody, webView, strBookPath);
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
			String strBookPath)
	{
		VideoPlayer player = new VideoPlayer(getContext());
		player.setTag(jsonHeader.mstrName);
		player.setDisplay(jsonHeader.mnX * mnScaleSize, jsonHeader.mnY * mnScaleSize, jsonHeader.mnWidth * mnScaleSize,
				jsonHeader.mnHeight * mnScaleSize);
		player.setVideo(strBookPath + jsonBody.mstrVideoSrc);
		player.setLoop(jsonBody.options.mbLoop);
		player.showController(jsonBody.appearance.mbPlayerControls);
		webView.addView(player);
		if (jsonBody.options.mbAutoPlay)
		{
			webView.addObjectHandle(player.getHandler(), InteractiveEvent.OBJECT_CATEGORY_VIDEO, true);
			player.play();
		}
		player = null;
	}

	private void playYoutubeVideo(JsonHeader jsonHeader, JsonVideo jsonBody, InteractiveWebView webView,
			String strBookPath)
	{
		YoutubeFrameLayout youtubeLayout = new YoutubeFrameLayout(getContext());
		youtubeLayout.setDisplay(jsonHeader.mnX * mnScaleSize, jsonHeader.mnY * mnScaleSize, jsonHeader.mnWidth
				* mnScaleSize, jsonHeader.mnHeight * mnScaleSize);
		youtubeLayout.setBackground(strBookPath + jsonHeader.mstrSrc);
		youtubeLayout.setTag(jsonHeader.mstrName);
		youtubeLayout.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
		Global.interactiveHandler.addYoutubeVideo(youtubeLayout, jsonHeader.mstrName, false, jsonBody.mstrVideoSrc,
				jsonBody.options.mbLoop, jsonBody.appearance.mbPlayerControls);
		webView.addView(youtubeLayout);
	}

	private void playUrlVideo(JsonHeader jsonHeader, JsonVideo jsonBody, InteractiveWebView webView, String strBookPath)
	{
		VideoPlayer player = new VideoPlayer(getContext());
		player.setTag(jsonHeader.mstrName);
		player.setDisplay(jsonHeader.mnX * mnScaleSize, jsonHeader.mnY * mnScaleSize, jsonHeader.mnWidth * mnScaleSize,
				jsonHeader.mnHeight * mnScaleSize);
		player.setVideo(jsonBody.mstrUrl);
		player.setLoop(jsonBody.options.mbLoop);
		player.showController(jsonBody.appearance.mbPlayerControls);
		webView.addView(player);
		if (jsonBody.options.mbAutoPlay)
		{
			webView.addObjectHandle(player.getHandler(), InteractiveEvent.OBJECT_CATEGORY_VIDEO, true);
			player.play();
		}
		player = null;
	}

	public boolean getInteractiveVideo(String strBookPath, JSONObject jsonAll,
			SparseArray<InteractiveVideoData> listVideoData) throws JSONException
	{
		if (null == listVideoData || null == strBookPath || null == jsonAll)
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
				switch (jsonBody.mnVideoType)
				{
				case InteractiveEvent.VIDEO_TYPE_LOCAL:
					strMediaSrc = strBookPath + jsonBody.mstrVideoSrc;
					break;
				case InteractiveEvent.VIDEO_TYPE_TOUTUBE:
					strMediaSrc = jsonBody.mstrVideoSrc;
					break;
				case InteractiveEvent.VIDEO_TYPE_URL:
					strMediaSrc = jsonBody.mstrVideoSrc;
					break;
				default:
					strMediaSrc = jsonBody.mstrVideoSrc;
					break;
				}

				listVideoData.put(listVideoData.size(), new InteractiveVideoData(jsonHeader.mstrName,
						getScaleUnit(jsonHeader.mnWidth), getScaleUnit(jsonHeader.mnHeight),
						getScaleUnit(jsonHeader.mnX), getScaleUnit(jsonHeader.mnY), strBookPath + jsonHeader.mstrSrc,
						jsonBody.mnVideoType, strMediaSrc, jsonBody.options.mnStart, jsonBody.options.mnEnd,
						jsonBody.options.mbAutoPlay, jsonBody.options.mbLoop, jsonBody.appearance.mbPlayerControls));
			}
		}
		return true;
	}
}
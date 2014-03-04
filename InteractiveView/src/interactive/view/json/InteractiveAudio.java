package interactive.view.json;

import interactive.view.audio.AudioPlayer;
import interactive.view.global.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.ViewGroup;

public class InteractiveAudio extends InteractiveObject
{

	public InteractiveAudio(Context context)
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
		if (!isCreateValid(container, strBookPath, jsonAll, JSON_AUDIO))
		{
			return false;
		}

		String strKey = getValidKey(jsonAll, JSON_AUDIO);
		if (null == strKey)
		{
			return false;
		}

		JSONArray jsonArrayAudio = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayAudio.length(); ++i)
		{
			JSONObject jsonObjAudio = jsonArrayAudio.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonAudio jsonBody = new JsonAudio();
			if (parseJsonHeader(jsonObjAudio, jsonHeader) && parseJsonAudio(jsonObjAudio, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{
					AudioPlayer audioPlayer = new AudioPlayer(getContext());
					audioPlayer.setTag(jsonHeader.mstrName);
					audioPlayer.setDisplay(Global.ScaleSize(jsonHeader.mnX), Global.ScaleSize(jsonHeader.mnY),
							Global.ScaleSize(jsonHeader.mnWidth), Global.ScaleSize(jsonHeader.mnHeight));
					audioPlayer.setPosition(nChapter, nPage);
					audioPlayer.setAudioFile(strBookPath + jsonBody.mstrMediaSrc);
					if (null != jsonHeader.mstrSrc && 0 < jsonHeader.mstrSrc.length())
					{
						audioPlayer.setImage(strBookPath + jsonHeader.mstrSrc, Global.ScaleSize(jsonHeader.mnWidth),
								Global.ScaleSize(jsonHeader.mnHeight));
					}
					audioPlayer.setOption(jsonBody.options.mbAutoPlay, jsonBody.options.mbLoop,
							jsonBody.options.mnStart, jsonBody.options.mnEnd);
					audioPlayer.setPlayerControls(jsonBody.appearance.mbPlayerControls);
					container.addView(audioPlayer);
				}
			}
		}
		return true;
	}

}

package interactive.view.json;

import interactive.view.doodle.DoodleView;
import interactive.view.json.InteractiveObject.JsonHeader;
import interactive.view.json.InteractiveObject.JsonPostcard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.ViewGroup;

public class InteractiveDoodle extends InteractiveObject
{

	public InteractiveDoodle(Context context)
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
		if (!isCreateValid(container, strBookPath, jsonAll, JSON_DOODLE))
		{
			return false;
		}

		JSONArray jsonArrayDoodle = jsonAll.getJSONArray(JSON_DOODLE);

		for (int i = 0; i < jsonArrayDoodle.length(); ++i)
		{
			JSONObject jsonDoodle = jsonArrayDoodle.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonDoodle jsonBody = new JsonDoodle();
			if (parseJsonHeader(jsonDoodle, jsonHeader) && parseJsonDoodle(jsonDoodle, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{
					DoodleView doodleView = new DoodleView(getContext(), container);
					doodleView.SetDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
							ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight));
					doodleView.setPosition(nChapter, nPage);
					doodleView.setImageSrc(strBookPath + jsonHeader.mstrSrc, ScaleSize(jsonHeader.mnWidth),
							ScaleSize(jsonHeader.mnHeight));
					container.addView(doodleView);
					doodleView = null;
				}
			}
			jsonDoodle = null;
			jsonHeader = null;
			jsonBody = null;
		}
		return false;
	}

}

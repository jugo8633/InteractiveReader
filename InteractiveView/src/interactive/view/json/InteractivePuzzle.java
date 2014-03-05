package interactive.view.json;

import interactive.view.json.InteractiveObject.JsonHeader;
import interactive.view.json.InteractiveObject.JsonPostcard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.ViewGroup;

public class InteractivePuzzle extends InteractiveObject
{

	public InteractivePuzzle(Context context)
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
		if (!isCreateValid(container, strBookPath, jsonAll, JSON_PUZZLE))
		{
			return false;
		}

		JSONArray jsonArrayPuzzle = jsonAll.getJSONArray(JSON_PUZZLE);
		for (int i = 0; i < jsonArrayPuzzle.length(); ++i)
		{
			JSONObject jsonPuzzle = jsonArrayPuzzle.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonPuzzle jsonBody = new JsonPuzzle();
			if (parseJsonHeader(jsonPuzzle, jsonHeader) && parseJsonPuzzle(jsonPuzzle, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{

				}
			}
		}
		return false;
	}

}

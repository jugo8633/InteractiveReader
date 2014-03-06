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
					doodleView.setBrushes(jsonBody.mnBrushes);
					doodleView.setPalette(jsonBody.mnPalette);
					if (null != jsonBody.eraserBtn)
					{
						doodleView.setEraserBtn(ScaleSize(jsonBody.eraserBtn.mnX), ScaleSize(jsonBody.eraserBtn.mnY),
								ScaleSize(jsonBody.eraserBtn.mnWidth), ScaleSize(jsonBody.eraserBtn.mnHeight),
								strBookPath + jsonBody.eraserBtn.mstrSrc);
					}
					if (null != jsonBody.paletteBtn)
					{
						doodleView.setPaletteBtn(ScaleSize(jsonBody.paletteBtn.mnX),
								ScaleSize(jsonBody.paletteBtn.mnY), ScaleSize(jsonBody.paletteBtn.mnWidth),
								ScaleSize(jsonBody.paletteBtn.mnHeight), strBookPath + jsonBody.paletteBtn.mstrSrc);
					}
					if (null != jsonBody.penBtn)
					{
						doodleView.setPenBtn(ScaleSize(jsonBody.penBtn.mnX), ScaleSize(jsonBody.penBtn.mnY),
								ScaleSize(jsonBody.penBtn.mnWidth), ScaleSize(jsonBody.penBtn.mnHeight), strBookPath
										+ jsonBody.penBtn.mstrSrc);
					}
					if (null != jsonBody.resetBtn)
					{
						doodleView.setResetBtn(ScaleSize(jsonBody.resetBtn.mnX), ScaleSize(jsonBody.resetBtn.mnY),
								ScaleSize(jsonBody.resetBtn.mnWidth), ScaleSize(jsonBody.resetBtn.mnHeight),
								strBookPath + jsonBody.resetBtn.mstrSrc);
					}
					if (null != jsonBody.saveBtn)
					{
						doodleView.setSaveBtn(ScaleSize(jsonBody.saveBtn.mnX), ScaleSize(jsonBody.saveBtn.mnY),
								ScaleSize(jsonBody.saveBtn.mnWidth), ScaleSize(jsonBody.saveBtn.mnHeight), strBookPath
										+ jsonBody.saveBtn.mstrSrc);
					}
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

package interactive.view.json;

import interactive.view.global.Global;
import interactive.view.image.InteractiveImageView;
import interactive.view.image.ScalableImageView;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;
import android.view.View;

public class InteractiveImage extends InteractiveObject
{
	public InteractiveImage(Context context)
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
		String strKey = null;

		if (!isCreateValid(webView, strBookPath, jsonAll, JSON_IMAGE))
		{
			return false;
		}

		strKey = getValidKey(jsonAll, JSON_IMAGE);
		if (null == strKey)
		{
			return false;
		}
		JSONArray jsonArrayImage = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayImage.length(); ++i)
		{
			JSONObject jsonImage = jsonArrayImage.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			if (parseJsonHeader(jsonImage, jsonHeader))
			{
				strKey = checkJsonGesture(jsonImage);
				if (null != strKey)
				{
					// image include gesture
					ScalableImageView imgView = new ScalableImageView(getContext());
					imgView.setTag(jsonHeader.mstrName);
					imgView.setImageURI(Uri.parse(strBookPath + jsonHeader.mstrSrc));
					imgView.setImageSize(ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight));
					imgView.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
							ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight));
					JSONArray jsonArrayGesture = jsonImage.getJSONArray(strKey);
					for (int j = 0; j < jsonArrayGesture.length(); ++j)
					{
						JsonGesture jsonGesture = new JsonGesture();
						JSONObject gesture = jsonArrayGesture.getJSONObject(j);
						if (parseJsonGesture(gesture, jsonGesture))
						{
							imgView.setImageScaleMode(jsonGesture.mnType, jsonGesture.mnEvent, jsonGesture.mnDisplay);
						}
						jsonGesture = null;
					}
					webView.addView(imgView);
					if (!jsonHeader.mbIsVisible)
					{
						imgView.setVisibility(View.GONE);
					}
					imgView = null;
				}
				else
				{
					if (!jsonHeader.mbIsVisible)
					{
						Global.interactiveHandler.addInteractiveImage(webView, jsonHeader.mstrName,
								(strBookPath + jsonHeader.mstrSrc), ScaleSize(jsonHeader.mnX),
								ScaleSize(jsonHeader.mnY), ScaleSize(jsonHeader.mnWidth),
								ScaleSize(jsonHeader.mnHeight), jsonHeader.mstrGroupId);

					}
					else
					{
						InteractiveImageView imgView = new InteractiveImageView(getContext());
						imgView.setTag(jsonHeader.mstrName);
						imgView.setImageURI(Uri.parse(strBookPath + jsonHeader.mstrSrc));
						imgView.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
								ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight));
						imgView.setGroupId(jsonHeader.mstrGroupId);
						webView.addView(imgView);
						imgView = null;
					}

				}
			}
			jsonHeader = null;
		}

		return true;
	}

	public boolean getInteractiveImage(String strBookPath, JSONObject jsonAll,
			SparseArray<InteractiveImageData> listImageData) throws JSONException
	{
		if (null == listImageData || null == strBookPath || null == jsonAll)
		{
			return false;
		}

		String strKey = getValidKey(jsonAll, JSON_IMAGE);
		if (null == strKey)
		{
			return false;
		}
		JSONArray jsonArrayImage = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayImage.length(); ++i)
		{
			JSONObject jsonImage = jsonArrayImage.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			if (parseJsonHeader(jsonImage, jsonHeader))
			{
				listImageData.put(listImageData.size(), new InteractiveImageData(jsonHeader.mstrName,
						ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight),
						ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY), strBookPath + jsonHeader.mstrSrc,
						jsonHeader.mstrGroupId));
			}
		}
		return true;
	}
}

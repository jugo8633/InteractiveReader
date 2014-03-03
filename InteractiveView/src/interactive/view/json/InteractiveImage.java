package interactive.view.json;

import interactive.common.BitmapHandler;
import interactive.view.define.InteractiveDefine;
import interactive.view.handler.InteractiveImageData;
import interactive.view.image.EventImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

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
	public boolean createInteractive(ViewGroup container, String strBookPath, JSONObject jsonAll, int nChapter,
			int nPage) throws JSONException
	{
		String strKey = null;

		if (!isCreateValid(container, strBookPath, jsonAll, JSON_IMAGE))
		{
			return false;
		}

		strKey = getValidKey(jsonAll, JSON_IMAGE);
		if (null == strKey)
		{
			return false;
		}

		/** get image */
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

		JSONArray jsonArrayImage = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayImage.length(); ++i)
		{
			JSONObject jsonImage = jsonArrayImage.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			if (parseJsonHeader(jsonImage, jsonHeader))
			{
				if (jsonHeader.mbIsVisible)
				{
					strKey = checkJsonGesture(jsonImage);
					if (null != strKey)
					{
						/** image include gesture , Event Image */
						EventImageView eventImage = new EventImageView(getContext());
						eventImage.setTag(jsonHeader.mstrName);
						eventImage.setPosition(nChapter, nPage);
						eventImage.setContainer(container);
						eventImage.setBitmap(strBookPath + jsonHeader.mstrSrc, ScaleSize(jsonHeader.mnWidth),
								ScaleSize(jsonHeader.mnHeight));
						eventImage.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
								ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight));

						JSONArray jsonArrayGesture = jsonImage.getJSONArray(strKey);
						for (int j = 0; j < jsonArrayGesture.length(); ++j)
						{
							JsonGesture jsonGesture = new JsonGesture();
							JSONObject gesture = jsonArrayGesture.getJSONObject(j);
							if (parseJsonGesture(gesture, jsonGesture))
							{
								eventImage.addGesture(jsonGesture.mnDisplay, jsonGesture.mnEvent,
										jsonGesture.mstrTargetId, jsonGesture.mnTargetType, jsonGesture.mnType);
								if (InteractiveDefine.IMAGE_EVENT_SHOW_ITEM == jsonGesture.mnEvent
										&& InteractiveDefine.OBJECT_CATEGORY_IMAGE == jsonGesture.mnTargetType
										&& null != listImageData)
								{
									InteractiveImageData imageData = getImageData(listImageData,
											jsonGesture.mstrTargetId);
									if (null != imageData)
									{
										eventImage.addEventTargetImage(imageData.mstrName, imageData.mstrSrc,
												imageData.mstrGroupId, imageData.mnX, imageData.mnY, imageData.mnWidth,
												imageData.mnHeight, imageData.mbIsVisible);
									}
								}
							}
							jsonGesture = null;
						}

						container.addView(eventImage);
						eventImage = null;
					}
					else
					{
						ImageView imgView = new ImageView(getContext());
						imgView.setTag(jsonHeader.mstrName);
						Bitmap bitmap = BitmapHandler.readBitmap(getContext(), strBookPath + jsonHeader.mstrSrc,
								ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight), false);
						imgView.setImageBitmap(bitmap);
						imgView.setX(ScaleSize(jsonHeader.mnX));
						imgView.setY(ScaleSize(jsonHeader.mnY));
						imgView.setLayoutParams(new LayoutParams(ScaleSize(jsonHeader.mnWidth),
								ScaleSize(jsonHeader.mnHeight)));
						container.addView(imgView);
						imgView = null;
					}
				}
			}
			jsonHeader = null;
		}

		if (null != listImageData)
		{
			listImageData.clear();
			listImageData = null;
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
						ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight), ScaleSize(jsonHeader.mnX),
						ScaleSize(jsonHeader.mnY), strBookPath + jsonHeader.mstrSrc, jsonHeader.mstrGroupId,
						jsonHeader.mbIsVisible));
			}
		}
		return true;
	}

	private InteractiveImageData getImageData(SparseArray<InteractiveImageData> listImageData, String strImageTag)
	{
		if (null == listImageData || null == strImageTag)
		{
			return null;
		}

		for (int i = 0; i < listImageData.size(); ++i)
		{
			if (listImageData.get(i).mstrName.equals(strImageTag))
			{
				return listImageData.get(i);
			}
		}
		return null;
	}
}

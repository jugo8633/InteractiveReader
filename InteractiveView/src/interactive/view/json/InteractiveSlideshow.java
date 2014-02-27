package interactive.view.json;

import interactive.common.Type;
import interactive.view.define.InteractiveDefine;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveImageData;
import interactive.view.handler.InteractiveMediaData;
import interactive.view.slideshow.SlideshowView;
import interactive.view.slideshow.SlideshowViewItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class InteractiveSlideshow extends InteractiveObject
{
	public InteractiveSlideshow(Context context)
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
		if (!isCreateValid(container, strBookPath, jsonAll, JSON_SLIDESHOW))
		{
			return false;
		}

		// get image data for slideshow
		InteractiveImage interactiveImage = new InteractiveImage(getContext());
		SparseArray<InteractiveImageData> listImageData = new SparseArray<InteractiveImageData>();
		interactiveImage.getInteractiveImage(strBookPath, jsonAll, listImageData);
		interactiveImage = null;

		// get video data for slideshow
		InteractiveVideo interactiveVideo = new InteractiveVideo(getContext());
		SparseArray<InteractiveMediaData> listMediaData = new SparseArray<InteractiveMediaData>();
		interactiveVideo.getInteractiveVideo(strBookPath, jsonAll, listMediaData, container);
		interactiveVideo = null;

		JSONArray jsonArraySlideshow = jsonAll.getJSONArray(JSON_SLIDESHOW);
		for (int i = 0; i < jsonArraySlideshow.length(); ++i)
		{
			JSONObject jsonSlideshow = jsonArraySlideshow.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonSlideshow jsonBody = new JsonSlideshow();
			if (parseJsonHeader(jsonSlideshow, jsonHeader) && parseJsonSlideshow(jsonSlideshow, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{
					SparseArray<SlideshowViewItem> listViewItem = new SparseArray<SlideshowViewItem>();
					SlideshowViewItem viewItem = null;
					int nItemType = Type.INVALID;
					for (int j = 0; j < jsonBody.listItem.size(); ++j)
					{
						Item item = jsonBody.listItem.get(j);

						viewItem = new SlideshowViewItem();

						switch (item.mnType)
						{
						case InteractiveDefine.OBJECT_CATEGORY_IMAGE:
							nItemType = SlideshowViewItem.TYPE_IMAGE;
							InteractiveImageData imageData = getImageData(listImageData, item.mstrTargetID);
							if (null != imageData)
							{
								viewItem.setSlideImage(imageData.mstrName, imageData.mstrSrc, imageData.mstrGroupId);
							}
							break;
						case InteractiveDefine.OBJECT_CATEGORY_VIDEO:
							nItemType = SlideshowViewItem.TYPE_VIDEO;
							InteractiveMediaData videoData = getVideoData(listMediaData, item.mstrTargetID);
							if (null != videoData)
							{
								viewItem.setSlideVideo(videoData.mstrName, videoData.mstrSrc, videoData.mstrMediaSrc,
										videoData.mnMediaType, videoData.mnStart, videoData.mnEnd,
										videoData.mbAutoplay, videoData.mbLoop, videoData.mbPlayerControls);
							}
							break;
						}
						viewItem.initViewItem(nItemType, item.mstrTypeName, item.mstrTitle, item.mstrDescription,
								item.mstrTargetID, strBookPath + item.mstrSourceImage);
						listViewItem.put(listViewItem.size(), viewItem);
					}

					SlideshowView slideshow = createSlideshow(jsonHeader.mstrName, ScaleSize(jsonHeader.mnWidth),
							ScaleSize(jsonHeader.mnHeight), ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
							jsonBody.mstrBackground, jsonBody.mnStyle, jsonBody.mbFullScreen, jsonBody.mnItemCount,
							listViewItem);
					slideshow.setPosition(nChapter, nPage);
					container.addView(slideshow);

					slideshow.showThumbnail();
					if (jsonBody.mbFullScreen)
					{
						RelativeLayout rlMain = slideshow.getScaleImageView();
						container.addView(rlMain);
					}
					listViewItem.clear();
					listViewItem = null;
				}
			}
			jsonHeader = null;
			jsonBody = null;
		}
		listMediaData.clear();
		listMediaData = null;
		listImageData.clear();
		listImageData = null;
		return true;
	}

	private SlideshowView createSlideshow(String strName, int nWidth, int nHeight, int nX, int nY,
			String strBackground, int nStyle, boolean bFullScreen, int nItemCount,
			SparseArray<SlideshowViewItem> listItem)
	{
		SlideshowView slideshowview = new SlideshowView(Global.theActivity);
		slideshowview.setFlingOnePage(true);
		slideshowview.setDisplay(nX, nY, nWidth, nHeight);
		slideshowview.setTag(strName);
		switch (nStyle)
		{
		case InteractiveDefine.SLIDESHOW_TYPE_NO_THUMBNAIL:
			slideshowview.setShowThumbnail(false);
			break;
		case InteractiveDefine.SLIDESHOW_TYPE_PAGE_CONTROL:
			slideshowview.setIndicatorShow(true);
			break;
		case InteractiveDefine.SLIDESHOW_TYPE_THUMBNAIL:
			slideshowview.setShowThumbnail(true);
			break;
		}
		slideshowview.setFullScreen(bFullScreen);
		slideshowview.setItem(listItem, nWidth, nHeight, false);
		return slideshowview;
	}

	private InteractiveImageData getImageData(SparseArray<InteractiveImageData> listImageData, String strTagName)
	{
		if (null == listImageData || null == strTagName || 0 >= listImageData.size())
		{
			return null;
		}

		for (int i = 0; i < listImageData.size(); ++i)
		{
			if (listImageData.get(i).mstrName.equals(strTagName))
			{
				return listImageData.get(i);
			}
		}
		return null;
	}

	private InteractiveMediaData getVideoData(SparseArray<InteractiveMediaData> listVideoData, String strTagName)
	{
		if (null == listVideoData || null == strTagName || 0 >= listVideoData.size())
		{
			return null;
		}

		for (int i = 0; i < listVideoData.size(); ++i)
		{
			if (listVideoData.get(i).mstrName.equals(strTagName))
			{
				return listVideoData.get(i);
			}
		}
		return null;
	}
}

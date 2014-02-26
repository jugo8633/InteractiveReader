package interactive.view.handler;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.view.define.InteractiveDefine;
import interactive.view.global.Global;
import interactive.view.map.GoogleMapActivity;
import interactive.view.youtube.YouTubePlayerActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class InteractiveButtonHandler
{
	private Runnable							runRemoveImage	= null;
	private SparseArray<InteractiveButtonData>	listButton		= null;
	private String								mstrButtonTag	= null;
	private String								mstrImageTag	= null;

	class Tags
	{
		public String	mstrButtonTag	= null;
		public String	mstrImageTag	= null;

		public Tags(String strButtonTag, String strImageTag)
		{
			mstrButtonTag = strButtonTag;
			mstrImageTag = strImageTag;
		}
	}

	public InteractiveButtonHandler()
	{
		super();
		listButton = new SparseArray<InteractiveButtonData>();

		runRemoveImage = new Runnable()
		{
			@Override
			public void run()
			{
				removeImage(mstrButtonTag, mstrImageTag);
			}
		};
	}

	@Override
	protected void finalize() throws Throwable
	{
		listButton.clear();
		listButton = null;
		super.finalize();
	}

	public void addButton(Context context, String strTag, String strGroupId, Handler handler)
	{
		listButton.put(listButton.size(), new InteractiveButtonData(context, strTag, strGroupId, handler));
	}

	public void addImageData(String strButtonTag, String strImageTag, int nWidth, int nHeight, int nX, int nY,
			String strSrc, String strGroupId, boolean bIsVisible)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null != buttonData)
		{
			buttonData.addImageData(strImageTag, nWidth, nHeight, nX, nY, strSrc, strGroupId, bIsVisible);
		}
	}

	public void addMapData(String strButtonTag, String strMapTag, int nMapType, double dLatitude, double dLongitude,
			int nZoomLevel, String strMarker, int nX, int nY, int nWidth, int nHeight, String strBackgroundImage,
			boolean bIsVisible)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null != buttonData)
		{
			buttonData.addMapData(strMapTag, nMapType, dLatitude, dLongitude, nZoomLevel, strMarker, nX, nY, nWidth,
					nHeight, strBackgroundImage, bIsVisible);
		}
	}

	public void addEventData(String strButtonTag, int nType, String strTypeName, int nEvent, String strEventName,
			int nTargetType, String strTargetID, int nDisplay)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null != buttonData)
		{
			buttonData.addEventData(nType, strTypeName, nEvent, strEventName, nTargetType, strTargetID, nDisplay);
		}
	}

	public void setContainer(String strButtonTag, ViewGroup viewGroup)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null != buttonData)
		{
			buttonData.setContainer(viewGroup);
		}
	}

	private InteractiveButtonData getButtonData(String strTag)
	{
		for (int i = 0; i < listButton.size(); ++i)
		{
			if (listButton.get(i).mstrTag.equals(strTag))
			{
				return listButton.get(i);
			}
		}
		return null;
	}

	synchronized public void handleButtonEvent(String strButtonTag)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null == buttonData)
		{
			Logs.showTrace("Button Data invalid!! tag=" + strButtonTag);
			return;
		}

		String strGroupId = getGroupId(buttonData);
		Logs.showTrace("Handle button event tag=" + strButtonTag + " group id=" + strGroupId);
		resetButton(strButtonTag, strGroupId);

		for (int i = 0; i < buttonData.listEventData.size(); ++i)
		{
			switch (buttonData.listEventData.get(i).mnEvent)
			{
			case InteractiveDefine.BUTTON_EVENT_DRAG:
				break;
			case InteractiveDefine.BUTTON_EVENT_SHOW_ITEM:
				showItem(buttonData, buttonData.listEventData.get(i).mnTargetType,
						buttonData.listEventData.get(i).mstrTargetID, buttonData.listEventData.get(i).mnDisplay);
				break;
			case InteractiveDefine.BUTTON_EVENT_VIDEO_PAUSE:
				break;
			case InteractiveDefine.BUTTON_EVENT_VIDEO_PLAY:
				playMedia(buttonData.listEventData.get(i).mstrTargetID);
				break;
			}
		}

	}

	private String getGroupId(InteractiveButtonData buttonData)
	{
		if (null != buttonData)
		{
			return buttonData.mstrGroupId;
		}
		return null;
	}

	synchronized private void resetButton(String strButtonTag, String strGroupId)
	{
		for (int i = 0; i < listButton.size(); ++i)
		{
			if (listButton.get(i).mstrGroupId.equals(strGroupId) && !listButton.get(i).mstrTag.equals(strButtonTag))
			{
				EventHandler.notify(listButton.get(i).mHandlerButton, EventMessage.MSG_RESET, 0, 0, null);
				if (null != listButton.get(i).listImageData)
				{
					for (int j = 0; j < listButton.get(i).listImageData.size(); ++j)
					{
						removeImage(listButton.get(i).mstrTag, listButton.get(i).listImageData.get(j).mstrName);
					}
				}
			}
		}
	}

	private void resetButton(String strButtonTag)
	{
		for (int i = 0; i < listButton.size(); ++i)
		{
			if (listButton.get(i).mstrTag.equals(strButtonTag))
			{
				EventHandler.notify(listButton.get(i).mHandlerButton, EventMessage.MSG_RESET, 0, 0, null);
			}
		}
	}

	public void initButton(String strButtonTag)
	{
		if (null == strButtonTag)
		{
			return;
		}

		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null == buttonData)
		{
			Logs.showTrace("Button Data invalid!! tag=" + strButtonTag);
			return;
		}

		if (null != buttonData.listImageData)
		{
			for (int i = 0; i < buttonData.listImageData.size(); ++i)
			{
				if (null != buttonData.listImageData.get(i).mImageView)
				{
					buttonData.getContainer().removeView(buttonData.listImageData.get(i).mImageView);
					if (null != buttonData.listImageData.get(i).mBitmapSrc)
					{
						if (!buttonData.listImageData.get(i).mBitmapSrc.isRecycled())
						{
							buttonData.listImageData.get(i).mBitmapSrc.recycle();
						}
						buttonData.listImageData.get(i).mBitmapSrc = null;
					}
					buttonData.listImageData.get(i).mImageView = null;
				}
			}
		}
	}

	private void showItem(InteractiveButtonData buttonData, int nTargetType, String strItemTag, int nDisplay)
	{
		Logs.showTrace("Button triggle show item target type=" + nTargetType);
		switch (nTargetType)
		{
		case InteractiveDefine.OBJECT_CATEGORY_IMAGE:
			showImage(buttonData, strItemTag);
			break;
		case InteractiveDefine.OBJECT_CATEGORY_MAP:
			showMap(buttonData, strItemTag);
			break;
		case InteractiveDefine.OBJECT_CATEGORY_VIDEO:
			showMedia(strItemTag, nDisplay);
			break;
		}
	}

	private void showImage(final InteractiveButtonData buttonData, String strImageTag)
	{
		if (null == buttonData.listImageData)
		{
			return;
		}
		Logs.showTrace("Button event show image tag=" + strImageTag);

		InteractiveImageData imageData = getImageData(buttonData, strImageTag);
		if (null != imageData && null == imageData.mImageView)
		{
			imageData.mBitmapSrc = BitmapHandler.readBitmap(buttonData.getContext(), imageData.mstrSrc,
					imageData.mnWidth, imageData.mnHeight);
			imageData.mImageView = new ImageView(buttonData.getContext());
			imageData.mImageView.setTag(imageData.mstrName);
			imageData.mImageView.setScaleType(ScaleType.CENTER_CROP);
			imageData.mImageView.setImageBitmap(imageData.mBitmapSrc);
			imageData.mImageView.setX(imageData.mnX);
			imageData.mImageView.setY(imageData.mnY);
			buttonData.getContainer().addView(imageData.mImageView);
			imageData.mImageView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					EventHandler.notify(selfHandler, EventMessage.MSG_IMAGE_CLICK, 0, 0, new Tags(buttonData.mstrTag,
							(String) view.getTag()));
					resetButton(buttonData.mstrTag);
				}
			});
		}
	}

	private void showMap(final InteractiveButtonData buttonData, String strMapTag)
	{
		if (null == buttonData.listMapData)
		{
			return;
		}
		Logs.showTrace("Button event show map tag=" + strMapTag);

		if (null == Global.theActivity)
		{
			Logs.showTrace("Invalid Activity");
			return;
		}
		InteractiveGoogleMapData googleMap = getMapData(buttonData, strMapTag);
		if (null != googleMap)
		{
			Intent intent = new Intent(Global.theActivity, GoogleMapActivity.class);
			intent.putExtra(GoogleMapActivity.EXTRA_TAG, googleMap.mstrTag);
			intent.putExtra(GoogleMapActivity.EXTRA_MAP_TYPE, googleMap.mnMapType);
			intent.putExtra(GoogleMapActivity.EXTRA_LATITUDE, googleMap.mdLatitude);
			intent.putExtra(GoogleMapActivity.EXTRA_LONGITUDE, googleMap.mdLongitude);
			intent.putExtra(GoogleMapActivity.EXTRA_ZOOM_LEVEL, googleMap.mnZoomLevel);
			intent.putExtra(GoogleMapActivity.EXTRA_MARKER, googleMap.mstrMarker);
			intent.putExtra(GoogleMapActivity.EXTRA_X, googleMap.mnX);
			intent.putExtra(GoogleMapActivity.EXTRA_Y, googleMap.mnY);
			intent.putExtra(GoogleMapActivity.EXTRA_WIDTH, googleMap.mnWidth);
			intent.putExtra(GoogleMapActivity.EXTRA_HEIGHT, googleMap.mnHeight);
			Global.theActivity.startActivity(intent);
		}
	}

	private void showMedia(String strMediaTag, int nDisplay)
	{
		if (null == strMediaTag)
		{
			return;
		}
		switch (nDisplay)
		{
		case InteractiveDefine.DISPLAY_TYPE_LAYOUT:
			showMediaLayout(strMediaTag);
			break;
		case InteractiveDefine.DISPLAY_TYPE_FULL_SCREEN:
			showMediaFullScreen(strMediaTag);
			break;
		}
	}

	private void showMediaLayout(String strMediaTag)
	{
		if (null == strMediaTag)
		{
			return;
		}

		InteractiveMediaData mediaData = new InteractiveMediaData();
		Global.interactiveHandler.getMediaData(strMediaTag, mediaData);
		if (null != mediaData)
		{
			Logs.showTrace("Button triggle show layout media media type=" + mediaData.mnMediaType);
			switch (mediaData.mnMediaType)
			{
			case InteractiveDefine.MEDIA_TYPE_LOCAL:
				break;
			case InteractiveDefine.MEDIA_TYPE_URL:
				break;
			case InteractiveDefine.MEDIA_TYPE_YOUTUBE:
				showYoutubeActivity(mediaData.mstrMediaSrc, true);
				break;
			}
		}
		mediaData = null;
	}

	private void showMediaFullScreen(String strMediaTag)
	{
		if (null == strMediaTag)
		{
			return;
		}

		InteractiveMediaData mediaData = new InteractiveMediaData();
		Global.interactiveHandler.getMediaData(strMediaTag, mediaData);
		if (null != mediaData)
		{
			Logs.showTrace("Button triggle show full screent media media type=" + mediaData.mnMediaType);
			switch (mediaData.mnMediaType)
			{
			case InteractiveDefine.MEDIA_TYPE_LOCAL:
				break;
			case InteractiveDefine.MEDIA_TYPE_URL:
				break;
			case InteractiveDefine.MEDIA_TYPE_YOUTUBE:
				showYoutubeActivity(mediaData.mstrMediaSrc, false);
				break;
			}
		}
		mediaData = null;
	}

	private void showYoutubeActivity(String strMediaId, boolean bTranslate)
	{
		if (null == strMediaId)
		{
			return;
		}
		Intent intent = new Intent(Global.theActivity, YouTubePlayerActivity.class);
		intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, strMediaId);
		intent.putExtra(YouTubePlayerActivity.EXTRA_TRANSLATE, bTranslate);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		Global.theActivity.startActivity(intent);
	}

	private InteractiveGoogleMapData getMapData(InteractiveButtonData buttonData, String strMapTag)
	{
		for (int i = 0; i < buttonData.listMapData.size(); ++i)
		{
			if (buttonData.listMapData.get(i).mstrTag.equals(strMapTag))
			{
				return buttonData.listMapData.get(i);
			}
		}
		return null;
	}

	private InteractiveImageData getImageData(InteractiveButtonData buttonData, String strImageTag)
	{
		if (null == buttonData || null == strImageTag || null == buttonData.listImageData)
		{
			return null;
		}

		for (int i = 0; i < buttonData.listImageData.size(); ++i)
		{
			if (buttonData.listImageData.get(i).mstrName.equals(strImageTag))
			{
				return buttonData.listImageData.get(i);
			}
		}
		return null;
	}

	synchronized private void removeImage(String strButtonTag, String strImageTag)
	{
		Logs.showTrace("Remove button image , button tag=" + strButtonTag + " image tag=" + strImageTag);
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null == buttonData)
		{
			Logs.showTrace("Remove button image fail , button data invalid");
			return;
		}

		InteractiveImageData imageData = getImageData(buttonData, strImageTag);
		if (null != imageData)
		{
			buttonData.getContainer().removeView(imageData.mImageView);
			if (null != imageData.mBitmapSrc)
			{
				if (!imageData.mBitmapSrc.isRecycled())
				{
					imageData.mBitmapSrc.recycle();
				}
				imageData.mBitmapSrc = null;
			}
			imageData.mImageView = null;
		}
	}

	private void playMedia(String strMediaTag)
	{
		if (null != strMediaTag)
		{
			EventHandler.notify(Global.interactiveHandler.getNotifyHandler(), EventMessage.MSG_MEDIA_PLAY, 0, 0,
					strMediaTag);
			Logs.showTrace("Button triggle Play media:" + strMediaTag);
		}
	}

	private Handler	selfHandler	= new Handler()
								{

									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_IMAGE_CLICK:
											Tags tags = (Tags) msg.obj;
											mstrButtonTag = tags.mstrButtonTag;
											mstrImageTag = tags.mstrImageTag;
											postDelayed(runRemoveImage, 100);
											break;
										}
									}
								};
}

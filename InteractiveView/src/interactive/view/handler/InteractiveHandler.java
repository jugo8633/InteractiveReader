package interactive.view.handler;

import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.view.postcard.Postcard;
import interactive.view.slideshow.SlideshowViewVideoLayout;
import interactive.view.video.VideoPlayer;
import interactive.view.youtube.YoutubeView;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class InteractiveHandler
{
	private static YoutubeView						youtubeView			= null;
	private static VideoPlayer						videoView			= null;
	private SparseArray<Postcard>					listPostcard		= null;
	private SparseArray<InteractiveYoutubeData>		listYoutube			= null;
	private SparseArray<InteractiveVideoData>		listLocalVideo		= null;
	private String									mstrPostcardDragTag	= null;
	private boolean									mbDraging			= false;
	private InteractiveButtonHandler				buttonHandler		= null;

	public InteractiveHandler()
	{
		super();
		listYoutube = new SparseArray<InteractiveYoutubeData>();
		listLocalVideo = new SparseArray<InteractiveVideoData>();
		listPostcard = new SparseArray<Postcard>();
		buttonHandler = new InteractiveButtonHandler();
	}

	@Override
	protected void finalize() throws Throwable
	{
		buttonHandler = null;
		super.finalize();
	}

	/** Button Interactive handler *************************/
	public void addButton(Context context, String strTag, String strGroupId, ViewGroup viewParent, Handler handler)
	{
		buttonHandler.addButton(context, strTag, strGroupId, handler);
		buttonHandler.setContainer(strTag, viewParent);
	}

	public void addButtonImage(String strButtonTag, String strImageTag, int nWidth, int nHeight, int nX, int nY,
			String strSrc, String strGroupId, boolean bIsVisible)
	{
		buttonHandler.addImageData(strButtonTag, strImageTag, nWidth, nHeight, nX, nY, strSrc, strGroupId, bIsVisible);
	}

	public void addButtonMap(String strButtonTag, String strMapTag, int nMapType, double dLatitude, double dLongitude,
			int nZoomLevel, String strMarker, int nX, int nY, int nWidth, int nHeight, String strBackgroundImage,
			boolean bIsVisible)
	{
		buttonHandler.addMapData(strButtonTag, strMapTag, nMapType, dLatitude, dLongitude, nZoomLevel, strMarker, nX,
				nY, nWidth, nHeight, strBackgroundImage, bIsVisible);
	}

	public void addButtonEvent(String strButtonTag, int nType, String strTypeName, int nEvent, String strEventName,
			int nTargetType, String strTargetID, int nDisplay)
	{
		buttonHandler.addEventData(strButtonTag, nType, strTypeName, nEvent, strEventName, nTargetType, strTargetID,
				nDisplay);
	}

	synchronized private void handleButtonEvent(String strTag)
	{
		if (null != strTag)
		{
			buttonHandler.handleButtonEvent(strTag);
		}
	}

	/** Postcard Interactive handler ********************************/
	public int addPostcard(Postcard postcard)
	{
		int nKey = listPostcard.size();
		listPostcard.put(nKey, postcard);
		return nKey;
	}

	public Postcard getPostcard(int nKey)
	{
		if (null != listPostcard)
		{
			return listPostcard.get(nKey);
		}
		return null;
	}

	public Postcard getPostcard(String strTag)
	{
		if (null != listPostcard)
		{
			for (int i = 0; i < listPostcard.size(); ++i)
			{
				if (listPostcard.get(i).getTag().equals(strTag))
				{
					return listPostcard.get(i);
				}
			}
		}

		Logs.showTrace("Get postcard fail");
		return null;
	}

	public void setPostcardDragTag(String strTag)
	{
		mstrPostcardDragTag = strTag;
	}

	public String getPostcardDragTag()
	{
		return mstrPostcardDragTag;
	}

	public void clearPostcardDragTag()
	{
		mstrPostcardDragTag = null;
	}

	private void setDraging(boolean bDraging)
	{
		mbDraging = bDraging;
	}

	public boolean getDraging()
	{
		return mbDraging;
	}

	private void sendPostcard()
	{
		if (null != getPostcardDragTag())
		{
			Postcard postcard = getPostcard(getPostcardDragTag());
			if (null != postcard)
			{
				postcard.sendPostcard();
			}
		}
	}

	private void DragEnd(int nObject)
	{
		switch (nObject)
		{
		case InteractiveDefine.OBJECT_CATEGORY_POSTCARD:
			if (null != getPostcardDragTag())
			{
				Postcard postcard = getPostcard(getPostcardDragTag());
				if (null != postcard)
				{
					Logs.showTrace("Show postcard tag=" + getPostcardDragTag());
					postcard.hidePostcard(false);
					postcard.endDrag();
				}
				clearPostcardDragTag();
			}
			else
			{
				Logs.showTrace("No postcard drag tag");
			}
			break;
		}
	}

	/** Youtube Interactive handler ********************************/
	public void initMediaView(Activity activity)
	{
		if (null != videoView)
		{
			videoView = null;
		}
		videoView = new VideoPlayer(activity);
		videoView.showController(true);

		if (null != youtubeView)
		{
			youtubeView = null;
		}
		youtubeView = new YoutubeView(activity);
		youtubeView.init("YoutubeView", activity);
		youtubeView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		youtubeView.setOnYoutubePlayListner(new YoutubeView.OnYoutubePlayListner()
		{
			@Override
			public void onYoutubePlayed()
			{
				for (int i = 0; i < listYoutube.size(); ++i)
				{
					if (null != listYoutube.get(i) && listYoutube.get(i).mbIsCurrentPlayer)
					{
						if (listYoutube.get(i).mYoutubeLayout instanceof SlideshowViewVideoLayout)
						{
							((SlideshowViewVideoLayout) listYoutube.get(i).mYoutubeLayout).notifyVideoPlay();
						}
					}
				}
			}
		});
		for (int i = 0; i < listYoutube.size(); ++i)
		{
			listYoutube.get(i).mbIsCurrentPlayer = false;
		}
	}

	public void addYoutubeVideo(RelativeLayout YoutubeLayout, String strTag, boolean bCurrentPlayer,
			String strVideoSrc, boolean bLoop, boolean bController)
	{
		int nKey = listYoutube.size();
		for (int i = 0; i < nKey; ++i)
		{
			if (null != listYoutube.get(i) && listYoutube.get(i).mstrTag.equals(strTag))
			{
				nKey = i;
				removeYoutube();
				break;
			}
		}
		listYoutube.put(nKey, new InteractiveYoutubeData(YoutubeLayout, strTag, bCurrentPlayer, strVideoSrc, bLoop,
				bController));
	}

	private void playYoutube(String strTag)
	{
		removeAllMedia();
		for (int i = 0; i < listYoutube.size(); ++i)
		{
			if (null != listYoutube.get(i) && listYoutube.get(i).mstrTag.equals(strTag))
			{
				youtubeView.bringToFront();
				listYoutube.get(i).mbIsCurrentPlayer = true;
				youtubeView.initVideo(listYoutube.get(i).mstrVideoSrc, listYoutube.get(i).mbIsLoop,
						listYoutube.get(i).mbShowController);
				listYoutube.get(i).mYoutubeLayout.addView(youtubeView);
				youtubeView.play(listYoutube.get(i).mstrVideoSrc);
				break;
			}
		}
	}

	private void removeYoutube()
	{
		if (null == youtubeView)
		{
			return;
		}
		youtubeView.pause();
		for (int i = 0; i < listYoutube.size(); ++i)
		{
			listYoutube.get(i).mbIsCurrentPlayer = false;
			listYoutube.get(i).mYoutubeLayout.removeView(youtubeView);
		}
	}

	/** Video Interactive handler ********************************/
	public void addLocalVideo(RelativeLayout VideoLayout, String strTag, boolean bCurrentPlayer, String strVideoSrc,
			boolean bLoop, boolean bController)
	{
		int nKey = listLocalVideo.size();
		for (int i = 0; i < listLocalVideo.size(); ++i)
		{
			if (null != listLocalVideo.get(i) && listLocalVideo.get(i).mstrTag.equals(strTag))
			{
				nKey = i;
				removeVideo();
				break;
			}
		}
		listLocalVideo.put(nKey, new InteractiveVideoData(VideoLayout, strTag, bCurrentPlayer, strVideoSrc, bLoop,
				bController));
	}

	private void playVideo(String strTag)
	{
		removeAllMedia();
		for (int i = 0; i < listLocalVideo.size(); ++i)
		{
			if (null != listLocalVideo.get(i) && listLocalVideo.get(i).mstrTag.equals(strTag))
			{
				listLocalVideo.get(i).mbIsCurrentPlayer = true;
				listLocalVideo.get(i).mVideoLayout.addView(videoView);
				videoView.play(listLocalVideo.get(i).mstrVideoSrc);
				Logs.showTrace("local video play:" + listLocalVideo.get(i).mstrVideoSrc);
				break;
			}
		}
	}

	private void removeVideo()
	{
		if (null == videoView)
		{
			return;
		}
		videoView.stop();
		for (int i = 0; i < listLocalVideo.size(); ++i)
		{
			listLocalVideo.get(i).mbIsCurrentPlayer = false;
			listLocalVideo.get(i).mVideoLayout.removeView(videoView);
		}
	}

	/********************************************************************************/

	public Handler getNotifyHandler()
	{
		return selfHandler;
	}

	private void playVideo(int nVideoType, String strTag)
	{
		switch (nVideoType)
		{
		case InteractiveDefine.VIDEO_TYPE_LOCAL:
			playVideo(strTag);
			break;
		case InteractiveDefine.VIDEO_TYPE_TOUTUBE:
			playYoutube(strTag);
			break;
		case InteractiveDefine.VIDEO_TYPE_URL:
			break;
		}
	}

	public void removeAllMedia()
	{
		removeVideo();
		removeYoutube();
	}

	private void handleReset(int nObjectType, String strTag)
	{
		switch (nObjectType)
		{
		case InteractiveDefine.OBJECT_CATEGORY_BUTTON:
			buttonHandler.initButton(strTag);
			break;
		case InteractiveDefine.OBJECT_CATEGORY_IMAGE:
			break;
		case InteractiveDefine.OBJECT_CATEGORY_MAP:
			break;
		case InteractiveDefine.OBJECT_CATEGORY_POSTCARD:
			break;
		case InteractiveDefine.OBJECT_CATEGORY_SLIDE_SHOW:
			break;
		case InteractiveDefine.OBJECT_CATEGORY_TICKET_BOOK:
			break;
		case InteractiveDefine.OBJECT_CATEGORY_VIDEO:
			break;
		}
	}

	private Handler	selfHandler	= new Handler()
								{
									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_VIDEO_PLAY:
											playVideo(msg.arg1, (String) msg.obj);
											break;
										case EventMessage.MSG_SEND_POSTCARD:
											sendPostcard();
											break;
										case EventMessage.MSG_DRAG_START:
											setDraging(true);
											break;
										case EventMessage.MSG_DRAG_END:
											setDraging(false);
											DragEnd(msg.arg1);
											break;
										case EventMessage.MSG_BUTTON_EVENT:
											handleButtonEvent((String) msg.obj);
											break;
										case EventMessage.MSG_RESET:
											handleReset(msg.arg1, (String) msg.obj);
											break;
										}
									}
								};
}

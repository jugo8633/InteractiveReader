package interactive.view.handler;

import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.define.InteractiveDefine;
import interactive.view.postcard.Postcard;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.ViewGroup;

public class InteractiveHandler
{
	private SparseArray<Postcard>		listPostcard		= null;
	private String						mstrPostcardDragTag	= null;
	private boolean						mbDraging			= false;
	private InteractiveButtonHandler	buttonHandler		= null;
	private InteractiveMediaHandler		mediaHandler		= null;

	public InteractiveHandler()
	{
		super();
		mediaHandler = new InteractiveMediaHandler();
		buttonHandler = new InteractiveButtonHandler();
		listPostcard = new SparseArray<Postcard>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		buttonHandler = null;
		mediaHandler = null;
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

	public void addButtonMedia(String strButtonTag, String strName, int nWidth, int nHeight, int nX, int nY,
			String strSrc, int nMediaType, String strMediaSrc, int nStart, int nEnd, boolean bAutoplay, boolean bLoop,
			boolean bPlayerControls, boolean bIsVisible, ViewGroup viewParent, boolean bIsCurrentPlayer)
	{
		buttonHandler.addMediaData(strButtonTag, strName, nWidth, nHeight, nX, nY, strSrc, nMediaType, strMediaSrc,
				nStart, nEnd, bAutoplay, bLoop, bPlayerControls, bIsVisible, viewParent, bIsCurrentPlayer);
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

	/** Media handle ***************************************************/
	private void playVideo(String strTag)
	{
		mediaHandler.playMedia(strTag);
	}

	private void stopMedia(int nMediaType)
	{
		if (Type.INVALID == nMediaType)
		{
			mediaHandler.stopMedia();
		}
		else
		{
			mediaHandler.stopMedia(nMediaType);
		}
	}

	private void stopMedia(String strTag)
	{
		mediaHandler.stopMedia(strTag);
	}

	public void addMediaData(String strName, int nWidth, int nHeight, int nX, int nY, String strSrc, int nMediaType,
			String strMediaSrc, int nStart, int nEnd, boolean bAutoplay, boolean bLoop, boolean bPlayerControls,
			boolean bIsVisible, ViewGroup viewParent, boolean bIsCurrentPlayer)
	{
		mediaHandler.addMediaData(strName, nWidth, nHeight, nX, nY, strSrc, nMediaType, strMediaSrc, nStart, nEnd,
				bAutoplay, bLoop, bPlayerControls, bIsVisible, viewParent, bIsCurrentPlayer);
	}

	public boolean getMediaData(String strName, InteractiveMediaData mediaData)
	{
		return mediaHandler.getMediaData(strName, mediaData);
	}

	public void initMediaView(Activity activity)
	{
		mediaHandler.initMediaView(activity);
	}

	public void setMediaContainer(String strTagName, ViewGroup viewContainer)
	{
		mediaHandler.setMediaContainer(strTagName, viewContainer);
	}

	/********************************************************************************/

	public Handler getNotifyHandler()
	{
		return selfHandler;
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
										case EventMessage.MSG_MEDIA_PLAY:
											playVideo((String) msg.obj);
											break;
										case EventMessage.MSG_MEDIA_STOP:
											if (null != msg.obj)
											{
												stopMedia((String) msg.obj);
											}
											else
											{
												stopMedia(msg.arg1); // msg.arg1 = -1 , stop all media
											}
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

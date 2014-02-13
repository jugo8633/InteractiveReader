package interactive.view.handler;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.global.Global;
import interactive.view.image.InteractiveImageView;
import interactive.view.map.GoogleMapActivity;
import interactive.view.postcard.Postcard;
import interactive.view.slideshow.SlideshowViewVideoLayout;
import interactive.view.video.VideoPlayer;
import interactive.view.webview.InteractiveWebView;
import interactive.view.youtube.YoutubeView;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class InteractiveHandler
{
	private Runnable				runRemoveImage;
	private String					mstrRemoveImage		= null;
	private static YoutubeView		youtubeView			= null;
	private static VideoPlayer		videoView			= null;
	private SparseArray<Postcard>	listPostcard		= null;
	private String					mstrPostcardDragTag	= null;
	private boolean					mbDraging			= false;

	public class GoogleMap
	{
		public String	mstrTag				= null;
		public int		mnMapType			= Type.INVALID;
		public double	mdLatitude			= 0f;
		public double	mdLongitude			= 0f;
		public int		mnZoomLevel			= Type.INVALID;
		public String	mstrMarker			= null;
		public int		mnX					= 0;
		public int		mnY					= 0;
		public int		mnWidth				= LayoutParams.MATCH_PARENT;
		public int		mnHeight			= LayoutParams.MATCH_PARENT;
		public String	mstrBackgroundImage	= null;

		public GoogleMap(String strTag, int nMapType, double dLatitude, double dLongitude, int nZoomLevel,
				String strMarker, int nX, int nY, int nWidth, int nHeight, String strBackgroundImage)
		{
			mstrTag = strTag;
			mnMapType = nMapType;
			mdLatitude = dLatitude;
			mdLongitude = dLongitude;
			mnZoomLevel = nZoomLevel;
			mstrMarker = strMarker;
			mnX = nX;
			mnY = nY;
			mnWidth = nWidth;
			mnHeight = nHeight;
			mstrBackgroundImage = strBackgroundImage;
		}
	}

	class InteractiveImage
	{
		public String				mstrTag			= null;
		public String				mstrImagePath	= null;
		public String				mstrGroupId		= null;
		public int					mnX				= 0;
		public int					mnY				= 0;
		public int					mnWidth			= LayoutParams.WRAP_CONTENT;
		public int					mnHeight		= LayoutParams.WRAP_CONTENT;
		public Handler				notifyHandler	= null;						// image
																					// close
																					// notify
		public InteractiveWebView	webView			= null;

		public InteractiveImage(InteractiveWebView interactiveWeb, String strTag, String strImagePath, int nX, int nY,
				int nWidth, int nHeight, String strGroupId)
		{
			mstrTag = strTag;
			webView = interactiveWeb;
			mstrImagePath = strImagePath;
			mnX = nX;
			mnY = nY;
			mnWidth = nWidth;
			mnHeight = nHeight;
			mstrGroupId = strGroupId;
		}

		public InteractiveImage(String strTag, Handler handler)
		{
			mstrTag = strTag;
			notifyHandler = handler;
		}
	}

	class YoutubeVideo
	{
		public RelativeLayout	mYoutubeLayout		= null;
		public String			mstrTag				= null;
		public boolean			mbIsCurrentPlayer	= false;
		public String			mstrVideoSrc		= null;
		public boolean			mbIsLoop			= false;
		public boolean			mbShowController	= false;

		public YoutubeVideo(RelativeLayout YoutubeLayout, String strTag, boolean bCurrentPlayer, String strVideoSrc,
				boolean bLoop, boolean bController)
		{
			mYoutubeLayout = YoutubeLayout;
			mstrTag = strTag;
			mbIsCurrentPlayer = bCurrentPlayer;
			mstrVideoSrc = strVideoSrc;
			mbIsLoop = bLoop;
			mbShowController = bController;
		}
	}

	class LocalVideo
	{
		public RelativeLayout	mVideoLayout		= null;
		public String			mstrTag				= null;
		public boolean			mbIsCurrentPlayer	= false;
		public String			mstrVideoSrc		= null;
		public boolean			mbIsLoop			= false;
		public boolean			mbShowController	= false;

		public LocalVideo(RelativeLayout VideoLayout, String strTag, boolean bCurrentPlayer, String strVideoSrc,
				boolean bLoop, boolean bController)
		{
			mVideoLayout = VideoLayout;
			mstrTag = strTag;
			mbIsCurrentPlayer = bCurrentPlayer;
			mstrVideoSrc = strVideoSrc;
			mbIsLoop = bLoop;
			mbShowController = bController;
		}
	}

	private SparseArray<GoogleMap>			listGoogleMap	= null;
	private SparseArray<InteractiveImage>	listImage		= null;
	private SparseArray<YoutubeVideo>		listYoutube		= null;
	private SparseArray<LocalVideo>			listLocalVideo	= null;

	public InteractiveHandler()
	{
		super();
		listGoogleMap = new SparseArray<GoogleMap>();
		listImage = new SparseArray<InteractiveImage>();
		listYoutube = new SparseArray<YoutubeVideo>();
		listLocalVideo = new SparseArray<LocalVideo>();
		listPostcard = new SparseArray<Postcard>();
		runRemoveImage = new Runnable()
		{
			@Override
			public void run()
			{
				removeInteractiveImageView(mstrRemoveImage);
			}
		};
	}

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

	public void addGoogleMap(String strTag, int nMapType, double dLatitude, double dLongitude, int nZoomLevel,
			String strMarker, int nX, int nY, int nWidth, int nHeight, String strBackgroundImage)
	{
		listGoogleMap.put(listGoogleMap.size(), new GoogleMap(strTag, nMapType, dLatitude, dLongitude, nZoomLevel,
				strMarker, nX, nY, nWidth, nHeight, strBackgroundImage));
	}

	public void addInteractiveImage(InteractiveWebView interactiveWeb, String strTag, String strImagePath, int nX,
			int nY, int nWidth, int nHeight, String strGroupId)
	{
		InteractiveImage interimg = getInteractiveImage(strTag);
		if (null != interimg)
		{
			if (null != interactiveWeb)
			{
				interimg.webView = interactiveWeb;
				interimg.mstrImagePath = strImagePath;
				interimg.mnX = nX;
				interimg.mnY = nY;
				interimg.mnWidth = nWidth;
				interimg.mnHeight = nHeight;
				interimg.mstrGroupId = strGroupId;
			}
		}
		else
		{
			listImage.put(listImage.size(), new InteractiveImage(interactiveWeb, strTag, strImagePath, nX, nY, nWidth,
					nHeight, strGroupId));
		}
	}

	public void addInteractiveImageNotify(String strTag, Handler handler)
	{
		InteractiveImage interimg = getInteractiveImage(strTag);
		if (null != interimg)
		{
			if (null != handler)
			{
				interimg.notifyHandler = handler;
			}
		}
		else
		{
			listImage.put(listImage.size(), new InteractiveImage(strTag, handler));
		}
	}

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
		listLocalVideo.put(nKey, new LocalVideo(VideoLayout, strTag, bCurrentPlayer, strVideoSrc, bLoop, bController));
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
		listYoutube.put(nKey, new YoutubeVideo(YoutubeLayout, strTag, bCurrentPlayer, strVideoSrc, bLoop, bController));
	}

	private InteractiveImage getInteractiveImage(String strTag)
	{
		if (null == strTag)
		{
			return null;
		}
		for (int i = 0; i < listImage.size(); ++i)
		{
			if (listImage.get(i).mstrTag.equals(strTag))
			{
				return listImage.get(i);
			}
		}
		return null;
	}

	public void showGoogleMapActivity(String strTag)
	{
		if (null == Global.theActivity)
		{
			Logs.showTrace("Invalid Activity");
			return;
		}
		GoogleMap googleMap = findGoogleMap(strTag);
		if (null != googleMap)
		{
			//	Intent intent = new Intent(Global.theActivity, GoogleMapActivity.class);
			Intent intent = new Intent("interactive.view.map.GoogleMapActivity.LAUNCH");
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
			intent.putExtra(GoogleMapActivity.EXTRA_BACKGROUND_IMAGE, googleMap.mstrBackgroundImage);
			Global.theActivity.startActivity(intent);
		}
	}

	private GoogleMap findGoogleMap(String strTag)
	{
		for (int i = 0; i < listGoogleMap.size(); ++i)
		{
			if (listGoogleMap.get(i).mstrTag.equals(strTag))
			{
				return listGoogleMap.get(i);
			}
		}
		return null;
	}

	private void showItem(int nCategory, String strTagName)
	{
		switch (nCategory)
		{
		case InteractiveEvent.OBJECT_CATEGORY_MAP:
			showGoogleMapActivity(strTagName);
			break;
		case InteractiveEvent.OBJECT_CATEGORY_IMAGE:
			showInteractiveImage(strTagName);
			break;
		}
	}

	public Handler getNotifyHandler()
	{
		return notifyHandler;
	}

	private void showInteractiveImage(String strTagName)
	{
		InteractiveImage interimg = getInteractiveImage(strTagName);
		if (null != interimg && null != interimg.mstrImagePath)
		{
			InteractiveImageView imgview = new InteractiveImageView(Global.theActivity);
			imgview.setTag(interimg.mstrTag);
			imgview.setImageURI(Uri.parse(interimg.mstrImagePath));
			imgview.setDisplay(interimg.mnX, interimg.mnY, interimg.mnWidth, interimg.mnHeight);
			imgview.setNotifyHandler(notifyHandler);
			View view = interimg.webView.findViewWithTag(strTagName);
			if (null != view)
			{
				interimg.webView.removeView(view);
			}
			interimg.webView.hideItem(strTagName);
			interimg.webView.addView(imgview);
			imgview.bringToFront();
		}
	}

	private void removeInteractiveImageView(String strTag)
	{
		if (null != strTag)
		{
			InteractiveImage interimg = getInteractiveImage(strTag);
			if (null != interimg)
			{
				EventHandler.notify(interimg.notifyHandler, EventMessage.MSG_IMAGE_CLICK, 0, 0, null);
				View view = interimg.webView.findViewWithTag(strTag);
				if (null != view)
				{
					interimg.webView.removeView(view);
				}
			}
		}
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

	private void playVideo(int nVideoType, String strTag)
	{
		switch (nVideoType)
		{
		case InteractiveEvent.VIDEO_TYPE_LOCAL:
			playVideo(strTag);
			break;
		case InteractiveEvent.VIDEO_TYPE_TOUTUBE:
			playYoutube(strTag);
			break;
		case InteractiveEvent.VIDEO_TYPE_URL:
			break;
		}
	}

	public void removeAllMedia()
	{
		removeVideo();
		removeYoutube();
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
		case InteractiveEvent.OBJECT_CATEGORY_POSTCARD:
			if (null != getPostcardDragTag())
			{
				Postcard postcard = getPostcard(getPostcardDragTag());
				if (null != postcard)
				{
					Logs.showTrace("Show postcard tag=" + getPostcardDragTag());
					postcard.hidePostcard(false);
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

	private Handler	notifyHandler	= new Handler()
									{
										@Override
										public void handleMessage(Message msg)
										{
											switch (msg.what)
											{
											case EventMessage.MSG_SHOW_ITEM:
												showItem(msg.arg1, (String) msg.obj);
												break;
											case EventMessage.MSG_IMAGE_CLICK:
												mstrRemoveImage = (String) msg.obj;
												postDelayed(runRemoveImage, 100);
												break;
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
											}
										}
									};
}

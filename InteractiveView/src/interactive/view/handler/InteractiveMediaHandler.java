package interactive.view.handler;

import android.app.Activity;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import interactive.common.Logs;
import interactive.view.define.InteractiveDefine;
import interactive.view.video.VideoPlayer;
import interactive.view.youtube.YoutubeView;

public class InteractiveMediaHandler
{
	private static YoutubeView					youtubePlayer	= null;
	private static VideoPlayer					videoPlayer		= null;
	private SparseArray<InteractiveMediaData>	listMediaData	= null;

	public InteractiveMediaHandler()
	{
		super();
		listMediaData = new SparseArray<InteractiveMediaData>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		youtubePlayer = null;
		videoPlayer = null;
		super.finalize();
	}

	public void initMediaView(Activity activity)
	{
		if (null != videoPlayer)
		{
			videoPlayer = null;
		}
		videoPlayer = new VideoPlayer(activity);
		videoPlayer.showController(true);

		if (null != youtubePlayer)
		{
			youtubePlayer = null;
		}

		youtubePlayer = new YoutubeView(activity);
		youtubePlayer.init("YoutubeView", activity);
		youtubePlayer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		for (int i = 0; i < listMediaData.size(); ++i)
		{
			listMediaData.get(i).mbIsCurrentPlayer = false;
		}
	}

	public void addMediaData(String strName, int nWidth, int nHeight, int nX, int nY, String strSrc, int nMediaType,
			String strMediaSrc, int nStart, int nEnd, boolean bAutoplay, boolean bLoop, boolean bPlayerControls,
			boolean bIsVisible, ViewGroup viewParent, boolean bIsCurrentPlayer)
	{
		int nKey = listMediaData.size();
		for (int i = 0; i < nKey; ++i)
		{
			if (null != listMediaData.get(i) && listMediaData.get(i).mstrName.equals(strName))
			{
				nKey = i;
				break;
			}
		}
		listMediaData
				.put(nKey, new InteractiveMediaData(strName, nWidth, nHeight, nX, nY, strSrc, nMediaType, strMediaSrc,
						nStart, nEnd, bAutoplay, bLoop, bPlayerControls, bIsVisible, viewParent, bIsCurrentPlayer));
	}

	public void playMedia(String strTag)
	{
		stopMedia();
		for (int i = 0; i < listMediaData.size(); ++i)
		{
			if (null != listMediaData.get(i) && listMediaData.get(i).mstrName.equals(strTag))
			{
				listMediaData.get(i).mbIsCurrentPlayer = true;
				switch (listMediaData.get(i).mnMediaType)
				{
				case InteractiveDefine.MEDIA_TYPE_LOCAL:
				case InteractiveDefine.MEDIA_TYPE_URL:
					Logs.showTrace("Video play:" + listMediaData.get(i).mstrMediaSrc);
					listMediaData.get(i).mMediaContainer.addView(videoPlayer);
					videoPlayer.setLoop(listMediaData.get(i).mbLoop);
					videoPlayer.showController(listMediaData.get(i).mbPlayerControls);
					videoPlayer.play(listMediaData.get(i).mstrMediaSrc);
					break;
				case InteractiveDefine.MEDIA_TYPE_YOUTUBE:
					youtubePlayer.bringToFront();
					youtubePlayer.initVideo(listMediaData.get(i).mstrMediaSrc, listMediaData.get(i).mbLoop,
							listMediaData.get(i).mbPlayerControls, listMediaData.get(i).mnStart,
							listMediaData.get(i).mnEnd);
					listMediaData.get(i).mMediaContainer.addView(youtubePlayer);
					youtubePlayer.play(listMediaData.get(i).mstrMediaSrc);
					Logs.showTrace("Play youtube on container:" + listMediaData.get(i).mMediaContainer.getTag());
					break;
				}
				break;
			}
		}
	}

	public void stopMedia()
	{
		for (int i = 0; i < listMediaData.size(); ++i)
		{
			if (listMediaData.get(i).mbIsCurrentPlayer)
			{
				listMediaData.get(i).mbIsCurrentPlayer = false;
				switch (listMediaData.get(i).mnMediaType)
				{
				case InteractiveDefine.MEDIA_TYPE_LOCAL:
				case InteractiveDefine.MEDIA_TYPE_URL:
					videoPlayer.stop();
					listMediaData.get(i).mMediaContainer.removeView(videoPlayer);
					break;
				case InteractiveDefine.MEDIA_TYPE_YOUTUBE:
					youtubePlayer.pause();
					listMediaData.get(i).mMediaContainer.removeView(youtubePlayer);
					break;
				}
			}
		}
	}

	public void stopMedia(int nMediaType)
	{
		for (int i = 0; i < listMediaData.size(); ++i)
		{
			if (listMediaData.get(i).mbIsCurrentPlayer && listMediaData.get(i).mnMediaType == nMediaType)
			{
				listMediaData.get(i).mbIsCurrentPlayer = false;
				switch (listMediaData.get(i).mnMediaType)
				{
				case InteractiveDefine.MEDIA_TYPE_LOCAL:
				case InteractiveDefine.MEDIA_TYPE_URL:
					videoPlayer.stop();
					listMediaData.get(i).mMediaContainer.removeView(videoPlayer);
					break;
				case InteractiveDefine.MEDIA_TYPE_YOUTUBE:
					youtubePlayer.pause();
					listMediaData.get(i).mMediaContainer.removeView(youtubePlayer);
					break;
				}
			}
		}
	}

	public void stopMedia(String strTag)
	{
		for (int i = 0; i < listMediaData.size(); ++i)
		{
			if (listMediaData.get(i).mbIsCurrentPlayer && listMediaData.get(i).mstrName.equals(strTag))
			{
				listMediaData.get(i).mbIsCurrentPlayer = false;
				switch (listMediaData.get(i).mnMediaType)
				{
				case InteractiveDefine.MEDIA_TYPE_LOCAL:
				case InteractiveDefine.MEDIA_TYPE_URL:
					videoPlayer.stop();
					listMediaData.get(i).mMediaContainer.removeView(videoPlayer);
					break;
				case InteractiveDefine.MEDIA_TYPE_YOUTUBE:
					youtubePlayer.pause();
					listMediaData.get(i).mMediaContainer.removeView(youtubePlayer);
					break;
				}
			}
		}
	}

	public void setMediaContainer(String strTagName, ViewGroup viewContainer)
	{
		if (null == strTagName || null == viewContainer)
		{
			return;
		}
		for (int i = 0; i < listMediaData.size(); ++i)
		{
			if (listMediaData.get(i).mstrName.equals(strTagName))
			{
				listMediaData.get(i).mMediaContainer = viewContainer;
				break;
			}
		}
	}
}

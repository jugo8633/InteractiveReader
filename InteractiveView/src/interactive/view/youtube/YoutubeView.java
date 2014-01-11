package interactive.view.youtube;

import interactive.common.Device;
import interactive.common.Logs;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.RelativeLayout;

public class YoutubeView extends RelativeLayout implements YouTubePlayer.OnInitializedListener
{
	/** The player view cannot be smaller than 110 pixels high. */
	private final int							PLAYER_VIEW_MINIMUM_HEIGHT_DP	= 110;
	private final String						DEVELOPER_KEY					= "AIzaSyDSCh-Uv3tkWuWg_wGF_uTpBS5ULXVq9Do";
	private YoutubeFragment						youtubeFragment					= null;
	private YouTubePlayer						player							= null;
	private String								mstrVideoId						= null;
	private boolean								mbIsPlay						= false;
	private boolean								mbIsLoop						= false;
	private boolean								mbShowController				= true;
	private Context								theContext						= null;
	private SparseArray<OnYoutubePlayListner>	listOnYoutubePlayListner		= null;

	public interface OnYoutubePlayListner
	{
		void onYoutubePlayed();
	}

	public YoutubeView(Context context)
	{
		super(context);
		init(context);
	}

	public YoutubeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public YoutubeView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	private void init(Context context)
	{
		theContext = context;
		listOnYoutubePlayListner = new SparseArray<OnYoutubePlayListner>();
	}

	public void init(String strTag, Activity activity)
	{
		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
		Fragment fragment = activity.getFragmentManager().findFragmentByTag(strTag);
		if (null != fragment)
		{
			ft.remove(fragment);
		}

		this.setTag(strTag);

		youtubeFragment = new YoutubeFragment();
		youtubeFragment.initialize(DEVELOPER_KEY, this);
		youtubeFragment.setContainer(this);

		ft.add(0, youtubeFragment, strTag);
		ft.commit();

	}

	public void initVideo(String strVideoId, boolean bLoop, boolean bShowControl)
	{
		mstrVideoId = strVideoId;
		mbIsLoop = bLoop;
		mbShowController = bShowControl;
	}

	public void setLoop(boolean bLoop)
	{
		mbIsLoop = bLoop;
	}

	public void showController(boolean bShow)
	{
		mbShowController = bShow;

	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		int nMixHeight = nHeight;
		setX(nX);
		setY(nY);
		if (PLAYER_VIEW_MINIMUM_HEIGHT_DP > nHeight)
		{
			nMixHeight = PLAYER_VIEW_MINIMUM_HEIGHT_DP;
		}
		setLayoutParams(new LayoutParams(nWidth, nMixHeight));
	}

	public void play(String strVideoId)
	{
		mstrVideoId = strVideoId;
		mbIsPlay = true;

		if (null != player && null != strVideoId)
		{
			if (player.isPlaying())
			{
				player.pause();
			}
			player.loadVideo(mstrVideoId);
			Logs.showTrace("Youtube play video: " + strVideoId);
		}
		else
		{
			if (null == player)
			{
				Logs.showTrace("Youtube player is null");
			}
			Logs.showTrace("Youtube play fail id=" + strVideoId);
		}
	}

	public void pause()
	{
		if (null != player)
		{
			if (player.isPlaying())
			{
				mbIsPlay = false;
				player.pause();
			}
		}
	}

	public void seekTo(int nSec)
	{
		player.seekToMillis(nSec * 1000);
	}

	@Override
	public void onInitializationFailure(Provider arg0, YouTubeInitializationResult errorReason)
	{
		Logs.showTrace("Youtube Init fail error:" + errorReason.toString());
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer YoutubePlayer,
			boolean wasRestored)
	{
		Logs.showTrace("Youtube initialization success");
		player = YoutubePlayer;
		if (mbShowController)
		{
			player.setPlayerStyle(PlayerStyle.DEFAULT);
		}
		else
		{
			player.setPlayerStyle(PlayerStyle.CHROMELESS);
		}
		player.setShowFullscreenButton(false);
		player.setPlaybackEventListener(new PlaybackEvent());
		player.setPlayerStateChangeListener(new VideoListener());
		player.setOnFullscreenListener(new OnFullscreenListener()
		{
			@Override
			public void onFullscreen(boolean bFullScreen)
			{
				Device device = new Device(theContext);
				device.getOrientation();
				//		theActivity.setRequestedOrientation(device.getOrientation());
				device = null;
			}
		});
		if (null != mstrVideoId)
		{
			if (!wasRestored)
			{
				player.cueVideo(mstrVideoId);
			}
		}
	}

	public void setOnYoutubePlayListner(YoutubeView.OnYoutubePlayListner listner)
	{
		if (null != listner)
		{
			listOnYoutubePlayListner.put(listOnYoutubePlayListner.size(), listner);
		}
	}

	private final class PlaybackEvent implements YouTubePlayer.PlaybackEventListener
	{

		@Override
		public void onBuffering(boolean arg0)
		{

		}

		@Override
		public void onPaused()
		{

		}

		@Override
		public void onPlaying()
		{
			for (int i = 0; i < listOnYoutubePlayListner.size(); ++i)
			{
				listOnYoutubePlayListner.get(i).onYoutubePlayed();
			}
			Logs.showTrace("youtube playing");
		}

		@Override
		public void onSeekTo(int arg0)
		{
		}

		@Override
		public void onStopped()
		{
		}

	}

	private final class VideoListener implements YouTubePlayer.PlayerStateChangeListener
	{

		@Override
		public void onLoaded(String videoId)
		{
			Logs.showTrace("Youtube loaded videoid=" + videoId);
			if (mbIsPlay)
			{
				player.play();
			}
		}

		@Override
		public void onVideoEnded()
		{
			if (mbIsLoop)
			{
				player.play();
				return;
			}
			mbIsPlay = false;
		}

		@Override
		public void onError(YouTubePlayer.ErrorReason errorReason)
		{
			Logs.showTrace("YouTubePlayer Error ErrorReason:" + errorReason.toString());

			if (errorReason == YouTubePlayer.ErrorReason.UNAUTHORIZED_OVERLAY)
			{

			}
			if (null != player)
			{
				player.pause();
			}
			if (errorReason == YouTubePlayer.ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION)
			{
				player.release();
				player = null;
			}
		}

		@Override
		public void onVideoStarted()
		{
			Logs.showTrace("youtube video start");
		}

		@Override
		public void onAdStarted()
		{
			Logs.showTrace("youtube video AD start");
		}

		@Override
		public void onLoading()
		{
			Logs.showTrace("youtube video loading");
		}

	}
}

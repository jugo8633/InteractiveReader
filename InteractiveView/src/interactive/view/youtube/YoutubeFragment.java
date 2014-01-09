package interactive.view.youtube;

import interactive.common.EventHandler;

import com.google.android.youtube.player.YouTubePlayerFragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class YoutubeFragment extends YouTubePlayerFragment
{

	public static final int	NOTIFY_VIEW_CREATED	= 0;
	public static final int	NOTIFY_DESTROY		= 1;
	private RelativeLayout	relativeMain		= null;
	private Handler			theHandler			= null;
	private View			playerView			= null;

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}

	public YoutubeFragment()
	{
		super();
		YoutubeFragment.newInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		playerView = super.onCreateView(inflater, container, savedInstanceState);
		if (null != playerView)
		{
			playerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			if (null != relativeMain)
			{
				relativeMain.addView(playerView);
			}

			if (null != theHandler)
			{
				EventHandler.notify(theHandler, NOTIFY_VIEW_CREATED, 0, 0, null);
			}
		}

		playerView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
			}
		});
		return playerView;
	}

	public View getPlayerView()
	{
		return playerView;
	}

	@Override
	public void onDestroy()
	{
		EventHandler.notify(theHandler, NOTIFY_DESTROY, 0, 0, null);
		super.onDestroy();
	}

	@Override
	public void onDestroyView()
	{
		if (null != relativeMain)
		{
			relativeMain.removeAllViews();
			relativeMain.removeAllViewsInLayout();
		}
		super.onDestroyView();
	}

	public void setContainer(RelativeLayout rlMain)
	{
		relativeMain = rlMain;
	}

	public void initHandler(Handler handler)
	{
		theHandler = handler;
	}

}

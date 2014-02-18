package interactive.view.handler;

import android.widget.RelativeLayout;

public class InteractiveVideoData
{
	public RelativeLayout	mVideoLayout		= null;
	public String			mstrTag				= null;
	public boolean			mbIsCurrentPlayer	= false;
	public String			mstrVideoSrc		= null;
	public boolean			mbIsLoop			= false;
	public boolean			mbShowController	= false;

	public InteractiveVideoData(RelativeLayout VideoLayout, String strTag, boolean bCurrentPlayer, String strVideoSrc,
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

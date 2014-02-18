package interactive.view.handler;

import android.widget.RelativeLayout;

public class InteractiveYoutubeData
{
	public RelativeLayout	mYoutubeLayout		= null;
	public String			mstrTag				= null;
	public boolean			mbIsCurrentPlayer	= false;
	public String			mstrVideoSrc		= null;
	public boolean			mbIsLoop			= false;
	public boolean			mbShowController	= false;

	public InteractiveYoutubeData(RelativeLayout YoutubeLayout, String strTag, boolean bCurrentPlayer,
			String strVideoSrc, boolean bLoop, boolean bController)
	{
		mYoutubeLayout = YoutubeLayout;
		mstrTag = strTag;
		mbIsCurrentPlayer = bCurrentPlayer;
		mstrVideoSrc = strVideoSrc;
		mbIsLoop = bLoop;
		mbShowController = bController;
	}
}

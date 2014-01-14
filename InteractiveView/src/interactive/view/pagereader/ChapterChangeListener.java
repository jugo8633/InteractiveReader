package interactive.view.pagereader;

import interactive.common.EventMessage;
import interactive.view.pagereader.ViewPager.OnPageChangeListener;
import android.os.Handler;
import android.os.Message;

public class ChapterChangeListener implements OnPageChangeListener
{

	private Handler	theHandler	= null;

	public ChapterChangeListener()
	{
		super();
	}

	public ChapterChangeListener(Handler handler)
	{
		super();
		theHandler = handler;
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{

	}

	@Override
	public void onPageSelected(int nChapter)
	{
		notify(EventMessage.MSG_VIEW_CHANGE, nChapter);
	}

	private void notify(int nEvent, int nPosition)
	{
		if (null != theHandler)
		{
			Message msg = new Message();
			msg.what = EventMessage.MSG_CHAPTER;
			msg.arg1 = nEvent;
			msg.arg2 = nPosition;
			msg.obj = null;
			theHandler.sendMessage(msg);
		}
	}

}

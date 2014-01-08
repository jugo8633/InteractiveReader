package interactive.interactiveview.pagereader;

import interactive.interactiveview.pagereader.ViewPager.OnPageChangeListener;
import android.os.Handler;
import android.os.Message;

public class ChapterChangeListener implements OnPageChangeListener
{

	private Handler	theHandler	= null;

	public ChapterChangeListener()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ChapterChangeListener(Handler handler)
	{
		super();
		theHandler = handler;
	}

	@Override
	protected void finalize() throws Throwable
	{
		// TODO Auto-generated method stub
		super.finalize();
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int nChapter)
	{
		// TODO Auto-generated method stub
		notify(EventMessage.MSG_VIEW_CHANGE, nChapter);
	}

	private void notify(int nEvent, int nPosition)
	{
		if (null != theHandler)
		{
			Message msg = new Message();
			msg.what = EventMessage.MSG_CHAPTER; // message type
			msg.arg1 = nEvent; // event
			msg.arg2 = nPosition;
			msg.obj = null;
			theHandler.sendMessage(msg);
		}
	}

}

package interactive.view.pagereader;

import interactive.common.EventMessage;
import interactive.view.pagereader.VerticalViewPager.OnPageChangeListener;
import android.os.Handler;
import android.os.Message;

public class PageChangeListener implements OnPageChangeListener
{

	private Handler	theHandler	= null;

	public PageChangeListener()
	{
		super();
	}

	public PageChangeListener(Handler handler)
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
	public void onPageScrollStateChanged(int state)
	{

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
	{

	}

	@Override
	public void onPageSelected(int nPage)
	{
		notify(EventMessage.MSG_VIEW_CHANGE, nPage);
	}

	private void notify(int nEvent, int nPosition)
	{
		if (null != theHandler)
		{
			Message msg = new Message();
			msg.what = EventMessage.MSG_PAGE;
			msg.arg1 = nEvent;
			msg.arg2 = nPosition;
			msg.obj = null;
			theHandler.sendMessage(msg);
		}
	}

}

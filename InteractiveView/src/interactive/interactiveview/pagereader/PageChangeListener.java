package interactive.interactiveview.pagereader;

import org.iii.ideas.appcross.viewpager.VerticalViewPager.OnPageChangeListener;

import android.os.Handler;
import android.os.Message;
import frame.event.EventMessage;

public class PageChangeListener implements OnPageChangeListener
{

	private Handler	theHandler	= null;

	public PageChangeListener()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public PageChangeListener(Handler handler)
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
	public void onPageScrollStateChanged(int state)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int nPage)
	{
		// TODO Auto-generated method stub
		notify(EventMessage.MSG_VIEW_CHANGE, nPage);
	}

	private void notify(int nEvent, int nPosition)
	{
		if (null != theHandler)
		{
			Message msg = new Message();
			msg.what = EventMessage.MSG_PAGE; // message type
			msg.arg1 = nEvent; // event
			msg.arg2 = nPosition;
			msg.obj = null;
			theHandler.sendMessage(msg);
		}
	}

}

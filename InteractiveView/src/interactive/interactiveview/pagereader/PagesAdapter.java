package interactive.interactiveview.pagereader;

import frame.event.EventMessage;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.iii.ideas.appcross.AppCrossApplication;
import org.iii.ideas.appcross.AppCrossDisplayPage;
import org.iii.ideas.appcross.viewpager.*;

public class PagesAdapter extends VerticalPagerAdapter
{

	private SparseArray<AppCrossDisplayPage>	listPage	= null;
	private Context								theContext	= null;
	private Handler								theHandler	= null;

	public PagesAdapter()
	{
		initData();
	}

	public PagesAdapter(Handler handler)
	{
		initData();
		theHandler = handler;
	}

	private void initData()
	{
		listPage = new SparseArray<AppCrossDisplayPage>();
	}

	public void setContext(Context context)
	{
		theContext = context;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return listPage.size();
	}

	@Override
	public int getItemPosition(Object object)
	{
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
		//	return POSITION_NONE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup viewPager, int position, Object object)
	{
		// TODO Auto-generated method stub
		// ((VerticalViewPager) arg0).removeView(listPage.get(arg1));
		viewPager.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		// TODO Auto-generated method stub

		container.addView(listPage.get(position));

		return listPage.get(position);
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	public void addPageView(AppCrossDisplayPage pageView)
	{
		listPage.put(listPage.size(), pageView);
	}

	public void clear()
	{
		listPage.clear();
	}

	public int size()
	{
		return listPage.size();
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

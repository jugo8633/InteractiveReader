package interactive.interactiveview.pagereader;

import frame.event.EventMessage;
import android.os.Handler;
import android.os.Message;

import org.iii.ideas.appcross.viewpager.PagerAdapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class ChaptersAdapter extends PagerAdapter
{
	private SparseArray<View>	listChapter	= null;
	private Handler				theHandler	= null;

	public ChaptersAdapter()
	{
		super();
		// TODO Auto-generated constructor stub
		initData();
	}

	public ChaptersAdapter(Handler handler)
	{
		super();
		// TODO Auto-generated constructor stub
		initData();
		theHandler = handler;
	}

	private void initData()
	{
		listChapter = new SparseArray<View>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		// TODO Auto-generated method stub
		super.finalize();
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		// Log.d("pagereader", "count = " + listChapter.size());
		return listChapter.size();
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
		viewPager.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		// TODO Auto-generated method stub
		container.addView(listChapter.get(position));
		//notify(EventMessage.MSG_VIEW_INIT, position);
		return listChapter.get(position);
	}

	public void addChapterView(View chapterView)
	{
		listChapter.put(listChapter.size(), chapterView);
	}

	public View getChildView(int nIndex)
	{
		if (null == listChapter || 0 > nIndex || nIndex >= listChapter.size())
		{
			return null;
		}

		return listChapter.get(nIndex);
	}

	public void clear()
	{
		listChapter.clear();
	}

	public int size()
	{
		return listChapter.size();
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

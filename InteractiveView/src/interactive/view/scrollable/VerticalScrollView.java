package interactive.view.scrollable;

import interactive.common.Logs;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView
{

	private boolean								mbIsScrollBottom			= false;
	private boolean								mbIsScrollTop				= false;
	private SparseArray<OnScrollBottomListener>	listOnScrollBottomListener	= null;
	private SparseArray<OnScrollTopListener>	listOnScrollTopListener		= null;

	public interface OnScrollBottomListener
	{
		public void onScrollBottom();
	}

	public interface OnScrollTopListener
	{
		public void onScrollTop();
	}

	public VerticalScrollView(Context context)
	{
		super(context);
		init();
	}

	public VerticalScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public VerticalScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		View view = (View) getChildAt(getChildCount() - 1);
		int diff = (view.getBottom() - (getHeight() + getScrollY()));// Calculate the scrolldiff
		if (diff == 0)
		{
			if (mbIsScrollBottom)
			{
				mbIsScrollBottom = false;
				notifyScrolledBottom();
				return;
			}
			mbIsScrollBottom = true;
			//	super.onScrollChanged(l, t - 1, oldl, oldt);
			Logs.showTrace("VerticalScrollView scroll to bootom");
			return;
		}

		if (view.getTop() == getScrollY())
		{
			if (mbIsScrollTop)
			{
				mbIsScrollTop = false;
				notifyScrolledTop();
				return;
			}
			mbIsScrollTop = true;
			//		super.onScrollChanged(l, t + 1, oldl, oldt);
			Logs.showTrace("VerticalScrollView scroll to top");
			return;
		}

		mbIsScrollBottom = false;
		mbIsScrollTop = false;
		super.onScrollChanged(l, t, oldl, oldt);
	}

	private void init()
	{
		listOnScrollBottomListener = new SparseArray<OnScrollBottomListener>();
		listOnScrollTopListener = new SparseArray<OnScrollTopListener>();
	}

	public void setOnScrollBottomListener(VerticalScrollView.OnScrollBottomListener listener)
	{
		if (null != listOnScrollBottomListener && null != listener)
		{
			listOnScrollBottomListener.put(listOnScrollBottomListener.size(), listener);
		}
	}

	public void setOnScrollTopListener(VerticalScrollView.OnScrollTopListener listener)
	{
		if (null != listOnScrollTopListener && null != listener)
		{
			listOnScrollTopListener.put(listOnScrollTopListener.size(), listener);
		}
	}

	private void notifyScrolledBottom()
	{
		for (int i = 0; i < listOnScrollBottomListener.size(); ++i)
		{
			listOnScrollBottomListener.get(i).onScrollBottom();
		}
	}

	private void notifyScrolledTop()
	{
		for (int i = 0; i < listOnScrollTopListener.size(); ++i)
		{
			listOnScrollTopListener.get(i).onScrollTop();
		}
	}

}

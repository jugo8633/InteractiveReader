package interactive.view.scrollable;

import interactive.common.Logs;
import interactive.common.Type;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView
{
	private SparseArray<OnScrollBottomListener>	listOnScrollBottomListener	= null;
	private SparseArray<OnScrollTopListener>	listOnScrollTopListener		= null;
	private boolean								mbIsBottom					= false;
	private boolean								mbIsTop						= false;

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

	private void init()
	{
		listOnScrollBottomListener = new SparseArray<OnScrollBottomListener>();
		listOnScrollTopListener = new SparseArray<OnScrollTopListener>();
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
	{
		if (0 < scrollY && clampedY)
		{
			mbIsBottom = true;
		}
		else
		{
			mbIsBottom = false;
		}

		if (0 >= scrollY && clampedY)
		{
			mbIsTop = true;
		}
		else
		{
			mbIsTop = false;
		}

		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	public boolean getIsBottom()
	{
		return mbIsBottom;
	}

	public boolean getIsTop()
	{
		return mbIsTop;
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

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		super.onTouchEvent(ev);
		return true;
	}

}

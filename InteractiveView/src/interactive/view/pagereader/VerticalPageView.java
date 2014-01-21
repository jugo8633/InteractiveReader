package interactive.view.pagereader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VerticalPageView extends VerticalViewPager
{

	private boolean	mbPagingEnabled	= true;

	public VerticalPageView(Context context)
	{
		super(context);
	}

	public VerticalPageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (this.mbPagingEnabled)
		{
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		if (this.mbPagingEnabled)
		{
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	public void setPagingEnabled(boolean b)
	{
		this.mbPagingEnabled = b;
	}
}

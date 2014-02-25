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
		try
		{
			if (this.mbPagingEnabled)
			{
				return super.onTouchEvent(event);
			}
		}
		catch (IllegalArgumentException ex)
		{
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		try
		{
			if (this.mbPagingEnabled)
			{
				return super.onInterceptTouchEvent(event);
			}
		}
		catch (IllegalArgumentException ex)
		{
		}

		return false;
	}

	public void setPagingEnabled(boolean b)
	{
		this.mbPagingEnabled = b;
	}
}

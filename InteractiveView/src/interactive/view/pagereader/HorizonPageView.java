package interactive.view.pagereader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorizonPageView extends ViewPager
{
	private boolean	mbPagingEnabled	= true;

	public HorizonPageView(Context context)
	{
		super(context);
	}

	public HorizonPageView(Context context, AttributeSet attrs)
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

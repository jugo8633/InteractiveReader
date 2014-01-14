package interactive.view.scrollable;

import interactive.common.Logs;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView
{

	private boolean								mbIsScrollBottom			= false;
	private boolean								mbIsScrollTop				= false;
	private SparseArray<OnScrollBottomListener>	listOnScrollBottomListener	= null;
	private SparseArray<OnScrollTopListener>	listOnScrollTopListener		= null;
	private int									mnTrackY					= 0;
	private Runnable							scrollerTask;

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

//	@Override
//	protected void onScrollChanged(int l, int t, int oldl, int oldt)
//	{
//		mbIsScrollBottom = false;
//		mbIsScrollTop = false;
//
//		View view = (View) getChildAt(getChildCount() - 1);
//		int diff = (view.getBottom() - (getHeight() + getScrollY()));
//		if (diff == 0)
//		{
//			mbIsScrollBottom = true;
//			Logs.showTrace("VerticalScrollView scroll to bootom");
//		}
//
//		if (view.getTop() == getScrollY())
//		{
//			mbIsScrollTop = true;
//			Logs.showTrace("VerticalScrollView scroll to top");
//		}
//
//		super.onScrollChanged(l, t, oldl, oldt);
//	}

	private void init()
	{
		listOnScrollBottomListener = new SparseArray<OnScrollBottomListener>();
		listOnScrollTopListener = new SparseArray<OnScrollTopListener>();
		//	this.setOnTouchListener(onTouchListener);
		//	initScrollerTask();
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
	{
		Logs.showTrace("onOverScrolled scrollX=" + scrollX + " scrollY" + scrollY + " clampedX" + " clampedY"
				+ clampedY);
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
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

	private void initScrollerTask()
	{
		scrollerTask = new Runnable()
		{
			@Override
			public void run()
			{
				int newPosition = VerticalScrollView.this.getScrollY();
				if (mnTrackY - newPosition == 0)
				{
					if (mbIsScrollBottom)
					{
						notifyScrolledBottom();
					}
					if (mbIsScrollTop)
					{
						notifyScrolledTop();
					}
				}
				else
				{
					startScrollerTask();
				}
			}
		};
	}

	private void startScrollerTask()
	{
		mnTrackY = this.getScrollY();
		postDelayed(scrollerTask, 100);
	}

	OnTouchListener	onTouchListener	= new OnTouchListener()
									{

										@Override
										public boolean onTouch(View v, MotionEvent event)
										{
											switch (event.getAction())
											{
											case MotionEvent.ACTION_UP:
												if (mbIsScrollBottom || mbIsScrollTop)
												{
													startScrollerTask();
												}
												break;
											}
											return false;
										}
									};

}

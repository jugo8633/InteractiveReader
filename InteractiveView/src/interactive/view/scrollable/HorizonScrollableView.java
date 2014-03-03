package interactive.view.scrollable;

import interactive.common.EventMessage;
import interactive.view.global.Global;
import interactive.view.scroll.ScrollHandler;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class HorizonScrollableView extends HorizontalScrollView
{
	private int						mnOffsetX		= 0;
	private Context					theContext		= null;
	private ScrollHandler			scrollHandler	= null;
	private boolean					mbCurrentActive	= false;
	private ScrollableImageHandler	imageHandler	= null;

	public HorizonScrollableView(Context context)
	{
		super(context);
		init(context);
	}

	public HorizonScrollableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public HorizonScrollableView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
	{
		scrollHandler.setOverScrolled(scrollX, scrollY, clampedX, clampedY);
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	private void init(Context context)
	{
		theContext = context;
		this.setVerticalScrollBarEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		this.setOnTouchListener(touchListener);
		scrollHandler = new ScrollHandler(ScrollHandler.HORIZON);
		imageHandler = new ScrollableImageHandler(selfHandler);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new LayoutParams(nWidth, nHeight));
		imageHandler.setDisplay(nWidth, nHeight);
	}

	public void setImage(String strImagePath, int nWidth, int nHeight, int nOffsetX, int nOffsetY)
	{
		if (0 < nOffsetX)
		{
			setOffset(nOffsetX);
		}

		if (0 > nOffsetY)
		{
			setPadding(0 - nOffsetY);
		}

		ImageView imageView = new ImageView(theContext);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setLayoutParams(new LayoutParams(nWidth, nHeight));

		removeAllViewsInLayout();
		addView(imageView);

		imageHandler.setImage(imageView, strImagePath, nWidth, nHeight, nOffsetX, nOffsetY);
	}

	private void setPadding(int nTop)
	{
		this.setPadding(0, nTop, 0, 0);
	}

	private void setOffset(int nX)
	{
		mnOffsetX = nX;
	}

	public void setPosition(int nChapter, int nPage)
	{
		Global.addActiveNotify(nChapter, nPage, selfHandler);
		Global.addUnActiveNotify(nChapter, nPage, selfHandler);
		scrollHandler.setPosition(nChapter, nPage);
	}

	private void initOffset()
	{
		this.scrollTo(mnOffsetX, 0);
	}

	private Handler			selfHandler		= new Handler()
											{
												@Override
												public void handleMessage(Message msg)
												{
													switch (msg.what)
													{
													case EventMessage.MSG_CURRENT_ACTIVE:
														mbCurrentActive = true;
														imageHandler.runInitHorizonImage();
														break;
													case EventMessage.MSG_NOT_CURRENT_ACTIVE:
														if (mbCurrentActive)
														{
															imageHandler.releaseImage();
														}
														mbCurrentActive = false;
														break;
													case EventMessage.MSG_VIEW_INITED:
														initOffset();
														break;
													}
												}
											};

	private OnTouchListener	touchListener	= new OnTouchListener()
											{
												@Override
												public boolean onTouch(View v, MotionEvent event)
												{
													return scrollHandler.setTouchEvent(v, event);
												}
											};

}

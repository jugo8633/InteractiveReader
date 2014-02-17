package interactive.view.scrollable;

import interactive.common.BitmapHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.global.Global;
import interactive.view.scroll.ScrollHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
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
	private int				mnOffsetX		= 0;
	private int				mnWidth			= Type.INVALID;
	private Context			theContext		= null;
	private ScrollHandler	scrollHandler	= null;

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
		//this.setOverScrollMode(OVER_SCROLL_NEVER);
		this.setOnTouchListener(touchListener);
		scrollHandler = new ScrollHandler(ScrollHandler.HORIZON);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new LayoutParams(nWidth, nHeight));
		mnWidth = nWidth;
	}

	private ImageView getImageView(Bitmap bitmap, int nWidth, int nHeight)
	{
		ImageView imageView = new ImageView(theContext);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setLayoutParams(new LayoutParams(nWidth, nHeight));
		imageView.setImageBitmap(bitmap);
		return imageView;
	}

	public void setImage(String strImagePath, int nWidth, int nHeight, int nOffsetX, int nOffsetY)
	{
		Bitmap bmp = null;

		if ((nWidth - nOffsetX) < mnWidth)
		{
			Bitmap bitmapBack = Bitmap
					.createBitmap(nWidth + (mnWidth - (nWidth - nOffsetX)), nHeight, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(theContext, strImagePath, nWidth, nHeight);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, 0f, 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		//	this.setOverScrollMode(OVER_SCROLL_NEVER);
		}
		else if (0 > nOffsetX)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(nWidth + (0 - nOffsetX), nHeight, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(theContext, strImagePath, nWidth, nHeight);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, (0 - nOffsetX), 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		//	this.setOverScrollMode(OVER_SCROLL_NEVER);
		}
		else
		{
			bmp = BitmapHandler.readBitmap(theContext, strImagePath, nWidth, nHeight);
		}

		ImageView imageView = getImageView(bmp, nWidth, nHeight);
		bmp = null;

		if (0 < nOffsetX)
		{
			setOffset(nOffsetX);
		}

		if (0 > nOffsetY)
		{
			setPadding(0 - nOffsetY);
		}

		removeAllViewsInLayout();
		addView(imageView);
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
		Global.addActiveNotify(nChapter, nPage, notifyHandler);
		scrollHandler.setPosition(nChapter, nPage);
	}

	private void initOffset()
	{
		this.scrollTo(mnOffsetX, 0);
	}

	private Handler			notifyHandler	= new Handler()
											{
												@Override
												public void handleMessage(Message msg)
												{
													switch (msg.what)
													{
													case EventMessage.MSG_CURRENT_ACTIVE:
														initOffset();
														break;
													}
													super.handleMessage(msg);
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

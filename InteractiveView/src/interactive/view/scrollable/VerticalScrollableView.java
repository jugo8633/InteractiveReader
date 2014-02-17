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
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

public class VerticalScrollableView extends ScrollView
{

	private int				mnOffsetY		= 0;
	private int				mnWidth			= Type.INVALID;
	private int				mnHeight		= Type.INVALID;
	private Context			theContext		= null;
	private ScrollHandler	scrollHandler	= null;

	public VerticalScrollableView(Context context)
	{
		super(context);
		init(context);
	}

	public VerticalScrollableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public VerticalScrollableView(Context context, AttributeSet attrs, int defStyle)
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
		//		this.setOverScrollMode(OVER_SCROLL_NEVER);
		this.setOnTouchListener(touchListener);
		scrollHandler = new ScrollHandler(ScrollHandler.VERTICAL);
	}

	public void setPosition(int nChapter, int nPage)
	{
		Global.addActiveNotify(nChapter, nPage, notifyHandler);
		scrollHandler.setPosition(nChapter, nPage);
	}

	private void initOffset()
	{
		this.scrollTo(0, mnOffsetY);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new LayoutParams(nWidth, nHeight));
		mnWidth = nWidth;
		mnHeight = nHeight;
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

		if (0 > nOffsetY)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(nWidth, nHeight + (0 - nOffsetY), Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(theContext, strImagePath, nWidth, nHeight);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, 0f, (0 - nOffsetY));
			bitmapBack.recycle();
			bitmapFront.recycle();
		//	this.setOverScrollMode(OVER_SCROLL_NEVER);
		}
		else if (0 < nOffsetY && (nHeight - nOffsetY) < mnHeight)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(nWidth, nHeight + nOffsetY, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(theContext, strImagePath, nWidth, nHeight);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, 0f, 0f);
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

		if ((nWidth - nOffsetX) < mnWidth)
		{
			setPadding(0, 0, mnWidth - (nWidth - nOffsetX), 0);
			if (0 < nOffsetX)
			{
				imageView.setX(0 - nOffsetX);
			}
		}

		if (0 < nOffsetY)
		{
			setOffset(nOffsetY);
		}

		if (0 > nOffsetX)
		{
			setPadding(0 - nOffsetX);
		}

		removeAllViewsInLayout();
		addView(imageView);
	}

	private void setPadding(int nLeft)
	{
		this.setPadding(nLeft, 0, 0, 0);
	}

	private void setOffset(int nY)
	{
		mnOffsetY = nY;
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

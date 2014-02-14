package interactive.view.scrollable;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.global.Global;
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
	private int		mnOffsetX		= 0;
	private int		mnWidth			= Type.INVALID;
	private Context	theContext		= null;
	private boolean	mbOverScrolled	= false;
	private float	mnX				= Type.INVALID;
	private int		mnScrollX		= Type.INVALID;
	private int		mnChapter		= Type.INVALID;

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
		mbOverScrolled = clampedX;
		if (mbOverScrolled)
		{
			mnScrollX = scrollX;
		}
		else
		{
			mnScrollX = Type.INVALID;
		}
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	private void init(Context context)
	{
		theContext = context;
		this.setVerticalScrollBarEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		this.setOverScrollMode(OVER_SCROLL_NEVER);
		this.setOnTouchListener(touchListener);
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
		}
		else if (0 > nOffsetX)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(nWidth + (0 - nOffsetX), nHeight, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(theContext, strImagePath, nWidth, nHeight);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, (0 - nOffsetX), 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
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
		mnChapter = nChapter;
		Global.addActiveNotify(nChapter, nPage, notifyHandler);
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
													switch (event.getAction())
													{
													case MotionEvent.ACTION_DOWN:
														if (!mbOverScrolled)
														{
															mnX = Type.INVALID;
															EventHandler.notify(Global.handlerActivity,
																	EventMessage.MSG_LOCK_HORIZON, 0, 0, null);
														}
														else
														{
															mnX = event.getRawX();
														}
														break;
													case MotionEvent.ACTION_CANCEL:
													case MotionEvent.ACTION_UP:
														EventHandler.notify(Global.handlerActivity,
																EventMessage.MSG_UNLOCK_HORIZON, 0, 0, null);
														if (Type.INVALID != mnX)
														{
															float nX = event.getRawX();
															int nMove = (int) Math.abs(mnX - nX);
															if (10 <= nMove)
															{
																if (0 == mnScrollX)
																{
																	EventHandler.notify(Global.handlerActivity,
																			EventMessage.MSG_JUMP, mnChapter - 1,
																			Type.INVALID, null);
																}
																if (0 < mnScrollX)
																{
																	EventHandler.notify(Global.handlerActivity,
																			EventMessage.MSG_JUMP, mnChapter + 1,
																			Type.INVALID, null);
																}
															}
														}
														mnX = Type.INVALID;

														break;
													}
													return false;
												}
											};

}

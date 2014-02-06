package interactive.view.scrollable;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.Logs;
import interactive.common.Type;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

public class ScrollableView extends RelativeLayout
{

	public static final int			SCROLL_LEFT_END			= 0;
	public static final int			SCROLL_RIGHT_END		= 1;
	public static final int			SCROLL_TOP_END			= 2;
	public static final int			SCROLL_DOWN_END			= 3;
	public static final int			DOUBLE_CLICK			= 4;

	public static final int			SCROLL_TYPE_AUTO		= 0;
	public static final int			SCROLL_TYPE_VERTICAL	= 1;
	public static final int			SCROLL_TYPE_HORIZONTAL	= 2;
	private ImageView				imageView				= null;
	private HorizontalScrollView	horizontalScrollView	= null;
	private ScrollView				verticalScrollView		= null;
	private int						mnDisplayWidth			= Type.INVALID;
	private int						mnDisplayHeight			= Type.INVALID;
	private int						mnScrollX				= 0;
	private int						mnScrollY				= 0;
	private int						mnScrollType			= 0;
	private Handler					notifyHandler			= null;		// send message to notify state change
	private GestureDetector			gestureDetector			= null;
	private int						mnHScrollX				= 0;
	private int						mnHScrollY				= 0;
	private int						mnVScrollX				= 0;
	private int						mnVScrollY				= 0;
	private int						mnDisplayX				= 0;
	private int						mnDisplayY				= 0;
	private int						mnChapter				= Type.INVALID;
	private int						mnPage					= Type.INVALID;

	public ScrollableView(Context context)
	{
		super(context);
	}

	public ScrollableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ScrollableView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void initNotifyHandler(Handler handler)
	{
		notifyHandler = handler;
	}

	private void initImageView()
	{
		verticalScrollView = new ScrollView(getContext());
		verticalScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		verticalScrollView.addView(imageView);
		verticalScrollView.setSmoothScrollingEnabled(true);
		verticalScrollView.setVerticalScrollBarEnabled(false);
		verticalScrollView.setHorizontalScrollBarEnabled(false);
		verticalScrollView.setVerticalFadingEdgeEnabled(false);
		verticalScrollView.setHorizontalFadingEdgeEnabled(false);

		horizontalScrollView = new HorizontalScrollView(getContext());
		horizontalScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		horizontalScrollView.addView(verticalScrollView);
		horizontalScrollView.setSmoothScrollingEnabled(true);
		horizontalScrollView.setHorizontalFadingEdgeEnabled(false);
		horizontalScrollView.setHorizontalScrollBarEnabled(false);
		horizontalScrollView.setVerticalFadingEdgeEnabled(false);
		horizontalScrollView.setVerticalScrollBarEnabled(false);

		this.addView(horizontalScrollView);

		verticalScrollView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					mnVScrollX = verticalScrollView.getScrollX();
					mnVScrollY = verticalScrollView.getScrollY();
					break;
				}
				gestureDetector.onTouchEvent(event);
				return false;
			}
		});

		horizontalScrollView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					mnHScrollX = horizontalScrollView.getScrollX();
					mnHScrollY = horizontalScrollView.getScrollY();
					break;
				}
				gestureDetector.onTouchEvent(event);
				return false;
			}
		});

	}

	private void initVertical()
	{
		verticalScrollView = new ScrollView(getContext());
		verticalScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		verticalScrollView.addView(imageView);
		verticalScrollView.setSmoothScrollingEnabled(true);
		verticalScrollView.setVerticalScrollBarEnabled(false);
		verticalScrollView.setHorizontalScrollBarEnabled(false);
		verticalScrollView.setVerticalFadingEdgeEnabled(false);
		verticalScrollView.setHorizontalFadingEdgeEnabled(false);
		this.addView(verticalScrollView);

		verticalScrollView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					mnVScrollX = verticalScrollView.getScrollX();
					mnVScrollY = verticalScrollView.getScrollY();
					break;
				}
				gestureDetector.onTouchEvent(event);
				return false;
			}
		});
	}

	private void initHorizontal()
	{
		horizontalScrollView = new HorizontalScrollView(getContext());
		horizontalScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		horizontalScrollView.addView(imageView);
		horizontalScrollView.setSmoothScrollingEnabled(true);
		horizontalScrollView.setHorizontalFadingEdgeEnabled(false);
		horizontalScrollView.setHorizontalScrollBarEnabled(false);
		horizontalScrollView.setVerticalFadingEdgeEnabled(false);
		horizontalScrollView.setVerticalScrollBarEnabled(false);
		this.addView(horizontalScrollView);

		horizontalScrollView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					mnHScrollX = horizontalScrollView.getScrollX();
					mnHScrollY = horizontalScrollView.getScrollY();
					break;
				}
				gestureDetector.onTouchEvent(event);
				return false;
			}
		});
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
		mnDisplayX = nX;
		mnDisplayY = nY;
		mnDisplayWidth = nWidth;
		mnDisplayHeight = nHeight;
	}

	private void createHorizonScrollView(String strTag, String strPath, int nWidth, int nHeight, int nScrollType,
			int nOffsetX, int nOffsetY, ViewGroup container)
	{
		HorizonScrollableView hview = new HorizonScrollableView(getContext());
		hview.setPosition(mnChapter, mnPage);
		hview.setTag(strTag);
		hview.setDisplay(mnDisplayX, mnDisplayY, mnDisplayWidth, mnDisplayHeight);
		hview.setImage(strPath, nWidth, nHeight, nOffsetX, nOffsetY);
		container.addView(hview);
	}

	private void createVerticalScrollView(String strTag, String strPath, int nWidth, int nHeight, int nScrollType,
			int nOffsetX, int nOffsetY, ViewGroup container)
	{
		VerticalScrollableView vView = new VerticalScrollableView(getContext());
		vView.setPosition(mnChapter, mnPage);
		vView.setTag(strTag);
		vView.setDisplay(mnDisplayX, mnDisplayY, mnDisplayWidth, mnDisplayHeight);
		vView.setImage(strPath, nWidth, nHeight, nOffsetX, nOffsetY);
		container.addView(vView);
	}

	private void createAutoScrollView(String strTag, String strPath, int nWidth, int nHeight, int nScrollType,
			int nOffsetX, int nOffsetY, ViewGroup container)
	{
		if (mnDisplayWidth < nWidth && mnDisplayHeight >= nHeight)
		{
			createHorizonScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			return;
		}
		if (mnDisplayHeight < nHeight && mnDisplayWidth >= nWidth)
		{
			createVerticalScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			return;
		}

		AutoScrollableView autoView = new AutoScrollableView(getContext());
		autoView.setPosition(mnChapter, mnPage);
		autoView.setTag(strTag);
		autoView.setDisplay(mnDisplayX, mnDisplayY, mnDisplayWidth, mnDisplayHeight);
		autoView.setImage(strPath, nWidth, nHeight, nOffsetX, nOffsetY);
		container.addView(autoView);
	}

	public void setImage(String strTag, String strPath, int nWidth, int nHeight, int nScrollType, int nOffsetX,
			int nOffsetY, ViewGroup container)
	{
		switch (nScrollType)
		{
		case SCROLL_TYPE_HORIZONTAL:
			createHorizonScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			break;
		case SCROLL_TYPE_VERTICAL:
			createVerticalScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			break;
		case SCROLL_TYPE_AUTO:
			createAutoScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			break;
		}

		/** ========================================================================*/

		if (nScrollType > 4)
		{

			mnScrollType = nScrollType;
			int nPadingLeft = 0;
			int nPadingTop = 0;

			Bitmap bitmapOrg = BitmapFactory.decodeFile(strPath);
			int width = bitmapOrg.getWidth();
			int height = bitmapOrg.getHeight();

			if (0 >= width || 0 >= height)
			{
				Logs.showTrace("Invalid Bitmap Size");
				return;
			}
			// calculate the scale 
			float scaleWidth = ((float) nWidth) / width;
			float scaleHeight = ((float) nHeight) / height;

			// create a matrix for the manipulation
			Matrix matrix = new Matrix();
			// resize the bit map
			matrix.postScale(scaleWidth, scaleHeight);
			// rotate the Bitmap
			//matrix.postRotate(45);

			// recreate the new Bitmap
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);

			Bitmap bmp = BitmapHandler.readBitmap(strPath, nWidth, nHeight);

			imageView = new ImageView(getContext());
			imageView.setImageBitmap(bmp);
			//imageView.setImageURI(Uri.parse(strPath));
			//imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setLayoutParams(new LayoutParams(nWidth, nHeight));
			//imageView.setX(0 - nOffsetX);
			//imageView.setY(0 - nOffsetY);

			// 判斷直橫
			if (SCROLL_TYPE_AUTO == nScrollType)
			{
				if (mnDisplayWidth < nWidth && mnDisplayHeight >= nHeight)
				{
					// horizontal
					mnScrollType = SCROLL_TYPE_HORIZONTAL;
				}

				if (mnDisplayHeight < nHeight && mnDisplayWidth >= nWidth)
				{
					// vertical
					mnScrollType = SCROLL_TYPE_VERTICAL;
				}

			}

			if (0 > nOffsetX)
			{
				nPadingLeft = 0 - nOffsetX;
			}
			if (0 > nOffsetY)
			{
				nPadingTop = 0 - nOffsetY;
			}
			this.setPadding(nPadingLeft, nPadingTop, 0, 0);

			gestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);

			switch (mnScrollType)
			{
			case SCROLL_TYPE_AUTO:
				if (0 < nOffsetX)
				{
					mnScrollX = nOffsetX;
				}
				if (0 < nOffsetY)
				{
					mnScrollY = nOffsetY;
				}
				initImageView();
				break;
			case SCROLL_TYPE_VERTICAL:
				if (0 < nOffsetY)
				{
					mnScrollY = nOffsetY;
				}
				initVertical();
				break;
			case SCROLL_TYPE_HORIZONTAL:
				if (0 < nOffsetX)
				{
					mnScrollX = nOffsetX;
				}
				initHorizontal();
				break;
			}

			this.bringToFront();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		switch (mnScrollType)
		{
		case SCROLL_TYPE_AUTO:
			if (null != verticalScrollView)
			{
				verticalScrollView.smoothScrollTo(0, mnScrollY);
			}
			if (null != horizontalScrollView)
			{
				horizontalScrollView.smoothScrollTo(mnScrollX, 0);
			}
			break;
		case SCROLL_TYPE_VERTICAL:
			if (null != verticalScrollView)
			{
				verticalScrollView.smoothScrollTo(0, mnScrollY);
			}
			break;
		case SCROLL_TYPE_HORIZONTAL:
			if (null != horizontalScrollView)
			{
				horizontalScrollView.smoothScrollTo(mnScrollX, 0);
			}
			break;
		}

		super.onWindowFocusChanged(hasWindowFocus);
	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
	}

	SimpleOnGestureListener	simpleOnGestureListener	= new SimpleOnGestureListener()
													{

														@Override
														public boolean onFling(MotionEvent e1, MotionEvent e2,
																float velocityX, float velocityY)
														{
															if (null == e1 || null == e2)
															{
																return super.onFling(e1, e2, velocityX, velocityY);
															}
															float sensitvity = 50;
															int nHScrollEnd = 0;
															int nVScrollEnd = 0;

															if (null != horizontalScrollView)
															{
																nHScrollEnd = horizontalScrollView.getScrollX();
																nVScrollEnd = horizontalScrollView.getScrollY();
															}
															else if (null != verticalScrollView)
															{
																nHScrollEnd = verticalScrollView.getScrollX();
																nVScrollEnd = verticalScrollView.getScrollY();
															}

															if ((e1.getX() - e2.getX()) > sensitvity)
															{
																// left
																if ((null != horizontalScrollView && mnHScrollX == nHScrollEnd)
																		|| (null != verticalScrollView && mnVScrollX == nHScrollEnd))
																{
																	EventHandler.notify(notifyHandler,
																			ScrollableView.SCROLL_LEFT_END, 0, 0, null);
																}
															}
															else if ((e2.getX() - e1.getX()) > sensitvity)
															{
																// right
																if ((null != horizontalScrollView && mnHScrollX == nHScrollEnd)
																		|| (null != verticalScrollView && mnVScrollX == nHScrollEnd))
																{
																	EventHandler
																			.notify(notifyHandler,
																					ScrollableView.SCROLL_RIGHT_END, 0,
																					0, null);
																}
															}

															if ((e1.getY() - e2.getY()) > sensitvity)
															{
																// up
																if ((null != horizontalScrollView && mnHScrollY == nVScrollEnd)
																		|| (null != verticalScrollView && mnVScrollY == nVScrollEnd))
																{
																	EventHandler.notify(notifyHandler,
																			ScrollableView.SCROLL_TOP_END, 0, 0, null);
																}

															}
															else if ((e2.getY() - e1.getY()) > sensitvity)
															{
																// down
																if ((null != horizontalScrollView && mnHScrollY == nVScrollEnd)
																		|| (null != verticalScrollView && mnVScrollY == nVScrollEnd))
																{
																	EventHandler.notify(notifyHandler,
																			ScrollableView.SCROLL_DOWN_END, 0, 0, null);
																}

															}
															return super.onFling(e1, e2, velocityX, velocityY);
														}

														@Override
														public boolean onDoubleTap(MotionEvent e)
														{
															EventHandler.notify(notifyHandler,
																	ScrollableView.DOUBLE_CLICK, 0, 0, null);
															return super.onDoubleTap(e);
														}

													};
}

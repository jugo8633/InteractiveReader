package interactive.view.scrollable;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ScrollImageView extends View
{

	private final int	DEFAULT_PADDING	= 10;
	private Display		mDisplay;
	private Bitmap		mImage;

	/* Current x and y of the touch */
	private float		mCurrentX		= 0;
	private float		mCurrentY		= 0;

	private float		mTotalX			= 0;
	private float		mTotalY			= 0;

	/* The touch distance change from the current touch */
	private float		mDeltaX			= 0;
	private float		mDeltaY			= 0;

	int					mDisplayWidth;
	int					mDisplayHeight;
	int					mPadding;

	public ScrollImageView(Context context)
	{
		super(context);
		initScrollImageView(context);
	}

	public ScrollImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initScrollImageView(context);
	}

	public ScrollImageView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initScrollImageView(context);
	}

	private void initScrollImageView(Context context)
	{
		WindowManager windowManager;
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mDisplay = windowManager.getDefaultDisplay();
		mPadding = DEFAULT_PADDING;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int nWidth;
		DisplayMetrics metrics = new DisplayMetrics();
		mDisplay.getMetrics(metrics);
		nWidth = metrics.widthPixels;
		metrics = null;

		int nHeight;
		metrics = new DisplayMetrics();
		mDisplay.getMetrics(metrics);
		nHeight = metrics.heightPixels;
		metrics = null;

		int width = measureDim(widthMeasureSpec, nWidth);
		int height = measureDim(heightMeasureSpec, nHeight);
		setMeasuredDimension(width, height);
	}

	private int measureDim(int measureSpec, int size)
	{
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY)
		{
			result = specSize;
		}
		else
		{
			result = size;
			if (specMode == MeasureSpec.AT_MOST)
			{
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	public Bitmap getImage()
	{
		return mImage;
	}

	public void setImage(Bitmap image)
	{
		mImage = image;
	}

	public int getPadding()
	{
		return mPadding;
	}

	public void setPadding(int padding)
	{
		this.mPadding = padding;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
			mCurrentX = event.getRawX();
			mCurrentY = event.getRawY();
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			float x = event.getRawX();
			float y = event.getRawY();

			// Update how much the touch moved
			mDeltaX = x - mCurrentX;
			mDeltaY = y - mCurrentY;

			mCurrentX = x;
			mCurrentY = y;

			invalidate();
		}
		else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
		}
		// Consume event
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (mImage == null)
		{
			return;
		}

		float newTotalX = mTotalX + mDeltaX;
		// Don't scroll off the left or right edges of the bitmap.
		if (mPadding > newTotalX && newTotalX > getMeasuredWidth() - mImage.getWidth() - mPadding)
			mTotalX += mDeltaX;

		float newTotalY = mTotalY + mDeltaY;
		// Don't scroll off the top or bottom edges of the bitmap.
		if (mPadding > newTotalY && newTotalY > getMeasuredHeight() - mImage.getHeight() - mPadding)
			mTotalY += mDeltaY;

		Paint paint = new Paint();
		canvas.drawBitmap(mImage, mTotalX, mTotalY, paint);
	}

}

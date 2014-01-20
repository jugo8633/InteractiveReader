package interactive.view.postcard;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FingerPaintView extends View
{

	//定義繪圖的基本參數
	private float					mfPaintWidth				= 8f;
	private int						mnPaintColor				= Color.BLUE;
	private boolean					mCapturing					= false;
	private Bitmap					mBmpPaint					= null;

	//定義防止線條有鋸齒的常數
	private static final boolean	GESTURE_RENDERING_ANTIALIAS	= true;
	private static final boolean	DITHER_FLAG					= true;

	private Paint					mPaint						= new Paint();
	private Path					mPath						= new Path();

	//矩形
	private final Rect				mInvalidRect				= new Rect();

	private float					mX;
	private float					mY;

	private float					mCurveEndX;
	private float					mCurveEndY;

	private int						mInvalidateExtraBorder		= 10;

	public FingerPaintView(Context context)
	{
		super(context);
		init();
	}

	public FingerPaintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public FingerPaintView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init()
	{
		setWillNotDraw(false);

		mPaint.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
		mPaint.setColor(mnPaintColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(mfPaintWidth);
		mPaint.setDither(DITHER_FLAG);
		mPath.reset();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (mBmpPaint != null)
		{
			canvas.drawBitmap(mBmpPaint, null, new Rect(0, 0, getWidth(), getHeight()), null);
		}
		else
		{
			canvas.drawPath(mPath, mPaint);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (mCapturing)
		{
			processEvent(event);
			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean processEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			touchDown(event);
			invalidate();
			return true;

		case MotionEvent.ACTION_MOVE:
			Rect rect = touchMove(event);
			if (rect != null)
			{
				invalidate(rect);
			}
			return true;

		case MotionEvent.ACTION_UP:
			touchUp(event, false);
			invalidate();
			return true;

		case MotionEvent.ACTION_CANCEL:
			touchUp(event, true);
			invalidate();
			return true;
		}
		return false;
	}

	private void touchUp(MotionEvent event, boolean b)
	{
	}

	private Rect touchMove(MotionEvent event)
	{
		Rect areaToRefresh = null;

		final float x = event.getX();
		final float y = event.getY();

		final float previousX = mX;
		final float previousY = mY;

		areaToRefresh = mInvalidRect;

		// start with the curve end 
		final int border = mInvalidateExtraBorder;
		areaToRefresh.set((int) mCurveEndX - border, (int) mCurveEndY - border, (int) mCurveEndX + border,
				(int) mCurveEndY + border);

		float cX = mCurveEndX = (x + previousX) / 2;
		float cY = mCurveEndY = (y + previousY) / 2;

		mPath.quadTo(previousX, previousY, cX, cY);

		// union with the control point of the new curve 
		areaToRefresh.union((int) previousX - border, (int) previousY - border, (int) previousX + border,
				(int) previousY + border);

		// union with the end point of the new curve 
		areaToRefresh.union((int) cX - border, (int) cY - border, (int) cX + border, (int) cY + border);

		mX = x;
		mY = y;

		return areaToRefresh;
	}

	private void touchDown(MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();

		mX = x;
		mY = y;
		mPath.moveTo(x, y);

		final int border = mInvalidateExtraBorder;
		mInvalidRect.set((int) x - border, (int) y - border, (int) x + border, (int) y + border);

		mCurveEndX = x;
		mCurveEndY = y;
	}

	/** 
	 * Erases the signature. 
	 */
	public void clear()
	{
		mBmpPaint = null;
		mPath.rewind();
		// Repaints the entire view. 
		invalidate();
	}

	public boolean isCapturing()
	{
		return mCapturing;
	}

	public void setIsCapturing(boolean mCapturing)
	{
		this.mCapturing = mCapturing;
	}

	public void setBitmapPaint(Bitmap bmpPaint)
	{
		mBmpPaint = bmpPaint;
		invalidate();
	}

	public Bitmap getBitmapPaint()
	{
		if (mBmpPaint != null)
		{
			return mBmpPaint;
		}
		else if (mPath.isEmpty())
		{
			return null;
		}
		else
		{
			Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bmp);
			c.drawPath(mPath, mPaint);
			return bmp;
		}
	}

	public void setPaintWidth(float width)
	{
		mfPaintWidth = width;
		mPaint.setStrokeWidth(mfPaintWidth);
		invalidate();
	}

	public float getPaintWidth()
	{
		return mPaint.getStrokeWidth();
	}

	public void setPaintColor(int color)
	{
		mnPaintColor = color;
	}

	/** 
	 * @return the byte array representing the paint as a PNG file format 
	 */
	public byte[] getSignaturePNG()
	{
		return getPaintBytes(CompressFormat.PNG, 0);
	}

	/** 
	 * @param quality Hint to the compressor, 0-100. 0 meaning compress for small 
	 *            size, 100 meaning compress for max quality. 
	 * @return the byte array representing the paint as a JPEG file format 
	 */
	public byte[] getPaintJPEG(int quality)
	{
		return getPaintBytes(CompressFormat.JPEG, quality);
	}

	private byte[] getPaintBytes(CompressFormat format, int quality)
	{
		Bitmap bmp = getBitmapPaint();
		if (bmp == null)
		{
			return null;
		}
		else
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			getBitmapPaint().compress(format, quality, stream);

			return stream.toByteArray();
		}
	}
}

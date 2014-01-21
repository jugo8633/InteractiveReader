package interactive.view.postcard;

import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;

public class FingerPaintView extends View
{

	private Bitmap				mBitmap			= null;
	private Canvas				mCanvas			= null;
	private Paint				mPaint			= null;
	private Path				mPath			= null;
	private Paint				mBitmapPaint	= null;
	private boolean				mbEraser		= false;
	private float				mX, mY;
	private static final float	TOUCH_TOLERANCE	= 4;
	private boolean				mbCapturing		= false;
	private Paint				mpaintEraser	= null;

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
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(8);

		mpaintEraser = new Paint();
		mpaintEraser.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
		mpaintEraser.setAntiAlias(true);
		mpaintEraser.setDither(true);
		mpaintEraser.setStyle(Paint.Style.STROKE);
		mpaintEraser.setStrokeJoin(Paint.Join.ROUND);
		mpaintEraser.setStrokeCap(Paint.Cap.ROUND);
		mpaintEraser.setStrokeWidth(24);

		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

		if (mbEraser)
		{
			mCanvas.drawPath(mPath, mpaintEraser);
		}
		else
		{
			canvas.drawPath(mPath, mPaint);
		}
	}

	private void touch_start(float x, float y)
	{
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y)
	{
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
		{
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up()
	{
		if (!mbEraser)
		{
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
		}
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (mbCapturing)
		{
			processEvent(event);
			return true;
		}
		else
		{
			return false;
		}
	}

	private void processEvent(MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
	}

	public void clear()
	{
		mPath.rewind();
		invalidate();
	}

	public boolean isCapturing()
	{
		return mbCapturing;
	}

	public void setIsCapturing(boolean mCapturing)
	{
		this.mbCapturing = mCapturing;
	}

	public void setEraser(boolean bEraser)
	{
		mbEraser = bEraser;
	}

	public void setBackground(String strImagePath)
	{
		setBackground(Drawable.createFromPath(strImagePath));
	}

	public boolean exportBitmap(String strPath)
	{
		int nWidth = getLayoutParams().width;
		int nHeight = getLayoutParams().height;

		Bitmap bitmap = Bitmap.createBitmap(nWidth, nHeight, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		measure(MeasureSpec.makeMeasureSpec(getLayoutParams().width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(getLayoutParams().height, MeasureSpec.EXACTLY));
		layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
		draw(c);

		FileOutputStream out;
		try
		{
			out = new FileOutputStream(strPath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}

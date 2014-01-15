package interactive.view.postcardorg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

public class FingerPaintView extends View
{
	private static final float	MINP	= 0.25f;
	private static final float	MAXP	= 0.75f;

	private Bitmap				mBitmap;
	private Canvas				mCanvas;
	private Paint				mPaint;
	private Path				mPath;
	private Paint				mBitmapPaint;
	private FingerPaint			fp;

	public FingerPaintView(Context context, FingerPaint fp)
	{
		super(context);
		// TODO Auto-generated constructor stub
		//
		this.fp = fp;
		this.mPaint = fp.getPaint();
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
		//canvas.drawColor(0xFFAAAAAA);
		canvas.drawColor(Color.TRANSPARENT);

		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

		if (fp.getMode() == 4)
		{
			Paint paint = new Paint();
			paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
			paint.setAntiAlias(true);
			paint.setDither(true);

			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeWidth(12);
			mCanvas.drawPath(mPath, paint);
		}
		else if (fp.getMode() == 6)
		{
			canvas.drawPath(mPath, mPaint);
		}

	}

	private float				mX, mY;
	private static final float	TOUCH_TOLERANCE	= 4;

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

		if (fp.getMode() != 4)
		{
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
		}

		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			//	MainActivity.locked = true;
			if (fp.getMode() == 0)
			{
				return true;
			}
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (fp.getMode() == 0)
			{
				return true;
			}
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			//		MainActivity.locked = false;
			if (fp.getMode() == 0)
			{
				return true;
			}
			touch_up();
			invalidate();
			break;
		}
		return true;
	}
}

package interactive.view.image;

import interactive.common.Logs;
import interactive.common.Type;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;

public class ScalableImageView extends ImageView
{

	public static final int			IMAGE_CLICK_TYPE_LONG_PRESS	= 100;
	public static final int			IMAGE_SCALE_EVENT_SPREAD	= 201;			
	public static final int			IMAGE_SCALE_EVENT_PINCH		= 202;			
	public static final int			IMAGE_DISPLAY_PARTIAL		= 0;			
	public static final int			IMAGE_DISPLAY_POPUP			= 1;			
	public static final int			IMAGE_DISPLAY_FULL			= 2;			

	private int						mnClickType					= Type.INVALID;
	private int						mnScaleEvent				= Type.INVALID;
	private int						mnDisplay					= Type.INVALID;

	private Matrix					matrix						= new Matrix();

	// mode can be in one of these 3 states
	private static final int		NONE						= 0;
	private static final int		DRAG						= 1;
	private static final int		ZOOM						= 2;
	private int						mode						= NONE;

	private static final int		CLICK						= 3;

	// Remember some things
	private PointF					last						= new PointF();
	private PointF					start						= new PointF();
	private float					minScale					= 1f;			// default
	private float					maxScale					= 3f;			// default
	private float					minScaleTemp;								// measure
	private float					maxScaleTemp;								// measure
	private float[]					m;
	private float					redundantXSpace, redundantYSpace;
	private float					width, height;
	private float					nowScale					= 1f;
	private float					origWidth, origHeight, imageWidth, imageHeight, redundantWidth, redundantHeight;
	private boolean					fit							= false;

	private ScaleGestureDetector	mScaleDetector;
	private ImageView				thisView					= null;

	public ScalableImageView(Context context)
	{
		super(context);
		initScalableImageView(context);
	}

	public ScalableImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initScalableImageView(context);
	}

	public ScalableImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initScalableImageView(context);
	}

	private void initScalableImageView(Context context)
	{
		thisView = this;
		super.setScaleType(ScaleType.CENTER);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		matrix.setTranslate(1f, 1f);
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
		setOnTouchListener(new DragListener());
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	@Override
	public void setImageBitmap(Bitmap bm)
	{
		imageWidth = bm.getWidth();
		imageHeight = bm.getHeight();
		super.setImageBitmap(bm);
	}

	@Override
	public void setImageDrawable(Drawable drawable)
	{
		super.setImageDrawable(drawable);
		imageWidth = drawable.getIntrinsicWidth();
		imageHeight = drawable.getIntrinsicHeight();
	}

	@Override
	public void setImageResource(int resourceID)
	{
		super.setImageResource(resourceID);
		imageWidth = getResources().getDrawable(resourceID).getIntrinsicWidth();
		imageHeight = getResources().getDrawable(resourceID).getIntrinsicHeight();
	}

	public void setImageSize(int nWidth, int nHeight)
	{
		imageWidth = nWidth;
		imageHeight = nHeight;
	}

	public void setMaxZoom(float x)
	{
		this.maxScale = x;
	}

	public void setMinZoom(float x)
	{
		this.minScale = x;
	}

	public void setFit(boolean fit)
	{
		this.fit = fit;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		float scale;
		if (fit)
		{
			float scaleX = (float) width / (float) imageWidth;
			float scaleY = (float) height / (float) imageHeight;
			scale = Math.min(scaleX, scaleY);
			matrix.setScale(scale, scale);
		}
		else
		{
			scale = 1;
		}
		minScaleTemp = minScale * scale;
		maxScaleTemp = maxScale * scale;

		setImageMatrix(matrix);
		nowScale = scale;

		// Center the image
		redundantHeight = (scale * (float) imageHeight);
		redundantWidth = (scale * (float) imageWidth);
		redundantYSpace = (float) height - (scale * (float) imageHeight);
		redundantXSpace = (float) width - redundantWidth;
		redundantYSpace /= (float) 2;
		redundantXSpace /= (float) 2;

		matrix.getValues(m);
		float x = m[Matrix.MTRANS_X];
		float y = m[Matrix.MTRANS_Y];
		matrix.postTranslate(redundantXSpace - x, redundantYSpace - y);

		origWidth = width - 2 * redundantXSpace;
		origHeight = height - 2 * redundantYSpace;
		setImageMatrix(matrix);
	}

	private class DragListener implements OnTouchListener
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			mScaleDetector.onTouchEvent(event);
			matrix.getValues(m);
			float x = m[Matrix.MTRANS_X];
			float y = m[Matrix.MTRANS_Y];
			PointF curr = new PointF(event.getX(), event.getY());

			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				last.set(event.getX(), event.getY());
				start.set(last);
				if (nowScale * imageWidth > width * 1.05 || nowScale * imageHeight > height * 1.05)
				{
					mode = DRAG;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = curr.x - last.x;
				float deltaY = curr.y - last.y;
				if (mode == DRAG)
				{
					float scaleWidth = Math.round(imageWidth * nowScale);
					float scaleHeight = Math.round(imageHeight * nowScale);
					// x
					if (scaleWidth < width)
					{// ���W�L��خɸm��
						deltaX = redundantXSpace - x - (imageWidth * nowScale - redundantWidth) / 2;
					}
					else
					{
						if (deltaX > 0 && x + deltaX > 0)
						{// ���k��&&����ԹL�Y
							deltaX = -x;
						}
						else if (deltaX < 0 && x + deltaX + scaleWidth < width)
						{// ������&&�k��ԹL�Y
							deltaX = width - x - scaleWidth;
						}
					}
					// y
					if (scaleHeight < height)
					{// ���W�L��خɸm��
						deltaY = redundantYSpace - y - (imageHeight * nowScale - redundantHeight) / 2;
					}
					else
					{
						if (deltaY > 0 && y + deltaY > 0)
						{// ���U��&&�W��ԹL�Y
							deltaY = -y;
						}
						else if (deltaY < 0 && y + deltaY + scaleHeight < height)
						{// ���W��&&�U��ԹL�Y
							deltaY = height - y - scaleHeight;
						}
					}
					matrix.postTranslate(deltaX, deltaY);
					last.set(curr.x, curr.y);
				}
				break;

			case MotionEvent.ACTION_UP:
				mode = NONE;
				int xDiff = (int) Math.abs(curr.x - start.x);
				int yDiff = (int) Math.abs(curr.y - start.y);
				if (xDiff < CLICK && yDiff < CLICK)
					performClick();
				break;

			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			}
			setImageMatrix(matrix);
			invalidate();
			return true;
		}
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			thisView.getParent().requestDisallowInterceptTouchEvent(true);
			mode = ZOOM;
			last.set(detector.getFocusX(), detector.getFocusY());
			start.set(last);
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			thisView.getParent().requestDisallowInterceptTouchEvent(false);
			super.onScaleEnd(detector);
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			float mScaleFactor = detector.getScaleFactor();
			float origScale = nowScale;
			nowScale *= mScaleFactor;
			if (nowScale > maxScaleTemp)
			{
				nowScale = maxScaleTemp;
				mScaleFactor = maxScaleTemp / origScale;
			}
			else if (nowScale < minScaleTemp)
			{
				nowScale = minScaleTemp;
				mScaleFactor = minScaleTemp / origScale;
			}
			if (origWidth * nowScale <= width || origHeight * nowScale <= height)
			{
				matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2);
			}
			else
			{
				matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
			}
			matrix.getValues(m);
			float x = m[Matrix.MTRANS_X];
			float y = m[Matrix.MTRANS_Y];
			float scaleWidth = Math.round(imageWidth * nowScale);
			float scaleHeight = Math.round(imageHeight * nowScale);
			if (nowScale < origScale)
			{// �Y�p�ɸm��
				if (x > 0 && x + scaleWidth > width)
				{// �u�����䦳�Ż�
					matrix.postTranslate(-x, 0);
				}
				else if (x < 0 && x + scaleWidth < width)
				{// �u���k�䦳�Ż�
					matrix.postTranslate(width - x - scaleWidth, 0);
				}
				else if (x > 0 && x + scaleWidth < width)
				{// ���䳣���Ż�
					matrix.postTranslate((width - scaleWidth) / 2 - x, 0);
				}
				if (y > 0 && y + scaleHeight > height)
				{// �u���W�䦳�Ż�
					matrix.postTranslate(0, -y);
				}
				else if (y < 0 && y + scaleHeight < height)
				{// �u���U�䦳�Ż�
					matrix.postTranslate(0, height - y - scaleHeight);
				}
				else if (y > 0 && y + scaleHeight < height)
				{// ���䳣���Ż�
					matrix.postTranslate(0, (height - scaleHeight) / 2 - y);
				}
			}

			PointF curr = new PointF(detector.getFocusX(), detector.getFocusY());
			float deltaX = curr.x - last.x;
			float deltaY = curr.y - last.y;
			last.set(curr.x, curr.y);
			// x
			if (scaleWidth < width)
			{// ���W�L��خɸm��
				deltaX = redundantXSpace - x - (imageWidth * nowScale - redundantWidth) / 2;
				return true;// �Y��o��w�g�m���L�F�A����
			}
			else
			{
				if (deltaX > 0 && x + deltaX > 0)
				{// ���k��&&����ԹL�Y
					deltaX = -x;
				}
				else if (deltaX < 0 && x + deltaX + scaleWidth < width)
				{// ������&&�k��ԹL�Y
					deltaX = width - x - scaleWidth;
				}
			}
			// y
			if (scaleHeight < height)
			{// ���W�L��خɸm��
				deltaY = redundantYSpace - y - (imageHeight * nowScale - redundantHeight) / 2;
				return true;// �Y��o��w�g�m���L�F�A����
			}
			else
			{
				if (deltaY > 0 && y + deltaY > 0)
				{// ���U��&&�W��ԹL�Y
					deltaY = -y;
				}
				else if (deltaY < 0 && y + deltaY + scaleHeight < height)
				{// ���W��&&�U��ԹL�Y
					deltaY = height - y - scaleHeight;
				}
			}
			matrix.postTranslate(deltaX, deltaY);
			return true;
		}
	}

	public void setImageScaleMode(int nClickType, int nScaleEvent, int nDisplay)
	{
		mnClickType = nClickType;
		mnScaleEvent = nScaleEvent;
		mnDisplay = nDisplay;

		Logs.showTrace("set image scale mode type=" + mnClickType + " scale=" + mnScaleEvent + " display=" + mnDisplay);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new ViewGroup.LayoutParams(nWidth, nHeight));
	}
}

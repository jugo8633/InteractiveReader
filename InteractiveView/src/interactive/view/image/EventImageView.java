package interactive.view.image;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.view.animation.zoom.ZoomHandler;
import interactive.view.global.Global;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class EventImageView extends ImageView
{
	private ScaleGestureDetector	scaleGestureDetector	= null;
	private ZoomHandler				zoomHandler				= null;

	public EventImageView(Context context)
	{
		super(context);
		init(context);
	}

	public EventImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public EventImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		zoomHandler = new ZoomHandler(context);
		this.setScaleType(ScaleType.CENTER_CROP);
		scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
		this.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
					break;
				}
				return scaleGestureDetector.onTouchEvent(event);
			}
		});
	}

	public void setZoomAction(ViewGroup viewParent)
	{
		//		scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleZoomHandler.getScaleGestureListener(
		//				getContext(), viewParent, this));
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
	}

	public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener
	{
		public ScaleGestureListener()
		{
			super();
		}

		private int	mnZoomSize	= 0;

		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			float factor = detector.getScaleFactor();

			Logs.showTrace("factor=" + factor + " mnZoomSize=" + mnZoomSize + " ##########################");
			if (1.0 < factor && 3.0 > factor && 2 != mnZoomSize)
			{
				mnZoomSize = 2;
				zoomHandler.zoomOut(EventImageView.this, mnZoomSize);
				Logs.showTrace("2 ##################################");
			}
			//			else if (3.0 <= factor && 4 != mnZoomSize)
			//			{
			//				mnZoomSize = 4;
			//				zoomHandler.zoomOut(EventImageView.this, mnZoomSize);
			//				Logs.showTrace("4 ##################################");
			//			}
			else if (0.8 > factor && 1 != mnZoomSize)
			{
				mnZoomSize = 1;
				zoomHandler.zoomOut(EventImageView.this, 1 / mnZoomSize);
				Logs.showTrace("1 ##################################");
			}
			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			Logs.showTrace("begin ########################");
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{

		}
	}
}

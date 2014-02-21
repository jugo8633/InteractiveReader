package interactive.view.image;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.animation.move.MoveHandler;
import interactive.view.animation.zoom.ZoomHandler;
import interactive.view.define.InteractiveDefine;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveImageData;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class EventImageView extends ImageView
{
	private Runnable							runRemoveImage			= null;
	private final int							EVENT_IMAGE_CLOSE		= 1;
	private ScaleGestureDetector				scaleGestureDetector	= null;
	private ScaleGestureDetector				scaleGestureEventImage	= null;
	private GestureDetector						gestureDetector			= null;
	private ZoomHandler							zoomHandler				= null;
	private SparseArray<Gesture>				listGesture				= null;
	private boolean								mbGestureSpread			= false;
	private boolean								mbGesturePinch			= false;
	private boolean								mbGestureLongPress		= false;
	private Bitmap								mBitmapSrc				= null;
	private Bitmap								mBitmapEvent			= null;
	private int									mnX						= Type.INVALID;
	private int									mnY						= Type.INVALID;
	private int									mnWidth					= Type.INVALID;
	private int									mnHeight				= Type.INVALID;
	private ImageView							imgScale				= null;
	private ImageView							imgEvent				= null;
	private SparseArray<InteractiveImageData>	listImageData			= null;
	private ViewGroup							container				= null;

	@SuppressWarnings("unused")
	private class Gesture
	{
		public int		mnDisplay		= Type.INVALID;
		public int		mnEvent			= Type.INVALID;
		public String	mstrTargetID	= null;
		public int		mnTargetType	= Type.INVALID;
		public int		mnType			= Type.INVALID;

		public Gesture(int nDisplay, int nEvent, String strTargetID, int nTargetType, int nType)
		{
			mnDisplay = nDisplay;
			mnEvent = nEvent;
			mstrTargetID = strTargetID;
			mnTargetType = nTargetType;
			mnType = nType;
		}
	}

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
		listGesture = new SparseArray<Gesture>();
		this.setScaleType(ScaleType.CENTER_CROP);
		zoomHandler = new ZoomHandler(getContext());
		zoomHandler.setNotifyHandler(selfHandler);
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

		this.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				if (mbGestureLongPress)
				{
					if (createScaleImage())
					{
						handleEvent();
					}
					return true;
				}
				return false;
			}
		});

		runRemoveImage = new Runnable()
		{
			@Override
			public void run()
			{
				removeEventImage();
			}
		};

		gestureDetector = new GestureDetector(getContext(), new GestureListener());
	}

	public void setContainer(ViewGroup viewParent)
	{
		container = viewParent;
	}

	public void setPosition(int nChapter, int nPage)
	{

	}

	public void setBitmap(String strPath, int nWidth, int nHeight)
	{
		mBitmapSrc = BitmapHandler.readBitmap(getContext(), strPath, nWidth, nHeight);
		this.setImageBitmap(mBitmapSrc);
	}

	public void addGesture(int nDisplay, int nEvent, String strTargetID, int nTargetType, int nType)
	{
		listGesture.put(listGesture.size(), new Gesture(nDisplay, nEvent, strTargetID, nTargetType, nType));
		switch (nType)
		{
		case InteractiveDefine.IMAGE_GESTURE_LONG_PRESS: // 長按
			mbGestureLongPress = true;
			break;
		case InteractiveDefine.IMAGE_GESTURE_PINCH: // 手勢縮小
			mbGesturePinch = true;
			break;
		case InteractiveDefine.IMAGE_GESTURE_SPREAD: // 手勢放大
			mbGestureSpread = true;
			break;
		}
	}

	public void addEventTargetImage(String strName, String strSrc, String strGroupID, int nX, int nY, int nWidth,
			int nHeight, boolean bIsVisible)
	{
		if (null == listImageData)
		{
			listImageData = new SparseArray<InteractiveImageData>();
		}
		listImageData.put(listImageData.size(), new InteractiveImageData(strName, nWidth, nHeight, nX, nY, strSrc,
				strGroupID, bIsVisible));
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		mnX = nX;
		mnY = nY;
		mnWidth = nWidth;
		mnHeight = nHeight;

		this.setX(mnX);
		this.setY(mnY);
		this.setLayoutParams(new LayoutParams(mnWidth, mnHeight));

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

			if (1.0 < factor && mbGestureSpread && 2 != mnZoomSize)
			{
				mnZoomSize = 2;
				zoomHandler.zoom(imgScale, mnZoomSize);
			}
			else if (1.0 > factor && mbGesturePinch && 1 != mnZoomSize)
			{
				mnZoomSize = 1;
				zoomHandler.zoom(imgScale, 0.5f);
			}

			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			mnZoomSize = 0;
			removeScaleImage();
			if (mbGestureSpread || mbGesturePinch)
			{
				return createScaleImage();
			}
			return false;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			Logs.showTrace("EventImage scale end");
		}
	}

	private boolean createScaleImage()
	{
		imgScale = new ImageView(getContext());
		imgScale.setScaleType(ScaleType.FIT_XY);
		imgScale.setAdjustViewBounds(true);
		imgScale.setImageBitmap(mBitmapSrc);
		imgScale.setX(mnX);
		imgScale.setY(mnY);
		imgScale.setLayoutParams(new LayoutParams(mnWidth, mnHeight));
		if (null != container)
		{
			container.addView(imgScale);
			imgScale.bringToFront();
		}
		else
		{
			imgScale = null;
			return false;
		}
		return true;
	}

	private void removeScaleImage()
	{
		if (null != imgScale)
		{
			container.removeView(imgScale);
			imgScale = null;
		}
	}

	private void createEventImage(InteractiveImageData imageData)
	{
		removeEventImage();
		mBitmapEvent = BitmapHandler.readBitmap(getContext(), imageData.mstrSrc, imageData.mnWidth, imageData.mnHeight);
		imgEvent = new ImageView(getContext());
		imgEvent.setX(imageData.mnX);
		imgEvent.setY(imageData.mnY);
		imgEvent.setLayoutParams(new LayoutParams(imageData.mnWidth, imageData.mnHeight));
		imgEvent.setScaleType(ScaleType.CENTER_CROP);
		imgEvent.setImageBitmap(mBitmapEvent);
		imgEvent.setVisibility(View.INVISIBLE);
		container.addView(imgEvent);

		scaleGestureEventImage = null;
		scaleGestureEventImage = new ScaleGestureDetector(getContext(), new OnScaleGestureListener()
		{
			@Override
			public boolean onScale(ScaleGestureDetector detector)
			{
				EventHandler.notify(selfHandler, EVENT_IMAGE_CLOSE, 0, 0, null);
				return false;
			}

			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector)
			{
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
				return true;
			}

			@Override
			public void onScaleEnd(ScaleGestureDetector detector)
			{
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
			}
		});

		imgEvent.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				gestureDetector.onTouchEvent(event);
				return scaleGestureEventImage.onTouchEvent(event);
			}
		});
	}

	private void removeEventImage()
	{
		if (null != imgEvent)
		{
			container.removeView(imgEvent);
			if (null != mBitmapEvent)
			{
				if (!mBitmapEvent.isRecycled())
				{
					mBitmapEvent.recycle();
				}
			}
			imgEvent = null;
		}
	}

	private void handleEvent()
	{
		for (int i = 0; i < listGesture.size(); ++i)
		{
			Gesture gesture = listGesture.get(i);
			switch (gesture.mnEvent)
			{
			case InteractiveDefine.IMAGE_EVENT_SHOW_ITEM:
				switch (gesture.mnTargetType)
				{
				case InteractiveDefine.OBJECT_CATEGORY_IMAGE:
					showEventImage(gesture.mstrTargetID);
					break;
				}
				break;
			case InteractiveDefine.IMAGE_EVENT_DRAG:
				break;
			}
		}
	}

	private void showEventImage(String strImageTag)
	{
		for (int i = 0; i < listImageData.size(); ++i)
		{
			if (listImageData.get(i).mstrName.equals(strImageTag))
			{
				createEventImage(listImageData.get(i));
				//				MoveHandler.move(imgScale, mnX, listImageData.get(i).mnX, mnY, listImageData.get(i).mnY, 500,
				//						selfHandler);
				imgEvent.setVisibility(View.VISIBLE);
				imgEvent.bringToFront();
				removeScaleImage();
			}
		}
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public void onLongPress(MotionEvent e)
		{
			EventHandler.notify(selfHandler, EVENT_IMAGE_CLOSE, 0, 0, null);
			//super.onLongPress(e);
		}
	}

	private Handler	selfHandler	= new Handler()
								{

									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_ANIMATION_END:
											switch (msg.arg1)
											{
											case EventMessage.MSG_ANIMATION_MOVE:
												imgEvent.setVisibility(View.VISIBLE);
												imgEvent.bringToFront();
												removeScaleImage();
												break;
											case EventMessage.MSG_ANIMATION_ZOOM:
												handleEvent();
												break;
											}
											break;
										case EVENT_IMAGE_CLOSE:
											postDelayed(runRemoveImage, 100);
											break;
										}
									}

								};
}

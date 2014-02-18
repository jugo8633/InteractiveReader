package interactive.view.button;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.FileHandler;
import interactive.common.Type;
import interactive.view.global.Global;
import interactive.view.type.InteractiveType;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ButtonView extends ImageView
{
	private SparseArray<String>	arShowItem		= null;
	private ImageSrc			imageSrc		= null;
	private String				mstrGroupId		= null;
	private Handler				notifyHandler	= null;
	private SparseArray<Event>	listEvent		= null;

	class ImageSrc
	{
		public Bitmap	mBitmapSrc	= null;
		public Bitmap	mBitmapDown	= null;
		public Bitmap	mBitmapUp	= null;

		public ImageSrc(String strSrc, String strTouchDown, String strTouchUp, int nWidth, int nHeight)
		{
			if (null != strSrc && FileHandler.isFileExist(strSrc))
			{
				mBitmapSrc = BitmapHandler.readBitmap(strSrc, nWidth, nHeight);
			}

			if (null != strTouchDown && FileHandler.isFileExist(strTouchDown))
			{
				mBitmapDown = BitmapHandler.readBitmap(strTouchDown, nWidth, nHeight);
			}

			if (null != strTouchUp && FileHandler.isFileExist(strTouchUp))
			{
				mBitmapUp = BitmapHandler.readBitmap(strTouchUp, nWidth, nHeight);
			}
		}
	}

	public class Event
	{
		public int		mnType			= Type.INVALID;
		public String	mstrTypeName	= null;
		public int		mnEvent			= Type.INVALID;
		public String	mstrEventName	= null;
		public int		mnTargetType	= Type.INVALID;
		public String	mstrTargetID	= null;
		public int		mnDisplay		= Type.INVALID;

		public Event(int nType, String strTypeName, int nEvent, String strEventName, int nTargetType,
				String strTargetID, int nDisplay)
		{
			mnType = nType;
			mstrTypeName = strTypeName;
			mnEvent = nEvent;
			mstrEventName = strEventName;
			mnTargetType = nTargetType;
			mstrTargetID = strTargetID;
			mnDisplay = nDisplay;
		}
	}

	public ButtonView(Context context)
	{
		super(context);
		initButton();
	}

	public ButtonView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initButton();
	}

	public ButtonView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initButton();
	}

	public void setPosition(int nChapter, int nPage)
	{
		Global.addActiveNotify(nChapter, nPage, buttonHandler);
	}

	private void initButton()
	{
		this.setFocusable(true);
		this.setClickable(true);
		this.setScaleType(ScaleType.FIT_CENTER);
		this.setBackgroundColor(Color.TRANSPARENT);
		listEvent = new SparseArray<Event>();
	}

	public void setImageSrc(String strSrc, String strTouchDown, String strTouchUp, int nWidth, int nHeight)
	{
		if (null != imageSrc)
		{
			imageSrc.mBitmapSrc.recycle();
			imageSrc.mBitmapDown.recycle();
			imageSrc.mBitmapUp.recycle();
		}
		imageSrc = null;
		imageSrc = new ImageSrc(strSrc, strTouchDown, strTouchUp, nWidth, nHeight);
		this.setImageBitmap(imageSrc.mBitmapSrc);
	}

	public void setGroupId(String strGroupId)
	{
		mstrGroupId = strGroupId;
	}

	public String getGroupId()
	{
		return mstrGroupId;
	}

	public void setNotifyHandler(Handler handler)
	{
		notifyHandler = handler;
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new ViewGroup.LayoutParams(nWidth, nHeight));
	}

	public void setButtonClickType(int nClickType)
	{
		switch (nClickType)
		{
		case InteractiveType.BUTTON_TYPE_TAP:
			this.setOnTouchListener(onTouchListener);
			break;
		case InteractiveType.BUTTON_TYPE_LONG_PRESS:
			this.setOnLongClickListener(onLongClickListener);
			break;
		}
	}

	public void addEvent(int nType, String strTypeName, int nEvent, String strEventName, int nTargetType,
			String strTargetID, int nDisplay)
	{
		listEvent.put(listEvent.size(), new Event(nType, strTypeName, nEvent, strEventName, nTargetType, strTargetID,
				nDisplay));
		if (InteractiveType.OBJECT_CATEGORY_IMAGE == nTargetType)
		{
			Global.interactiveHandler.addInteractiveImageNotify(strTargetID, buttonHandler);
		}
	}

	public void setShowItem(String strTagName)
	{
		if (null == arShowItem)
		{
			arShowItem = new SparseArray<String>();
		}
		arShowItem.put(arShowItem.size(), strTagName);
	}

	private void showItem(Event event)
	{
		switch (event.mnTargetType)
		{
		case InteractiveType.OBJECT_CATEGORY_IMAGE:
			EventHandler.notify(notifyHandler, EventMessage.MSG_SHOW_ITEM, InteractiveType.OBJECT_CATEGORY_IMAGE, 0,
					event.mstrTargetID);
			break;
		case InteractiveType.OBJECT_CATEGORY_MAP:
			EventHandler.notify(notifyHandler, EventMessage.MSG_SHOW_ITEM, InteractiveType.OBJECT_CATEGORY_MAP, 0,
					event.mstrTargetID);
			break;
		}
	}

	private void startEvent()
	{
		for (int i = 0; i < listEvent.size(); ++i)
		{
			Event event = listEvent.get(i);
			switch (event.mnEvent)
			{
			case InteractiveType.BUTTON_EVENT_SHOW_ITEM:
				showItem(event);
				break;
			case InteractiveType.BUTTON_EVENT_DRAG:
				break;
			case InteractiveType.BUTTON_EVENT_VIDEO_PAUSE:
				break;
			case InteractiveType.BUTTON_EVENT_VIDEO_PLAY:
				break;
			}
			event = null;
		}
	}

	private OnTouchListener		onTouchListener		= new OnTouchListener()
													{
														@Override
														public boolean onTouch(View v, MotionEvent event)
														{
															switch (event.getAction())
															{
															case MotionEvent.ACTION_DOWN:
																if (null != imageSrc.mBitmapDown)
																{
																	ButtonView.this
																			.setImageBitmap(imageSrc.mBitmapDown);
																}
																else
																{
																	setColorFilter(Color.parseColor("#70000000"));
																}
																break;
															case MotionEvent.ACTION_UP:
																setColorFilter(Color.TRANSPARENT);
																if (null != imageSrc.mBitmapUp)
																{
																	ButtonView.this.setImageBitmap(imageSrc.mBitmapUp);
																}
																else
																{
																	ButtonView.this.setImageBitmap(imageSrc.mBitmapSrc);
																}
																startEvent();
																break;
															case MotionEvent.ACTION_CANCEL:
																setColorFilter(Color.TRANSPARENT);
																ButtonView.this.setImageBitmap(imageSrc.mBitmapSrc);
																break;
															}
															return true;
														}
													};

	private OnLongClickListener	onLongClickListener	= new OnLongClickListener()
													{

														@Override
														public boolean onLongClick(View v)
														{
															return false;
														}

													};

	private Handler				buttonHandler		= new Handler()
													{
														@Override
														public void handleMessage(Message msg)
														{
															switch (msg.what)
															{
															case EventMessage.MSG_IMAGE_CLICK:
																ButtonView.this.setImageBitmap(imageSrc.mBitmapSrc);
																break;
															case EventMessage.MSG_CURRENT_ACTIVE:
																setColorFilter(Color.TRANSPARENT);
																ButtonView.this.setImageBitmap(imageSrc.mBitmapSrc);
																break;
															}
														}
													};

}

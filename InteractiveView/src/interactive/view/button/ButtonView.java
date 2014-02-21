package interactive.view.button;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.FileHandler;
import interactive.view.define.InteractiveDefine;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ButtonView extends ImageView
{
	private ImageSrc	imageSrc		= null;
	private Handler		notifyHandler	= null;

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
		Global.addActiveNotify(nChapter, nPage, selfHandler);
		Global.addUnActiveNotify(nChapter, nPage, selfHandler);
	}

	private void initButton()
	{
		this.setFocusable(true);
		this.setClickable(true);
		this.setScaleType(ScaleType.FIT_CENTER);
		this.setBackgroundColor(Color.TRANSPARENT);
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
		case InteractiveDefine.BUTTON_TYPE_TAP:
			this.setOnTouchListener(onTouchListener);
			break;
		case InteractiveDefine.BUTTON_TYPE_LONG_PRESS:
			this.setOnLongClickListener(onLongClickListener);
			break;
		}
	}

	private void startEvent()
	{
		EventHandler.notify(notifyHandler, EventMessage.MSG_BUTTON_EVENT, 0, 0, getTag());
	}

	public Handler getButtonHandler()
	{
		return selfHandler;
	}

	public void reset()
	{
		setColorFilter(Color.TRANSPARENT);
		ButtonView.this.setImageBitmap(imageSrc.mBitmapSrc);
	}

	private OnTouchListener		onTouchListener		= new OnTouchListener()
													{
														@Override
														public boolean onTouch(View view, MotionEvent event)
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
																reset();
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

	private Handler				selfHandler			= new Handler()
													{
														@Override
														public void handleMessage(Message msg)
														{
															switch (msg.what)
															{
															case EventMessage.MSG_IMAGE_CLICK:
															case EventMessage.MSG_RESET:
																reset();
																break;
															case EventMessage.MSG_CURRENT_ACTIVE:

																break;
															case EventMessage.MSG_NOT_CURRENT_ACTIVE:
																EventHandler.notify(
																		Global.interactiveHandler.getNotifyHandler(),
																		EventMessage.MSG_RESET,
																		InteractiveDefine.OBJECT_CATEGORY_BUTTON, 0,
																		ButtonView.this.getTag());
																reset();
																break;
															}
														}
													};

}

package interactive.view.postcard;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.view.animation.flipcard.Rotate3d;
import interactive.view.global.Global;
import interactive.view.pagereader.PageReader;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class Postcard
{

	private FrameLayout		postcardFrame	= null;
	private ViewGroup		container		= null;
	private Context			theContext		= null;
	private GestureDetector	gestureDetector	= null;
	private FingerPaintView	fingerPaintView	= null;
	private ImageView		imgPostFront	= null;
	private Rotate3d		rotate3d		= null;

	public Postcard(Context context, ViewGroup viewGroup)
	{
		super();
		theContext = context;
		container = viewGroup;
		postcardFrame = new FrameLayout(context);
		gestureDetector = new GestureDetector(context, simpleOnGestureListener);
		rotate3d = new Rotate3d();
	}

	public void initPostcardFrame(String strName, int nX, int nY, int nWidth, int nHeight, String strFront,
			String strBack)
	{
		postcardFrame.setTag(strName);
		postcardFrame.setX(nX);
		postcardFrame.setY(nY);
		postcardFrame.setLayoutParams(new LayoutParams(nWidth, nHeight));
		container.removeView(postcardFrame);
		container.addView(postcardFrame);

		initPostcard(strFront, strBack);

		postcardFrame.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_HORIZON, 0, 0, null);
					break;
				case MotionEvent.ACTION_UP:
					EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_HORIZON, 0, 0, null);
					break;
				}
				gestureDetector.onTouchEvent(event);
				return true;
			}
		});

		rotate3d.setlistOnRotateEndListener(new Rotate3d.OnRotateEndListener()
		{
			@Override
			public void onRotateEnd()
			{
				switchCard();
			}
		});

		Global.pageReader.setOnPageSwitchedListener(new PageReader.OnPageSwitchedListener()
		{
			@Override
			public void onPageSwitched()
			{
				fingerPaintView.setIsCapturing(false);
				clearSelected();
			}
		});
	}

	/** set postcard front image and back image */
	private void initPostcard(String strFront, String strBack)
	{
		postcardFrame.removeAllViewsInLayout();

		if (null != strBack)
		{
			fingerPaintView = new FingerPaintView(theContext);
			fingerPaintView.setBackground(Drawable.createFromPath(strBack));
			fingerPaintView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			postcardFrame.addView(fingerPaintView);
			fingerPaintView.setVisibility(View.GONE);
		}

		if (null != strFront)
		{
			imgPostFront = new ImageView(theContext);
			imgPostFront.setImageURI(Uri.parse(strFront));
			imgPostFront.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			postcardFrame.addView(imgPostFront);
		}

	}

	private void switchCard()
	{

		if (imgPostFront.getVisibility() == View.VISIBLE)
		{
			fingerPaintView.setVisibility(View.VISIBLE);
			imgPostFront.setVisibility(View.GONE);
			showEdit(true);
		}
		else
		{
			imgPostFront.setVisibility(View.VISIBLE);
			fingerPaintView.setVisibility(View.GONE);
			showEdit(false);
		}
	}

	private void showEdit(boolean bShow)
	{
		clearSelected();
		View view = null;
		if (bShow)
		{
			view = container.findViewWithTag("pen");
			if (null != view)
			{
				view.setVisibility(View.VISIBLE);
			}
			view = container.findViewWithTag("eraser");
			if (null != view)
			{
				view.setVisibility(View.VISIBLE);
			}
			view = container.findViewWithTag("textArea");
			if (null != view)
			{
				view.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			view = container.findViewWithTag("pen");
			if (null != view)
			{
				view.setVisibility(View.GONE);
			}
			view = container.findViewWithTag("eraser");
			if (null != view)
			{
				view.setVisibility(View.GONE);
			}
			view = container.findViewWithTag("textArea");
			if (null != view)
			{
				view.setVisibility(View.GONE);
			}
		}
	}

	public void initPen(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgPen = new ImageView(theContext);
		imgPen.setTag("pen");
		imgPen.setVisibility(View.GONE);
		initOptionImage(imgPen, nWidth, nHeight, nX, nY, strImagePath);
	}

	public void initEraser(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgEraser = new ImageView(theContext);
		imgEraser.setTag("eraser");
		imgEraser.setVisibility(View.GONE);
		initOptionImage(imgEraser, nWidth, nHeight, nX, nY, strImagePath);
	}

	public void initTextArea(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgTextArea = new ImageView(theContext);
		imgTextArea.setTag("textArea");
		imgTextArea.setVisibility(View.GONE);
		initOptionImage(imgTextArea, nWidth, nHeight, nX, nY, strImagePath);
	}

	public void initCamera(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgCamera = new ImageView(theContext);
		imgCamera.setTag("camera");
		initOptionImage(imgCamera, nWidth, nHeight, nX, nY, strImagePath);
	}

	public void initOpenButton(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgOpenButton = new ImageView(theContext);
		imgOpenButton.setTag("openButton");
		initOptionImage(imgOpenButton, nWidth, nHeight, nX, nY, strImagePath);
	}

	public void initMailBox(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgMailBox = new ImageView(theContext);
		imgMailBox.setTag("mailBox");
		initOptionImage(imgMailBox, nWidth, nHeight, nX, nY, strImagePath);
	}

	private void initOptionImage(ImageView img, int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		img.setX(nX);
		img.setY(nY);
		img.setLayoutParams(new LayoutParams(nWidth, nHeight));
		img.setImageURI(Uri.parse(strImagePath));
		img.setScaleType(ScaleType.CENTER_CROP);
		container.addView(img);
		img.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onOptionClick(v);
			}
		});
	}

	private void onOptionClick(View view)
	{
		if (null == view)
		{
			return;
		}
		String strTag = (String) view.getTag();

		if (strTag.equals("pen"))
		{
			if (null == view.getBackground())
			{
				View eraserView = container.findViewWithTag("eraser");
				if (null != eraserView)
				{
					eraserView.setBackground(null);
				}
				view.setBackgroundResource(Global.getResourceId(theContext, "circle", "drawable"));
				fingerPaintView.setIsCapturing(true);
				container.requestDisallowInterceptTouchEvent(true);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
			}
			else
			{
				container.requestDisallowInterceptTouchEvent(false);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
				fingerPaintView.setIsCapturing(false);
				view.setBackground(null);
			}
		}

		if (strTag.equals("eraser"))
		{
			if (null == view.getBackground())
			{
				View penView = container.findViewWithTag("pen");
				if (null != penView)
				{
					penView.setBackground(null);
				}
				view.setBackgroundResource(Global.getResourceId(theContext, "circle", "drawable"));
				fingerPaintView.setIsCapturing(true);
				container.requestDisallowInterceptTouchEvent(true);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
			}
			else
			{
				container.requestDisallowInterceptTouchEvent(false);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
				fingerPaintView.setIsCapturing(false);
				view.setBackground(null);
			}
		}
	}

	private void clearSelected()
	{
		View eraserView = container.findViewWithTag("eraser");
		if (null != eraserView)
		{
			eraserView.setBackground(null);
		}

		View penView = container.findViewWithTag("pen");
		if (null != penView)
		{
			penView.setBackground(null);
		}
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

															if ((e1.getX() - e2.getX()) > sensitvity)
															{
																// left
																rotate3d.applyRotation(postcardFrame, 0, -90,
																		Rotate3d.ROTATE_LEFT);

															}
															else if ((e2.getX() - e1.getX()) > sensitvity)
															{
																// right
																rotate3d.applyRotation(postcardFrame, 0, 90,
																		Rotate3d.ROTATE_RIGHT);

															}
															return super.onFling(e1, e2, velocityX, velocityY);
														}
													};
}

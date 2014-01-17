package interactive.view.postcard;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.view.animation.flipcard.Rotate3d;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
		}
		else
		{
			imgPostFront.setVisibility(View.VISIBLE);
			fingerPaintView.setVisibility(View.GONE);
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

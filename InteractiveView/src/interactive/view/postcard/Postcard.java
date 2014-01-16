package interactive.view.postcard;

import interactive.common.EventHandler;
import interactive.view.scrollable.ScrollableView;
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

	public Postcard(Context context, ViewGroup viewGroup)
	{
		super();
		theContext = context;
		container = viewGroup;
		postcardFrame = new FrameLayout(context);
		gestureDetector = new GestureDetector(context, simpleOnGestureListener);
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
	}

	/** set postcard front image and back image */
	private void initPostcard(String strFront, String strBack)
	{
		postcardFrame.removeAllViewsInLayout();

		if (null != strFront)
		{
			ImageView image = new ImageView(theContext);
			image.setImageURI(Uri.parse(strFront));
			image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			postcardFrame.addView(image);
		}

		if (null != strBack)
		{
			FingerPaint fingerPaint = new FingerPaint(theContext);
			fingerPaint.setBackground(Drawable.createFromPath(strBack));
			fingerPaint.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			postcardFrame.addView(fingerPaint);
		}

		postcardFrame.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				gestureDetector.onTouchEvent(event);
				return false;
			}
		});
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
															}
															else if ((e2.getX() - e1.getX()) > sensitvity)
															{
																// right
															}
															return super.onFling(e1, e2, velocityX, velocityY);
														}
													};
}

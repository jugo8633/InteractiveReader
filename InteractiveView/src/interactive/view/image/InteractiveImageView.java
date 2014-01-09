package interactive.view.image;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class InteractiveImageView extends ImageView
{

	private Handler	theHandler	= null;
	private String	mstrGroupId	= null;

	public InteractiveImageView(Context context)
	{
		super(context);
		init();
	}

	public InteractiveImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public InteractiveImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		setBackgroundColor(Color.TRANSPARENT);
		this.setOnTouchListener(onTouchListener);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new ViewGroup.LayoutParams(nWidth, nHeight));
	}

	public void setNotifyHandler(Handler handler)
	{
		theHandler = handler;
	}

	public void setGroupId(String strGroupId)
	{
		mstrGroupId = strGroupId;
	}

	public String getGroupId()
	{
		return mstrGroupId;

	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility)
	{
		super.onVisibilityChanged(changedView, visibility);
		if (View.GONE == visibility)
		{
			if (null != theHandler)
			{
				EventHandler.notify(theHandler, EventMessage.MSG_IMAGE_CLICK, 0, 0,
						getTag());
			}
		}
	}

	private OnTouchListener	onTouchListener	= new OnTouchListener()
											{

												@Override
												public boolean onTouch(View v,
														MotionEvent event)
												{
													v.setVisibility(View.GONE);
													return true;
												}

											};
}

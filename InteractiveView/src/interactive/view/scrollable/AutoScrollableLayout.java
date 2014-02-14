package interactive.view.scrollable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

public class AutoScrollableLayout extends RelativeLayout
{

	private ImageView	imageView	= null;

	public AutoScrollableLayout(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AutoScrollableLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AutoScrollableLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private void init(Context context)
	{

	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new LayoutParams(nWidth, nHeight));
	}
}

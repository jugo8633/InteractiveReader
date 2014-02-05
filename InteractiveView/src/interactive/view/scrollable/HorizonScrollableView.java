package interactive.view.scrollable;

import interactive.common.BitmapHandler;
import interactive.common.ClearCache;
import interactive.common.EventMessage;
import interactive.common.FileHandler;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class HorizonScrollableView extends HorizontalScrollView
{
	private LinearLayout	linearLayout	= null;
	private ImageView		imageView		= null;
	private int				mnOffsetX		= 0;
	private int				mnChapter		= Type.INVALID;
	private int				mnPage			= Type.INVALID;
	private int				mnWidth			= Type.INVALID;
	private int				mnHeight		= Type.INVALID;
	private Context			theContext		= null;

	public HorizonScrollableView(Context context)
	{
		super(context);
		init(context);
	}

	public HorizonScrollableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public HorizonScrollableView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		theContext = context;
		setHorizontalScrollBarEnabled(false);

		imageView = new ImageView(context);
		imageView.setScaleType(ScaleType.CENTER_CROP);

		linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		linearLayout.setHorizontalScrollBarEnabled(false);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new LayoutParams(nWidth, nHeight));
		mnWidth = nWidth;
		mnHeight = nHeight;
	}

	public void setImage(String strImagePath, int nWidth, int nHeight, int nOffsetX, int nOffsetY)
	{
		Bitmap bmp = BitmapHandler.readBitmap(strImagePath, nWidth, nHeight);
		imageView.setLayoutParams(new LayoutParams(nWidth, nHeight));
		imageView.setImageBitmap(bmp);
		bmp = null;

		linearLayout.removeAllViewsInLayout();
		linearLayout.addView(imageView);

		if ((nWidth - nOffsetX) < mnWidth)
		{
			int nOffSize = nWidth - nOffsetX;
			View view = new View(theContext);
			view.setLayoutParams(new LayoutParams(nOffSize, LayoutParams.WRAP_CONTENT));
			linearLayout.addView(view);
		}

		removeView(linearLayout);
		addView(linearLayout);

		if (0 < nOffsetX)
		{
			setOffset(nOffsetX);
		}

		if (0 > nOffsetY)
		{
			setPadding(0 - nOffsetY);
		}
	}

	private void setPadding(int nTop)
	{
		this.setPadding(0, nTop, 0, 0);
	}

	private void setOffset(int nX)
	{
		mnOffsetX = nX;
	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
		Global.addActiveNotify(mnChapter, mnPage, notifyHandler);
	}

	private void initOffset()
	{
		this.scrollTo(mnOffsetX, 0);
	}

	private Handler	notifyHandler	= new Handler()
									{
										@Override
										public void handleMessage(Message msg)
										{
											switch (msg.what)
											{
											case EventMessage.MSG_CURRENT_ACTIVE:
												initOffset();
												Logs.showTrace("current view:" + HorizonScrollableView.this.getTag()
														+ " ###########");
												break;
											}
											super.handleMessage(msg);
										}
									};
}

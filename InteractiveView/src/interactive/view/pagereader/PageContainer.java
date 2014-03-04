package interactive.view.pagereader;

import interactive.common.EventMessage;
import interactive.view.global.Global;
import interactive.view.image.ImageViewHandler;
import interactive.view.slideshow.SlideshowView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class PageContainer extends RelativeLayout
{

	public PageContainer(Context context)
	{
		super(context);
	}

	public void setDisplay(float nX, float nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
	}

	public void setPosition(int nChapter, int nPage)
	{
		//		Global.addActiveNotify(nChapter, nPage, selfHandler);
		//		Global.addUnActiveNotify(nChapter, nPage, selfHandler);
	}

	public void setBackground(String strImagePath, int nWidth, int nHeight)
	{
		this.setBackground(Drawable.createFromPath(strImagePath));
	}

	private Handler	selfHandler	= new Handler()
								{
									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_CURRENT_ACTIVE:

											break;
										case EventMessage.MSG_NOT_CURRENT_ACTIVE:

											break;

										}
									}
								};
}

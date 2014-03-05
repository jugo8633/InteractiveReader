package interactive.view.doodle;

import interactive.common.EventMessage;
import interactive.view.fingerpaint.FingerPaintView;
import interactive.view.global.Global;
import interactive.view.image.ImageViewHandler;
import interactive.view.slideshow.SlideshowView;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class DoodleView extends RelativeLayout
{
	private ViewGroup			container		= null;
	private ImageViewHandler	imageHandler	= null;
	private boolean				mbCurrentActive	= false;
	private FingerPaintView		fingerPaintView	= null;

	private DoodleView(Context context)
	{
		super(context);
	}

	public DoodleView(Context context, ViewGroup viewGroup)
	{
		super(context);
		container = viewGroup;
		imageHandler = new ImageViewHandler(selfHandler);

		fingerPaintView = new FingerPaintView(context);
		fingerPaintView.setId(Global.getUserId());
		fingerPaintView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(fingerPaintView);
		fingerPaintView.setIsCapturing(true);
		fingerPaintView.setEraser(false);
	}

	public void SetDisplay(float fX, float fY, int nWidth, int nHeight)
	{
		this.setX(fX);
		this.setY(fY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
	}

	public void setPosition(int nChapter, int nPage)
	{
		Global.addActiveNotify(nChapter, nPage, selfHandler);
		Global.addUnActiveNotify(nChapter, nPage, selfHandler);
	}

	public void setImageSrc(String strImagePath, int nWidth, int nHeight)
	{
		ImageView imageView = new ImageView(getContext());
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(imageView);
		imageHandler.addImageView(imageView, strImagePath, nWidth, nHeight);
	}

	private Handler	selfHandler	= new Handler()
								{
									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_CURRENT_ACTIVE:
											mbCurrentActive = true;
											imageHandler.runInitImageView();
											break;
										case EventMessage.MSG_NOT_CURRENT_ACTIVE:
											if (mbCurrentActive)
											{
												mbCurrentActive = false;
												imageHandler.releaseBitmap();
											}
											break;
										case EventMessage.MSG_VIEW_INITED:
											break;
										}
									}
								};
}

package interactive.view.handler;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class InteractiveMediaLayout extends RelativeLayout
{
	private ImageView						imgPlay					= null;
	private ImageView						imgBackground			= null;
	private Handler							notifyHandler			= null;
	private SparseArray<OnVideoPlayListner>	listOnVideoPlayListner	= null;
	private String							mstrMediaTagName		= null;
	private boolean							mbAutoplay				= false;
	private int								mnChapter				= Type.INVALID;
	private int								mnPage					= Type.INVALID;

	public interface OnVideoPlayListner
	{
		void onVideoPlayed();
	}

	public InteractiveMediaLayout(Context context)
	{
		super(context);
		init(context);
	}

	public InteractiveMediaLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public InteractiveMediaLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
	}

	private void init(Context context)
	{
		imgBackground = new ImageView(context);
		imgBackground.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(imgBackground);
		listOnVideoPlayListner = new SparseArray<OnVideoPlayListner>();
	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
	}

	public void setAutoplay(boolean bAutoplay)
	{
		mbAutoplay = bAutoplay;
		if (mbAutoplay)
		{
			Global.addActiveNotify(mnChapter, mnPage, selfHandler);
		}
		else
		{
			int nResId = getContext().getResources().getIdentifier("video_play", "drawable",
					getContext().getPackageName());
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			imgPlay = new ImageView(getContext());
			imgPlay.setImageResource(nResId);
			imgPlay.setScaleType(ScaleType.CENTER_CROP);
			imgPlay.setLayoutParams(layoutParams);
			imgPlay.setImageAlpha(90);
			imgPlay.setOnClickListener(playClickListener);
			addView(imgPlay);
		}
	}

	public void setBackground(String strImage, int nWidth, int nHeight)
	{
		if (null != imgBackground && null != strImage && 0 < nWidth && 0 < nHeight)
		{
			Bitmap bitmap = BitmapHandler.readBitmap(getContext(), strImage, nWidth, nHeight, false);
			imgBackground.setImageBitmap(bitmap);
		}
	}

	public void setNotifyHandler(Handler handler)
	{
		notifyHandler = handler;
	}

	public void setOnVideoPlayListner(InteractiveMediaLayout.OnVideoPlayListner listner)
	{
		if (null != listner)
		{
			listOnVideoPlayListner.put(listOnVideoPlayListner.size(), listner);
		}
	}

	public void notifyVideoPlay()
	{
		for (int i = 0; i < listOnVideoPlayListner.size(); ++i)
		{
			listOnVideoPlayListner.get(i).onVideoPlayed();
		}
	}

	private void setContainer()
	{
		Global.interactiveHandler.setMediaContainer(mstrMediaTagName, this);
	}

	public void setMediaTag(String strTagName)
	{
		mstrMediaTagName = strTagName;
	}

	private void playMedia()
	{
		notifyVideoPlay();
		if (null != notifyHandler)
		{
			setContainer();
			EventHandler.notify(notifyHandler, EventMessage.MSG_MEDIA_PLAY, 0, 0, mstrMediaTagName);
			Logs.showTrace("Play media:" + mstrMediaTagName);
		}
	}

	OnClickListener	playClickListener	= new OnClickListener()
										{
											@Override
											public void onClick(View v)
											{
												playMedia();
											}
										};

	private Handler	selfHandler			= new Handler()
										{

											@Override
											public void handleMessage(Message msg)
											{
												switch (msg.what)
												{
												case EventMessage.MSG_CURRENT_ACTIVE:
													if (mbAutoplay)
													{
														playMedia();
													}
													break;
												}
											}

										};

}

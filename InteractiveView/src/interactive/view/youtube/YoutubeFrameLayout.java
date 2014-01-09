package interactive.view.youtube;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.view.type.InteractiveType;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class YoutubeFrameLayout extends RelativeLayout
{

	private ImageView						imgPlay					= null;
	private ImageView						imgBackground			= null;
	private Handler							notifyHandler			= null;
	private SparseArray<OnVideoPlayListner>	listOnVideoPlayListner	= null;

	public interface OnVideoPlayListner
	{
		void onVideoPlayed();
	}

	public YoutubeFrameLayout(Context context)
	{
		super(context);
		init(context);
	}

	public YoutubeFrameLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public YoutubeFrameLayout(Context context, AttributeSet attrs, int defStyle)
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
		int nResId = context.getResources().getIdentifier("video_play", "drawable", context.getPackageName());

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		imgPlay = new ImageView(context);
		imgPlay.setImageResource(nResId);
		imgPlay.setScaleType(ScaleType.CENTER_CROP);
		imgPlay.setLayoutParams(layoutParams);
		imgBackground = new ImageView(context);
		imgBackground.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(imgBackground);
		addView(imgPlay);

		imgPlay.setOnClickListener(playClickListener);

		listOnVideoPlayListner = new SparseArray<OnVideoPlayListner>();
	}

	public void setBackground(String strImage)
	{
		if (null != imgBackground && null != strImage)
		{
			imgBackground.setImageURI(Uri.parse(strImage));
		}
	}

	public void setNotifyHandler(Handler handler)
	{
		notifyHandler = handler;
	}

	public void setOnVideoPlayListner(YoutubeFrameLayout.OnVideoPlayListner listner)
	{
		if (null != listner)
		{
			listOnVideoPlayListner.put(listOnVideoPlayListner.size(), listner);
		}
	}

	OnClickListener	playClickListener	= new OnClickListener()
										{
											@Override
											public void onClick(View v)
											{
												for (int i = 0; i < listOnVideoPlayListner.size(); ++i)
												{
													listOnVideoPlayListner.get(i).onVideoPlayed();
												}

												if (null != notifyHandler)
												{
													EventHandler.notify(notifyHandler, EventMessage.MSG_VIDEO_PLAY,
															InteractiveType.VIDEO_TYPE_TOUTUBE, 0, getTag());
												}
											}
										};
}

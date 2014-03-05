package interactive.view.audio;

import java.io.IOException;

import interactive.common.BitmapHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.global.Global;
import interactive.view.image.ImageViewHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class AudioPlayer extends RelativeLayout
{

	private Bitmap				mBmpSrc			= null;
	private ImageView			imageView		= null;
	private MediaController		audioController	= null;
	private MediaPlayer			mediaPlayer		= null;
	private ImageViewHandler	imageHandler	= null;
	private boolean				mbCurrentActive	= false;
	private boolean				mbAutoplay		= false;
	private boolean				mbAutoStop		= true;
	private int					mnEnd			= Type.INVALID;
	private boolean				mbLoop			= false;
	private int					mnStart			= 0;

	public AudioPlayer(Context context)
	{
		super(context);
		init();
	}

	public AudioPlayer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public AudioPlayer(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer mp)
			{
				if (!mbLoop)
				{
					if (null != audioController)
					{
						audioController.hide();
					}
				}
			}
		});

		imageHandler = new ImageViewHandler(selfHandler);
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (mediaPlayer.isPlaying())
				{
					mediaPlayer.pause();
					if (null != audioController)
					{
						audioController.updatePausePlay();
					}
				}
				else
				{
					mediaPlayer.start();
					if (null != audioController)
					{
						audioController.updatePausePlay();
					}
				}
				if (!audioController.isShowing())
				{
					audioController.show(0);
				}
			}
		});
	}

	@Override
	protected void finalize() throws Throwable
	{
		mediaPlayer.stop();
		mediaPlayer.release();
		super.finalize();
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
	}

	public void setPosition(int nChapter, int nPage)
	{
		Global.addActiveNotify(nChapter, nPage, selfHandler);
		Global.addUnActiveNotify(nChapter, nPage, selfHandler);
	}

	public void setPlayerControls(boolean bShow)
	{
		if (null != audioController)
		{
			this.removeView(audioController);
			audioController = null;
		}

		if (bShow)
		{
			RelativeLayout.LayoutParams controlerLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, Global.ScaleSize(40));
			controlerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			audioController = new MediaController(getContext(), false);
			audioController.setLayoutParams(controlerLayoutParams);
			audioController.setAnchorView(this);
			audioController.setMediaPlayer(mediaPlayer);
			this.addView(audioController);
		}
	}

	public void setOption(boolean bAutoPlay, boolean bAutoStop, boolean bLoop, int nStart, int nEnd)
	{
		mbAutoplay = bAutoPlay;
		mbAutoStop = bAutoStop;
		mbLoop = bLoop;
		mnStart = nStart;
		mnEnd = nEnd;
		mediaPlayer.setLooping(bLoop);
	}

	public void setImage(String strImagePath, int nWidth, int nHeight)
	{
		BitmapHandler.releaseBitmap(mBmpSrc);
		if (null != imageView)
		{
			this.removeView(imageView);
			imageView = null;
		}
		imageView = new ImageView(getContext());
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setVisibility(View.GONE);
		this.addView(imageView);
		imageHandler.addImageView(imageView, strImagePath, nWidth, nHeight);
	}

	public void setAudioFile(String strFile)
	{
		try
		{
			mediaPlayer.setDataSource(strFile);
			mediaPlayer.prepare();
			Logs.showTrace("Audio play set data source=" + strFile);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void play()
	{
		if (mediaPlayer.isPlaying())
		{
			mediaPlayer.pause();
		}
		mediaPlayer.seekTo(mnStart * 1000);
		mediaPlayer.start();
		if (null != audioController)
		{
			audioController.show(0);
			audioController.updatePausePlay();
		}
	}

	private void stop()
	{
		if (mediaPlayer.isPlaying())
		{
			mediaPlayer.pause();
		}
		mediaPlayer.seekTo(0);
		if (null != audioController)
		{
			audioController.updatePausePlay();
			audioController.hide();
		}
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
											if (mbAutoplay)
											{
												play();
											}
											break;
										case EventMessage.MSG_NOT_CURRENT_ACTIVE:
											if (mbCurrentActive)
											{
												if (mbAutoStop)
												{
													stop();
												}
												mbCurrentActive = false;
												imageHandler.releaseBitmap();
											}
											break;

										}
									}
								};
}

package interactive.view.webview;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.FileHandler;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.data.PageData;
import interactive.view.global.Global;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class InteractiveWebView extends WebView
{
	public static final String	EXTRA_URL		= "extra_url";
	private boolean				mbOverLoadUrl	= false;
	private int					mnJumpChapter	= Type.INVALID;
	private int					mnJumpPage		= Type.INVALID;
	private int					mnChapter		= Type.INVALID; // self chapter
	private int					mnPage			= Type.INVALID; // self page
	private GestureDetector		gestureDetector	= null;
	private boolean				mbAutoPlay		= false;

	public InteractiveWebView(Context context)
	{
		super(context);
		init();
	}

	public InteractiveWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public InteractiveWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init()
	{
		//		this.getSettings().setLoadWithOverviewMode(true);
		//		this.getSettings().setUseWideViewPort(false);
		//		this.getSettings().setJavaScriptEnabled(true);
		//		this.getSettings().setPluginState(PluginState.ON);
		//		this.getSettings().setBuiltInZoomControls(true);
		//		this.getSettings().setSupportZoom(false);
		//		this.getSettings().setAllowContentAccess(false);
		//		this.getSettings().setDisplayZoomControls(false);
		//		this.getSettings().setAllowFileAccess(true);
		//		this.getSettings().setDefaultTextEncodingName("utf-8");
		//		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		//		this.getSettings().setDomStorageEnabled(true);
		//		this.getSettings().setPluginState(PluginState.ON_DEMAND);
		//
		//		this.setHorizontalScrollBarEnabled(false);
		//		this.setVerticalScrollBarEnabled(false);
		//		this.setFocusable(true);
		//		this.setFocusableInTouchMode(true);
		//		this.setBackgroundColor(Color.TRANSPARENT);
		//		this.setClickable(false);
		//		this.setHorizontalFadingEdgeEnabled(false);
		//		this.setVerticalFadingEdgeEnabled(false);
		//		this.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		//      this.setInitialScale(1);

		this.getSettings().setJavaScriptEnabled(true);
		this.setWebViewClient(new WebViewClient());

		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setBuiltInZoomControls(false);
		this.getSettings().setSupportZoom(true);
		this.getSettings().setLoadWithOverviewMode(true);
		this.getSettings().setUseWideViewPort(false);
		this.setOverScrollMode(OVER_SCROLL_NEVER);
		this.setHorizontalScrollBarEnabled(false);
		this.setVerticalScrollBarEnabled(false);
		this.setClickable(false);

		this.setOnLongClickListener(longClickListener);
		this.setOnTouchListener(onTouchListener);

		setWebViewClient(new myWebViewClient());
		setWebChromeClient(new WebChromeClient());

		gestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);

	}

	private boolean isPageExist(String strPageName)
	{
		if (null != PageData.listPageData)
		{
			for (int nChapter = 0; nChapter < PageData.listPageData.size(); ++nChapter)
			{
				for (int nPage = 0; nPage < PageData.listPageData.get(nChapter).size(); ++nPage)
				{
					PageData.Data pageData = PageData.listPageData.get(nChapter).get(nPage);
					if (pageData.strName.equals(strPageName))
					{
						mnJumpChapter = pageData.nChapter;
						mnJumpPage = pageData.nPage;
						return true;
					}
				}
			}
		}

		return false;
	}

	private class myWebViewClient extends WebViewClient
	{
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			/** 跳頁 */
			File file = new File(url);
			String strName = file.getName();
			file = null;
			if (mbOverLoadUrl && InteractiveWebView.this.isPageExist(strName))
			{
				EventHandler
						.notify(Global.handlerActivity, EventMessage.MSG_JUMP_FADE, mnJumpChapter, mnJumpPage, null);
				mnJumpPage = Type.INVALID;
				mnJumpChapter = Type.INVALID;
				return true;
			}

			/** 外部連結 & 內部連結 */
			String strFilePath = url.substring(7, url.length());
			if (url.startsWith("http:") || url.startsWith("https:")
					|| (mbOverLoadUrl && url.startsWith("file:") && FileHandler.isFileExist(strFilePath)))
			{
				Intent intent = new Intent(getContext(), WebBrowserActivity.class);
				intent.putExtra(EXTRA_URL, url);
				getContext().startActivity(intent);
				return true;
			}

			if (url.startsWith("mailto:"))
			{
				MailTo mt = MailTo.parse(url);
				Intent i = newEmailIntent(Global.theActivity, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
				Global.theActivity.startActivity(i);
			}

			return mbOverLoadUrl;
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			mbOverLoadUrl = true;
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			mbOverLoadUrl = false;
			super.onPageStarted(view, url, favicon);
		}

		private Intent newEmailIntent(Context context, String address, String subject, String body, String cc)
		{
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
			intent.putExtra(Intent.EXTRA_TEXT, body);
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent.putExtra(Intent.EXTRA_CC, cc);
			intent.setType("message/rfc822");
			return intent;
		}

	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
		Global.addActiveNotify(nChapter, nPage, selfHandler);
	}

	public void setDisplaySize(int nWidth, int nHeight)
	{
		this.setLayoutParams(new RelativeLayout.LayoutParams(nWidth, nHeight));
	}

	public void setAutoPlay(boolean bAutoPlay)
	{
		mbAutoPlay = bAutoPlay;
	}

	private OnLongClickListener	longClickListener		= new OnLongClickListener()
														{
															@Override
															public boolean onLongClick(View v)
															{
																scrollTo(0, 0);
																return true;
															}
														};

	private OnTouchListener		onTouchListener			= new OnTouchListener()
														{

															@Override
															public boolean onTouch(View v, MotionEvent event)
															{
																switch (event.getAction())
																{
																case MotionEvent.ACTION_DOWN:
																	scrollTo(0, 0);
																	break;
																case MotionEvent.ACTION_MOVE:
																	break;
																case MotionEvent.ACTION_CANCEL:
																case MotionEvent.ACTION_UP:
																	scrollTo(0, 0);
																	break;
																case MotionEvent.ACTION_SCROLL:
																	scrollTo(0, 0);
																	return true;
																}
																return gestureDetector.onTouchEvent(event);
															}

														};

	SimpleOnGestureListener		simpleOnGestureListener	= new SimpleOnGestureListener()
														{

															@Override
															public boolean onFling(MotionEvent e1, MotionEvent e2,
																	float velocityX, float velocityY)
															{
																float sensitvity = 50;

																if (null == e1 || null == e2)
																{
																	return false;
																}
																if ((e1.getX() - e2.getX()) > sensitvity)
																{
																	// left
																	EventHandler.notify(Global.handlerActivity,
																			EventMessage.MSG_JUMP, mnChapter + 1,
																			Type.INVALID, null);
																}
																else if ((e2.getX() - e1.getX()) > sensitvity)
																{
																	// right
																}

																if ((e1.getY() - e2.getY()) > sensitvity)
																{
																	// up
																}
																else if ((e2.getY() - e1.getY()) > sensitvity)
																{
																	// down
																}

																return super.onFling(e1, e2, velocityX, velocityY);
															}

															@Override
															public boolean onDoubleTap(MotionEvent e)
															{
																EventHandler.notify(Global.handlerActivity,
																		EventMessage.MSG_DOUBLE_CLICK, Type.INVALID,
																		Type.INVALID, null);
																return true;
															}

														};

	private Handler				selfHandler				= new Handler()
														{
															@Override
															public void handleMessage(Message msg)
															{
																switch (msg.what)
																{
																case EventMessage.MSG_CURRENT_ACTIVE:
																	if (mbAutoPlay)
																	{
																		InteractiveWebView.this.reload();
																	}
																	Logs.showTrace("Web Page tag="
																			+ InteractiveWebView.this.getTag()
																			+ " is active");
																	break;
																}
															}
														};

}

package interactive.view.webview;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.view.global.Global;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class WebBrowser extends RelativeLayout
{

	private final int	ID_OPTION_BAR	= ++Global.mnUserId;
	private final int	ID_FLASH		= ++Global.mnUserId;
	private final int	ID_BACK			= ++Global.mnUserId;
	private final int	ID_PRE_PAGE		= ++Global.mnUserId;
	private final int	ID_NEXT_PAGE	= ++Global.mnUserId;
	private Handler		notifyHandler	= null;

	private WebView		webView			= null;

	public WebBrowser(Context context)
	{
		super(context);
		init(context);
	}

	public WebBrowser(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public WebBrowser(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init(Context context)
	{
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		int nBtnSize = Global.ScaleSize(27);

		/** create option bar */
		RelativeLayout rlLayout = new RelativeLayout(context);
		rlLayout.setId(ID_OPTION_BAR);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				Global.ScaleSize(44));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		rlLayout.setLayoutParams(layoutParams);
		rlLayout.setBackgroundColor(Color.parseColor("#f4f4f4f2"));
		rlLayout.setPadding(Global.ScaleSize(30), Global.ScaleSize(7), Global.ScaleSize(30), Global.ScaleSize(10));
		this.addView(rlLayout);

		webView = new WebView(context);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		webView.setInitialScale(1);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);

		RelativeLayout.LayoutParams webLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		webLayoutParams.addRule(RelativeLayout.BELOW, rlLayout.getId());
		webView.setLayoutParams(webLayoutParams);
		webView.setWebViewClient(new myWebViewClient());
		this.addView(webView);

		ImageView imageReflash = new ImageView(context);
		ImageView imageBack = new ImageView(context);
		ImageView imagePrePage = new ImageView(context);
		ImageView imageNextPage = new ImageView(context);

		RelativeLayout.LayoutParams reflashLayoutParams = new RelativeLayout.LayoutParams(nBtnSize, nBtnSize);
		reflashLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		imageReflash.setId(ID_FLASH);
		imageReflash.setLayoutParams(reflashLayoutParams);
		imageReflash.setImageResource(Global.getResourceId(context, "reflash_normal", "drawable"));
		imageReflash.setScaleType(ScaleType.CENTER_INSIDE);
		rlLayout.addView(imageReflash);

		RelativeLayout.LayoutParams backLayoutParams = new RelativeLayout.LayoutParams(nBtnSize, nBtnSize);
		backLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		imageBack.setId(ID_BACK);
		imageBack.setLayoutParams(backLayoutParams);
		imageBack.setImageResource(Global.getResourceId(context, "exit_normal", "drawable"));
		imageBack.setScaleType(ScaleType.CENTER_INSIDE);
		rlLayout.addView(imageBack);

		RelativeLayout.LayoutParams nextPageLayoutParams = new RelativeLayout.LayoutParams(nBtnSize, nBtnSize);
		nextPageLayoutParams.addRule(RelativeLayout.LEFT_OF, imageReflash.getId());
		nextPageLayoutParams.setMargins(0, 0, Global.ScaleSize(65), 0);
		imageNextPage.setId(ID_NEXT_PAGE);
		imageNextPage.setLayoutParams(nextPageLayoutParams);
		imageNextPage.setImageResource(Global.getResourceId(context, "back_normal", "drawable"));
		imageNextPage.setScaleType(ScaleType.CENTER_INSIDE);
		rlLayout.addView(imageNextPage);

		RelativeLayout.LayoutParams prePageLayoutParams = new RelativeLayout.LayoutParams(nBtnSize, nBtnSize);
		prePageLayoutParams.addRule(RelativeLayout.LEFT_OF, imageNextPage.getId());
		prePageLayoutParams.setMargins(0, 0, Global.ScaleSize(50), 0);
		imagePrePage.setId(ID_PRE_PAGE);
		imagePrePage.setLayoutParams(prePageLayoutParams);
		imagePrePage.setImageResource(Global.getResourceId(context, "forward_normal", "drawable"));
		imagePrePage.setScaleType(ScaleType.CENTER_INSIDE);
		rlLayout.addView(imagePrePage);

		imageReflash.setOnTouchListener(onTouchListener);
		imageBack.setOnTouchListener(onTouchListener);
		imagePrePage.setOnTouchListener(onTouchListener);
		imageNextPage.setOnTouchListener(onTouchListener);

	}

	public void loadURL(String strUrl)
	{
		webView.loadUrl(strUrl);
	}

	private class myWebViewClient extends WebViewClient
	{
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			if (url.startsWith("http:") || url.startsWith("https:"))
			{
				loadURL(url);
			}
			return true;
		}
	}

	private void optionDown(View view)
	{
		if (view.getId() == ID_BACK)
		{
			((ImageView) view).setImageResource(Global.getResourceId(getContext(), "exit_rollover", "drawable"));
		}
		if (view.getId() == ID_PRE_PAGE)
		{
			((ImageView) view).setImageResource(Global.getResourceId(getContext(), "forward_rollover", "drawable"));
		}
		if (view.getId() == ID_NEXT_PAGE)
		{
			((ImageView) view).setImageResource(Global.getResourceId(getContext(), "back_rollover", "drawable"));
		}
		if (view.getId() == ID_FLASH)
		{
			((ImageView) view).setImageResource(Global.getResourceId(getContext(), "reflash_rollover", "drawable"));
		}
	}

	private void optionUp(View view)
	{
		if (view.getId() == ID_BACK)
		{
			EventHandler.notify(notifyHandler, EventMessage.MSG_BROWSER_CLOSE, 0, 0, null);
		}
		if (view.getId() == ID_PRE_PAGE)
		{
			if (webView.canGoBack())
			{
				webView.goBack();
			}
		}
		if (view.getId() == ID_NEXT_PAGE)
		{
			if (webView.canGoForward())
			{
				webView.goForward();
			}
		}
		if (view.getId() == ID_FLASH)
		{
			webView.reload();
		}

		optionCancel(view);

	}

	private void optionCancel(View view)
	{
		if (view.getId() == ID_BACK)
		{
			((ImageView) view).setImageResource(Global.getResourceId(getContext(), "exit_normal", "drawable"));
		}
		if (view.getId() == ID_PRE_PAGE)
		{
			((ImageView) view).setImageResource(Global.getResourceId(getContext(), "forward_normal", "drawable"));
		}
		if (view.getId() == ID_NEXT_PAGE)
		{
			((ImageView) view).setImageResource(Global.getResourceId(getContext(), "back_normal", "drawable"));
		}
		if (view.getId() == ID_FLASH)
		{
			((ImageView) view).setImageResource(Global.getResourceId(getContext(), "reflash_normal", "drawable"));
		}
	}

	public void setNotifyHandler(Handler handler)
	{
		notifyHandler = handler;
	}

	private OnTouchListener	onTouchListener	= new OnTouchListener()
											{

												@Override
												public boolean onTouch(View view, MotionEvent event)
												{
													switch (event.getAction())
													{
													case MotionEvent.ACTION_DOWN:
														optionDown(view);
														break;
													case MotionEvent.ACTION_UP:
														optionUp(view);
														break;
													case MotionEvent.ACTION_CANCEL:
														optionCancel(view);
														break;
													}
													return true;
												}

											};
}

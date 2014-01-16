package interactive.view.webview;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.data.PageData;
import interactive.view.global.Global;
import interactive.view.scrollable.ScrollableView;
import interactive.view.type.InteractiveType;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class InteractiveWebView extends WebView
{
	private SparseArray<InteractiveImage>	listInteractiveImage	= null;		// webview clicked then hide
	private Handler							pageReaderHandler		= null;		//send message to PageReader
	private boolean							mbOverLoadUrl			= false;
	private int								mnJumpChapter			= Type.INVALID;
	private int								mnJumpPage				= Type.INVALID;
	private int								mnChapter				= Type.INVALID; // self chapter
	private int								mnPage					= Type.INVALID; // self page
	private SparseArray<ObjectHandle>		listObjHandle			= null;
	private GestureDetector					gestureDetector			= null;
	private String							mstrBackgroundImage		= null;

	private class InteractiveImage
	{
		public String	mstrImageTag	= null;
		public String	mstrGroupId		= null;

		public InteractiveImage(String strImageTag, String strGroupId)
		{
			mstrImageTag = strImageTag;
			mstrGroupId = strGroupId;
		}
	}

	public class ObjectHandle
	{
		public Handler	handler			= null;
		public int		mnObjectType	= Type.INVALID;
		public boolean	mbAutoPlay		= false;

		public ObjectHandle()
		{
		}
	}

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

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void init()
	{
		this.getSettings().setLoadWithOverviewMode(true);
		this.getSettings().setUseWideViewPort(false);
		this.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setPluginState(PluginState.ON);
		this.getSettings().setBuiltInZoomControls(true);
		this.getSettings().setSupportZoom(false);
		this.getSettings().setAllowContentAccess(false);
		this.getSettings().setDisplayZoomControls(false);
		this.getSettings().setAllowFileAccess(true);
		this.getSettings().setDefaultTextEncodingName("utf-8");
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setPluginState(PluginState.ON_DEMAND);

		this.setHorizontalScrollBarEnabled(false);
		this.setVerticalScrollBarEnabled(false);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setClickable(false);
		this.setHorizontalFadingEdgeEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		this.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		this.setOnLongClickListener(longClickListener);
		this.setOnTouchListener(onTouchListener);

		setWebViewClient(new myWebViewClient());
		setWebChromeClient(new WebChromeClient());

		listObjHandle = new SparseArray<ObjectHandle>();

		gestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);

	}

	public void initPageReaderHandler(Handler handler)
	{
		pageReaderHandler = handler;
	}

	//	public void initDisplayPageHandler(Handler handler)
	//	{
	//		displayPageHandler = handler;
	//	}
	//
	//	public Handler getDisplayPageHandler()
	//	{
	//		return displayPageHandler;
	//	}

	public void addObjectHandle(Handler handler, int nObjType, boolean bAutoPlay)
	{
		ObjectHandle objHandle = new ObjectHandle();
		objHandle.handler = handler;
		objHandle.mnObjectType = nObjType;
		objHandle.mbAutoPlay = bAutoPlay;
		listObjHandle.put(listObjHandle.size(), objHandle);
		objHandle = null;
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
			if (url.startsWith("http:") || url.startsWith("https:"))
			{
				// show web active
				return true;
			}
			File file = new File(url);
			String strName = file.getName();
			file = null;
			if (mbOverLoadUrl && InteractiveWebView.this.isPageExist(strName))
			{
				EventHandler.notify(pageReaderHandler, EventMessage.MSG_WEB, EventMessage.MSG_JUMP, mnJumpChapter,
						mnJumpPage);
				mnJumpPage = Type.INVALID;
				mnJumpChapter = Type.INVALID;
				return true;
			}

			return false;
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

	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
	}

	public void setDisplaySize(int nWidth, int nHeight)
	{
		this.setLayoutParams(new RelativeLayout.LayoutParams(nWidth, nHeight));
	}

	public void setItemHide(String strTag, String strGroupId)
	{
		if (null == listInteractiveImage)
		{
			listInteractiveImage = new SparseArray<InteractiveImage>();
		}
		listInteractiveImage.put(listInteractiveImage.size(), new InteractiveImage(strTag, strGroupId));
	}

	private String getGroupId(String strTag)
	{
		for (int i = 0; i < listInteractiveImage.size(); ++i)
		{
			if (listInteractiveImage.get(i).mstrImageTag.equals(strTag))
			{
				return listInteractiveImage.get(i).mstrGroupId;
			}
		}
		return null;
	}

	public void hideItem(String strExpItem)
	{
		if (null == listInteractiveImage)
		{
			return;
		}
		String strGroupId = getGroupId(strExpItem);
		for (int i = 0; i < listInteractiveImage.size(); ++i)
		{
			for (int j = 0; j < this.getChildCount(); ++j)
			{
				if (null == this.getChildAt(j).getTag()
						|| (null != strExpItem && this.getChildAt(j).getTag().equals(strExpItem)))
				{
					continue;
				}
				if (this.getChildAt(j).getTag().equals(listInteractiveImage.get(i).mstrImageTag))
				{
					if (null == strGroupId)
					{
						this.getChildAt(j).setVisibility(View.GONE);
					}
					else
					{
						if (listInteractiveImage.get(i).mstrGroupId.equals(strGroupId))
						{
							this.getChildAt(j).setVisibility(View.GONE);
						}
					}
				}
			}
		}
	}

	public void setCurrentView(boolean bCurrent)
	{
		scrollTo(0, 0);
		if (bCurrent)
		{
			if (null != listObjHandle)
			{
				for (int i = 0; i < listObjHandle.size(); ++i)
				{
					ObjectHandle objHandle = listObjHandle.get(i);
					switch (objHandle.mnObjectType)
					{
					case InteractiveType.OBJECT_CATEGORY_VIDEO:
						if (objHandle.mbAutoPlay)
						{
							/**
							 * notify video to play, if video have autoplay
							 */
							EventHandler.notify(objHandle.handler, EventMessage.MSG_WEB, EventMessage.MSG_VIDEO_PLAY,
									0, null);
						}
						break;
					}
				}
			}
		}
		else
		{
			//		EventHandler.notify(displayPageHandler, EventMessage.MSG_WEB, EventMessage.WND_STOP, Type.INVALID, null);
		}
	}

	public void setBackgroundImage(String strImage)
	{
		mstrBackgroundImage = strImage;
	}

	public String getBackgroundImage()
	{
		return mstrBackgroundImage;
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
																	EventHandler.notify(pageReaderHandler,
																			EventMessage.MSG_WEB,
																			EventMessage.MSG_JUMP, mnChapter + 1,
																			Type.INVALID);
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
																return super.onDoubleTap(e);
															}

														};

	private Handler				webHandler				= new Handler()
														{
															@Override
															public void handleMessage(Message msg)
															{
																switch (msg.what)
																{
																case ScrollableView.SCROLL_LEFT_END:
																	EventHandler.notify(pageReaderHandler,
																			EventMessage.MSG_WEB,
																			EventMessage.MSG_JUMP, mnChapter + 1,
																			Type.INVALID);
																	break;
																case ScrollableView.SCROLL_RIGHT_END:
																	EventHandler.notify(pageReaderHandler,
																			EventMessage.MSG_WEB,
																			EventMessage.MSG_JUMP, mnChapter - 1,
																			Type.INVALID);
																	break;
																case ScrollableView.SCROLL_TOP_END:
																	EventHandler.notify(pageReaderHandler,
																			EventMessage.MSG_WEB,
																			EventMessage.MSG_JUMP, Type.INVALID,
																			mnPage + 1);
																	break;
																case ScrollableView.SCROLL_DOWN_END:
																	EventHandler.notify(pageReaderHandler,
																			EventMessage.MSG_WEB,
																			EventMessage.MSG_JUMP, Type.INVALID,
																			mnPage - 1);
																	break;
																case ScrollableView.DOUBLE_CLICK:
																	//																	EventHandler.notify(displayPageHandler,
																	//																			EventMessage.MSG_WEB,
																	//																			EventMessage.MSG_DOUBLE_CLICK,
																	//																			Type.INVALID, null);
																	EventHandler.notify(Global.handlerActivity,
																			EventMessage.MSG_DOUBLE_CLICK,
																			Type.INVALID, Type.INVALID, null);
																	break;
																case EventMessage.MSG_SHOW_ITEM: // button click and show item
																	hideItem((String) msg.obj);
																	break;
																}
															}
														};

	public Handler getWebHandler()
	{
		return webHandler;
	}

}

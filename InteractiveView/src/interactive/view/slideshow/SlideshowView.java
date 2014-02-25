package interactive.view.slideshow;

import interactive.common.BitmapHandler;
import interactive.common.Device;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveMediaLayout;
import interactive.view.image.ImageViewHandler;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class SlideshowView extends RelativeLayout
{

	public static final String						EXTRA_CURRENT_ITEM		= "current_item";
	public static final String						EXTRA_GALLERY_ITEM		= "gallery_item";
	public static final String						EXTRA_ORIENTATION		= "orientation";

	private HorizontalScrollView					horizontalScrollView	= null;
	private HorizontalScrollView					horizontalThumbnail		= null;
	private LinearLayout							linearLayout			= null;
	private LinearLayout							lLayoutIndicator		= null;
	private LinearLayout							lLayoutThumbnail		= null;
	private RelativeLayout							rLayoutIndicator		= null;
	private RelativeLayout							rLayoutThumbnail		= null;
	private RelativeLayout							rLayoutScaleImage		= null;
	private TextView								textViewTitle			= null;
	private TextView								textViewDescript		= null;
	private Runnable								scrollerTask;
	private final int								newCheck				= 100;
	private int										mnX						= 0;
	private BaseAdapter								theAdapter				= null;
	private int										mnOffSetCount			= 0;
	private int										mnCurrentItem			= 0;
	private Activity								theActivity				= null;
	private int										mnDisplayWidth			= LayoutParams.MATCH_PARENT;
	private int										mnDisplayHeight			= LayoutParams.MATCH_PARENT;
	private SparseArray<ImageView>					listIndicator			= null;
	private GestureDetector							gestureDetector			= null;
	private ScaleGestureDetector					scaleGestureDetector	= null;
	private ImageView								imgScale				= null;
	private int										mnWidth					= 0;
	private int										mnHeight				= 0;
	private Bitmap									bitmapScale				= null;
	private ArrayList<SlideshowViewItem>			listGalleryItem			= null;
	private boolean									mbEnableFullScreen		= false;
	private boolean									mbShowThumbnail			= false;
	private boolean									mbShowIndicator			= false;
	private Activity								slideViewActivity		= null;
	private boolean									mbIsFullScreen			= false;
	private SparseArray<onScrollStopListner>		listOnScrollstopListner	= null;
	private SparseArray<OnSlideshowItemSwitched>	listOnItemSwitched		= null;
	private boolean									mbFlingOnePage			= false;
	private int										mnDeviceWidth			= Type.INVALID;
	private int										mnDeviceHeight			= Type.INVALID;
	private SparseArray<String>						listMediaTag			= null;
	private int										mnChapter				= Type.INVALID;
	private int										mnPage					= Type.INVALID;
	private ImageViewHandler						imageHandler			= null;
	private boolean									mbCurrentActive			= false;
	private Runnable								runInitImage			= null;
	private ProgressBar								progressBar				= null;

	public interface OnSlideshowItemSwitched
	{
		void onItemSwitched();
	}

	public interface onScrollStopListner
	{
		void onScrollStoped();
	}

	public SlideshowView(Activity activity)
	{
		super(activity);
		theActivity = activity;
		init(activity);
	}

	public SlideshowView(Context context)
	{
		super(context);
		init(context);
	}

	public SlideshowView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public SlideshowView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	protected void finalize() throws Throwable
	{
		listOnScrollstopListner.clear();
		listOnScrollstopListner = null;
		linearLayout.removeAllViewsInLayout();
		linearLayout = null;
		horizontalScrollView.removeAllViewsInLayout();
		horizontalScrollView = null;
		listIndicator.clear();
		listIndicator = null;
		imageHandler = null;
		this.removeAllViewsInLayout();
		super.finalize();
	}

	private void init(Context context)
	{
		this.setBackgroundColor(Color.TRANSPARENT);

		/** init slide */
		scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
		horizontalScrollView = new HorizontalScrollView(context);
		horizontalScrollView.setId(547878);
		linearLayout = new LinearLayout(context);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		horizontalScrollView.setLayoutParams(layoutParams);
		horizontalScrollView.setBackgroundColor(Color.TRANSPARENT);

		linearLayout
				.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		horizontalScrollView.addView(linearLayout);
		linearLayout.setBackgroundColor(Color.TRANSPARENT);
		addView(horizontalScrollView);

		listOnScrollstopListner = new SparseArray<onScrollStopListner>();
		horizontalScrollView.setSmoothScrollingEnabled(true);
		horizontalScrollView.setOnTouchListener(onHVTouchListener);
		horizontalScrollView.setHorizontalScrollBarEnabled(false);
		initScrollerTask();

		setOnScrollStopListner(new onScrollStopListner()
		{
			@Override
			public void onScrollStoped()
			{
				setViewCenter();
			}
		});

		/** init indicator layout */
		rLayoutIndicator = new RelativeLayout(context);
		RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rLayoutIndicator.setLayoutParams(layoutParams1);

		lLayoutIndicator = new LinearLayout(context);
		lLayoutIndicator.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 25));
		lLayoutIndicator.setAlpha(1.0f);
		lLayoutIndicator.setBackgroundColor(Color.parseColor("#80000000"));
		lLayoutIndicator.setGravity(Gravity.CENTER);
		lLayoutIndicator.setOrientation(LinearLayout.HORIZONTAL);

		rLayoutIndicator.addView(lLayoutIndicator);
		addView(rLayoutIndicator);
		rLayoutIndicator.bringToFront();
		rLayoutIndicator.setVisibility(View.GONE);
		listIndicator = new SparseArray<ImageView>();

		/** init thumbnail layout */
		rLayoutThumbnail = new RelativeLayout(context);
		rLayoutThumbnail.setLayoutParams(layoutParams1);
		rLayoutThumbnail.setBackgroundColor(Color.parseColor("#80000000"));
		rLayoutThumbnail.setAlpha(1.0f);

		horizontalThumbnail = new HorizontalScrollView(context);
		horizontalThumbnail.setLayoutParams(new HorizontalScrollView.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		horizontalThumbnail.setSmoothScrollingEnabled(true);
		horizontalThumbnail.setHorizontalScrollBarEnabled(false);

		lLayoutThumbnail = new LinearLayout(context);
		lLayoutThumbnail.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 120));
		lLayoutThumbnail.setGravity(Gravity.CENTER);
		lLayoutThumbnail.setOrientation(LinearLayout.HORIZONTAL);

		horizontalThumbnail.addView(lLayoutThumbnail);
		rLayoutThumbnail.addView(horizontalThumbnail);
		addView(rLayoutThumbnail);
		rLayoutThumbnail.bringToFront();
		rLayoutThumbnail.setVisibility(View.GONE);

		initGestureDetector();

		/** init Scale */
		imgScale = new ImageView(context);
		imgScale.setScaleType(ScaleType.FIT_XY);
		imgScale.setAdjustViewBounds(false);
		imgScale.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imgScale.setVisibility(View.GONE);

		rLayoutScaleImage = new RelativeLayout(context);
		rLayoutScaleImage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		rLayoutScaleImage.addView(imgScale);

		/** init gallery item */
		listGalleryItem = new ArrayList<SlideshowViewItem>();

		/** init text view */
		textViewTitle = new TextView(context);
		textViewTitle.setTextColor(Color.rgb(255, 255, 255));
		textViewTitle.setTextSize(20);
		RelativeLayout.LayoutParams txtlayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		txtlayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		txtlayoutParams.addRule(RelativeLayout.ABOVE, horizontalScrollView.getId());
		textViewTitle.setLayoutParams(txtlayoutParams);
		textViewTitle.setPadding(20, 0, 0, 40);
		textViewTitle.setText("Hello");

		textViewDescript = new TextView(context);
		textViewDescript.setTextColor(Color.rgb(255, 255, 255));
		textViewDescript.setTextSize(20);
		RelativeLayout.LayoutParams txtlayoutParams2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		txtlayoutParams2.addRule(RelativeLayout.CENTER_VERTICAL);
		txtlayoutParams2.addRule(RelativeLayout.BELOW, horizontalScrollView.getId());
		textViewDescript.setLayoutParams(txtlayoutParams2);
		textViewDescript.setPadding(20, 40, 0, 0);
		textViewDescript.setText("Welcome");

		addView(textViewTitle);
		addView(textViewDescript);

		/** init item switch listener */
		listOnItemSwitched = new SparseArray<OnSlideshowItemSwitched>();

		/** init image viewer */
		imageHandler = new ImageViewHandler();
		runInitImage = new Runnable()
		{
			@Override
			public void run()
			{
				if (imageHandler.isRelease())
				{
					postDelayed(runInitImage, 500);
					return;
				}
				imageHandler.initImageView();
				SlideshowView.this.invalidate();
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
				SlideshowView.this.removeView(progressBar);
			}
		};

		/** init progress bar */
		progressBar = new ProgressBar(context);
		RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(80, 80);
		progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		progressBar.setLayoutParams(progressParams);
		progressBar.getIndeterminateDrawable().setColorFilter(0xFF309FD6, android.graphics.PorterDuff.Mode.MULTIPLY);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus)
		{
			setCurrentItem(mnCurrentItem);
			lLayoutThumbnail.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					this.getHeight() / 4));
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		setCurrentItem(mnCurrentItem);
		startScrollerTask();
		lLayoutThumbnail.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, this.getHeight() / 4));
	}

	public void setActivity(Activity activity)
	{
		theActivity = activity;
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new RelativeLayout.LayoutParams(nWidth, nHeight));
		mnDisplayWidth = nWidth;
		mnDisplayHeight = nHeight;
		lLayoutThumbnail.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, nHeight / 4));
	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
		Global.addActiveNotify(nChapter, nPage, selfHandler);
		Global.addUnActiveNotify(nChapter, nPage, selfHandler);
	}

	public void initImageView()
	{
		imageHandler.initImageView();
	}

	private void setViewCenter()
	{
		int center = horizontalScrollView.getScrollX() + horizontalScrollView.getWidth() / 2;
		int chilrenNum = linearLayout.getChildCount();
		for (int i = 0; i < chilrenNum; ++i)
		{
			View v = linearLayout.getChildAt(i);
			int viewLeft = v.getLeft();
			int viewWidth = v.getWidth();
			if (center >= viewLeft && center <= viewLeft + viewWidth)
			{
				if ((i - mnOffSetCount) < 0)
				{
					setCurrentItem(0);
					setIndicator(true, mnCurrentItem);
					break;
				}
				if ((chilrenNum - mnOffSetCount) <= i)
				{
					scrollToEnd();
					break;
				}
				horizontalScrollView.scrollBy((viewLeft + viewWidth / 2) - center, 0);
				if (mnCurrentItem != (i - mnOffSetCount))
				{
					setIndicator(false, mnCurrentItem);
					mnCurrentItem = i - mnOffSetCount;
					setIndicator(true, mnCurrentItem);
					notifyItemSwitched();
					if (mbShowIndicator)
					{
						rLayoutIndicator.setVisibility(View.VISIBLE);
						rLayoutIndicator.bringToFront();
					}
					Logs.showTrace("slideshow switch item:" + mnCurrentItem);
				}
				break;
			}
		}

		setItemTitle(mnCurrentItem);
		if (this.mbShowThumbnail)
		{
			setThumbnailCenter(mnCurrentItem);
		}
	}

	private void setThumbnailCenter(int nItem)
	{
		View v = lLayoutThumbnail.getChildAt(nItem);

		if (null == v)
		{
			return;
		}

		int center = horizontalThumbnail.getScrollX() + horizontalThumbnail.getWidth() / 2;
		int viewLeft = v.getLeft();
		int viewWidth = v.getWidth();
		horizontalThumbnail.smoothScrollBy((viewLeft + viewWidth / 2) - center, 0);
		invalidate();
	}

	private void setItemTitle(int nCurrentItem)
	{
		SlideshowViewItem viewItem = listGalleryItem.get(nCurrentItem);

		textViewTitle.setText(viewItem.getTitle());
		textViewDescript.setText(viewItem.getDescription());
	}

	private int getCenter()
	{
		return horizontalScrollView.getScrollX() + horizontalScrollView.getWidth() / 2;
	}

	public void scrollToEnd()
	{
		int nLastChild = linearLayout.getChildCount() - mnOffSetCount - 1;
		View v = linearLayout.getChildAt(nLastChild);
		int viewLeft = v.getLeft();
		int viewWidth = v.getWidth();
		horizontalScrollView.scrollBy((viewLeft + viewWidth / 2) - getCenter(), 0);
		mnCurrentItem = (nLastChild - mnOffSetCount);
		setIndicator(true, mnCurrentItem);
		invalidate();
	}

	public void setCurrentItem(int nItem)
	{
		View v = linearLayout.getChildAt(nItem + mnOffSetCount);

		if (null == v)
		{
			return;
		}
		setIndicator(false, mnCurrentItem);
		int viewLeft = v.getLeft();
		int viewWidth = v.getWidth();
		horizontalScrollView.smoothScrollBy((viewLeft + viewWidth / 2) - getCenter(), 0);
		mnCurrentItem = nItem;
		setIndicator(true, mnCurrentItem);
		invalidate();
		notifyItemSwitched();
	}

	public void setOnScrollStopListner(SlideshowView.onScrollStopListner listner)
	{
		if (null != listner)
		{
			listOnScrollstopListner.put(listOnScrollstopListner.size(), listner);
		}
	}

	public void setOnItemSwitchedListener(SlideshowView.OnSlideshowItemSwitched listener)
	{
		if (null != listener)
		{
			listOnItemSwitched.put(listOnItemSwitched.size(), listener);
		}
	}

	private void notifyItemSwitched()
	{
		if (null != listMediaTag)
		{
			for (int i = 0; i < listMediaTag.size(); ++i)
			{
				EventHandler.notify(Global.interactiveHandler.getNotifyHandler(), EventMessage.MSG_MEDIA_STOP,
						Type.INVALID, Type.INVALID, listMediaTag.get(i));
			}
		}

		for (int i = 0; i < listOnItemSwitched.size(); ++i)
		{
			listOnItemSwitched.get(i).onItemSwitched();
		}
	}

	private void initScrollerTask()
	{
		scrollerTask = new Runnable()
		{
			@Override
			public void run()
			{
				int newPosition = horizontalScrollView.getScrollX();
				if (mnX - newPosition == 0)
				{
					for (int i = 0; i < listOnScrollstopListner.size(); ++i)
					{
						listOnScrollstopListner.get(i).onScrollStoped();
					}
				}
				else
				{
					mnX = horizontalScrollView.getScrollX();
					postDelayed(scrollerTask, newCheck);
				}
			}
		};
	}

	private void startScrollerTask()
	{
		mnX = horizontalScrollView.getScrollX();
		postDelayed(scrollerTask, newCheck);
	}

	@SuppressWarnings("unused")
	private int setOffSetTemp(int nTempWidth)
	{
		View viewOffSet = new View(theActivity);
		viewOffSet.setLayoutParams(new ViewGroup.LayoutParams(nTempWidth, 0));
		linearLayout.addView(viewOffSet);
		return 1;
	}

	private int setOffSetWidth(int nChildWidth)
	{
		if (null == theActivity || 0 >= nChildWidth)
		{
			View viewOffSet = new View(theActivity);
			viewOffSet.setLayoutParams(new ViewGroup.LayoutParams(this.getDisplayWidth() / 2, 0));
			linearLayout.addView(viewOffSet);
			return 1;
		}
		int nWidth = 0;

		if (0 >= mnDisplayWidth)
		{
			nWidth = getDisplayWidth();
		}
		else
		{
			nWidth = mnDisplayWidth; // custom set display
		}

		int offSetW = (nWidth / 2) / nChildWidth;
		int nRemin = (nWidth / 2) % nChildWidth;
		if (nRemin >= (nChildWidth / 2))
		{
			++offSetW;
		}

		for (int nOffSet = 0; nOffSet < offSetW; ++nOffSet)
		{
			View viewOffSet = new View(theActivity);
			viewOffSet.setLayoutParams(new ViewGroup.LayoutParams(nChildWidth, 0));
			linearLayout.addView(viewOffSet);
		}

		return offSetW;
	}

	private int getDisplayWidth()
	{
		Device device = new Device(getContext());
		int nWidth = device.getDeviceWidth();
		device = null;
		return nWidth;
	}

	private int getDisplayHeight()
	{
		Device device = new Device(getContext());
		int nHeight = device.getDeviceHeight();
		device = null;
		return nHeight;
	}

	private ImageView getImageView(int nResId, String strPath)
	{
		ImageView imageview = new ImageView(getContext());
		imageview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageview.setScaleType(ScaleType.CENTER_CROP);
		imageview.setAdjustViewBounds(false);
		imageview.setBackgroundColor(Color.TRANSPARENT);
		imageview.setId(Global.getUserId());

		if (0 < nResId)
		{
			imageview.setImageResource(nResId);
		}
		else if (null != strPath)
		{
			int nBitmapWidth = Global.ScaleSize(BitmapHandler.getBitmapWidth(strPath));
			int nBitmapHeight = Global.ScaleSize(BitmapHandler.getBitmapHeight(strPath));
			if (0 >= nBitmapWidth)
			{
				nBitmapWidth = 800;
			}

			if (0 >= nBitmapHeight)
			{
				nBitmapHeight = 800;
			}

			Logs.showTrace("Slideshow get image width=" + nBitmapWidth + " height=" + nBitmapHeight);
			//Bitmap bmp = BitmapHandler.readBitmap(theActivity, strPath, nBitmapWidth, nBitmapHeight);
			//	imageview.setImageBitmap(bmp);
			imageHandler.addImageView(imageview, strPath, nBitmapWidth, nBitmapHeight);
		}
		else
		{
			return null;
		}
		return imageview;
	}

	public void setItem(SparseArray<SlideshowViewItem> listItem, int nItemWidth, int nItemHeight, boolean bFullScreen)
	{
		mbIsFullScreen = bFullScreen;

		int nIndex = 0;
		if (null == listItem || 0 >= listItem.size())
		{
			return;
		}

		for (nIndex = 0; nIndex < listItem.size(); ++nIndex)
		{
			SlideshowViewItem viewItem = new SlideshowViewItem(listItem.get(nIndex).getType(), listItem.get(nIndex)
					.getTypeName(), listItem.get(nIndex).getTitle(), listItem.get(nIndex).getDescription(), listItem
					.get(nIndex).getTargetId(), listItem.get(nIndex).getSourceImage());
			switch (listItem.get(nIndex).getType())
			{
			case SlideshowViewItem.TYPE_IMAGE:
				viewItem.setSlideImage(listItem.get(nIndex).getImageName(), listItem.get(nIndex).getImageSrc(),
						listItem.get(nIndex).getImageGroupId());
				break;
			case SlideshowViewItem.TYPE_VIDEO:
				viewItem.setSlideVideo(listItem.get(nIndex).getVideoName(), listItem.get(nIndex).getVideoSrc(),
						listItem.get(nIndex).getVideoId(), listItem.get(nIndex).getVideoType(), listItem.get(nIndex)
								.getVideoStart(), listItem.get(nIndex).getVideoEnd(), listItem.get(nIndex)
								.getVideoAutoplay(), listItem.get(nIndex).getVideoLoop(), listItem.get(nIndex)
								.getVideoPlayerControls());
				break;
			}
			listGalleryItem.add(viewItem);
		}

		linearLayout.removeAllViewsInLayout();
		theAdapter = null;

		int nWidth = nItemWidth;
		if (0 >= nItemWidth)
		{
			nWidth = this.getDisplayWidth();
		}

		//	mnOffSetCount = setOffSetWidth(nWidth);
		//mnOffSetCount = setOffSetTemp(nWidth / 2);

		for (nIndex = 0; nIndex < listGalleryItem.size(); ++nIndex)
		{
			SlideshowViewItem gitem = listGalleryItem.get(nIndex);
			ImageView imgView = null;

			switch (gitem.getType())
			{
			case SlideshowViewItem.TYPE_IMAGE:
				if (mbIsFullScreen)
				{
					imgView = getImageView(Type.INVALID, gitem.getSourceImage());
				}
				else
				{
					imgView = getImageView(Type.INVALID, gitem.getImageSrc());
				}
				if (null != imgView)
				{
					imgView.setLayoutParams(new ViewGroup.LayoutParams(nItemWidth, nItemHeight));
					linearLayout.addView(imgView);
				}
				break;
			case SlideshowViewItem.TYPE_VIDEO:
				InteractiveMediaLayout localVideo = initMedia(gitem, nWidth, nItemHeight);
				linearLayout.addView(localVideo);
				break;
			}
		}

		//setOffSetWidth(nWidth);
		//setOffSetTemp(nWidth / 2);
		initIndicator(listGalleryItem.size());
		initThumbnail();
	}

	private InteractiveMediaLayout initMedia(SlideshowViewItem viewItem, int nWidth, int nHeight)
	{
		InteractiveMediaLayout mediaLayout = new InteractiveMediaLayout(getContext());
		mediaLayout.setMediaTag(viewItem.getVideoName());
		mediaLayout.setPosition(mnChapter, mnPage);
		mediaLayout.setAutoplay(viewItem.getVideoAutoplay());
		mediaLayout.setBackgroundColor(Color.BLACK);
		mediaLayout.setLayoutParams(new ViewGroup.LayoutParams(nWidth, nHeight));
		if (mbIsFullScreen)
		{
			mediaLayout.setBackground(viewItem.getSourceImage(), nWidth, nHeight);
		}
		else
		{
			mediaLayout.setBackground(viewItem.getVideoSrc(), nWidth, nHeight);
		}
		mediaLayout.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
		mediaLayout.setOnVideoPlayListner(new InteractiveMediaLayout.OnVideoPlayListner()
		{
			@Override
			public void onVideoPlayed()
			{
				rLayoutIndicator.setVisibility(View.GONE);
				rLayoutThumbnail.setVisibility(View.GONE);
				rLayoutScaleImage.setVisibility(View.GONE);
			}
		});

		if (null == listMediaTag)
		{
			listMediaTag = new SparseArray<String>();
		}
		listMediaTag.put(listMediaTag.size(), viewItem.getVideoName());
		return mediaLayout;
	}

	public void setAdapter(BaseAdapter adapter, int nItemWidth, int nItemHeight)
	{
		if (null == adapter || 0 >= adapter.getCount())
		{
			return;
		}

		linearLayout.removeAllViewsInLayout();
		theAdapter = adapter;

		mnOffSetCount = setOffSetWidth(nItemWidth);

		int nWidth = 0;
		if (0 >= nItemWidth)
		{
			nWidth = this.getDisplayWidth();
		}
		for (int i = 0; i < theAdapter.getCount(); ++i)
		{
			View viewHold = theAdapter.getView(i, null, null);
			viewHold.setLayoutParams(new ViewGroup.LayoutParams(nWidth, nItemHeight));
			linearLayout.addView(viewHold);
		}

		setOffSetWidth(nItemWidth);
		initIndicator(theAdapter.getCount());
	}

	private void initIndicator(int nCount)
	{
		if (0 >= nCount)
		{
			return;
		}
		lLayoutIndicator.removeAllViewsInLayout();
		listIndicator.clear();
		for (int i = 0; i < nCount; ++i)
		{
			ImageView img = new ImageView(getContext());
			img.setLayoutParams(new LinearLayout.LayoutParams(18, 18));
			img.setImageResource(getResourceId("dot_unfocuse", "drawable"));
			img.setPadding(0, 0, 10, 0);
			lLayoutIndicator.addView(img);
			listIndicator.put(listIndicator.size(), img);
		}
	}

	private void setIndicator(boolean bSelected, int nPosition)
	{

		if (null == listIndicator || null == listIndicator.get(nPosition) || 0 >= listIndicator.size()
				|| nPosition >= listIndicator.size())
		{
			return;
		}

		if (bSelected)
		{
			listIndicator.get(nPosition).setImageResource(getResourceId("dot_focuse", "drawable"));
		}
		else
		{
			listIndicator.get(nPosition).setImageResource(getResourceId("dot_unfocuse", "drawable"));
		}
	}

	public void setIndicatorShow(boolean bShow)
	{
		mbShowIndicator = bShow;
		if (bShow)
		{
			rLayoutIndicator.setVisibility(View.VISIBLE);
		}
		else
		{
			rLayoutIndicator.setVisibility(View.GONE);
		}
	}

	public int getCurrentItem()
	{
		return mnCurrentItem;
	}

	public View getCurrentView()
	{
		return linearLayout.getChildAt(getCurrentItem());
	}

	private int getResourceId(String name, String defType)
	{
		if (null == theActivity)
		{
			return -1;
		}
		return theActivity.getResources().getIdentifier(name, defType, theActivity.getPackageName());
	}

	private void initGestureDetector()
	{
		gestureDetector = new GestureDetector(getContext(), new DoubleTapListener());
	}

	private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{
			showThumbnail();
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			if (mbFlingOnePage)
			{
				goOnePage(e1, e2, velocityX, velocityY);
				return true;
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			EventHandler
					.notify(Global.handlerActivity, EventMessage.MSG_DOUBLE_CLICK, Type.INVALID, Type.INVALID, null);
			return super.onDoubleTap(e);
		}

	}

	public void setFlingOnePage(boolean bOnePage)
	{
		mbFlingOnePage = bOnePage;
	}

	private void goOnePage(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{

		float sensitvity = 50;

		if (null == e1 || null == e2)
		{
			return;
		}
		if ((e1.getX() - e2.getX()) > sensitvity)
		{
			// left
			setCurrentItem(mnCurrentItem + 1);
		}
		else if ((e2.getX() - e1.getX()) > sensitvity)
		{
			// right
			setCurrentItem(mnCurrentItem - 1);
		}

		if ((e1.getY() - e2.getY()) > sensitvity)
		{
			// up
		}
		else if ((e2.getY() - e1.getY()) > sensitvity)
		{
			// down
		}
	}

	public void showThumbnail()
	{
		if (mbShowThumbnail)
		{
			if (rLayoutThumbnail.getVisibility() == View.GONE)
			{
				if (rLayoutIndicator.getVisibility() == View.VISIBLE)
				{
					rLayoutIndicator.setVisibility(View.GONE);
				}
				textViewDescript.setVisibility(View.GONE);
				rLayoutThumbnail.setVisibility(View.VISIBLE);
			}
			else
			{
				rLayoutThumbnail.setVisibility(View.GONE);
				textViewDescript.setVisibility(View.VISIBLE);
				if (mbShowIndicator)
				{
					rLayoutIndicator.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	public void setShowThumbnail(boolean bShow)
	{
		mbShowThumbnail = bShow;
	}

	//	public void initThumbnail(SparseArray<Integer> listImageResId)
	//	{
	//		if (null == listImageResId || 0 >= listImageResId.size())
	//		{
	//			return;
	//		}
	//		lLayoutThumbnail.removeAllViewsInLayout();
	//		for (int i = 0; i < listImageResId.size(); ++i)
	//		{
	//			ImageView img = new ImageView(getContext());
	//			img.setImageResource(listImageResId.get(i));
	//			img.setScaleType(ScaleType.FIT_CENTER);
	//			img.setAdjustViewBounds(true);
	//			img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	//			img.setPadding(3, 4, 3, 4);
	//
	//			lLayoutThumbnail.addView(img);
	//		}
	//	}

	private void initThumbnail()
	{
		if (null == listGalleryItem || 0 >= listGalleryItem.size())
		{
			return;
		}

		lLayoutThumbnail.removeAllViewsInLayout();

		for (int i = 0; i < listGalleryItem.size(); ++i)
		{
			ImageView img = new ImageView(getContext());
			img.setId(Global.getUserId());
			SlideshowViewItem viewItem = listGalleryItem.get(i);
			switch (viewItem.getType())
			{
			case SlideshowViewItem.TYPE_IMAGE:
				setThumbnail(img, viewItem.getImageSrc());
				break;
			case SlideshowViewItem.TYPE_VIDEO:
				setThumbnail(img, viewItem.getVideoSrc());
				break;
			}
			img.setScaleType(ScaleType.FIT_CENTER);
			img.setAdjustViewBounds(true);
			img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			img.setPadding(3, 4, 3, 4);
			lLayoutThumbnail.addView(img);
			img.setTag(i);
			img.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					int nItemIndex = (Integer) view.getTag();
					setCurrentItem(nItemIndex);
					setThumbnailCenter(nItemIndex);
				}
			});
		}
	}

	private void setThumbnail(ImageView imageView, String strImagePath)
	{
		int nBitmapWidth = BitmapHandler.getBitmapWidth(strImagePath);
		int nBitmapHeight = BitmapHandler.getBitmapHeight(strImagePath);
		if (0 >= nBitmapWidth)
		{
			nBitmapWidth = 200;
		}

		if (0 >= nBitmapHeight)
		{
			nBitmapHeight = 200;
		}
		imageHandler.addImageView(imageView, strImagePath, nBitmapWidth, nBitmapHeight);
		//	Bitmap bitmap = BitmapHandler.readBitmap(theActivity, strImagePath, nBitmapWidth / 4, nBitmapHeight / 4);
		//	imageView.setImageBitmap(bitmap);
	}

	OnTouchListener	onHVTouchListener	= new OnTouchListener()
										{
											@Override
											public boolean onTouch(View v, MotionEvent event)
											{
												if (MotionEvent.ACTION_DOWN == event.getAction())
												{
													EventHandler.notify(Global.handlerActivity,
															EventMessage.MSG_LOCK_HORIZON, 0, 0, null);
												}

												if (MotionEvent.ACTION_UP == event.getAction())
												{
													EventHandler.notify(Global.handlerActivity,
															EventMessage.MSG_UNLOCK_HORIZON, 0, 0, null);
												}
												gestureDetector.onTouchEvent(event);
												scaleGestureDetector.onTouchEvent(event);
												switch (event.getAction())
												{
												case MotionEvent.ACTION_UP:
													startScrollerTask();
													if (mbFlingOnePage)
													{
														return true;
													}
													break;
												}

												return false;
											}
										};

	public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener
	{

		private boolean	bScaleStart	= false;

		public ScaleGestureListener()
		{
			super();
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			float factor = detector.getScaleFactor();

			if (factor > 1.0f)
			{
				if (mnDeviceWidth > (int) (mnWidth * factor) && mnDeviceHeight > (int) (mnHeight * factor))
				{
					imgScale.setLayoutParams(new LayoutParams((int) (mnWidth * factor), (int) (mnHeight * factor)));
				}
				imgScale.setX((float) (detector.getFocusX() - ((mnWidth * factor) / 4)));
				imgScale.setY((float) (detector.getFocusY() - ((mnHeight * factor) / 4)));
				bScaleStart = true;
			}

			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			if (null != slideViewActivity)
			{
				return true;
			}

			bScaleStart = false;
			if (!mbEnableFullScreen)
			{
				return false;
			}

			initScale();

			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			if (null != slideViewActivity)
			{
				slideViewActivity.finish();
				return;
			}

			imgScale.setVisibility(View.GONE);
			if (null != bitmapScale)
			{
				bitmapScale.recycle();
			}
			if (bScaleStart)
			{
				showGalleryActivity();
			}
			SlideshowView.this.setVisibility(View.VISIBLE);
			bScaleStart = false;
			SlideshowView.this.setViewCenter();
			rLayoutScaleImage.setVisibility(View.GONE);
		}
	}

	private void initScale()
	{
		mnWidth = getWidth();
		mnHeight = getHeight();
		setViewCenter();
		bitmapScale = BitmapHandler.loadBitmapFromView(getContext(), SlideshowView.this);
		imgScale.setImageBitmap(bitmapScale);
		imgScale.setVisibility(View.VISIBLE);
		imgScale.bringToFront();
		rLayoutScaleImage.setVisibility(View.VISIBLE);
		rLayoutScaleImage.bringToFront();
		imgScale.setX((int) SlideshowView.this.getX());
		imgScale.setY((int) SlideshowView.this.getY());
		Device device = new Device(getContext());
		mnDeviceWidth = device.getDeviceWidth();
		mnDeviceHeight = device.getDeviceHeight();
		device = null;
	}

	public RelativeLayout getScaleImageView()
	{
		return rLayoutScaleImage;
	}

	private void showGalleryActivity()
	{
		if (0 < listGalleryItem.size())
		{
			Device device = new Device(theActivity);
			int nOrientation = device.getOrientation();
			device = null;

			Intent intent = new Intent(getContext(), SlideshowViewActivity.class);
			intent.putExtra(EXTRA_CURRENT_ITEM, mnCurrentItem);
			intent.putExtra(EXTRA_GALLERY_ITEM, listGalleryItem);
			intent.putExtra(EXTRA_ORIENTATION, nOrientation);
			getContext().startActivity(intent);
			setViewCenter();
		}
	}

	public void setFullScreen(boolean bEnable)
	{
		mbEnableFullScreen = bEnable;
	}

	public void setSlideViewActivity(Activity activity)
	{
		slideViewActivity = activity;
	}

	private Handler	selfHandler	= new Handler()
								{

									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_CURRENT_ACTIVE:
											SlideshowView.this.addView(progressBar);
											mbCurrentActive = true;
											EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0,
													0, null);
											this.postDelayed(runInitImage, 500);
											break;
										case EventMessage.MSG_NOT_CURRENT_ACTIVE:
											if (mbCurrentActive)
											{
												SlideshowView.this.removeView(progressBar);
												mbCurrentActive = false;
												imageHandler.releaseBitmap();
											}
											break;
										}
									}

								};
}

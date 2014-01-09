package interactive.view.slideshow;

import interactive.common.Device;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.global.Global;
import interactive.view.type.InteractiveType;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		this.removeAllViewsInLayout();
		super.finalize();
	}

	private void init(Context context)
	{
		/** init slide */
		scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
		horizontalScrollView = new HorizontalScrollView(context);
		horizontalScrollView.setId(547878);
		linearLayout = new LinearLayout(context);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		horizontalScrollView.setLayoutParams(layoutParams);

		linearLayout
				.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		horizontalScrollView.addView(linearLayout);
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

		this.setBackgroundResource(android.R.color.background_dark);

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
		lLayoutThumbnail.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, nHeight / 4));
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
		imageview.setAdjustViewBounds(true);

		if (0 < nResId)
		{
			imageview.setImageResource(nResId);
		}
		else if (null != strPath)
		{
			imageview.setImageURI(Uri.parse(strPath));
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
					imgView.setLayoutParams(new ViewGroup.LayoutParams(nWidth, nItemHeight));
					linearLayout.addView(imgView);
				}
				break;
			case SlideshowViewItem.TYPE_VIDEO:
				switch (gitem.getVideoType())
				{
				case InteractiveType.VIDEO_TYPE_LOCAL:
					SlideshowViewVideoLayout localVideo = initLocalVideo(gitem, nWidth, nItemHeight);
					linearLayout.addView(localVideo);
					break;
				case InteractiveType.VIDEO_TYPE_TOUTUBE:
					SlideshowViewVideoLayout youtube = initYoutubeVideo(gitem, nWidth, nItemHeight);
					linearLayout.addView(youtube);
					break;
				case InteractiveType.VIDEO_TYPE_URL:
					break;
				}
				break;
			}
		}

		//setOffSetWidth(nWidth);
		//setOffSetTemp(nWidth / 2);
		initIndicator(listGalleryItem.size());
		initThumbnail();
	}

	private SlideshowViewVideoLayout initLocalVideo(SlideshowViewItem viewItem, int nWidth, int nHeight)
	{
		SlideshowViewVideoLayout localVideoLayout = new SlideshowViewVideoLayout(getContext());
		localVideoLayout.setVideoType(InteractiveType.VIDEO_TYPE_LOCAL);
		localVideoLayout.setLayoutParams(new ViewGroup.LayoutParams(nWidth, nHeight));
		if (mbIsFullScreen)
		{
			localVideoLayout.setBackground(viewItem.getSourceImage());
			localVideoLayout.setTag(viewItem.getVideoName() + "full");
		}
		else
		{
			localVideoLayout.setBackground(viewItem.getVideoSrc());
			localVideoLayout.setTag(viewItem.getVideoName());
		}

		localVideoLayout.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
		Global.interactiveHandler.addLocalVideo(localVideoLayout, (String) localVideoLayout.getTag(), false,
				viewItem.getVideoId(), viewItem.getVideoLoop(), viewItem.getVideoPlayerControls());
		localVideoLayout.setOnVideoPlayListner(new SlideshowViewVideoLayout.OnVideoPlayListner()
		{
			@Override
			public void onVideoPlayed()
			{
				rLayoutIndicator.setVisibility(View.GONE);
				rLayoutThumbnail.setVisibility(View.GONE);
				rLayoutScaleImage.setVisibility(View.GONE);
			}
		});
		return localVideoLayout;
	}

	private SlideshowViewVideoLayout initYoutubeVideo(SlideshowViewItem viewItem, int nWidth, int nHeight)
	{
		SlideshowViewVideoLayout youtubeLayout = new SlideshowViewVideoLayout(getContext());
		youtubeLayout.setVideoType(InteractiveType.VIDEO_TYPE_TOUTUBE);
		youtubeLayout.setLayoutParams(new ViewGroup.LayoutParams(nWidth, nHeight));
		if (mbIsFullScreen)
		{
			youtubeLayout.setBackground(viewItem.getSourceImage());
			youtubeLayout.setTag(viewItem.getVideoName() + "full");
		}
		else
		{
			youtubeLayout.setBackground(viewItem.getVideoSrc());
			youtubeLayout.setTag(viewItem.getVideoName());
		}
		youtubeLayout.setNotifyHandler(Global.interactiveHandler.getNotifyHandler());
		Global.interactiveHandler.addYoutubeVideo(youtubeLayout, (String) youtubeLayout.getTag(), false,
				viewItem.getVideoId(), viewItem.getVideoLoop(), viewItem.getVideoPlayerControls());

		youtubeLayout.setOnVideoPlayListner(new SlideshowViewVideoLayout.OnVideoPlayListner()
		{
			@Override
			public void onVideoPlayed()
			{
				rLayoutIndicator.setVisibility(View.GONE);
				rLayoutThumbnail.setVisibility(View.GONE);
				rLayoutScaleImage.setVisibility(View.GONE);
			}
		});
		return youtubeLayout;
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
			Logs.showTrace("go next page #########################");
		}
		else if ((e2.getX() - e1.getX()) > sensitvity)
		{
			// right
			setCurrentItem(mnCurrentItem - 1);
			Logs.showTrace("go pre page #########################");
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

	public void initThumbnail(SparseArray<Integer> listImageResId)
	{
		if (null == listImageResId || 0 >= listImageResId.size())
		{
			return;
		}
		lLayoutThumbnail.removeAllViewsInLayout();
		for (int i = 0; i < listImageResId.size(); ++i)
		{
			ImageView img = new ImageView(getContext());
			img.setImageResource(listImageResId.get(i));
			img.setScaleType(ScaleType.FIT_CENTER);
			img.setAdjustViewBounds(true);
			img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			img.setPadding(3, 4, 3, 4);

			lLayoutThumbnail.addView(img);
		}
	}

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
			SlideshowViewItem viewItem = listGalleryItem.get(i);
			switch (viewItem.getType())
			{
			case SlideshowViewItem.TYPE_IMAGE:
				img.setImageURI(Uri.parse(viewItem.getImageSrc()));
				break;
			case SlideshowViewItem.TYPE_VIDEO:
				img.setImageURI(Uri.parse(viewItem.getVideoSrc()));
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

	OnTouchListener	onHVTouchListener	= new OnTouchListener()
										{
											@Override
											public boolean onTouch(View v, MotionEvent event)
											{
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

			//			if (null != slideViewActivity)
			//			{
			//				if (factor < 1.0f)
			//				{
			//					//					imgScale.setLayoutParams(new LayoutParams((int) (mnWidth * factor), (int) (mnHeight * factor)));
			//					//					imgScale.setX((float) (mnWidth / 2 - (mnWidth * factor) / 3));
			//					//					imgScale.setY((float) (mnHeight / 2 - (mnHeight * factor) / 2));
			//					SlideshowView.this.setLayoutParams(new LayoutParams((int) (mnWidth * factor),
			//							(int) (mnHeight * factor)));
			//					SlideshowView.this.setX((float) (detector.getFocusX() - ((mnWidth * factor) / 4)));
			//					SlideshowView.this.setY((float) (detector.getFocusY() - ((mnHeight * factor) / 4)));
			//					Logs.showTrace("activity #####################################");
			//				}
			//				return false;
			//			}
			if (factor > 1.0f)
			{
				imgScale.setVisibility(View.VISIBLE);
				//	SlideshowView.this.setVisibility(View.GONE);
				imgScale.setLayoutParams(new LayoutParams((int) (mnWidth * factor), (int) (mnHeight * factor)));
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
				//	initScale();
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
		bitmapScale = loadBitmapFromView(SlideshowView.this);
		//	bitmapScale = loadBitmapFromView(horizontalScrollView);
		imgScale.setImageBitmap(bitmapScale);
		imgScale.bringToFront();
		rLayoutScaleImage.setVisibility(View.VISIBLE);
		rLayoutScaleImage.bringToFront();
		imgScale.setX((int) SlideshowView.this.getX());
		imgScale.setY((int) SlideshowView.this.getY());
	}

	private Bitmap loadBitmapFromView(View v)
	{
		int nWidth = v.getLayoutParams().width;
		int nHeight = v.getLayoutParams().height;

		if (0 >= nWidth)
		{
			nWidth = this.getDisplayWidth();
		}

		if (0 >= nHeight)
		{
			nHeight = this.getDisplayHeight();
		}

		Bitmap b = Bitmap.createBitmap(nWidth, nHeight, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.measure(MeasureSpec.makeMeasureSpec(v.getLayoutParams().width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(v.getLayoutParams().height, MeasureSpec.EXACTLY));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		v.draw(c);

		return b;
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

	SimpleOnGestureListener	simpleOnGestureListener	= new SimpleOnGestureListener()
													{

													};
}

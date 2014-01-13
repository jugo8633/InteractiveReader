package interactive.view.gallery;

import interactive.common.Device;
import interactive.common.EventHandler;
import interactive.common.Logs;
import interactive.common.Type;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GalleryView extends HorizontalScrollView
{

	private LinearLayout	linearLayout	= null;
	private Activity		theActivity		= null;
	private Handler			theHandler		= null;
	public static final int	MSG_WND_CLICK	= 0;
	public static final int	MSG_IMAGE_CLICK	= 1;
	private int				mnOffSetCount	= Type.INVALID;
	private int				mnCurrentItem	= 0;

	private Runnable		scrollerTask;
	private int				intitPosition;
	private int				newCheck		= 100;

	public interface onScrollStopListner
	{
		void onScrollStoped();
	}

	private onScrollStopListner	onScrollstopListner;

	public class ImageTag
	{
		public int	mnChapter	= Type.INVALID;
		public int	mnPage		= Type.INVALID;

		public ImageTag(int nChapter, int nPage)
		{
			mnChapter = nChapter;
			mnPage = nPage;
		}
	}

	public GalleryView(Context context)
	{
		super(context);
		init(context);
	}

	public GalleryView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public GalleryView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus)
		{
			setCurrentItem(mnCurrentItem);
			this.setViewCenter();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		setCurrentItem(mnCurrentItem);
		startScrollerTask();
	}

	private void init(Context context)
	{
		linearLayout = new LinearLayout(context);

		this.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		linearLayout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		this.addView(linearLayout);

		linearLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				EventHandler.notify(theHandler, MSG_WND_CLICK, 0, 0, null);
			}
		});

		this.setSmoothScrollingEnabled(true);
		this.setOnTouchListener(onTouchListener);

		scrollerTask = new Runnable()
		{
			@Override
			public void run()
			{
				int newPosition = getScrollX();
				if (intitPosition - newPosition == 0)
				{
					setViewCenter();
					if (onScrollstopListner != null)
					{
						onScrollstopListner.onScrollStoped();
					}
				}
				else
				{
					intitPosition = getScrollX();
					GalleryView.this.postDelayed(scrollerTask, newCheck);
				}
			}
		};
	}

	public void setCurrentItem(int nItem)
	{
		View v = linearLayout.getChildAt(nItem + mnOffSetCount);

		if (null == v)
		{
			return;
		}
		int viewLeft = v.getLeft();
		int viewWidth = v.getWidth();
		this.smoothScrollBy((viewLeft + viewWidth / 2) - getCenter(), 0);
		mnCurrentItem = nItem;
		invalidate();
	}

	private int getCenter()
	{
		return this.getScrollX() + this.getWidth() / 2;
	}

	public void setOnScrollStopListner(GalleryView.onScrollStopListner listner)
	{
		onScrollstopListner = listner;
	}

	public void startScrollerTask()
	{
		intitPosition = getScrollX();
		GalleryView.this.postDelayed(scrollerTask, newCheck);
	}

	public void setActivity(Activity activity)
	{
		theActivity = activity;
	}

	public void setNotifyHandler(Handler handler)
	{
		theHandler = handler;
	}

	public void addChild(View child)
	{
		linearLayout.addView(child);
	}

	public void addChild(SparseArray<SparseArray<ImageView>> listView, int nChildWidth, int nChildHeight)
	{

		if (null == theActivity)
		{
			Logs.showTrace("Invalid Activity!!");
			return;
		}

		mnOffSetCount = setOffSetWidth(nChildWidth);

		LayoutInflater layInflater = (LayoutInflater) theActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int nChapter = 0; nChapter < listView.size(); ++nChapter)
		{
			View viewHold = layInflater.inflate(getResourceId("gallery_view_item", "layout"), null);
			viewHold.setBackgroundResource(android.R.color.transparent);
			LinearLayout verticalLinearLayout = (LinearLayout) viewHold.findViewById(getResourceId(
					"linearLayoutGalleryViewItem", "id"));
			verticalLinearLayout.setLayoutParams(new LayoutParams(nChildWidth, LayoutParams.WRAP_CONTENT));
			verticalLinearLayout.setBackgroundResource(android.R.color.transparent);
			for (int nPage = 0; nPage < listView.get(nChapter).size(); ++nPage)
			{
				ImageTag imgTag = new ImageTag(nChapter, nPage);
				ImageView img = listView.get(nChapter).get(nPage);
				img.setTag(imgTag);
				img.setOnClickListener(imageOnClickListener);
				verticalLinearLayout.addView(img);
				imgTag = null;
				img = null;
			}
			addChild(viewHold);
		}
		setOffSetWidth(nChildWidth);
	}

	private int setOffSetWidth(int nChildWidth)
	{
		Device device = new Device(theActivity);
		int nWidth = device.getDisplayWidth();
		device = null;
		int offSetW = (nWidth / 2) / nChildWidth;
		int nRemin = (nWidth / 2) % nChildWidth;
		if (nRemin >= (nChildWidth / 2))
		{
			++offSetW;
		}

		LayoutInflater layInflater = (LayoutInflater) theActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int nOffSet = 0; nOffSet < offSetW; ++nOffSet)
		{
			View viewOffSet = layInflater.inflate(getResourceId("gallery_view_item", "layout"), null);
			LinearLayout llOffSet = (LinearLayout) viewOffSet.findViewById(getResourceId("linearLayoutGalleryViewItem",
					"id"));
			llOffSet.setLayoutParams(new LayoutParams(nChildWidth, 0));
			ImageView img = new ImageView(theActivity);
			llOffSet.addView(img);
			img = null;
			addChild(viewOffSet);
		}

		return offSetW;
	}

	private int getResourceId(String name, String defType)
	{
		return theActivity.getResources().getIdentifier(name, defType, theActivity.getPackageName());
	}

	private void setViewCenter()
	{
		//get the center
		int center = this.getScrollX() + this.getWidth() / 2;
		int chilrenNum = linearLayout.getChildCount();
		for (int i = 0; i < chilrenNum; ++i)
		{
			View v = linearLayout.getChildAt(i);
			int viewLeft = v.getLeft();
			int viewWidth = v.getWidth();
			if (center >= viewLeft && center <= viewLeft + viewWidth)
			{
				Logs.showTrace("CENTER THIS : " + ((viewLeft + viewWidth / 2) - center) + " child=" + i);

				if ((i - mnOffSetCount) < 0)
				{
					scrollToStart();
					break;
				}
				if ((chilrenNum - mnOffSetCount) <= i)
				{
					scrollToEnd();
					break;
				}
				this.scrollBy((viewLeft + viewWidth / 2) - center, 0);
				mnCurrentItem = i - mnOffSetCount;
				break;
			}
		}
	}

	private void scrollToStart()
	{
		int center = this.getScrollX() + this.getWidth() / 2;
		View v = linearLayout.getChildAt(mnOffSetCount);
		int viewLeft = v.getLeft();
		int viewWidth = v.getWidth();
		this.scrollBy((viewLeft + viewWidth / 2) - center, 0);
		mnCurrentItem = 0;
	}

	private void scrollToEnd()
	{
		int center = this.getScrollX() + this.getWidth() / 2;
		int nLastChild = linearLayout.getChildCount() - mnOffSetCount - 1;
		View v = linearLayout.getChildAt(nLastChild);
		int viewLeft = v.getLeft();
		int viewWidth = v.getWidth();
		this.scrollBy((viewLeft + viewWidth / 2) - center, 0);
		mnCurrentItem = (nLastChild - mnOffSetCount);
	}

	public int getCurrentItem()
	{
		return mnCurrentItem;

	}

	OnClickListener	imageOnClickListener	= new OnClickListener()
											{
												@Override
												public void onClick(View v)
												{
													ImageTag imgTag = (ImageTag) v.getTag();
													EventHandler.notify(theHandler, MSG_IMAGE_CLICK, imgTag.mnChapter,
															imgTag.mnPage, null);
												}
											};

	OnTouchListener	onTouchListener			= new OnTouchListener()
											{
												@Override
												public boolean onTouch(View v, MotionEvent event)
												{
													switch (event.getAction())
													{
													case MotionEvent.ACTION_UP:
														startScrollerTask();
														break;
													}
													return false;
												}
											};

}

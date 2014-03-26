package interactive.widget;

import interactive.common.Device;
import interactive.view.global.Global;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class BookGallery extends RelativeLayout
{

	private ViewPager			viewPager		= null;
	private BookGalleryAdapter	galleryAdapter	= null;
	private IndicatorView		indicator		= null;

	public BookGallery(Context context)
	{
		super(context);
		init(context);
	}

	public BookGallery(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public BookGallery(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{

		/** init indicator layout */
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				ScaleSize(context, 10));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		indicator = new IndicatorView(context);
		indicator.setId(Global.getUserId());
		indicator.setLayoutParams(layoutParams);
		addView(indicator);

		/** init book gallery */
		RelativeLayout.LayoutParams gallerylayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		gallerylayoutParams.addRule(RelativeLayout.ABOVE, indicator.getId());

		viewPager = new ViewPager(context);
		viewPager.setLayoutParams(gallerylayoutParams);
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageScrollStateChanged(int arg0)
			{

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageSelected(int arg0)
			{
				indicator.setPosition(arg0);
			}
		});

		addView(viewPager);

		galleryAdapter = new BookGalleryAdapter();
	}

	private int ScaleSize(Context context, int nSize)
	{
		Device device = new Device(context);
		float fScale = device.getScaleSize();
		device = null;

		int nResultSize = (int) Math.floor(nSize * fScale);
		return nResultSize;
	}

	public void updateGallery()
	{
		viewPager.removeAllViewsInLayout();
		viewPager.setAdapter(galleryAdapter);
		indicator.setCount(galleryAdapter.size());
		indicator.setPosition(0);
	}

	public void addPageView(View view)
	{
		galleryAdapter.addPageView(view);
	}

	public class BookGalleryAdapter extends PagerAdapter
	{

		private SparseArray<View>	listPage	= null;

		public BookGalleryAdapter()
		{
			listPage = new SparseArray<View>();
		}

		@Override
		public int getCount()
		{
			return listPage.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			container.addView(listPage.get(position));
			return listPage.get(position);
		}

		public void addPageView(View pageView)
		{
			listPage.put(listPage.size(), pageView);
		}

		public View getChildView(int nIndex)
		{
			if (null == listPage || 0 > nIndex || nIndex >= listPage.size())
			{
				return null;
			}

			return listPage.get(nIndex);
		}

		public void clear()
		{
			listPage.clear();
		}

		public int size()
		{
			return listPage.size();
		}
	}
}

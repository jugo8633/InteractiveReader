package interactive.widget;

import interactive.common.Device;
import interactive.common.Logs;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BookGallery extends RelativeLayout
{

	private final int				INDICATOR_SIZE		= 25;
	private ViewPager				viewPager			= null;
	private RelativeLayout			rLayoutIndicator	= null;
	private LinearLayout			lLayoutIndicator	= null;
	private SparseArray<ImageView>	listIndicator		= null;
	private BookGalleryAdapter		galleryAdapter		= null;

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
		rLayoutIndicator = new RelativeLayout(context);
		rLayoutIndicator.setId(Global.getUserId());
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rLayoutIndicator.setLayoutParams(layoutParams);

		lLayoutIndicator = new LinearLayout(context);
		lLayoutIndicator.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ScaleSize(context,
				INDICATOR_SIZE)));

		lLayoutIndicator.setGravity(Gravity.CENTER);
		lLayoutIndicator.setOrientation(LinearLayout.HORIZONTAL);
		rLayoutIndicator.addView(lLayoutIndicator);
		addView(rLayoutIndicator);

		listIndicator = new SparseArray<ImageView>();

		/** init book gallery */
		RelativeLayout.LayoutParams gallerylayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		gallerylayoutParams.addRule(RelativeLayout.ABOVE, rLayoutIndicator.getId());

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
		initIndicator(galleryAdapter.size());
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
			img.setLayoutParams(new LinearLayout.LayoutParams(ScaleSize(getContext(), 18), ScaleSize(getContext(), 18)));
			img.setImageResource(Global.getResourceId(getContext(), "dot_unfocuse", "drawable"));
			img.setPadding(0, 0, 10, 0);
			img.setBackgroundColor(Color.RED);
			lLayoutIndicator.addView(img);
			Logs.showTrace("indicator=" + nCount + " #####################");
			listIndicator.put(listIndicator.size(), img);
		}
	}
}

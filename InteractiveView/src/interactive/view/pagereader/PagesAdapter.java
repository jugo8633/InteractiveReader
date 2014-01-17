package interactive.view.pagereader;

import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class PagesAdapter extends VerticalPagerAdapter
{

	private SparseArray<DisplayPage>	listPage	= null;

	public PagesAdapter()
	{
		initData();
	}

	private void initData()
	{
		listPage = new SparseArray<DisplayPage>();
	}

	@Override
	public int getCount()
	{
		return listPage.size();
	}

	@Override
	public int getItemPosition(Object object)
	{
		return super.getItemPosition(object);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup viewPager, int position, Object object)
	{
		viewPager.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		container.addView(listPage.get(position));
		return listPage.get(position);
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	public void addPageView(DisplayPage pageView)
	{
		listPage.put(listPage.size(), pageView);
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

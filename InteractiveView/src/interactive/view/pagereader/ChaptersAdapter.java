package interactive.view.pagereader;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class ChaptersAdapter extends PagerAdapter
{
	private SparseArray<View>	listChapter	= null;

	public ChaptersAdapter()
	{
		super();
		initData();
	}

	private void initData()
	{
		listChapter = new SparseArray<View>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	@Override
	public int getCount()
	{
		return listChapter.size();
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
		container.addView(listChapter.get(position));
		return listChapter.get(position);
	}

	public void addChapterView(View chapterView)
	{
		listChapter.put(listChapter.size(), chapterView);
	}

	public View getChildView(int nIndex)
	{
		if (null == listChapter || 0 > nIndex || nIndex >= listChapter.size())
		{
			return null;
		}

		return listChapter.get(nIndex);
	}

	public void clear()
	{
		listChapter.clear();
	}

	public int size()
	{
		return listChapter.size();
	}
}

package interactive.view.data;

import interactive.common.Type;
import interactive.view.webview.InteractiveWebView;
import android.util.SparseArray;
import android.view.ViewGroup;

public class PageData
{
	public static SparseArray<SparseArray<Data>>	listPageData	= new SparseArray<SparseArray<Data>>();
	private Data									data			= null;

	public class Data
	{
		public int					nWidth			= Type.INVALID;
		public int					nHeight			= Type.INVALID;
		public int					nChapter		= Type.INVALID;
		public int					nPage			= Type.INVALID;
		public String				strPath			= null;
		public String				strName			= null;
		public String				strShapTiny		= null;
		public String				strShapLarge	= null;
		public String				strChapterName	= null;
		public String				strDescript		= null;
		public InteractiveWebView	extWebView		= null;
		public ViewGroup			container		= null;
		public boolean				bIsFavorite		= false;
	}

	public PageData()
	{

	}

	@Override
	protected void finalize() throws Throwable
	{
		data = null;
		super.finalize();
	}

	public Data createData()
	{
		data = new Data();
		return data;
	}

	public static ViewGroup getContainer(int nChapter, int nPage)
	{
		if (Type.INVALID == nChapter || Type.INVALID == nPage)
		{
			return null;
		}
		return listPageData.get(nChapter).get(nPage).container;
	}
}

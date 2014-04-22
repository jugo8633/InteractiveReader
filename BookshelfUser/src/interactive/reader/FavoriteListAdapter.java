package interactive.reader;

import interactive.common.Type;
import interactive.view.global.Global;
import android.app.Activity;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FavoriteListAdapter extends BaseAdapter
{

	private SparseArray<ItemData>	listItemData			= null;
	private LayoutInflater			mInflater				= null;
	private int						mnLayoutId				= Type.INVALID;
	private int						mnImageViewId			= Type.INVALID;
	private int						mnTextViewTitleId		= Type.INVALID;
	private int						mnTextViewDescriptId	= Type.INVALID;
	private ImageView				mImageViewPage			= null;
	private TextView				mTextViewTitle			= null;
	private TextView				mTextViewDescript		= null;

	class ItemData
	{
		public String	strTag			= null;
		public String	mstrImage		= null;
		public int		mnChapter		= Type.INVALID;
		public int		mnPage			= Type.INVALID;
		public String	mstrTitle		= null;
		public String	mstrDescript	= null;

		public ItemData(String strImage, int nChapter, int nPage, String strTitle, String strDescript)
		{
			mstrImage = strImage;
			mnChapter = nChapter;
			mnPage = nPage;
			mstrTitle = strTitle;
			mstrDescript = strDescript;
		}
	}

	public FavoriteListAdapter(Activity activity)
	{
		listItemData = new SparseArray<ItemData>();
		mInflater = LayoutInflater.from(activity);
		mnLayoutId = Global.getResourceId(activity, "reader_favorite_list_view_item", "layout");
		mnImageViewId = Global.getResourceId(activity, "imageViewFavoriteItem", "id");
		mnTextViewTitleId = Global.getResourceId(activity, "textViewFavoriteItemTitle", "id");
		mnTextViewDescriptId = Global.getResourceId(activity, "textViewFavoriteDescript", "id");
	}

	@Override
	public int getCount()
	{
		return listItemData.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View viewHold = mInflater.inflate(mnLayoutId, null);

		mImageViewPage = (ImageView) viewHold.findViewById(mnImageViewId);
		mTextViewTitle = (TextView) viewHold.findViewById(mnTextViewTitleId);
		mTextViewDescript = (TextView) viewHold.findViewById(mnTextViewDescriptId);

		mImageViewPage.setImageURI(Uri.parse(listItemData.get(position).mstrImage));
		mTextViewTitle.setText(listItemData.get(position).mstrTitle);
		mTextViewDescript.setText(listItemData.get(position).mstrDescript);

		return viewHold;
	}

	public void addItemData(String strImage, int nChapter, int nPage, String strTitle, String strDescript)
	{
		listItemData.put(listItemData.size(), new ItemData(strImage, nChapter, nPage, strTitle, strDescript));
	}

	public void clear()
	{
		listItemData.clear();
	}

	public int getChapter(int nPosition)
	{
		return listItemData.get(nPosition).mnChapter;
	}

	public int getPage(int nPosition)
	{
		return listItemData.get(nPosition).mnPage;
	}
}

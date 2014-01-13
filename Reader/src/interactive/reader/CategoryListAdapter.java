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

public class CategoryListAdapter extends BaseAdapter
{

	private SparseArray<ItemData>	listItemData			= null;
	private LayoutInflater			mInflater				= null;
	private int						mnItemResourceId		= Type.INVALID;
	private int						mnImageResourceId		= Type.INVALID;
	private int						mnChapterNameResourceId	= Type.INVALID;
	private int						mnDescriptResourceId	= Type.INVALID;

	class ItemData
	{
		public String	mstrImage		= null;
		public String	mstrTitle		= null;
		public String	mstrDescript	= null;

		public ItemData(String strImage, String strTitle, String strDescript)
		{
			mstrImage = strImage;
			mstrTitle = strTitle;
			mstrDescript = strDescript;
		}
	}

	public CategoryListAdapter(Activity activity)
	{
		super();
		listItemData = new SparseArray<ItemData>();
		mInflater = LayoutInflater.from(activity);
		mnItemResourceId = Global.getResourceId(activity, "reader_category_list_view_item", "layout");
		mnImageResourceId = Global.getResourceId(activity, "imageViewCategoryItem", "id");
		mnChapterNameResourceId = Global.getResourceId(activity, "textViewCategoryTitle", "id");
		mnDescriptResourceId = Global.getResourceId(activity, "textViewCategoryDescript", "id");
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
		View viewHold = mInflater.inflate(mnItemResourceId, null);
		ImageView imgPage = (ImageView) viewHold.findViewById(mnImageResourceId);
		TextView txChapterName = (TextView) viewHold.findViewById(mnChapterNameResourceId);
		TextView txDescript = (TextView) viewHold.findViewById(mnDescriptResourceId);

		imgPage.setImageURI(Uri.parse(listItemData.get(position).mstrImage));
		txChapterName.setText(listItemData.get(position).mstrTitle);
		txDescript.setText(listItemData.get(position).mstrDescript);
		return viewHold;
	}

	public void addItemData(String strImage, String strTitle, String strDescript)
	{
		listItemData.put(listItemData.size(), new ItemData(strImage, strTitle, strDescript));
	}

	public void clear()
	{
		listItemData.clear();
	}

}

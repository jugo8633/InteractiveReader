package interactive.bookshelfuser;

import interactive.view.global.Global;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DrawerMenuAdapter extends BaseAdapter
{

	private Context	theContext	= null;

	public DrawerMenuAdapter(Context context)
	{
		super();
		theContext = context;
	}

	@Override
	public int getCount()
	{
		return 5;
	}

	@Override
	public Object getItem(int arg0)
	{
		return arg0;
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) theContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(Global.getResourceId(theContext, "drawer_menu_item", "layout"), null);

		RelativeLayout itemLayout = (RelativeLayout) view.findViewById(Global.getResourceId(theContext,
				"drawer_menu_item_layout", "id"));
		TextView itemText = (TextView) view.findViewById(Global
				.getResourceId(theContext, "drawer_menu_item_text", "id"));

		LinearLayout adLayout = (LinearLayout) view.findViewById(Global.getResourceId(theContext,
				"drawer_menu_item_ad_layout", "id"));
		switch (position)
		{
		case 0: //登入
			itemText.setText(theContext.getString(Global.getResourceId(theContext, "login", "string")));
			break;
		case 1: //設定
			itemText.setText(theContext.getString(Global.getResourceId(theContext, "config", "string")));
			break;
		case 2: //最新消息
			itemText.setText(theContext.getString(Global.getResourceId(theContext, "news", "string")));
			break;
		case 3: //訂閱
			itemText.setText(theContext.getString(Global.getResourceId(theContext, "subscript", "string")));
			break;
		case 4: //廣告
			itemLayout.setVisibility(View.GONE);
			adLayout.setVisibility(View.VISIBLE);
			break;
		default:
			View viewSpace = (View) view.findViewById(Global.getResourceId(theContext, "viewSpace", "id"));
			viewSpace.setVisibility(View.VISIBLE);
			break;
		}

		return view;
	}
}

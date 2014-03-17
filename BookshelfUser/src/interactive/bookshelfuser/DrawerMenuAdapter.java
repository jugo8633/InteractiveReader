package interactive.bookshelfuser;

import interactive.view.global.Global;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DrawerMenuAdapter extends BaseAdapter
{

	public static final String	TAG_LOGIN		= "login";
	public static final String	TAG_CONFIG		= "config";
	public static final String	TAG_NEWS		= "news";
	public static final String	TAG_SUBSCRIPT	= "subscript";
	private Context				theContext		= null;

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
		ImageView itemImage = (ImageView) view.findViewById(Global.getResourceId(theContext, "drawer_menu_item_image",
				"id"));

		LinearLayout adLayout = (LinearLayout) view.findViewById(Global.getResourceId(theContext,
				"drawer_menu_item_ad_layout", "id"));
		switch (position)
		{
//		case 0:
//			itemLayout.setVisibility(View.GONE); // for pull refresh temp area
//			break;
		case 0: //登入
			view.setTag(TAG_LOGIN);
			itemText.setText(theContext.getString(Global.getResourceId(theContext, "login", "string")));
			itemImage.setImageResource(Global.getResourceId(theContext, "signin_normal", "drawable"));
			break;
		case 1: //設定
			view.setTag("config");
			itemText.setText(theContext.getString(Global.getResourceId(theContext, "config", "string")));
			itemImage.setImageResource(Global.getResourceId(theContext, "setting_normal", "drawable"));
			break;
		case 2: //最新消息
			view.setTag("news");
			itemText.setText(theContext.getString(Global.getResourceId(theContext, "news", "string")));
			itemImage.setImageResource(Global.getResourceId(theContext, "notification_normal", "drawable"));
			break;
		case 3: //訂閱
			view.setTag("subscript");
			itemText.setText(theContext.getString(Global.getResourceId(theContext, "subscript", "string")));
			itemImage.setImageResource(Global.getResourceId(theContext, "subscribe_normal", "drawable"));
			break;
		case 4: //廣告
			itemLayout.setVisibility(View.GONE);
			adLayout.setVisibility(View.VISIBLE);
			break;
		default:
			itemLayout.setVisibility(View.GONE);
			break;
		}

		return view;
	}

	private OnClickListener	menuItemClick	= new OnClickListener()
											{

												@Override
												public void onClick(View view)
												{
													String strTag = (String) view.getTag();
													if (strTag.equals("login"))
													{

													}
													else if (strTag.equals("config"))
													{

													}
													else if (strTag.equals("news"))
													{

													}
													else if (strTag.equals("subscript"))
													{

													}
												}

											};
}

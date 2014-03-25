package interactive.widget;

import interactive.common.Device;
import interactive.common.Type;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TabButton extends RelativeLayout
{

	private final int							BUTTON_WIDTH	= 80;
	private LinearLayout						linearLayout	= null;
	private ImageView							imageIndicate	= null;
	private SparseArray<Items>					listItem		= null;
	private float								mfX				= 0;
	private SparseArray<OnItemSwitchedListener>	listItemSwitch	= null;
	private int									mnSelectedId	= Type.INVALID;

	public static interface OnItemSwitchedListener
	{
		public void onItemSwitched(int nIndex);
	}

	private class Items
	{
		public TextView	mTextView	= null;
		public int		mnId		= Type.INVALID;

		public Items(TextView textView, int nId)
		{
			mTextView = textView;
			mnId = nId;
		}
	}

	public TabButton(Context context)
	{
		super(context);
		init(context);
	}

	public TabButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public TabButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	private void init(Context context)
	{
		linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.addView(linearLayout);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ScaleSize(context, BUTTON_WIDTH),
				ScaleSize(context, 10));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		imageIndicate = new ImageView(context);
		imageIndicate.setLayoutParams(layoutParams);
		imageIndicate.setScaleType(ScaleType.CENTER_INSIDE);
		imageIndicate.setImageResource(Global.getResourceId(context, "triangle_indicate", "drawable"));
		this.addView(imageIndicate);
		imageIndicate.bringToFront();

		listItem = new SparseArray<Items>();
		listItemSwitch = new SparseArray<OnItemSwitchedListener>();
	}

	private int ScaleSize(Context context, int nSize)
	{
		Device device = new Device(context);
		float fScale = device.getScaleSize();
		device = null;

		int nResultSize = (int) Math.floor(nSize * fScale);
		return nResultSize;
	}

	public void setDisplay(float fX, float fY, int nWidth, int nHeight)
	{
		this.setX(fX);
		this.setY(fY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
	}

	public void addTextButton(String strText)
	{
		TextView textView = new TextView(getContext());
		textView.setId(Global.getUserId());
		textView.setText(strText);
		textView.setTextSize(16);
		textView.setTextColor(Color.GRAY);
		textView.setGravity(Gravity.CENTER);
		textView.setLayoutParams(new LayoutParams(Global.ScaleSize(BUTTON_WIDTH), LayoutParams.MATCH_PARENT));
		linearLayout.addView(textView);
		listItem.put(listItem.size(), new Items(textView, textView.getId()));

		textView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				setItemSelect((TextView) view);
			}
		});
	}

	public void setItemSelect(int nIndex)
	{
		for (int i = 0; i < listItem.size(); ++i)
		{
			if (nIndex == i)
			{
				mnSelectedId = listItem.get(i).mnId;
				imageIndicate.clearAnimation();
				mfX = listItem.get(i).mTextView.getX();
				imageIndicate.animate().translationX(mfX).setDuration(200)
						.setInterpolator(new AccelerateDecelerateInterpolator());
				listItem.get(i).mTextView.setTextColor(Color.BLUE);
				notifyItemSwitched(nIndex);
			}
			else
			{
				listItem.get(i).mTextView.setTextColor(Color.GRAY);
			}
		}
	}

	private void setItemSelect(TextView textView)
	{
		int nId = textView.getId();
		if (mnSelectedId == nId)
		{
			return;
		}
		mnSelectedId = nId;
		for (int i = 0; i < listItem.size(); ++i)
		{
			if (listItem.get(i).mnId == mnSelectedId)
			{
				setItemSelect(i);
				break;
			}
		}
	}

	public void setOnItemSwitchedListener(TabButton.OnItemSwitchedListener listener)
	{
		if (null != listener)
		{
			listItemSwitch.put(listItemSwitch.size(), listener);
		}
	}

	private void notifyItemSwitched(int nIndex)
	{
		for (int i = 0; i < listItemSwitch.size(); ++i)
		{
			listItemSwitch.get(i).onItemSwitched(nIndex);
		}
	}
}

package interactive.widget;

import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.animation.move.MoveHandler;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TabButton extends RelativeLayout
{

	private final int			BUTTON_WIDTH	= 80;
	private LinearLayout		linearLayout	= null;
	private ImageView			imageIndicate	= null;
	private SparseArray<Items>	listItem		= null;
	private float				mfX				= 0;

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

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(BUTTON_WIDTH, 10);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		imageIndicate = new ImageView(context);
		imageIndicate.setLayoutParams(layoutParams);
		imageIndicate.setScaleType(ScaleType.CENTER_INSIDE);
		imageIndicate.setImageResource(Global.getResourceId(context, "triangle_indicate", "drawable"));
		this.addView(imageIndicate);
		imageIndicate.bringToFront();

		listItem = new SparseArray<Items>();
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
		textView.setLayoutParams(new LayoutParams(BUTTON_WIDTH, LayoutParams.MATCH_PARENT));
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
				imageIndicate.clearAnimation();
				mfX = listItem.get(i).mTextView.getX();
				imageIndicate.animate().translationX(mfX).setDuration(200)
						.setInterpolator(new AccelerateDecelerateInterpolator());
				listItem.get(i).mTextView.setTextColor(Color.BLUE);
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
		for (int i = 0; i < listItem.size(); ++i)
		{
			if (listItem.get(i).mnId == nId)
			{
				setItemSelect(i);
				break;
			}
		}
	}
}

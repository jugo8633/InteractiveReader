package interactive.widget;

import interactive.bookshelfuser.DrawerMenuAdapter;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class PullToRefreshListView extends ListView implements OnScrollListener
{

	private final int				TAP_TO_REFRESH				= 1;
	private final int				PULL_TO_REFRESH				= 2;
	private final int				RELEASE_TO_REFRESH			= 3;
	private final int				REFRESHING					= 4;

	private RelativeLayout			mRefreshView				= null;
	private TextView				mRefreshViewText			= null;
	private ImageView				mRefreshViewImage			= null;
	private ProgressBar				mRefreshViewProgress		= null;
	private TextView				mRefreshViewLastUpdated		= null;

	private RotateAnimation			mFlipAnimation				= null;
	private RotateAnimation			mReverseFlipAnimation		= null;

	private int						mnRefreshOriginalTopPadding	= 0;
	private int						mnRefreshState				= Type.INVALID;
	private int						mnRefreshViewHeight			= 0;
	private int						mnLastMotionY				= 0;
	private int						mnCurrentScrollState		= Type.INVALID;
	private boolean					mbBounceHack				= false;

	private OnScrollListener		mOnScrollListener			= null;
	private OnRefreshListener		mOnRefreshListener			= null;

	private View					mSelectedView				= null;
	private OnItemSelectedListener	mOnItemSelectedListener		= null;

	public static interface OnItemSelectedListener
	{
		public void onItemSelected(int nIndex);
	}

	public interface OnRefreshListener
	{
		/**
		 * Called when the list should be refreshed.
		 * <p>
		 * A call to {@link PullToRefreshListView #onRefreshComplete()} is
		 * expected to indicate that the refresh has completed.
		 */
		public void onRefresh();
	}

	public PullToRefreshListView(Context context)
	{
		super(context);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		/** init animation */
		mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);

		mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		/** init layout */
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mRefreshView = (RelativeLayout) inflater.inflate(
				Global.getResourceId(context, "pull_to_refresh_header", "layout"), this, false);
		mRefreshViewText = (TextView) mRefreshView.findViewById(Global.getResourceId(context, "pull_to_refresh_text",
				"id"));
		mRefreshViewImage = (ImageView) mRefreshView.findViewById(Global.getResourceId(context,
				"pull_to_refresh_image", "id"));
		mRefreshViewProgress = (ProgressBar) mRefreshView.findViewById(Global.getResourceId(context,
				"pull_to_refresh_progress", "id"));
		mRefreshViewLastUpdated = (TextView) mRefreshView.findViewById(Global.getResourceId(context,
				"pull_to_refresh_updated_at", "id"));

		mRefreshViewImage.setMinimumHeight(50);
		mnRefreshOriginalTopPadding = mRefreshView.getPaddingTop();

		mnRefreshState = TAP_TO_REFRESH;

		addHeaderView(mRefreshView);

		super.setOnScrollListener(this);

		measureView(mRefreshView);

		mnRefreshViewHeight = mRefreshView.getMeasuredHeight();
		setVerticalScrollBarEnabled(false);

		this.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3)
			{
				String strTag = (String) view.getTag();
				if (null == strTag)
				{
					return;
				}

				if (null != mSelectedView)
				{
					RelativeLayout itemLayout = (RelativeLayout) mSelectedView.findViewById(Global.getResourceId(
							getContext(), "drawer_menu_item_layout", "id"));
					TextView itemText = (TextView) mSelectedView.findViewById(Global.getResourceId(getContext(),
							"drawer_menu_item_text", "id"));
					ImageView itemImage = (ImageView) mSelectedView.findViewById(Global.getResourceId(getContext(),
							"drawer_menu_item_image", "id"));

					itemLayout.setBackgroundResource(Global.getResourceId(getContext(), "drawer_menu_background",
							"color"));
					itemText.setTextColor(Color.parseColor("#494949"));

					String strSelectedTag = (String) mSelectedView.getTag();
					if (strSelectedTag.equals(DrawerMenuAdapter.TAG_LOGIN))
					{
						itemImage.setImageResource(Global.getResourceId(getContext(), "signin_normal", "drawable"));
					}
					if (strSelectedTag.equals(DrawerMenuAdapter.TAG_CONFIG))
					{
						itemImage.setImageResource(Global.getResourceId(getContext(), "setting_normal", "drawable"));
					}
					if (strSelectedTag.equals(DrawerMenuAdapter.TAG_NEWS))
					{
						itemImage.setImageResource(Global
								.getResourceId(getContext(), "notification_normal", "drawable"));
					}
					if (strSelectedTag.equals(DrawerMenuAdapter.TAG_SUBSCRIPT))
					{
						itemImage.setImageResource(Global.getResourceId(getContext(), "subscribe_normal", "drawable"));
					}
				}

				RelativeLayout itemLayout = (RelativeLayout) view.findViewById(Global.getResourceId(getContext(),
						"drawer_menu_item_layout", "id"));
				TextView itemText = (TextView) view.findViewById(Global.getResourceId(getContext(),
						"drawer_menu_item_text", "id"));
				ImageView itemImage = (ImageView) view.findViewById(Global.getResourceId(getContext(),
						"drawer_menu_item_image", "id"));

				itemLayout.setBackgroundResource(Global.getResourceId(getContext(), "drawer_menu_background_click",
						"color"));
				itemText.setTextColor(Color.parseColor("#efefef"));

				if (strTag.equals(DrawerMenuAdapter.TAG_LOGIN))
				{
					itemImage.setImageResource(Global.getResourceId(getContext(), "signin_click", "drawable"));
				}
				if (strTag.equals(DrawerMenuAdapter.TAG_CONFIG))
				{
					itemImage.setImageResource(Global.getResourceId(getContext(), "setting_click", "drawable"));
				}
				if (strTag.equals(DrawerMenuAdapter.TAG_NEWS))
				{
					itemImage.setImageResource(Global.getResourceId(getContext(), "notification_click", "drawable"));
				}
				if (strTag.equals(DrawerMenuAdapter.TAG_SUBSCRIPT))
				{
					itemImage.setImageResource(Global.getResourceId(getContext(), "subscribe_click", "drawable"));
				}
				if (null != strTag)
				{
					mSelectedView = view;
				}
				Logs.showTrace("item click index=" + arg2);
				mOnItemSelectedListener.onItemSelected(arg2);
			}
		});

		setFastScrollEnabled(false);

	}

	private void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null)
		{
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		}
		else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		setSelection(1);
		this.clearChoices();
	}

	@Override
	public void setAdapter(ListAdapter adapter)
	{
		super.setAdapter(adapter);

		setSelection(1);
		this.clearChoices();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l)
	{
		mOnScrollListener = l;
	}

	public void setOnRefreshListener(OnRefreshListener onRefreshListener)
	{
		mOnRefreshListener = onRefreshListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		final int y = (int) event.getY();
		mbBounceHack = false;

		switch (event.getAction())
		{
		case MotionEvent.ACTION_UP:
			if (getFirstVisiblePosition() == 0 && mnRefreshState != REFRESHING)
			{
				if ((mRefreshView.getBottom() >= mnRefreshViewHeight || mRefreshView.getTop() >= 0)
						&& mnRefreshState == RELEASE_TO_REFRESH)
				{
					// Initiate the refresh
					mnRefreshState = REFRESHING;
					prepareForRefresh();
					onRefresh();
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mnLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			applyHeaderPadding(event);
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		// When the refresh view is completely visible, change the text to say
		// "Release to refresh..." and flip the arrow drawable.
		if (mnCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL && mnRefreshState != REFRESHING)
		{
			if (firstVisibleItem == 0)
			{
				mRefreshViewImage.setVisibility(View.VISIBLE);
				if ((mRefreshView.getBottom() >= mnRefreshViewHeight + 20 || mRefreshView.getTop() >= 0)
						&& mnRefreshState != RELEASE_TO_REFRESH)
				{
					mRefreshViewText.setText(Global.getResourceId(getContext(), "pull_to_refresh_release_label",
							"string"));
					mRefreshViewImage.clearAnimation();
					mRefreshViewImage.startAnimation(mFlipAnimation);
					mnRefreshState = RELEASE_TO_REFRESH;
				}
				else if (mRefreshView.getBottom() < mnRefreshViewHeight + 20 && mnRefreshState != PULL_TO_REFRESH)
				{
					mRefreshViewText
							.setText(Global.getResourceId(getContext(), "pull_to_refresh_pull_label", "string"));
					if (mnRefreshState != TAP_TO_REFRESH)
					{
						mRefreshViewImage.clearAnimation();
						mRefreshViewImage.startAnimation(mReverseFlipAnimation);
					}
					mnRefreshState = PULL_TO_REFRESH;
				}
			}
			else
			{
				mRefreshViewImage.setVisibility(View.GONE);
				resetHeader();
			}
		}
		else if (mnCurrentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0 && mnRefreshState != REFRESHING)
		{
			setSelection(1);
			this.clearChoices();
			mbBounceHack = true;
		}
		else if (mbBounceHack && mnCurrentScrollState == SCROLL_STATE_FLING)
		{
			setSelection(1);
			this.clearChoices();
		}

		if (mOnScrollListener != null)
		{
			mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		mnCurrentScrollState = scrollState;

		if (mnCurrentScrollState == SCROLL_STATE_IDLE)
		{
			mbBounceHack = false;
			if (mnRefreshState != REFRESHING)
			{
				setSelection(1);
				clearChoices();
			}
		}

		if (mOnScrollListener != null)
		{
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}

	}

	private void resetHeader()
	{
		if (mnRefreshState != TAP_TO_REFRESH)
		{
			mnRefreshState = TAP_TO_REFRESH;

			resetHeaderPadding();

			// Set refresh view text to the pull label
			mRefreshViewText.setText(Global.getResourceId(getContext(), "pull_to_refresh_tap_label", "string"));
			// Replace refresh drawable with arrow drawable
			mRefreshViewImage
					.setImageResource(Global.getResourceId(getContext(), "ic_pulltorefresh_arrow", "drawable"));
			// Clear the full rotation animation
			mRefreshViewImage.clearAnimation();
			// Hide progress bar and arrow.
			mRefreshViewImage.setVisibility(View.GONE);
			mRefreshViewProgress.setVisibility(View.GONE);
		}
	}

	private void applyHeaderPadding(MotionEvent ev)
	{
		// getHistorySize has been available since API 1
		int pointerCount = ev.getHistorySize();

		for (int p = 0; p < pointerCount; ++p)
		{
			if (mnRefreshState == RELEASE_TO_REFRESH)
			{
				int historicalY = (int) ev.getHistoricalY(p);

				// Calculate the padding to apply, we divide by 1.7 to
				// simulate a more resistant effect during pull.
				int topPadding = (int) (((historicalY - mnLastMotionY) - mnRefreshViewHeight) / 1.7);

				mRefreshView.setPadding(mRefreshView.getPaddingLeft(), topPadding, mRefreshView.getPaddingRight(),
						mRefreshView.getPaddingBottom());
			}
		}
	}

	public void prepareForRefresh()
	{
		resetHeaderPadding();

		mRefreshViewImage.setVisibility(View.GONE);
		// We need this hack, otherwise it will keep the previous drawable.
		mRefreshViewImage.setImageDrawable(null);
		mRefreshViewProgress.setVisibility(View.VISIBLE);

		// Set refresh view text to the refreshing label
		mRefreshViewText.setText(Global.getResourceId(getContext(), "pull_to_refresh_refreshing_label", "string"));

		mnRefreshState = REFRESHING;
	}

	public void onRefresh()
	{
		if (mOnRefreshListener != null)
		{
			mOnRefreshListener.onRefresh();
		}
	}

	private void resetHeaderPadding()
	{
		mRefreshView.setPadding(mRefreshView.getPaddingLeft(), mnRefreshOriginalTopPadding,
				mRefreshView.getPaddingRight(), mRefreshView.getPaddingBottom());
	}

	public void onRefreshComplete(CharSequence lastUpdated)
	{
		setLastUpdated(lastUpdated);
		onRefreshComplete();
	}

	public void setLastUpdated(CharSequence lastUpdated)
	{
		if (lastUpdated != null)
		{
			mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
			mRefreshViewLastUpdated.setText(lastUpdated);
		}
		else
		{
			mRefreshViewLastUpdated.setVisibility(View.GONE);
		}
	}

	public void onRefreshComplete()
	{
		resetHeader();

		// If refresh view is visible when loading completes, scroll down to
		// the next item.
		if (getFirstVisiblePosition() == 0)
		{
			//		invalidateViews();
			setSelection(1);
			this.clearChoices();
		}
	}

	public void setOnItemSelectedListener(PullToRefreshListView.OnItemSelectedListener listener)
	{
		if (null != listener)
		{
			mOnItemSelectedListener = listener;
		}
	}

	public void clearSelected()
	{
		invalidateViews();
	}
}

package interactive.reader;

import android.app.Activity;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import interactive.common.ObjectFactory;
import interactive.common.Type;
import interactive.view.global.Global;

public class FootbarHandler
{
	private ObjectFactory						objectFactory		= null;
	private Footbar								footbar				= null;
	private SparseArray<OnItemSelectedListener>	listOnItemSelected	= null;

	public interface OnItemSelectedListener
	{
		void OnItemSelected(int nIndexSelected);
	}

	public class Footbar
	{
		public int	mnBookCityId	= Type.INVALID;
		public int	mnBookshelfId	= Type.INVALID;
		public int	mnReaderId		= Type.INVALID;
	}

	public FootbarHandler(Activity activity)
	{
		super();
		init(activity);
	}

	private void init(Activity activity)
	{
		objectFactory = new ObjectFactory(activity);
		objectFactory.setSingleSelect(true);
		footbar = new Footbar();
		footbar.mnBookCityId = Global.getResourceId(activity, "bookCityBtn", "id");
		footbar.mnBookshelfId = Global.getResourceId(activity, "bookshelfBtn", "id");
		footbar.mnReaderId = Global.getResourceId(activity, "readerBtn", "id");

		setOptionTouch(
				activity,
				objectFactory.addImageButton(footbar.mnBookCityId,
						Global.getResourceId(activity, "cart_normal", "drawable"),
						Global.getResourceId(activity, "cart_rollover", "drawable"),
						Global.getResourceId(activity, "cart_click", "drawable")));

		setOptionTouch(
				activity,
				objectFactory.addImageButton(footbar.mnBookshelfId,
						Global.getResourceId(activity, "books_normal", "drawable"),
						Global.getResourceId(activity, "books_rollover", "drawable"),
						Global.getResourceId(activity, "books_click", "drawable")));

		setOptionTouch(
				activity,
				objectFactory.addImageButton(footbar.mnReaderId,
						Global.getResourceId(activity, "reader_normal", "drawable"),
						Global.getResourceId(activity, "reader_rollover", "drawable"),
						Global.getResourceId(activity, "reader_click", "drawable")));

		listOnItemSelected = new SparseArray<OnItemSelectedListener>();

	}

	private void setOptionTouch(Activity activity, int nResId)
	{
		ImageView imageView = (ImageView) activity.findViewById(nResId);
		if (null != imageView)
		{
			imageView.setOnTouchListener(optionTouchListener);
		}
	}

	public void setDefaultSelected(int nResId)
	{
		objectFactory.setImgBtnTouchUp(nResId);
	}

	public void setOnItemSelectedListener(FootbarHandler.OnItemSelectedListener listener)
	{
		if (null != listener)
		{
			listOnItemSelected.put(listOnItemSelected.size(), listener);
		}
	}

	private void notifyItemSelected(int nResId)
	{
		int nIndex = Type.INVALID;
		if (nResId == footbar.mnBookCityId)
		{
			nIndex = 0;
		}
		if (nResId == footbar.mnBookshelfId)
		{
			nIndex = 1;
		}
		if (nResId == footbar.mnReaderId)
		{
			nIndex = 2;
		}

		for (int i = 0; i < listOnItemSelected.size(); ++i)
		{
			listOnItemSelected.get(i).OnItemSelected(nIndex);
		}
	}

	private OnTouchListener	optionTouchListener	= new OnTouchListener()
												{

													@Override
													public boolean onTouch(View v, MotionEvent event)
													{
														int nResId = v.getId();

														switch (event.getAction())
														{
														case MotionEvent.ACTION_DOWN:
															objectFactory.setImgBtnTouchDown(nResId);
															break;
														case MotionEvent.ACTION_UP:
															objectFactory.setImgBtnTouchUp(nResId);
															notifyItemSelected(nResId);
															break;
														}
														return true;
													}
												};

}

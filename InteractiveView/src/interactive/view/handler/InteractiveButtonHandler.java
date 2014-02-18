package interactive.view.handler;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class InteractiveButtonHandler
{
	private Runnable							runRemoveImage	= null;
	private SparseArray<InteractiveButtonData>	listButton		= null;
	private String								mstrButtonTag	= null;
	private String								mstrImageTag	= null;

	class Tags
	{
		public String	mstrButtonTag	= null;
		public String	mstrImageTag	= null;

		public Tags(String strButtonTag, String strImageTag)
		{
			mstrButtonTag = strButtonTag;
			mstrImageTag = strImageTag;
		}
	}

	public InteractiveButtonHandler()
	{
		super();
		listButton = new SparseArray<InteractiveButtonData>();

		runRemoveImage = new Runnable()
		{
			@Override
			public void run()
			{
				removeImage(mstrButtonTag, mstrImageTag);
			}
		};
	}

	@Override
	protected void finalize() throws Throwable
	{
		listButton.clear();
		listButton = null;
		super.finalize();
	}

	public void addButton(Context context, String strTag, String strGroupId, Handler handler)
	{
		listButton.put(listButton.size(), new InteractiveButtonData(context, strTag, strGroupId, handler));
	}

	public void addImageData(String strButtonTag, String strImageTag, int nWidth, int nHeight, int nX, int nY,
			String strSrc, String strGroupId, boolean bIsVisible)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null != buttonData)
		{
			buttonData.addImageData(strImageTag, nWidth, nHeight, nX, nY, strSrc, strGroupId, bIsVisible);
		}
	}

	public void addEventData(String strButtonTag, int nType, String strTypeName, int nEvent, String strEventName,
			int nTargetType, String strTargetID, int nDisplay)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null != buttonData)
		{
			buttonData.addEventData(nType, strTypeName, nEvent, strEventName, nTargetType, strTargetID, nDisplay);
		}
	}

	public void setContainer(String strButtonTag, ViewGroup viewGroup)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null != buttonData)
		{
			buttonData.setContainer(viewGroup);
		}
	}

	private InteractiveButtonData getButtonData(String strTag)
	{
		for (int i = 0; i < listButton.size(); ++i)
		{
			if (listButton.get(i).mstrTag.equals(strTag))
			{
				return listButton.get(i);
			}
		}
		return null;
	}

	public void handleButtonEvent(String strButtonTag)
	{
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null == buttonData)
		{
			Logs.showTrace("Button Data invalid!! tag=" + strButtonTag);
			return;
		}

		String strGroupId = getGroupId(buttonData);
		Logs.showTrace("Handle button event tag=" + strButtonTag + " group id=" + strGroupId);
		resetButton(strButtonTag, strGroupId);

		for (int i = 0; i < buttonData.listEventData.size(); ++i)
		{
			switch (buttonData.listEventData.get(i).mnEvent)
			{
			case InteractiveDefine.BUTTON_EVENT_DRAG:
				break;
			case InteractiveDefine.BUTTON_EVENT_SHOW_ITEM:
				showItem(buttonData, buttonData.listEventData.get(i).mnTargetType,
						buttonData.listEventData.get(i).mstrTargetID);
				break;
			case InteractiveDefine.BUTTON_EVENT_VIDEO_PAUSE:
				break;
			case InteractiveDefine.BUTTON_EVENT_VIDEO_PLAY:
				break;
			}
		}

	}

	private String getGroupId(InteractiveButtonData buttonData)
	{
		if (null != buttonData)
		{
			return buttonData.mstrGroupId;
		}
		return null;
	}

	private void resetButton(String strButtonTag, String strGroupId)
	{
		for (int i = 0; i < listButton.size(); ++i)
		{
			if (listButton.get(i).mstrGroupId.equals(strGroupId) && !listButton.get(i).mstrTag.equals(strButtonTag))
			{
				EventHandler.notify(listButton.get(i).mHandlerButton, EventMessage.MSG_RESET, 0, 0, null);
				if (null != listButton.get(i).listImageData)
				{
					for (int j = 0; j < listButton.get(i).listImageData.size(); ++j)
					{
						removeImage(listButton.get(i).mstrTag, listButton.get(i).listImageData.get(j).mstrName);
					}
				}
			}
		}
	}

	private void resetButton(String strButtonTag)
	{
		for (int i = 0; i < listButton.size(); ++i)
		{
			if (listButton.get(i).mstrTag.equals(strButtonTag))
			{
				EventHandler.notify(listButton.get(i).mHandlerButton, EventMessage.MSG_RESET, 0, 0, null);
			}
		}
	}

	public void initButton(String strButtonTag)
	{
		if (null == strButtonTag)
		{
			return;
		}

		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null == buttonData)
		{
			Logs.showTrace("Button Data invalid!! tag=" + strButtonTag);
			return;
		}

		if (null != buttonData.listImageData)
		{
			for (int i = 0; i < buttonData.listImageData.size(); ++i)
			{
				if (null != buttonData.listImageData.get(i).mImageView)
				{
					buttonData.getContainer().removeView(buttonData.listImageData.get(i).mImageView);
					if (null != buttonData.listImageData.get(i).mBitmapSrc)
					{
						if (!buttonData.listImageData.get(i).mBitmapSrc.isRecycled())
						{
							buttonData.listImageData.get(i).mBitmapSrc.recycle();
						}
						buttonData.listImageData.get(i).mBitmapSrc = null;
					}
					buttonData.listImageData.get(i).mImageView = null;
				}
			}
		}
	}

	private void showItem(InteractiveButtonData buttonData, int nTargetType, String strItemTag)
	{
		switch (nTargetType)
		{
		case InteractiveDefine.OBJECT_CATEGORY_IMAGE:
			showImage(buttonData, strItemTag);
			break;
		case InteractiveDefine.OBJECT_CATEGORY_MAP:
			break;
		case InteractiveDefine.OBJECT_CATEGORY_VIDEO:
			break;
		}
	}

	private void showImage(final InteractiveButtonData buttonData, String strImageTag)
	{
		if (null == buttonData.listImageData)
		{
			return;
		}
		Logs.showTrace("Button event show image tag=" + strImageTag);
		for (int i = 0; i < buttonData.listImageData.size(); ++i)
		{
			if (buttonData.listImageData.get(i).mstrName.equals(strImageTag))
			{
				if (null != buttonData.listImageData.get(i).mImageView)
				{
					continue;
				}
				buttonData.listImageData.get(i).mBitmapSrc = BitmapHandler.readBitmap(buttonData.getContext(),
						buttonData.listImageData.get(i).mstrSrc, buttonData.listImageData.get(i).mnWidth,
						buttonData.listImageData.get(i).mnHeight);
				buttonData.listImageData.get(i).mImageView = new ImageView(buttonData.getContext());
				buttonData.listImageData.get(i).mImageView.setTag(buttonData.listImageData.get(i).mstrName);
				buttonData.listImageData.get(i).mImageView.setScaleType(ScaleType.CENTER_CROP);
				buttonData.listImageData.get(i).mImageView.setImageBitmap(buttonData.listImageData.get(i).mBitmapSrc);
				buttonData.listImageData.get(i).mImageView.setX(buttonData.listImageData.get(i).mnX);
				buttonData.listImageData.get(i).mImageView.setY(buttonData.listImageData.get(i).mnY);
				buttonData.getContainer().addView(buttonData.listImageData.get(i).mImageView);
				buttonData.listImageData.get(i).mImageView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						EventHandler.notify(selfHandler, EventMessage.MSG_IMAGE_CLICK, 0, 0, new Tags(
								buttonData.mstrTag, (String) view.getTag()));
						resetButton(buttonData.mstrTag);
						resetButton(buttonData.mstrTag, buttonData.mstrGroupId);
					}
				});
			}
		}
	}

	synchronized private void removeImage(String strButtonTag, String strImageTag)
	{
		Logs.showTrace("Remove button image , button tag=" + strButtonTag + " image tag=" + strImageTag);
		InteractiveButtonData buttonData = getButtonData(strButtonTag);
		if (null == buttonData)
		{
			Logs.showTrace("Remove button image fail , button data invalid");
			return;
		}

		for (int i = 0; i < buttonData.listImageData.size(); ++i)
		{
			if (buttonData.listImageData.get(i).mstrName.equals(strImageTag))
			{
				buttonData.getContainer().removeView(buttonData.listImageData.get(i).mImageView);
				if (null != buttonData.listImageData.get(i).mBitmapSrc)
				{
					if (!buttonData.listImageData.get(i).mBitmapSrc.isRecycled())
					{
						buttonData.listImageData.get(i).mBitmapSrc.recycle();
					}
					buttonData.listImageData.get(i).mBitmapSrc = null;
				}
				buttonData.listImageData.get(i).mImageView = null;
			}
		}
	}

	private Handler	selfHandler	= new Handler()
								{

									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_IMAGE_CLICK:
											Tags tags = (Tags) msg.obj;
											mstrButtonTag = tags.mstrButtonTag;
											mstrImageTag = tags.mstrImageTag;
											postDelayed(runRemoveImage, 100);
											break;
										}
									}
								};
}

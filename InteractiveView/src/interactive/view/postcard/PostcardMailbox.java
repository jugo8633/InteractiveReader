package interactive.view.postcard;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveDefine;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class PostcardMailbox extends RelativeLayout
{

	private ImageView	imageView	= null;
	private int			mnWidth		= 0;
	private int			mnHeight	= 0;

	public PostcardMailbox(Context context)
	{
		super(context);
		init(context);
	}

	public PostcardMailbox(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public PostcardMailbox(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		imageView = new ImageView(context);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageView.setScaleType(ScaleType.CENTER_CROP);
		this.addView(imageView);

		imageView.setOnDragListener(new PostcardDragListener());
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
		mnWidth = nWidth;
		mnHeight = nHeight;
	}

	public void setImage(String strPath)
	{
		Bitmap bitmap = BitmapHandler.readBitmap(strPath, mnWidth, mnHeight);
		imageView.setImageBitmap(bitmap);
	}

	class PostcardDragListener implements OnDragListener
	{
		@Override
		public boolean onDrag(View v, DragEvent event)
		{
			switch (event.getAction())
			{
			case DragEvent.ACTION_DRAG_STARTED:
				EventHandler.notify(Global.interactiveHandler.getNotifyHandler(), EventMessage.MSG_DRAG_START,
						InteractiveDefine.OBJECT_CATEGORY_POSTCARD, 0, null);
				Logs.showTrace("ACTION_DRAG_STARTED");
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				Logs.showTrace("ACTION_DRAG_ENTERED");
				imageView.setBackgroundColor(Color.YELLOW);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				Logs.showTrace("ACTION_DRAG_EXITED");
				imageView.setBackgroundColor(Color.TRANSPARENT);
				break;
			case DragEvent.ACTION_DROP:
				Logs.showTrace("ACTION_DROP");
				if (v == imageView)
				{
					EventHandler.notify(Global.interactiveHandler.getNotifyHandler(), EventMessage.MSG_SEND_POSTCARD,
							0, 0, null);
				}
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				Logs.showTrace("ACTION_DRAG_ENDED");
				imageView.setBackgroundColor(Color.TRANSPARENT);
				EventHandler.notify(Global.interactiveHandler.getNotifyHandler(), EventMessage.MSG_DRAG_END,
						InteractiveDefine.OBJECT_CATEGORY_POSTCARD, 0, null);
			default:
				Logs.showTrace("DragEvent=" + event.getAction());
				break;
			}
			return true;
		}
	};
}

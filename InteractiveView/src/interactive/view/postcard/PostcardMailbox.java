package interactive.view.postcard;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.view.global.Global;
import interactive.view.handler.InteractiveEvent;
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
			//signal for the start of a drag and drop operation.
			case DragEvent.ACTION_DRAG_STARTED:
				// do nothing
				break;

			//the drag point has entered the bounding box of the View
			case DragEvent.ACTION_DRAG_ENTERED:
				imageView.setBackgroundColor(Color.YELLOW);
				break;

			//the user has moved the drag shadow outside the bounding box of the View
			case DragEvent.ACTION_DRAG_EXITED:
				imageView.setBackgroundColor(Color.TRANSPARENT);
				break;

			//drag shadow has been released,the drag point is within the bounding box of the View
			case DragEvent.ACTION_DROP:
				// if the view is the bottomlinear, we accept the drag item
				if (v == imageView)
				{
					EventHandler.notify(Global.interactiveHandler.getNotifyHandler(), EventMessage.MSG_SEND_POSTCARD,
							0, 0, null);
				}

				break;

			//the drag and drop operation has concluded.
			case DragEvent.ACTION_DRAG_ENDED:
				imageView.setBackgroundColor(Color.TRANSPARENT);
				EventHandler.notify(Global.interactiveHandler.getNotifyHandler(), EventMessage.MSG_DRAG_END,
						InteractiveEvent.OBJECT_CATEGORY_POSTCARD, 0, null);
			default:
				break;
			}
			return true;
		}
	};
}

package interactive.view.postcard;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Postcard
{

	private FrameLayout	postcardFrame	= null;
	private ViewGroup	container		= null;
	private Context		theContext		= null;

	public Postcard(Context context, ViewGroup viewGroup)
	{
		super();
		theContext = context;
		container = viewGroup;
		postcardFrame = new FrameLayout(context);
	}

	public void initPostcardFrame(String strName, int nX, int nY, int nWidth, int nHeight)
	{
		postcardFrame.setTag(strName);
		postcardFrame.setX(nX);
		postcardFrame.setY(nY);
		postcardFrame.setLayoutParams(new LayoutParams(nWidth, nHeight));
		container.removeView(postcardFrame);
		container.addView(postcardFrame);
	}

	public void addPostcard(String strPath)
	{
		ImageView image = new ImageView(theContext);
		image.setImageURI(Uri.parse(strPath));
		image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		postcardFrame.addView(image);
	}
}

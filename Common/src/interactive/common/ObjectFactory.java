package interactive.common;

import android.app.Activity;
import android.util.SparseArray;
import android.widget.ImageView;

public class ObjectFactory
{
	public class ImageButton
	{
		public int	mnId				= Type.INVALID;
		public int	mnNormalSrcId		= Type.INVALID;
		public int	mnTouchDownSrcId	= Type.INVALID;
		public int	mnTouchUpSrcId		= Type.INVALID;

		public ImageButton(int nId, int nNormalId, int nTouchDownId, int nTouchUpId)
		{
			mnId = nId;
			mnNormalSrcId = nNormalId;
			mnTouchDownSrcId = nTouchDownId;
			mnTouchUpSrcId = nTouchUpId;
		}
	}

	private SparseArray<ImageButton>	listImgBtn	= null;
	private Activity					theActivity	= null;

	public ObjectFactory(Activity activity)
	{
		theActivity = activity;
		listImgBtn = new SparseArray<ImageButton>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		if (null != listImgBtn)
		{
			listImgBtn.clear();
			listImgBtn = null;
		}
		super.finalize();
	}

	public ImageView getImageButton(int nId)
	{
		return (ImageView) theActivity.findViewById(nId);
	}

	public int addImageButton(int nId, int nNormalId, int nTouchDownId, int nTouchUpId)
	{
		ImageButton imgbtn = new ImageButton(nId, nNormalId, nTouchDownId, nTouchUpId);
		listImgBtn.put(nId, imgbtn);
		return nId;
	}

	public void setImgBtnTouchDown(int nId)
	{
		ImageButton imgbtn = listImgBtn.get(nId);
		if (null != imgbtn)
		{
			ImageView imgview = getImageButton(nId);
			imgview.setImageResource(imgbtn.mnTouchDownSrcId);
		}
	}

	public void setImgBtnTouchUp(int nId)
	{
		ImageButton imgbtn = listImgBtn.get(nId);
		if (null != imgbtn)
		{
			ImageView imgview = getImageButton(nId);
			imgview.setImageResource(imgbtn.mnTouchUpSrcId);
		}
	}

	public void setImgBtnNormal(int nId)
	{
		ImageButton imgbtn = listImgBtn.get(nId);
		if (null != imgbtn)
		{
			ImageView imgview = getImageButton(nId);
			imgview.setImageResource(imgbtn.mnNormalSrcId);
		}
	}

}

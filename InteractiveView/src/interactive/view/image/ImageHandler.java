package interactive.view.image;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/** 
 * 圖片處理
 */
public class ImageHandler
{

	/** 
	 * 圖片旋轉
	 *  
	 * @param bmp 
	 *            
	 * @param degree 圖片旋轉的角度，負值為逆時針旋轉，正值為順時針旋轉 
	 *          
	 * @return 
	 */
	public static Bitmap rotateBitmap(Bitmap bmp, float degree)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	/** 
	 * 圖片縮放 
	 *  
	 * @param bm 
	 * @param scale 
	 *            值小於則為縮小，否則為放大 
	 * @return 
	 */
	public static Bitmap resizeBitmap(Bitmap bm, float scale)
	{
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}

	/** 
	 * 圖片縮放 
	 *  
	 * @param bm 
	 * @param w 
	 *            縮小或放大成的寬 
	 * @param h 
	 *            縮小或放大成的高 
	 * @return 
	 */
	public static Bitmap resizeBitmap(Bitmap bm, int w, int h)
	{
		Bitmap BitmapOrg = bm;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
	}

	/** 
	 * 圖片反轉 
	 *  
	 * @param bm 
	 * @param flag 
	 *            0為水平反轉，1為垂直反轉 
	 * @return 
	 */
	public static Bitmap reverseBitmap(Bitmap bmp, int flag)
	{
		float[] floats = null;
		switch (flag)
		{
		case 0: // 水平反转  
			floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
			break;
		case 1: // 垂直反转  
			floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
			break;
		}

		if (floats != null)
		{
			Matrix matrix = new Matrix();
			matrix.setValues(floats);
			return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		}

		return null;
	}

}
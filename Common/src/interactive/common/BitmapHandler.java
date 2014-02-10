package interactive.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.opengl.GLES10;
import android.view.View;

public class BitmapHandler
{

	private static final int	msMaxTexture	= getMaxTexDim();

	public BitmapHandler()
	{
		super();
	}

	private static int getMaxTexDim()
	{
		int[] maxTextureSize = new int[1];
		GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
		return maxTextureSize[0];
	}

	/**
	  * 以最省內存的方式讀取本地資源的圖片
	  * @param context
	  * @param resId
	  * @return
	  */
	public static Bitmap readBitmap(Context context, int resId)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		//獲取資源圖片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static Bitmap readBitmap(String strFilePath, int reqWidth, int reqHeight)
	{
		if (null == strFilePath)
		{
			return null;
		}
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(new File(strFilePath));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFile(strFilePath, options);
		options.inJustDecodeBounds = false;
		float fWidth = reqWidth;
		float fHeight = reqHeight;
		float fScale = 1.0f;
		if (reqWidth > reqHeight && msMaxTexture < reqWidth)
		{
			fWidth = msMaxTexture;
			fScale = reqWidth / msMaxTexture;
			fHeight = reqHeight / fScale;
		}

		if (reqWidth < reqHeight && msMaxTexture < reqHeight)
		{
			fHeight = msMaxTexture;
			fScale = reqHeight / msMaxTexture;
			fWidth = reqWidth / fScale;
		}

		if (reqWidth == reqHeight && msMaxTexture < reqWidth)
		{
			fWidth = msMaxTexture;
			fHeight = msMaxTexture;
		}

		int nWidth = (int) Math.floor(fWidth);
		int nHeight = (int) Math.floor(fHeight);
		options.inSampleSize = calculateInSampleSize(options, nWidth, nHeight);
		Bitmap originalBitmap = BitmapFactory.decodeStream(fis, null, options);
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, nWidth, nHeight, false);
		try
		{
			fis.close();
			fis = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return resizedBitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth)
		{
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/** 
	 * 合併兩張bitmap為一張 
	 * @param background 
	 * @param foreground 
	 * @return Bitmap 
	 */
	public static Bitmap combineBitmap(Bitmap background, Bitmap foreground, float left, float top)
	{
		if (background == null)
		{
			return null;
		}
		Bitmap newmap = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(newmap);
		canvas.drawBitmap(background, 0, 0, null);
		canvas.drawBitmap(foreground, left, top, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newmap;
	}

	public static Bitmap getScreenshotsForCurrentWindow(Activity activity)
	{
		View cv = activity.getWindow().getDecorView();
		Bitmap bmp = Bitmap.createBitmap(cv.getWidth(), cv.getHeight(), Bitmap.Config.ARGB_4444);
		cv.draw(new Canvas(bmp));
		return bmp;
	}

	public static Bitmap cutBitmap(Bitmap mBitmap, Rect r, Bitmap.Config config)
	{
		int width = r.width();
		int height = r.height();

		Bitmap croppedImage = Bitmap.createBitmap(width, height, config);

		Canvas cvs = new Canvas(croppedImage);
		Rect dr = new Rect(0, 0, width, height);
		cvs.drawBitmap(mBitmap, r, dr, null);
		return croppedImage;
	}
}

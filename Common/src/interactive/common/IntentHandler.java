package interactive.common;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class IntentHandler
{

	public static final int	MODE_CAMERA_IMAGE	= 1;
	public static final int	MODE_VIDEO			= 2;
	public static final int	MODE_IMAGE_GALLERY	= 3;
	public static final int	MODE_VIDEO_GALLERY	= 4;
	public static final int	REQUEST_CODE_IMAGE	= 5;
	public static final int	REQUEST_CODE_VIDEO	= 6;

	private Intent			takeIntent			= null;

	public IntentHandler()
	{
		super();
	}

	public void intent(Activity activity, int nMode)
	{
		switch (nMode)
		{
		case MODE_CAMERA_IMAGE: //由拍照取得圖片
			takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			activity.startActivityForResult(takeIntent, REQUEST_CODE_IMAGE);
			break;
		case MODE_VIDEO: //由錄影取得影片
			takeIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			//設定錄影品質(0為低,1為高)
			takeIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			//設定錄影時間(秒)
			takeIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 900);
			activity.startActivityForResult(takeIntent, REQUEST_CODE_VIDEO);
			break;
		case MODE_IMAGE_GALLERY: //由選取檔案取得圖片
			takeIntent = new Intent();
			takeIntent.setType("image/*");
			takeIntent.setAction(Intent.ACTION_GET_CONTENT);
			activity.startActivityForResult(Intent.createChooser(takeIntent, "來源選擇"), REQUEST_CODE_IMAGE);
			break;
		case MODE_VIDEO_GALLERY: //由選取檔案取得影片
			takeIntent = new Intent();
			takeIntent.setType("video/*");
			takeIntent.setAction(Intent.ACTION_GET_CONTENT);
			activity.startActivityForResult(Intent.createChooser(takeIntent, "來源選擇"), REQUEST_CODE_VIDEO);
			break;
		}
	}

	public Bitmap activityResult(Activity activity, int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{ //使用者按下確定
			if (requestCode == REQUEST_CODE_IMAGE)
			{ //來源為圖片
				ContentResolver resolver = activity.getContentResolver();
				//取得圖片位址
				Uri uri = data.getData();
				Bitmap bmp = null;
				try
				{
					//取得圖片Bitmap
					bmp = MediaStore.Images.Media.getBitmap(resolver, uri);
					return bmp;
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else if (requestCode == REQUEST_CODE_VIDEO)
			{ //來源為影片
				//取得影片位址
				Uri uri = data.getData();

				//// 以下為取得影片示意圖用 ////
				String strPre = "video/media/";
				int videoID;
				ContentResolver resolver = activity.getContentResolver();
				//取得影片ID
				videoID = Integer.valueOf(uri.toString().substring(uri.toString().indexOf(strPre) + strPre.length()));
				BitmapFactory.Options options = new BitmapFactory.Options();
				//圖片品質
				options.inSampleSize = 2;
				//取得影片示意圖
				Bitmap bmpThumb = MediaStore.Video.Thumbnails.getThumbnail(resolver, videoID,
						MediaStore.Video.Thumbnails.MINI_KIND, options);
				return bmpThumb;
			}
		}
		else if (resultCode == Activity.RESULT_CANCELED)
		{
			//使用者按下取消或離開
		}
		return null;
	}
}

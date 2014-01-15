package interactive.view.postcardorg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

public class PostCardUtility
{
	private Activity			act;
	private Context				context;
	private AbsoluteLayout		main;
	private FrameLayout			mainPostcard;
	private ImageView			img_camera;
	private ImageView			img_photo;
	private ImageView			img_postcard;
	private ImageView			img_drawing;
	private ImageView			img_postbox;
	private ImageView			drawing_pen;
	private ImageView			drawing_eraser;

	private EditText			textArea;
	private ViewGroup			albumView;
	private ListView			photoList;
	private static Uri			outputFileUri;
	private LayoutInflater		mInflater;
	private ArrayList<String>	al;
	private Paint				mPaint;
	private FingerPaint			fp;
	public FingerPaintView		fpv;
	public static final int		TAKE_PICTURE	= 1122;
	private int					mainX;
	private int					mainY;

	public PostCardUtility(Context context, AbsoluteLayout main)
	{
		this.act = (Activity) context;
		this.context = context;
		this.main = main;
		mInflater = LayoutInflater.from(context);
		main.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
	}

	public void setCamera(int width, int height, int x, int y, String uriString) // 設定相機
	{
		img_camera = new ImageView(context);
		img_camera.setImageURI(Uri.parse(uriString));
		// img_camera.setImageResource(R.drawable.camera);//yt
		AbsoluteLayout.LayoutParams img_camera_lp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		main.addView(img_camera, img_camera_lp);
		img_camera.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				takePics();
				if (!img_postcard.isShown())
				{
					img_postcard.setVisibility(View.VISIBLE);
					fpv.setVisibility(View.INVISIBLE);
					if (drawing_pen != null)
					{
						drawing_pen.setVisibility(View.INVISIBLE);
					}

					if (drawing_eraser != null)
					{
						drawing_eraser.setVisibility(View.INVISIBLE);
					}

					if (textArea != null)
					{
						textArea.setVisibility(View.INVISIBLE);
					}

				}
			}
		});
	}

	public void setAlbum(int width, int height, int x, int y, String uriString, final int[] listParams) // 設定相簿圖片
	{
		img_photo = new ImageView(context);
		img_photo.setImageURI(Uri.parse(uriString));
		// img_photo.setImageResource(R.drawable.photo);//yt
		AbsoluteLayout.LayoutParams img_photo_lp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		main.addView(img_photo, img_photo_lp);
		img_photo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				openAlbum(listParams[0], listParams[1], listParams[2], listParams[3]);
				if (!img_postcard.isShown())
				{
					img_postcard.setVisibility(View.VISIBLE);
					fpv.setVisibility(View.INVISIBLE);
					if (drawing_pen != null)
					{
						drawing_pen.setVisibility(View.INVISIBLE);
					}

					if (drawing_eraser != null)
					{
						drawing_eraser.setVisibility(View.INVISIBLE);
					}

					if (textArea != null)
					{
						textArea.setVisibility(View.INVISIBLE);
					}

				}
			}
		});
	}

	private void applyRotation(int position, float start, float end, int width2, int height2)
	{
		// Find the center of the container
		final float centerX = width2 / 2.0f;
		final float centerY = height2 / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final MyAnimation rotation = new MyAnimation(start, end, centerX, centerY, 180.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(position, width2, height2));

		mainPostcard.startAnimation(rotation);
	}

	private final class DisplayNextView implements Animation.AnimationListener
	{
		private final int	mPosition;
		private final int	mWidth;
		private final int	mHeight;

		private DisplayNextView(int position, int width, int height)
		{
			mPosition = position;
			mWidth = width;
			mHeight = height;
		}

		public void onAnimationStart(Animation animation)
		{
		}

		public void onAnimationEnd(Animation animation)
		{
			img_postcard.post(new SwapViews(mPosition, mWidth, mHeight));
		}

		public void onAnimationRepeat(Animation animation)
		{
		}
	}

	private final class SwapViews implements Runnable
	{
		private final int	mPosition;
		private final int	mWidth;
		private final int	mHeight;

		public SwapViews(int position, int width, int height)
		{
			mPosition = position;
			mWidth = width;
			mHeight = height;
		}

		public void run()
		{
			final float centerX = mWidth / 2.0f;
			final float centerY = mHeight / 2.0f;
			MyAnimation rotation;

			if (img_postcard.getVisibility() == View.INVISIBLE)
			{
				img_postcard.setVisibility(View.VISIBLE);
				fpv.setVisibility(View.INVISIBLE);
				//DRAWING = false;
				if (img_camera != null)
				{
					img_camera.setVisibility(View.VISIBLE);
				}

				if (img_photo != null)
				{
					img_photo.setVisibility(View.VISIBLE);
				}

				if (drawing_pen != null)
				{
					drawing_pen.setVisibility(View.INVISIBLE);
				}

				if (drawing_eraser != null)
				{
					drawing_eraser.setVisibility(View.INVISIBLE);
				}
				if (textArea != null)
				{
					textArea.setVisibility(View.INVISIBLE);
				}
				// mImageView.requestFocus();

				if (mPosition == 0)
				{
					rotation = new MyAnimation(-90, 0, centerX, centerY, 180.0f, false);
				}
				else
				{
					rotation = new MyAnimation(90, 0, centerX, centerY, 180.0f, false);
				}
			}
			else
			{
				img_postcard.setVisibility(View.INVISIBLE);
				fpv.setVisibility(View.VISIBLE);
				//DRAWING = true;
				if (img_camera != null)
				{
					img_camera.setVisibility(View.INVISIBLE);
				}

				if (img_photo != null)
				{
					img_photo.setVisibility(View.INVISIBLE);
				}

				if (drawing_pen != null)
				{
					drawing_pen.setVisibility(View.VISIBLE);
				}
				if (drawing_eraser != null)
				{
					drawing_eraser.setVisibility(View.VISIBLE);
				}
				if (textArea != null)
				{
					textArea.setVisibility(View.VISIBLE);
				}
				// mPhotosList.requestFocus();
				if (mPosition == 0)
				{
					rotation = new MyAnimation(-90, 0, centerX, centerY, 180.0f, false);
				}
				else
				{
					rotation = new MyAnimation(90, 0, centerX, centerY, 180.0f, false);
				}

			}

			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());

			mainPostcard.startAnimation(rotation);
		}
	}

	public void setPosterCard(final int width, final int height, int x, int y, String uriString) // 設定明信片
	{
		mainPostcard = new FrameLayout(context);
		img_postcard = new ImageView(context);
		img_postcard.setImageURI(Uri.parse(uriString));
		mainX = x;
		mainY = y;
		final GestureDetector mGestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener()
				{

					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
					{

						int dx = (int) (e2.getX() - e1.getX());
						if (Math.abs(dx) > 10 && Math.abs(velocityX) > Math.abs(velocityY))
						{
							if (velocityX > 0)
							{
								// System.out.println("right");
								applyRotation(0, 0, 90, width, height);
							}
							else
							{
								// System.out.println("left");
								applyRotation(-1, 0, -90, width, height);
							}
							return true;
						}
						else
						{
							return false;
						}
					}
				});

		img_postcard.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				mGestureDetector.onTouchEvent(event);
				if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
				{
					//			AppCrossApplication.unLock();
				}
				else
				{
					//			AppCrossApplication.lock();
				}
				return true;
			}

		});
		// img_poster.setImageResource(R.drawable.doc);//yt
		AbsoluteLayout.LayoutParams img_postcard_lp = new AbsoluteLayout.LayoutParams(width, height, 0, 0);
		mainPostcard.addView(img_postcard, img_postcard_lp);
		img_postcard_lp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		main.addView(mainPostcard, img_postcard_lp);
	}

	public void setDrawing(int width, int height, int x, int y, String uriString) // 設定繪圖
	{
		img_drawing = new ImageView(context);
		int nResId = context.getResources().getIdentifier("post_hint", "drawable", context.getPackageName());

		AbsoluteLayout.LayoutParams img_drawing_lp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		main.addView(img_drawing, img_drawing_lp);

	}

	public void setFingerPaintView(final int width, final int height, int x, int y, String uriString) // 設定繪圖物件
	{
		fp = new FingerPaint(act);
		// mPaint = fp.getPaint();
		fpv = new FingerPaintView(context, fp);
		AbsoluteLayout.LayoutParams allp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		fpv.setLayoutParams(allp);

		//	String path = uriString.substring(uriString.indexOf("file://") + 7, uriString.length());
		//	Logs.showTrace("uriString=" + uriString + " path: " + path + " ################");
		fpv.setBackgroundDrawable(Drawable.createFromPath(uriString));
		fpv.setVisibility(View.INVISIBLE);

		final GestureDetector mGestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener()
				{

					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
					{

						int dx = (int) (e2.getX() - e1.getX());
						if (Math.abs(dx) > 10 && Math.abs(velocityX) > Math.abs(velocityY))
						{
							if (velocityX > 0)
							{
								// System.out.println("right");
								applyRotation(0, 0, 90, width, height);
							}
							else
							{
								// System.out.println("left");
								applyRotation(-1, 0, -90, width, height);
							}
							return true;
						}
						else
						{
							return false;
						}
					}
				});

		fpv.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				if (fp.getMode() == 0)
				{
					mGestureDetector.onTouchEvent(event);
				}

				if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
				{
		//			AppCrossApplication.unLock();
				}
				else
				{
		//			AppCrossApplication.lock();
				}
				return false;
			}

		});

		allp = new AbsoluteLayout.LayoutParams(width, height, 0, 0);
		mainPostcard.addView(fpv, allp);
	}

	public void setDrawingPen(int width, int height, int x, int y, String uriString)
	{
		drawing_pen = new ImageView(context);
		drawing_pen.setImageURI(Uri.parse(uriString));
		AbsoluteLayout.LayoutParams img_pen_lp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		drawing_pen.setLayoutParams(img_pen_lp);
		drawing_pen.setVisibility(View.INVISIBLE);
		main.addView(drawing_pen, img_pen_lp);
		drawing_pen.setColorFilter(Color.parseColor("#70000000"));
		drawing_pen.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (fp.getMode() == 0 || fp.getMode() == 4)
				{
					((ImageView) v).setColorFilter(Color.TRANSPARENT);
					drawing_eraser.setColorFilter(Color.parseColor("#70000000"));
					fp.setPaintOptions(6);
				}
				else
				{

					((ImageView) v).setColorFilter(Color.parseColor("#70000000"));
					fp.setPaintOptions(0);
				}

			}

		});
	}

	public void setDrawingEraser(int width, int height, int x, int y, String uriString)
	{
		drawing_eraser = new ImageView(context);
		drawing_eraser.setImageURI(Uri.parse(uriString));
		AbsoluteLayout.LayoutParams img_eraser_lp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		drawing_eraser.setLayoutParams(img_eraser_lp);
		drawing_eraser.setVisibility(View.INVISIBLE);
		main.addView(drawing_eraser, img_eraser_lp);
		drawing_eraser.setColorFilter(Color.parseColor("#70000000"));
		drawing_eraser.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (fp.getMode() == 0 || fp.getMode() == 6)
				{
					((ImageView) v).setColorFilter(Color.TRANSPARENT);
					drawing_pen.setColorFilter(Color.parseColor("#70000000"));
					fp.setPaintOptions(4);
				}
				else
				{

					((ImageView) v).setColorFilter(Color.parseColor("#70000000"));

					fp.setPaintOptions(0);
				}

			}

		});
	}

	public void setPostBox(int width, int height, int x, int y, String uriString) // 設定郵寄
	{
		img_postbox = new ImageView(context);
		img_postbox.setImageURI(Uri.parse(uriString));
		// img_postbox.setImageResource(R.drawable.postbox);//yt
		AbsoluteLayout.LayoutParams img_postbox_lp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		main.addView(img_postbox, img_postbox_lp);
		img_postbox.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				sendEMails();
			}
		});
	}

	public void setTextArea(int width, int height, int x, int y, String uriString) // 設定郵寄
	{
		textArea = new EditText(context);

		String path = uriString.substring(uriString.indexOf("file://") + 7, uriString.length());
		textArea.setBackgroundDrawable(Drawable.createFromPath(path));

		textArea.setVisibility(View.INVISIBLE);
		textArea.setGravity(Gravity.TOP | Gravity.LEFT);
		FrameLayout.LayoutParams textArea_lp = new FrameLayout.LayoutParams(width, height);
		textArea_lp.leftMargin = x - mainX;
		textArea_lp.topMargin = y - mainY;

		mainPostcard.addView(textArea, textArea_lp);

	}

	private void takePics() // 拍攝照片
	{
		// Create an output file.
		File file = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
		outputFileUri = Uri.fromFile(file);
		// Log.d("Ned", "path:" + outputFileUri.toString());

		// Generate the Intent.
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		// Launch the camera app.
		act.startActivityForResult(intent, TAKE_PICTURE);
	}

	private void openAlbum(int width, int height, int x, int y) // 開啟相簿選取圖片
	{
		// albumView = mInflater.inflate(R.layout.album_layout, null);//yt
		// photoList = (ListView)albumView.findViewById(R.id.photo_list);//yt

		albumView = new AbsoluteLayout(context);
		AbsoluteLayout.LayoutParams albumView_lp = new AbsoluteLayout.LayoutParams(
				AbsoluteLayout.LayoutParams.MATCH_PARENT, AbsoluteLayout.LayoutParams.MATCH_PARENT, 0, 0);
		albumView.setLayoutParams(albumView_lp);
		main.addView(albumView, albumView_lp);
		photoList = new ListView(context);
		photoList.setBackgroundColor(Color.LTGRAY);
		AbsoluteLayout.LayoutParams photoList_lp = new AbsoluteLayout.LayoutParams(width, height, x, y);
		photoList.setLayoutParams(photoList_lp);
		albumView.addView(photoList, photoList_lp);

		// ///
		al = new ArrayList<String>();
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
		dirCheck(file);
		photoList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
			{
				// TODO Auto-generated method stub
				BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
				// Decode the image file into a Bitmap sized to fill the View
				factoryOptions.inJustDecodeBounds = false;
				factoryOptions.inSampleSize = 4;
				factoryOptions.inPurgeable = true;
				img_postcard.setImageBitmap(BitmapFactory.decodeFile(al.get(position), factoryOptions));
			}
		});
		photoList.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, al
				.toArray(new String[al.size()])));

		albumView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				main.removeView(albumView);
			}
		});
	}

	public void loadPics(Intent data) // 拍攝照片的後續處理
	{
		// Check if the result includes a thumbnail Bitmap
		if (data != null)
		{
			// Log.d("Ned", "data != null");
			if (data.hasExtra("data"))
			{
				// Log.d("Ned", "hasExtra");
				Bitmap thumbnail = data.getParcelableExtra("data");
				img_postcard.setImageBitmap(thumbnail);
				// saveToJPEG(); 
			}
		}
		else
		{
			// Log.d("Ned", "data = null");
			// If there is no thumbnail image data, the image
			// will have been stored in the target output URI.

			// Resize the full image to fit in out image view.
			int width = img_postcard.getWidth();
			int height = img_postcard.getHeight();
			// Log.d("Ned", "fileUri:" + outputFileUri.getPath());
			BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
			factoryOptions.inJustDecodeBounds = true;
			// BitmapFactory.decodeFile(outputFileUri.getPath(),
			// factoryOptions);

			int imageWidth = factoryOptions.outWidth;
			int imageHeight = factoryOptions.outHeight;

			// Determine how much to scale down the image
			int scaleFactor = Math.min(imageWidth / width, imageHeight / height);

			// Decode the image file into a Bitmap sized to fill the View
			factoryOptions.inJustDecodeBounds = false;
			factoryOptions.inSampleSize = scaleFactor;
			factoryOptions.inPurgeable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(outputFileUri.getPath(), factoryOptions);
			img_postcard.setImageBitmap(bitmap);
			// Log.d("Ned", "bitmap: " + bitmap.getByteCount());
		}
	}

	private void saveToJPEG()
	{
		System.gc();
		BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
		factoryOptions.inJustDecodeBounds = true;
		factoryOptions.inJustDecodeBounds = false;
		factoryOptions.inSampleSize = 1;
		factoryOptions.inPurgeable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(outputFileUri.getPath(), factoryOptions);
		// Log.d("Ned", "bitmap rowbytes:" + bitmap.getRowBytes());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		File file = new File(Environment.getExternalStorageDirectory(), "image.jpg");
		try
		{
			// Log.d("Ned", "baos:" + baos.size());
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendEMails() // 郵寄明信片, 目前設定為私人信箱
	{

		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		intent.setType("image/jpeg");
		// intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {
		// "nedhwchwn@iii.org.tw" });
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Poster Card");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		ArrayList<Uri> uris = new ArrayList<Uri>();

		uris.add(getImage(img_postcard, context.getExternalCacheDir().getPath() + File.separator + "front.png"));
		if (textArea != null)
			uris.add(getImage(mainPostcard, true, context.getExternalCacheDir().getPath() + File.separator + "back.png"));
		else
			uris.add(getImage(fpv, context.getExternalCacheDir().getPath() + File.separator + "back.png"));

		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		act.startActivity(Intent.createChooser(intent, "Send mail..."));
	}

	private Uri getImage(FrameLayout v, boolean b, String path)
	{
		// TODO Auto-generated method stub
		Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);

		if (img_postcard.getVisibility() == View.INVISIBLE)
		{
			v.draw(canvas);
		}
		else
		{
			img_postcard.setVisibility(View.INVISIBLE);
			fpv.setVisibility(View.VISIBLE);
			textArea.setVisibility(View.VISIBLE);
			v.draw(canvas);
			img_postcard.setVisibility(View.VISIBLE);
			fpv.setVisibility(View.INVISIBLE);
			textArea.setVisibility(View.INVISIBLE);
		}

		FileOutputStream out;
		try
		{
			out = new FileOutputStream(path);

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return Uri.fromFile(new File(path));
	}

	private Uri getImage(View v, String path)
	{
		// TODO Auto-generated method stub
		Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);

		v.draw(canvas);
		FileOutputStream out;
		try
		{
			out = new FileOutputStream(path);

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return Uri.fromFile(new File(path));
	}

	private void dirCheck(File f) // 遞迴
	{
		// Log.d("Ned", "fileName:" + f.toString());
		if (!f.toString().contains("/.") && f.canRead())
		{
			File lf[] = f.listFiles();
			for (int i = 0; i < lf.length; i++)
			{
				// if(lf[i].isDirectory())
				// DirCheck(lf[i]);
				// else
				// ItemCheck(lf[i]);

				if (!lf[i].isDirectory())
					itemCheck(lf[i]);
			}
		}
	}

	public void setDisplayMode(boolean isFront)
	{

	}

	private void itemCheck(File f)
	{
		String item = f.toString();
		if (item.endsWith(".jpg"))
		{
			if (item.contains(" "))
			{
				String tmpFile = item;
				tmpFile = tmpFile.replaceAll(" ", "_");
				File originalFile = new File(item);
				File newFile = new File(tmpFile);
				originalFile.renameTo(newFile);
				item = newFile.getPath();
			}

			String tmpFileName = item.substring(0, item.lastIndexOf("."));
			if (tmpFileName.contains("."))
			{
				tmpFileName = tmpFileName.replaceAll("\\.", "_");
				String tmpItem = tmpFileName + item.substring(item.lastIndexOf("."), item.length());
				File originalFile = new File(item);
				File newFile = new File(tmpItem);
				originalFile.renameTo(newFile);
				item = tmpItem;
			}
			// Log.d("Ned", "DCIM item:" + item);
			al.add(item);
		}
	}

	public FingerPaint getFingerPaint()
	{
		return fp;
	}

	public ImageView getPostCardImg()
	{
		return img_postcard;
	}
}

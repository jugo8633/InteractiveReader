package interactive.view.postcard;

import java.io.File;
import java.io.FileOutputStream;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.IntentHandler;
import interactive.common.Share;
import interactive.view.animation.flipcard.Rotate3d;
import interactive.view.animation.zoom.ZoomHandler;
import interactive.view.global.Global;
import interactive.view.pagereader.PageReader;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class Postcard
{

	public static final int			POSTCARD_ACTIVITY_RESULT	= 777778;
	private FrameLayout				postcardFrame				= null;
	private ViewGroup				container					= null;
	private Context					theContext					= null;
	private GestureDetector			gestureDetector				= null;
	private FingerPaintView			fingerPaintView				= null;
	private ImageView				imgPostFront				= null;
	private Rotate3d				rotate3d					= null;
	private String					mstrPostcardFrontPath		= null;
	private String					mstrPostcardBackPath		= null;
	private ScaleGestureDetector	scaleGestureDetector		= null;
	private ImageView				imgThumb					= null;
	private boolean					mbScaling					= false;
	private boolean					mbFront						= true;
	private ImageView				imgDrag						= null;
	private int						mnPostcardWidth				= 0;
	private int						mnPostcardHeight			= 0;
	private EditText				editText					= null;
	private String					mstrText					= null;
	private int						mnTextMaxLine				= 1;

	public Postcard(Context context, ViewGroup viewGroup)
	{
		super();
		theContext = context;
		container = viewGroup;
		postcardFrame = new FrameLayout(context);
		gestureDetector = new GestureDetector(context, simpleOnGestureListener);
		rotate3d = new Rotate3d();

		mstrPostcardFrontPath = context.getExternalCacheDir().getPath() + File.separator + "front.png";
		mstrPostcardBackPath = context.getExternalCacheDir().getPath() + File.separator + "back.png";

		scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
	}

	public void initPostcardFrame(String strName, int nX, int nY, int nWidth, int nHeight, String strFront,
			String strBack)
	{
		mnPostcardWidth = nWidth;
		mnPostcardHeight = nHeight;
		postcardFrame.setId(++Global.mnUserId);
		postcardFrame.setTag(strName);
		postcardFrame.setX(nX);
		postcardFrame.setY(nY);
		postcardFrame.setLayoutParams(new LayoutParams(mnPostcardWidth, mnPostcardHeight));
		container.removeView(postcardFrame);
		container.addView(postcardFrame);

		initPostcard(strFront, strBack);

		postcardFrame.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_HORIZON, 0, 0, null);
					break;
				case MotionEvent.ACTION_UP:
					EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_HORIZON, 0, 0, null);
					break;
				}
				gestureDetector.onTouchEvent(event);
				scaleGestureDetector.onTouchEvent(event);
				return true;
			}
		});

		rotate3d.setlistOnRotateEndListener(new Rotate3d.OnRotateEndListener()
		{
			@Override
			public void onRotateEnd()
			{
				switchCard();
			}
		});

		Global.pageReader.setOnPageSwitchedListener(new PageReader.OnPageSwitchedListener()
		{
			@Override
			public void onPageSwitched()
			{
				fingerPaintView.setIsCapturing(false);
				clearSelected();
			}
		});
	}

	/** set postcard front image and back image */
	private void initPostcard(String strFront, String strBack)
	{
		postcardFrame.removeAllViewsInLayout();

		if (null != strBack)
		{
			fingerPaintView = new FingerPaintView(theContext);
			fingerPaintView.setId(++Global.mnUserId);
			fingerPaintView.setBackground(strBack);
			fingerPaintView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			postcardFrame.addView(fingerPaintView);
			fingerPaintView.setVisibility(View.INVISIBLE);
		}

		if (null != strFront)
		{
			imgPostFront = new ImageView(theContext);
			imgPostFront.setId(++Global.mnUserId);
			imgPostFront.setImageURI(Uri.parse(strFront));
			imgPostFront.setScaleType(ScaleType.CENTER_CROP);
			imgPostFront.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			postcardFrame.addView(imgPostFront);
		}

	}

	private void switchCard()
	{

		if (imgPostFront.getVisibility() == View.VISIBLE)
		{
			showBack();
		}
		else
		{
			showFront();
		}
	}

	private void showFront()
	{
		mbFront = true;
		imgPostFront.setVisibility(View.VISIBLE);
		fingerPaintView.setVisibility(View.INVISIBLE);
		showEdit(false);
		showImageFrom(true);
	}

	private void showBack()
	{
		mbFront = false;
		fingerPaintView.setVisibility(View.VISIBLE);
		imgPostFront.setVisibility(View.INVISIBLE);
		showEdit(true);
		showImageFrom(false);
	}

	private void showEdit(boolean bShow)
	{
		clearSelected();
		showView(container.findViewWithTag("pen"), bShow);
		showView(container.findViewWithTag("eraser"), bShow);
		showView(postcardFrame.findViewWithTag("textArea"), bShow);
	}

	private void showImageFrom(boolean bShow)
	{
		showView(container.findViewWithTag("camera"), bShow);
		showView(container.findViewWithTag("openButton"), bShow);
	}

	private void showView(View view, boolean bShow)
	{
		if (null == view)
		{
			return;
		}
		if (bShow)
		{
			view.setVisibility(View.VISIBLE);
		}
		else
		{
			view.setVisibility(View.INVISIBLE);
		}
	}

	public void initPen(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgPen = new ImageView(theContext);
		imgPen.setTag("pen");
		imgPen.setVisibility(View.GONE);
		initOptionImage(imgPen, nWidth, nHeight, nX, nY, strImagePath);
	}

	public void initEraser(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgEraser = new ImageView(theContext);
		imgEraser.setTag("eraser");
		imgEraser.setVisibility(View.GONE);
		initOptionImage(imgEraser, nWidth, nHeight, nX, nY, strImagePath);
	}

	public void initTextArea(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		mnTextMaxLine = nHeight / 24;
		--mnTextMaxLine;
		editText = new EditText(theContext);
		editText.setTextColor(Color.BLUE);
		editText.setHintTextColor(Color.GRAY);
		editText.setHint("tap to type");
		editText.setTag("textArea");
		editText.setVisibility(View.GONE);
		editText.setX(nX - postcardFrame.getX());
		editText.setY(nY - postcardFrame.getY());

		editText.setLayoutParams(new LayoutParams(nWidth, nHeight));
		editText.setTextSize(24f);
		postcardFrame.addView(editText);

		editText.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void afterTextChanged(Editable s)
			{
				int lineCount = editText.getLineCount();
				if (lineCount > mnTextMaxLine)
				{
					editText.setText(mstrText);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				mstrText = s.toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{

			}
		});
	}

	public void initCamera(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgCamera = new ImageView(theContext);
		imgCamera.setTag("camera");
		initOptionImage(imgCamera, nWidth, nHeight, nX, nY, strImagePath);
	}

	public void initOpenButton(int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		ImageView imgOpenButton = new ImageView(theContext);
		imgOpenButton.setTag("openButton");
		initOptionImage(imgOpenButton, nWidth, nHeight, nX, nY, strImagePath);
	}

	private void initOptionImage(ImageView img, int nWidth, int nHeight, int nX, int nY, String strImagePath)
	{
		img.setX(nX);
		img.setY(nY);
		img.setLayoutParams(new LayoutParams(nWidth, nHeight));
		img.setImageURI(Uri.parse(strImagePath));
		img.setScaleType(ScaleType.CENTER_CROP);
		container.addView(img);
		img.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onOptionClick(v);
			}
		});
	}

	private void onOptionClick(View view)
	{
		if (null == view)
		{
			return;
		}
		String strTag = (String) view.getTag();

		if (strTag.equals("pen"))
		{
			if (null == view.getBackground())
			{
				View eraserView = container.findViewWithTag("eraser");
				if (null != eraserView)
				{
					eraserView.setBackground(null);
				}
				view.setBackgroundColor(Color.YELLOW);
				fingerPaintView.setIsCapturing(true);
				fingerPaintView.setEraser(false);
				container.requestDisallowInterceptTouchEvent(true);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
			}
			else
			{
				container.requestDisallowInterceptTouchEvent(false);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
				fingerPaintView.setIsCapturing(false);
				view.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		if (strTag.equals("eraser"))
		{
			if (null == view.getBackground())
			{
				View penView = container.findViewWithTag("pen");
				if (null != penView)
				{
					penView.setBackground(null);
				}
				view.setBackgroundColor(Color.YELLOW);
				fingerPaintView.setIsCapturing(true);
				fingerPaintView.setEraser(true);
				container.requestDisallowInterceptTouchEvent(true);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
			}
			else
			{
				container.requestDisallowInterceptTouchEvent(false);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
				fingerPaintView.setIsCapturing(false);
				view.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		if (strTag.equals("camera"))
		{
			cameraCapture();
			imgPostFront.setVisibility(View.VISIBLE);
			fingerPaintView.setVisibility(View.INVISIBLE);
			showEdit(false);
		}

		if (strTag.equals("openButton"))
		{
			pictureSelect();
			imgPostFront.setVisibility(View.VISIBLE);
			fingerPaintView.setVisibility(View.INVISIBLE);
			showEdit(false);
		}
	}

	private void clearSelected()
	{
		View eraserView = container.findViewWithTag("eraser");
		if (null != eraserView)
		{
			eraserView.setBackground(null);
		}

		View penView = container.findViewWithTag("pen");
		if (null != penView)
		{
			penView.setBackground(null);
		}
	}

	public void sendPostcard()
	{
		boolean bResult = true;
		if (null == fingerPaintView || null == mstrPostcardBackPath || null == mstrPostcardFrontPath)
		{
			return;
		}

		if (null != mstrText)
		{
			imgPostFront.setVisibility(View.INVISIBLE);
			fingerPaintView.setVisibility(View.VISIBLE);
			editText.setVisibility(View.VISIBLE);
			postcardFrame.invalidate();
			bResult = exportBitmap(postcardFrame, mstrPostcardBackPath);
			if (this.mbFront)
			{
				imgPostFront.setVisibility(View.VISIBLE);
				fingerPaintView.setVisibility(View.INVISIBLE);
				editText.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			bResult = fingerPaintView.exportBitmap(mstrPostcardBackPath, mnPostcardWidth, mnPostcardHeight);
		}

		if (bResult && exportBitmap(mstrPostcardFrontPath))
		{
			Share share = new Share(Global.theActivity);
			SparseArray<String> listImage = new SparseArray<String>();
			listImage.put(listImage.size(), mstrPostcardFrontPath);
			listImage.put(listImage.size(), mstrPostcardBackPath);
			share.shareAll("Postcard", "Postcard", null, listImage);
			share = null;
		}
		hidePostcard(false);
	}

	private boolean exportBitmap(View view, String strPath)
	{
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		view.draw(c);

		FileOutputStream out;
		try
		{
			out = new FileOutputStream(strPath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private boolean exportBitmap(String strPath)
	{
		Bitmap bitmap = Bitmap.createBitmap(imgPostFront.getWidth(), imgPostFront.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		imgPostFront.draw(c);

		FileOutputStream out;
		try
		{
			out = new FileOutputStream(strPath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private void cameraCapture()
	{
		Global.handlerPostcard = postcardHandler;

		IntentHandler intentHandler = new IntentHandler();
		intentHandler.intent(Global.theActivity, IntentHandler.MODE_CAMERA_IMAGE);
		intentHandler = null;
	}

	private void getPicture(Bitmap bitmap)
	{
		imgPostFront.setImageBitmap(bitmap);
	}

	private void pictureSelect()
	{
		Global.handlerPostcard = postcardHandler;

		IntentHandler intentHandler = new IntentHandler();
		intentHandler.intent(Global.theActivity, IntentHandler.MODE_IMAGE_GALLERY);
		intentHandler = null;
	}

	private void zoomPostcard()
	{
		imgThumb = new ImageView(theContext);
		Bitmap bitmap = Bitmap.createBitmap(postcardFrame.getWidth(), postcardFrame.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		postcardFrame.draw(c);
		imgThumb.setTag("thumbImage");
		imgThumb.setX(postcardFrame.getX());
		imgThumb.setY(postcardFrame.getY());
		imgThumb.setLayoutParams(new LayoutParams(postcardFrame.getWidth(), postcardFrame.getHeight()));
		imgThumb.setImageBitmap(bitmap);
		imgThumb.setScaleType(ScaleType.CENTER_CROP);
		imgThumb.setPadding(2, 2, 2, 2);
		imgThumb.setBackgroundColor(Color.GRAY);
		container.addView(imgThumb);
		imgThumb.bringToFront();
		imgThumb.setVisibility(View.VISIBLE);

		imgDrag = new ImageView(theContext);
		imgDrag.setTag("dragImage");
		imgDrag.setLayoutParams(new LayoutParams(postcardFrame.getWidth() / 4, postcardFrame.getHeight() / 4));
		imgDrag.setImageBitmap(bitmap);
		imgDrag.setScaleType(ScaleType.CENTER_CROP);
		imgDrag.setX(postcardFrame.getX());
		imgDrag.setY(postcardFrame.getY());
		imgDrag.setVisibility(View.INVISIBLE);
		imgDrag.setBackgroundColor(Color.DKGRAY);
		imgDrag.setPadding(2, 2, 2, 4);
		container.addView(imgDrag);

		Global.interactiveHandler.setPostcardDragTag((String) postcardFrame.getTag());
		hidePostcard(true);
		ZoomHandler zoomHandler = new ZoomHandler(theContext);
		zoomHandler.zoomOut(imgThumb, 0.25f);
		zoomHandler.setNotifyHandler(postcardHandler);
		container.invalidate();
	}

	public String getTag()
	{
		return (String) postcardFrame.getTag();
	}

	private void startDrag()
	{
		container.removeView(imgThumb);
		imgThumb = null;
		if (null == imgDrag)
		{
			return;
		}

		ClipData.Item item = new ClipData.Item((CharSequence) imgDrag.getTag());

		String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };
		ClipData data = new ClipData(imgDrag.getTag().toString(), mimeTypes, item);
		DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imgDrag);
		imgDrag.startDrag(data, //data to be dragged
				shadowBuilder, //drag shadow
				imgDrag, //local data about the drag and drop operation
				0 //no needed flags
		);

	}

	public void hidePostcard(boolean bhide)
	{
		if (bhide)
		{
			fingerPaintView.setVisibility(View.INVISIBLE);
			imgPostFront.setVisibility(View.INVISIBLE);
			postcardFrame.setVisibility(View.INVISIBLE);
			if (null != editText)
			{
				editText.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			if (mbFront)
			{
				imgPostFront.setVisibility(View.VISIBLE);
				imgPostFront.bringToFront();
			}
			else
			{
				fingerPaintView.setVisibility(View.VISIBLE);
				if (null != editText)
				{
					editText.setVisibility(View.VISIBLE);
				}
				fingerPaintView.bringToFront();
			}
			postcardFrame.setVisibility(View.VISIBLE);
		}
	}

	private Handler			postcardHandler			= new Handler()
													{
														@Override
														public void handleMessage(Message msg)
														{
															switch (msg.what)
															{
															case EventMessage.MSG_ACTIVITY_RESULT:
																getPicture((Bitmap) msg.obj);
																break;
															case EventMessage.MSG_ANIMATION_END:
																startDrag();
																break;
															case EventMessage.MSG_DRAG_END:
																container.removeView(imgDrag);
																hidePostcard(false);
																break;
															}

															super.handleMessage(msg);
														}
													};

	SimpleOnGestureListener	simpleOnGestureListener	= new SimpleOnGestureListener()
													{

														@Override
														public boolean onFling(MotionEvent e1, MotionEvent e2,
																float velocityX, float velocityY)
														{
															if (null == e1 || null == e2 || mbScaling)
															{
																return super.onFling(e1, e2, velocityX, velocityY);
															}
															float sensitvity = 50;

															if ((e1.getX() - e2.getX()) > sensitvity)
															{
																// left
																rotate3d.applyRotation(postcardFrame, 0, -90,
																		Rotate3d.ROTATE_LEFT);

															}
															else if ((e2.getX() - e1.getX()) > sensitvity)
															{
																// right
																rotate3d.applyRotation(postcardFrame, 0, 90,
																		Rotate3d.ROTATE_RIGHT);

															}
															return super.onFling(e1, e2, velocityX, velocityY);
														}
													};

	public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener
	{
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			float factor = detector.getScaleFactor();
			if (factor < 1.0f && !mbScaling) // 縮小
			{
				mbScaling = true;
				zoomPostcard();
				return true;
			}
			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			mbScaling = false;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			mbScaling = false;
			container.removeView(imgThumb);
			imgThumb = null;
		}
	}
}

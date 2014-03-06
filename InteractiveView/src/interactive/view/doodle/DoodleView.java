package interactive.view.doodle;

import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.fingerpaint.FingerPaintView;
import interactive.view.flip.FlipperView;
import interactive.view.global.Global;
import interactive.view.image.ImageViewHandler;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class DoodleView extends RelativeLayout
{
	private ViewGroup			container		= null;
	private ImageViewHandler	imageHandler	= null;
	private boolean				mbCurrentActive	= false;
	private FingerPaintView		fingerPaintView	= null;
	private int					mnBrushes		= 3;
	private int					mnPalette		= 0;
	private int					mnEraserBtn		= Type.INVALID;
	private int					mnPaletteBtn	= Type.INVALID;
	private int					mnPenBtn		= Type.INVALID;
	private int					mnResetBtn		= Type.INVALID;
	private int					mnSaveBtn		= Type.INVALID;
	private RelativeLayout		leftRL			= null;
	private RelativeLayout		rightRL			= null;
	private DrawerLayout		drawerLayout	= null;

	private DoodleView(Context context)
	{
		super(context);
	}

	public DoodleView(Context context, ViewGroup viewGroup)
	{
		super(context);
		container = viewGroup;
		imageHandler = new ImageViewHandler(selfHandler);

		fingerPaintView = new FingerPaintView(context);
		fingerPaintView.setId(Global.getUserId());
		fingerPaintView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(fingerPaintView);
		fingerPaintView.setIsCapturing(true);
		fingerPaintView.setEraser(false);
		initSlidingMenu();
	}

	private void initSlidingMenu()
	{
		ListView listView = new ListView(getContext());
		listView.setLayoutParams(new LayoutParams(100, LayoutParams.MATCH_PARENT));
		listView.setBackgroundColor(Color.GREEN);
		ListView listView2 = new ListView(getContext());
		listView2.setLayoutParams(new LayoutParams(100, LayoutParams.MATCH_PARENT));
		listView2.setBackgroundColor(Color.BLUE);
		leftRL = new RelativeLayout(getContext());
		rightRL = new RelativeLayout(getContext());
		drawerLayout = new DrawerLayout(getContext());

		leftRL.setGravity(Gravity.START);
		rightRL.setGravity(Gravity.RIGHT);

		leftRL.setLayoutParams(new LayoutParams(100, LayoutParams.MATCH_PARENT));
		rightRL.setLayoutParams(new LayoutParams(100, LayoutParams.MATCH_PARENT));

		leftRL.setBackgroundColor(Color.GRAY);
		rightRL.setBackgroundColor(Color.RED);

		leftRL.addView(listView);
		rightRL.addView(listView2);

		drawerLayout.setLayoutParams(new LayoutParams(800, 800));
		drawerLayout.addView(leftRL);
		drawerLayout.addView(rightRL);
		container.addView(drawerLayout);
	}

	public void SetDisplay(float fX, float fY, int nWidth, int nHeight)
	{
		this.setX(fX);
		this.setY(fY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
	}

	public void setPosition(int nChapter, int nPage)
	{
		Global.addActiveNotify(nChapter, nPage, selfHandler);
		Global.addUnActiveNotify(nChapter, nPage, selfHandler);
	}

	public void setImageSrc(String strImagePath, int nWidth, int nHeight)
	{
		ImageView imageView = new ImageView(getContext());
		imageView.setId(Global.getUserId());
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(imageView);
		imageHandler.addImageView(imageView, strImagePath, nWidth, nHeight);
	}

	public void setBrushes(int nBrushes)
	{
		mnBrushes = nBrushes;
		fingerPaintView.setPenStrokeWidth(nBrushes);
	}

	public void setEraseWidth(int nWidth)
	{
		fingerPaintView.setEraseStrokeWidth(nWidth);
	}

	public void setPalette(int nPalette)
	{
		mnPalette = nPalette;
	}

	private int addButton(float nX, float nY, int nWidth, int nHeight, String strImagePath)
	{
		ImageView imageView = new ImageView(getContext());
		imageView.setId(Global.getUserId());
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setX(nX);
		imageView.setY(nY);
		imageView.setLayoutParams(new LayoutParams(nWidth, nHeight));
		container.addView(imageView);
		imageHandler.addImageView(imageView, strImagePath, nWidth, nHeight);

		imageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				int nId = view.getId();
				if (nId == mnEraserBtn)
				{
					fingerPaintView.setEraser(true);
				}
				else if (nId == mnPaletteBtn)
				{
					drawerLayout.openDrawer(Gravity.END);
				}
				else if (nId == mnPenBtn)
				{
					fingerPaintView.setEraser(false);
					drawerLayout.openDrawer(Gravity.LEFT);
				}
				else if (nId == mnResetBtn)
				{
					fingerPaintView.clear();
				}
				else if (nId == mnSaveBtn)
				{

				}
			}
		});
		return imageView.getId();
	}

	public void setEraserBtn(float nX, float nY, int nWidth, int nHeight, String strImagePath)
	{
		mnEraserBtn = addButton(nX, nY, nWidth, nHeight, strImagePath);
	}

	public void setPaletteBtn(float nX, float nY, int nWidth, int nHeight, String strImagePath)
	{
		mnPaletteBtn = addButton(nX, nY, nWidth, nHeight, strImagePath);
	}

	public void setPenBtn(float nX, float nY, int nWidth, int nHeight, String strImagePath)
	{
		mnPenBtn = addButton(nX, nY, nWidth, nHeight, strImagePath);
	}

	public void setResetBtn(float nX, float nY, int nWidth, int nHeight, String strImagePath)
	{
		mnResetBtn = addButton(nX, nY, nWidth, nHeight, strImagePath);
	}

	public void setSaveBtn(float nX, float nY, int nWidth, int nHeight, String strImagePath)
	{
		mnSaveBtn = addButton(nX, nY, nWidth, nHeight, strImagePath);
	}

	private Handler	selfHandler	= new Handler()
								{
									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_CURRENT_ACTIVE:
											mbCurrentActive = true;
											imageHandler.runInitImageView();
											fingerPaintView.setVisibility(View.VISIBLE);
											break;
										case EventMessage.MSG_NOT_CURRENT_ACTIVE:
											if (mbCurrentActive)
											{
												mbCurrentActive = false;
												imageHandler.releaseBitmap();
												fingerPaintView.setVisibility(View.INVISIBLE);
											}
											break;
										case EventMessage.MSG_VIEW_INITED:
											break;
										}
									}
								};
}

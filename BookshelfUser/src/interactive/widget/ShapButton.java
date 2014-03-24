package interactive.widget;

import interactive.view.global.Global;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class ShapButton extends View implements OnClickListener
{
	private final int	DEFAULT_TEXT_SIZE	= 16;
	private Paint		mPaintText			= null;
	private Paint		mPaintBorder		= null;
	private String		mstrText			= null;
	int					mAscent				= 0;
	private Path		path				= null;
	private boolean		mbClicked			= false;
	private int			mnColorText			= 0;
	private int			mnColorTextClick	= 0;
	private int			mnColorBorder		= 0;
	private int			mnColorBorderClick	= 0;

	public ShapButton(Context context)
	{
		super(context);
		init();
	}

	public ShapButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();

		int[] stringAttrs = new int[] { Global.getResourceId(context, "text", "attr") };
		int[] intAttrs = new int[] { Global.getResourceId(context, "textSize", "attr"),
				Global.getResourceId(context, "borderSize", "attr") };
		int[] colorAttrs = new int[] { Global.getResourceId(context, "textColor", "attr"),
				Global.getResourceId(context, "textColorClick", "attr"),
				Global.getResourceId(context, "borderColor", "attr"),
				Global.getResourceId(context, "borderColorClick", "attr") };

		final TypedArray stringA = context.obtainStyledAttributes(attrs, stringAttrs);
		final TypedArray intA = context.obtainStyledAttributes(attrs, intAttrs);
		final TypedArray colorA = context.obtainStyledAttributes(attrs, colorAttrs);

		CharSequence s = stringA.getString(0);
		if (s != null)
		{
			setText(s.toString());
		}
		else
		{
			setText("Text");
		}

		setTextSize(intA.getDimensionPixelSize(0, DEFAULT_TEXT_SIZE));
		setBorderSize(intA.getDimensionPixelSize(1, 3));

		mnColorText = colorA.getColor(0, Color.BLACK);
		mnColorTextClick = colorA.getColor(1, Color.BLACK);
		mnColorBorder = colorA.getColor(2, Color.BLACK);
		mnColorBorderClick = colorA.getColor(3, Color.BLACK);
		setTextColor(mnColorText);
		setBorderColor(mnColorBorder);

		stringA.recycle();
		intA.recycle();
		colorA.recycle();

	}

	public ShapButton(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init()
	{
		super.setOnClickListener(this);

		mPaintText = new Paint();
		mPaintText.setAntiAlias(true);
		mPaintText.setTextSize(DEFAULT_TEXT_SIZE);

		mPaintBorder = new Paint();
		mPaintBorder.setAntiAlias(true);
		mPaintBorder.setStyle(Paint.Style.STROKE);

		path = new Path();
	}

	public void setText(String strText)
	{
		mstrText = strText;
		this.requestLayout();
		this.invalidate();
	}

	public void setTextSize(int nSize)
	{
		mPaintText.setTextSize(nSize);
		this.requestLayout();
		this.invalidate();
	}

	public void setBorderSize(int nSize)
	{
		mPaintBorder.setStrokeWidth(nSize);
	}

	public void setBorderColor(int nColor)
	{
		mPaintBorder.setColor(nColor);
		this.invalidate();
	}

	public void setTextColor(int nColor)
	{
		mPaintText.setColor(nColor);
		this.invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		this.setMeasuredDimension(measuredWidth, measuredHeight);
	}

	private int measureHeight(int measureSpec)
	{
		/**
		 * ascent：是baseline之上至字符最高處的距離
		 * descent：是baseline之下至字符最低處的距離
		 * leading：是上一行字符的descent到下一行的ascent之間的距離，也就是相鄰行間的空白距離
		 * top：指的是最高字符到baseline的值，即ascent的最大值
		 * bottom：指最低字符到baseline的值，即descent的最大值
		 */
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) mPaintText.ascent();

		if (specMode == MeasureSpec.EXACTLY)
		{
			result = specSize;
		}
		else
		{
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-mAscent + mPaintText.descent()) + getPaddingTop() + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST)
			{
				// Respect AT_MOST value if that was what is called for by measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private int measureWidth(int measureSpec)
	{
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) // match_parent or exactly size
		{
			result = specSize;
		}
		else
		{
			// Measure the text
			result = (int) mPaintText.measureText(mstrText) + getPaddingLeft() + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) // wrap_content
			{
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int nTextWidth = (int) mPaintText.measureText(mstrText) + getPaddingLeft() + getPaddingRight();
		int nCenterX = (this.getWidth() - nTextWidth) / 2;
		int nTextHeight = (int) (mPaintText.descent() + mPaintText.ascent()) + getPaddingTop() + getPaddingBottom();
		int nCenterY = (this.getHeight() - nTextHeight) / 2;

		if (mbClicked)
		{
			mPaintText.setColor(mnColorTextClick);
			mPaintBorder.setColor(mnColorBorderClick);
		}
		else
		{
			mPaintText.setColor(mnColorText);
			mPaintBorder.setColor(mnColorBorder);
		}

		path.moveTo(0, 0);
		path.lineTo(this.getWidth(), 0);
		path.lineTo(this.getWidth(), this.getHeight());
		path.lineTo(0, this.getHeight());
		path.close();

		canvas.drawPath(path, mPaintBorder);
		canvas.drawText(mstrText, nCenterX, nCenterY, mPaintText);
	}

	@Override
	public void onClick(View v)
	{
		mbClicked = mbClicked ? false : true;
		this.invalidate();
	}

	public void setClick(boolean bClick)
	{
		mbClicked = bClick;
		this.invalidate();
	}
}

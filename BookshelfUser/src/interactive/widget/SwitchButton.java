package interactive.widget;

import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.widget.CompoundButton;

public class SwitchButton extends CompoundButton
{
	private final int		SWITCH_BOUND	= Global.ScaleSize(2);
	private Paint			paint			= null;
	private RectF			oval			= null;

	private OnClickListener	onClickListener;

	public SwitchButton(Context context)
	{
		this(context, null);
		init(context);
	}

	public SwitchButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public SwitchButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		paint = new Paint();
		paint.setAntiAlias(true);
		oval = new RectF();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	private int measureHeight(int measureSpec)
	{
		int nResult = 80; // default
		int nSpecMode = MeasureSpec.getMode(measureSpec);
		int nSpecSize = MeasureSpec.getSize(measureSpec);

		if (MeasureSpec.AT_MOST == nSpecMode) // when layout set wrap_content
		{
			nResult = nSpecSize;
		}
		else if (MeasureSpec.EXACTLY == nSpecMode) // when layout set match_parent or exactly size
		{
			nResult = nSpecSize;
		}

		return nResult;
	}

	private int measureWidth(int measureSpec)
	{
		int nResult = 80; // default
		int nSpecMode = MeasureSpec.getMode(measureSpec);
		int nSpecSize = MeasureSpec.getSize(measureSpec);

		if (MeasureSpec.AT_MOST == nSpecMode) // when layout set wrap_content
		{
			nResult = nSpecSize;
		}
		else if (MeasureSpec.EXACTLY == nSpecMode) // when layout set match_parent or exactly size
		{
			nResult = nSpecSize;
		}

		return nResult;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// calculate the left and right values
		float right = this.getWidth() - this.getPaddingRight();
		float left = right - this.getWidth() + this.getPaddingLeft();
		float height = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
		float radius = height / 2; // 圓半徑

		oval.set(left, 0, right, height);
		if (this.isChecked())
		{
			paint.setColor(Color.parseColor("#4B96C2"));
			//paint.setColor(Color.parseColor("#5a7fc0"));
			canvas.drawRoundRect(oval, radius, radius, paint);
			paint.setColor(Color.WHITE);
			canvas.drawCircle(right - radius, radius, radius - SWITCH_BOUND, paint);
		}
		else
		{
			paint.setColor(Color.GRAY);
			canvas.drawRoundRect(oval, radius, radius, paint);
			paint.setColor(Color.WHITE);
			canvas.drawCircle(left + radius, radius, radius - SWITCH_BOUND, paint);
		}

		canvas.save();

	}

	@Override
	public void setOnClickListener(OnClickListener l)
	{
		super.setOnClickListener(l);

		this.onClickListener = l;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			this.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			this.setChecked(!this.isChecked());
			invalidate();

			// call the onClickListener
			if (this.onClickListener != null)
				this.onClickListener.onClick(this);
			return false;
		}

		return super.onTouchEvent(event);
	}

}

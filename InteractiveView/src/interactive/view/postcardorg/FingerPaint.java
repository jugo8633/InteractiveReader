package interactive.view.postcardorg;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class FingerPaint implements ColorPickerDialog.OnColorChangedListener
{
	private Context context;
	private Paint mPaint;
	private MaskFilter  mEmboss;
    private MaskFilter  mBlur;
	private ColorPickerDialog cpd;
	private int mode=0;
	
	public FingerPaint(Context context)
	{
		this.context = context;
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	}
	
	public Paint getPaint()
	{
		return mPaint;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public void setPaintOptions(int id)
	{
		optionItemSelected(id);
	}
	
	public void optionItemSelected(int id)
	{
		mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch(id) 
        {
        	case 0:
        	mode = 0;
            
            break;
            case 1:
            	mode = 1;
                new ColorPickerDialog(context, this, mPaint.getColor()).show();
                break;
            case 2:
            	mode = 2;
                if (mPaint.getMaskFilter() != mEmboss) 
                {
                    mPaint.setMaskFilter(mEmboss);
                } 
                else 
                {
                    mPaint.setMaskFilter(null);
                }
                break;
            case 3:
            	mode = 3;
                if (mPaint.getMaskFilter() != mBlur) 
                {
                    mPaint.setMaskFilter(mBlur);
                } 
                else 
                {
                    mPaint.setMaskFilter(null);
                }
                break;
            case 4:
                //mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            	mode = 4;
                break;
            case 5:
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                mPaint.setAlpha(0x80);
                mode = 5;
                break;
            case 6:
            	mode = 6;
            	mPaint.setAntiAlias(true);
                mPaint.setDither(true);
                mPaint.setColor(0xFFFF0000);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStrokeWidth(12);
                break;
        }
	}
	
	@Override
	public void colorChanged(int color) 
	{
		// TODO Auto-generated method stub
		mPaint.setColor(color);
	}
}

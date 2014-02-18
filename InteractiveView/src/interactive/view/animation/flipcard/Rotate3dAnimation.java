package interactive.view.animation.flipcard;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3dAnimation extends Animation
{
	private final float		mFromDegrees;
	private final float		mToDegrees;
	private final float		mCenterX;
	private final float		mCenterY;
	private final float		mDepthZ;
	private final boolean	mReverse;		// 是否需要扭曲
	private Camera			mCamera;		// Camera類是用來實現繞Y軸旋轉後透視投影

	public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ,
			boolean reverse)
	{
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;
		mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight)
	{
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t)
	{
		final float fromDegrees = mFromDegrees;
		// 生成中間角度
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime); // 旋轉角度（angle）

		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		// 通過t.getMatrix()取得當前的矩陣，然後通過camera.translate來對矩陣進行平移變換
		final Matrix matrix = t.getMatrix();
		camera.save();
		if (mReverse)
		{
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		}
		else
		{
			camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		}
		camera.rotateY(degrees); // camera.rotateY進行旋轉
		camera.getMatrix(matrix); // 取得變換後的矩陣
		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}

}

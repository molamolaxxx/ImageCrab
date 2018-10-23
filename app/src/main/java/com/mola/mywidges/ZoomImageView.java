package com.mola.mywidges;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.mola.imagecrab.ImageActivity;
import com.mola.interfaces.OnScaleHintListener;

/**
 * 可缩放的ImageView
 *
 */
public class ZoomImageView extends ImageView 
                   implements OnGlobalLayoutListener,OnScaleGestureListener,
                     OnTouchListener {

	private boolean mOnce;
	// ��ʼ�����ű���
	private float initScale;
	// ˫���Ŵ�ʱ�ﵽ��ֵ
	private float MidScale;
	// �Ŵ�ļ���
	private float MaxScale;
	//放大速率
	private float SCALE_RATE=0.38f;
	private OnScaleHintListener onScaleHintListener=null;
	// ��ͼƬ��������
	private Matrix Matrix;

	// ���ŵ�����
	private ScaleGestureDetector scaleGestureDetector;

	// / -------------------------------------------------------
	// ��һ�ζ�㴥�ص�����
	private int mLastPCount;
	// ��¼��һ�ε����ĵ������
	private float mLastCenterX;
	private float mLastCenterY;

	private float mTouchSlop;
	// ----------------------------------------------------------------------

	private GestureDetector gestureDetector;
	// �Ƿ���������
	private boolean isAuto = false;
	public void setListener(OnScaleHintListener onScaleHintListener){
		this.onScaleHintListener=onScaleHintListener;
	}
	public ZoomImageView(final Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Matrix = new Matrix();
		// ���Ų���
		scaleGestureDetector = new ScaleGestureDetector(context, this);
		// ���Ʋ���
		gestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						if(getScale()<MidScale)
							onScaleHintListener.onHintChange(ImageActivity.TYPE_BIG);
						else
							onScaleHintListener.onHintChange(ImageActivity.TYPE_SMALL);
						if (!isAuto) {

							float centerX = e.getX();
							float centerY = e.getY();

							float scale = getScale();
							if (scale < MidScale) {
								//以midScale为目标
								postDelayed(new AutoScaleRunnable(MidScale
										, centerX, centerY), 16);
								isAuto = true;
							} else {
								postDelayed(new AutoScaleRunnable(initScale
										, centerX, centerY), 16);
								isAuto = true;
							}
						}
						return super.onDoubleTap(e);
					}
				});

		// ע�ᴥ���¼�
		setOnTouchListener(this);
		// ��ȡϵͳ���ٽ�ֵ
		mTouchSlop = ViewConfiguration.get(context).getTouchSlop();

		// ����ͼƬ������ģʽ
		super.setScaleType(ScaleType.MATRIX);
	}

	/**
	 * ʵ�ֻ���������
	 */
	class AutoScaleRunnable implements Runnable {

		private float targetScale;
		private float centerX;
		private float centerY;

		// ��ʱ�����ű���
		private float tempScale;

		/**
		 * 
		 * @param targetScale Ŀ������ű���
		 * @param centerX ���ŵ�����
		 * @param centerY ���ŵ�����
		 */
		public AutoScaleRunnable(float targetScale, float centerX, float centerY) {
			this.targetScale = targetScale;
			this.centerX = centerX;
			this.centerY = centerY;
			float curScale = getScale();
			
			if (curScale < targetScale) {
				// /�Ŵ�
				tempScale = 1.07f;
			} else {
				// ��С
				tempScale = 0.93f;
			}

		}

		@Override
		public void run() {

			Matrix.postScale(tempScale, tempScale, centerX, centerY);
			checkCenterAndBorderWhenScale();
			setImageMatrix(Matrix);

			float curScale = getScale();
			if ((tempScale < 1.0f && curScale > targetScale)
					|| (tempScale > 1.0f && curScale < targetScale)) {
				// ����ִ���߳�
				postDelayed(this, 16);
			} else {
				float scale = targetScale / curScale;
				Matrix.postScale(scale, scale, centerX, centerY);
				checkCenterAndBorderWhenScale();
				setImageMatrix(Matrix);
				isAuto = false;
			}
		}
	};

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context) {
		this(context, null);
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO �Զ����ɵķ������
		super.onAttachedToWindow();

		getViewTreeObserver().addOnGlobalLayoutListener(this);

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		// TODO �Զ����ɵķ������
		super.onDetachedFromWindow();

		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * ���ؼ���ȫ����֮��Ļص�
	 * 
	 * 1 ���ȼ������ŵı�����ƽ�Ƶľ��� ʵ��ͼƬ�ľ�����ʾ������
	 * 
	 * 
	 */
	@Override
	public void onGlobalLayout() {

		if (!mOnce) {
			// �ؼ��ĳߴ�
			int width = getWidth();
			int height = getHeight();
			// ���ŵı���
			float scale = 1.0f;
			// ��ȡͼƬ
			Drawable drawable = getDrawable();
			if (drawable == null) {
				return;
			}
			// ��ȡͼƬ�Ŀ��
			int dw = drawable.getIntrinsicWidth();
			int dh = drawable.getIntrinsicHeight();

			// ����ͼƬ�����ű���
			if (dw > width && dh < height) {
				scale = width * 1.0f / dw;
			}

			if (dw < width && dh > height) {
				scale = height * 1.0f / dh;
			}

			// �Ŵ�
			if (dw < width && dh < height) {
				scale = Math.min((width * 1.0f / dw), (height * 1.0f / dh));
			}

			// ��С
			if (dw > width && dh > height) {
				scale = Math.min((width * 1.0f / dw), (height * 1.0f / dh));
			}

			// �������ŵı���
			initScale = scale;
			MidScale = scale * 2;
			MaxScale = scale * 4;

			int offX = getWidth() / 2 - dw / 2;
			int offY = getHeight() / 2 - dh / 2;

			/**
			 * ��ƽ�Ƶ��ؼ�������,Ȼ���������
			 */
			Matrix.postTranslate(offX, offY);
			Matrix.postScale(initScale, initScale, getWidth() / 2,
					getHeight() / 2);
			setImageMatrix(Matrix);

			mOnce = true;
		}
	}

	/**
	 * ��ȡͼƬ�����ű���
	 * 
	 * @return
	 */
	public float getScale() {
		float[] values = new float[9];
		Matrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}

	@Override
	public boolean onScale(ScaleGestureDetector arg0) {

		// ���ŵ�����
		float scaleFactor = arg0.getScaleFactor();
		// ��ȡͼƬ�����ű���
		float scale = getScale();
		if(scale<MidScale)
			onScaleHintListener.onHintChange(ImageActivity.TYPE_SMALL);
		else
			onScaleHintListener.onHintChange(ImageActivity.TYPE_BIG);
		System.out.println(scale);
		if (getDrawable() == null) {
			return true;
		}

		if ((scaleFactor > 1.0f && scale < MaxScale)
				|| (scaleFactor < 1.0f && scale > initScale)) {
			if(scaleFactor>1.0f)
				scaleFactor=(scaleFactor-1)*SCALE_RATE+1;
			else
				scaleFactor=1-(1-scaleFactor)*SCALE_RATE;

			if (scale * scaleFactor > MaxScale) {
				scaleFactor = MaxScale / scale;
			}

			if (scale * scaleFactor < initScale) {
				scaleFactor = initScale / scale;
			}

			Matrix.postScale(scaleFactor, scaleFactor, arg0.getFocusX(),
					arg0.getFocusY());

			// �߽�ļ��
			checkCenterAndBorderWhenScale();
			setImageMatrix(Matrix);
		}
		return false;
	}

	/**
	 * ��ȡͼƬ����֮��Ŀ�͸� ���������
	 * 
	 * @return
	 */
	public RectF getMatrixRectF() {
		RectF rectf = new RectF();
		Matrix matrix = Matrix;
		Drawable d = getDrawable();
		if (d != null) {
			rectf.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rectf);
		}
		return rectf;
	}

	/**
	 * �����ŵ�ʱ��,���б߽�Ŀ�����λ�õĿ��� ��Ҫͨ��ƽ�Ʋ������
	 */
	private void checkCenterAndBorderWhenScale() {

		RectF rectf = getMatrixRectF();
		float offX = 0;
		float offY = 0;

		// ��ͼƬ�Ŀ�ȴ��ڿؼ��Ŀ��
		if (rectf.width() >= getWidth()) {

			if (rectf.left > 0) {
				offX = -rectf.left;
			}

			if (rectf.right < getWidth()) {
				offX = getWidth() - rectf.right;
			}
		}

		// ��ͼƬ�ĸ߶ȴ��ڿؼ��ĸ߶�
		if (rectf.height() >= getHeight()) {

			if (rectf.top > 0) {
				offY = -rectf.top;
			}

			if (rectf.bottom < getHeight()) {
				offY = getHeight() - rectf.bottom;
			}
		}

		// ��ͼƬ�Ŀ�С�ڿؼ��Ŀ��
		if (rectf.width() < getWidth()) {
			offX = getWidth() / 2 - rectf.right + rectf.width() / 2;
		}

		// ��ͼƬ�ĸ߶�С�ڿؼ��ĸ߶�
		if (rectf.height() < getHeight()) {
			offY = getHeight() / 2 - rectf.bottom + rectf.height() / 2;
		}
		// ͼƬ��ƽ��
		Matrix.postTranslate(offX, offY);
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector arg0) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector arg0) {

	}

	/**
	 * �������¼��������ŵ����ƽ��д���
	 */
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		if (gestureDetector.onTouchEvent(arg1)) {
			return true;
		}

		scaleGestureDetector.onTouchEvent(arg1);

		/**
		 * ������ָƽ�ƵĴ���
		 */
		// ��ȡ��ָ�Ĵ��ص���Ŀ
		int pCount = arg1.getPointerCount();
		// ���㴥�ص����ĵ������
		float centerX = 0;
		float centerY = 0;

		for (int i = 0; i < pCount; i++) {
			centerX += arg1.getX(i);
			centerY += arg1.getY(i);
		}
		centerX = centerX / pCount;
		centerY = centerY / pCount;

		// ��ָ���������ı�
		if (mLastPCount != pCount) {
			mLastCenterX = centerX;
			mLastCenterY = centerY;
		}
		mLastPCount = pCount;

		RectF rectf1=getMatrixRectF();
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(rectf1.width() > getWidth() || rectf1.height() >getHeight()){
				//���󸸿ؼ���Ҫ�����¼�
				//图片大于控件，不拦截
					this.getParent().requestDisallowInterceptTouchEvent(true);
					System.out.println("请求父容器不拦截");

			}
			else {
				this.getParent().requestDisallowInterceptTouchEvent(false);
				System.out.println("拦截");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mLastPCount = 0;
			break;
		case MotionEvent.ACTION_MOVE:

			if(rectf1.width() > getWidth() || rectf1.height() >getHeight() ){
				//如果scale=1，拦截
				if(getScale()==initScale)
					{
						this.getParent().requestDisallowInterceptTouchEvent(false);
						System.out.println("拦截");
					}
				else {
					this.getParent().requestDisallowInterceptTouchEvent(true);
					System.out.println("请求父容器不拦截,放大为:" + getScale());
				}
			}
			else {
				this.getParent().requestDisallowInterceptTouchEvent(false);
				System.out.println("拦截");
			}
			float dx = centerX - mLastCenterX;
			float dy = centerY - mLastCenterY;

			// ���ƫ�ƴﵽ�ٽ�ֵ
			if (Math.sqrt((dx * dx) + (dy * dy)) > mTouchSlop) {

				// ͼƬ���ƶ�
				RectF rectf = getMatrixRectF();
				Drawable d = getDrawable();
				if (d != null) {
					// ���ͼƬ�Ŀ��С�ڿؼ��Ŀ��,�����ƶ�
					if (rectf.width() < getWidth()) {
						dx = 0;
					}

					if (rectf.height() < getHeight()) {
						dy = 0;
					}

					Matrix.postTranslate(dx, dy);
					checkBorderWhenTranslate();
					setImageMatrix(Matrix);
				}
			}

			mLastCenterX = centerX;
			mLastCenterY = centerY;
			break;
		}
		return true;
	}

	/**
	 * ���ƶ���ʱ��,������ı߽�
	 */
	private void checkBorderWhenTranslate() {

		RectF rectf = getMatrixRectF();
		Drawable d = getDrawable();

		float offX = 0;
		float offY = 0;

		if (d == null) {
			return;
		}

		// ��ͼƬ�Ŀ�ȴ��ڿؼ��Ŀ��
		if (rectf.width() >= getWidth()) {

			if (rectf.left > 0) {
				offX = -rectf.left;
			}

			if (rectf.right < getWidth()) {
				offX = getWidth() - rectf.right;
			}
		}

		// ��ͼƬ�ĸ߶ȴ��ڿؼ��ĸ߶�
		if (rectf.height() >= getHeight()) {

			if (rectf.top > 0) {
				offY = -rectf.top;
			}

			if (rectf.bottom < getHeight()) {
				offY = getHeight() - rectf.bottom;
			}
		}

		// ��ͼƬ�Ŀ�С�ڿؼ��Ŀ��
		if (rectf.width() < getWidth()) {
			offX = getWidth() / 2 - rectf.right + rectf.width() / 2;
		}

		// ��ͼƬ�ĸ߶�С�ڿؼ��ĸ߶�
		if (rectf.height() < getHeight()) {
			offY = getHeight() / 2 - rectf.bottom + rectf.height() / 2;
		}
		// ͼƬ��ƽ��
		Matrix.postTranslate(offX, offY);
	}
}

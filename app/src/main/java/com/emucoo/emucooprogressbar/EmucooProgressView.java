package com.emucoo.emucooprogressbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import org.jetbrains.annotations.NotNull;

/**
 * 亦墨的进度条
 */
public class EmucooProgressView extends View {
    private static final String TAG=EmucooProgressView.class.getSimpleName();

    private int mMaxProgress = 100;
    private int mCurProgress = 50;
    private int mTextSize = 11;

    private TextPaint mTextPaint;
    private Paint mTrianglePaint;
    private int mTextPadding = 4;
    private Paint mFaintPaint, mBgPaint, mProgressBarPaint,mProgressTextPaint;

    private int mLineDiffer = 8; //
    private int mFaintStrokeSize = 20;
    private int mBgStrokeSize = mFaintStrokeSize - mLineDiffer;
    private int mProgressStrokeSize = mBgStrokeSize;

    private Rect mTextRect = new Rect();

    private int mDeltaHeight = 4;
    private int mGap = 1;

    private int mLineCenterY = 0; //最外层的背景

    private int mWidth = 0;
    private int mHeight = 0;

    private int mFaintCapRadius = 0;
    private int mProgressCapRadius = 0;

    private RectF rectF = new RectF();
    private int mProgreStartX;
    private int mBgStopX;
    private int mProgreStopX;

    private Path mTrianglePath = new Path();
    private LinearGradient mGradient;

    private int mStartColor = Color.parseColor("#fff5a94e");
    private int mEndColor = Color.parseColor("#fff54e4e");

    private boolean mNeedTag = false; //上面的文字
    private boolean mNeedProgressText = false; //中间文字
    private boolean mKeepGradient = false;

    private PorterDuffXfermode mXorMode = new PorterDuffXfermode(PorterDuff.Mode.XOR);


    public EmucooProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public EmucooProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EmucooProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }



    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
//        setLayerType(LAYER_TYPE_SOFTWARE,null);

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.EmucooProgressView, defStyle, 0);

        mMaxProgress = a.getInt(
                R.styleable.EmucooProgressView_emu_pv_max, 100);
        mCurProgress = a.getInt(
                R.styleable.EmucooProgressView_emu_pv_progress,
                0);

        mStartColor = a.getColor(R.styleable.EmucooProgressView_emu_pv_start_color,mStartColor);
        mEndColor = a.getColor(R.styleable.EmucooProgressView_emu_pv_end_color,mEndColor);
        mNeedTag = a.getBoolean(R.styleable.EmucooProgressView_emu_pv_need_tag,false);
        mNeedProgressText = a.getBoolean(R.styleable.EmucooProgressView_emu_pv_need_text,false);
        mKeepGradient = a.getBoolean(R.styleable.EmucooProgressView_emu_pv_keep_gradient,false);
        if( !a.getBoolean(R.styleable.EmucooProgressView_emu_pv_need_shadow,true)){
            mLineDiffer = 0;
            mBgStrokeSize = mFaintStrokeSize - mLineDiffer;
            mProgressStrokeSize = mBgStrokeSize;
        }


        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(sp(mTextSize));
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        mFaintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFaintPaint.setStyle(Paint.Style.STROKE);
        mFaintPaint.setStrokeCap(Paint.Cap.ROUND);
        mFaintPaint.setStrokeWidth(dp(mFaintStrokeSize));
        mFaintPaint.setColor(Color.parseColor("#e8e8ea"));

        mFaintCapRadius = dp(mFaintStrokeSize) / 2;

        mTrianglePaint = new Paint();
        mTrianglePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTrianglePaint.setStrokeWidth(0);
        mTrianglePaint.setColor(mEndColor);
        mTrianglePaint.setStyle(Paint.Style.FILL);


        mBgPaint = new Paint(mFaintPaint);
        mBgPaint.setStrokeWidth(dp(mBgStrokeSize));
        mBgPaint.setColor(Color.parseColor("#d2d3d7"));
        mProgressCapRadius = dp(mBgStrokeSize) / 2;


        mProgressBarPaint = new Paint(mBgPaint);

        mProgressBarPaint.setColor(Color.RED);
        mProgressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressTextPaint.setTextSize(sp(mTextSize-2));
        mProgressTextPaint.setColor(Color.BLACK);

//        mProgressTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

//        mProgressTextPaint.setColorFilter(new PorterDuffColorFilter())

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        resolveSize()
        int measuredWidth, measuredHeight;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = width;
        } else {
            measuredWidth = DipUtil.screenWidth();
        }

        String progress = getProgressString();
        mTextPaint.getTextBounds(progress, 0, progress.length(), mTextRect);
        mTextRect.right += dp(mTextPadding) * 2;
        mTextRect.bottom += dp(mTextPadding) * 2;


        if(mNeedTag) {
            mLineCenterY = mTextRect.height() + dp(mDeltaHeight);//+dp(mGap);
            measuredHeight = (mLineCenterY + dp(mFaintStrokeSize));
        }else{
            mLineCenterY = 0;
            measuredHeight = dp(mFaintStrokeSize);
        }

        measuredWidth = resolveSize(measuredWidth, widthMeasureSpec);
        measuredHeight = resolveSize(measuredHeight, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(measuredWidth);
        mHeight = MeasureSpec.getSize(measuredHeight);


        if (mGradient == null) {
            if(mKeepGradient){
                mProgreStopX = calculateShaderWidth(mMaxProgress);
            }else {
                calculateProgress();
            }
            mGradient = new LinearGradient(mProgreStartX, mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2, mProgreStopX, mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2, mStartColor, mEndColor, Shader.TileMode.CLAMP);
            System.out.println( "onMeasure: startX:" +mProgreStartX +"   endX:"+mProgreStopX+ " startColor:"+mStartColor+"  endColor:"+mEndColor);
//            new LinearGradient(mProgreStartX, mLineCenterY + mProgressCapRadius +  dp(mLineDiffer)/2, mWidth, mLineCenterY + mProgressCapRadius +  dp(mLineDiffer)/2, mStartColor, mEndColor, Shader.TileMode.CLAMP);
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    public void setMax(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.parseColor("#88cdcdcd"));

        calculateProgress();

        canvas.drawLine(mFaintCapRadius, mLineCenterY + mFaintCapRadius, mWidth - mFaintCapRadius, mLineCenterY + mFaintCapRadius, mFaintPaint);

        canvas.drawLine(mProgreStartX, mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2, mBgStopX, mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2, mBgPaint);


        int saved = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);



        mProgressBarPaint.setShader(mGradient);
        canvas.drawLine(mProgreStartX, mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2, mProgreStopX, mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2, mProgressBarPaint);

        //画进度文字
        if(mNeedProgressText) {
            String progressString = getProgressString();
            mTextRect.setEmpty();
            mTextPaint.getTextBounds(progressString, 0, progressString.length(), mTextRect);
            int x = (mWidth - mTextRect.width()) / 2;
            int y = mLineCenterY + (dp(mFaintStrokeSize) - (mTextRect.bottom - mTextRect.top)) / 2 - mTextRect.top;
            mProgressTextPaint.setXfermode(mXorMode);
            canvas.drawText(progressString, x, y, mProgressTextPaint);
            mProgressTextPaint.setXfermode(null);
            canvas.restoreToCount(saved);
        }

        if(mNeedTag) {
            //最下面的点
            int pointLowestX = mProgreStopX;
            int pointLowestY = mLineCenterY;

            int mRealDeltaHeight = dp(mDeltaHeight);
            int leftX = (int) (pointLowestX - mRealDeltaHeight * Math.sqrt(3) / 3);
            int leftY = pointLowestY - mRealDeltaHeight;

            int rightX = (int) (pointLowestX + mRealDeltaHeight * Math.sqrt(3) / 3);
            int rightY = pointLowestY - mRealDeltaHeight;

            mTrianglePath.reset();
            mTrianglePath.moveTo(pointLowestX, pointLowestY);
            mTrianglePath.lineTo(leftX, leftY);
            mTrianglePath.lineTo(rightX, rightY);
            mTrianglePath.close();
            canvas.drawPath(mTrianglePath, mTrianglePaint);

            String progress = getProgressString();
            mTextRect.setEmpty();
            mTextPaint.getTextBounds(progress, 0, progress.length(), mTextRect);
            mTextRect.right += dp(mTextPadding) * 2;
            mTextRect.bottom += dp(mTextPadding) * 2;

            System.out.println("text rect:" + mTextRect);
            System.out.println("on draw triangle height:" + dp(mDeltaHeight));
            System.out.println("on draw -> text rect height:" + mTextRect.height());

            rectF.set(mTextRect);
            rectF.offset(mProgreStopX - mTextRect.width() / 2, -mTextRect.top);

            if (rectF.right > mWidth) {
                //超过边界
                float offset = mWidth - rectF.right;
                rectF.offset(offset, 0);
                mTextRect.offset((int) (offset), 0);
            }

            canvas.drawRoundRect(rectF, 5f, 5f, mTrianglePaint);
            mTextRect.offset(mProgreStopX - mTextRect.width() / 2 + dp(mTextPadding), 0);
            canvas.drawText(progress, mTextRect.left, (rectF.height() - (mTextRect.bottom - mTextRect.top)) / 2 - mTextRect.top + dp(mTextPadding), mTextPaint);
        }
    }

    private void calculateProgress() {
        mProgreStartX = mProgressCapRadius + dp(mLineDiffer) / 2;
        mBgStopX = mWidth - mProgressCapRadius - dp(mLineDiffer) / 2;

        int width = mBgStopX - mProgreStartX;
        mProgreStopX = (int) (mProgreStartX + width * (float) mCurProgress / mMaxProgress);
    }

    private int calculateShaderWidth(int targetProgress) {
        mProgreStartX = mProgressCapRadius + dp(mLineDiffer) / 2;
        mBgStopX = mWidth - mProgressCapRadius - dp(mLineDiffer) / 2;

        int width = mBgStopX - mProgreStartX;
        return (int) (mProgreStartX + width * (float) targetProgress / mMaxProgress);
    }

    @NotNull
    private String getProgressString() {
        return (int) (mCurProgress / (float) mMaxProgress * 100) + "%";
    }

    public void setProgress(final int targetProgress, boolean needAnim) {
        if (needAnim) {
            if (isShown()) {
                System.out.println("is shown");
                mGradient = new LinearGradient(
                        mProgreStartX,
                        mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2,
                        calculateShaderWidth(targetProgress),
                        mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2,
                        mStartColor, mEndColor, Shader.TileMode.CLAMP);
                ObjectAnimator.ofInt(EmucooProgressView.this, "progressInternal", mCurProgress, targetProgress).setDuration(250).start();
            } else {
                post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("is not show");
                        mGradient = new LinearGradient(
                                mProgreStartX,
                                mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2,
                                calculateShaderWidth(targetProgress),
                                mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2,
                                mStartColor, mEndColor, Shader.TileMode.CLAMP);
                        ObjectAnimator.ofInt(EmucooProgressView.this, "progressInternal", mCurProgress, targetProgress).setDuration(250).start();
                    }
                });
            }

        } else {
            if (isShown()) {
                /*
                mGradient = new LinearGradient(
                        mProgreStartX,
                        mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2,
                        calculateShaderWidth(targetProgress),
                        mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2,
                        mStartColor, mEndColor, Shader.TileMode.CLAMP);
                        */
                setProgressInternal(targetProgress);
            } else {
                post(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        mGradient = new LinearGradient(
                                mProgreStartX,
                                mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2,
                                calculateShaderWidth(targetProgress),
                                mLineCenterY + mProgressCapRadius + dp(mLineDiffer) / 2,
                                mStartColor, mEndColor, Shader.TileMode.CLAMP);
                                */
                        setProgressInternal(targetProgress);

                    }
                });
            }
        }

    }

    private void setProgressInternal(int progress) {
        this.mCurProgress = progress;
        invalidate();
    }

    private void startAnimInternal() {


    }

    private int dp(int i) {
        return DipUtil.dip2px(i);
    }

    private int sp(int i) {
        return DipUtil.convertSpToPixels(i);
    }

}

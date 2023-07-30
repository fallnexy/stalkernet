package com.example.stalkernet.map;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class VitalItem {

    private ImageView mImage;
    private ProgressBar mProgress;
    private TextView mText;

    protected final int mDisplayWidth;
    protected final int mDisplayHeight;

    public VitalItem(View inflate, int width, int height){
        this.inflate = inflate;
        mDisplayWidth = width;
        mDisplayHeight = height;
    }
    /*
    * устанавливает картинку, прогресс бар и текст
    * */
    public void setItem(int imageView, int progressBar, int textView){
        mImage = inflate.findViewById(imageView);
        mProgress = inflate.findViewById(progressBar);
        mText = inflate.findViewById(textView);

        int factor = mDisplayHeight / 40;
        int progressWidthFactor = mDisplayWidth / 5;
        float progressHeightFactor = factor / 10.9f;
        float textFactor = factor / 4f;
        //Log.d(LOG_CHE, "factor = " + factor);
        mImage.getLayoutParams().width = factor;
        mImage.getLayoutParams().height = factor;
        mProgress.setScaleY(progressHeightFactor);
        mProgress.getLayoutParams().width = progressWidthFactor;
        mText.setTextSize(textFactor);
    }

    public ImageView getItemImage() {
        return mImage;
    }

    public void setItemImage(Drawable drawable) {
        mImage.setImageDrawable(drawable);
    }

    public ProgressBar getProgressBar() {
        return mProgress;
    }

    public void setItemProgress(ProgressBar mItemProgress) {
        this.mProgress = mItemProgress;
    }

    public TextView getTextView() {
        return mText;
    }

    public void setText(String text) {
        mText.setText(text);
    }

    private View inflate;


}

package com.example.stalkernet.map;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RBPItem extends VitalItem{

    private final View inflate;
    private ProgressBar questBar, artBar, suitBar;
    private ImageView mImage;


    private TextView text;

    public RBPItem(View inflate, int width, int height) {
        super(inflate,width, height);
        this.inflate = inflate;

        //this.layoutId = layout;
        /*ConstraintLayout placeHolder = inflate.findViewById(layout);
        inflater.inflate(R.layout.item_vital_status, placeHolder);*/
    }

    public void setProtectionBars(int quest, int art, int suit){
        questBar = inflate.findViewById(quest);
        questBar.setScaleY(1f);
        questBar.getLayoutParams().width = mDisplayWidth / 25;
        artBar = inflate.findViewById(art);
        artBar.setScaleY(1f);
        artBar.getLayoutParams().width = mDisplayWidth / 25;
        suitBar = inflate.findViewById(suit);
        suitBar.setScaleY(1f);
        suitBar.getLayoutParams().width = mDisplayWidth / 25;
    }

    public ProgressBar getProtectionBar(int type){
        switch (type){
            case 0:
                return questBar;
            case 1:
                return artBar;
            case 2:
                return suitBar;
        }
        return null;
    }

    public void setImageId(int imageId) {
        //this.imageId = imageId;
    }

    /*public int getImageId() {
        //return imageId;
    }*/

    /*public void setItem(int imageId) {
        this.imageId = imageId;
        //super.setItem(R.id.ivContamination, R.id.pbContamination, R.id.tvContamination);
        mProgressQuest = inflate.findViewById(R.id.pbContaminationQuest);
        mProgressQuest.setScaleY(1f);
        mProgressQuest.getLayoutParams().width = mDisplayWidth / 25;
    }

    public int getImageId() {
        return imageId;
    }

    public ProgressBar getProgressQuest() {
        return mProgressQuest;
    }

    public void setProgressQuest(ProgressBar mProgressQuest) {
        this.mProgressQuest = mProgressQuest;
    }*/
}

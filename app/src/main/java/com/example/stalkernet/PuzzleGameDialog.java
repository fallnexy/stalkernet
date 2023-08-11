package com.example.stalkernet;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static com.example.stalkernet.StatsService.INTENT_SERVICE;

public class PuzzleGameDialog extends DialogFragment {

    public static final String MILESTONE_ID = "milestoneId";
    private static final String MILESTONE_NAME = "milestoneName";
    private static final String MILESTONE_DESCRIPTION = "milestoneDescription";
    private static final String MILESTONE_ENABLE = "milestoneDescriptionEnable";
    private int mFirstImage = 0;
    private int mSecondImage = 0;
    private Drawable firstIcon;
    private int mMilestoneId, enable;
    private String name, description;
    MaterialButton[] imageBtn = new MaterialButton[6];
    TextView tvName, tvDescription;

    private Context mContext;

    public static PuzzleGameDialog newInstance(int milestoneId, String name, String description, int enable){
        PuzzleGameDialog puzzleGameDialog = new PuzzleGameDialog();
        Bundle args = new Bundle();
        args.putInt(MILESTONE_ID, milestoneId);
        args.putString(MILESTONE_NAME, name);
        args.putString(MILESTONE_DESCRIPTION, description);
        args.putInt(MILESTONE_ENABLE, enable);
        puzzleGameDialog.setArguments(args);
        return puzzleGameDialog;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMilestoneId = getArguments().getInt(MILESTONE_ID);
        name = getArguments().getString(MILESTONE_NAME);
        description = getArguments().getString(MILESTONE_DESCRIPTION);
        enable = getArguments().getInt(MILESTONE_ENABLE);


    }
    @Nullable
    @Override
    public  View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.dialog_puzzle_game, container, false);
        View decorView = getDialog().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        GridLayout layout = inflate.findViewById(R.id.gridLayoutPuzzle);
        layout.setVisibility(enable);
        tvName = inflate.findViewById(R.id.dialogTitlePuzzle);
        tvName.setText(name);
        tvDescription = inflate.findViewById(R.id.tvDescriptionPuzzle);
        tvDescription.setText(description);
        setImages(inflate);
        setBtnFinish(inflate);
        return inflate;
    }

    public void setBtnFinish(View inflate){
        MaterialButton finish = inflate.findViewById(R.id.btnMilestoneFinish);
        finish.setOnClickListener(view -> {
            checkMilestone();
            dismiss();
        });
    }
    int[] ivPuzzles = {R.id.ivPuzzle1, R.id.ivPuzzle2, R.id.ivPuzzle3, R.id.ivPuzzle4, R.id.ivPuzzle5, R.id.ivPuzzle6};
    int[][] pictures = {
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2},
            {R.drawable.milestone_1_5, R.drawable.milestone_1_1, R.drawable.milestone_1_4, R.drawable.milestone_1_6, R.drawable.milestone_1_3, R.drawable.milestone_1_2}
    };
    int[] alfa_1 = {251, 255, 252, 250, 253, 254};
    int[] alfa = {255, 254, 253, 252, 251, 250};
    public void setImages(View inflate){
        for (int i = 0; i < imageBtn.length; i++){
            imageBtn[i] = inflate.findViewById(ivPuzzles[i]);
            imageBtn[i].setBackground(getResources().getDrawable(pictures[mMilestoneId - 1][i]));
            imageBtn[i].getBackground().setAlpha(alfa_1[i]);
            imageBtn[i].setOnClickListener(view -> {
                changeImage(inflate, view.getId(), view.getBackground());
            });
        }
    }

    private void changeImage(View inflate, int id, Drawable icon){
        if (mFirstImage==0){
            mFirstImage = id;
            firstIcon = icon;
        } else if (mSecondImage==0) {
            mSecondImage = id;
            inflate.findViewById(mFirstImage).setBackground(icon);
            inflate.findViewById(mSecondImage).setBackground(firstIcon);
            mFirstImage = 0;
            mSecondImage = 0;
        }
    }

    private void checkMilestone(){
        int j = 0;
        for (int i = 0; i < 6; i++){
            if (imageBtn[i].getBackground().getAlpha() == alfa[i]){
                j++;
            } else {
                Toast.makeText(mContext, "Ошибка при получении вехи", Toast.LENGTH_LONG).show();
                break;
            }
        }
        if (j == 6){
            Intent intent = new Intent(INTENT_SERVICE);
            intent.putExtra(MILESTONE_ID, mMilestoneId);
            mContext.sendBroadcast(intent);
            Toast.makeText(mContext, "Веха получена", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

}

package com.example.stalkernet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static com.example.stalkernet.StatsService.INTENT_SERVICE;
import static com.example.stalkernet.StatsService.INTENT_SERVICE_MINE;
import static com.example.stalkernet.anomaly.MineAnomaly.MINE_COUNT_DOWN;

public class MineDialogFragment extends DialogFragment {
    private Button mRightAnswerButton;
    private Button mWrongAnswerButton;
    private Button mWrongSecondAnswerButton;
    private Button mWrongThirdAnswerButton;
    private TextView mExampleTextView;
    private TextView mTimerTextView;
    private Context mContext;

    private int mFirstNumber;
    private int mSecondNumber;
    private int mRightAnswer;
    private int mWrongAnswer;
    private int mWrongSecondAnswer;
    private int mWrongThirdAnswer;
    private int mChooseButton;

    private boolean mProtection;

    private CountDownTimer mCountdownTimer;

    public static MineDialogFragment newInstance(boolean protection) {
        MineDialogFragment dialogFragment = new MineDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("protection", protection);
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Random random = new Random();
        mProtection = getArguments().getBoolean("protection");
        mFirstNumber = random.nextInt(9) + 1;
        mSecondNumber = random.nextInt(9) + 1;
        mRightAnswer = mFirstNumber + mSecondNumber;
        mChooseButton = random.nextInt(4);
        mWrongAnswer = mRightAnswer + 1 ;
        mWrongSecondAnswer = mRightAnswer - 1;
        mWrongThirdAnswer = mChooseButton > 1 ? mRightAnswer + 2 : mRightAnswer - 2;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_mine, container, false);
        View decorView = getDialog().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        int[] buttonIds = {
                R.id.btn_right_answer,
                R.id.btn_left_answer,
                R.id.btn_right_down_answer,
                R.id.btn_left_down_answer
        };

        mRightAnswerButton = view.findViewById(buttonIds[mChooseButton]);
        mWrongAnswerButton = view.findViewById(buttonIds[(mChooseButton + 1) % 4]);
        mWrongSecondAnswerButton = view.findViewById(buttonIds[(mChooseButton + 2) % 4]);
        mWrongThirdAnswerButton = view.findViewById(buttonIds[(mChooseButton + 3) % 4]);

        mExampleTextView = view.findViewById(R.id.tv_example);
        mTimerTextView = view.findViewById(R.id.tv_timer);

        mRightAnswerButton.setText(String.valueOf(mRightAnswer));
        mWrongAnswerButton.setText(String.valueOf(mWrongAnswer));
        mWrongSecondAnswerButton.setText(String.valueOf(mWrongSecondAnswer));
        mWrongThirdAnswerButton.setText(String.valueOf(mWrongThirdAnswer));

        View.OnClickListener clickListener = v -> {
            mCountdownTimer.cancel();
            Intent intent = new Intent(INTENT_SERVICE);
            intent.putExtra(INTENT_SERVICE_MINE, v.getTag().toString());
            mContext.sendBroadcast(intent);
            dismissAllowingStateLoss();
        };

        mRightAnswerButton.setOnClickListener(clickListener);
        mRightAnswerButton.setTag("true");

        mWrongAnswerButton.setOnClickListener(clickListener);
        mWrongAnswerButton.setTag("false");

        mWrongSecondAnswerButton.setOnClickListener(clickListener);
        mWrongSecondAnswerButton.setTag("false");

        mWrongThirdAnswerButton.setOnClickListener(clickListener);
        mWrongThirdAnswerButton.setTag("false");

        mExampleTextView.setText(getString(R.string.example_format, mFirstNumber, mSecondNumber));

        // Start the countdown timer for 3 seconds
        mCountdownTimer = new CountDownTimer(MINE_COUNT_DOWN, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    mTimerTextView.setText(getString(R.string.timer_format, millisUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (mProtection) {
                    mRightAnswerButton.performClick();
                } else {
                    mWrongAnswerButton.performClick();
                }
            }
        }.start();

        return view;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mCountdownTimer.cancel();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}

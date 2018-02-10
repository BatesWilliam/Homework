package edu.wtamu.wb1009200.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATER = "cheater";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String KEY_CHEAT_TOKENS = "cheat_tokens";

    private int mScores = 0;
    private int mCount = 0;
    private int mCheatTokens = 3;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    private TextView mAPIVersionTextView;
    private Button mCheatButton;

    private Question[] mQuestionBank = new Question[]
    {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private int[] mAnswered = {0,0,0,0,0,0};
    private boolean[] mCheated = {false, false, false, false, false, false};
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Log.d(TAG, "onCreate(Bundle) called");

        if(savedInstanceState != null)
        {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER,false);
            mCheatTokens = savedInstanceState.getInt(KEY_CHEAT_TOKENS, 3);
        }

        /* API Version */
        mAPIVersionTextView = findViewById(R.id.api_version_Text_view);
        mAPIVersionTextView.setText("Current API Level is " + Build.VERSION.SDK_INT);

        /* Question */
        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        /* True Button */
        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mFalseButton.setEnabled(false);
                mTrueButton.setEnabled(false);
                checkAnswer(true);
                mAnswered[mCurrentIndex] = 1;
            }
        });

        /* False Button */
        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mFalseButton.setEnabled(false);
                mTrueButton.setEnabled(false);
                checkAnswer(false);
                mAnswered[mCurrentIndex] = 1;
            }
        });


        /* Next Button */
        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;

                if (mAnswered[mCurrentIndex] == 0)
                {
                    mTrueButton.setEnabled(true);
                    mFalseButton.setEnabled(true);
                    updateQuestion();
                }
                else
                {
                    for(int j = 0; j < mQuestionBank.length; j++)
                    {
                        mCount += mAnswered[j];
                    }
                    if(mCount == mQuestionBank.length)
                    {
                        Toast.makeText(getApplicationContext(),"Your score percentage is "
                                +String.valueOf(Math.round(mScores/(float)mQuestionBank.length*100))
                                +"%",Toast.LENGTH_SHORT).show();
                    }
                    mCount = 0;
                    updateQuestion();
                }
            }
        });


        /* Previous Button */
        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mCurrentIndex == 0)
                {
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                else
                {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                }
                updateQuestion();
            }
        });

        /* Cheater Button */
        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mCheatTokens == 0)
                {
                    mCheatButton.setEnabled(false);
                }

                /* Begin Cheat Activity */
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT)
        {
            if (data == null)
            {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_CHEATER, mIsCheater);
        savedInstanceState.putInt(KEY_CHEAT_TOKENS, mCheatTokens);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    /* Question Update */
    private void updateQuestion()
    {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    /* User Input */
    private void checkAnswer(boolean userPressedTrue)
    {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId;

        if(mIsCheater)
        {
            messageResId = R.string.judgment_toast;
            mCheated[mCurrentIndex] = true;
            mCheatTokens--;
        }
        else
        {
            if (userPressedTrue == answerIsTrue)
            {
                mQuestionBank[mCurrentIndex].mAnsweredCorrectly = true;
                messageResId = R.string.correct_toast;
                mScores++;
            }
            else
            {
                mQuestionBank[mCurrentIndex].mAnsweredCorrectly = false;
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}

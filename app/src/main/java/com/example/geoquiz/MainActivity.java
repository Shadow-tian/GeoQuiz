package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mBackButton;
    private Button mRefershButton;
    private Button mCommitButton;
    private TextView mTextView;
    private TextView mAnsweredText;
    private int mCurrentIndex;
    private static final int ANSWERED = 0XFE;
    private static final int CORRECT = 0X01;
    private static final int NO_ANSWERED = 0X00;
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERED = "index_answered";
    private Toast mToast = null;

    private Question[] mQuestionBank = new Question[]
            {
                new Question(R.string.question_1, false),
                new Question(R.string.question_2, true),
                new Question(R.string.question_3, true),
                new Question(R.string.question_4, false),
                new Question(R.string.question_5, true),
                new Question(R.string.question_6, true)
            };
    private int[] mAnswered = new int[mQuestionBank.length];

    private void showTextToast(String msg) {
        //判断队列中是否包含已经显示的Toast
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)  //该方法通常在onStop()方法之前由系统调用，除非用户按后退键
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        savedInstanceState.putIntArray(KEY_ANSWERED,mAnswered);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
        {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mAnswered = savedInstanceState.getIntArray(KEY_ANSWERED);
        }

        mTrueButton = (Button)findViewById(R.id.true_button);
        mFalseButton = (Button)findViewById(R.id.false_button);
        mNextButton = (Button)findViewById(R.id.next_button);
        mRefershButton = (Button)findViewById(R.id.refersh_button);
        mCommitButton = (Button)findViewById(R.id.commit_button);

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int forward = mConfiguration.orientation; //获取屏幕方向
        if(forward == mConfiguration.ORIENTATION_LANDSCAPE)     //如果是横屏
        {
            ;
        }
        else if(forward == mConfiguration.ORIENTATION_PORTRAIT)     //如果是竖屏
        {
            mBackButton = (Button)findViewById(R.id.back_button);
        }
        mTextView = (TextView)findViewById(R.id.question_text_view);
        mAnsweredText = (TextView)findViewById(R.id.answered_text);


        mTrueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*
                Toast.makeText(MainActivity. this,R.string.correct_toast, Toast.LENGTH_SHORT).show();
                */
                checkAnswer(true);
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*
                Toast mToast = Toast.makeText(MainActivity.this, R.string.incorrect_toast, Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
                */
                checkAnswer(false);
            }
        });
        mRefershButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /**将已经答过的问题标记为未答*/
                mAnswered[mCurrentIndex] = NO_ANSWERED;
                anwseredUpdate();
                //Toast.makeText(MainActivity.this, "请重新回答此问题!", Toast.LENGTH_SHORT).show();
                showTextToast("请重新回答此问题!");
            }
        });
        mCommitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                /**点击交卷*/
                int correctNum = 0;
                boolean finishFlag;
                finishFlag = true;
                for(int i=0; i<mQuestionBank.length; i++)
                {
                    if(mAnswered[i] == NO_ANSWERED)     //如果有没答的题直接提示不能交卷，继续答题
                    {
                        finishFlag = false;
                        break;
                    }
                    else
                    {
                        if((mAnswered[i] & CORRECT) != 0)         //如果都回答了，则记录答对几道题
                        {
                            correctNum++;
                        }

                    }
                }
                if(finishFlag == true)
                {
                    int persent = correctNum*100/mQuestionBank.length;
                    String resultAnswer = "共"+ mQuestionBank.length + "道题，您答对" + correctNum + "道 正确率为" +persent+ "%";
                    showTextToast(resultAnswer);
                }
                else
                {
                    showTextToast("您还有没回答的问题，请继续答题");
                }

            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCurrentIndex++;
                if(mCurrentIndex == mQuestionBank.length)
                {
                    mCurrentIndex = 0;
                }
                upDateQuestion();
            }
        });
        if(forward == mConfiguration.ORIENTATION_PORTRAIT)     //如果是竖屏
        {
            mBackButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mCurrentIndex--;
                    if (mCurrentIndex < 0)
                    {
                        mCurrentIndex = mQuestionBank.length - 1;
                    }
                    upDateQuestion();
                }
            });
        }
        mTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCurrentIndex++;
                if(mCurrentIndex == mQuestionBank.length)
                {
                    mCurrentIndex = 0;
                }
                upDateQuestion();
            }
        });
        upDateQuestion();
    }

    private void anwseredUpdate()
    {
        if((mAnswered[mCurrentIndex] & ANSWERED) != 0)   //如果此问题已经答过，显示已答标识
        {
            mAnsweredText.setText("已答");
        }
        else
        {
            mAnsweredText.setText("未答");
        }
    }
    private void upDateQuestion()
    {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mTextView.setText(question);
        anwseredUpdate();
    }
    private void checkAnswer(boolean t_or_f)
    {
        if((mAnswered[mCurrentIndex] & ANSWERED) != 0)   //如果此问题已经答过
        {
            //Toast.makeText(MainActivity.this, "此题您已经答过了!", Toast.LENGTH_SHORT).show();
            showTextToast("此题您已经答过了!");
        }
        else
        {
            if(t_or_f == mQuestionBank[mCurrentIndex].isAnswerTrue())
            {
                //Toast.makeText(MainActivity.this, "回答正确！", Toast.LENGTH_SHORT).show();
                mAnswered[mCurrentIndex] |= CORRECT;    //标记为正确
                showTextToast("回答正确！");
            }
            else
            {
                /*
                Toast mToast = Toast.makeText(MainActivity.this, "回答错误!!!", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
                */
                mAnswered[mCurrentIndex] &= ~(1<<0);    //标记为错误
                showTextToast("回答错误!!!");
            }
        }

        /**记录已经答过的问题*/
        mAnswered[mCurrentIndex] |= ANSWERED;
        anwseredUpdate();   //显示已答状态
    }
}

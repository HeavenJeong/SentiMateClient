package com.example.haewonjeong.sentimateclient2;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by HaeWon Jeong on 4/11/2016.
 */
public class Senti extends RelativeLayout
{
    public Senti(Context context, String[] info)
    {
        super(context);

        TextView nickname = new TextView(context);
        nickname.setText(info[0]);
        LayoutParams nameParams = new LayoutParams(300, LayoutParams.WRAP_CONTENT);
        nickname.setLayoutParams(nameParams);

        addView(nickname);
    }
}


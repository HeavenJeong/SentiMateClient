package com.example.haewonjeong.sentimateclient2;

import android.util.Log;

import java.util.StringTokenizer;

/**
 * Created by HaeWon Jeong on 5/26/2016.
 */
public class UserInfo {
    private String nickname = null;
    private String id = null;
    private String photoURL =null;
    private int birthyear = -1;
    private int sex = 0;
    private String job = null;
    private int bloodtype = 0;
    private int religion = 0;

    private String[] sexCate = {"남자", "여자"};
    private String[] bloodtypeCate = {"A", "B", "O", "AB"};
    private String[] religionCate = {"천주교", "기독교", "불교", "기타"};
    private String[] emotionCate = {"센치해요", "기분이 좋아요", "슬퍼요"};


    private int emotion = 0;
    private String content = "내용이 없습니다.";
    private boolean activate = false;

    public UserInfo(){}
    public UserInfo(String msg) {
        //{9|id|정보}
        String temp="";
        Log.i("UserInfostring",msg);

        StringTokenizer st_del = new StringTokenizer(msg, NetManager.DELIMITER);

        //while (st_del.hasMoreTokens()) {
            temp = st_del.nextToken(); //9

            // 9|닉넴|id|사진|나이|성|직업|혈액형|종교
                Log.i("UserInfostring",temp);
                this.nickname = st_del.nextToken();
                this.id = st_del.nextToken();
                this.photoURL = st_del.nextToken();//0
                this.birthyear = Integer.parseInt(st_del.nextToken());//0
                this.sex = Integer.parseInt(st_del.nextToken());
                this.job = st_del.nextToken();
                this.bloodtype = Integer.parseInt(st_del.nextToken());
                this.religion = Integer.parseInt(st_del.nextToken());

       // }



}
    public int getAge()
    {
        return this.birthyear;
    }
    public String getNickname()
    {
        return this.nickname;
    }
    public String getId(){return this.id;}
    public int getBirthyear()
    {
        return this.birthyear;
    }
    public int getEmotion()
    {
        return this.emotion;
    }
    public String getContent()
    {
        return this.content;
    }
    public Boolean getActivate(){return this.activate;}
    public String getSexString(){return sexCate[sex];}
    public String getBloodString(){return bloodtypeCate[bloodtype];}
    public String religionString(){return religionCate[religion];}
    public String getEotionString(){return emotionCate[emotion];}

    public void setEmotion(int emotion)
    {
        this.emotion = emotion;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setActivate(boolean activate)
    {
        this.activate = activate;
    }


}

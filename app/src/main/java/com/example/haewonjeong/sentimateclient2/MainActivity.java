package com.example.haewonjeong.sentimateclient2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends Activity
{
    public static boolean lastitemVisibleFlag = false;

    private ImageButton setBtn;//상단 설정 버튼
    private ImageButton friendlistBtn; //상단 친구목록 버튼
    private ImageButton refreshBtn; //친구 목록 갱신 버튼
    private ListView lv;//리스트 뷰
    private Context context;
    private ArrayList prgmName;
    public static int[] listImage = new int[100];
    public static String[] listName = new String[100];
    public static int[] listdAge = new int[100];
    public static String[] listEmotion = new String[100];
    public static String[] listContent = new String[100];
    public static String[] userid= new String[100];
       public static ArrayList<UserInfo> userList = new ArrayList<>(); //유저 배열

    int distance;

    private RequestThread requestThread = new RequestThread();
    private boolean onRequest = false;

    private String yourId;

    //테스트용
    int cnt = 0; //인원수5
    int gotItemCnt = 0; //리스트가 추가된 횟수

    String[] answer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String msg;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        distance = getIntent().getIntExtra("distance", 500);

        setBtn = (ImageButton) findViewById(R.id.settingsBtn);
        friendlistBtn = (ImageButton) findViewById(R.id.friendListBtn);
        refreshBtn = (ImageButton)findViewById(R.id.refreshBtn);

        setBtn.setOnClickListener(new View.OnClickListener() { //설정버튼
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);

            }
        });
        friendlistBtn.setOnClickListener(new View.OnClickListener() { //상단 친구 목록 버튼
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RelationshipActivity.class);
                startActivity(i);
            }
        });

        onRequest = true;
        requestThread.start();

        //리스트 뷰
///        msg = NetManager.REQ_LIST + NetManager.DELIMITER +location.getLatitude()+NetManager.DELIMITER+location.getLatitude() ; //리스트 요청 프로토콜 서버에 보냄 2|좌표
        msg = NetManager.REQ_LIST + NetManager.DELIMITER + NetManager.lat + NetManager.DELIMITER + NetManager.lng + NetManager.DELIMITER + distance; //리스트 요청 프로토콜 서버에 보냄 2|좌표

        answer = NetManager.sendandrecvMsg(msg); //리스트를 받음 {2|인원수|id1|id2}...


        String packet = answer[0];
        String temp; //토크나이징용 String
        String tempFor9 = "";
        //2|인원수|id1|id2..
        StringTokenizer st_del = new StringTokenizer(packet, NetManager.DELIMITER);

        Log.i("packet", packet);

        if ((NetManager.SEND_LIST == Integer.parseInt(st_del.nextToken()))) { //2
            cnt = Integer.parseInt(st_del.nextToken()); //인원수
            for (int i = 0; i < cnt; i++) {
                userid[i] = st_del.nextToken();
            }
        }


        if (cnt == 0) {
        }//0명이면 정보를 요청하지 않는다.
        else
        {

           // for(int i=0; i<cnt; i++){ //id를 최대 10개까지 요청

                for(int j=0; j<10; j++)
                {
                    if(j >= cnt) { //0,1 >=1
                        Log.i("MainActivity","End of list");break;} //
                    else{
                    tempFor9 += NetManager.DELIMITER + userid[j];
                        Log.i("MainActivity",tempFor9);
                    }
                }
                msg = NetManager.REQ_INFORMATION + tempFor9;//정보요청 {9|id1|id2...}
                // 정보수신 answer[0] = 9|id1|정보 answer[1]=9|id2|정보
                answer = NetManager.sendandrecvMsg(msg);
                for (int k = 0; k < 10; k++) {
                    if(k >= cnt)
                    {break;}
                    else {
                        UserInfo userinfo = new UserInfo(answer[k]);
                        userList.add(userinfo);
                    }
                }
            //}

        }

        //테스트용. 40명의 데이터를 넣음
       /*
        for (int i = 0; i < fake_cnt; i++) {
            fake_dbanswer[i] = "9|dumi" + i + "|None2|0|0|0|NoJob|0|0";
        }*//*
        String[] fake_answer = new String[10]; //우선 데이터를 10개 받아온다.*//*
        for (int i = 0; i < 10; i++) {
            if (i >= fake_cnt) {
                break;
            }
            fake_answer[i] = fake_dbanswer[i];
            UserInfo userinfo = new UserInfo(fake_answer[i]);
            userList.add(userinfo);
            gotItemCnt++;

        }
        Log.i("MainAc", "" + MainActivity.userList.size());*/



       /* Log.i("MainActivity",packet);
        if((NetManager.SEND_LIST == Integer.parseInt(st_del.nextToken()))) {
            cnt = Integer.parseInt(st_del.nextToken()); //인원수
            for(int i = 0; i<cnt; i++)
            {
                userid[i] = st_del.nextToken();
                tempFor9 += NetManager.DELIMITER+userid[i];
            }
        }*/
        //  UserInfo userinfo = new UserInfo("9|test2|None2|0|0|0|NoJob|0|0");
        //userList.add(userinfo);
        //테스트 끝

        //메인 리스트 설정
        context = this;
        lv = (ListView) findViewById(R.id.freindlistview);

        final CustomAdapter customAdapter = new CustomAdapter(this);
        lv.setAdapter(customAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int userPosition = position;

                AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
                alt_bld.setMessage("대화 요청을 보내시겠습니까?").setCancelable(
                        false).setPositiveButton("요청",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                String msg = NetManager.REQ_CONNECT_REQUEST + NetManager.DELIMITER + "0" + NetManager.DELIMITER + MainActivity.userList.get(userPosition + 1).getId();
                                String[] answer = NetManager.sendandrecvMsg(msg);
                                yourId = MainActivity.userList.get(userPosition + 1).getId();

                                switch (answer[0])
                                {
                                    case NetManager.SEND_REQ_REPLY + NetManager.DELIMITER + "1":
                                        onRequest = false;
                                        Intent chatIntent = new Intent(getApplicationContext(), ChattingActivity.class);
                                        chatIntent.putExtra("your_id", yourId);
                                        MainActivity.this.startActivity(chatIntent);

                                        break;

                                    case NetManager.SEND_REQ_REPLY + NetManager.DELIMITER + "0":
                                        AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
                                        alt_bld.setMessage("요청이 거부되었습니다.").setCancelable(
                                                false).setPositiveButton("Yes",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        // Action for 'Yes' Button
                                                    }
                                                });
                                        AlertDialog alert = alt_bld.create();
                                        // Title for AlertDialog\
                                        alert.show();

                                        break;
                                }
                                // Action for 'Yes' Button
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alt_bld.create();
                // Title for AlertDialog
                // Icon for AlertDialog
                alert.show();

            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
                    //TODO 화면이 바닦에 닿을때 처리
                    Log.i("onScrollStateChanged","onScrollStateChanged");
                    String tempFor9 = "";
                    if(gotItemCnt*10 >= cnt) { //0>20 10>20 20>20
                        //더이상 새 리스트를 요청하지 않는다.
                    }
                    else {
                        for (int i = 0; i < 10; i++) {
                            if (i + gotItemCnt * 10 >= cnt) {
                                Log.i("onScrollStateChanged", "End of list2");
                                break;
                            } //
                            else {
                                tempFor9 += NetManager.DELIMITER + userid[i + gotItemCnt];
                                Log.i("onScrollStateChanged", tempFor9);
                            }
                        }
                        String msg = "";
                        msg = NetManager.REQ_INFORMATION + tempFor9;//정보요청 {9|id1|id2...}
                        // 정보수신 answer[0] = 9|id1|정보 answer[1]=9|id2|정보
                        answer = NetManager.sendandrecvMsg(msg);
                        for (int k = 0; k < 10; k++) {
                            if (k + gotItemCnt * 10 >= cnt) {
                                break;
                            } else {
                                UserInfo userinfo = new UserInfo(answer[k]);
                                userList.add(userinfo);
                            }
                        }
                        gotItemCnt++;
                        Log.i("Main_gotItemcnt", "" + gotItemCnt);
                        customAdapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);

            }
        });

        //상단 친구목록 갱신 버튼 리스너
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg="";
                int size = userList.size();
                for(int i=1; i<size; i++)
                    userList.remove(i);
                //userList.clear(); //유저리스트를 비운다
                gotItemCnt =0; //리스트에 불러온 유저수를 초기화 한다.
                cnt=0;//인원수 초기화

                msg = NetManager.REQ_LIST + NetManager.DELIMITER + NetManager.lat + NetManager.DELIMITER + NetManager.lng + NetManager.DELIMITER + distance; //리스트 요청 프로토콜 서버에 보냄 2|좌표

                answer = NetManager.sendandrecvMsg(msg); //리스트를 받음 {2|인원수|id1|id2}...

                String packet = answer[0];
                String tempFor9 = "";
                //2|인원수|id1|id2..
                StringTokenizer st_del = new StringTokenizer(packet, NetManager.DELIMITER);

                Log.i("packet", packet);

                if ((NetManager.SEND_LIST == Integer.parseInt(st_del.nextToken()))) { //2
                    cnt = Integer.parseInt(st_del.nextToken()); //인원수
                    for (int i = 0; i < cnt; i++) {
                        userid[i] = st_del.nextToken();
                    }
                }
                if (cnt == 0) {
                }//0명이면 정보를 요청하지 않는다.
                else
                {

                    // for(int i=0; i<cnt; i++){ //id를 최대 10개까지 요청

                    for(int j=0; j<10; j++)
                    {
                        if(j >= cnt) { //0,1 >=1
                            Log.i("MainActivity","End of list");break;} //
                        else{
                            tempFor9 += NetManager.DELIMITER + userid[j];
                            Log.i("MainActivity",tempFor9);
                        }
                    }
                    msg = NetManager.REQ_INFORMATION + tempFor9;//정보요청 {9|id1|id2...}
                    // 정보수신 answer[0] = 9|id1|정보 answer[1]=9|id2|정보
                    answer = NetManager.sendandrecvMsg(msg);
                    for (int k = 0; k < 10; k++) {
                        if(k >= cnt)
                        {break;}
                        else {
                            UserInfo userinfo = new UserInfo(answer[k]);
                            userList.add(userinfo);
                        }
                    }
                    //}

                }
                //메인 리스트 설정
                context = MainActivity.this;
                lv = (ListView) findViewById(R.id.freindlistview);

                final CustomAdapter customAdapter = new CustomAdapter(MainActivity.this);
                lv.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class RequestThread extends Thread
    {
        public void run()
        {
            while(onRequest)
            {
                try
                {
                    sleep(3000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                String msg = ""+NetManager.REQ_CONNECT_REPONSE;
                String[] answer = NetManager.sendandrecvMsg(msg);

                if(answer[0].charAt(0) == '4')
                {
                    if(answer[0].charAt(2) == '1')
                    {
                        onRequest = false;
                        Intent chatIntent = new Intent(MainActivity.this, ChattingActivity.class);
                        chatIntent.putExtra("your_id", yourId);
                        MainActivity.this.startActivity(chatIntent);

                    }
                    else
                    {
                        AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
                        alt_bld.setMessage("요청이 거부되었습니다.").setCancelable(
                                false).setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        // Action for 'Yes' Button
                                    }
                                });
                        AlertDialog alert = alt_bld.create();
                        // Title for AlertDialog\
                        alert.show();
                    }
                }
                if(answer[0].charAt(0) == '3' && answer[0].charAt(2) == '0')
                {
                    StringTokenizer st = new StringTokenizer(answer[0], NetManager.DELIMITER);
                    st.nextToken();
                    st.nextToken();
                    final String fromId = st.nextToken();
                    final String dialogMsg = fromId + "님께서 대화 요청을 보냈습니다.";

                    final AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
                    alt_bld.setMessage(dialogMsg).setCancelable(
                            false).setPositiveButton("수락",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    String msg = NetManager.SEND_REQ_REPLY + NetManager.DELIMITER + "1";
                                    NetManager.sendandrecvMsg(msg);

                                    onRequest = false;
                                    Intent chatIntent = new Intent(getApplicationContext(), ChattingActivity.class);
                                    chatIntent.putExtra("your_id", fromId);
                                    MainActivity.this.startActivity(chatIntent);
                                    // Action for 'Yes' Button
                                }
                            }).setNegativeButton("거부",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // Action for 'NO' Button
                                    String msg = NetManager.SEND_REQ_REPLY + NetManager.DELIMITER + "0";
                                    NetManager.sendandrecvMsg(msg);
                                    dialog.cancel();
                                }
                            });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog alert = alt_bld.create();
                            // Title for AlertDialog
                            alert.show();
                        }
                    });

                }
            }
        }
    }

    class ActivateThread extends Thread
    {
        static final private String msg = "" + NetManager.REQ_ACTIVATE_LIST;
        private int seconds = 0;

        public void run()
        {
            while(true)
            {
                String[] answer = NetManager.sendandrecvMsg(msg);
                StringTokenizer activateTokenizer = new StringTokenizer(answer[0], NetManager.DELIMITER);

                if(!activateTokenizer.nextToken().equals("a"))
                    continue;

                while(activateTokenizer.hasMoreTokens())
                {
                    String id = activateTokenizer.nextToken();
                    int activated = Integer.parseInt(activateTokenizer.nextToken());
                    int index = Integer.parseInt(activateTokenizer.nextToken());
                    String message = activateTokenizer.nextToken();

                    boolean searched = false;
                    int i;

                    for(i = 0; i<MainActivity.userList.size(); i++)
                    {
                        if(MainActivity.userList.get(i).getId().equals(id))
                        {
                            searched = true;
                            break;
                        }
                    }

                    if(searched)
                    {
                        if(activated == 1 )
                        {
                            MainActivity.userList.get(i).setActivate(true);
                            MainActivity.userList.get(i).setEmotion(index);
                            MainActivity.userList.get(i).setContent(message);
                        }
                        else
                        {
                            MainActivity.userList.get(i).setActivate(false);
                        }
                        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this);
                        lv.setAdapter(customAdapter);
                    }
                }

                seconds += 2;

                try {
                    sleep(2000);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


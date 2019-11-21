package com.example.haewonjeong.sentimateclient2;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by HaeWon Jeong on 4/21/2016.
 */
public class RelationshipActivity extends Activity
{
    private ListView friend_lv,block_lv;//친구, 차단 리스트 뷰
    private ImageButton exitBtn;
    private Context context;
    private ArrayList prgmName;
    public static String[] listName = new String[100];
    public static String[] userid= new String[100];
    public static ArrayList<UserInfo> FriendList = new ArrayList<>(); //친구 배열
    public static ArrayList<UserInfo> BolckedList = new ArrayList<>(); //차단 배열

    String[] answer;
    String msg = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship);

        exitBtn = (ImageButton)findViewById(R.id.relationship_exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //친구 목록 요청을 보냄 7|0|0
        msg = NetManager.REQ_ADDUSERORBLOCK + NetManager.DELIMITER + "0" + NetManager.DELIMITER + "0" ;
       answer = NetManager.sendandrecvMsg(msg); //친구 리스트를 받음 7|0|id1|id2...

        String friend_packet = answer[0];
        String temp; //토크나이징용 String
        int frined_cnt = 0; //인원수

        //7|0|id1|id2..
        StringTokenizer st_del = new StringTokenizer(friend_packet, NetManager.DELIMITER);
        Log.i("Relationpacket", friend_packet);

        st_del.nextToken(); //7

        String tempFor9 = ""+NetManager.REQ_INFORMATION; //9|id1|id2..
        while (st_del.hasMoreTokens())
        {
            frined_cnt++;
             //친구목록의 친구 정보를 요청하는 패킷을 만듦 9|id1|id2..
            tempFor9 += ""+NetManager.DELIMITER+st_del.nextToken();
        }
        if (frined_cnt == 0) {
        }//0명이면 정보를 요청하지 않는다.
        else {
            //친구 목록 요청을 보냄//9|id1|id2..
            msg = tempFor9 ;
            answer = NetManager.sendandrecvMsg(msg); //친구 리스트를 받음 // 9|닉넴|id|사진|나이|성|직업|혈액형|종교
            while(answer[0].charAt(0) != '9')
                answer = NetManager.sendandrecvMsg(msg);
            for (int i = 0; i < frined_cnt; i++) {
                Log.i("Relation",""+i);
                UserInfo userinfo = new UserInfo(answer[i]);
                FriendList.add(userinfo);
            }
        }

        /*//차단 친구 목록 요청을 보냄 7|1|0
        msg = NetManager.REQ_ADDUSERORBLOCK + NetManager.DELIMITER + "1" + NetManager.DELIMITER + "0" ;
        answer = NetManager.sendandrecvMsg(msg); //친구 리스트를 받음 7|1|id1|id2...

        String blocked_packet = answer[0];
        String temp2; //토크나이징용 String
        int block_cnt = 0; //인원수

        //7|0|id1|id2..
        StringTokenizer st_del2 = new StringTokenizer(blocked_packet, NetManager.DELIMITER);
        Log.i("packet", blocked_packet);

        st_del.nextToken(); //7
        st_del.nextToken(); //1

        String tempFor9_2 = ""+NetManager.REQ_INFORMATION; //9|id1|id2..
        while (st_del2.hasMoreTokens())
        {
            block_cnt++;
            //차단목록의 유저 정보를 요청하는 패킷을 만듦 9|id1|id2..
            tempFor9_2 += ""+NetManager.DELIMITER+st_del2.nextToken();
        }
        if (block_cnt == 0) {
        }//0명이면 정보를 요청하지 않는다.
        else {
            //차단 목록 요청을 보냄//9|id1|id2..
            msg = tempFor9_2 ;
            answer = NetManager.sendandrecvMsg(msg); //차단 리스트를 받음 // 9|닉넴|id|사진|나이|성|직업|혈액형|종교
            for (int i = 0; i < block_cnt; i++) {
                UserInfo userinfo = new UserInfo(answer[i]);
                BolckedList.add(userinfo);
            }
        }*/

        //친구 리스트뷰
        //어댑터 준비
        ArrayAdapter<String> friendListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);

        for(int i=0; i<FriendList.size(); i++)
        {
            friendListAdapter.add(FriendList.get(i).getNickname());
        }
        //어댑터 연결
        friend_lv = (ListView)findViewById(R.id.friendList);
        friend_lv.setAdapter(friendListAdapter);

        friend_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //짧게 누르면 채팅을 요청한다.
                final int userPosition = position;

                AlertDialog.Builder alt_bld = new AlertDialog.Builder(RelationshipActivity.this);
                alt_bld.setMessage("대화 요청을 보내시겠습니까?").setCancelable(
                        false).setPositiveButton("요청",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                String msg = NetManager.REQ_CONNECT_REQUEST + NetManager.DELIMITER + "0" + NetManager.DELIMITER + MainActivity.userList.get(userPosition + 1).getId();
                                String[] answer = NetManager.sendandrecvMsg(msg);
                                String yourId = MainActivity.userList.get(userPosition + 1).getId();

                                switch (answer[0])
                                {
                                    case NetManager.SEND_REQ_REPLY + NetManager.DELIMITER + "1":
                                        //onRequest = false;
                                        Intent chatIntent = new Intent(RelationshipActivity.this, ChattingActivity.class);
                                        chatIntent.putExtra("your_id", yourId);
                                        RelationshipActivity.this.startActivity(chatIntent);

                                        break;

                                    case NetManager.SEND_REQ_REPLY + NetManager.DELIMITER + "0":
                                        AlertDialog.Builder alt_bld = new AlertDialog.Builder(RelationshipActivity.this);
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

        friend_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //꾹 누르면 친구을 삭제 한다. 7|0|2|id
                //차단할 id를 서버에 보낸다.
                msg = NetManager.REQ_ADDUSERORBLOCK + NetManager.DELIMITER + "0"+ NetManager.DELIMITER+"2"
                        +NetManager.DELIMITER + FriendList.get(position).getId();
                answer = NetManager.sendandrecvMsg(msg); //삭제 결과를 받음, 쓰이는 곳은 없음
                //삭제된 id를 어레이리스트에서 뺀다.
                FriendList.remove(position);
                return true;
            }
        });


        /*차단 리스트 뷰는 친구 리스트와 같게 뜨게 해놓았음!!!*/
        ArrayAdapter<String>  blockedListAdapter=null;
        for(int i=0; i<BolckedList.size(); i++)
        {
            blockedListAdapter.add(FriendList.get(i).getNickname());
        }
        blockedListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        //어댑터 연결
        block_lv = (ListView)findViewById(R.id.banList);
        block_lv.setAdapter(blockedListAdapter);
        /*/차단 리스트뷰
        ArrayAdapter<String> blockedListAdapter=null;
        for(int i=0; i<BolckedList.size(); i++)
        {
            blockedListAdapter.add(BolckedList.get(0).getNickname());
        }
        blockedListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        //어댑터 연결
        block_lv = (ListView)findViewById(R.id.banList);
        block_lv.setAdapter(blockedListAdapter);

        block_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //짧게 누름
            }
        });
        block_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //꾹 누르면 차단을 삭제 한다. 7|1|2|id
                //차단할 id를 서버에 보낸다.
                msg = NetManager.REQ_ADDUSERORBLOCK + NetManager.DELIMITER + "1"+ NetManager.DELIMITER+"2"
                        +NetManager.DELIMITER + BolckedList.get(position).getId();
                answer = NetManager.sendandrecvMsg(msg); //차단 결과를 받음, 쓰이는 곳은 없음
                //차단한 id를 어레이리스트에서 뺀다.
                BolckedList.remove(position);
                return true;
            }
        });*/
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
}



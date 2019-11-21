package com.example.haewonjeong.sentimateclient2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.haewonjeong.sentimateclient2.NetManager;
import com.example.haewonjeong.sentimateclient2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class ChattingActivity extends AppCompatActivity {
    ImageButton input_button;
    ImageButton chat_addBtn, chat_banBtn;
    EditText editText;
    TextView nameText;
    String user_input = "";
    int last_msg_index = 0;
    String my_id = "";
    String your_id = "";
    String[] msg_tokens = {"", "", "", ""};
    String send_id = "";
    String msg = "";
    Date date;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent get_id = getIntent();
        this.your_id = get_id.getStringExtra("your_id");
        this.my_id = MainActivity.userList.get(0).getId();

        Get_data get_data = new Get_data();
        get_data.start();
        input_button = (ImageButton) findViewById(R.id.chat_sendBtn);
        editText = (EditText) findViewById(R.id.chat_chatEdit);
        chat_addBtn =(ImageButton)findViewById(R.id.chat_addBtn);
        chat_banBtn = (ImageButton)findViewById(R.id.chat_banBtn);
        nameText = (TextView)findViewById(R.id.chat_name);

        nameText.setText(your_id);

        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_input = editText.getText().toString();
                String msg = NetManager.REQ_CHAT + NetManager.DELIMITER + user_input;
                NetManager.sendandrecvMsg(msg);

                editText.setText("");

                // 입력받은 문자열 화면에 띄우고 서버에 전송

            }
        });
        //친구 추가
        chat_addBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                //추가할 id를 서버에 보낸다.
                msg = NetManager.REQ_ADDUSERORBLOCK + NetManager.DELIMITER + "0"+ NetManager.DELIMITER+"1"
                        +NetManager.DELIMITER + your_id;
                NetManager.sendandrecvMsg(msg); //추가 결과를 받음, 쓰이는 곳은 없음
                //id를 어레이리스트에추가한다.

                msg = NetManager.REQ_INFORMATION + NetManager.DELIMITER + your_id;
                String[] answer = NetManager.sendandrecvMsg(msg);

                RelationshipActivity.FriendList.add(new UserInfo(answer[0]));
            }
        });
        //친구 차단
        chat_banBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });

    }

    class Get_data extends Thread {
        @Override
        public void run() {
            super.run();
            // 서버에 데이터 요청
            while(true) {
                // 메세지의 마지막 인덱스를 보내면 최신의 나머지 데이터가 넘어옴.
                // 넘기는 방식 : {|b|내아이디|상대방아이디|마지막인덱스}
                String msg = "b" + NetManager.DELIMITER + last_msg_index;
                Log.d("get_chatting_data_param", msg);
                String[] result = NetManager.sendandrecvMsg(msg);
//            Log.d("RESPONE_MSG", result);

                if (result[0].charAt(0) == 'B')
                    continue;
                if (result[0].charAt(0) == '-')
                    continue;
                if (result[0].charAt(0) == '5')
                    continue;

                try {
                    for (int i = 0; result[i] != null; i++) {

                        StringTokenizer element = new StringTokenizer(result[i], NetManager.DELIMITER);

                        // 'b' 자르기
                        element.nextToken();

                        // 메세지 인덱스
                        last_msg_index = Integer.parseInt(element.nextToken());

                        // 메세지 보낸 사람
                        send_id = element.nextToken();

                        // 메세지 내용
                        msg = element.nextToken();

                        // 메세지 보낸 시간
                        try {
                            date = simpleDateFormat.parse(element.nextToken());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Log.d("Chatting", "Sender : " + send_id + ", msg : " + msg /*+ ", time : " + date.getTime()*/);
                    }
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 넘어온 데이터를 {}로 구분하여 한줄씩 리스트에 정리하여 넣어줌

                }catch(Exception e)
                {

                }
            }

        }
    }
}

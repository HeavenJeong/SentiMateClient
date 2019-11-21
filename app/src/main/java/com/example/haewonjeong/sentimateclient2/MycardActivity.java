package com.example.haewonjeong.sentimateclient2;

/**
 * Created by HaeWon Jeong on 5/12/2016.
 * 내 상태 작성 엑티비티
 */
        import android.app.Activity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;

        import java.util.StringTokenizer;


public class MycardActivity extends Activity
{

    private String msg;
    private Button postbtn;
    private TextView nicknametxt, agetxt; //닉네임, 나이
    private Spinner emotion_spinner; //기분
    private Spinner distance_spinner;
    private EditText contentEdit; //상태 메세지
    private String[] emotionCate = {"센치해요", "기분이 좋아요", "슬퍼요"};
    private String[] distanceCate = {"100m", "500m", "1km", "5km", "10km"};
    private int[] distanceValue = {100, 500, 1000, 5000, 10000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycard);

        postbtn = (Button) findViewById(R.id.postbtn);
        nicknametxt = (TextView)findViewById(R.id.NicknameText);
        agetxt = (TextView)findViewById(R.id.ageText);
        emotion_spinner = (Spinner) findViewById(R.id.emotion_spinner);
        distance_spinner = (Spinner)findViewById(R.id.distancespinner);
        contentEdit = (EditText)findViewById(R.id.msgfield);

        //서버에 등록 되어있는 내 닉네임, 나이를 띄움.
        nicknametxt.setText(MainActivity.userList.get(0).getNickname());
        agetxt.setText("  ("+MainActivity.userList.get(0).getBirthyear()+")");
        //카드 등록
        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //활성화 프로토콜 1|상태||메세지
                String[] answer;
                int emotion = 0;
                String content = "";
                emotion = emotion_spinner.getSelectedItemPosition();
                content = contentEdit.getText().toString();

                int distance = distanceValue[distance_spinner.getSelectedItemPosition()];

                //수정한 기분,상태를 userinfor에 설정
                MainActivity.userList.get(0).setEmotion(emotion);
                MainActivity.userList.get(0).setContent(content);


                //활성화 프로토콜 서버에 보냄 8|1|emotion|conent
                msg = NetManager.REQ_ACTIVATE + NetManager.DELIMITER + "1" + NetManager.DELIMITER
                        + emotion + NetManager.DELIMITER + content;
                answer = NetManager.sendandrecvMsg(msg);

                NetManager.onActivated = true;
                //NetManager.activateThread.start();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("distance", distance);
                startActivity(intent);
                Log.i("execute", "Mycard");

                finish();
            }
        });

        ArrayAdapter<String> emotion_adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, emotionCate);
        emotion_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        emotion_spinner.setAdapter(emotion_adapter);

        ArrayAdapter<String> distance_adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, distanceCate);
        distance_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        distance_spinner.setAdapter(distance_adapter);
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
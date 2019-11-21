package com.example.haewonjeong.sentimateclient2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterActivity extends Activity {

    private ImageButton idOverlapBtn; //id중복체크 확인 버튼
    private ImageButton nickoverlapbtn;//닉넴 중복체크 확인 버튼
    private EditText registeridtext;//id 입력란
    private EditText registerpasswordtxt; //비밀번호
    private EditText registerpasswordcheck; //비밀번호 확인
    private EditText nicknametxt;//닉넴
    private EditText jobtxt;//직업
    private TextView errortxt1;//id중복체크 결과를 알려주는 텍스트
    private TextView errortxt2;//비밀번호 검사 결과를 알려주는 텍스트
    private TextView errortxt3; //닉넴 중복체크
    private boolean overlapchk = false;//id중복체크 결과값
    private boolean nickoverlapchk = false; //닉넴 중복체크 결과값
    private boolean idBtnClickedChk = false; //닉네임 중복체크 버튼 클릭 확인
    private boolean nickBtnCickedChk = false; //닉네임 중복 체크 버튼 클릭 확인
    private Spinner agespinner; //나이
    private Spinner sexspinner; //성별
    private Spinner bloodtypespinner; //나이
    private Spinner reilgionspinner; //성별
    private Button okbtn;

    private String[] ageCate;
    private String[] sexCate = {"남자", "여자"};
    private String[] bloodtypeCate = {"A", "B", "O", "AB"};
    private String[] religionCate = {"천주교", "기독교", "불교", "기타"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idOverlapBtn = (ImageButton) findViewById(R.id.overlapchkbtn);//id 중복확인
        nickoverlapbtn = (ImageButton) findViewById(R.id.nickoverlapchkbtn);//닉네임 중복확인
        registeridtext = (EditText) findViewById(R.id.registerIdfield); //id 입력란
        registerpasswordcheck = (EditText) findViewById(R.id.PasswordCheckfield); //비밀번호 확인
        registerpasswordtxt = (EditText) findViewById(R.id.registerPasswordfield); //비밀번호
        nicknametxt = (EditText) findViewById(R.id.nicknamefield);//닉네임 입력란
        jobtxt = (EditText)findViewById(R.id.jobfield);
        errortxt1 = (TextView) findViewById(R.id.errortxt1);
        errortxt2 = (TextView) findViewById(R.id.errortxt2);
        errortxt3 = (TextView) findViewById(R.id.errortxt3);
        agespinner = (Spinner) findViewById(R.id.agespinner);
        sexspinner = (Spinner) findViewById(R.id.sexspinner);
        bloodtypespinner = (Spinner) findViewById(R.id.bloodtypespinner);
        reilgionspinner = (Spinner) findViewById(R.id.religionspinner);
        okbtn = (Button) findViewById(R.id.okbtn);

        ageCate = new String[100];
        for (int i = 0; i < ageCate.length; i++)
            ageCate[i] = "" + (i + 11);

        ArrayAdapter<String> ageadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, ageCate);
        ageadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        agespinner.setAdapter(ageadapter);


        ArrayAdapter<String> sexadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, sexCate);
        sexadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sexspinner.setAdapter(sexadapter);

        ArrayAdapter<String> bloodtypeadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, bloodtypeCate);
        bloodtypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bloodtypespinner.setAdapter(bloodtypeadapter);


        ArrayAdapter<String> reilgionadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, religionCate);
        reilgionadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        reilgionspinner.setAdapter(reilgionadapter);

        //id 중복 체크
        idOverlapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id;
                String msg;
                String[] answer;

                id = registeridtext.getText().toString();

                //1/0/id
                msg = NetManager.REQ_RESTER + NetManager.DELIMITER + "0" + NetManager.DELIMITER + id;

                answer = NetManager.sendandrecvMsg(msg);

                switch (answer[0]) {
                    //1/0/중복결과
                    case NetManager.REQ_RESTER + NetManager.DELIMITER + "0" + NetManager.DELIMITER + "1":  //id중복 체크시 : 1 성공, 0 실패
                        overlapchk = true;
                        errortxt1.setText("사용가능한 id 입니다.");
                        idOverlapBtn.setImageResource(R.drawable.check_after_icon);
                         break;
                    case NetManager.REQ_RESTER + NetManager.DELIMITER + "0" + NetManager.DELIMITER + "0":
                        overlapchk = false;
                        idOverlapBtn.setImageResource(R.drawable.check_before_icon);
                        errortxt1.setText("중복된 id 입니다.");
                        break;
                }
            }
        });

        //비밀번호 확인
        TextWatcher watcher = new TextWatcher() {
            //텍스트의 길이가 변경 되었을 경우 발생할 이벤트를 작성
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            //텍스트가 변경 될 때 마다 발생할 이벤트를 작성.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //텍스트 변경 후 발생할 이벤트를 작성
            @Override
            public void afterTextChanged(Editable s) {
                if (registerpasswordcheck.isFocusable()) { //Edit가 포커스 되어 있을 경우에만 실행 됨.

                    if ((registerpasswordtxt.getText().toString()).equals(registerpasswordcheck.getText().toString())) {//재 입력한 비밀번호가 같을 경우
                        errortxt2.setText("비밀번호가 일치합니다.");
                    } else {
                        errortxt2.setText("비밀번호가 일치하지 않습니다.");
                    }
                }

            }
        };
        registerpasswordcheck.addTextChangedListener(watcher);

        //닉네임 중복 체크
        nickoverlapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nick;
                String nickmsg;
                String[] nickanswer;

                nick = nicknametxt.getText().toString();
                //1/1/nick
                nickmsg = NetManager.REQ_RESTER + NetManager.DELIMITER + "1" + NetManager.DELIMITER + nick;

                nickanswer = NetManager.sendandrecvMsg(nickmsg);
                //1/1/중복결과
                switch (nickanswer[0]) {

                    case NetManager.REQ_RESTER + NetManager.DELIMITER + "1" + NetManager.DELIMITER + "1":  //닉넴중복 체크시 : 1 성공, 0 실패
                        nickoverlapchk = true;
                        nickoverlapbtn.setImageResource(R.drawable.check_after_icon);
                        errortxt3.setText("사용 가능한 닉네임 입니다.");
                        break;
                    case NetManager.REQ_RESTER + NetManager.DELIMITER + "1" + NetManager.DELIMITER + "0":
                        nickoverlapchk = false;
                        nickoverlapbtn.setImageResource(R.drawable.check_before_icon);
                        errortxt3.setText("사용중인 닉네임 입니다.");

                        break;

                }
            }
        });

        //회원가입
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nickname,id,password,photoURL,birthyear,sex,job,bloodtype,religion;
                String msg;
                String[] answer;
                if (overlapchk && nickoverlapchk) //아이디와 닉네임 중복체크 성공시
                {
                    nickname = nicknametxt.getText().toString();
                    id = registeridtext.getText().toString();
                    password = registerpasswordtxt.getText().toString();
                    photoURL = "";
                    birthyear = agespinner.getSelectedItem().toString();
                    sex = sexspinner.getSelectedItemPosition() + ""; //male 0, female 1
                    job = jobtxt.getText().toString();
                    bloodtype = bloodtypespinner.getSelectedItemPosition() +"";
                    religion = reilgionspinner.getSelectedItemPosition()+"";

                    //회원가입 : 1|2|닉넴|id|사진|생일|성|직업|혈액형|종교
                    msg = NetManager.REQ_RESTER + NetManager.DELIMITER + "2" + NetManager.DELIMITER +
                            nickname + NetManager.DELIMITER +id+ NetManager.DELIMITER + password + NetManager.DELIMITER
                            +photoURL+ NetManager.DELIMITER +birthyear+ NetManager.DELIMITER+sex+ NetManager.DELIMITER
                            +job+ NetManager.DELIMITER+bloodtype+ NetManager.DELIMITER+religion;

                    answer = NetManager.sendandrecvMsg(msg);

                    switch (answer[0]) {

                        //{1|2|1 가입 성공
                        case NetManager.REQ_RESTER + NetManager.DELIMITER + "2" + NetManager.DELIMITER + "1":  //회원가입 결과 1
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            break;
                        //1|2|0 가입 실패
                        case NetManager.REQ_RESTER + NetManager.DELIMITER + "2" + NetManager.DELIMITER + "0":

                            break;
                    }
                } else {
                }
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
}


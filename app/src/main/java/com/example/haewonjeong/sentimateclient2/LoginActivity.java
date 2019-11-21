package com.example.haewonjeong.sentimateclient2;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by HaeWon Jeong on 4/14/2016.
 */
public class LoginActivity extends Activity {

    private TextView registertext;
    private Button loginbtn;
    private EditText idfield;
    private EditText passwordfield;
    private TextView loginError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NetManager.init(this);

        registertext = (TextView) findViewById(R.id.register);
        registertext.setText(Html.fromHtml("<u>"+"회원 가입"+"</u>"));
        loginbtn = (Button) findViewById(R.id.login);
        idfield = (EditText) findViewById(R.id.idfield);
        passwordfield = (EditText) findViewById(R.id.passwordfield);
        loginError = (TextView) findViewById(R.id.login_error);


        //회원가입
        registertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        //로그인
        //SharedPreference로 활성화 상태 체크-> id, password 값 넘겨줌 -> 카드쓰기 / 리스트로
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id;
                String password;
                String msg;
                String[] answer;

                id = idfield.getText().toString();
                password = passwordfield.getText().toString();

                //Sever가 받는것 0|id|password
                msg = NetManager.REQ_LOGIN + NetManager.DELIMITER + id + NetManager.DELIMITER + password;
                answer = NetManager.sendandrecvMsg(msg);

                switch (answer[0]) {

                    //로그인 성공하면 내 정보를 요청하는 패킷을 보낸다.
                    case NetManager.REQ_LOGIN + NetManager.DELIMITER + "0":

                        //정보요청 9|id
                        msg = NetManager.REQ_INFORMATION + NetManager.DELIMITER + id;
                        answer = NetManager.sendandrecvMsg(msg);
                        UserInfo Userinfo = new UserInfo(answer[0]);
                        MainActivity.userList.add(Userinfo);

                        //활성화 체크
                        SharedPreferences sp = getSharedPreferences("Act", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("Act", MainActivity.userList.get(0).getActivate());
                        editor.commit();

                        Boolean myactivatedCheck = sp.getBoolean("Act", false);
                        if (myactivatedCheck) //이미 글을 썼다면 리스트를 띄우고
                        {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else //아니면 카드 작성 엑티비를 띄운다.
                        {
                            Intent intent = new Intent(getApplicationContext(), MycardActivity.class);
                            startActivity(intent);
                        }
                        break;
                    //1비밀번호 틀림
                    case NetManager.REQ_LOGIN + NetManager.DELIMITER + "1":
                        loginError.setText("비밀번호가 틀렸습니다.");
                        break;
                    //2 아이디 없음
                    case NetManager.REQ_LOGIN + NetManager.DELIMITER + "2":
                        loginError.setText("존재하지 않는 아이디 입니다.");
                        break;
                    //3 이미로그인
                    case NetManager.REQ_LOGIN + NetManager.DELIMITER + "3":
                        loginError.setText("이미 로그인 되어 있습니다.");
                        break;

                }
            }
        });

        getLocation();
        Log.i("LocationManager", "getLocation");
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = 0;
        float minDistance = 0;
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("LocationManager","onLocationChanged :"+location);
                NetManager.setLocation(location.getLatitude(), location.getLongitude());
                /*NetManager.lat = location.getLatitude();
                NetManager.lng = location.getLongitude();*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);

        /*Location myLocation = new Location("myLocation");
        try {
            myLocation.setLatitude(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
            myLocation.setLongitude(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
            myLat = myLocation.getLatitude();
            myLng = myLocation.getLongitude();
            canGetLoc = true;
        } catch (Exception e) {
            try {
                myLocation.setLatitude(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude());
                myLocation.setLongitude(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude());
                myLat = myLocation.getLatitude();
                myLng = myLocation.getLongitude();
                canGetLoc = true;
            } catch (Exception exception) {
                canGetLoc = false;
                Toast.makeText(this, "현재 위치 정보를 가져오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }*/
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
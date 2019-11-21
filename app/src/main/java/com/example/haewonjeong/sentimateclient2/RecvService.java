package com.example.haewonjeong.sentimateclient2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.net.Socket;

/**
 * Created by HaeWon Jeong on 4/21/2016.
 */
public class RecvService extends Service implements Runnable
{
    private byte[] c = new byte[1024];

    private Socket socket;

    public int onStartCommand(Intent intent, int flags, int stardId) // 쓰레드 실행 시 시작되는 함수
    {
        Thread myThread = new Thread(this);
        myThread.start();

        return START_NOT_STICKY; // 서비스 속성 선택
    }

    public void run()
    {
        try {
            NetManager.getConnectSem().acquire();
        }
        catch (Exception e)
        {

        }

        socket = NetManager.getSocket();
        if(socket == null)
            Log.i(NetManager.TAG, "null");
        Log.i(NetManager.TAG, "recv release");

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(socket.getInputStream());
        }
        catch(Exception e)
        {
            Log.i(NetManager.TAG, e.toString());
            Log.i(NetManager.TAG, "ahh");
        }
        Log.i(NetManager.TAG, "recv init");

        while(true)
        {
            // Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
            try
            {
                //데이터 수신
                //socket.connect(socketAddress, 3000);
                //byte[] c = new byte[1024];
                int len;

                if(bis.available() != 0)
                {
                    len = bis.read(c);
                    Log.i(NetManager.TAG, "len : " + len);
                    if(len == -1) {
                        // 통신 종료 or 재연결
                        break;
                    }

                    String recvData = "";
                    for(int i = 0; i < c.length; i++) {
                        recvData += (char) c[i];
                    }
                    char[] tempArr = recvData.toCharArray();
                    tempArr[len] = '\0';
                    recvData = new String(tempArr);

                    NetManager.setRecvedMsg(recvData);
                    Log.i(NetManager.TAG, "recv data : " + recvData);
                }

                try
                {
                    Thread.sleep(30);
                }catch(Exception e)
                {

                }

                //받은 데이터를 나눔
                //Start byte : <, End Byte >, Delimeter |
                //StringTokenizer st = new StringTokenizer(recvData,DELIMETER );
                //int command = Integer.parseInt(st.nextToken());

                    /*switch(command)
                    {

                        //PACKET : 0x01SEND_RESULT_LOGIN/id/password..
                        case REQ_RESTER:{
                            break;
                        }

                        //PACKET : 0x011/0/중복체크
                        //PACKET : 0x011/회원가입
                        case REQ_LIST:{
                            break;
                        }
                        //PACKET : 0x012/좌표
                        case REQ_CONNECT_REQUEST:{
                            break;
                        }
                        //PACKET : 0x013/0/id
                        //PACKET : 0x013/1         timeout
                        case REQ_ACTIVATE:{
                            break;
                        }
                        //PACKET : 0x014/0/id
                        //PACKET : 0x014/1/id/상태/메세지
                        case REQ_CHAT:{
                            break;
                        }
                        //PACKET : 0x016
                        case REQ_USEROUT:{
                            break;
                        }
                        //PACKET : 0x017/0/id       친구
                        //PACKET : 0x017/1/id       차단
                        case REQ_ADDUSERORBLOCK:{
                            break;
                        }

                    }*/
            }
            catch (Exception e)
            {
                e.printStackTrace();
                break;
            }


        }//while종료

        Log.i(NetManager.TAG, "Receive service terminated");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


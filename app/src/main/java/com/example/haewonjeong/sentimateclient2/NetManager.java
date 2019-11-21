package com.example.haewonjeong.sentimateclient2;


        import android.content.Context;
        import android.content.Intent;
        import android.util.Log;

        import java.io.BufferedOutputStream;
        import java.io.IOException;
        import java.net.InetSocketAddress;
        import java.net.Socket;
        import java.net.SocketAddress;
        import java.util.StringTokenizer;
        import java.util.concurrent.Semaphore;

/**
 * Created by HaeWon Jeong on 4/11/2016.
 */
public class NetManager
{
    private static final String IP = "192.168.0.245";
    private static final int PORT = 24181;
    public static final String START_BITE = "{";
    public static final String END_BITE = "}";
    public static final String DELIMITER = "|";
    //SEPARATOR /
    //Delimeter " "

    public static final String TAG = "juwon";

    //서버에 전송하는 메세지 코드(서버 받는거)
    public static final int REQ_LOGIN = 0;//로그인 결과
    public static final int REQ_RESTER = 1;//회원가입 결과
    public static final int REQ_LIST = 2; //리스트요청
    public static final int REQ_CONNECT_REQUEST = 3; //연결요청
    public static final int REQ_CONNECT_REPONSE = 4; //연결 답변
    public static final int REQ_CHAT = 5; //채팅
    public static final int REQ_USEROUT = 6; //채팅 퇴장
    public static final int REQ_ADDUSERORBLOCK =7;//친구/차단
    public static final int REQ_ACTIVATE =8; //활성화
    public static final int REQ_INFORMATION =9; //정보요청 9|id..
    public static final char REQ_ACTIVATE_LIST = 'a'; // 활성화 정보 요청

    //enterroom, sendwords, logout, coerceout

    //서버로부터 전송되는 메세지 코드(client 받는거)
    public static final int SEND_RESULT_LOGIN = 0; //로그인결과
    public static final int SEND_RESTER_RESULT = 1; //회원가입결과
    public static final int SEND_LIST = 2; //리스트
    public static final int SEND_REQ_CONNECT = 3; //연결 요청
    public static final int SEND_REQ_REPLY =4; //연결답변
    public static final int SEND_CHAT = 5; //채팅
    public static final int SEND_USEROUT = 6; //상대 퇴장
    public static final int SEND_INFORMMATION_GET =9; //정보수신
    public static final char SEND_ACTIVATE_LIST = 'a'; // 활성화 정보 수신
    //enterroom, yes_loging

    static private boolean isInitialized = false;

    static private Object sendKey = new Object();

    static private JCnetsend sendto;
    //static private JCnetget recfrom;
    static private String sendedmsg="";
    static private String recvedmsg="";

    static private SocketAddress socketAddress;
    static private Socket socket;

    static private Semaphore recvSem;
    static private Semaphore connectSem;

    static public boolean onActivated = false;

    static public double lat;
    static public double lng;

    static public void init(Context con)
    {
        if(isInitialized)
            return;
        isInitialized = true;
        recvSem = new Semaphore(1, true);
        connectSem = new Semaphore(2, true);
        try {
            recvSem.acquire();
            connectSem.acquire();
            connectSem.acquire();
        }
        catch(Exception e)
        {

        }

        //Thread started
        sendto = new JCnetsend();
        //sendto.start();
        Intent intent = new Intent(con, RecvService.class);
        //recfrom = new JCnetget();
        //recfrom.start();

        class connectThread extends Thread
        {
            public void run()
            {
                Log.i(TAG, "run");
                socketAddress = new InetSocketAddress(IP, PORT);
                socket = new Socket(); //Create endpoint for TCP
                try {
                    socket.connect(socketAddress, 3000);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, e.toString());
                    Log.i(TAG, "catch");
                }
                Log.i(TAG, "after catch");

                connectSem.release();
                connectSem.release();

                //1/0/id
                String msg = NetManager.REQ_RESTER + NetManager.DELIMITER + "0" + NetManager.DELIMITER + "dd";

                NetManager.sendandrecvMsg(msg);
            }
        }

        //recfrom.start();
        con.startService(intent);
        sendto.start();

        new connectThread().start();
    }

    /*public NetManager()
    {
        //Thread started
        sendto = new JCnetsend();
        //sendto.start();
        recfrom = new JCnetget();
        //recfrom.start();

        class connectThread extends Thread
        {
            public void run()
            {
                socketAddress = new InetSocketAddress(IP, PORT);
                socket = new Socket(); //Create endpoint for TCP
                try {
                    socket.connect(socketAddress, 3000);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                sendto.start();
                recfrom.start();
            }
        }

        new connectThread().start();
    }*/

    //스타트,엔드 비트 붙여 주는 메소드
    static public String[] sendandrecvMsg(String s)
    {
        String[] copy = new String[100];

        synchronized (sendKey) {
            //스타트 비트
            sendedmsg += START_BITE;
            sendedmsg += DELIMITER;
            /*switch (s) {
                case "로그인":
                    sendedmsg += REQ_LOGIN;
                    break;
                case "회원가입":
                    sendedmsg += REQ_RESTER;
                    break;
                case "리스트요청":
                    sendedmsg += REQ_LIST;
                    break;
                case "연결요청":
                    sendedmsg += REQ_CONNECT_REQUEST;
                    break;
                case "활성화":
                    sendedmsg += REQ_CHAT;
                    break;
                case "채팅":
                    sendedmsg += REQ_USEROUT;
                    break;
                case "채팅퇴장":
                    sendedmsg += REQ_USEROUT;
                    break;
                case "친구/차단":
                    sendedmsg += REQ_ADDUSERORBLOCK;
                    break;

            }*/
            sendedmsg += s;
            //엔드 비트
            sendedmsg += END_BITE;
        }


        try {
            recvSem.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "recvSem acquired");

        //받은 패킷을 자름
        StringTokenizer st = new StringTokenizer(recvedmsg,END_BITE);
        String temp;
        int i=0;
        while(st.hasMoreTokens()){
            temp = st.nextToken();
            if(!START_BITE.equals(temp.charAt(0)+""))
            { continue;}
            else
            {
                copy[i++]=temp.substring(DELIMITER.length() + 1);
            }

        }
        recvedmsg = "";

        return copy;
    }

    static public class JCnetsend extends Thread
    {
        Context context;
        public void run()
        {
            try {
                connectSem.acquire();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            Log.i(TAG, "send release");

            /*socketAddress = new InetSocketAddress(IP, PORT);
            socket = new Socket(); //Create endpoint for TCP
            try {
                socket.connect(socketAddress, 3000);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Log.i(TAG, "send error");
                e.printStackTrace();
            }
            Log.i(TAG, "send init");
            //byte[] b = new byte[1024];

            while(true) {
                try {

                    if(!sendedmsg.equals("")) {
                        synchronized(sendKey) {
                            //Toast.makeText(context, sendedmsg, Toast.LENGTH_LONG).show();
                            bos.write(sendedmsg.getBytes());
                            bos.flush();//클라이언트에 데이터를 재전송
                        }
                    }
                    sendedmsg = "";


                } catch(Exception e) {
                    e.printStackTrace();
                    break;
                }

                try {
                    Thread.sleep(30);
                } catch(Exception e) {

                }
            }

            Log.i(TAG, "Send thread terminated");

        }
    }

    public static Socket getSocket()
    {
        return socket;
    }

    public static Semaphore getConnectSem()
    {
        return connectSem;
    }

    public static void setRecvedMsg(String msg)
    {
        recvedmsg += msg;
        Log.i(TAG, "after set recved msg");
        recvSem.release();
    }

    public static void setLocation(double ilat, double ilng)
    {
        lat = ilat;
        lng = ilng;
        Log.i("LocationManager", "lat : " + lat + ", lng : " + lng);
    }

}


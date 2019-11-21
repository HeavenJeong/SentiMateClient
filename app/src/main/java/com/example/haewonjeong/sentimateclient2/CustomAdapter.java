package com.example.haewonjeong.sentimateclient2;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by HaeWon Jeong on 4/21/2016.
 */
public class CustomAdapter extends BaseAdapter {

    String[] nameId, emotionId,contentId;
    Context context;
    int[] imageId, ageId;
    private static LayoutInflater inflater = null;
    ArrayList<UserInfo> forListAarry = new ArrayList<>(); //유저 배열을 하나 만든다.

    //생성자
    public CustomAdapter(MainActivity mainActivity){
        context = mainActivity;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        forListAarry.addAll(MainActivity.userList); //원래의 유저 리스트를 깊은 복사한다.
        forListAarry.remove(0); //맨처음 본인의 Userinfo는 뺀다.
    }

    @Override
    public int getCount() {

        //return MainActivity.userList.size()-1;
        return forListAarry.size();
    }

    //지정한 위치에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }
    public class Holder
    {
        TextView nametxtview,agetxtview,emotiontxtview, contettxtview;
        ImageView imgview;

    }

    //지정한 위치에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        //return MainActivity.userList.get(position+1);
        return  forListAarry.get(position);
    }
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    public View getView(int position, View convertView, ViewGroup parent){


        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Holder holder = new Holder();
        // "card_list" Layout을 inflate하여 convertView 참조 획득.
        View rowView;
        rowView = inflater.inflate(R.layout.card_list,null);
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        holder.nametxtview = (TextView)rowView.findViewById(R.id.nameTextview);
        holder.imgview = (ImageView)rowView.findViewById(R.id.imageView1);
        holder.agetxtview = (TextView)rowView.findViewById(R.id.ageText);
        holder.emotiontxtview =(TextView) rowView.findViewById(R.id.emotionField);
        holder.contettxtview = (TextView)rowView.findViewById(R.id.contentField);


        // Data Set(userList)에서 position에 위치한 데이터 참조 획득
        UserInfo userInfo = forListAarry.get(position);
        //UserInfo userInfo = MainActivity.userList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        holder.nametxtview.setText(userInfo.getNickname());
        holder.imgview.setImageResource(R.drawable.profile_image1);
        holder.agetxtview.setText("("+userInfo.getAge()+")");
        holder.emotiontxtview.setText(userInfo.getEotionString());
        holder.contettxtview.setText(userInfo.getContent());

       /* rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Clicked"+ nameId[position],Toast.LENGTH_SHORT).show();
            }
        });*/


        return rowView;
    }

    //아이템 데이터 추가
    public void addItem(){
        UserInfo userInfo = new UserInfo();
        //userInfo.set
        //MainActivity.userList.add(userInfo);
    }
}
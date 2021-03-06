//https://junyoung-developer.tistory.com/107
//https://stackoverflow.com/questions/1851633/how-to-add-a-button-dynamically-in-android/13423467
//http://sunmo.blogspot.com/2010/10/tabwidget-%EA%B0%81-%ED%8E%98%EC%9D%B4%EC%A7%80%EB%B3%84-%EC%95%A1%ED%8B%B0%EB%B9%84%ED%8B%B0-%EB%B3%B4%EC%97%AC%EC%A3%BC%EA%B8%B0-2.html
//https://stackoverflow.com/questions/21950587/how-to-use-different-activities-with-tabhost-widget-in-android/21950690
//https://doraeul.tistory.com/21
//https://recipes4dev.tistory.com/42 - listView
//https://recipes4dev.tistory.com/45 - listview and button
//https://www.youtube.com/watch?v=aJ8pYAtMdlU - dynamically listview 유튜브
//https://kiwinam.com/posts/23/android-start-activity-for-result/ -startactivityforresult()
//https://stackoverflow.com/questions/2217753/changing-background-color-of-listview-items-on-android-리스트뷰 아이템 배경 바꾸기

package com.example.min;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.min.ChooseDictionary;
import com.example.min.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends ActivityGroup {
    TabHost.TabSpec spec;
    int seletedItem=0;   //선택된단어장

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView username;
    private TextView affiliation;

    static ListView listview;
    static ListViewAdapter adapter;
    static ArrayList<String> items;
    static Context context;
    static ArrayList<String> itemColor; //dic Color

    private ImageView profile_image;
    private final String imgName = "osz.png";

    public int CSAT = 1;
    public int TOEIC = 2;
    public int TOEFL = 3;
    public int EleMid = 4;
    public int TEPS = 5;
    public int voca = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.listView1);
        items = new ArrayList<>();
        context=getApplicationContext();
        itemColor=new ArrayList<String>();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        username = findViewById(R.id.username);
        FirebaseUser firebaseUser = auth.getCurrentUser();

        profile_image = findViewById(R.id.profile_image);
        affiliation = findViewById(R.id.affiliation);

        try {
            String imgpath = getCacheDir() + "/" + imgName;   // 내부 저장소에 저장되어 있는 이미지 경로
            Bitmap bm = BitmapFactory.decodeFile(imgpath);
            profile_image.setImageBitmap(bm);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋
        } catch (Exception e) {
        }


        DocumentReference docRef = db.collection("UserInfo").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username.setText(document.get("Name").toString());
                        affiliation.setText(document.get("Affiliation").toString());
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with", task.getException());
                }
            }
        });

        /*TabHost tabHost=findViewById(R.id.host);
        tabHost.setup();
        TabHost.TabSpec spec= tabHost.newTabSpec("tab1");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(),R.drawable.mypage_icon,null));
        spec.setContent(R.id.tab1);
        tabHost.addTab(spec);
        spec= tabHost.newTabSpec("tab2");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(),R.drawable.notice_icon,null));
        spec.setContent(R.id.tab2); //settingmain
        tabHost.addTab(spec);
        spec= tabHost.newTabSpec("tab3");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(),R.drawable.setting_icon,null));
        spec.setContent(R.id.tab3); //notification
        tabHost.addTab(spec);*/

        TabHost tabHost=findViewById(R.id.host);
        tabHost.setup(this.getLocalActivityManager());

        spec= tabHost.newTabSpec("tab1");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(),R.drawable.mypage_icon,null));
        spec.setContent(R.id.tab1);
        tabHost.addTab(spec);


        spec= tabHost.newTabSpec("tab2");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(),R.drawable.notice_icon,null));
        Intent intent1=new Intent(this,Notification.class); //Notification
        spec.setContent(intent1);
        tabHost.addTab(spec);

        spec= tabHost.newTabSpec("tab3");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(),R.drawable.setting_icon,null));
        Intent intent=new Intent(this,SettingsMain.class);  //SettingsMain
        spec.setContent(intent);
        tabHost.addTab(spec);

    }
    public void floatingButton_addDic(View view){
        //Toast.makeText(getApplicationContext(),"add Dic 클릭",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this, ChooseDictionary.class);
        startActivityForResult(intent,3000);

    }
    public void floatingButton_editScreen(View view){
        Toast.makeText(getApplicationContext(),"edit Dic 클릭",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String text="null";
        if(requestCode==3000) {
            if (resultCode==1) {    //나만의 단어장 추가
                text="나만의 단어장";
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();

            }else if (resultCode==2) {  //min 수능 단어장 추가
                text="min 수능 단어장";
                voca = CSAT;
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
            }
            else if (resultCode==3) {  //min 토익 단어장 추가
                text="min 토익 단어장";
                voca = TOEIC;
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
            }
            else if (resultCode==4) {  //공유된 단어장 추가
                text="공유된 단어장";
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
            }else if (resultCode==5) {  //토플 단어장 추가
                text="min TOEFL 단어장";
                voca = TOEFL;
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
            }else if (resultCode==6) {  //텝스 단어장 추가
                text="min TEPS 단어장";
                voca = TEPS;
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
            }else if (resultCode==7) {  //텝스 단어장 추가
                text="min 초/중 필수 단어장";
                voca = EleMid;
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
            }else if (resultCode==-1) {  //취소버튼(단어장추가안해)
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode==3000&&resultCode!=-1){
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String name=(String) listview.getItemAtPosition(i);
                    //Toast.makeText(getApplicationContext(),"ShortClick : "+name,Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    ComponentName componentName=new ComponentName("com.example.min","com.example.min.ClickDictioinary");
                    intent.setComponent(componentName);
                    intent.putExtra("dicName",name);
                    intent.putExtra("dicColor",itemColor.get(i));
                    intent.putExtra("voca", voca);
                    startActivity(intent);
                }
            });
            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String name=(String) listview.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(),"LongClick : "+name,Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder ad=new AlertDialog.Builder(MainActivity.this);
                    ad.setIcon(R.mipmap.ic_launcher);//삭제 이미지
                    ad.setTitle("단어장 삭제");
                    ad.setMessage("단어장을 삭제하시겠습니까?");
                    ad.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            items.remove(position);
                            itemColor.remove(position);
                            listview.setAdapter(adapter);
                            dialog.dismiss();
                        }
                    });
                    ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();

                    return true;
                }
            });
            adapter = new ListViewAdapter(getApplicationContext(), items);
            listview.setAdapter(adapter);
            addItem(text);
        }
        if(requestCode==4000){
            if (resultCode==-1) {  //result_ok
                //Toast.makeText(MainActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
                String itemColor=data.getStringExtra("color");
                //Toast.makeText(MainActivity.this, itemColor, Toast.LENGTH_LONG).show();
                changeItemColor(ListViewAdapter.getSelectedDic(),itemColor);
            }
        }
    }
    public static void removeItem(int remove){
        items.remove(remove);
        itemColor.remove(remove);
        listview.setAdapter(adapter);
    }
    public static void addItem(String item){
        items.add(item);
        itemColor.add("white");
        listview.setAdapter(adapter);
    }
    public static void changeItemColor(int change,String color){
        switch (color){
            case "YELLOW":
                listview.getChildAt(change).setBackgroundColor(ContextCompat.getColor(context,R.color.pastel_yellow));
                itemColor.set(change,color);
                break;
            case "PINK":
                listview.getChildAt(change).setBackgroundColor(ContextCompat.getColor(context,R.color.pastel_pink));
                itemColor.set(change,color);
                break;
            case "GREEN":
                listview.getChildAt(change).setBackgroundColor(ContextCompat.getColor(context,R.color.pastel_green));
                itemColor.set(change,color);
                break;
            case "BLUE":
                listview.getChildAt(change).setBackgroundColor(ContextCompat.getColor(context,R.color.pastel_blue));
                itemColor.set(change,color);
                break;
            case "PURPLE":
                listview.getChildAt(change).setBackgroundColor(ContextCompat.getColor(context,R.color.pastel_purple));
                itemColor.set(change,color);
                break;
            case "GRAY":
                listview.getChildAt(change).setBackgroundColor(ContextCompat.getColor(context,R.color.pastel_gray));
                itemColor.set(change,color);
                break;
        }

    }

}

class ListViewAdapter extends ArrayAdapter<String> {
    ArrayList<String> list;
    static Context context;
    static int selectedDic;
    public ListViewAdapter(Context context, ArrayList<String> items){
        super(context,R.layout.listview_custom,items);
        this.context=context;
        list=items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.listview_custom,null);
        }
        context=parent.getContext();

        TextView name=convertView.findViewById(R.id.dicName);
        name.setText(list.get(position));

        ImageView settingBtn=convertView.findViewById(R.id.dicSetting);settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goto SettingDic.class
                //Toast.makeText(context, "click : settingBtn", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(context,SettingDictionary.class);
                ((Activity) context).startActivityForResult(intent,4000);
                selectedDic=position;
            }
        });
        return convertView;
    }
    static public int getSelectedDic(){
        return selectedDic;
    }

}
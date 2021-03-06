package com.example.min;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signupscreen02 extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase 인증
    private FirebaseFirestore db; // Firestore Database
    private EditText etEmail, etPwd, etName;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupscreen02);

        Intent getSignUp01Info = getIntent();
        String Job = getSignUp01Info.getStringExtra("Job");
        String Dic = getSignUp01Info.getStringExtra("Dic");
        String Affiliation = getSignUp01Info.getStringExtra("Affiliation");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPwd = findViewById(R.id.et_pwd);
        etName = findViewById(R.id.et_name);

        btnRegister = findViewById(R.id.btn_register);

        /*Intent getSignUp01Info=getIntent();
        String job=getSignUp01Info.getStringExtra("job");
        String dic=getSignUp01Info.getStringExtra("dic");
        //Toast.makeText(signupscreen02.this,job+dic,Toast.LENGTH_SHORT).show();*/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 회원가입 처리 시작
                String strEmail = etEmail.getText().toString();
                String strPwd = etPwd.getText().toString();
                String strName = etName.getText().toString();

                // Firebase 인증 절차
                auth.createUserWithEmailAndPassword(strEmail, strPwd)
                        .addOnCompleteListener(signupscreen02.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    UserAccount account = new UserAccount();
                                    account.setIdToken(firebaseUser.getUid());
                                    account.setEmailId(firebaseUser.getEmail());
                                    account.setPassword(strPwd);
                                    account.setName(strName);
                                    account.setJob(Job);
                                    account.setDic(Dic);
                                    account.setAffiliation(Affiliation);

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("ID", account.getEmailId());
                                    user.put("Password", account.getPassword());
                                    user.put("Name", account.getName());
                                    user.put("IDToken", account.getIdToken());
                                    user.put("Job", account.getJob());
                                    user.put("Dic", account.getDic());
                                    user.put("Affiliation", account.getAffiliation());

                                    db.collection("UserInfo")
                                            .document(account.getIdToken())
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                    Toast.makeText(signupscreen02.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent();
                                                    ComponentName componentName = new ComponentName("com.example.min","com.example.min.signupscreen03");
                                                    intent.setComponent(componentName);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(signupscreen02.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(signupscreen02.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
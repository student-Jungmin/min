package com.example.min;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class SettingsModifyUserProfile extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    AppCompatEditText change_name;
    AppCompatEditText change_affiliation;
    AppCompatButton btn_apply;
    boolean isClickChangeProfile;

    private final int GET_GALLERY_IMAGE = 200;
    private String imgName = "osz.png";
    private ImageView profile_image;
    private Button btn_change_profile_image;
    private Bitmap imgBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_modify_user_profile);

        profile_image = findViewById(R.id.profile_image);
        btn_change_profile_image = findViewById(R.id.btn_change_profile_image);
        isClickChangeProfile = false;

        try {
            String imgpath = getCacheDir() + "/" + imgName;   // ?????? ???????????? ???????????? ?????? ????????? ??????
            Bitmap bm = BitmapFactory.decodeFile(imgpath);
            profile_image.setImageBitmap(bm);   // ?????? ???????????? ????????? ???????????? ??????????????? ???
        } catch (Exception e) {

        }

        btn_change_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);

                isClickChangeProfile = true;
            }
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();

        DocumentReference docRef = db.collection("UserInfo").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        change_name.setHint(document.get("Name").toString());
                        change_affiliation.setHint(document.get("Affiliation").toString());
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with", task.getException());
                }
            }
        });


        change_name = findViewById(R.id.change_name);
        change_affiliation = findViewById(R.id.change_affiliation);
        btn_apply = findViewById(R.id.apply);
    }

    public void back_to_settings_main(View view){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.example.min", "com.example.min.SettingsMain");
        intent.setComponent(componentName);
        startActivity(intent);
    }

    public void apply_modification(View view){

        String name = change_name.getText().toString();
        String affiliation = change_affiliation.getText().toString();

        saveBitmapToJpeg(imgBitmap);

        UserAccount account = new UserAccount();
        account.setName(name);
        account.setAffiliation(affiliation);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        DocumentReference updateRef = db.collection("UserInfo").document(firebaseUser.getUid());

        updateRef
                .update("Name", account.getName(), "Affiliation", account.getAffiliation())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error Updating document", e);
                    }
                });

        change_name.setHint(change_name.getText().toString());
        change_affiliation.setHint(change_affiliation.getText().toString());
        Toast.makeText(getApplicationContext(), "?????????????????????.",Toast.LENGTH_SHORT).show();

        change_name.setText(null);
        change_affiliation.setText(null);
        isClickChangeProfile = false;
    }

    public void cancel_modification(View view){

        Toast.makeText(getApplicationContext(), "?????????????????????.",Toast.LENGTH_SHORT).show();

        isClickChangeProfile = false;
        change_name.setText(null);
        change_affiliation.setText(null);

        Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + imgName);
        profile_image.setImageBitmap(bitmap);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            ContentResolver resolver = getContentResolver();
            try {
                InputStream inputStream = resolver.openInputStream(selectedImageUri);
                imgBitmap = BitmapFactory.decodeStream(inputStream);
                profile_image.setImageBitmap(imgBitmap);
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    //????????? ???????????? ??????
    public void saveBitmapToJpeg(Bitmap bitmap) {   // ????????? ????????? ?????? ???????????? ??????
        File tempFile = new File(getCacheDir(), imgName);    // ?????? ????????? ?????? ??????
        try {
            tempFile.createNewFile();   // ???????????? ??? ????????? ????????????
            FileOutputStream out = new FileOutputStream(tempFile);  // ????????? ??? ??? ?????? ???????????? ????????????
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress ????????? ????????? ???????????? ???????????? ????????????
            out.close();    // ????????? ????????????
        } catch (Exception e) {
        }
    }
}

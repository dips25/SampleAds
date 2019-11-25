package com.example.sampleads.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sampleads.MainActivity;
import com.example.sampleads.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PostAdActivity extends AppCompatActivity {

    private static final int VIDEO_CAPTURE = 100 ;
    private static final String TAG = PostAdActivity.class.getSimpleName();
    ProgressDialog progressDialog;

    VideoView vdv;
    EditText title;
    EditText description;
    LinearLayout section_category;
    TextView txtcategory;
    ImageView add;
    private MediaController mediaController;
    private Uri videoUri;
    Button post;
    private StorageReference mStorageRef;
    private UploadTask uploadTask;
    FirebaseFirestore db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postad);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting Ad.Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        String catitem = intent.getStringExtra("item");

        add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent,VIDEO_CAPTURE);
            }
        });
        vdv = (VideoView) findViewById(R.id.vdv);
        vdv.setVisibility(View.INVISIBLE);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);

        section_category = (LinearLayout) findViewById(R.id.section_category);
        txtcategory = (TextView) findViewById(R.id.txt_category);

        post = (Button) findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updatePost();

            }
        });


        if (catitem==null){

            txtcategory.setVisibility(View.GONE);


        }else {

            txtcategory.setVisibility(View.VISIBLE);
            txtcategory.setText(catitem);
        }



        section_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostAdActivity.this,ProductListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updatePost() {

        if (videoUri!=null){

            progressDialog.show();

            final StorageReference ref = mStorageRef.child("images/"+UUID.randomUUID().toString()+videoUri.getLastPathSegment()+".mp4");
            uploadTask = ref.putFile(videoUri);

            final Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){

                        throw Objects.requireNonNull(task.getException());
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){

                        String url = task.getResult().toString();

                        Long tsLong = System.currentTimeMillis() / 1000;
                        String timestamp = tsLong.toString();


                        if (url!=null){

                            Map map = new HashMap();
                            map.put("id", UUID.randomUUID().toString());
                            map.put("title",title.getText().toString());
                            map.put("description",description.getText().toString());
                            map.put("category",txtcategory.getText().toString());
                            map.put("video",ref.getName());
                            map.put("timestamp",timestamp);

                            db.collection("Posts")
                                    .add(map)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                                            progressDialog.dismiss();

                                            Intent intent = new Intent(PostAdActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                            Toast.makeText(PostAdActivity.this, "You Ad is live.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            progressDialog.dismiss();
                                            Toast.makeText(PostAdActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(PostAdActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }else {

            Toast.makeText(this, "Upload media to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_CAPTURE) {

            if (resultCode == RESULT_OK) {

                if (data != null) {
                    vdv.setVisibility(View.VISIBLE);
                    add.setVisibility(View.GONE);
                    videoUri = data.getData();
                    if (videoUri != null) {
                        Toast.makeText(this, "Video:" + videoUri.toString(), Toast.LENGTH_SHORT).show();
                    }
                    vdv.setVideoURI(videoUri);
                    mediaController = new MediaController(PostAdActivity.this);
                    vdv.setMediaController(mediaController);
                    mediaController.setAnchorView(vdv);
                    vdv.start();

                }
            }
        }
    }
}

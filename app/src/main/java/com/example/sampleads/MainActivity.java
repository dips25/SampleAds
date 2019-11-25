package com.example.sampleads;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleads.Models.Posts;
import com.example.sampleads.activities.PostAdActivity;
import com.example.sampleads.activities.SignUpActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int VIDEO_CAPTURE = 100 ;

    Uri videoUri;
    RecyclerView videoView;
    MediaController mediaController;
    FirebaseFirestore db;
    Query query;
    FirestoreRecyclerAdapter adapter;
    StorageReference storageref;
    StorageReference vidreference;
    ProgressBar progressBar;
    LinearLayout bottomsheet;
    BottomSheetBehavior sheetBehavior;

    ImageView video;
    ImageView camera;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storageref = FirebaseStorage.getInstance().getReference();

        bottomsheet = (LinearLayout) findViewById(R.id.bottomsheet);
        sheetBehavior = BottomSheetBehavior.from(bottomsheet);
        sheetBehavior.setPeekHeight(300);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);



        FloatingActionButton fab = findViewById(R.id.fab);
        videoView = (RecyclerView) findViewById(R.id.video_view);
        videoView.setHasFixedSize(true);

        video = (ImageView) findViewById(R.id.video);
        camera = (ImageView) findViewById(R.id.camera);

        videoView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        query = db.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
        setupRecyclerView(query);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this, PostAdActivity.class);
                startActivity(intent);


            }
        });







        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){

                    sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

                }else {

                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

            }
        });
    }

    private void setupRecyclerView(Query query) {

        FirestoreRecyclerOptions<Posts> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Posts>()
                .setQuery(query, new SnapshotParser<Posts>() {
                    @NonNull
                    @Override
                    public Posts parseSnapshot(@NonNull DocumentSnapshot snapshot) {

                        return snapshot.toObject(Posts.class);
                    }
                }).setLifecycleOwner(this)
                .build();

        adapter = new FirestoreRecyclerAdapter<Posts,PostHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final PostHolder holder, int position, @NonNull Posts model) {

                String title = model.getTitle();
                String description = model.getDescription();
                String videouri = model.getVideo();
                vidreference = storageref.child("images/"+videouri);


                try {
                    final File localfile = File.createTempFile("images","mp4");
                    vidreference.getFile(localfile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){

                                if (holder.progressBar.isShown()){

                                    holder.progressBar.setVisibility(View.GONE);
                                }

                                holder.videoView.requestFocus();
                                holder.videoView.setVideoURI(Uri.fromFile(localfile));

                                mediaController = new MediaController(MainActivity.this);
                                holder.videoView.setMediaController(mediaController);
                                mediaController.setAnchorView(holder.videoView);
                                holder.videoView.start();



                            }



                        }
                    });

                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Error:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }



                holder.title.setText(title);
                holder.description.setText(description);


            }



            @NonNull
            @Override
            public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_ad,parent,false);

                return new PostHolder(view) {
                };
            }
        };

        videoView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==VIDEO_CAPTURE ){

            if (resultCode==RESULT_OK){

                if (data != null) {
                    videoUri = data.getData();
                    Toast.makeText(this, "Video:" + videoUri.toString(), Toast.LENGTH_SHORT).show();


                }


            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser==null){

            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

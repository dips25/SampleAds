package com.example.sampleads;

import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

class PostHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView description;
    VideoView videoView;
    MediaController mediaController;
    ProgressBar progressBar;

    public PostHolder(View cardView) {
        super(cardView);


        videoView = (VideoView) cardView.findViewById(R.id.singlevideo);
        title = (TextView) cardView.findViewById(R.id.singletitle);
        description = (TextView) cardView.findViewById(R.id.singledesc);
        progressBar = (ProgressBar) cardView.findViewById(R.id.progressvideo);








    }
}

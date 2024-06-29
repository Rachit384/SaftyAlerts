package com.example.saftyalerts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saftyalerts.Fragments.PostFragment;
import com.example.saftyalerts.Model.Post;
import com.example.saftyalerts.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TEXT = 1;
    private static final int VIEW_TYPE_IMAGE = 2;
    private static final int VIEW_TYPE_BOTH = 3;

    private Context context;
    private List<Post> postList;



    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @Override
    public int getItemViewType(int position) {
        Post post = postList.get(position);
        if (post.getText() != null && post.getImageUrl() != null) {
            return VIEW_TYPE_BOTH;
        } else if (post.getText() != null) {
            return VIEW_TYPE_TEXT;
        } else {
            return VIEW_TYPE_IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_TEXT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_text, parent, false);
            return new TextPostViewHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_image, parent, false);
            return new ImagePostViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_both, parent, false);
            return new BothPostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = postList.get(position);
        if (holder instanceof TextPostViewHolder) {
            ((TextPostViewHolder) holder).bind(post);
        } else if (holder instanceof ImagePostViewHolder) {
            ((ImagePostViewHolder) holder).bind(post);
        } else {
            ((BothPostViewHolder) holder).bind(post);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView;
        private ImageView profileImageView;
        private TextView postTextView;

        public TextPostViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            postTextView = itemView.findViewById(R.id.postTextView);
        }

        public void bind(Post post) {
            usernameTextView.setText(post.getuser());
            postTextView.setText(post.getText());

            // Load profile image using Glide or Picasso
            Glide.with(context)
                    .load(post.getUserProfileImage())
                    .placeholder(R.drawable.place) // Placeholder image while loading
                    .into(profileImageView);
        }
    }

    public class ImagePostViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView;
        private ImageView profileImageView;
        private ImageView postImageView;

        public ImagePostViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            postImageView = itemView.findViewById(R.id.postImageView);
        }

        public void bind(Post post) {
            usernameTextView.setText(post.getuser());

            // Load profile image using Glide or Picasso
            Glide.with(context)
                    .load(post.getUserProfileImage())
                    .placeholder(R.drawable.place) // Placeholder image while loading
                    .into(profileImageView);

            // Load post image using Glide or Picasso
            Glide.with(context)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.place) // Placeholder image while loading
                    .into(postImageView);
        }
    }

    public class BothPostViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView;
        private ImageView profileImageView;
        private TextView postTextView;
        private ImageView postImageView;

        public BothPostViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            postTextView = itemView.findViewById(R.id.postTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
        }

        public void bind(Post post) {
            usernameTextView.setText(post.getuser());
            postTextView.setText(post.getText());

            // Load profile image using Glide or Picasso
            Glide.with(context)
                    .load(post.getUserProfileImage())
                    .placeholder(R.drawable.place) // Placeholder image while loading
                    .into(profileImageView);

            // Load post image using Glide or Picasso
            Glide.with(context)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.place) // Placeholder image while loading
                    .into(postImageView);
        }
    }
}


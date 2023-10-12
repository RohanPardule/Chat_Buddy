package com.example.chatbuddy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbuddy.ChatActivity;
import com.example.chatbuddy.R;
import com.example.chatbuddy.model.UserModel;
import com.example.chatbuddy.util.AndroidUtil;
import com.example.chatbuddy.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel,SearchUserRecyclerAdapter.UserModelViewHolder> {

Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options , Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
holder.usernameText.setText(model.getUsername());
holder.phoneText.setText(model.getPhone());
if ((model.getUserId().equals(FirebaseUtil.currentUserId())))
{
    holder.usernameText
            .setText(model.getUsername()+" (ME)");
}


holder.itemView.setOnClickListener(v -> {
    //chat activity
    Intent intent=new Intent(context, ChatActivity.class);
    AndroidUtil.passUsermodelIntent(intent,model);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);

});


    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText,phoneText;
        ImageView profilePic;


        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText=itemView.findViewById(R.id.username_txt);
            phoneText=itemView.findViewById(R.id.phone_txt);
            profilePic=itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}

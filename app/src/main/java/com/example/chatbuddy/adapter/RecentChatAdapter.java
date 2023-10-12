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
import com.example.chatbuddy.ChatroomModel;
import com.example.chatbuddy.R;

import com.example.chatbuddy.model.UserModel;
import com.example.chatbuddy.util.AndroidUtil;
import com.example.chatbuddy.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.auth.User;

public class RecentChatAdapter extends FirestoreRecyclerAdapter<ChatroomModel,RecentChatAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options , Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds()).get().addOnCompleteListener(task -> {
    if (task.isSuccessful())
    {
        boolean lastMsgSentByMe=model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

        UserModel otherUserModel=task.getResult().toObject(UserModel.class);
        holder.usernameText.setText(otherUserModel.getUsername());
        if (lastMsgSentByMe)
        {
            holder.lastMsgText.setText("You : "+model.getLastMessage());
        }
        else
        {
            holder.lastMsgText.setText(model.getLastMessage());
        }

        holder.lastMsgTime.setText(FirebaseUtil.timestampTOString(model.getLastMessageTimeStamp()));


        holder.itemView.setOnClickListener(v -> {
            //chat activity
            Intent intent=new Intent(context, ChatActivity.class);
            AndroidUtil.passUsermodelIntent(intent,otherUserModel);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        });
    }


});



    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText,lastMsgText,lastMsgTime;
        ImageView profilePic;


        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText=itemView.findViewById(R.id.username_txt);
            lastMsgText=itemView.findViewById(R.id.last_msg_txt);
            lastMsgTime=itemView.findViewById(R.id.last_time_txt);
            profilePic=itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}


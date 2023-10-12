package com.example.chatbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chatbuddy.adapter.ChatRecyclerAdapter;
import com.example.chatbuddy.adapter.SearchUserRecyclerAdapter;
import com.example.chatbuddy.model.ChatMessageModel;
import com.example.chatbuddy.model.UserModel;
import com.example.chatbuddy.util.AndroidUtil;
import com.example.chatbuddy.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
ChatRecyclerAdapter adapter;
    String chatroomId;
    ChatroomModel chatroomModel;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser= AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId= FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        messageInput=findViewById(R.id.chat_msg_input);
        sendMessageBtn=findViewById(R.id.message_send_btn);
        backBtn=findViewById(R.id.chat_back_btn);
        otherUsername=findViewById(R.id.other_username);
        recyclerView=findViewById(R.id.chat_recyclerView);

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener(v -> {
            String message=messageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        });
        getOrCreateChatroomModel();
        setUpChatRecyclerView();
    }

    private void setUpChatRecyclerView() {
        Query query= FirebaseUtil.getChatrromMessageReference(chatroomId).orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options=new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter=new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    void sendMessageToUser(String message) {

       chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
       chatroomModel.setLastMessageTimeStamp(Timestamp.now());
       chatroomModel.setLastMessage(message);
       ChatMessageModel chatMessageModel=new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());
       FirebaseUtil.getChatRoomReference(chatroomId).set(chatroomModel);

       FirebaseUtil.getChatrromMessageReference(chatroomId).add(chatMessageModel).addOnCompleteListener(
               new OnCompleteListener<DocumentReference>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentReference> task) {
                       if (task.isSuccessful()){
                           messageInput.setText("");
                       }
                   }
               }
       );
    }

    void getOrCreateChatroomModel(){


        FirebaseUtil.getChatRoomReference(chatroomId).get().addOnCompleteListener(task -> {
if(task.isSuccessful())
{
chatroomModel=task.getResult().toObject(ChatroomModel.class);
if(chatroomModel==null)
{
    chatroomModel=new ChatroomModel(chatroomId, Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()
    ), Timestamp.now(),"");
}
FirebaseUtil.getChatRoomReference(chatroomId).set(chatroomModel);
}
        });
    }
}
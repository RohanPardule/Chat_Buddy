package com.example.chatbuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatbuddy.model.UserModel;
import com.example.chatbuddy.util.AndroidUtil;
import com.example.chatbuddy.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {
    ImageView profilePic;
    EditText phoneInput,userNameInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    UserModel currentUserModel;

    public ProfileFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View view= inflater.inflate(R.layout.fragment_profile, container, false);
    profilePic=view.findViewById(R.id.profile_imgView);
        userNameInput=view.findViewById(R.id.profile_username);
        phoneInput=view.findViewById(R.id.profile_phone);
        updateProfileBtn=view.findViewById(R.id.profile_update_btn);
        progressBar=view.findViewById(R.id.profile_progress_bar);
        logoutBtn=view.findViewById(R.id.logout_btn);
        getUserData();

        updateProfileBtn.setOnClickListener(v -> {
updateBtnClick();

        });


        logoutBtn.setOnClickListener(v -> {
            FirebaseUtil.logout();
            Intent intent=new Intent(getContext(),SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    return view;
    }

    private void getUserData() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
currentUserModel=task.getResult().toObject(UserModel.class);

userNameInput.setText(currentUserModel.getUsername());
phoneInput.setText(currentUserModel.getPhone());
        });
    }

    void setInProgress(boolean inProgress)
    {
        if (inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
           updateProfileBtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    void updateBtnClick(){
        String newUsername=userNameInput.getText().toString();
        //checking username is valid or not
        if (newUsername.isEmpty() || newUsername.length()<3)
        {
            userNameInput.setError("Username length should be at least 3 chars");
            return;

        }

        currentUserModel.setUsername(newUsername);
        setInProgress(true);
        updateToFirestore();

    }
    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful())
            {
                AndroidUtil.showToast(getContext(),"Updated Successfully");
            }
            else
            {
                AndroidUtil.showToast(getContext(),"Updated Failed");
            }
        });

    }

}
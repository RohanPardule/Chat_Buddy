package com.example.chatbuddy.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.chatbuddy.model.UserModel;

public class AndroidUtil {

    public static  void showToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static  void passUsermodelIntent(Intent intent, UserModel model)
    {
        intent.putExtra("username",model.getUsername());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
    }

    public static  UserModel getUserModelFromIntent(Intent intent)
    {
        UserModel model=new UserModel();
        model.setUsername(intent.getStringExtra("username"));
        model.setPhone(intent.getStringExtra("phone"));
        model.setUserId(intent.getStringExtra("userId"));
        return model;
    }
}

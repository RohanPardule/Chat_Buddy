package com.example.chatbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbuddy.util.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOTPActivity extends AppCompatActivity {
    String phoneNumber;
    Long timeoutSeconds=60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    EditText otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    TextView ressendOtpTextview;

    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otpactivity);

        otpInput=findViewById(R.id.login_otp);
        nextBtn=findViewById(R.id.login_next_btn);
        progressBar=findViewById(R.id.login_otp_progress_bar);
        ressendOtpTextview=findViewById(R.id.resend_otp_text);

//getting phone number from intent
        phoneNumber=getIntent().getExtras().getString("phone");

//send otp method called
        sendOtp(phoneNumber,false);


 //next button click listener
        nextBtn.setOnClickListener(v -> {
String enteredOtp=otpInput.getText().toString();
PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
signIn(credential);
setInProgress(true);

        });

  //resend Otp
  ressendOtpTextview.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          sendOtp(phoneNumber,true);
      }
  });



    }

    //method for sending otp
    void sendOtp(String phoneNumber,boolean isResend)
     {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder=PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                        setInProgress(false); }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode=s;
                        resendingToken=forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(),"OTP verification successfully");
                        setInProgress(false);}
                });

        if (isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }
        else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build() );
        }

    }



    void setInProgress(boolean inProgress)
    {
        if (inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }



    void signIn(PhoneAuthCredential phoneAuthCredential){

        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Intent intent=new Intent(LoginOTPActivity.this,LoginUsernameActivity.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);

                }
                else {
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                }
            }
        });
    }


 void startResendTimer(){
        ressendOtpTextview.setEnabled(false);
     Timer timer=new Timer();
     timer.scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
             timeoutSeconds--;
             ressendOtpTextview.setText("Resend OTP in "+timeoutSeconds+" seconds");
             if(timeoutSeconds<=0)
             {
                 timeoutSeconds=60L;
                 timer.cancel();
                 runOnUiThread(()->{
                     ressendOtpTextview.setEnabled(true);
                 });
             }
         }
     },0,1000);
 }
}
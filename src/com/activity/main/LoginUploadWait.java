package com.activity.main;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoginUploadWait extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.login_uploadwait);
		final Intent it = new Intent(this, LoginRegisterSuccess.class); //��Ҫת���Activity  
        Timer timer = new Timer();
        TimerTask task = new TimerTask(){
        	@Override
        	public void run(){
        		finish();
        		startActivity(it); //ִ��
        	}
        };
        timer.schedule(task, 1000 * 1); //1���
	}
}

package com.activity.main;

import com.activity.context.MapApplication;
import com.function.login.LoginFunction;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	private EditText accnum;
	private EditText pwd;
	
	private ImageButton login;
	private ImageButton register;
	private ImageButton reset;
	
	private TextView forgetpwd;
	
	private Intent loginintent;
	private Intent registerintent;
	private Intent forgetintent;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_login);
		
		accnum = (EditText) findViewById(R.id.AccountNumber);
		pwd = (EditText) findViewById(R.id.Password);
		
		forgetpwd = (TextView) findViewById(R.id.ForgetPwd);
		
		login = (ImageButton) findViewById(R.id.login);
		register = (ImageButton) findViewById(R.id.register);
		reset = (ImageButton) findViewById(R.id.reset);
		
		loginintent = new Intent(Login.this,LoginWait.class);
		registerintent = new Intent(Login.this,LoginRegister.class);
		forgetintent = new Intent(Login.this,LoginFindPassword.class);
		
		forgetpwd.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				// TODO Auto-generated method stub
				forgetpwd.setTextColor(Color.MAGENTA);
				new Handler().postDelayed(new Runnable(){  
				     public void run(){  
				     //execute the task
				    	 startActivity(forgetintent);
				     }  
				  }, 100);								
			}
		});
		
		login.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				LoginFunction login = new LoginFunction();
				String username = accnum.getText().toString();
				String password = pwd.getText().toString();
				
				//startActivity(loginintent);
				//finish();
				
				if(login.taken(username,password)){
					// TODO ���û�ID������
					
					String userId = new LoginFunction().Loginserve(username,password);
					MapApplication data = ((MapApplication)getApplicationContext()); 
					data.setUserId(userId);
					data.setUserName(username);
					//Toast.makeText(Login.this, userId+"", Toast.LENGTH_SHORT).show();
					
					startActivity(loginintent);
					finish();
				}
				else{
					Toast.makeText(Login.this, "�û������������!", Toast.LENGTH_LONG).show();
					/////
					//startActivity(loginintent);
					//finish();
				}
			}
		});
		
		register.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				//finish();
				startActivity(registerintent);
			}
		});
		
		reset.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				accnum.setText("");
				pwd.setText("");
			}
		});				
	}

	public boolean taken(String userid, String password) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    // ���¼����Ϸ��ذ�ť   
	    if (keyCode == KeyEvent.KEYCODE_BACK){
	    	new AlertDialog.Builder(this)
	    	.setTitle("ȷ���˳�")
	    	.setMessage("��ȷ��Ҫ�˳���") 
	    	.setNegativeButton("ȡ��",null)
	    	.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}    		
	    	}).show();
	    }
		return true;
	}
}

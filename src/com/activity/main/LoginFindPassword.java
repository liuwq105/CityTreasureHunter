package com.activity.main;

import com.function.findpassword.GetpassWord;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class LoginFindPassword extends Activity{
	private EditText accnum;
	private EditText question;
	private EditText answer;
		
	private ImageButton ok;
	private ImageButton back;
	
	//private TextView show;
	private TextView change;
	
	public static TextView show;
	public static Builder adialog;
	int i;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.login_findpasspord);
		
		accnum = (EditText) findViewById(R.id.AccountNumber);
		question = (EditText) findViewById(R.id.Question);
		answer = (EditText) findViewById(R.id.Answer);
		
		change = (TextView) findViewById(R.id.Change);
		ok = (ImageButton) findViewById(R.id.OK);
		back = (ImageButton) findViewById(R.id.Back);
		
		show = (TextView) findViewById(R.id.Textview);				
		question.setText("�����׵������ǣ�");
		change.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				change.setTextColor(Color.MAGENTA);
				new Handler().postDelayed(new Runnable(){  
				     public void run(){  
				     //execute the task
				    	 Toast.makeText(LoginFindPassword.this, "��������", Toast.LENGTH_SHORT).show();
				    	 if(question.getText().toString().equals("�����׵������ǣ�"))
							{
								i=1;
							}
							if(question.getText().toString().equals("��ĸ�׵������ǣ�"))
							{
								i=2;
							}
							
							if(question.getText().toString().equals("������ѧУ�������ǣ�"))
							{
								i=3;
							}
							switch(i) {  
							case 1:  question.setText("��ĸ�׵������ǣ�"); break; 
							case 2:  question.setText("������ѧУ�������ǣ�"); break; 
							case 3:  question.setText("�����׵������ǣ�"); break; 
							default:break; 
							}
				     }  
				  }, 100);				
			}
		});
		
		ok.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//show.setText("��������Ϊ��������������");
				//show.setText("�𰸴���");
				//����ش���ȷ�����첽AsyncTask����ʾ�Ի���
				adialog=new AlertDialog.Builder(LoginFindPassword.this);
	    		adialog.setTitle("��ʾ");
	    		adialog.setMessage("�����˺Ż�����ش��������������");
	    		adialog.setNegativeButton("ȷ��",new DialogInterface.OnClickListener() {
	    		     
	    		     @Override
	    		     public void onClick(DialogInterface dialog, int which) {
	    		   
	    		     }
	    		    });
	    		new GetpassWord().execute(accnum,question,answer);
	    		     
			}
		});
		
		back.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
























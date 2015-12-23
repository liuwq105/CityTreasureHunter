package com.activity.buildact;

import java.util.ArrayList;

import com.activity.context.MapApplication;
import com.activity.main.R;
import com.baidu.mapapi.map.OverlayItem;
import com.function.buildact.CreatActivity;
import com.function.buildact.CreatCheckpoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class BuildBrowsing extends Activity{
	private TextView activityname;
	private TextView activitystyle;
	private TextView brifintroduction;
	private TextView headinformation;	
	private TextView checknumber;
	private TextView checkinformation;
	private TextView warnnumber;
	private TextView warninformation;
	private TextView limittimeh;
	private TextView limittimem;
	private TextView lasttime;
	private TextView limittime;
	
	private ImageButton mback;
	private ImageButton mok;
	
	private Intent okintent;
	ArrayList<OverlayItem> overlayItems;
	private Intent backintent;
	
	ArrayList<String> question;//��������
	ArrayList<String> answer;//�����
	ArrayList<String> warning;//�������Ϣ
	

	private String sactivityname;//�����
	private String slasttime;//����ʱ��
	
	private String sbrifintroduction;//�ද���
	private String pro;//�����
	private String peoplenumber;//��������

	private String slimitTimemin;
	private String slimitTimeh;
	
	ArrayList<String> checkpointLongitude;//���㾭��
	ArrayList<String> checkpointLatitude;//����γ��
	ArrayList<String> warningpointLongitude;//����㾭��
	ArrayList<String> warningpointLatitude;//�����γ��
	String startLongitude;//��㾭��,��Ϊ˫�˻��ջ����Ϊ����ĵ�
	String startLatitude;//���γ��,��Ϊ˫�˻��ջ����Ϊ����ĵ�
	
	StringBuffer checkinfo;
	StringBuffer warninginfo;
	
	String userId;
	
 	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.build_browsing);
		
		BuildInformation.buildActivityList.add(this);
		
		MapApplication data = ((MapApplication)getApplicationContext()); 
		userId = data.getUserId();
		//Toast.makeText(BuildBrowsing.this, userId+"", Toast.LENGTH_SHORT).show();
		
		activityname = (TextView) findViewById(R.id.ActivityName);
		activitystyle = (TextView) findViewById(R.id.ActivityStyle);
		brifintroduction = (TextView) findViewById(R.id.BrifIntroduction);
		headinformation = (TextView) findViewById(R.id.HeadInformation);
		checknumber = (TextView) findViewById(R.id.CheckNumber);
		checkinformation = (TextView) findViewById(R.id.CheckInformation);
		warnnumber = (TextView) findViewById(R.id.WarnNumber);
		warninformation = (TextView) findViewById(R.id.WarnInformation);
		limittime= (TextView) findViewById(R.id.LimitTime);
		//limittimem = (TextView) findViewById(R.id.LimitTimemin);
		lasttime = (TextView) findViewById(R.id.LastTime);
		
		mok = (ImageButton) findViewById(R.id.mOK);
		mback = (ImageButton) findViewById(R.id.mBack);
		question=new ArrayList<String>();
		answer=new ArrayList<String>();
		warning=new ArrayList<String>();
		
		checkpointLongitude=new ArrayList<String>();
		checkpointLatitude=new ArrayList<String>();
		warningpointLongitude=new ArrayList<String>();
		warningpointLatitude=new ArrayList<String>();
		

		
		
        Bundle bundle=this.getIntent().getExtras();
		sactivityname=(String) bundle.getSerializable("sactivityname");
		slasttime=(String) bundle.getSerializable("slasttime");
		slimitTimemin=(String) bundle.getSerializable("slimitTimemin");
		slimitTimeh=(String) bundle.getSerializable("slimitTimeh");
		sbrifintroduction=(String) bundle.getSerializable("sbrifintroduction");
		pro=(String) bundle.getSerializable("pro");
		peoplenumber=(String) bundle.getSerializable("peoplenumber");
		
		startLongitude=(String) bundle.getSerializable("startLongitude");
		startLatitude=(String) bundle.getSerializable("startLatitude");
		
        
		
		answer=(ArrayList<String>) bundle.getSerializable("answer");
		question=(ArrayList<String>) bundle.getSerializable("question");
		warning=(ArrayList<String>) bundle.getSerializable("warning");
		
		checkpointLongitude=(ArrayList<String>) bundle.getSerializable("checkpointLongitude");
		checkpointLatitude=(ArrayList<String>) bundle.getSerializable("checkpointLatitude");
		warningpointLongitude=(ArrayList<String>) bundle.getSerializable("warningpointLongitude");
		warningpointLatitude=(ArrayList<String>) bundle.getSerializable("warningpointLatitude");
		
		
		activityname.setText(sactivityname);
		brifintroduction.setText(sbrifintroduction);
		headinformation.setText("���ȣ� "+startLatitude+"\n"+"  γ�ȣ� "+startLongitude);
		//limittimeh.setText(slimitTimeh);
		//limittimem.setText(slimitTimemin);
		limittime.setText(slimitTimeh+" h "+slimitTimemin+" min ");
		lasttime.setText(slasttime);
		
		checknumber.setText(question.size()+" ");
		if(pro.equals("����"))activitystyle.setText(pro+"(����������"+peoplenumber+")");
		else activitystyle.setText(pro);
		
		checkinfo = new StringBuffer();
		for(int i = 0; i < question.size(); i++){
	     checkinfo. append("��"+(i+1)+"������"+"\n�����ȣ� "+checkpointLongitude.get(i)+"  γ�ȣ� "+checkpointLatitude.get(i)+"��"+"\n");
		 checkinfo. append("����"+" :  "+question.get(i)+"\n");
		 checkinfo. append("��"+" :  "+answer.get(i)+"\n");
		}
		checkinformation.setText(checkinfo);
	
		warnnumber.setText(warning.size()+" ");
		warninginfo = new StringBuffer();
		for(int i = 0; i < warning.size(); i++){
			checkinfo. append("��"+(i+1)+"�������"+"\n�����ȣ� "+warningpointLongitude.get(i)+"  γ�ȣ� "+warningpointLatitude.get(i)+"��"+"\n");
			warninginfo. append("����"+(i+1)+"����: "+warning.get(i)+"\n");
		}
		warninformation.setText(warninginfo);
		
		okintent = new Intent(BuildBrowsing.this, BuildUpload.class);
		
		mok.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				// TODO Auto-generated method stub
				if(pro.equals("˫�˻���"))
				{
					//slimitTimeh="00";
					//slimitTimemin="00";
					peoplenumber="2";
					
				}
				if(pro.equals("������"))
				{
					peoplenumber="1";
				}
				// TODO ���û�ID
				String symbolcre=new CreatActivity().doInBackground(pro,sactivityname,userId,startLatitude,startLongitude,
						slasttime,slimitTimeh,slimitTimemin,question.size()+"",sbrifintroduction,peoplenumber);
				for(int i = 0; i < question.size(); i++){
					new CreatCheckpoint().doInBackground(pro,question.get(i),answer.get(i),checkpointLatitude.get(i),checkpointLongitude.get(i));
					}
				for(int i = 0; i < warning.size(); i++){
					new CreatCheckpoint().doInBackground(pro,warning.get(i),"-1",warningpointLatitude.get(i),warningpointLongitude.get(i));
				}
				
				okintent.putExtra("symbol",symbolcre.toString());
				okintent.putExtra("lat", startLatitude);
				okintent.putExtra("lon", startLongitude);
				okintent.putExtra("type", pro);
				
				startActivity(okintent);
				finish(); 
			}
		});
		
		mback.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View v){
				// TODO Auto-generated method stub
				finish();				
			}
		});
	}
}

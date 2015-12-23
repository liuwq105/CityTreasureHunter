package com.activity.rankinglist;

import java.util.ArrayList;

import com.activity.main.R;
import com.function.rankinglist.GetActivity;
import com.function.rankinglist.Getpersonal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class RankAct extends Activity implements OnScrollListener{
	private static final String TAG = "RankAct";
	private Button but_menu;
	private Button single;
	private Button team;
	private ListView listView;	
	private View moreView; //���ظ���ҳ��
	//private SimpleAdapter adapter;
	ArrayAdapter<String> adapter;
	//private ArrayList<HashMap<String, String>> listData;
	private ArrayList<String> listData;
	private ArrayList<String> listid=new ArrayList<String>();
	private LinearLayout progress;
	private int lastItem;
    private int count=20;
    private int countold;
	private PopupWindow m_popupWindow;
	private ImageView refresh;
	int offset=20;//���ʮ����ʼ
	int offsetperson=20;
	int limit=10;//������10��
	int symbol=1;//���������������ı�ʶ
	String sport="sport3";//Ĭ�ϵ�����
	String[][] activitys=null;
	String[][] person=null;
	private View contentView;
	GetActivity getact=new GetActivity();//��ȡ��������Ϣ
	Getpersonal getper=new Getpersonal();//��ȡ��������Ϣ
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.rankinglist_actlist);				
		init();
		setListener();
		
		listView = (ListView)findViewById(R.id.listView);
        moreView = getLayoutInflater().inflate(R.layout.joinact_load, null);
        //listData = new ArrayList<HashMap<String,String>>();
        listData = new ArrayList<String>();
        this.prepareData(); //׼������
        count = listData.size();
     
        //adapter = new SimpleAdapter(this, listData,R.layout.item,new String[]{"itemText"}, new int[]{R.id.itemText});
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listData);
        listView.addFooterView(moreView); //��ӵײ�view��һ��Ҫ��setAdapter֮ǰ��ӣ�����ᱨ��
        
        listView.setAdapter(adapter); //����adapter
        listView.setOnScrollListener(this); //����listview�Ĺ����¼�
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long index){
				// TODO Auto-generated method stub		
				String sname = listView.getItemAtPosition((int) index).toString();
				String sid = listid.get((int)index).toString();
				Toast.makeText(RankAct.this, "�������ǵ�" + sname+ "�� ���"+sid, Toast.LENGTH_SHORT).show();		
				Intent intent=new Intent(RankAct.this,MarkTen.class); 
				intent.putExtra("sid",sid);
				intent.putExtra("sname", sname);  
				intent.putExtra("sport",sport);
				startActivityForResult(intent, 1);
				//startActivity(intent);
			}			
		});				
	}
	
	private void prepareData(){ //׼������
		person=getper.doInBackground(20, 0);//�Ȼ�õ��˵�ǰʮ�����
    	for(int i=0;i<person.length;i++){
    		//HashMap<String, String> map = new HashMap<String, String>();
    		//map.put("itemText", person[i].toString());
    		//listData.add(map);
    		listid.add(person[i][0].toString());//���sid
    		listData.add(person[i][1].toString());
    	}   
    	count = listData.size();
    }
    
    private void loadMoreData(){ //���ظ�������
    	 count = adapter.getCount(); 
    	 if(symbol==1){
    		 person=getper.doInBackground(limit, offsetperson);//���˻��Ϣ
    		 for(int i=0;i<person.length;i++){
    	    		//HashMap<String, String> map = new HashMap<String, String>();
    	    		//map.put("itemText", person[i].toString());
    	    		//listData.add(map);
    			 listid.add(person[i][0].toString());//���sid
    			 listData.add(person[i][1].toString());
    	    	}
    		 listView.setAdapter(adapter); //����adapter
    		 offsetperson+=10;//�����������5
    	 }
    	 if(symbol==0){
				//offset=10;
				activitys=getact.doInBackground(limit, offset);//������Ϣ
				for(int i=0;i<activitys.length;i++){
    	    		//HashMap<String, String> map = new HashMap<String, String>();
    	    		//map.put("itemText", person[i].toString());
    	    		//listData.add(map);
					listid.add(person[i][0].toString());//���sid
					listData.add(activitys[i][1].toString());
    	    	}
				listView.setAdapter(adapter); //����adapter
				offset+=10;
			}
    	count = listData.size();
    }

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){		
		Log.i(TAG, "firstVisibleItem="+firstVisibleItem+"\nvisibleItemCount="+visibleItemCount+"\ntotalItemCount"+totalItemCount);	 	
		lastItem = firstVisibleItem + visibleItemCount - 1;  //��1����Ϊ������˸�addFooterView		
	}

	public void onScrollStateChanged(AbsListView view, int scrollState){ 
		//�����������ǣ������һ��item�����������ݵ�����ʱ�����и���
		if(lastItem == count&&scrollState == OnScrollListener.SCROLL_STATE_IDLE){ 
			Log.i(TAG, "������ײ�");
			moreView.setVisibility(View.VISIBLE);		 
		    mHandler.sendEmptyMessage(0);			 
		}		
	}
	//����Handler
	private Handler mHandler = new Handler(){
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg){
		if(msg.what==0){			     
				try{
					Thread.sleep(3000);
				}catch (InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    loadMoreData();  //���ظ������ݣ��������ʹ���첽����
			    adapter.notifyDataSetChanged();
			    moreView.setVisibility(View.GONE); 			    
			    if(count==countold){
			    	Toast.makeText(RankAct.this, "���ȫ����ʾ", 3000).show();
			        //listView.removeFooterView(moreView); //�Ƴ��ײ���ͼ
			    }
			    countold=count;//�Ѽ���������ݴ�����
			}
		};
	};
	
	@SuppressWarnings("deprecation")
	private void init(){
		contentView = getLayoutInflater().inflate(R.layout.rankinglist_popupmenu, null,true);
		but_menu = (Button) findViewById(R.id.but_menu);
		refresh = (ImageView) findViewById(R.id.Refresh);
		progress = (LinearLayout) findViewById(R.id.progress);
		single = (Button) contentView.findViewById(R.id.Single);
		team = (Button) contentView.findViewById(R.id.Team);
		
		m_popupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, true);

		m_popupWindow.setBackgroundDrawable(new BitmapDrawable());// �������ſ��Ե�����أ���������ťdismiss()popwindow
		m_popupWindow.setOutsideTouchable(true);
		m_popupWindow.setAnimationStyle(R.style.PopupAnimation);
	}
	
	private void setListener(){
		contentView.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				m_popupWindow.dismiss();
			}
		});
		but_menu.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				try {
					if (m_popupWindow.isShowing()){
						m_popupWindow.dismiss();
					}
					m_popupWindow.showAsDropDown(v);
				} catch (Exception e){
					Toast.makeText(RankAct.this, e.getMessage(),Toast.LENGTH_SHORT);
				}
			}
		});
		
		refresh.setOnClickListener(new View.OnClickListener(){
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refresh.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
				Toast.makeText(RankAct.this, "����ˢ���б�", Toast.LENGTH_SHORT).show();
				new Handler().postDelayed(new Runnable(){  
				     public void run(){  
				     //execute the task
				    	 refresh.setVisibility(View.VISIBLE);
				    	 progress.setVisibility(View.GONE);
				     }  
				}, 2000);	
				
				if(symbol==1){
					offsetperson=20;
					listData.removeAll(listData);//�Ƴ�����List���ݣ�
					listid.removeAll(listid);
					person=getper.doInBackground(20, 0);
					for(int i=0;i<person.length;i++)
					{
						listid.add(person[i][0].toString());
						listData.add(person[i][1].toString());
					}
					listView.setAdapter(adapter);
					count = listData.size();
				}
				if(symbol==0){
					offset=20;
					listData.removeAll(listData);//�Ƴ�����List���ݣ�
					listid.removeAll(listid);
					activitys=getact.doInBackground(20, 0);
					for(int i=0;i<activitys.length;i++)
					{
						listid.add(activitys[i][0].toString());
						listData.add(activitys[i][1].toString());
						//myList2.add(activitys[i]);
					}
					listView.setAdapter(adapter);
					count = listData.size();
				}
			}
		});
				
		single.setOnClickListener(new View.OnClickListener(){		
			public void onClick(View arg0) {
				m_popupWindow.dismiss();
				but_menu.setText("��������");
				symbol=1;//��������
				offsetperson=20;//���ݲ�ѯ���²�ѯ
				sport="sport3";
				listData.removeAll(listData);//�Ƴ�����List���ݣ�
				listid.removeAll(listid);
				person=getper.doInBackground(20, 0);
				for(int i=0;i<person.length;i++){
					listid.add(person[i][0].toString());
					listData.add(person[i][1].toString());
		    	}
				listView.setAdapter(adapter);
				count = listData.size();
			}
		});
		
		team.setOnClickListener(new View.OnClickListener(){		
			public void onClick(View arg0) {
				m_popupWindow.dismiss();
				but_menu.setText("��������");
				symbol=0;
				offset=20;
				sport="sport2";
				listData.removeAll(listData);//�Ƴ�����List����
				listid.removeAll(listid);
				activitys=getact.doInBackground(20, 0);//Ĭ����ʾ����������
				for(int i=0;i<activitys.length;i++)
				{
					listid.add(activitys[i][0].toString());
					listData.add(activitys[i][1].toString());
				}
				listView.setAdapter(adapter);
				count = listData.size();
				
			}
		});
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (m_popupWindow != null && m_popupWindow.isShowing()){
				m_popupWindow.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
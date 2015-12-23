package com.activity.joinact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.activity.main.R;
import com.activity.rankinglist.RankAct;
import com.function.joinact.Getofflinedpersonal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PracticeSearchAct extends Activity implements OnScrollListener{
	private static final String TAG = "PractiseAct";	
	private ImageView refresh;
	private ListView listView;	
	private View moreView; //���ظ���ҳ��
	//private SimpleAdapter adapter;
	ArrayAdapter<String> adapter;
	//private ArrayList<HashMap<String, String>> listData;
	private ArrayList<String> listData;
	private ArrayList<String> listid=new ArrayList<String>();//id
	private LinearLayout progress;
	private int lastItem;
    private int count=20;
    private int countold;
    int offset=20;//���ʮ����ʼ
	int offsetperson=20;
	int limit=10;//������10��
	String[][] offlinedperson=null;
	Getofflinedpersonal getoffper=new Getofflinedpersonal();
	
	public static List<Activity> practiceActivityList = new ArrayList<Activity>();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joinact_practise);
		
		practiceActivityList.add(this);
		
		refresh = (ImageView) this.findViewById(R.id.Refresh);		
		listView = (ListView)findViewById(R.id.listView);
        moreView = getLayoutInflater().inflate(R.layout.joinact_load, null);
        //listData = new ArrayList<HashMap<String,String>>();
        listData = new ArrayList<String>();
        progress = (LinearLayout) findViewById(R.id.progress);
        prepareData(); //׼������
        count = listData.size();    
        //adapter = new SimpleAdapter(this, listData,R.layout.joinact_item,new String[]{"itemText"}, new int[]{R.id.itemText});
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listData);
        
        listView.addFooterView(moreView); //��ӵײ�view��һ��Ҫ��setAdapter֮ǰ��ӣ�����ᱨ��        
        listView.setAdapter(adapter); //����adapter
        listView.setOnScrollListener(this); //����listview�Ĺ����¼�		
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long index) {
				// TODO Auto-generated method stub	
				String sname = listView.getItemAtPosition((int) index).toString();
				String sid = listid.get((int)index).toString();
				Toast.makeText(PracticeSearchAct.this, "�������ǵ�" + sname + "�� ���"+sid, Toast.LENGTH_SHORT).show();		
				Intent intent=new Intent(PracticeSearchAct.this,PracticeActInfo.class);				
				
				// TODO ���ID�ͻ��
				intent.putExtra("sid",sid);
				intent.putExtra("sname", sname);  
				startActivityForResult(intent, 1);
				
				//startActivity(intent);
			}			
		});		
		
		refresh.setOnClickListener(new View.OnClickListener(){			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				refresh.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
				Toast.makeText(PracticeSearchAct.this, "����ˢ���б�", Toast.LENGTH_SHORT).show();
				new Handler().postDelayed(new Runnable(){  
				     public void run(){  
				     //execute the task
				    	 refresh.setVisibility(View.VISIBLE);
				    	 progress.setVisibility(View.GONE);
				     }  
				  }, 2000);	
				offsetperson=20;
				listData.removeAll(listData);//�Ƴ�����List���ݣ�
				listid.removeAll(listid);
				offlinedperson=getoffper.doInBackground(20, 0);
				for(int i=0;i<offlinedperson.length;i++)
				{
					listid.add(offlinedperson[i][0].toString());
					listData.add(offlinedperson[i][1].toString());
				}
				listView.setAdapter(adapter);
				count = listData.size();
			}
		});								
	}

	private void prepareData(){  //׼������
		offlinedperson=getoffper.doInBackground(20, 0);		
    	for(int i=0;i<offlinedperson.length;i++){
    		//HashMap<String, String> map = new HashMap<String, String>();
    		//map.put("itemText", "����"+i);
    		//listData.add(map);
    		listid.add(offlinedperson[i][0].toString());//���sid
    		listData.add(offlinedperson[i][1].toString());
    	} 
    	count = listData.size();
    }
    
    private void loadMoreData(){ //���ظ�������
    	 count = adapter.getCount(); 
    	 offlinedperson=getoffper.doInBackground(limit, offsetperson);//���˻��Ϣ
		 for(int i=0;i<offlinedperson.length;i++){
	    		//HashMap<String, String> map = new HashMap<String, String>();
	    		//map.put("itemText", person[i].toString());
	    		//listData.add(map);
			 listid.add(offlinedperson[i][0].toString());//���sid
			 listData.add(offlinedperson[i][1].toString());
	    	}
		 listView.setAdapter(adapter); //����adapter
		 offsetperson+=10;//�����������5
    	count = listData.size();
    }

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){		
		Log.i(TAG, "firstVisibleItem="+firstVisibleItem+"\nvisibleItemCount="+visibleItemCount+"\ntotalItemCount"+totalItemCount);	 	
		lastItem = firstVisibleItem + visibleItemCount - 1;  //��1����Ϊ������˸�addFooterView		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){ 
		Log.i(TAG, "scrollState="+scrollState);
		//�����������ǣ������һ��item�����������ݵ�����ʱ�����и���
		if(lastItem == count  && scrollState == this.SCROLL_STATE_IDLE){ 
			Log.i(TAG, "������ײ�");
			moreView.setVisibility(view.VISIBLE);		 
		    mHandler.sendEmptyMessage(0);			 
		}		
	}
	//����Handler
	private Handler mHandler = new Handler(){
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
			    	Toast.makeText(PracticeSearchAct.this, "���ȫ����ʾ", 3000).show();
			        //listView.removeFooterView(moreView); //�Ƴ��ײ���ͼ
			    }
			    countold=count;//�Ѽ���������ݴ�����
			}
		};
	};    
}
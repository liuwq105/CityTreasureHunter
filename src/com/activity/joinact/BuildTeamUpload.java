package com.activity.joinact;

import java.util.Timer;
import java.util.TimerTask;

import com.activity.context.Group;
import com.activity.context.MapApplication;
import com.activity.main.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BuildTeamUpload extends Activity{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.build_upload);
        
        SearchAct.joinActActivityList.add(this);
        
        
        
        final Intent it = new Intent(this, Group.class); //��Ҫת���Activity  
        Timer timer = new Timer();
        TimerTask task = new TimerTask(){
        	@Override
        	public void run(){
        		int len = SearchAct.joinActActivityList.size();
        		for (int i=0;i<len;i++){
        			if (SearchAct.joinActActivityList.get(i) != null)
        				SearchAct.joinActActivityList.get(i).finish();
        		}
        		
        		startActivity(it); //ִ��
        		finish();
        	}
        };
        timer.schedule(task, 1000 * 1); //1���
    }
}

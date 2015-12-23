package com.activity.context;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class MapApplication extends Application{
	private String userId;
	private String username;
	private String actId;
	private String actName;
	private int limitTime;
	private String teamId;
	private String teamName;
	private int teamMax;
	private boolean isHead;

	private static MapApplication mInstance;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager;
    
    public static final String strKey = "4B2EBC226F68A4BEC54B0E7E0C6D65EB5FB0809D";
 
    @Override
    public void onCreate(){
    	super.onCreate();
    	mInstance = this;
    	initEngineManager(this);
    }
    
    @Override
	//��������app���˳�֮ǰ����mapadpi��destroy()�����������ظ���ʼ��������ʱ������
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}
		super.onTerminate();
	}
    
    // ״̬����
    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if ( !mBMapManager.init(strKey,new MyGeneralListener()) ) {
            Toast.makeText(MapApplication.getInstance().getApplicationContext(), 
                    	   "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
        }
	}
  
    public static MapApplication getInstance(){
		return mInstance;
	}
    
    // �����¼���������������ͨ�������������Ȩ��֤�����
 	// �ýӿڷ�������״̬����Ȩ��֤�Ƚ�����û���Ҫʵ�ָýӿ��Դ�����Ӧ�¼�
    public static class MyGeneralListener implements MKGeneralListener{
		
		@Override
		public void onGetNetworkState(int error){
			if (error == MKEvent.ERROR_NETWORK_CONNECT){
                Toast.makeText(MapApplication.getInstance().getApplicationContext(), "���������������",
                    Toast.LENGTH_LONG).show();
            }
            else if (error == MKEvent.ERROR_NETWORK_DATA){
                Toast.makeText(MapApplication.getInstance().getApplicationContext(), "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
            else if (error == MKEvent.ERROR_PERMISSION_DENIED){
            }
			// .....
		}

		@Override
		public void onGetPermissionState(int error) {
			if (error ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //��ȨKey����
                Toast.makeText(MapApplication.getInstance().getApplicationContext(), 
                        	   "���� MapApplication.java�ļ�������ȷ����ȨKey��", Toast.LENGTH_LONG).show();
                MapApplication.getInstance().m_bKeyRight = false;
            }
		}
    }
    
    // TODO ȫ�ִ洢����
    public void setUserId(String id){
    	userId = id;
    }
    
    public String getUserId(){
    	return userId;
    }
    
    public void setUserName(String name){
    	username = name;
    }
    
    public String getUserName(){
    	return username;
    }
    
    public void setActId(String id){
    	actId = id;
    }
    
    public String getActId(){
    	return actId;
    }
    
    public void setActName(String name){
    	actName = name;
    }
    
    public String getActName(){
    	return actName;
    }
    
    public void setTeamId(String id){
    	teamId = id;
    }
    
    public String getTeamId(){
    	return teamId;
    }
    
    public void setTeamName(String name){
    	teamName = name;
    }
    
    public String getTeamName(){
    	return teamName;
    }
    
    public void setIsHead(boolean b){
    	isHead = b;
    }
    
    public boolean getIsHead(){
    	return isHead;
    } 
    
    public void setTeamMax(int num){
    	teamMax = num;
    }
    
    public int getTeamMax(){
    	return teamMax;
    }
    
    public void setLimitTime(int time){
    	limitTime = time;
    }
    
    public int getLimitTime(){
    	return limitTime;
    }
}

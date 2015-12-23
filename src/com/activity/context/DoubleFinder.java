package com.activity.context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.activity.joinact.SearchAct;
import com.activity.main.R;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.function.UrlIP.UrlIP;
import com.function.doublefinder.DoubleTrails;
import com.function.doublefinder.ExitDoubleGame;
import com.function.doublefinder.GetDoubleExit;
import com.function.doublefinder.GetDoubleLatLon;
import com.function.doublefinder.GetDoubleStartSign;
import com.function.doublefinder.GetUsedPrivileges;
import com.function.doublefinder.JoinDouble;
import com.function.doublefinder.UsePrivileges;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class DoubleFinder extends Activity implements Runnable{
	private MapView mMapView;
	private MapController mMapController;
	
	private LocationClient locationClient;
	LocationData locData;	
	private static final int UPDATE_TIME = 2000;	
	private int locationControl = 1;
	
	int myWriteIn = 0;
	int tarWriteIn = 0;

	//�켣��ģ��ͼ�㡢Ŀ��ͼ��
	GraphicsOverlay myPathOverlay;
	GraphicsOverlay targetPathOverlay;
	GraphicsOverlay circleOverlay;
	//ItemizedOverlay<OverlayItem> targetPoiOverlay;
	
	//��λ����������ͼ��
	MyLocationOverlay mLocationOverlay;
	LocationListener mLocationListener;
	MKMapViewListener mMapListener;
	BDNotifyListener mNotifyListner;	
	
	//������	
	SensorManager manager;
	Sensor sensor;	
	float angle;

	boolean isStart = false;
	boolean isOver = false;
	
	Button end;
	Button save;
	private Button prompt;
	private Button prompta;
	private Button promptb;
	private Button promptc;
	PopupWindow popupWindow;
	private LinearLayout isshow;
	boolean flag = false;
	
	// ˫��λ��
	ArrayList<GeoPoint> myLocPoi = new ArrayList<GeoPoint>();
	ArrayList<GeoPoint> tarLocPoi = new ArrayList<GeoPoint>();
	
	// ������
	private EditText historyEdit; 
	private EditText messageEdit;
	private Button sendButton;
	private String ip = new UrlIP().getIP();
	String chat_txt,chat_in;
	private static final int PORT = 8521;	
	Socket socket;
	Thread thread;
	DataInputStream in;
	DataOutputStream out;
	boolean sender = false;		//���Ʒ����߲��𶯣���������
	
	private PopupWindow m_popupWindow;
	View contentView;
	
	//˫�˻���
	Handler handler;
	Handler chanceHappenHandler;
	Timer chanceTimer;
	Runnable getChanceRun;		//���ջ������
	Runnable showCircle;		//��ʾλ�ò�ģ�����ϴ�����λ��
	Runnable startContexRun;	//���ܿ�ʼ�ź�
	Runnable exitRun;			//���Է��Ƿ��˳�
	Runnable endContextRun;
	Runnable recoverChan1;
	Runnable recoverChan2;
	Runnable recoverChan3;
	Runnable showDistanceRun;
	//Runnable getFinderPoiRun;
	
	//λ�á�ģ��
	Random rdm;
	int radius = 1000;
	double latOffset;
	double lonOffset;
	GeoPoint realPoi;
	Timer showDistance;
	
	//�������
	int chan1 = 1;
	int chan2 = 1;
	int chan3 = 3;
	int finmax1 = 1;
	int finmax2 = 1;
	int finmax3 = 3;
	
	int finderChan1 = 4;
	int finderChan2 = 5;
	int finderChan3 = 6;
	
	boolean chan3Available = true;
		
	// �����Ϣ
	String userId; 
	String username;
	int channel;				//Ƶ��
	String actId;
	final String type = "double";
	int DURING_TIME = 2*60*60*1000;
	
	final int reFinChan1 = 10*60*1000;
	final int reFinChan2 = 6*60*1000;
	final int reFinChan3 = 8*60*1000;

	//
	String usedExitName;
	String chanceNumber = "0";
	final int showDistanceTime = 62*1000;
	
	MyThread myThread;
	    
	@SuppressLint("ShowToast")
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.context_doublefinder);	
		
		//userId = this.getIntent().getExtras().getString("userId");
		//username = this.getIntent().getExtras().getString("username");
		//actId = this.getIntent().getExtras().getString("sid");
		
		MapApplication data = ((MapApplication)getApplicationContext()); 
		userId = data.getUserId();
		username = data.getUserName();
		actId = data.getActId();
		//channel = 0 - Integer.parseInt(actId);
		channel = Integer.parseInt(actId);
		
		//ƫ����
		rdm = new Random(System.currentTimeMillis());
		latOffset = rdm.nextDouble()/130-rdm.nextInt()%2;
		rdm = new Random(System.currentTimeMillis());
		lonOffset = rdm.nextDouble()/100-rdm.nextInt()%2;

		manager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		MapApplication app = (MapApplication)this.getApplication();
		
		//δ��ʼ�������
		if (app.mBMapManager == null){
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(MapApplication.strKey, new MapApplication.MyGeneralListener());
		}
		app.mBMapManager.start();
		
		mMapView = (MapView)findViewById(R.id.bmapsView);  
		mMapView.setBuiltInZoomControls(true);  
		//�����������õ����ſؼ�  
		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(14);//���õ�ͼ�������� 
		mMapController.setCompassMargin(20, 20);
		mMapController.setCenter(new GeoPoint((int) (37.5299 * 1E6), (int)(122.0606 * 1E6)));
		
		// ��ӹ켣ͼ��
		myPathOverlay = new GraphicsOverlay(mMapView);
		targetPathOverlay = new GraphicsOverlay(mMapView);			
		// ��Ӷ�λͼ��
        mLocationOverlay = new MyLocationOverlay(mMapView);
        mLocationOverlay.enableCompass();
		mMapView.getOverlays().add(mLocationOverlay);
		// ���ģ��λ��ͼ��
		circleOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(circleOverlay);
		
		locationClient = new LocationClient(getApplicationContext());
        locData = new LocationData();
        LocationClientOption option = new LocationClientOption();
        //option.setPriority(LocationClientOption.NetWorkFirst);
    	option.setOpenGps(true);                                //�Ƿ��GPS 
        option.setCoorType("bd09ll");                           //���÷���ֵ���������͡� 
        option.setPriority(LocationClientOption.GpsFirst); 		//���ö�λ���ȼ� 
        option.setProdName("ɽ��");               		      	//���ò�Ʒ�����ơ�ǿ�ҽ�����ʹ���Զ���Ĳ�Ʒ�����ƣ����������Ժ�Ϊ���ṩ����Ч׼ȷ�Ķ�λ���� 
        option.setScanSpan(UPDATE_TIME);                        //���ö�ʱ��λ��ʱ��������λ���� 
        locationClient.setLocOption(option);
		//ע��λ�ü����� 
        locationClient.registerLocationListener(new BDLocationListener() { 
             
            @Override 
            public void onReceiveLocation(BDLocation location) {
            	//TODO ��λ����        	
                if (location == null) {
                	Toast.makeText(DoubleFinder.this, "��λʧ��", Toast.LENGTH_SHORT).show();
                    return; 
                }
        		
                locData.latitude = location.getLatitude();
                locData.longitude = location.getLongitude();
                locData.accuracy = location.getRadius();	//����
                
                //drawable --> bitmap����ת
                Drawable marker = getResources().getDrawable(R.drawable.arrow);
    			BitmapDrawable bd = (BitmapDrawable)marker;
                Bitmap bm = bd.getBitmap();
                Matrix matrix=new Matrix(); 
                matrix.postRotate(angle);
                Bitmap dstbmp=Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);
                BitmapDrawable newbd = new BitmapDrawable(dstbmp);                  		
                newbd.setBounds(0, 0, newbd.getIntrinsicWidth(), newbd.getIntrinsicHeight());  
                mLocationOverlay.setMarker(newbd);
                
                mLocationOverlay.setData(locData);
                mMapView.refresh();
            	GeoPoint p = new GeoPoint((int)(locData.latitude * 1E6), (int)(locData.longitude * 1E6));                
                if (locationControl == 1){
                	mMapController.animateTo(p);
                	locationControl = 0;
                }
                if (isStart && myWriteIn == 0 && locData.latitude != 0)
                	myLocPoi.add(p);
                myWriteIn = (myWriteIn+1)%3;               
            }
            @Override 
            public void onReceivePoi(BDLocation location) { 
            }              
        });        
		locationClient.start();
		//�����ζ�λ
		locationClient.requestLocation();
		
		////
		//locData.latitude = 37.5299;
		//locData.longitude = 122.0806;
		
		end = (Button)findViewById(R.id.end);
        save = (Button)findViewById(R.id.save); 
        prompt = (Button)this.findViewById(R.id.prompt);
        end.setClickable(false);            
        
        save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mMapView.getCurrentMap();
			}       	
        }); 

		handler = new Handler();	
		
		//����ʱ�����ң�
		endContextRun = new Runnable(){
			@Override
			public void run() {
				Toast.makeText(DoubleFinder.this, "ʱ�䵽����ʧ����", Toast.LENGTH_LONG).show();
				//handler.removeCallbacks(finderPoiRun);
				/*
				handler.removeCallbacks(recoverChan1);
				handler.removeCallbacks(recoverChan2);
				handler.removeCallbacks(recoverChan3);
				handler.removeCallbacks(exitRun);
				handler.removeCallbacks(getFinderPoiRun);*/
				myThread.stopHandler();
		        		
				isOver = true;	
				mMapView.getOverlays().clear();
				myPathOverlay.setData(myPath(myLocPoi, true));
				mMapView.getOverlays().add(myPathOverlay);
				targetPathOverlay.setData(myPath(tarLocPoi, false));						
				mMapView.getOverlays().add(targetPathOverlay);
				mMapView.refresh();
				Toast.makeText(DoubleFinder.this, "��켣:�ҷ�������/�Է�������", Toast.LENGTH_LONG).show();
				
				save.setVisibility(View.VISIBLE);
				end.setText("�����뿪");
				end.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// ��תҳ��
						leave();
						Intent intent = new Intent(DoubleFinder.this,RecordOfDouble.class);
						startActivity(intent);
						finish();
					}							
				});//end of listener
			}			
		};
		
		// ���ͼ����ź�
		new JoinDouble().execute(actId,userId);
		// TODO ���տ�ʼ�ź�
		startContexRun = new Runnable(){
			@Override
			public void run() {				
				String isBegin = new GetDoubleStartSign().doInBackground(actId);
				if (isBegin.equals("1")){
					isStart = true;
					Toast.makeText(DoubleFinder.this, "��Ϸ��ʼ��ʱ��"+(DURING_TIME/(1000*60))+"���ӣ�", Toast.LENGTH_SHORT).show();
					/*
					handler.post(showCircle);
					handler.post(recoverChan1);
					handler.post(recoverChan2);
					handler.post(recoverChan3);
					handler.post(getChanceRun);
					handler.postDelayed(endContextRun, DURING_TIME);
					handler.removeCallbacks(startContexRun);*///
					myThread.start();
					
					Vibrator vibrator = (Vibrator) DoubleFinder.this.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(1000);
					
					prompt.setHint("");
					prompt.setText("���ᨋ");
					prompt.setClickable(true);
					
					end.setHint("");
					end.setText("�˳���Ϸ");
					end.setClickable(true);
			        end.setOnClickListener(new ExitGameOnClickListener());
			        
					handler.removeCallbacks(this);
				}
				else
					handler.postDelayed(this, 2000);
			}			
		};		
		handler.postDelayed(startContexRun, 100);
		
		// TODO �˳���Ա
		usedExitName = new GetDoubleExit().doInBackground(actId);
		exitRun = new Runnable(){
			@Override
			public void run() {
				String exitName = new GetDoubleExit().doInBackground(actId);
				if (!isOver && !(exitName.equals(usedExitName)) && !(exitName.equals(username)) 
						&& !exitName.equals("-1")){
					handler.removeCallbacks(this);	
					
					Toast.makeText(DoubleFinder.this, "�Է��˳���Ϸ�����Ѿ���ʤ��", Toast.LENGTH_LONG).show();							
					isOver = true;
	
					end.setHint("");
					end.setText("�����뿪");
					end.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// ��תҳ��
							leave();
							Intent intent = null;
							if (isStart)
								intent = new Intent(DoubleFinder.this,RecordOfDouble.class);
							else
								intent = new Intent(DoubleFinder.this,SearchAct.class);
							startActivity(intent);
							mMapView.destroy();
							finish();
						}							
					});
					
	        		if (isStart){
	        			/*
	        			handler.removeCallbacks(showCircle);
	        			handler.removeCallbacks(recoverChan1);
						handler.removeCallbacks(recoverChan2);
						handler.removeCallbacks(recoverChan3);
						handler.removeCallbacks(endContextRun);*/
						
						myThread.stopHandler();
						
	        			save.setVisibility(View.VISIBLE);
	        			
	        			prompt.setHint("���ᨋ");
						prompt.setText("");
	        			prompt.setClickable(false);
	        			
	        			mMapView.getOverlays().clear();
						myPathOverlay.setData(myPath(myLocPoi, true));
						mMapView.getOverlays().add(myPathOverlay);
						targetPathOverlay.setData(myPath(tarLocPoi, false));						
						mMapView.getOverlays().add(targetPathOverlay);
						mMapView.refresh();
						Toast.makeText(DoubleFinder.this, "��켣:�ҷ�������/�Է�������", Toast.LENGTH_LONG).show();	
	        		}
	        		else
	        			handler.removeCallbacks(startContexRun);	
				}
				else
					handler.postDelayed(this, 2500);
				
				usedExitName = exitName;			
			}		
		};
		handler.post(exitRun);
		
		// TODO �ϴ����������,�ж�ʤ��,��ʾģ��λ��	
		//final double mlat = 37.5299;
		//final double mlon = 122.0806;
		showCircle = new Runnable(){		
			@Override
			public void run() {	
				if (!isOver){
					//�ϴ�λ��
					new DoubleTrails().execute(actId,userId,locData.latitude,locData.longitude);
					
					String[] finderPoi = new GetDoubleLatLon().doInBackground(actId,userId,"ufinderlatlon");
					
					if (!finderPoi[0].equals("-1") && !finderPoi[0].equals("0")){
						double lat = Double.valueOf(finderPoi[0]);
						double lon = Double.valueOf(finderPoi[1]);
						//Toast.makeText(DoubleFinder.this, lat+"  "+lon, Toast.LENGTH_SHORT).show();
						//lat += latOffset;
						//lon += lonOffset;
						GeoPoint tPoi = new GeoPoint((int)(lat * 1E6),(int)(lon * 1E6));
						if (isStart && tarWriteIn == 0)
							tarLocPoi.add(tPoi);
						tarWriteIn = (tarWriteIn+1)%3;
					
						circleOverlay.removeAll();
						circleOverlay.setData(myCircle(tPoi, radius));
						mMapView.refresh();
					
						GeoPoint myPoi = new GeoPoint((int) (locData.latitude * 1E6), (int)(locData.longitude * 1E6));
						double distance = DistanceUtil.getDistance(myPoi, tPoi);
						if (distance < 150){
							isOver = true;
							Toast.makeText(DoubleFinder.this, "��ϲ�����ҵ�Ŀ��!", Toast.LENGTH_LONG).show();
						
							/*
							handler.removeCallbacks(startContexRun);
		        			handler.removeCallbacks(exitRun);	
		        			handler.removeCallbacks(showCircle);
		        			handler.removeCallbacks(recoverChan1);
		        			handler.removeCallbacks(recoverChan2);
		        			handler.removeCallbacks(recoverChan3);
		        			handler.removeCallbacks(getChanceRun);*/
							//handler.removeCallbacks(getFinderPoiRun);
							myThread.stopHandler();
		        		
							save.setVisibility(View.VISIBLE);
							
							prompt.setHint("���ᨋ");
							prompt.setText("");
							prompt.setClickable(false);
		        			
							mMapView.getOverlays().clear();
							myPathOverlay.setData(myPath(myLocPoi, true));
							mMapView.getOverlays().add(myPathOverlay);
							targetPathOverlay.setData(myPath(tarLocPoi, false));						
							mMapView.getOverlays().add(targetPathOverlay);
							mMapView.refresh();
							Toast.makeText(DoubleFinder.this, "��켣:�ҷ�������/�Է�������", Toast.LENGTH_LONG).show();
		        		
							end.setText("�����뿪");
							end.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View v) {
									// ��תҳ��
									leave();
									Intent intent = new Intent(DoubleFinder.this,RecordOfDouble.class);
									startActivity(intent);
									mMapView.destroy();
									finish();
								}							
							});
						}
					}
					handler.postDelayed(this, 2000);
				}
				else
					handler.removeCallbacks(this);
			}					
		};
		
		// TODO ��ʾ���루���ᣩ
		showDistanceRun = new Runnable(){
			@Override
			public void run() {
				if (!isOver){
					int len = tarLocPoi.size();
					GeoPoint tarPoi = tarLocPoi.get(len-1);
					showTarget(tarPoi);
					
					handler.postDelayed(this, 10000);
				}
				else
					handler.removeCallbacks(this);
			}			
		};

		// TODO ����ָ�
		recoverChan1 = new Runnable(){
			@Override
			public void run() {
				if (isStart){
					if (chan1 < finmax1)
						chan1++;
					handler.postDelayed(this, reFinChan1);
				}
				else
					handler.removeCallbacks(this);
			}			
		};
		
		recoverChan2 = new Runnable(){
			@Override
			public void run() {
				if (isStart){
					if (chan2 < finmax2)
						chan2++;
					handler.postDelayed(this, reFinChan2);
				}
				else
					handler.removeCallbacks(this);
			}			
		};

		handler.post(recoverChan2);
		
		recoverChan3 = new Runnable(){
			@Override
			public void run() {
				if (isStart){
					if (chan3 < finmax3)
						chan3++;
					handler.postDelayed(this, reFinChan3);
				}
				else
					handler.removeCallbacks(this);
			}			
		};
		
		// TODO ���ջ���
		getChanceRun = new Runnable(){
			@Override
			public void run() {
				if (!isOver){
					String newChance = new GetUsedPrivileges().doInBackground(actId);
					if (!chanceNumber.equals(newChance) && !newChance.equals("")){
						int activingChan = Math.abs(Integer.parseInt(newChance));
						Message message = new Message();  
						message.what = activingChan;  
						chanceHappenHandler.sendMessage(message);	
					}
					chanceNumber = newChance;	
					handler.postDelayed(this, 2000);
				}
				else
					handler.removeCallbacks(this);
			}			
		};
				
		// TODO �������
		chanceHappenHandler = new Handler(){					
			public void handleMessage(Message msg) {  
				Vibrator vibrator = (Vibrator) DoubleFinder.this.getSystemService(Context.VIBRATOR_SERVICE);
				switch(msg.what){				
				case 1:
					Toast.makeText(DoubleFinder.this, "�Է��Ŵ��������ģ���뾶��", Toast.LENGTH_SHORT).show();
					vibrator.vibrate(500);
					radiuslarger();
					break;
				case 2:
					Toast.makeText(DoubleFinder.this, "�Է�������Լ���ƫ������", Toast.LENGTH_SHORT).show();
					vibrator.vibrate(500);
					setHigherOffset();
					break;
				case 3:
					Toast.makeText(DoubleFinder.this, "�ҷ�λ����ʱ��¶��", Toast.LENGTH_LONG).show();
					vibrator.vibrate(1000);
					break;
				case 6:
					handler.post(showDistanceRun);
					chan3Available = false;
					//promptc.setText("");
					//promptc.setHint("��ʾ˫������("+chan3+"/"+finmax3+")");
					promptc.setClickable(false);
					break;
				case -6:
					handler.removeCallbacks(showDistanceRun);
					Toast.makeText(DoubleFinder.this, "��ʾʱ�䵽��", Toast.LENGTH_SHORT).show();
					chan3Available = true;
					//promptc.setText("��ʾ˫������("+chan3+"/"+finmax3+")");
					//promptc.setHint("");
					promptc.setClickable(true);
					break;
				default:
					break;
				}
			};			        
		};//end of Handler
		
		myThread = new MyThread(handler);

		mLocationListener = new LocationListener(){		
			public void onLocationChanged(Location location) {
				if (location != null){
					GeoPoint pt = new GeoPoint((int)(location.getLatitude()*1e6),
							(int)(location.getLongitude()*1e6));
					mMapView.getController().animateTo(pt);
				}
			}

			@Override
			public void onProviderDisabled(String provider) {	
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {	
			}
        };
	
		mMapListener = new MKMapViewListener(){
		   	@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
		        //�����ͼ����¼�
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					Toast.makeText(DoubleFinder.this,title,Toast.LENGTH_SHORT).show();
					mMapController.animateTo(mapPoiInfo.geoPt);
				}
			}
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onGetCurrentMap(Bitmap bitmap) {	
				// TODO �����ͼ�¼�
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh_mm_ss");
				Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��     
				String filename = formatter.format(curDate)+".jpg";
				
				// �ж�sd���Ƿ����
				boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				File root = null;
				if (sdCardExist) {   
		            root = Environment.getExternalStorageDirectory();		// ��ȡ��Ŀ¼   
		        } else {   
		        	Toast.makeText(DoubleFinder.this, "δ�ҵ��ڴ濨��", Toast.LENGTH_LONG).show();
		        	return;
		        }
				String filePath = root.toString()+"/CityTreasureHunter/DoubleContext";		// ���·��
				Toast.makeText(DoubleFinder.this, "�����У����Ժ�", Toast.LENGTH_LONG).show();
				// ����
		        File file = new File(filePath);
		        if (!file.exists()) { 
					file.mkdirs();  //���������ļ���         
		        }
		        file = new File(filePath,filename);  
		        try {
					file.createNewFile();
				} catch (IOException e) {
					Toast.makeText(DoubleFinder.this, "�ļ��Ѵ��ڣ�", Toast.LENGTH_SHORT).show();    
				}
		        
		        FileOutputStream fos = null;
		        try {   
		        	// -------??		        	
		        	fos = new FileOutputStream(file);  
		            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);   
		            fos.flush();   
		            fos.close();
		            Toast.makeText(DoubleFinder.this, "�ɹ���"+"ͼƬ����·��:"+filePath, Toast.LENGTH_SHORT).show();   
		        }catch (FileNotFoundException e) {   
		        	Toast.makeText(DoubleFinder.this, "δ�ҵ��ļ���", Toast.LENGTH_SHORT).show();    
		        }catch (IOException e) {   
		        	Toast.makeText(DoubleFinder.this, "�洢����", Toast.LENGTH_SHORT).show();   
		        } 	        
			}
			@Override
			public void onMapAnimationFinish() {				
			}
			@Override
			public void onMapMoveFinish() {
			}  	
		};
		// �û�ע���ͼ������
		mMapView.regMapViewListener(MapApplication.getInstance().mBMapManager, mMapListener);		
              
		contentView = getLayoutInflater().inflate(R.layout.context_doublefindermenu, null,true);
		isshow = (LinearLayout)this.findViewById(R.id.isshow);
		
		m_popupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, true);
		m_popupWindow.setBackgroundDrawable(new BitmapDrawable());// �������ſ��Ե�����أ���������ťdismiss()popwindow
		m_popupWindow.setOutsideTouchable(true);
		m_popupWindow.setAnimationStyle(R.style.PopupAnimation);
		
		prompta = (Button) contentView.findViewById(R.id.prompta);
		promptb = (Button) contentView.findViewById(R.id.promptb);
		promptc = (Button) contentView.findViewById(R.id.promptc);
		
		
		prompt.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				try {
					if (m_popupWindow.isShowing()) {
						m_popupWindow.dismiss();
					}
					else
						m_popupWindow.showAsDropDown(v);					
							
					if (chan1 == 0){
						prompta.setText("");
						prompta.setHint("ģ���뾶��С(0/"+finmax1+")");
						prompta.setClickable(false);
					}
					else{
						prompta.setHint("");
						prompta.setText("ģ���뾶��С("+chan1+"/"+finmax1+")");
						prompta.setClickable(true);
					}
					
					if (chan2 == 0){
						promptb.setText("");					
						promptb.setHint("�����Сƫ����(0/"+finmax1+")");
						promptb.setClickable(false);
					}
					else{
						promptb.setHint("");
						promptb.setText("�����Сƫ����("+chan2+"/"+finmax2+")");
						promptb.setClickable(true);
					}
					/*
					if (chan3 == 0){
						promptc.setText("");
						promptc.setHint("��ʾ˫������(0/"+finmax1+")");
						promptc.setClickable(false);
					}
					else{
						promptc.setHint("");
						promptc.setText("��ʾ˫������("+chan3+"/"+finmax3+")");
						promptc.setClickable(true);
					}*/
					
					if (chan3Available && chan3 != 0){
						promptc.setText("��ʾ˫������("+chan3+"/"+finmax3+")");
						promptc.setHint("");
						promptc.setClickable(true);
					}
					else{
						promptc.setText("");
						promptc.setHint("��ʾ˫������("+chan3+"/"+finmax3+")");
						promptc.setClickable(false);
					}
				} catch (Exception e) {
					Toast.makeText(DoubleFinder.this, e.getMessage(),Toast.LENGTH_SHORT);
				}
			}
		});
		prompt.setClickable(false);
		
		prompta.setOnClickListener(new View.OnClickListener() {					
			@SuppressLint("ShowToast")
			public void onClick(View arg0) {				
				m_popupWindow.dismiss();
				chan1--;
				radiusLower();
				new UsePrivileges().execute(actId,finderChan1);
				finderChan1 = 0-finderChan1;
				//Toast.makeText(DoubleFinder.this, "��Ȩ1", Toast.LENGTH_SHORT).show();
			}
		});
		
		promptb.setOnClickListener(new View.OnClickListener() {					
			public void onClick(View arg0) {
				chan2--;
				setLowerOffset();
				m_popupWindow.dismiss();
				new UsePrivileges().execute(actId,finderChan2);
				finderChan2 = 0-finderChan2;
			}
		});
		
		promptc.setOnClickListener(new View.OnClickListener() {					
			public void onClick(View arg0) {	
				chan3--;
				showDistance = new Timer();
				showDistance.schedule(new TimerTask(){
					@Override
					public void run() {
						Message message = new Message();  
			            message.what = -6;  
			            chanceHappenHandler.sendMessage(message);	
					}					
				}, showDistanceTime);
				m_popupWindow.dismiss();
				new UsePrivileges().execute(actId,finderChan3);
				finderChan3 = 0-finderChan3;
			}
		});  
        
        Button btn_show = (Button) findViewById(R.id.btn_show);
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.context_slide, null);
		popupWindow =new PopupWindow(layout, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		Button btn_dismiss = (Button) layout.findViewById(R.id.btn_dismiss);
        
		historyEdit=(EditText) layout.findViewById(R.id.history); 
		messageEdit=(EditText) layout.findViewById(R.id.message);
		sendButton=(Button) layout.findViewById(R.id.send);
		
		// TODO �����Ұ�ť		
		sendButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				send();
			}
		}); 
					
		historyEdit.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {			
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {	
				// ��Ϣ��
				if (sender == false){
					Vibrator vibrator = (Vibrator) DoubleFinder.this.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(1000);
				}	
				sender = false;
			}		
		});
        save.setVisibility(View.INVISIBLE);
        
        login();  
        
        btn_dismiss.setOnClickListener(new OnClickListener(){
			 @Override
			 public void onClick(View v){				 
				 isshow.setVisibility(View.VISIBLE);
				 new Handler().postDelayed(new Runnable(){  
				     public void run(){  
				     //execute the task
				    	 openMenu();
				     }  
				  }, 100);
			 }
		 });
		
		btn_show.setOnClickListener(new OnClickListener(){
			 @Override
			 public void onClick(View v){
				 openMenu();
				 new Handler().postDelayed(new Runnable(){  
				     public void run(){  
				     //execute the task
				    	 isshow.setVisibility(View.INVISIBLE);
				     }  
				  }, 2000);
				 
			 }
		 });		
	}
	
	// TODO ���ᣨ�ҷ���	
	//�뾶��С
	private void radiusLower(){
		radius =(int) (radius/1.75);
		latOffset = latOffset/1.75;
		lonOffset = lonOffset/1.75;
	}
	
	//�뾶���
	private void radiuslarger(){
		radius = (int) (radius*1.6);
		latOffset = latOffset*1.6;
		lonOffset = lonOffset*1.6;
	}
	
	//��Сƫ����
	private void setLowerOffset(){
		rdm = new Random(System.currentTimeMillis());
		latOffset = rdm.nextDouble()/150-rdm.nextInt()%2;
		rdm = new Random(System.currentTimeMillis());
		lonOffset = rdm.nextDouble()/120-rdm.nextInt()%2;
	}
	
	//����ƫ����
	private void setHigherOffset(){
		rdm = new Random(System.currentTimeMillis());
		latOffset = rdm.nextDouble()/100-rdm.nextInt()%2;
		rdm = new Random(System.currentTimeMillis());
		lonOffset = rdm.nextDouble()/90-rdm.nextInt()%2;
	}
	
	//��ʾ˫������(������)
	private void showTarget(GeoPoint targetPoi){
		GeoPoint myPoi = new GeoPoint((int) (locData.latitude * 1E6), (int)(locData.longitude * 1E6));
		double distance = DistanceUtil.getDistance(myPoi, targetPoi);
		distance = (double)Math.round(distance*100)/100;
		Toast.makeText(DoubleFinder.this, "��Ŀ��ľ��룺"+distance+"�ף�", Toast.LENGTH_SHORT).show();
	}
	
	// TODO ������
	@SuppressLint("SimpleDateFormat")
	public void login(){		
		try{
			//�ͻ���socket
			socket = new Socket(ip,PORT);				
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
				
			//�������������Ϣ
			out.writeInt(channel);
			//out.writeUTF("�û���  "+username+"  "+nowStr+"�����ˣ�");						
		}catch(IOException e){
			e.printStackTrace();
		}
		//���̣߳�������������Ϣ������ʱ����
		thread = new Thread(DoubleFinder.this);
		thread.start();	
	}
	
	public void leave(){		
		try{			
			out.writeUTF("<"+username+"> "+"�����ˣ�");
			//�Ⱥ�
			out.close();
			in.close();
			socket.close();
		}catch(IOException e){					
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public void send(){		
		chat_txt = messageEdit.getText().toString();
		
		if (chat_txt == null && chat_txt.equals("")){
			Toast.makeText(DoubleFinder.this,"���Ͳ���Ϊ�գ�",Toast.LENGTH_SHORT).show();
		}
		else{
			sender = true;
			Date now=new Date(System.currentTimeMillis()); 
			SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
			String nowStr = format.format(now);
			try {
				out.writeInt(channel);
				out.writeUTF(username+" "+nowStr+"˵��\n"+chat_txt);
			} catch (IOException e) {
				e.printStackTrace();
			}			
			messageEdit.setText("");		
		}	
	}
	
	@Override
	public void run() {
		while (true){
			try{
				//��ȡ����������Ϣ
				//if (isStart){
					chat_in = in.readUTF();
					chat_in = chat_in+"\n";
					
					myHandler.sendMessage(myHandler.obtainMessage());
				//}
			}catch(IOException e){
			}
		}
	}
	@SuppressLint("HandlerLeak")
	
	//ˢ�½���(ԭ���޷��ٷ����̸߳��½���)
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			//
			historyEdit.append(chat_in);
			super.handleMessage(msg);
		}
	};
	
	//TODO --���ؼ�����--
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    // ���¼����Ϸ��ذ�ť   
	    if (keyCode == KeyEvent.KEYCODE_BACK) {  
	    	if (!isOver){
	    		new AlertDialog.Builder(this) 
	    		.setTitle("�˳�")  
	    		.setMessage("��ȷ��Ҫ�˳�������")  
	    		.setNegativeButton("ȡ��",null)  
	    		.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {  
	    			@SuppressWarnings("unchecked")
					public void onClick(DialogInterface dialog,int whichButton) {
	    				//�˳��ź�
		        		new ExitDoubleGame().execute(actId,userId,"doublefinderexit");	        		
		        		isOver = true;

		        		handler.removeCallbacks(startContexRun);
		        		handler.removeCallbacks(exitRun);
		        		if (isStart){	
		        			/*
		        			handler.removeCallbacks(showCircle);
		        			handler.removeCallbacks(recoverChan1);
		        			handler.removeCallbacks(recoverChan2);
		        			handler.removeCallbacks(recoverChan3);
		        			handler.removeCallbacks(endContextRun);
		        			handler.removeCallbacks(getChanceRun);*/
			        		//handler.removeCallbacks(getFinderPoiRun);
		        			
		        			myThread.stopHandler();
		        			
		        			save.setVisibility(View.VISIBLE);
		        			
		        			prompt.setHint("���ᨋ");
							prompt.setText("");
		        			prompt.setClickable(false);
		        			
		        			mMapView.getOverlays().clear();
							myPathOverlay.setData(myPath(myLocPoi, true));
							mMapView.getOverlays().add(myPathOverlay);
							targetPathOverlay.setData(myPath(tarLocPoi, false));						
							mMapView.getOverlays().add(targetPathOverlay);
							mMapView.refresh();
							Toast.makeText(DoubleFinder.this, "��켣", Toast.LENGTH_LONG).show();
		        		}
		        		else
		        			handler.removeCallbacks(startContexRun);
		        		
						end.setText("�����뿪");
						end.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// ��תҳ��
								if (isStart)
									leave();
								Intent intent = null;
								if (isStart)
									intent = new Intent(DoubleFinder.this,RecordOfDouble.class);
								else
									intent = new Intent(DoubleFinder.this,SearchAct.class);
								startActivity(intent);
								mMapView.destroy();
								finish();
							}							
						});
	    			}  
	    		}).show(); 
	    	}
	        else{
	        	Intent intent = new Intent(DoubleFinder.this,RecordOfDouble.class);
				startActivity(intent);
				finish();
	        }
	    	return true; 
	    } 
	    else{  
	        return super.onKeyDown(keyCode, event);  
	    }  
	} 
	
	//TODO --���򴫸���--
    SensorEventListener listener=new SensorEventListener(){
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			angle = event.values[0];
		}
	};
	
	// TODO ��ù켣
	public Graphic myPath(ArrayList<GeoPoint> LocPoi, boolean mine){
		// ---��õ�---
		/*
		GeoPoint gpoint1 = new GeoPoint((int) (37.5299 * 1E6), (int)(122.0606 * 1E6));
		GeoPoint gpoint2 = new GeoPoint((int) (37.5302 * 1E6), (int)(122.0654 * 1E6));
		GeoPoint gpoint3 = new GeoPoint((int) (37.5273 * 1E6), (int)(122.0683 * 1E6));
			
		LocPoi.add(gpoint1);
		LocPoi.add(gpoint2);
		LocPoi.add(gpoint3);*/
		
		//����������
		int len = LocPoi.size();		
		GeoPoint[] geolist = new GeoPoint[len];
		
		for (int i=0;i<len;i++){
			geolist[i] = LocPoi.get(i);
		}
		// �����뾭γת��
		Projection projection = mMapView.getProjection();
			
		Path path = new Path();
		Point loc = new Point();
		//����
		Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(4);
            
		if (len == 0)
			Toast.makeText(DoubleFinder.this, "�޵�ɼ�¼��", Toast.LENGTH_SHORT).show();
		else{
			mMapController.animateTo(geolist[0]);
			projection.toPixels(geolist[0], loc);
			path.moveTo(loc.x, loc.y);
			for (int i=1;i<len;i++){
				projection.toPixels(geolist[i], loc);
				path.lineTo(loc.x, loc.y);
			}				
		}
		Geometry geometry = new Geometry();
		geometry.setPolyLine(geolist);
		Symbol symbol = new Symbol();	//������ʽ 
		Symbol.Color color = symbol.new Color();	//������ɫ��ʽ
		
		if (mine){
			color.blue = 255;
			color.red = 0;
		}
		else{
			color.blue = 0;
			color.red = 255;
		}
		color.green = 0;	
		color.alpha = 126;
		symbol.setLineSymbol(color, 4);
		Graphic graphic = new Graphic(geometry, symbol);
		return graphic;
	}
	
	public Graphic myCircle(GeoPoint p, int radius){
		Geometry circle = new Geometry();
		circle.setCircle(p, radius);
		Symbol symbol = new Symbol();	//������ʽ 
		Symbol.Color color = symbol.new Color();	//������ɫ��ʽ
		color.red = 0;
		color.green = 0;
		color.blue = 255;
		color.alpha = 126;
		symbol.setSurface(color, 1, 0);		//�������߿�
		Graphic graphic = new Graphic(circle, symbol);
		return graphic;
	}
	
	//�����Ұ�ť
	public void btn_showOnClicked() {
		 openMenu();
		
	} 


	public void btn_dismissOnClicked() {
		 openMenu();
	}
	public void openMenu(){
		if (!flag){
			 popupWindow.setAnimationStyle(R.style.PopupAnimationb);
			 popupWindow.showAtLocation(findViewById(R.id.btn_show), Gravity.NO_GRAVITY, 0, 0);
			 popupWindow.setFocusable(true);
			 popupWindow.update();
			 flag =true;		 		
		}else{
			 popupWindow.dismiss();
			 popupWindow.setFocusable(false);
			 flag =false;
		}
	}
	
	// TODO �˳���ť����
	class ExitGameOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(DoubleFinder.this) 
	        .setTitle("�˳�")  
	        .setMessage("��ȷ��Ҫ�˳���Ϸ��")  
	        .setNegativeButton("ȡ��",null)  
	        .setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {  
	        	@SuppressWarnings("unchecked")
				public void onClick(DialogInterface dialog,int whichButton) { 
	        		//�˳��ź�
	        		new ExitDoubleGame().execute(actId,userId,"doublefinderexit");	        		
	        		isOver = true;
	        			        		
	        		handler.removeCallbacks(startContexRun);
	        		handler.removeCallbacks(exitRun);
	        		if (isStart){	
	        			handler.removeCallbacks(showCircle);
	        			handler.removeCallbacks(recoverChan1);
	        			handler.removeCallbacks(recoverChan2);
	        			handler.removeCallbacks(recoverChan3);
	        			handler.removeCallbacks(getChanceRun);
		        		//handler.removeCallbacks(getFinderPoiRun);
		        		
	        			save.setVisibility(View.VISIBLE);
	        			
	        			prompt.setHint("���ᨋ");
						prompt.setText("");
	        			prompt.setClickable(false);
	        			
	        			mMapView.getOverlays().clear();
						myPathOverlay.setData(myPath(myLocPoi, true));
						mMapView.getOverlays().add(myPathOverlay);
						targetPathOverlay.setData(myPath(tarLocPoi, false));						
						mMapView.getOverlays().add(targetPathOverlay);
						mMapView.refresh();
						Toast.makeText(DoubleFinder.this, "��켣:�ҷ�������/�Է�������", Toast.LENGTH_LONG).show();
	        		}
	        		else
	        			handler.removeCallbacks(startContexRun);
	        		
					end.setText("�����뿪");
					end.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// ��תҳ��
							if (isStart)
								leave();
							Intent intent = null;
							if (isStart)
								intent = new Intent(DoubleFinder.this,RecordOfDouble.class);
							else
								intent = new Intent(DoubleFinder.this,SearchAct.class);
							startActivity(intent);
							mMapView.destroy();
							finish();
						}							
					});
	            }  
	        }).show();
			
		}		
	}
	
	// TODO �߳�
	class MyThread extends Thread{
		Handler mHandler;
			
		public MyThread(Handler handler){
			mHandler = handler;
		}
			
		public void run(){
			mHandler.post(recoverChan1);
			mHandler.post(recoverChan2);
			mHandler.post(recoverChan3);
			mHandler.postDelayed(endContextRun, DURING_TIME);
			mHandler.postDelayed(showCircle,50);
			mHandler.postDelayed(getChanceRun,100);
		}
			
		public void stopHandler(){	
			mHandler.removeCallbacks(recoverChan1);
			mHandler.removeCallbacks(recoverChan2);
			mHandler.removeCallbacks(recoverChan3);
			mHandler.removeCallbacks(endContextRun);
			mHandler.removeCallbacks(showCircle);
			mHandler.removeCallbacks(getChanceRun);
		}	
	}
	
	@Override 
    protected void onDestroy() { 
        if (locationClient != null && locationClient.isStarted()) { 
            locationClient.stop();            
            locationClient = null;
        } 
        if (handler != null)
        	handler = null;
        if (myThread != null)
        	myThread = null;
        mMapView.destroy();
        mLocationOverlay.disableCompass(); // �ر�ָ����
        manager.unregisterListener(listener);
        leave();       
        super.onDestroy();
    }
	
	@Override 
	protected void onPause(){
		MapApplication app = (MapApplication)this.getApplication();
        mLocationOverlay.disableCompass(); // �ر�ָ����
		app.mBMapManager.stop();
		manager.unregisterListener(listener);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		MapApplication app = (MapApplication)this.getApplication();
		// ע�ᶨλ�¼�����λ�󽫵�ͼ�ƶ�����λ��
        mLocationOverlay.enableCompass(); // ��ָ����
		app.mBMapManager.start();
		manager.registerListener(listener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}
}

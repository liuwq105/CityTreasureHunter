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
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.function.UrlIP.UrlIP;
import com.function.team.ExitActivity;
import com.function.team.GetCheckPoi;
import com.function.team.GetExitCapUsers;
import com.function.team.GetNotifyPoi;
import com.function.team.GetOrigin;
import com.function.team.GetPointSign;
import com.function.team.GetStartSign;
import com.function.team.GetUsersTrail;
import com.function.team.JoinGroup;
import com.function.team.PointSign;
import com.function.team.StartGame;
import com.function.team.TimeEnd;
import com.function.team.UsersTrail;

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

public class Group extends Activity implements Runnable{
	private MapView mMapView;
	private MapController mMapController;
	
	private LocationClient locationClient;
	LocationData locData;	
	private static final int UPDATE_TIME = 2000;
	
	private int locationControl = 1;
	private final int rotateTime = 1000*20;	//��ʾ·��ʱ�䣨MS��
	int extra = 0;
	
	int writeIn = 0;
	
	Handler rotateHandler;
	RouteOverlay routeOverlay;
	Timer rotateShow;

	startPoiOverlay startPoi;
	CheckPoiOverlay vaguePoi;
	TeamPoiOverlay teamPoiOverlay;
	
	//�켣ͼ��
	GraphicsOverlay pathOverlay;
	
	//��λ����������ͼ��
	MyLocationOverlay mLocationOverlay;
	LocationListener mLocationListener;
	MKMapViewListener mMapListener;
	BDNotifyListener mNotifyListner;	
	
	private MKSearch mSearch;
	
	//������	
	SensorManager manager;
	Sensor sensor;	
	float angle;
	
	//��������,���������
	String[][] checkPoi;
	ArrayList<Boolean> checkArrive = new ArrayList<Boolean>();
	String[][] notifyPoi;
	
	ArrayList<GeoPoint> myLocPoi = new ArrayList<GeoPoint>();
	ArrayList<GeoPoint> teamLocPoi = new ArrayList<GeoPoint>();
	boolean canStart = true;
	
	int arrivedPoi = 0;
	boolean isStart = false;
	boolean isOver = false;
	
	Button rotate;
	Button end;
	Button save;
	PopupWindow popupWindow;
	private LinearLayout isshow;
	boolean flag = false;
	
	Handler handler;
	Runnable teamPoiRun;			//��Աλ��
	Runnable finishedPoiRun;		//��Ա�������		
	Runnable startActRun;			//�ӳ���ʼ�ź�
	Runnable endContextRun;
	Runnable getExitRun;			//��Ա���ӳ����˳���������
	
	MyThread myThread;
	
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
	
	// �����Ϣ
	String teamId;
	String userId; 
	private String username;
	String actId;	
	String teamName;
	int channel;
	final String type = "team";
	boolean isHead;
	int limitTime;
	int teamNum = 0;
	int teamMax;		//������������
	
	
	String city = "����";	
	String usedExitTeammate;
	String usedCapName;
	String[][] trails;
	    
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.context_groupview);
		
		MapApplication data = ((MapApplication)getApplicationContext()); 
		userId = data.getUserId();
		username = data.getUserName();
		actId = data.getActId();
		limitTime = data.getLimitTime();
		
		channel = Integer.parseInt(actId);		
		teamId = data.getTeamId();
		teamName = data.getTeamName();
		isHead = data.getIsHead();
		teamMax = data.getTeamMax();
		
		//actId = this.getIntent().getExtras().getString("sid");

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
		pathOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(pathOverlay);
				
		// ��Ӷ�λͼ��
        mLocationOverlay = new MyLocationOverlay(mMapView);
        mLocationOverlay.enableCompass();
		mMapView.getOverlays().add(mLocationOverlay);
		// ��ӵ���ͼ��
		routeOverlay = new RouteOverlay(Group.this, mMapView);		
		mMapView.getOverlays().add(routeOverlay);
		// ��Ӽ���ͼ��
		vaguePoi = new CheckPoiOverlay(null, mMapView);
		mMapView.getOverlays().add(vaguePoi);
		// ������ͼ��
		startPoi = new startPoiOverlay(null, mMapView);
		mMapView.getOverlays().add(startPoi);
		// ��Ӷ���λ��ͼ��
		teamPoiOverlay = new TeamPoiOverlay(null, mMapView);
		mMapView.getOverlays().add(teamPoiOverlay);
		
		// �����д����Ϣ
		new JoinGroup().execute(actId,teamId,userId,teamName,isHead);		
		
		// �������
		setStartPoi();
	
		handler = new Handler();
		
		
		//�����˳���Ϣ
		final GetExitCapUsers getExitCapUsers = new GetExitCapUsers();
		usedExitTeammate = getExitCapUsers.doInBackground(teamId,"0");
		usedCapName = getExitCapUsers.doInBackground(teamId, "1");
		
		// TODO ��ö�Ա�˳�
		getExitRun = new Runnable(){
			@Override
			public void run() {
				if (!isOver){
					String mes = null;
					String exitTeammate = getExitCapUsers.doInBackground(teamId,"0");	//�˳�����
					String newCap = getExitCapUsers.doInBackground(teamId, "1");
					if (!exitTeammate.equals(usedExitTeammate) && !exitTeammate.equals("-1")){									
						mes = exitTeammate+"�˳���";				
						
						// �Ƿ��Ƕӳ��˳�
						if (!(usedCapName.equals(newCap))){
							mes = mes+"�¶ӳ�Ϊ"+newCap+"!";
							if (newCap.equals(username))
								isHead = true;
						}
						if (mes != null)
							Toast.makeText(Group.this, mes, Toast.LENGTH_SHORT).show();	
					}
					usedExitTeammate = exitTeammate;
					usedCapName = newCap;
					handler.postDelayed(this, 4000);
				}
				else
					handler.removeCallbacks(this);
			}
		};
		//handler.post(getExitRun);
		
		//����ʱ
		endContextRun = new Runnable(){
			@Override
			public void run() {				
				//�ź�ȷ��...
				Toast.makeText(Group.this, "����ʱ�䵽��", Toast.LENGTH_SHORT).show();
				isOver = true;
				locationClient.stop();
				
        		save.setVisibility(View.VISIBLE);
        		mMapView.getOverlays().clear();
				pathOverlay = new GraphicsOverlay(mMapView);
				pathOverlay.setData(myPath());
				mMapView.getOverlays().add(pathOverlay);
				mMapView.refresh();
				Toast.makeText(Group.this, "�������ı��λ�켣", Toast.LENGTH_LONG).show();
				
				//֪ͨ�����Լ��˳�
				new ExitActivity().execute(teamId,userId,isHead);
				rotate.setText("");
				rotate.setHint("·��");
				rotate.setClickable(false);
				end.setText("�����뿪");
				end.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// ��תҳ��
						Intent intent = new Intent(Group.this,SearchAct.class);
						startActivity(intent);
						mMapView.destroy();
						finish();
					}							
				});				
			}			
		};
		
		// TODO ��ö�Աλ��
		//final double lat = 37.5299;
		//final double lon = 122.0506;
		
		final GetUsersTrail getUsersTrail = new GetUsersTrail();	
		teamPoiRun = new Runnable(){		
			@Override
			public void run() {
				if (!isOver){
					//�ϴ�����λ��
					//new UsersTrail().execute(teamId,userId,lat+"",lon+"");
					new UsersTrail().execute(teamId,userId,locData.latitude,locData.longitude);
					trails = getUsersTrail.doInBackground(teamId,userId);
				
					//////
					//Toast.makeText(Group.this, trails[0][0]+"+"+trails[0][1]+"+"+trails[0][2], Toast.LENGTH_SHORT).show();	
					teamPoiOverlay.removeAll();
					if (!trails[0][0].equals("-1")){
						teamNum = trails.length;
						teamLocPoi.clear();
					
						//teamPoiOverlay.removeAll();
						for (int i=0;i<teamNum;i++){
							double lat = Double.valueOf(trails[i][0]);
							double lon = Double.valueOf(trails[i][1]);
							GeoPoint teamPoi = new GeoPoint((int)(lat * 1E6),(int)(lon * 1E6));
							OverlayItem item = new OverlayItem(teamPoi, trails[i][2], null);
							Drawable marker = getResources().getDrawable(R.drawable.teammate);
							//��item����marker
							item.setMarker(marker);
							//��ͼ�������item
							teamPoiOverlay.addItem(item);
							
							teamLocPoi.add(teamPoi);
						}
						mMapView.refresh();		
					}
					handler.postDelayed(this, 3000);
				}
				else
					handler.removeCallbacks(this);	
			}	
		};
		//handler.post(teamPoiRun);
		
		myThread = new MyThread(handler);
		myThread.start();
		
		// ��öӳ���ʼ�ź�
		final GetStartSign getStartSign = new GetStartSign();
		startActRun = new Runnable(){
			@Override
			public void run() {
				if (!isStart){
					String sign = getStartSign.doInBackground(teamId);
					if (sign.equals("1")){
						setCheckPoi();
						isStart = true;
						Toast.makeText(Group.this, "������ʼ��ʱ��"+limitTime/(1000*60)+"����", Toast.LENGTH_SHORT).show();
						handler.postDelayed(endContextRun, limitTime);
						rotate.setVisibility(View.VISIBLE);
					}	
					handler.postDelayed(this, 1000);
				}
				else
					handler.removeCallbacks(this);
			}		
		};
		handler.post(startActRun);
			
		// TODO ��Ա��ɼ���
		final GetPointSign getPointSign = new GetPointSign();
		finishedPoiRun = new Runnable(){
			@Override
			public void run() {
				if (!isOver){
					// �ѵ������				
					String[] teamArrive = getPointSign.doInBackground(teamId);	//��ʼ����Ϊ��
				
					//Toast.makeText(Group.this, arrivedPoi[0]+" ! "+ arrivedPoi[1], Toast.LENGTH_SHORT).show();
					if (!(teamArrive[1].equals("-1"))){
						int index = Integer.parseInt(teamArrive[1]);			
						if (!checkArrive.get(index)){
							checkArrive.set(index, true);
							Toast.makeText(Group.this, "���"+teamArrive[0]+"�ɹ��ִ���һ�����㣡", Toast.LENGTH_SHORT).show();
							OverlayItem item = vaguePoi.getItem(index);
							Drawable marker = getResources().getDrawable(R.drawable.finishedpoi);
							marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());		
							item.setMarker(marker);			
							vaguePoi.updateItem(item);							
							mMapView.refresh();
							arrivedPoi++;
							if (arrivedPoi == checkArrive.size()){
								locationClient.stop();
								
								handler.removeCallbacks(teamPoiRun);
								handler.removeCallbacks(endContextRun);
								handler.removeCallbacks(getExitRun);
								isOver = true;
								save.setVisibility(View.VISIBLE);
								mMapView.getOverlays().clear();
								pathOverlay = new GraphicsOverlay(mMapView);
								pathOverlay.setData(myPath());
								mMapView.getOverlays().add(pathOverlay);
								mMapView.refresh();
								Toast.makeText(Group.this, "�ѽ������������ı��λ�켣", Toast.LENGTH_LONG).show();
						
								rotate.setText("");
								rotate.setHint("·��");
								rotate.setClickable(false);
								end.setText("�����뿪");
								end.setOnClickListener(new OnClickListener(){
									@Override
									public void onClick(View v) {
										// ��תҳ��
										new ExitActivity().execute(teamId,userId,isHead);
										leave();
										Intent intent = new Intent(Group.this,RecordOfTeam.class);
										startActivity(intent);
										mMapView.destroy();
										finish();
									}							
								});	
								//�ϴ�ʱ��
								if (isHead)
									new TimeEnd().execute(teamId,actId,arrivedPoi,"teamend");
							}		
						}
					}//end of outer if		
					handler.postDelayed(this, 6000);
				}
				else
					handler.removeCallbacks(this);	
			}		
		};	
	
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
					Toast.makeText(Group.this,title,Toast.LENGTH_SHORT).show();
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
		        	Toast.makeText(Group.this, "δ�ҵ��ڴ濨��", Toast.LENGTH_LONG).show();
		        	return;
		        }
				String filePath = root.toString()+"/CityTreasureHunter/GroupContext";		// ���·��
				
				// ����
		        File file = new File(filePath);
		        if (!file.exists()) { 
					file.mkdirs();  //���������ļ���         
		        }
		        file = new File(filePath,filename);  
		        try {
					file.createNewFile();
				} catch (IOException e) {
					Toast.makeText(Group.this, "�ļ��Ѵ��ڣ�", Toast.LENGTH_SHORT).show();    
				}
		        
		        FileOutputStream fos = null;
		        try {   		        	
		        	fos = new FileOutputStream(file);  
		            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);   
		            fos.flush();   
		            fos.close();
		            Toast.makeText(Group.this, "�ɹ���"+"ͼƬ����·��:"+filePath+"\\", Toast.LENGTH_SHORT).show();   
		        }catch (FileNotFoundException e) {   
		        	Toast.makeText(Group.this, "δ�ҵ��ļ���", Toast.LENGTH_SHORT).show();    
		        }catch (IOException e) {   
		        	Toast.makeText(Group.this, "�洢����", Toast.LENGTH_SHORT).show();   
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
		
		//�Ƽ�·��
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, (MKSearchListener) new MySearchListener());
				
		/*
         * ��λ���
         */
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
                	Toast.makeText(Group.this, "��λʧ��", Toast.LENGTH_SHORT).show();
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
                if (isStart && writeIn == 0 && locData.latitude != 0)
                	myLocPoi.add(p);
                writeIn = (writeIn+1)%3;
                
            }
            @Override 
            public void onReceivePoi(BDLocation location) { 
            }              
        });        
		locationClient.start();
		//�����ζ�λ
		locationClient.requestLocation();		
              
        //TODO ��������
        //һ��һ��		
		
        GetNotifyPoi getNotyfyPoi = new GetNotifyPoi();
        notifyPoi = getNotyfyPoi.doInBackground(actId,type);
        int notifyLen = notifyPoi.length;
        for (int i=0;i<notifyLen;i++){
        	final String alertText = notifyPoi[i][2];
        	double notifyLat = Double.valueOf(notifyPoi[i][0]);
			double notifyLon = Double.valueOf(notifyPoi[i][1]);
        	BDNotifyListener mNotifyListner = new BDNotifyListener(){      		
        		@Override
            	public void onNotify(BDLocation Location, float distance) {  
            		//��
            		Vibrator vibrator = (Vibrator) Group.this.getSystemService(Context.VIBRATOR_SERVICE);
            		vibrator.vibrate(1000); 
            		Toast.makeText(Group.this, alertText, Toast.LENGTH_SHORT).show();
            		super.onNotify(Location, distance);
            	}
        	};      	
        	mNotifyListner.SetNotifyLocation(notifyLat, notifyLon, 200, "bd09ll");
        	locationClient.registerNotify(mNotifyListner);
        }
		 
        rotate = (Button)findViewById(R.id.rotate);
        end = (Button)findViewById(R.id.end);
        save = (Button)findViewById(R.id.save); 
        
        isshow = (LinearLayout)this.findViewById(R.id.isshow);
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
					Vibrator vibrator = (Vibrator) Group.this.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(1000);
					Toast.makeText(Group.this,"�𶯣�",Toast.LENGTH_SHORT).show();
				}	
				sender = false;
			}		
		});

        rotate.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
        
        rotate.setOnClickListener(new OnClickListener(){
			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View v) {
				// TODO ·����ʼ�붨ʱ����
				//��ʱ����·��
				rotateShow = new Timer();
				rotateShow.schedule(new TimerTask(){
					@Override
					public void run() {
						Message message = new Message();  
			            message.what = 1;  
			            rotateHandler.sendMessage(message);		            
					}//end of run
				}, 0);
				
				rotateHandler = new Handler(){					
					@SuppressLint("HandlerLeak")
					public void handleMessage(Message msg) {  
			            if (msg.what == 1) {  
			            	locationControl = 0;	//��ͣ��λʱ������
							final MKPlanNode pre = new MKPlanNode();
							final MKPlanNode cur = new MKPlanNode();		
							double distance = Double.MAX_VALUE;			
							GeoPoint myPoi = new GeoPoint((int) (locData.latitude * 1E6), (int)(locData.longitude * 1E6));
							pre.pt = myPoi;
							
							int length = checkPoi.length;
							for (int i=0;i<length;i++){
								if (!checkArrive.get(i)){
									double lat = Double.valueOf(checkPoi[i][0]);
									double lon = Double.valueOf(checkPoi[i][1]);
									GeoPoint p = new GeoPoint((int) (lat * 1E6), (int)(lon * 1E6));
									double dis = DistanceUtil.getDistance(myPoi, p);
									if (distance > dis){
										distance = dis;
										cur.pt = p;
									}
								}
							}
							mSearch.walkingSearch(city, pre, city, cur);		
			            }  
			            else{
			            	// ֹͣ���·��
			            	Toast.makeText(Group.this, "��ʾʱ�䵽��", Toast.LENGTH_SHORT).show();
							locationControl = 1;							
							mMapView.getOverlays().remove(routeOverlay);
							routeOverlay = new RouteOverlay(Group.this, mMapView);		
							mMapView.getOverlays().add(routeOverlay);
							mMapView.refresh();
							rotateShow.cancel();
							
							rotate.setText("·��");
				            rotate.setHint("");
				            rotate.setClickable(true);
			            }
			        };			        
				};//end of Handler
			}        	
        });
        
        //TODO ��������       
        end.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//locData.latitude = 37.5299;
        		//locData.longitude = 122.0606;
        		
				new AlertDialog.Builder(Group.this) 
		        .setTitle("�˳�")  
		        .setMessage("��ȷ��Ҫ�˳�������")  
		        .setNegativeButton("ȡ��",null)  
		        .setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {  
		        	public void onClick(DialogInterface dialog,int whichButton) { 
		        		new ExitActivity().execute(teamId,userId,isHead);
		        		
		        		//handler.removeCallbacks(teamPoiRun);	
		        		//handler.removeCallbacks(getExitRun);
		        		myThread.stopHandler();
		        		
		        		isOver = true;
		        		locationClient.stop();
		        		
		        		if (!isStart){
		        			handler.removeCallbacks(startActRun);
		        			mMapView.getOverlays().clear();
		        			startActRun = null;
		        		}
		        		else{
		        			handler.removeCallbacks(finishedPoiRun);
		        			handler.removeCallbacks(endContextRun);

		        			//д��ʱ�䣨���һλ�˳���
		        			if (teamNum == 1 && trails[0][0].equals("-1")){
								new TimeEnd().execute(teamId,actId,arrivedPoi,"teamend");
								Toast.makeText(Group.this, "ʱ���¼��", Toast.LENGTH_SHORT).show();
		        			}
		        			
		        			save.setVisibility(View.VISIBLE);
		        			mMapView.getOverlays().clear();
		        			pathOverlay = new GraphicsOverlay(mMapView);
		        			pathOverlay.setData(myPath());
		        			mMapView.getOverlays().add(pathOverlay);
		        			mMapView.refresh();
		        			Toast.makeText(Group.this, "�������ı��λ�켣", Toast.LENGTH_SHORT).show();
		        		}
						rotate.setText("");
						rotate.setHint("·��");
						rotate.setClickable(false);
						end.setText("�����뿪");
						end.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// ��תҳ��
								leave();						
								Intent intent = null;
								if (isStart)
									intent = new Intent(Group.this,RecordOfTeam.class);
								else
									intent = new Intent(Group.this,SearchAct.class);
								startActivity(intent);
								mMapView.destroy();
								finish();
							}							
						});
		            }  
		        }).show();	
			} 			
        });      
        
        save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Toast.makeText(Group.this, "�����У����Ժ�", Toast.LENGTH_LONG).show();
				mMapView.getCurrentMap();			
			}       	
        }); 
        
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
		thread = new Thread(Group.this);
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
			Toast.makeText(Group.this,"���Ͳ���Ϊ�գ�",Toast.LENGTH_SHORT).show();
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
				chat_in = in.readUTF();
				chat_in = chat_in+"\n";
				//
				myHandler.sendMessage(myHandler.obtainMessage());
			}catch(IOException e){
			}
		}
	}
	@SuppressLint("HandlerLeak")
	
	//ˢ�½���(ԭ���޷��ٷ����̸߳��½���)
	Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			
			historyEdit.append(chat_in);
			super.handleMessage(msg);
		}
	};
	
	//TODO ���ü���
	private void setCheckPoi(){
		//handler.removeCallbacks(startActRun);
		//startActRun = null;
		isStart = true;
		
		mMapView.getOverlays().remove(startPoi);	
		GetCheckPoi getCheckPoi = new GetCheckPoi();
		checkPoi = getCheckPoi.doInBackground(actId,type);
		int length = checkPoi.length;
				
		for (int i=0;i<length;i++){
			double lat = Double.valueOf(checkPoi[i][0]).doubleValue();
			double lon = Double.valueOf(checkPoi[i][1]).doubleValue();
					
			GeoPoint p = new GeoPoint((int) (lat * 1E6), (int)(lon * 1E6));
			OverlayItem item = new OverlayItem(p, "", null);		
			Drawable marker = getResources().getDrawable(R.drawable.checkpoi);
					
			item.setMarker(marker);
			vaguePoi.addItem(item);
			vaguePoi.setQA(checkPoi[i][2], checkPoi[i][3]);
			checkArrive.add(false);
		}
		mMapView.refresh();
	
		handler.post(finishedPoiRun);		
	}
	
	// TODO �������
	private void setStartPoi(){
		GetOrigin getOrigin = new GetOrigin();
		String getStartPoi[] = getOrigin.doInBackground(type, actId);
		double lat = Double.valueOf(getStartPoi[0]); 
		double lon = Double.valueOf(getStartPoi[1]); 		
		
		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int)(lon * 1E6));
		OverlayItem item = new OverlayItem(p, "", null);		
		Drawable marker = getResources().getDrawable(R.drawable.start_context);
		item.setMarker(marker);
		startPoi.addItem(item);	
		mMapView.refresh();
	}
	
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
	    				isOver = true;
	    				locationClient.stop();
	    				
	    				new ExitActivity().execute(teamId,userId,isHead);
	    				//handler.removeCallbacks(teamPoiRun);
	    				//handler.removeCallbacks(getExitRun);
	    				myThread.stopHandler();
	    				
	    				if (!isStart){
		        			handler.removeCallbacks(startActRun);
		        			mMapView.getOverlays().clear();
	    				}
		        		else{
		        			//�ϴ�ʱ�䣨��;�˵����һ�ˣ�
		        			if (teamNum == 0){
								new TimeEnd().execute(teamId,actId,arrivedPoi,"teamend");
								Toast.makeText(Group.this, "ʱ���¼��", Toast.LENGTH_SHORT).show();
		        			}
		        			
		        			handler.removeCallbacks(finishedPoiRun);
		        			handler.removeCallbacks(endContextRun);
		        			
		        			new TimeEnd().execute(teamId,actId,arrivedPoi,"teamend");
		        			save.setVisibility(View.VISIBLE);
			        		mMapView.getOverlays().clear();
							pathOverlay = new GraphicsOverlay(mMapView);
							pathOverlay.setData(myPath());
							mMapView.getOverlays().add(pathOverlay);
							mMapView.refresh();
							Toast.makeText(Group.this, "�ѽ������������ı��λ�켣", Toast.LENGTH_LONG).show();
		        		}
	    				    				
						rotate.setText("");
						rotate.setHint("·��");
						rotate.setClickable(false);
						end.setText("�����뿪");
						end.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// ��תҳ��
								leave();
								Intent intent = null;
								if (isStart)
									intent = new Intent(Group.this,RecordOfTeam.class);
								else
									intent = new Intent(Group.this,SearchAct.class);
								startActivity(intent);
								mMapView.destroy();
								finish();
							}							
						}); 
	    			}  
	    		}).show(); 
	    	}
	        else{
	        	Intent intent = null;
	        	if (isOver)
					intent = new Intent(Group.this,RecordOfTeam.class);
				else
					intent = new Intent(Group.this,SearchAct.class);
				startActivity(intent);
				mMapView.destroy();
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
	
	public class MySearchListener implements MKSearchListener{

		@Override
		public void onGetAddrResult(MKAddrInfo res, int arg1) {
			city = res.addressComponents.city;
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {	
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {	
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {	
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {	
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {	
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			// TODO �Ƽ�·�ߴ���
			if (res.getPlan(0).getRoute(0) != null){
				routeOverlay.setData(res.getPlan(0).getRoute(0));		    
				mMapView.refresh();
				rotate.setText("");
				rotate.setHint("·��");
				rotate.setClickable(false);
				Toast.makeText(Group.this, "�Ƽ�·������ʱ��Ϊ��" + rotateTime/(1000*60) +"���ӣ�", Toast.LENGTH_SHORT).show();
				extra += 3;
				rotateShow.schedule(new TimerTask(){
					@Override
					public void run() {
						Message message = new Message();  
						message.what = 0;  
						rotateHandler.sendMessage(message);					
					}							
				}, rotateTime);
				mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
				mMapView.getController().animateTo(res.getStart().pt);
			}
			else
				Toast.makeText(Group.this, "δ����������·����", Toast.LENGTH_SHORT).show();
		}		
	}
	
	public Graphic myPath(){
		// ---��õ�---
		/*
		GeoPoint gpoint1 = new GeoPoint((int) (37.5299 * 1E6), (int)(122.0606 * 1E6));
		GeoPoint gpoint2 = new GeoPoint((int) (37.5302 * 1E6), (int)(122.0654 * 1E6));
		GeoPoint gpoint3 = new GeoPoint((int) (37.5273 * 1E6), (int)(122.0683 * 1E6));
			
		locPoi.add(gpoint1);
		locPoi.add(gpoint2);
		locPoi.add(gpoint3);*/
		
		int len = myLocPoi.size();
		
		GeoPoint[] geolist = new GeoPoint[len];
		
		for (int i=0;i<len;i++){
			geolist[i] = myLocPoi.get(i);
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
			Toast.makeText(Group.this, "�޵�ɼ�¼��", Toast.LENGTH_SHORT).show();
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
		color.red = 0;
		color.green = 0;
		color.blue = 255;
		color.alpha = 126;
		symbol.setLineSymbol(color, 4);
		Graphic graphic = new Graphic(geometry, symbol);
		return graphic;
	}
	
	//TODO --����ͼ��--
	class CheckPoiOverlay extends ItemizedOverlay<OverlayItem>{
		private ArrayList<String> question = new ArrayList<String>();
		private ArrayList<String> answer = new ArrayList<String>();
		
		public CheckPoiOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}
		
		public void setQA(String que, String ans){
			question.add(que);
			answer.add(ans); 
		}
		
		protected boolean onTap(final int index){
			if (checkArrive.get(index) == false){
				
				GeoPoint myPoi = new GeoPoint((int) (locData.latitude * 1E6), (int)(locData.longitude * 1E6));
				double distance = DistanceUtil.getDistance(myPoi, this.getItem(index).getPoint());
				if (distance > 20000.0)
					Toast.makeText(Group.this, "����ɻش�Χ����"+(double)Math.round((distance-200.0)*10000)/10000+"�ף�", Toast.LENGTH_SHORT).show();
				else{		
					final EditText ansEdit = new EditText(Group.this);
					new AlertDialog.Builder(Group.this)
					.setTitle("������ü���Ĵ𰸣�")
					.setMessage("���⣺"+question.get(index))
					.setView(ansEdit)
					.setPositiveButton("�ύ", new DialogInterface.OnClickListener(){
						@SuppressWarnings("unchecked")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (ansEdit.getText().toString().equals(answer.get(index))){
								// ֪ͨ����
								new PointSign().execute(teamId,userId,index);
					
								OverlayItem item = CheckPoiOverlay.this.getItem(index);
								Drawable marker = getResources().getDrawable(R.drawable.finishedpoi);
								marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());		
								item.setMarker(marker);			
								CheckPoiOverlay.this.updateItem(item);							
								//isTaped.set(index, true);
								checkArrive.set(index, true);
								Toast.makeText(Group.this, "�ش���ȷ��", Toast.LENGTH_SHORT).show();	
								mMapView.refresh();
								arrivedPoi++;
								if (arrivedPoi == checkArrive.size()){
									//�ӳ��ϴ�ʱ��
									locationClient.stop();
									
									if (isHead)
										new TimeEnd().execute(teamId,actId,arrivedPoi,"teamend");
									
									handler.removeCallbacks(teamPoiRun);
									handler.removeCallbacks(finishedPoiRun);
									
									isOver = true;
									save.setVisibility(View.VISIBLE);
					        		mMapView.getOverlays().clear();
									pathOverlay = new GraphicsOverlay(mMapView);
									pathOverlay.setData(myPath());
									mMapView.getOverlays().add(pathOverlay);
									mMapView.refresh();
									Toast.makeText(Group.this, "�ѽ������������ı��λ�켣", Toast.LENGTH_LONG).show();
									
									rotate.setText("");
									rotate.setHint("·��");
									rotate.setClickable(false);
									end.setText("�����뿪");
									end.setOnClickListener(new OnClickListener(){
										@Override
										public void onClick(View v) {
											// ��תҳ��
											Intent intent = new Intent(Group.this,RecordOfTeam.class);
											startActivity(intent);
											mMapView.destroy();
											finish();
										}							
									});	
								}		
							}
							else
								Toast.makeText(Group.this, "�ش����", Toast.LENGTH_SHORT).show();
						}					
					})
					.setNegativeButton("ȡ��", null)
					.show();
				}
			}
			return true;
		}
	}
	
	//TODO --���ͼ��--
	class startPoiOverlay extends ItemizedOverlay<OverlayItem>{

		public startPoiOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}	
		
		@SuppressWarnings("unchecked")
		protected boolean onTap(final int index){
			GeoPoint myPoi = new GeoPoint((int) (locData.latitude * 1E6), (int)(locData.longitude * 1E6));
			double distance = DistanceUtil.getDistance(myPoi, this.getItem(index).getPoint());
			if (distance > 30000.0)
				Toast.makeText(Group.this, "�����������"+(double)Math.round((distance-300.0)*10000)/10000+"�ף�", Toast.LENGTH_SHORT).show();
			else{
				if (isHead){
					if (teamNum == teamMax){
						canStart = true;
						for (int i=0;i<teamNum;i++){
							GeoPoint teamPoi = teamLocPoi.get(i);
							double dist = DistanceUtil.getDistance(teamPoi, this.getItem(index).getPoint());
							if (dist > 30000.0){
								canStart = false;
								break;
							}	
						}
						if (canStart){
							setCheckPoi();
							//��Ա֪ͨ
							rotate.setVisibility(View.VISIBLE);
							Toast.makeText(Group.this, "������ʼ��ʱ��"+limitTime/(1000*60)+"����", Toast.LENGTH_SHORT).show();
							handler.postDelayed(endContextRun, limitTime);
							//��¼ʱ��
							new StartGame().execute(teamId,actId,"teamstart");
						}
					}
					else
						Toast.makeText(Group.this, "��Ա��δ���룡", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(Group.this, "���г�Ա�������300���ں��ɶӳ������ʼ", Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		}
	}
	
	// TODO ---��Աͼ��---
	class TeamPoiOverlay extends ItemizedOverlay<OverlayItem>{

		public TeamPoiOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}
		
		protected boolean onTap(int index){
			String name = this.getItem(index).getTitle();
			Toast.makeText(Group.this, "��Ա��"+name, Toast.LENGTH_SHORT).show();
			return true;			
		}
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
	
	// TODO �߳�
	class MyThread extends Thread{
		Handler mHandler;
		
		public MyThread(Handler handler){
			mHandler = handler;
		}
		
		public void run(){
			mHandler.postDelayed(teamPoiRun,0);
			mHandler.postDelayed(getExitRun,100);
		}
		
		public void stopHandler(){
			mHandler.removeCallbacks(teamPoiRun);
			mHandler.removeCallbacks(getExitRun);
		}
	}
	
	@Override 
    protected void onDestroy() { 
        if (locationClient != null && locationClient.isStarted()) { 
            locationClient.stop();            
            locationClient = null;
        } 
        //handler.removeCallbacks(startActRun);
        if (handler != null)
        	handler = null;
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

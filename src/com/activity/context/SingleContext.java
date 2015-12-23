package com.activity.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.function.singlecontext.GetCheckPoi;
import com.function.singlecontext.GetNotifyPoi;
import com.function.singlecontext.GetOrigin;
import com.function.singlecontext.StartGame;
import com.function.singlecontext.TimeEnd;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SingleContext extends Activity {
	private MapView mMapView;
	private MapController mMapController;
	
	private LocationClient locationClient;
	LocationData locData;	
	private static final int UPDATE_TIME = 2000;
	
	private int locationControl = 1;
	private final int rotateTime = 1000*20;	//��ʾ·��ʱ�䣨MS��
	int extra = 0;
	
	Handler rotateHandler;
	RouteOverlay routeOverlay;
	Timer rotateShow;

	startPoiOverlay startPoi;
	checkPoiOverlay vaguePoi;
	
	//�켣��ģ������ͼ��
	GraphicsOverlay pathOverlay;
	
	//��λ����������ͼ��
	MyLocationOverlay mLocationOverlay;
	LocationListener mLocationListener;
	MKMapViewListener mMapListener;
	BDNotifyListener mNotifyListner;	
	BDNotifyListener mNotifyListner2;
	
	ArrayList<GeoPoint> locPoi = new ArrayList<GeoPoint>();
	int writeIn = 0;
	
	//������	
	SensorManager manager;
	Sensor sensor;	
	float angle;
	
	//��������,���������
	String[][] checkPoi;
	ArrayList<Boolean> checkArrive = new ArrayList<Boolean>();
	String[][] notifyPoi;
	
	private MKSearch mSearch;
	
	int arrivedPoi = 0;
	boolean isStart = false;
	boolean isOver = false;
	
	Button rotate;
	Button end;
	Button save;
	
	Runnable endContext;
	Handler handler;
	
	//�����Ϣ
	String userId;
	String username;
	String actId;
	
	final String type = "single";	
	int limitTime;
	String city = "����";
	    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.context_singlecontext);
		
		MapApplication data = ((MapApplication)getApplicationContext()); 
		userId = data.getUserId();
		username = data.getUserName();
		actId = data.getActId();
		limitTime = data.getLimitTime();
		
		//userId = this.getIntent().getExtras().getString("userId");
		//username = this.getIntent().getExtras().getString("username");
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
		routeOverlay = new RouteOverlay(SingleContext.this, mMapView);		
		mMapView.getOverlays().add(routeOverlay);
		// ��Ӽ���ͼ��
		vaguePoi = new checkPoiOverlay(null, mMapView);
		mMapView.getOverlays().add(vaguePoi);
		// ������ͼ��
		startPoi = new startPoiOverlay(null, mMapView);
		mMapView.getOverlays().add(startPoi);
		
		// �������
		setStartPoi();
		
		handler = new Handler();
		// TODO ʱ�䵽
		endContext = new Runnable(){
			@SuppressWarnings("unchecked")
			@Override
			public void run() {				
				Toast.makeText(SingleContext.this, "����ʱ�䵽��", Toast.LENGTH_SHORT).show();
				//д��ʱ��
    			new TimeEnd().execute(userId,actId,arrivedPoi,"singleend");
				isOver = true;
				locationClient.stop();
				
        		save.setVisibility(View.VISIBLE);
        		mMapView.getOverlays().clear();
				pathOverlay = new GraphicsOverlay(mMapView);
				pathOverlay.setData(myPath());
				mMapView.getOverlays().add(pathOverlay);
				mMapView.refresh();
				Toast.makeText(SingleContext.this, "�������ı��λ�켣", Toast.LENGTH_LONG).show();
				
				rotate.setText("");
				rotate.setHint("·��");
				rotate.setClickable(false);
				end.setText("�����뿪");
				end.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// ��תҳ��
						Intent intent = new Intent(SingleContext.this,RecordOfSingle.class);
						startActivity(intent);
						mMapView.destroy();
						finish();
					}							
				});				
			}			
		};
		
		
		/*
		mLocationListener = new LocationListener(){		
			public void onLocationChanged(Location location) {
				if (location != null){
					GeoPoint pt = new GeoPoint((int)(location.getLatitude()*1E6),
							(int)(location.getLongitude()*1E6));
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
        };*/
	
		mMapListener = new MKMapViewListener(){
		   	@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
		        //�����ͼ����¼�
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					Toast.makeText(SingleContext.this,title,Toast.LENGTH_SHORT).show();
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
		        	Toast.makeText(SingleContext.this, "δ�ҵ��ڴ濨��", Toast.LENGTH_LONG).show();
		        	return;
		        }
				String filePath = root.toString()+"/CityTreasureHunter/SingleContext";		// ���·��
				// ����
		        File file = new File(filePath);
		        if (!file.exists()) { 
					file.mkdirs();  //���������ļ���         
		        }
		        file = new File(filePath,filename);  
		        try {
					file.createNewFile();
				} catch (IOException e) {
					Toast.makeText(SingleContext.this, "�ļ��Ѵ��ڣ�", Toast.LENGTH_SHORT).show();    
				}
		        
		        FileOutputStream fos = null;
		        try {   
		        	// -------??		        	
		        	fos = new FileOutputStream(file);  
		            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);   
		            fos.flush();   
		            fos.close();
		            Toast.makeText(SingleContext.this, "�ɹ���"+"ͼƬ����·��:"+filePath+"\\", Toast.LENGTH_SHORT).show();   
		        }catch (FileNotFoundException e) {   
		        	Toast.makeText(SingleContext.this, "δ�ҵ��ļ���", Toast.LENGTH_SHORT).show();    
		        }catch (IOException e) {   
		        	Toast.makeText(SingleContext.this, "�洢����", Toast.LENGTH_SHORT).show();   
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
                	Toast.makeText(SingleContext.this, "��λʧ��", Toast.LENGTH_SHORT).show();
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
            	
            	if (writeIn == 0)
            		locPoi.add(p);
            	writeIn = (writeIn+1)%3;            	
            	
                if (locationControl == 1){
                	mMapController.animateTo(p);
                	locationControl = 0;
                }
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
        	double lat = Double.valueOf(notifyPoi[i][0]);
			double lon = Double.valueOf(notifyPoi[i][1]);
        	BDNotifyListener mNotifyListner = new BDNotifyListener(){      		
        		@Override
            	public void onNotify(BDLocation Location, float distance) {  
            		//��
            		Vibrator vibrator = (Vibrator) SingleContext.this.getSystemService(Context.VIBRATOR_SERVICE);
            		vibrator.vibrate(1000); 
            		Toast.makeText(SingleContext.this, alertText, Toast.LENGTH_SHORT).show();
            		super.onNotify(Location, distance);
            	}
        	};      	
        	mNotifyListner.SetNotifyLocation(lat, lon, 200, "bd09ll");
        	locationClient.registerNotify(mNotifyListner);
        }
        
        rotate = (Button)findViewById(R.id.rotate);
        end = (Button)findViewById(R.id.end);
        save = (Button)findViewById(R.id.save); 
        
        rotate.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
        
        rotate.setOnClickListener(new OnClickListener(){
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
			            	locationControl = 0;					//��ͣ��λʱ������
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
			            	Toast.makeText(SingleContext.this, "��ʾʱ�䵽��", Toast.LENGTH_SHORT).show();
							locationControl = 1;							
							mMapView.getOverlays().remove(routeOverlay);
							routeOverlay = new RouteOverlay(SingleContext.this, mMapView);		
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
				//locData.latitude = 37.5099;
        		//locData.longitude = 122.0606;
        		
				new AlertDialog.Builder(SingleContext.this) 
		        .setTitle("�˳�")  
		        .setMessage("��ȷ��Ҫ�˳�������")  
		        .setNegativeButton("ȡ��",null)  
		        .setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {  
		        	@SuppressWarnings("unchecked")
					public void onClick(DialogInterface dialog,int whichButton) {  
		        		isOver = true;
		        		locationClient.stop();
		        		
		        		if (isStart){
		        			handler.removeCallbacks(endContext);
		        			save.setVisibility(View.VISIBLE);
		        			mMapView.getOverlays().clear();
		        			pathOverlay = new GraphicsOverlay(mMapView);
		        			pathOverlay.setData(myPath());
		        			mMapView.getOverlays().add(pathOverlay);
		        			mMapView.refresh();
		        			Toast.makeText(SingleContext.this, "�������ı��λ�켣", Toast.LENGTH_LONG).show();
		        			
		        			//д��ʱ��
		        			new TimeEnd().execute(userId,actId,arrivedPoi,"singleend");
		        		}
		        		rotate.setText("");
		        		rotate.setHint("·��");
		        		rotate.setClickable(false);
		        		end.setText("�����뿪");
						end.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// ��תҳ��
								Intent intent = null;
								if (isStart)
									intent = new Intent(SingleContext.this,RecordOfSingle.class);
								else
									intent = new Intent(SingleContext.this,SearchAct.class);
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
				Toast.makeText(SingleContext.this, "�����У����Ժ�", Toast.LENGTH_LONG).show();
				mMapView.getCurrentMap();
			}       	
        });       
	}
	
	//TODO д����㺯��
	private void setCheckPoi(){			
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
	    				
	    				if (isStart){
	    					handler.removeCallbacks(endContext);
	    					save.setVisibility(View.VISIBLE);
	    					mMapView.getOverlays().clear();
	    					pathOverlay = new GraphicsOverlay(mMapView);
	    					pathOverlay.setData(myPath());
	    					mMapView.getOverlays().add(pathOverlay);
	    					mMapView.refresh();
	    					Toast.makeText(SingleContext.this, "�ѽ������������ı��λ�켣", Toast.LENGTH_LONG).show();
	    					
	    					//д��ʱ��
		        			new TimeEnd().execute(userId,actId,arrivedPoi,"singleend");
	    				}
						rotate.setText("");
						rotate.setHint("·��");
						rotate.setClickable(false);
						end.setText("�����뿪");
						end.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// ��תҳ��
								Intent intent = null;
								if (isStart)
									intent = new Intent(SingleContext.this,RecordOfSingle.class);
								else
									intent = new Intent(SingleContext.this,SearchAct.class);
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
	        	if (isStart)
					intent = new Intent(SingleContext.this,RecordOfSingle.class);
				else
					intent = new Intent(SingleContext.this,SearchAct.class);
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
				Toast.makeText(SingleContext.this, "�Ƽ�·������ʱ��Ϊ��" + rotateTime/(1000*60) +"���ӣ�", Toast.LENGTH_SHORT).show();
				extra += 5;
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
				Toast.makeText(SingleContext.this, "δ����������·����", Toast.LENGTH_SHORT).show();
		}		
	}
	
	public Graphic myPath(){
		// ---��õ�---
		///
		/*
		GeoPoint gpoint1 = new GeoPoint((int) (37.5299 * 1E6), (int)(122.0606 * 1E6));
		GeoPoint gpoint2 = new GeoPoint((int) (37.5302 * 1E6), (int)(122.0654 * 1E6));
		GeoPoint gpoint3 = new GeoPoint((int) (37.5273 * 1E6), (int)(122.0683 * 1E6));
			
		////
		//�Ȼ��length(le)���ڴ��쾲̬����
		locPoi.add(gpoint1);
		locPoi.add(gpoint2);
		locPoi.add(gpoint3);*/
		
		int len = locPoi.size();
		
		GeoPoint[] geolist = new GeoPoint[len];
		
		for (int i=0;i<len;i++){
			geolist[i] = locPoi.get(i);
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
			Toast.makeText(SingleContext.this, "�޵�ɼ�¼��", Toast.LENGTH_SHORT).show();
		else{
			mMapController.animateTo(locPoi.get(0));
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
	class checkPoiOverlay extends ItemizedOverlay<OverlayItem>{
		private ArrayList<String> question = new ArrayList<String>();
		private ArrayList<String> answer = new ArrayList<String>();
		
		public checkPoiOverlay(Drawable arg0, MapView arg1) {
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
				if (distance > 200.0)
					Toast.makeText(SingleContext.this, "����ɻش�Χ����"+(double)Math.round((distance-200.0)*10000)/10000+"�ף�", Toast.LENGTH_SHORT).show();
				else{		
					final EditText ansEdit = new EditText(SingleContext.this);
					new AlertDialog.Builder(SingleContext.this)
					.setTitle("������ü���Ĵ𰸣�")
					.setMessage("���⣺"+question.get(index))
					.setView(ansEdit)
					.setPositiveButton("�ύ", new DialogInterface.OnClickListener(){
						@SuppressWarnings("unchecked")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (ansEdit.getText().toString().equals(answer.get(index))){	
								OverlayItem item = checkPoiOverlay.this.getItem(index);
								Drawable marker = getResources().getDrawable(R.drawable.finishedpoi);
								marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());		
								item.setMarker(marker);			
								checkPoiOverlay.this.updateItem(item);							
								checkArrive.set(index, true);
								Toast.makeText(SingleContext.this, "�ش���ȷ��", Toast.LENGTH_SHORT).show();	
								mMapView.refresh();
								arrivedPoi++;
								if (arrivedPoi == checkArrive.size()){									
									//д��ʱ��
				        			new TimeEnd().execute(userId,actId,arrivedPoi,"singleend");
				        			locationClient.stop();
				        			
									handler.removeCallbacks(endContext);
									isOver = true;
									save.setVisibility(View.VISIBLE);
					        		mMapView.getOverlays().clear();
									pathOverlay = new GraphicsOverlay(mMapView);
									pathOverlay.setData(myPath());
									mMapView.getOverlays().add(pathOverlay);
									mMapView.refresh();
									Toast.makeText(SingleContext.this, "�ѽ������������ı��λ�켣", Toast.LENGTH_LONG).show();
									
									rotate.setText("");
									rotate.setHint("·��");
									rotate.setClickable(false);
									end.setText("�����뿪");
									end.setOnClickListener(new OnClickListener(){
										@Override
										public void onClick(View v) {
											// ��תҳ��
											Intent intent = new Intent(SingleContext.this,RecordOfSingle.class);
											startActivity(intent);
											mMapView.destroy();
											finish();
										}							
									});
								}		
							}
							else
								Toast.makeText(SingleContext.this, "�ش����", Toast.LENGTH_SHORT).show();
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
			if (distance > 300.0)
				Toast.makeText(SingleContext.this, "�����������"+(double)Math.round((distance-300.0)*10000)/10000+"�ף�", Toast.LENGTH_SHORT).show();
			else{
				isStart = true;
				setCheckPoi();
				rotate.setVisibility(View.VISIBLE);
				Toast.makeText(SingleContext.this, "������ʼ��ʱ��"+limitTime/(1000*60)+"����", Toast.LENGTH_SHORT).show();
				handler.postDelayed(endContext, limitTime);
				//��¼ʱ��
				new StartGame().execute(userId,actId,"singlestart");
			}
			return true;
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
        mMapView.destroy();
        mLocationOverlay.disableCompass(); // �ر�ָ����
        manager.unregisterListener(listener);
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

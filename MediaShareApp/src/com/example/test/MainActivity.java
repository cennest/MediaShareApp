package com.example.test;


import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;



import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;




 

public class MainActivity extends Activity {
	
	
	Button btnSetupServer;
	Button btnSetupClient;
	Button btnSendMessage;
	Button btnConnect;
	Button btnSetup;
	EditText messageTextView;
	TextView logView;
	String SSID = "\"" + "BOX" + "\"";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btnSendMessage = (Button)findViewById(R.id.btnSendMessage);
        messageTextView = (EditText)findViewById(R.id.messageTextViewId);
        btnConnect = (Button)findViewById(R.id.btnConnect);
        btnSetup = (Button)findViewById(R.id.btnSetup);
        logView = (TextView)findViewById(R.id.textlogId);
        btnSetupServer = (Button)findViewById(R.id.btnSetupServer);
        btnSetupClient = (Button)findViewById(R.id.btnSetupClient);
        
        
        btnConnect.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				connect();
			}
		});
        btnSetup.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				try {
					setup();
				} catch (InvocationTargetException e) {
					
				}
			}
		});
        
        btnSendMessage.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub					
				play();
				String message = messageTextView.getText().toString();
				logView.setText(message);
			}
		});
        
        btnSetupServer.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub					
				new Thread(new Runnable() {
				    public void run() {
				    	//startServer();
				    	startMediaServer();
				    }
				  }).start();
							}
		});
        
        btnSetupClient.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub					
				new Thread(new Runnable() {
				    public void run() {
				    	startClient();
				    }
				  }).start();
			}
		});
        
        
    }

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static String ipaddress;
    private static ArrayList<Socket> clientSocketCollection;
    private static MediaPlayer mediaPlayer;
    private void startServer()
    {
    	try{
    		InetAddress serverAddr = InetAddress.getByName("192.168.43.1");
    		serverSocket = new ServerSocket(1234,0,serverAddr);
    		java.lang.System.out.println("Server started");
    		//logView.setText("Server started on "+ getIPAddress());
    		clientSocket = serverSocket.accept();
    		PrintWriter writer = null;
    		int counter = 0;
    		while(true)
        	{
    			writer = new PrintWriter(clientSocket.getOutputStream());  
    			final String updateMsg = "Message "+counter;
    			writer.write(updateMsg);
    			java.lang.System.out.println("Sending...");
    			counter++;
    			writer.flush();
    			
    			runOnUiThread(new Runnable() {
    		        @Override
    		        public void run() {
    		        	logView.setText(updateMsg);   	    			
    		        }
    		      });
    			Thread.sleep(500);
        	}
    		
    	}catch(Exception ex){
    		
    	}
    }
    
    private void startMediaServer()
    {
    	try{
    		InetAddress serverAddr = InetAddress.getByName("192.168.43.1");
    		serverSocket = new ServerSocket(1234,0,serverAddr);
    		java.lang.System.out.println("Server started");
    		clientSocketCollection = new ArrayList<Socket>();    		
    		PrintWriter writer = null;
    		int counter = 0;
    		while(counter<2)
        	{
    			Socket clientSocket = serverSocket.accept();
    			clientSocketCollection.add(clientSocket);  
    			writer = new PrintWriter(clientSocket.getOutputStream());  
    			writer.write("Setup");
    			writer.flush();
    			counter++;
        	}
    			
    	}catch(Exception ex){
    		
    	}
    }
    
    private void play()
    {
    	try {
    	if(clientSocketCollection.size()>0){
    		
    			PrintWriter writer = null;
        		for(int i=0; i< clientSocketCollection.size();i++)
        		{
        			Socket clientSocket = clientSocketCollection.get(i);
        			writer = new PrintWriter(clientSocket.getOutputStream());  
        			writer.write("Play");
        			writer.flush();
        		}
			    		
    	}    
    	} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    private void stop()
    {
    	if(clientSocketCollection.size()>0){
    		try {
    			PrintWriter writer = null;
        		for(int i=0; i< clientSocketCollection.size();i++)
        		{
        			Socket clientSocket = clientSocketCollection.get(i);
        			writer = new PrintWriter(clientSocket.getOutputStream());  
        			writer.write("Stop");
        		}
			} catch (Exception e) {
				// TODO: handle exception
			}    		
    	}    	
    }
    
    private void setupLocalSong()
    {	
    	 mediaPlayer = MediaPlayer.create(this, R.raw.vop1); 
    }
    
    private void playLocalSong()
    {	
    	mediaPlayer.start();
    }
    
    private void startClient()
    {
    	
    	try{
    		PrintWriter writer = null;
    		clientSocket = new Socket("192.168.43.1", 1234);
    		java.lang.System.out.println("Client started");
    		//logView.setText("Writting hello");
    		//writer = new PrintWriter(clientSocket.getOutputStream());    		
    		//writer.write("hello");
    		while(true)
        	{
    			Scanner scanner = new Scanner(clientSocket.getInputStream());            	
        		String msg = scanner.nextLine();
    			java.lang.System.out.println("Client Message : "+msg);
    			final String updateMsg = msg;
    			if(msg.endsWith("Setup"))
    			{
    				setupLocalSong();
    			} else if(msg.endsWith("Play"))
    			{
    				playLocalSong();
    			}
    			runOnUiThread(new Runnable() {
    		        @Override
    		        public void run() {
    		        	logView.setText(updateMsg);    	    			
    		        }
    		      });
        	}
    		//writer.flush();    		
    		
    	}catch(Exception ex){
    		
    		logView.setText("startClient exception");
    	}    	
    }
    
    
    
    private boolean recieveConnection() {
		
		Log.d("Pradeep", "Testing");
		return true;
	}
    
    public String getIpAddr() {
    	 WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE); 
    	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    	   int ip = wifiInfo.getIpAddress();
    	   java.lang.System.out.println("SSID : "+wifiInfo.getSSID());  
    	   String ipString = String.format(
    	   "%d.%d.%d.%d",
    	   (ip & 0xff),
    	   (ip >> 8 & 0xff),
    	   (ip >> 16 & 0xff),
    	   (ip >> 24 & 0xff));

    	   return ipString;
    	}
    
private boolean connect() {
	Context context = this;
	WifiConfiguration conf = new WifiConfiguration();
    conf.SSID = SSID;
    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
    wifiManager.addNetwork(conf);
    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
    for( WifiConfiguration i : list ) {
        if(i.SSID != null && i.SSID.equals(SSID)) {
            try {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                //System.out.print("i.networkId " + i.networkId + "\n");
                boolean flag = wifiManager.reconnect();   
                if(flag){
                	logView.setText("Connected to "+getIpAddr());
                }
                
                else
                	logView.setText("failed");
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }           
    }

		return true;
	}
    
public String getIPAddress() 
{
  String ipaddress = "";
  
    try 
    {
      Enumeration<NetworkInterface> enumnet = NetworkInterface.getNetworkInterfaces();
      NetworkInterface netinterface = null;
      
        while(enumnet.hasMoreElements()) 
        {
            netinterface = enumnet.nextElement();
            
            for (Enumeration<InetAddress> enumip = netinterface.getInetAddresses(); 
               enumip.hasMoreElements();) 
            {
                InetAddress inetAddress = enumip.nextElement();
                
                if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                {
                  ipaddress = inetAddress.getHostAddress();
                  java.lang.System.out.println("Address : "+ipaddress); 
                  break;
                }
            }
        }
    } 
    catch (SocketException e) 
    {
        e.printStackTrace();
    }

    return ipaddress;
}

    private boolean setup() throws InvocationTargetException {
    	Context context = this;
    	 WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	    if(wifiManager.isWifiEnabled())
    	    {
    	        wifiManager.setWifiEnabled(false);          
    	    }       
    	    Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();   //Get all declared methods in WifiManager class     
    	    boolean methodFound=false;
    	    for(Method method: wmMethods){
    	        if(method.getName().equals("setWifiApEnabled")){
    	            methodFound=true;
    	            WifiConfiguration netConfig = new WifiConfiguration();
    	            netConfig.SSID = SSID;
    	            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

    	            try {
    	                boolean apstatus=(Boolean) method.invoke(wifiManager, netConfig,true);          
    	                for (Method isWifiApEnabledmethod: wmMethods)
    	                {
    	                    if(isWifiApEnabledmethod.getName().equals("isWifiApEnabled")){
    	                        while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){
    	                        };
    	                        for(Method method1: wmMethods){
    	                            if(method1.getName().equals("getWifiApState")){
    	                                int apstate;
    	                                apstate=(Integer)method1.invoke(wifiManager);
    	                            }
    	                        }
    	                    }
    	                }
    	                if(apstatus)
    	                {
    	                    //System.out.println("SUCCESSdddd"); 
    	                	logView.setText("Setup completed "+getIPAddress()); 
    	              	  
    	                }else
    	                {
    	                   // System.out.println("FAILED");  
    	                	logView.setText("Setup failed");  

    	                }

    	            } catch (IllegalArgumentException e) {
    	                e.printStackTrace();
    	            } catch (IllegalAccessException e) {
    	                e.printStackTrace();
    	            } 
    	        }      
    	    }   
    	  	
		return true;
	}
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}

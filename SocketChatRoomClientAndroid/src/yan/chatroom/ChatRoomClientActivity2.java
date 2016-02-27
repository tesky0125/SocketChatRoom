package yan.chatroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChatRoomClientActivity2 extends Activity {

	private EditText textChatLog;
	private EditText textMsg;
	private Button btnLogin;
	private Button btnSend;
	
	private static final String SERVER_IP = "202.114.171.38";
	private static final int SERVER_PORT = 54321;
	private Thread socketThread;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String strMsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		initViewsListeners();
		socketThread = new Thread(socketRunnable);
		
	}

	private void initViewsListeners() {
		// TODO Auto-generated method stub
		textChatLog = (EditText) this.findViewById(R.id.EditText01);
		textMsg = (EditText) this.findViewById(R.id.EditText02);
		btnLogin = (Button) this.findViewById(R.id.Button_In);
		btnSend = (Button) this.findViewById(R.id.Button_Send);
		
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					socket = new Socket(SERVER_IP,SERVER_PORT);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
//					out = new PrintWriter(socket.getOutputStream(),true);
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")),true);
					
					socketThread.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				strMsg = textMsg.getText().toString();
				out.println(strMsg);
				out.flush();
			}
		});
	}
	
	private Runnable socketRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try {
					if((strMsg = in.readLine())!=null){
						strMsg+="\n";
						socketHandler.sendMessage(socketHandler.obtainMessage());//这里操作有点模糊
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private Handler socketHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			textChatLog.append(strMsg);
		}
		
	};

}

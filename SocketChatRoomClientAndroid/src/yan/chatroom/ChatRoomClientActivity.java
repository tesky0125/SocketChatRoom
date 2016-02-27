package yan.chatroom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChatRoomClientActivity extends Activity {
	// 客户端， 客户端需要与服务器始终保持通信状态 注意：因Android是线程安全的，所以不能直接在线程中更新视图，需使用Handler来更新视图
	// 当点击”登陆“按钮时，连接服务器，并取得需要操作的流，点击"发送"按钮时取出输入框中的内容发送向服务器，由服务器发送给每个客户端
	private final String DEBUG_TAG = "ChatRoomClientActivity";
	private static final String SERVERIP = "202.114.171.38";// 服务器IP、端口
	private static final int SERVERPORT = 54321;
	private Thread mThread = null;
	private Socket mSocket = null;
	private Button mButton_In = null;
	private Button mButton_Send = null;
	private EditText mEditText01 = null;
	private EditText mEditText02 = null;
	private BufferedReader mBufferedReader = null;
	private PrintWriter mPrintWriter = null;
	private String mStrMSG = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mButton_In = (Button) findViewById(R.id.Button_In);
		mButton_Send = (Button) findViewById(R.id.Button_Send);
		mEditText01 = (EditText) findViewById(R.id.EditText01);
		mEditText02 = (EditText) findViewById(R.id.EditText02);

		mButton_In.setOnClickListener(new OnClickListener() {// 登陆按钮
					public void onClick(View v) {
						try {
							mSocket = new Socket(SERVERIP, SERVERPORT); // 连接服务器
							mBufferedReader = new BufferedReader(
									new InputStreamReader(mSocket
											.getInputStream()));// 取得输入、输出流
							mPrintWriter = new PrintWriter(mSocket
									.getOutputStream(), true);
						} catch (Exception e) {
							Log.e(DEBUG_TAG, e.toString());
						}
					}
				});

		mButton_Send.setOnClickListener(new OnClickListener() {// 发送消息按钮
					public void onClick(View v) {
						try {
							String str = mEditText02.getText().toString()
									+ "\n";// 取得编辑框中我们输入的内容
							mPrintWriter.print(str);// 发送给服务器
							mPrintWriter.flush();
						} catch (Exception e) {
							Log.e(DEBUG_TAG, e.toString());
						}
					}
				});

		mThread = new Thread(mRunnable);
		mThread.start();
	}

	private Runnable mRunnable = new Runnable() { // 线程:监听服务器发来的消息
		public void run() {
			while (true) {
				try {
					if ((mStrMSG = mBufferedReader.readLine()) != null) {
						mStrMSG += "\n";// 消息换行
						mHandler.sendMessage(mHandler.obtainMessage());// 发送消息
					}
				} catch (Exception e) {
					Log.e(DEBUG_TAG, e.toString());
				}
			}
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);// 接受消息 刷新
			try {
				mEditText01.append(mStrMSG); // 将聊天记录添加进来
			} catch (Exception e) {
				Log.e(DEBUG_TAG, e.toString());
			}
		}
	};
}

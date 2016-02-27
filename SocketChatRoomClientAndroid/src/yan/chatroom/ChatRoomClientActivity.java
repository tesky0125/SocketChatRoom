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
	// �ͻ��ˣ� �ͻ�����Ҫ�������ʼ�ձ���ͨ��״̬ ע�⣺��Android���̰߳�ȫ�ģ����Բ���ֱ�����߳��и�����ͼ����ʹ��Handler��������ͼ
	// ���������½����ťʱ�����ӷ���������ȡ����Ҫ�������������"����"��ťʱȡ��������е����ݷ�������������ɷ��������͸�ÿ���ͻ���
	private final String DEBUG_TAG = "ChatRoomClientActivity";
	private static final String SERVERIP = "202.114.171.38";// ������IP���˿�
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

		mButton_In.setOnClickListener(new OnClickListener() {// ��½��ť
					public void onClick(View v) {
						try {
							mSocket = new Socket(SERVERIP, SERVERPORT); // ���ӷ�����
							mBufferedReader = new BufferedReader(
									new InputStreamReader(mSocket
											.getInputStream()));// ȡ�����롢�����
							mPrintWriter = new PrintWriter(mSocket
									.getOutputStream(), true);
						} catch (Exception e) {
							Log.e(DEBUG_TAG, e.toString());
						}
					}
				});

		mButton_Send.setOnClickListener(new OnClickListener() {// ������Ϣ��ť
					public void onClick(View v) {
						try {
							String str = mEditText02.getText().toString()
									+ "\n";// ȡ�ñ༭�����������������
							mPrintWriter.print(str);// ���͸�������
							mPrintWriter.flush();
						} catch (Exception e) {
							Log.e(DEBUG_TAG, e.toString());
						}
					}
				});

		mThread = new Thread(mRunnable);
		mThread.start();
	}

	private Runnable mRunnable = new Runnable() { // �߳�:������������������Ϣ
		public void run() {
			while (true) {
				try {
					if ((mStrMSG = mBufferedReader.readLine()) != null) {
						mStrMSG += "\n";// ��Ϣ����
						mHandler.sendMessage(mHandler.obtainMessage());// ������Ϣ
					}
				} catch (Exception e) {
					Log.e(DEBUG_TAG, e.toString());
				}
			}
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);// ������Ϣ ˢ��
			try {
				mEditText01.append(mStrMSG); // �������¼��ӽ���
			} catch (Exception e) {
				Log.e(DEBUG_TAG, e.toString());
			}
		}
	};
}

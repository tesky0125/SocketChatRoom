package yan.chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRoomServer {// �������� ��Ҫ�������벢��������ģʽ����������
	private static final int SERVERPORT = 54321; // �������˿�
	private ExecutorService mExecutorServicePool; // �̳߳� ��ҪΪÿ���ͻ��˶�����һ���߳�
	private ServerSocket mServerSocket; // ServerSocket����
	private static List<Socket> mClientList = new ArrayList<Socket>(); // �ͻ�������
																		// ͨ��List�������������ӽ����Ŀͻ��˵�Socket����(Ҳ����CopyOnWriteArrayList������)

	public ChatRoomServer() {
		try {
			mServerSocket = new ServerSocket(SERVERPORT);// ���÷������˿�
			mExecutorServicePool = Executors.newCachedThreadPool();// ����һ���̳߳�
			System.out.println("start...");
			Socket client = null;// ������ʱ����ͻ������ӵ�Socket����
			while (true) {
				client = mServerSocket.accept(); // ���տͻ����Ӳ���ӵ�list��
				mClientList.add(client);
				mExecutorServicePool.execute(new ServerThread4Client(client));// ����һ���ͻ����߳�
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	static class ServerThread4Client implements Runnable {// ÿ���ͻ��˵�������һ���߳�
		private Socket mSocket;
		private BufferedReader mBufferedReader;
		private PrintWriter mPrintWriter;
		private String mStrMSG;

		public ServerThread4Client(Socket socket) throws IOException {
			this.mSocket = socket;
			mBufferedReader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			mStrMSG = "user:" + this.mSocket.getInetAddress() + " come total:"
					+ mClientList.size();
			sendMessage();
		}

		public void run() {
			try {
				while ((mStrMSG = mBufferedReader.readLine()) != null) {
					if (mStrMSG.trim().equals("exit")) {
						// ��һ���ͻ����˳�ʱ
						mClientList.remove(mSocket);
						mBufferedReader.close();
						mPrintWriter.close();
						mStrMSG = "user:" + this.mSocket.getInetAddress()
								+ " exit total:" + mClientList.size();
						mSocket.close();
						sendMessage();
						break;
					} else {
						mStrMSG = mSocket.getInetAddress() + ":" + mStrMSG;
						sendMessage();
					}
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}

		private void sendMessage() throws IOException {// ������Ϣ�����пͻ���
			System.out.println(mStrMSG);
			for (Socket client : mClientList) {
				mPrintWriter = new PrintWriter(client.getOutputStream(), true);
				mPrintWriter.println(mStrMSG);
			}
		}
	}

	public static void main(String[] args) { // main���� ����������
		new ChatRoomServer();
	}

}

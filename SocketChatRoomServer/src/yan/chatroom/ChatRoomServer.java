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

public class ChatRoomServer {// 服务器端 需要单独编译并在命令行模式下启动测试
	private static final int SERVERPORT = 54321; // 服务器端口
	private ExecutorService mExecutorServicePool; // 线程池 需要为每个客户端都开启一个线程
	private ServerSocket mServerSocket; // ServerSocket对象
	private static List<Socket> mClientList = new ArrayList<Socket>(); // 客户端连接
																		// 通过List来储存所有连接进来的客户端的Socket对象(也可用CopyOnWriteArrayList来储存)

	public ChatRoomServer() {
		try {
			mServerSocket = new ServerSocket(SERVERPORT);// 设置服务器端口
			mExecutorServicePool = Executors.newCachedThreadPool();// 创建一个线程池
			System.out.println("start...");
			Socket client = null;// 用来临时保存客户端连接的Socket对象
			while (true) {
				client = mServerSocket.accept(); // 接收客户连接并添加到list中
				mClientList.add(client);
				mExecutorServicePool.execute(new ServerThread4Client(client));// 开启一个客户端线程
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	static class ServerThread4Client implements Runnable {// 每个客户端单独开启一个线程
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
						// 当一个客户端退出时
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

		private void sendMessage() throws IOException {// 发送消息给所有客户端
			System.out.println(mStrMSG);
			for (Socket client : mClientList) {
				mPrintWriter = new PrintWriter(client.getOutputStream(), true);
				mPrintWriter.println(mStrMSG);
			}
		}
	}

	public static void main(String[] args) { // main方法 开启服务器
		new ChatRoomServer();
	}

}

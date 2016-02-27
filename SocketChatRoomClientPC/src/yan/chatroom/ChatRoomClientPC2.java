package yan.chatroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRoomClientPC2 {
	
	private static final String SERVER_IP = "202.114.171.38";
	private static final int SERVER_PORT = 54321;
	private Socket socket;
	private ExecutorService threadExec = null;
	private BufferedReader server_in;
	private String strServerMsg;
	
	public ChatRoomClientPC2(){
		try {
			socket = new Socket(SERVER_IP, SERVER_PORT);
			threadExec = Executors.newCachedThreadPool();
			threadExec.execute(new ClientThreadPC(socket));
			
			System.out.println("running from server.");
			try {
				server_in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				
				while((strServerMsg =  server_in.readLine())!=null){
					System.out.println(strServerMsg);
				}
			} catch (Exception e) {
				System.out.println("2:"+e);
			}
			
		} catch (Exception e) {
			System.out.println("1:"+e);
		}
	}
	
	private class ClientThreadPC implements Runnable{

		private Socket socket;
		private BufferedReader device_in;
		private PrintWriter out;
		private String strDeviceMsg;
		
		public ClientThreadPC(Socket socket){
			this.socket = socket;
		}
		
		@Override
		public void run() {
//			System.out.println("running from server.");
//			//接收服务器消息在此会阻塞接收设备的消息，移植到主线程或者另开一个新线程
//			try {
//				server_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				
//				while((strMsg =  server_in.readLine())!=null){
//					System.out.println(strMsg);
//				}
//			} catch (Exception e) {
//				System.out.println("2:"+e);
//			}
			System.out.println("running from device.");
			//
			try {
				device_in = new BufferedReader(new InputStreamReader(System.in/*,"UTF-8"*/));
//				out = new PrintWriter(socket.getOutputStream(),true);
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
//				while((strDeviceMsg = device_in.readLine())!=null){
				while(true){
					strDeviceMsg = device_in.readLine();
					System.out.println(strDeviceMsg);//
					out.println(strDeviceMsg);
					if(strDeviceMsg.trim().equals("exit")){
						server_in.close();
						device_in.close();
						out.close();
						threadExec.shutdownNow();
						break;
					}
				}
			} catch (Exception e) {
				System.out.println("3:"+e);
			}
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ChatRoomClientPC2();
	}

}

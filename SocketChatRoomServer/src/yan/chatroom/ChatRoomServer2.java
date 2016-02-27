package yan.chatroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRoomServer2 {
	
	private static final int SERVER_PORT = 54321;
	private ServerSocket server = null;
	private ExecutorService execServicePool = null;
	private List<Socket> clientList = new ArrayList<Socket>();
	
	public ChatRoomServer2()
	{
		try{
			server = new ServerSocket(SERVER_PORT);
			execServicePool = Executors.newCachedThreadPool();
			System.out.println("Server start..");
			while (true) {//
				Socket client = server.accept();
				clientList.add(client);
				execServicePool.execute(new ServerThread4Client(client));
			}
		}catch(Exception e)
		{
			System.out.println("1:"+e);
		}
	}
	
	private class ServerThread4Client implements Runnable{

		private Socket client;
		private BufferedReader in;
		private PrintWriter out;
		private String strMsg;
		
		public ServerThread4Client(Socket client)
		{
			this.client = client;
			try {
				in = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
			} catch (IOException e) {
				System.out.println("2:"+e);
			}
			strMsg = "User:"+client.getInetAddress()+" come. Total clients:"+clientList.size();
			sendMessage();
		}


		@Override
		public void run() {
			try{
				while((strMsg = in.readLine()) != null){
					if(strMsg.trim().equals("exit")){
						clientList.remove(client);
						strMsg = "User:"+client.getInetAddress()+" exit. Total clients:"+clientList.size();
						sendMessage();
						out.close();
						in.close();
						client.close();
						break;
					}else{
						strMsg = client.getInetAddress()+":"+strMsg;
						sendMessage();
					}
				}
			}catch(Exception e){
				System.out.println("4:"+e);
			}
		}
		
		private void sendMessage() {
			System.out.println(strMsg);
			for(Socket client : clientList){//向每一个客户端发送消息
				try {
					//out = new PrintWriter(client.getOutputStream(),true);//自动刷新
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8")),true);
				} catch (IOException e) {
					System.out.println("3:"+e);
				}
				out.println(strMsg);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatRoomServer2();
	}

}

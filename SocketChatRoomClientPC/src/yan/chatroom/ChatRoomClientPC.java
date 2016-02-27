package yan.chatroom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRoomClientPC {// ��Ҫ�������벢��������ģʽ����������
	private static final int PORT = 54321;
	private static ExecutorService exec = Executors.newCachedThreadPool();

	public ChatRoomClientPC() {
		try {
			Socket socket = new Socket("202.114.171.38", PORT);
			exec.execute(new ClientPCThread(socket));
			BufferedReader br = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			String msg;
			while ((msg = br.readLine()) != null) {
				System.out.println(msg);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	static class ClientPCThread implements Runnable {// �ͻ����̻߳�ȡ����̨������Ϣ
		private Socket socket;

		public ClientPCThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				String msg;
				while (true) {
					msg = br.readLine();
					pw.println(msg);
					if (msg.trim().equals("exit")) {
						pw.close();
						br.close();
						exec.shutdownNow();
						break;
					}
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new ChatRoomClientPC();
	}

}

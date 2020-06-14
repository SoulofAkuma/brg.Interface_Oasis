package group;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cc.Pair;

public class TimeoutController implements Runnable {

	private HashMap<String, Pair<Socket, Pair<Integer, Integer>>> timeoutSockets = new HashMap<String, Pair<Socket, Pair<Integer, Integer>>>(); //Sockets to be closed after a certain amount of time stored with a pair of <time passed so far, time to pass overall>. Time in DECISECONDS
	private boolean isActive = false; //Indicates whether this class should proceed executing
	
	public String addSocket(Socket socket, int seconds) {
		String tcID = "";
		Random random = new Random();
		do {
			for (int i = 0; i < 9; i++) {
				tcID += random.nextInt(10);
			}
		} while(timeoutSockets.containsKey(tcID));
		timeoutSockets.put(tcID, new Pair<Socket, Pair<Integer, Integer>>(socket, new Pair<Integer, Integer>(0, seconds)));
		return tcID;
	}
	
	@Override
	public void run() {
		this.isActive = true;
		try {
			while (this.isActive) {
				ArrayList<String> finished = new ArrayList<String>();
				for (Map.Entry<String, Pair<Socket, Pair<Integer, Integer>>> socket : this.timeoutSockets.entrySet()) {
					if (socket.getValue().getValue().getKey() == socket.getValue().getValue().getValue()) {
						finished.add(socket.getKey());
					}
				}
				for (int i = 0; i < finished.size(); i++) {
					try {
						this.timeoutSockets.get(finished.get(i)).getKey().shutdownInput();
					} catch (Exception e) {}
					try {
						this.timeoutSockets.get(finished.get(i)).getKey().shutdownOutput();
					} catch (Exception e) {}
					try {
						this.timeoutSockets.get(finished.get(i)).getKey().close();
					} catch (Exception e) {}
					this.timeoutSockets.remove(finished.get(i));
				}
				Thread.sleep(100);
				HashMap<String, Pair<Socket, Pair<Integer, Integer>>> tempMap = new HashMap<String, Pair<Socket, Pair<Integer, Integer>>>();
				for (Map.Entry<String, Pair<Socket, Pair<Integer, Integer>>> socket : this.timeoutSockets.entrySet()) {
					tempMap.put(socket.getKey(), new Pair<Socket, Pair<Integer, Integer>>(this.timeoutSockets.get(socket.getKey()).getKey(), new Pair<Integer, Integer>(this.timeoutSockets.get(socket.getKey()).getValue().getKey() + 1, this.timeoutSockets.get(socket.getKey()).getValue().getValue()))); //Increment left integer of pair
				}
			}
		} catch (InterruptedException e) {}
	}
	
	public void stop() {
		for (Map.Entry<String, Pair<Socket, Pair<Integer, Integer>>> socket : this.timeoutSockets.entrySet()) {
			try {
				socket.getValue().getKey().shutdownInput();
			} catch (Exception e) {}
			try {
				socket.getValue().getKey().shutdownOutput();
			} catch (Exception e) {}
			try {
				socket.getValue().getKey().close();
			} catch (Exception e) {}
		}
		this.isActive = false;
	}
	
	public void removeCooldown(String tcID) {
		this.timeoutSockets.put(tcID, new Pair<Socket, Pair<Integer, Integer>>(this.timeoutSockets.get(tcID).getKey(), new Pair<Integer, Integer>(this.timeoutSockets.get(tcID).getValue().getValue(), this.timeoutSockets.get(tcID).getValue().getValue())));
	}
}

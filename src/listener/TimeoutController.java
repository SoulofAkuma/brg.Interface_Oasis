package listener;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import cc.Pair;

public class TimeoutController implements Runnable {

	private ArrayList<Pair<Socket, Pair<Integer, Integer>>> timeoutSockets = new ArrayList<Pair<Socket, Pair<Integer, Integer>>>(); //Sockets to be closed after a certain amount of time stored with a pair of <time passed so far, time to pass overall>
	private boolean isActive = false; //Indicates whether this class should proceed executing
	
	public void addSocket(Socket socket, int seconds) {
		timeoutSockets.add(new Pair<Socket, Pair<Integer, Integer>>(socket, new Pair<Integer, Integer>(0, seconds)));
	}
	
	@Override
	public void run() {
		this.isActive = true;
		try {
			while (this.isActive) {
				ArrayList<Integer> finished = new ArrayList<Integer>();
				for (int i = 0; i < this.timeoutSockets.size(); i++) {
					if (this.timeoutSockets.get(i).getValue().getKey() == this.timeoutSockets.get(i).getValue().getValue()) {
						finished.add(i);
					}
				}
				for (int i = 0; i < finished.size(); i++) {
					try {
						this.timeoutSockets.get(finished.get(i)).getKey().shutdownInput();
						this.timeoutSockets.get(finished.get(i)).getKey().shutdownOutput();
						this.timeoutSockets.get(finished.get(i)).getKey().close();
					} catch (IOException e) {}
					this.timeoutSockets.remove((int) finished.get(i));
				}
				Thread.sleep(1000);
				for (int i = 0; i < this.timeoutSockets.size(); i++) {
					this.timeoutSockets.set(i, new Pair<Socket, Pair<Integer, Integer>>(this.timeoutSockets.get(i).getKey(), new Pair<Integer, Integer>(this.timeoutSockets.get(i).getValue().getKey() + 1, this.timeoutSockets.get(i).getValue().getValue()))); //Increment left integer of pair
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

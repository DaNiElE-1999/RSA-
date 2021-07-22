import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// This represents a communication channel
// Someone sends a message directed towards a party, and others part of the channel listen
public class CommunicationChannel {
	public static interface IChannelListener {
		// Notify a party for sent message
		void notifyMessage(ChannelMessage m);
		RSA.PublicKey getPublicKey();
	}

	public static class ChannelMessage {
		public IChannelListener sender;
		public IChannelListener receiver;
		public MyBigInteger[] encrypted;
		
		public ChannelMessage(IChannelListener sender, IChannelListener receiver, MyBigInteger[] encrypted) {
			this.sender = sender;
			this.receiver = receiver;
			this.encrypted = encrypted;
		}
	}
	
	public HashMap<String, IChannelListener> parties;
	
	public CommunicationChannel() {
		parties = new HashMap<String, IChannelListener>();
	}
	
	public void addParty(Party p) {
		parties.put(p.getName(), (IChannelListener)p);
	}
	
	// Broadcast this message to all parties
	public void broadcast(ChannelMessage m) {
		for (Map.Entry<String, IChannelListener> entry : parties.entrySet()) {
			entry.getValue().notifyMessage(m);
		}
	}
	
	public IChannelListener getParty(String name) {
		return parties.get(name);
	}
	
	public void say(Party p, String message) {
		System.out.println(p.getName() + " says: " + message);
	}
}

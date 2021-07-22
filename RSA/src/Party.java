import java.math.BigInteger;

/**
 * Represents a party in a communiaction, that possesses a private key
 * and broadcasts a public key.
 *
 */
public class Party implements CommunicationChannel.IChannelListener {
	private boolean badIntentions = false;
	private String name;
	// Keys:
	RSA.PublicKey pubKey;
	RSA.PrivateKey privKey;
	
	private CommunicationChannel channel;
	
	public Party(String name) {
		this.name = name;
		
		this.privKey = RSA.generatePrivateKey();
		this.pubKey = RSA.genereatePublicKey(privKey);
	}
	
	public Party(String name, boolean badIntentions) {
		this(name);
		this.badIntentions = badIntentions;
	}
	
	public void joinChannel(CommunicationChannel channel, boolean silent) {
		channel.addParty(this);
		
		if (!silent)
			channel.say(this, "Hello world, my public key is " + this.pubKey);
		
		this.channel = channel;
	}
	
	public void joinChannel(CommunicationChannel channel) {
		joinChannel(channel, false);
	}
	
	public void sendMessage(String partyName, String message) {
		CommunicationChannel.IChannelListener otherParty = channel.getParty(partyName);
		
		MyBigInteger[] encrypted = RSA.encrypt(message.getBytes(), otherParty.getPublicKey());
		
		// Create a message
		CommunicationChannel.ChannelMessage m = new CommunicationChannel.ChannelMessage(
				(CommunicationChannel.IChannelListener)this, 
				otherParty, 
				encrypted
			);
		
		// Broadcast it
		channel.broadcast(m);
	}
	
	// Send an encrypted message to 
	
	public String getName() {
		return name;
	}

	// A message was received:
	@Override
	public void notifyMessage(CommunicationChannel.ChannelMessage m) {
		// Ignore messages we sent
		if (m.sender == this)
			return;
		
		// Else only decrypt messages sent to us:
		else if (m.receiver == this) {
			String decryptedMessage = RSA.decrypt(m.encrypted, privKey);
			channel.say(this, "I received message: " + decryptedMessage);
		}
		
		// Else, if this party has some bad intentions, intercept messages
		else if (badIntentions) {
			channel.say(this, "I intercepted message: " + RSA.encryptedArrayToString(m.encrypted));
		}
	}

	// Get the public key
	@Override
	public RSA.PublicKey getPublicKey() {
		// TODO Auto-generated method stub
		return pubKey;
	}
}

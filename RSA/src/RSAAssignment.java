import java.util.Scanner;

public class RSAAssignment {
	public static void main(String[] args) {
		CommunicationChannel channel = new CommunicationChannel();
		
		Party alice = new Party("Alice");
		Party bob = new Party("Bob");
		//Charlie has bad intentions
		Party charlie = new Party("Charlie", true);
		
		// Charlie joins channel silently
		charlie.joinChannel(channel, true);
		
		// Alice and Bob join
		alice.joinChannel(channel);
		bob.joinChannel(channel);
		
		//Now we start the conversation between the two:
		Scanner input = new Scanner(System.in);
		String m;
		while (true) {
			System.out.print("Alice's message to Bob: ");
			m = input.nextLine();
			alice.sendMessage("Bob", m);
			
			System.out.print("Bob's message to Alice: ");
			m = input.nextLine();
			bob.sendMessage("Alice", m);
		}
	}
	
	
}

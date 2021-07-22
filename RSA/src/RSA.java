import java.math.BigInteger;
import java.util.Random;

public class RSA {
	// mostly implemented here

	public static int BITS = 64;

	public static MyBigInteger E = new MyBigInteger(new Long(65537L).toString());

	// pq included in both Private and Public key for sake of convenience

	public static class PrivateKey {
		public MyBigInteger p;
		public MyBigInteger q;
		public MyBigInteger lambda;
		public MyBigInteger d;

		public MyBigInteger pq;

		public PrivateKey(MyBigInteger p, MyBigInteger q) {
			this.p = p;
			this.q = q;
			this.pq = p.multiply(q);

			lambda = MyBigInteger.carmichael(p, q);
			MyBigInteger[] res = MyBigInteger.extendedEuclid(E, lambda);
			d = res[1];
			
			// It seems to not work for negative d, so we turn it to positive
			if (d.signum() < 0) {
				d = d.mod(lambda);
			}
		}
	}

	public static class PublicKey {
		private MyBigInteger pq;
		private MyBigInteger e;

		public PublicKey(PrivateKey privKey) {
			pq = privKey.pq;
			e = E;
		}

		public MyBigInteger getPQ() {
			return pq;
		}

		public MyBigInteger getE() {
			return e;
		}
		
		public String toString() {
			return "(pq: " + pq + ", e: " + e + ")";
		}
	}

	// Generate a pair of random primes, then a private key from those two.
	public static PrivateKey generatePrivateKey() {
		// Generate two primes, half the bitlength of the key
		MyBigInteger p, q;

		p = MyBigInteger.probablePrime(BITS / 2);
		q = MyBigInteger.probablePrime(BITS / 2);

		return new RSA.PrivateKey(p, q);
	}

	// Generate a public key from an already generate private key.
	public static PublicKey genereatePublicKey(PrivateKey privKey) {
		return new RSA.PublicKey(privKey);
	}

	// ENCRYPTION:
	// Given a public key, encrypt a single byte.
	public static MyBigInteger encrypt(byte m, PublicKey pubKey) {
		MyBigInteger mBig = new MyBigInteger(m);
		return MyBigInteger.modPow(mBig, pubKey.getE(), pubKey.getPQ());
	}

	// Encrypt some message using a public key:
	public static MyBigInteger[] encrypt(byte[] message, PublicKey pubKey) {
		MyBigInteger[] result = new MyBigInteger[message.length];

		for (int i = 0; i < message.length; i++) {

			result[i] = RSA.encrypt(message[i], pubKey);
		}

		return result;
	}

	// Encrypt a string:
	public static MyBigInteger[] encrypt(String message, PublicKey pubKey) {
		return RSA.encrypt(message.getBytes(), pubKey);
	}

	// DECRYPTION:
	// Decrypt a single encrypted byte:
	public static byte decrypt(MyBigInteger b, PrivateKey privKey) {
		MyBigInteger decrypted = MyBigInteger.modPow(b, privKey.d, privKey.pq);
		return decrypted.getLastByte();
	}

	// Decrypt the whole message
	public static String decrypt(MyBigInteger[] encrypted, PrivateKey privKey) {
		StringBuilder message = new StringBuilder();
		
		for (MyBigInteger b : encrypted)
			message.append((char)RSA.decrypt(b, privKey));
		
		return message.toString();
	}
	
	// Get a jumbled string of the encrypted message.
	public static String encryptedArrayToString(MyBigInteger[] message) {
		StringBuilder result = new StringBuilder("");
		for (MyBigInteger m : message) {
			
			byte[] mArray = m.toByteArray();
			
			for (byte b : mArray) {
				result.append(Integer.toHexString(b & 0xFF));
			}
		}
		
		//result.setCharAt(result.length() - 1, ']');
		
		return result.toString();
	}

}

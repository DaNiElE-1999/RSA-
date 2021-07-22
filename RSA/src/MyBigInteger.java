import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A wrapper class to BigInteger, where 
 * we do our own implementations of the required methods
 *
 */
public class MyBigInteger {
	private BigInteger _big;
	
	public MyBigInteger(BigInteger big) {
		_big = big;
	}
	
	public MyBigInteger(byte[] arg0) {
		_big = new BigInteger(arg0);
	}
	
	public MyBigInteger(byte b) {
		this(new byte[] { b } );
	}
	
	public MyBigInteger(int numBits, Random rnd) {
		_big = new BigInteger(numBits, rnd);
	}
	
	public MyBigInteger(String str) {
		_big = new BigInteger(str);
	}

	private static final long serialVersionUID = 2927756202726623107L;

	// We use the default multiply
	public MyBigInteger multiply(MyBigInteger another) {
		return new MyBigInteger(_big.multiply(another._big));
	}

	// We use the builtin mod
	public MyBigInteger mod(MyBigInteger another) {
		return new MyBigInteger(_big.mod(another._big));
	}
	
	// We use the builtin add
	public MyBigInteger add(MyBigInteger another) {
		return new MyBigInteger(_big.add(another._big));
	}
	
	public MyBigInteger subtractOne() {
		return new MyBigInteger(_big.subtract(BigInteger.ONE));
	}
	
	public int signum() {
		return _big.signum();
	}
	
	// An implementation of binary exponentiation
	public static MyBigInteger modPow(MyBigInteger base, MyBigInteger exp, MyBigInteger m) {
		if (exp._big.equals(BigInteger.ZERO))
			return new MyBigInteger(BigInteger.ONE);
		
		if (exp._big.equals(BigInteger.ONE))
			return base;
		
		BigInteger result = BigInteger.ONE;
		
		BigInteger baseBig = base._big;
		BigInteger expBig = exp._big;

		// This approach is really not that efficient, but it does the job well
		// For as long as there are bits left in expBig:
		while (expBig.compareTo(BigInteger.ZERO) > 0) {
			boolean isOdd = expBig.getLowestSetBit() == 0; //1 is the last bit
			
			// If we have 1 as rightmost bit, we multiply result by the current base
			if(isOdd) {
				result = result.multiply(baseBig).mod(m._big);
			}
			
			//We square the base
			baseBig = baseBig.multiply(baseBig).mod(m._big);
			
			// We shift the exponent right, effectively removing the last bit
			expBig = expBig.shiftRight(1);
		}
		
		return new MyBigInteger(result);
	}
	
	private static BigInteger modPow(BigInteger base, BigInteger exp, BigInteger m) {
		return modPow(
				new MyBigInteger(base),
				new MyBigInteger(exp),
				new MyBigInteger(m)
			)._big;
	}
	
	//An implementation of the Extended Euclidean Algorithm
	public static MyBigInteger[] extendedEuclid(MyBigInteger a, MyBigInteger b) {
		BigInteger aBig = a._big;
		BigInteger bBig = b._big;
		
		BigInteger old_s = BigInteger.ONE, s = BigInteger.ZERO;
		BigInteger old_t = BigInteger.ZERO, t = BigInteger.ONE;
		BigInteger tmp;
		
		while (bBig.compareTo(BigInteger.ZERO) != 0) {
			BigInteger[] qAndR = aBig.divideAndRemainder(bBig);
			
			aBig = bBig;
			bBig = qAndR[1]; //Set bBig to remainder now
			
			tmp = old_s;
			old_s = s;
			s = tmp.subtract(s.multiply(qAndR[0]));
			
			tmp = old_t;
			old_t = t;
			t = tmp.subtract(t.multiply(qAndR[0]));
		}
		
		return new MyBigInteger[] {
				new MyBigInteger(aBig),
				new MyBigInteger(old_s),
				new MyBigInteger(old_t)
		};
	}
	
	public static int MR_ROUNDS = 50;
	private static BigInteger TWO = new BigInteger("2");
	// Miller-Rabin test
	public static boolean millerRabin(MyBigInteger a, int rounds) {
		// First we have to express a = 2^m * n + 1;
		BigInteger aBig = a._big;
		BigInteger aMinOne = aBig.subtract(BigInteger.ONE);
		int m = aMinOne.getLowestSetBit(); // Find all the powers of 2 (m)
		BigInteger n = aMinOne.shiftRight(m); // Get rid of the powers of 2 by right shifting.
		
		// Create an iteration of random
		Random rnd = new Random();
		
		for (; rounds > 0; rounds--) {
			// Generate a random base
			BigInteger b;
			do {
				b = new BigInteger(aBig.bitLength(), rnd);
			} while (!(b.compareTo(BigInteger.ZERO) > 0 && b.compareTo(aBig) < 0)); 
			// Keep generating if not in not in range (1, a)
			
			// Now, there are two conditions:
			// 	1. b^n mod a = 1, in which case we pass this round
			b = MyBigInteger.modPow(b, n, aBig);
			if (b.equals(BigInteger.ONE))
				continue;
			
			//  2. or, there's some 0 <= k < m for which b^(2^k * n) = -1 mod a
			boolean probablyPrime = false;
			for (int j=0; j < m; j++) {
				// Found a -1
				if (b.equals(aMinOne)) {
					probablyPrime = true;
					break;
				}
				
				b = MyBigInteger.modPow(b, TWO, aBig);
			}
			
			// No -1 was found, we have a witness b
			if (!probablyPrime)
				return false;
		}
		
		return true;
	}
	
	public static MyBigInteger gcd(MyBigInteger a, MyBigInteger b) {
		return extendedEuclid(a, b)[0];
	}
	public static MyBigInteger lcm(MyBigInteger a, MyBigInteger b) {
		return new MyBigInteger(
				a._big.multiply(b._big).divide(gcd(a, b)._big)
			);
	}
	
	// Carmichael function for two numbers p, q
	// Both assumed to be prime, to simplify operations,
	// in which case we simply have lcm(p-1, q-1)
	public static MyBigInteger carmichael(MyBigInteger p, MyBigInteger q) {
		return lcm(p.subtractOne(), q.subtractOne());
	}
	
	// Generate a probable prime
	public static MyBigInteger probablePrime(int nBits) {
		Random rnd = new Random();
		
		MyBigInteger b;
		do {
			b = new MyBigInteger(new BigInteger(nBits, rnd));
		} while (!MyBigInteger.millerRabin(b, MR_ROUNDS));
		
		return b;
	}
	
	public String toString() {
		return _big.toString();
	}
	
	public byte getLastByte() {
		byte[] byteArr = _big.toByteArray();
		return byteArr[byteArr.length - 1];
	}
	
	public byte[] toByteArray() {
		return _big.toByteArray();
	}
	
	public String toCharArray() {
		return new String(_big.toByteArray());
	}
}

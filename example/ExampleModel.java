
public class ExampleModel implements ExampleInterface {
	boolean[] isPrimeMemo = new boolean[0];
	
	int primesLessThan(int N) {
        // initially assume all integers are prime
		if (isPrimeMemo.length < N + 1) {
	        boolean[] isPrime = new boolean[N + 1];
	        for (int i = 2; i <= N; i++) {
	        	isPrimeMemo[i] = true;
	        }
	
	        // mark non-primes <= N using Sieve of Eratosthenes
	        for (int i = 2; i*i <= N; i++) {
	
	            // if i is prime, then mark multiples of i as nonprime
	            // suffices to consider mutiples i, i+1, ..., N/i
	            if (isPrimeMemo[i]) {
	                for (int j = i; i*j <= N; j++) {
	                	isPrimeMemo[i*j] = false;
	                }
	            }
	        }
		}

        // count primes
        int primes = 0;
        for (int i = 2; i <= N; i++) {
            if (isPrimeMemo[i]) primes++;
        }
	}
	
	boolean isPrime(int n) {
		if (isPrimeMemo.length < n)
			primesLessThan(n);
		return isPrimeMemo[n];
	}
}

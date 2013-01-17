package com.jetdrone.and2d.util;

public class MiniMT {
	private long seed;
	private static final int N = 624;
	private static final int M = 397;
	private static final int MATRIX_A = 0x9908b0df;
	private static final int UPPER_MASK = 0x80000000;
	private static final int LOWER_MASK = 0x7fffffff;
	private static final int TEMPERING_MASK_B = 0x9d2c5680;
	private static final int TEMPERING_MASK_C = 0xefc60000;
	private int mt[];
	private int mti;
	private int mag01[] = { 0x0, MATRIX_A };

	public MiniMT() {
		this.setSeed(0);
	}

	public MiniMT(long seed) {
		this.setSeed(seed);
	}

	public void setSeed(final long _seed) {
		seed = _seed;
		mt = new int[N];
		mt[0] = (int) (seed & 0xfffffff);
		for (mti = 1; mti < N; mti++) {
			mt[mti] = (1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti);
			mt[mti] &= 0xffffffff;
		}
	}

	public final int generate() {
		int y;
		if (mti >= N) {
			int kk;
			for (kk = 0; kk < N - M; kk++) {
				y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
				mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
			}
			for (; kk < N - 1; kk++) {
				y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
				mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
			}
			y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
			mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];
			mti = 0;
		}
		y = mt[mti++];
		y ^= y >>> 11;
		y ^= (y << 7) & TEMPERING_MASK_B;
		y ^= (y << 15) & TEMPERING_MASK_C;
		y ^= (y >>> 18);
		return y;
	}
}

package com.mentat.memoize;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*;

import org.junit.Test;

class MemoizeTest {
	private static def LOOKUP = [1:'david', 2:'jerry', 3:'justin', 4:'sarah']

	@Test
	void test() {
		withPool {
			Closure nameFetcher = this.&getName.gmemoize()
			
			println nameFetcher(1)
			println nameFetcher(1)
			println nameFetcher(2)
			println nameFetcher(2)
		}
	}

	private String getName(int id) {
		println 'looking up'
		LOOKUP[id]
	}
}

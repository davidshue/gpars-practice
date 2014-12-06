package com.mentat.async;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*
import groovyx.gpars.AsyncFun

import org.junit.Test

class AsyncTest {

	@Test
	public void test() {
		withPool {
			println slow.callAsync('abc')
			println fast('xyz')
		}
	}
	
	@AsyncFun
	Closure fast = {slow}

	
	Closure slow = { a ->
		println a
	}
}

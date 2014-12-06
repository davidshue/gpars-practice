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
			println fun('123')*.callAsync()
		}
	}
	
	@AsyncFun
	Closure fast = {slow}

	
	Closure slow = { a ->
		println a
	}
	
	/**
	 * functions are not suited for async, using closure instead.
	 * You can still call it through async or asyncFun, but you cannot work the Future and Promise
	 * magic
	 * @param s
	 * @return
	 */
	protected fun(String s) {
		slow.call(s)
	}
}

package com.mentat.async;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*
import groovyx.gpars.AsyncFun
import groovyx.gpars.dataflow.Promise

import java.util.concurrent.Future

import org.junit.Test

class AsyncTest {

	@Test
	public void test() {
		withPool {
			println slow('abc')
			Future future = slow.callAsync('.-.')
			println future.get()
			Promise promise = fast('xyz')
			promise.whenBound {println it}
			
			/**
			 * making a method asynchronous
			 */
			Closure fun = this.&fun.asyncFun()
			Promise funPromise = fun('123')
			funPromise.whenBound {println it}
		}
	}
	
	@AsyncFun
	Closure fast = {slow(it)}

	
	Closure slow = { a ->
		return a.toUpperCase()
	}
	
	/**
	 * functions are not suited for async, using closure instead.
	 * You can still call it through async or asyncFun, but you cannot work the Future and Promise
	 * magic
	 * @param s
	 * @return
	 */
	protected fun(String s) {
		slow(s)
	}
}

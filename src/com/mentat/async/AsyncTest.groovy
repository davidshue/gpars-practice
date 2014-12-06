package com.mentat.async;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*
import groovyx.gpars.AsyncFun
import groovyx.gpars.dataflow.Promise

import java.util.concurrent.Future

import org.junit.Test

class AsyncTest {

	@Test
	void testSlow() {
		def seeds = ['snail1', 'turtle2', 'sloth3']
		println '\ntestSlow\n'
		long start = System.currentTimeMillis()
		withPool {
			(0..50_000).eachParallel {
				slow(it as String)
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
	}
	
	@Test
	void testCallAsync() {
		println '\ntestCallAsync\n'
		def seeds = ['groovy1', 'scala2', 'ruby3']
		long start = System.currentTimeMillis()
		withPool {
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			(0..50_000).each {
				Future future = slow.callAsync(it as String)
				future.get()
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
	}
	
	@Test
	void testAsyncFun() {
		println '\ntestAsyncFun\n'
		def seeds = ['bootstrap1', 'foundation2', 'angular3']
		long start = System.currentTimeMillis()
		withPool {
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			(0..50_000).each {
				Promise promise = fast(it as String)
				promise.whenBound {}
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
	}
	
	@Test
	void testAsyncMethod() {
		println '\ntestAsyncMethod\n'
		def seeds = ['google1', 'apple2', 'yahoo3']
		long start = System.currentTimeMillis()
		withPool {
			Closure fun = this.&fun.asyncFun()
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			(0..50_000).each {
				/**
				 * making a method asynchronous
				 */
				Promise funPromise = fun(it as String)
				funPromise.whenBound {}
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
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

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
		println 'testSlow'
		withPool {
			seeds.eachParallel {
				println slow(it)
			}
		}
	}
	
	@Test
	void testCallAsync() {
		println 'testCallAsync'
		def seeds = ['groovy1', 'scala2', 'ruby3']
		withPool {
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			seeds.each {
				Future future = slow.callAsync(it as String)
				println future.get()
			}
		}
	}
	
	@Test
	void testAsyncFun() {
		println 'testAsyncFun'
		def seeds = ['bootstrap1', 'foundation2', 'angular3']
		withPool {
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			seeds.each {
				Promise promise = fast(it)
				promise.whenBound {println it}
			}
		}
	}
	
	@Test
	void testAsyncMethod() {
		println 'testAsyncMethod'
		def seeds = ['google1', 'apple2', 'yahoo3']
		withPool {
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			seeds.each {
				/**
				 * making a method asynchronous
				 */
				Closure fun = this.&fun.asyncFun()
				Promise funPromise = fun(it)
				funPromise.whenBound {println it}
			}
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

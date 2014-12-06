package com.mentat.async;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*
import groovyx.gpars.AsyncFun
import groovyx.gpars.dataflow.Promise

import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

import org.junit.Test

class AsyncTest {
	private range = (1..100)
	private AtomicInteger counter = new AtomicInteger()

	@Test
	void testSlow() {
		def seeds = ['snail1', 'turtle2', 'sloth3']
		println '\n--testSlow--\n'
		long start = System.currentTimeMillis()
		withPool {
			range.eachParallel {
				concat(it)
				counter.incrementAndGet()
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		println 'total ' + counter.value
	}
	
	@Test
	void testCallAsync() {
		println '\n--testCallAsync--\n'
		def seeds = ['groovy1', 'scala2', 'ruby3']
		long start = System.currentTimeMillis()
		withPool {
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			range.each {
				Future future = concat.callAsync(it)
				future.get()
				counter.incrementAndGet()
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		println 'total ' + counter.value
	}
	
	@Test
	void testAsyncFun() {
		println '\n--testAsyncFun--\n'
		def seeds = ['bootstrap1', 'foundation2', 'angular3']
		long start = System.currentTimeMillis()
		withPool {
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			range.each {
				Promise promise = fastConcat(it)
				promise.whenBound {counter.incrementAndGet()}
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		println 'total ' + counter.value
	}
	
	@Test
	void testAsyncMethod() {
		println '\n--testAsyncMethod--\n'
		def seeds = ['google1', 'apple2', 'yahoo3']
		long start = System.currentTimeMillis()
		withPool {
			Closure fun = this.&fun.asyncFun()
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			range.each {
				/**
				 * making a method asynchronous
				 */
				Promise funPromise = fun(it as String)
				funPromise.whenBound {counter.incrementAndGet()}
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		println 'total ' + counter.value
	}
	
	@AsyncFun
	Closure fast = {slow(it)}

	
	Closure slow = { a ->
		sleep 20
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
		sleep 20
		slow(s)
	}
	
	@AsyncFun
	Closure fastConcat = {
		sleep 20
		fast('concatted ' + it)
	}
	
	Closure concat = {
		sleep 20
		slow('concatted ' + it)
	}
}

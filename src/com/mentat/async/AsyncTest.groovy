package com.mentat.async;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*
import groovyx.gpars.AsyncFun
import groovyx.gpars.dataflow.Promise

import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

import org.junit.Test

class AsyncTest {
	private range = (1..20)
	private AtomicInteger counter = new AtomicInteger()
	@Test
	void testSlow() {
		def seeds = ['snail1', 'turtle2', 'sloth3']
		println '\n--testSlow--\n'
		long start = System.currentTimeMillis()

		range.each {
			concat(it)
			counter.incrementAndGet()
		}

		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		assertEquals range.size(), counter.value
	}
	
	@Test
	void testParallel() {
		def seeds = ['snail1', 'turtle2', 'sloth3']
		println '\n--testParallel--\n'
		long start = System.currentTimeMillis()
		withPool {
			range.eachParallel {
				concat(it)
				counter.incrementAndGet()
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		assertEquals range.size(), counter.value
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
		assertEquals range.size(), counter.value
	}
	
	@Test
	void testAsyncFunFull() {
		println '\n--testAsyncFunFull--\n'
		def seeds = ['bootstrap1', 'foundation2', 'angular3']
		long start = System.currentTimeMillis()
		withPool {
			//range.makeConcurrent()
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			range.each {
				Promise promise = fastConcat(it)
				promise.whenBound {counter.incrementAndGet()}
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		assertEquals range.size(), counter.value
	}
	
	@Test
	void testAsyncFunPartial() {
		println '\n--testAsyncFunPartial--\n'
		def seeds = ['bootstrap1', 'foundation2', 'angular3']
		long start = System.currentTimeMillis()
		withPool {
			// Here you need to use each, not eachParallel. Conventional wisdom here is that since you are calling
			// it async, it returns right away, so no need to eachParallel. If you do, it will throw exceptions here
			range.each {
				Promise promise = fastConcatPartial(it)
				promise.whenBound {counter.incrementAndGet()}
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		assertEquals range.size(), counter.value
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
				Promise funPromise = fun(it)
				funPromise.whenBound {counter.incrementAndGet()}
			}
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
		assertEquals range.size(), counter.value
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
	protected fun(s) {
		sleep 20
		slow('concatted ' + s)
	}
	
	@AsyncFun
	Closure fastConcatPartial = {
		sleep 20
		slow('concatted ' + it)
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

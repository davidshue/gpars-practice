package com.mentat.async;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*

import java.util.concurrent.atomic.AtomicInteger

import org.junit.Test

class AsyncFunTest {

	@Test
	public void testAsyncFun() {
		withPool() {
			Closure plus = {
				Integer a, Integer b ->
				sleep 3000
				println 'Adding numbers'
				a + b
			}.asyncFun()

			Closure multiply = {
				Integer a, Integer b ->
				sleep 2000
				a * b
			}.asyncFun()

			Closure measureTime = {

				->
				sleep 3000
				4
			}.asyncFun()

			Closure distance = {
				Integer initialDistance, Integer velocity, Integer time ->
				plus(initialDistance, multiply(velocity, time))
			}.asyncFun()

			Closure chattyDistance = {
				Integer initialDistance, Integer velocity, Integer time ->
				println 'All parameters are now ready - starting'
				println 'About to call another asynchronous function'
				def innerResultPromise = plus(initialDistance, multiply(velocity, time))
				println 'Returning the promise for the inner calculation as my own result'
				return innerResultPromise
			}.asyncFun()
			
			AtomicInteger counter = new AtomicInteger()
			(1..10).eachParallel {  
				distance(100, it, measureTime()).whenBound {
					counter.incrementAndGet()
					println it
				}
			}
			
			while(counter.get() != 10) {
				sleep 1000
			}

			//println "Distance = " + distincePromise.get() + ' m'
			//println "ChattyDistance = " + chattyDistance(100, 20, measureTime()).get() + ' m'
		}
	}

}

package com.mentat.then;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*
import groovyx.gpars.AsyncFun
import groovyx.gpars.dataflow.Promise

import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

import org.junit.Test

class ThenTest {
	private range = (1..20000)

	@Test
	void testReadWrite() {
		PeekingActor actor = new PeekingActor()
		actor.start()
		
		withPool {
			Closure acting = this.&then.asyncFun()
			Promise promise = acting(actor)
			while(!promise.bound) {
				println actor
				sleep 1000
			}
		}
		
		actor.stop()
		println actor
	}
	private void then(PeekingActor actor) {
		def seeds = ['snail1', 'turtle2', 'sloth3', 'snake4', 'seal5', 'frog6', 'beetle7']
		println '\n--testThen--\n'
		long start = System.currentTimeMillis()
		withPool() {
			Closure asyncRead = {
				readStep(it)
			}.asyncFun()
			range.each {
				asyncRead(it as String).then(
					{
					actor << 'read'
					writeStep(it); 
					actor << 'write'
					},
					{ e -> throw new RuntimeException('Error occurred', e)}
				)
			}
		}
		while (true) {
			if (actor.writeCount == range.size()) {
				break
			}
			sleep 50
		}
		long end = System.currentTimeMillis()
		println 'took ' + (end-start) + ' ms'
	}

	
	Closure readStep = {
		int sleepTime = new Random().nextInt(181) + 20 // between 20ms and 200ms
		sleep sleepTime
		return it.toUpperCase()
	}
	
	Closure writeStep = {
		int sleepTime = new Random().nextInt(1001) + 100 // between 100ms and 1100 ms
		sleep sleepTime
		return 'Concatted ' + it
	}

}

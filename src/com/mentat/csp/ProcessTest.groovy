package com.mentat.csp;

import static org.junit.Assert.*
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.ResizeablePool

import java.util.concurrent.Callable

import org.junit.Test

class ProcessTest {

	@Test
	void testProcess() {
		def group = new DefaultPGroup(new ResizeablePool(true))

		def t = group.task {
		    println "I am a process"
		}
		//t.get()
		
		t.join()
	}
	
	@Test
	void testRunnableProcess() {
		def group = new DefaultPGroup(new ResizeablePool(true))
		
		def t = group.task new MyRunnableProcess()
		
		t.join()
	}
	
	@Test
	void testCallableProcess () {
		def group = new DefaultPGroup(new ResizeablePool(true))
		
		def t = group.task new MyCallableProcess()
		
		println t.get()
	}
	
	class MyRunnableProcess implements Runnable {
		
		@Override
		void run() {
			println "I am a process"
		}
	}
	
	class MyCallableProcess implements Callable<String> {
		@Override
		String call() {
			println "I am a process"
			return "CSP is great!"
		}
	}

}

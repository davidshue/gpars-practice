package com.mentat.csp;

import static org.junit.Assert.*
import groovy.transform.TupleConstructor
import groovyx.gpars.dataflow.DataflowReadChannel
import groovyx.gpars.dataflow.DataflowWriteChannel
import groovyx.gpars.dataflow.SyncDataflowQueue
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.ResizeablePool

import java.util.concurrent.Callable

import org.junit.Test

class Channel2Test {

	@Test
	void test() {
		def group = new DefaultPGroup(new ResizeablePool(true))
		
		def a = new SyncDataflowQueue()
		def b = new SyncDataflowQueue()
		def c = new SyncDataflowQueue()
		
		group.task new Formatter(a, b)
		group.task new Greeter(b, c)
		
		a << "Joe"
		a << "Dave"
		println c.val
		println c.val
	}
	
	@TupleConstructor
	class Formatter implements Callable<String> {
		DataflowReadChannel rawNames
		DataflowWriteChannel formattedNames
	
		@Override
		String call() {
			while(!Thread.currentThread().isInterrupted()) {
				String name = rawNames.val
				formattedNames << name.toUpperCase()
			}
		}
	}
	
	@TupleConstructor
	class Greeter implements Callable<String> {
		DataflowReadChannel names
		DataflowWriteChannel greetings
	
		@Override
		String call() {
			while(!Thread.currentThread().isInterrupted()) {
				String name = names.val
				greetings << "Hello " + name
			}
		}
	}

}

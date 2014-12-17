package com.mentat.csp

import static org.junit.Assert.*
import groovy.transform.TupleConstructor
import groovyx.gpars.dataflow.DataflowReadChannel
import groovyx.gpars.dataflow.DataflowWriteChannel
import groovyx.gpars.dataflow.SyncDataflowQueue
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.scheduler.ResizeablePool

import java.util.concurrent.Callable

import org.junit.Test

class ChannelTest {

	@Test
	void test() {
		def group = new DefaultPGroup(new ResizeablePool(true))
		
		def a = new SyncDataflowQueue()
		def b = new SyncDataflowQueue()
		
		group.task new Greeter(a, b)
		
		a << "Joe"
		println b.val
		a << "Dave"
		println b.val
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
			return "CSP is great!"
		}
	}
}

package com.mentat.dataflow

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.DataflowBroadcast
import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.DataflowReadChannel
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.dataflow.DataflowWriteChannel
import groovyx.gpars.dataflow.Dataflows

import org.junit.Test

class FirstDataflowTest {
	@Test
	void testDataflow() {
		final def x = new DataflowVariable()
		final def y = new DataflowVariable()
		final def z = new DataflowVariable()
		
		task {
			z << x.val + y.val
		}
		
		task {
			x << 10
		}
		
		task {
			y << 5
		}
		
		println "Result: ${z.val}"
	}
	
	@Test
	void testDataflows() {
		final def df = new Dataflows()
		
		task {
			df.z = df.x + df.y
		}
		
		task {
			df.x = 10
		}
		
		task {
			df.y = 5
		}
		
		println "Result: ${df.z}"
	}
	
	@Test
	void testDataflowQueue() {
		def words = ['Groovy', 'fantastic', 'concurrency', 'fun', 'enjoy', 'safe', 'GPars', 'data', 'flow']
		final def buffer = new DataflowQueue()
		
		task {
			for (word in words) {
				buffer << word.toUpperCase()  //add to the buffer
			}
		}
		
		task {
			while(true) println buffer.val  //read from the buffer in a loop
		}
	}
	
	@Test
	void testPubSub() {
		DataflowWriteChannel broadcastStream = new DataflowBroadcast()
		DataflowReadChannel stream1 = broadcastStream.createReadChannel()
		DataflowReadChannel stream2 = broadcastStream.createReadChannel()
		broadcastStream << 'Message1'
		broadcastStream << 'Message2'
		broadcastStream << 'Message3'
		assert stream1.val == stream2.val
		assert stream1.val == stream2.val
		assert stream1.val == stream2.val
	}
}

package com.mentat.speculate;

import static groovyx.gpars.GParsPool.speculate
import static groovyx.gpars.GParsPool.withPool
import static groovyx.gpars.dataflow.Dataflow.task
import static org.junit.Assert.*
import groovyx.gpars.dataflow.DataflowQueue

import org.junit.Test

class SpeculateTest {

	@Test
	public void test() {
		def alternative1 = {
			'http://www.dzone.com/links/index.html'.toURL().text
		}

		def alternative2 = {
			'http://www.dzone.com/'.toURL().text
		}

		def alternative3 = {
			'http://www.dzzzzzone.com/'.toURL().text  //wrong url
		}

		def alternative4 = {
			'http://dzone.com/'.toURL().text
		}

		withPool(4) {
			println speculate([alternative1, alternative2, alternative3, alternative4]).contains('groovy')
		}
	}
	
	@Test
	void testWithDataFlow() {
		def alternative1 = {
			'http://www.dzone.com/links/index.html'.toURL().text
		}
		
		def alternative2 = {
			'http://www.dzone.com/'.toURL().text
		}
		
		def alternative3 = {
			'http://www.dzzzzzone.com/'.toURL().text  //will fail due to wrong url
		}
		
		def alternative4 = {
			'http://dzone.com/'.toURL().text
		}
		
		//Pick either one of the following, both will work:
		final def result = new DataflowQueue()
		//  final def result = new DataflowVariable()
		
		[alternative1, alternative2, alternative3, alternative4].each {code ->
			task {
				try {
					result << code()
				} catch (ignore) { }  //We deliberately ignore unsuccessful urls
			}
		}
		
		println result.val.contains('groovy')
	}

}

package com.mentat.speculate;

import static groovyx.gpars.GParsPool.speculate
import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*;

import org.junit.Test;

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

}

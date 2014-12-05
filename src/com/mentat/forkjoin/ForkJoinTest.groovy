package com.mentat.forkjoin

import static groovyx.gpars.GParsPool.runForkJoin
import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*

import org.junit.Test

class ForkJoinTest {

	@Test
	public void test() {
		withPool() {
			println """Number of files: ${
        runForkJoin(new File("/Users/dshue1/Downloads")) {file ->
            long count = 0
            file.eachFile {
                if (it.isDirectory()) {
                    println "Forking a child task for $it"
					forkOffChild(it)           //fork a child task
				} else {
					count++
				}
			}
			return count + (childrenResults.sum(0))
			//use results of children tasks to calculate and store own result
		}
	}"""
}
}

}

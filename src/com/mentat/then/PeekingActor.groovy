package com.mentat.then

import groovy.transform.ToString
import groovyx.gpars.actor.DefaultActor


@ToString(includeNames=true, includeFields=true, excludes=['startTime', 'endTime'])
class PeekingActor extends DefaultActor {
	int readCount = 0
	int writeCount = 0
	boolean failure = false
	int maxDiscrepancy = 0
	
	private long startTime
	private long endTime
	
	int getReadWriteDiscrepancy() {
		readCount - writeCount
	}

	@Override
	void afterStart() {
		startTime = System.currentTimeMillis()
	}
	
	@Override
	void afterStop(List undeliveredMessages) {
		endTime = System.currentTimeMillis()
		println 'Took ' + (endTime - startTime) + ' ms'
		println this
	}
	
	void act() {
		loop {
			react {type ->
				switch(type) {
					case 'read':
						readCount++
						break
					case 'write':
						writeCount++
						break
					case 'failure':
						failure = true
						break
					default: break
				}
				if (readWriteDiscrepancy > maxDiscrepancy) maxDiscrepancy = readWriteDiscrepancy
			}
		}
	}
}

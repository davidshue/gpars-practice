package com.mentat.withPool;

import static groovyx.gpars.GParsPool.withPool
import static org.junit.Assert.*
import groovyx.gpars.ParallelEnhancer

import org.junit.Test

class WithPoolTest {

	@Test
	public void testWithPool() {
		def range = (0..10)
		println 'Sequential: '
		range.each { print it + ',' }
		println()

		withPool {

			println 'Sequential: '
			range.each { print it + ',' }
			println()

			range.makeConcurrent()

			println 'Concurrent: '
			range.each { print it + ',' }
			println()

			range.makeSequential()

			println 'Sequential: '
			range.each { print it + ',' }
			println()
			
			range.asConcurrent {
				println 'Concurrent: '
				range.each { print it + ',' }
				println()
			}
		}
		
		println 'Sequential: '
		range.each { print it + ',' }
		println()	
	}
	
	private selectImportantNames(names) {
		names.collect {it.toUpperCase()}.findAll{it.size() > 4}
	}
	
	@Test
	void testTransparent() {
		def names = ['Joe', 'Alice', 'Dave', 'Jason']
		ParallelEnhancer.enhanceInstance(names)
		//The selectImportantNames() will process the name collections concurrently
		println selectImportantNames(names.makeConcurrent())
	}
	
	@Test
	void testMomoize() {
		def urls = ['http://www.dzone.com', 'http://www.theserverside.com', 'http://www.infoq.com']
		Closure download = {url ->
			println "Downloading $url"
			url.toURL().text.toUpperCase()
		}
		withPool {
			Closure cachingDownload = download.gmemoize()
			println 'Groovy sites today: ' + urls.findAllParallel {url -> cachingDownload(url).contains('GROOVY')}
			println 'Grails sites today: ' + urls.findAllParallel {url -> cachingDownload(url).contains('GRAILS')}
			println 'Griffon sites today: ' + urls.findAllParallel {url -> cachingDownload(url).contains('GRIFFON')}
			println 'Gradle sites today: ' + urls.findAllParallel {url -> cachingDownload(url).contains('GRADLE')}
			println 'Concurrency sites today: ' + urls.findAllParallel {url -> cachingDownload(url).contains('CONCURRENCY')}
			println 'GPars sites today: ' + urls.findAllParallel {url -> cachingDownload(url).contains('GPARS')}
		}
	}

}

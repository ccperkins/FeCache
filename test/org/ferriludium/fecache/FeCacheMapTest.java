package org.ferriludium.fecache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;


/*******************************************************************************
 * Copyright [2016] [Cornelius Perkins]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *    Cornelius Perkins - initial API and implementation and/or initial documentation
 *    
 * Author Cornelius Perkins (ccperkins at both github and bitbucket)
 *******************************************************************************/ 

public class FeCacheMapTest {

	private class TestMessage {
		public final String id;
		@SuppressWarnings("unused")
		public final String contents;
		public TestMessage(String id, String contents) {
			super();
			this.id = id;
			this.contents = contents;
		}
	}

	private void createAndStore (String key, String value, long expiry, FeCache<String, TestMessage> cache) {
		TestMessage msg = new TestMessage (key, value);
		cache.store(msg.id, msg, new FeCache.CacheExpiry(expiry));		
	}

	@Test
	public void testPruningFixedExpiry() {
		// create cache
		FeCache<String, TestMessage> cache = new FeCache<> (FeCache.ExpiryPolicy.FIXED);
		// insert and age the cached values
		prepareTest(cache);
		// retrieve and test each of the four rows.  Expected:  AA, BB gone, CC, DD exist
		TestMessage m = cache.retrieve("AA"); assertEquals(null, m);
		m = cache.retrieve("BB"); assertEquals(null, m);
		m = cache.retrieve("CC"); assertNotEquals(null, m);
		m = cache.retrieve("DD"); assertNotEquals(null, m);
	}

	@Test
	public void testPruningAccessExtendsLife() {
		// create cache
		FeCache<String, TestMessage> cache = new FeCache<> (FeCache.ExpiryPolicy.EXTENDED_BY_ACCESS);
		// insert and age the cached values
		prepareTest(cache);
		// retrieve and test each of the four rows.  Expected:  All four exist
		TestMessage m = cache.retrieve("AA"); assertNotEquals(null, m);
		m = cache.retrieve("BB"); assertNotEquals(null, m);
		m = cache.retrieve("CC"); assertNotEquals(null, m);
		m = cache.retrieve("DD"); assertNotEquals(null, m);
		// Now access some of the entries and wait a bit, 
		//   then access again.  Expected: the ones we 
		//   accessed will still be there, but the ones
		//   we didn't will be gone.
		for (int ii=0; ii < 5; ii++) {
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			m = cache.retrieve("AA"); assertNotEquals(null, m);
			m = cache.retrieve("BB"); assertNotEquals(null, m);
		}
		m = cache.retrieve("AA"); assertNotEquals(null, m);
		m = cache.retrieve("BB"); assertNotEquals(null, m);
		m = cache.retrieve("CC"); assertEquals(null, m);
		m = cache.retrieve("DD"); assertEquals(null, m);		
	}


	void prepareTest (FeCache<String, TestMessage> cache) {
		// insert two rows with 100 ms expiry AA, BB, two rows with 200 ms expiry CC, DD
		createAndStore ("AA", "go away harry ", 100L, cache);
		createAndStore ("BB", "good to know  ", 100L, cache);
		createAndStore ("CC", "just in case  ", 200L, cache);
		createAndStore ("DD", "you are my son", 200L, cache);
		// 3x { sleep 50 ms; touch each of the entries }
		for (int ii=0; ii < 3; ii++) {
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			TestMessage m = cache.retrieve("AA");
			m = cache.retrieve("BB");
			m = cache.retrieve("CC");
			m = cache.retrieve("DD");
		}
		// request prune
		cache.requestPrune();
		
		// At this point, if access extends, all entries will still be live, but if 
		// access is fixed, AA and BB will have been pruned, and CC and DD should still be present.
		
	}

	/*
	@Test
	public void testPrunerThreadDirectly() {
		// Need a cache to test
		FeCacheMap<String, TestMessage> cache = new FeCacheMap<> (new FeCacheManagementPolicy(LifeLengthRule.EXPIRY_TIME_EXTENDED_BY_ACCESS));
		FeCacheMap.Pruner pruner = new FeCacheMap.Pruner(100);
		pruner.start();
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			System.out.println("Requesting stop");
			pruner.requestStop();
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			try { Thread.sleep (50); } catch (InterruptedException e) { e.printStackTrace(); fail("interrupted");}
			System.out.println("Should be done by now");
	}
	*/


}


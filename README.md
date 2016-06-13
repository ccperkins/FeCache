# README #

FeCache is an extremely lightweight (*) simple caching system in Java which allows 
entries to be cached with different expiration times, specified in milliseconds - 
that is, each entry can be given a different time-to-live.

* By "extremely lightweight" I mean that the jar including the unit tests is 7492 bytes.

It works like a Map, with the exception that entries are removed (pruned) when
they expire (or never: it is possible to insert entries which will never expire.

When a cache is created, it is given a cache management policy, which can specify 
either that expiry times are constant, or that as entries are accessed, their expiry 
time is updated.  For example, under the "constant" expiry policy, an entry which is 
given 1000 ms expiry will be prunable 1000 ms from insertion no matter how many times 
it's accessed.  Under "access extends" expiry policy, the same entry will only 
become prunable 1000 ms after its last access.

Pruning is performed passively - that is, entries which are due to expire are 
only removed (pruned) when retrieval attempts are made.  At each access to the cache whether for insertion or retrieval, the pruning process is performed, so all ready-to-expire entries will be removed.


### Getting set up ###
* The simplest way to get set up is just to download the jar and include it in your classpath, then:
    1. Decide what kind of entries you wish to cache, and how you'll retrieve 
         them.  In the example below, the entries are called TestMessage, and 
         they're identified and retrieved by unique String identifiers, but 
         anything with valid hash/equals methods will do for a key.
    2. Create the cache:
	`FeCache<String, TestMessage> cache = new FeCache<> (new FeCacheManagementPolicy(LifeLengthRule.EXPIRY_TIME_CONSTANT));`

		
    3. Then insert entries, Given a TestMessage entry called msg and an expiry time in milliseconds, store an entry:
	`TestMessage msg = new TestMessage (someKey, blah, ...);`
	`cache.store(someKey, msg, new SimpleCacheExpiry(expiry));`		


    4. Retrieval is also simple:
	`TestMessage m = cache.retrieve(someKey);`
		
		If the entry has expired and been pruned, the return will be null.

		
	


### Contribution guidelines ###

At this point, this is a one-man show, but if you have comments, questions, or suggestions I'm happy to hear them.


### Who do I talk to? ###

* Repo owner or admin
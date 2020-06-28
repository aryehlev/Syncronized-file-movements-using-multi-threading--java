//323746016
/**
 * A synchronized bounded-size queue for multithreaded producer-consumer applications.
 *
 * @param <T> Type of data items
 */
public class SynchronizedQueue<T> {

	private T[] buffer;
	private int producers;
	private int capacity;
	private int outIndex;
	private int inIndex;
	private int count;
	private Object queueLock;


	/**
	 * Constructor. Allocates a buffer (an array) with the given capacity and
	 * resets pointers and counters.
	 * @param capacity Buffer capacity
	 */
	@SuppressWarnings("unchecked")
	public SynchronizedQueue(int capacity) {
		this.buffer = (T[])(new Object[capacity]);
		this.producers = 0;
		this.count = 0;
		this.outIndex = 0;
		this.inIndex = 0;
		this.queueLock = new Object();
		this.capacity = capacity;

	}

	/**
	 * Dequeues the first item from the queue and returns it.
	 * If the queue is empty but producers are still registered to this queue,
	 * this method blocks until some item is available.
	 * If the queue is empty and no more items are planned to be added to this
	 * queue (because no producers are registered), this method returns null.
	 *
	 * @return The first item, or null if there are no more items
	 * @see #registerProducer()
	 * @see #unregisterProducer()
	 */
	public T dequeue() {
		synchronized (this.queueLock) {
			while (this.producers != 0 && this.count == 0) { //if there are producers and the queue is empty
				try {
					this.queueLock.wait();                //if empty wait for producers to put in
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			T itemTODequeue = null;
			if (this.count != 0) {		//if not empty get an item or else return null
				itemTODequeue = this.buffer[this.outIndex];        // return the item that is now in out index
				this.outIndex = (this.outIndex + 1) % this.capacity; 	// update out index in circuler fashion

				if(this.outIndex == this.inIndex){  //updates the amount of elements in the queue, if they are equal then reset counter
					count = 0;
				}
				else {
					count--;
				}
			}
			this.queueLock.notifyAll();
			return itemTODequeue;
		}
	}

	/**
	 * Enqueues an item to the end of this queue. If the queue is full, this
	 * method blocks until some space becomes available.
	 *
	 * @param item Item to enqueue
	 */
	public void enqueue(T item) {
		synchronized (this.queueLock) {
			while (this.capacity == this.count) { //if the queue is full then wait
				try {
					this.queueLock.wait();                //if empty wait for producers to put
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.buffer[this.inIndex] = item;      // put the item in the in index
			this.inIndex = (this.inIndex + 1) % this.capacity; 	// update in index in circuler fashion

			if(this.outIndex == this.inIndex){  //updates the amount of elements in the queue, if they are equal then reset counter
				count = 0;
			}
			else {
				count++;
			}

			this.queueLock.notifyAll();
		}
	}

	/**
	 * Returns the capacity of this queue
	 * @return queue capacity
	 */
	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * Returns the current size of the queue (number of elements in it)
	 * @return queue size
	 */
	public int getSize()
	{
		return this.count;
	}

	/**==
	 * Registers a producer to this queue. This method actually increases the
	 * internal producers counter of this queue by 1. This counter is used to
	 * determine whether the queue is still active and to avoid blocking of
	 * consumer threads that try to dequeue elements from an empty queue, when
	 * no producer is expected to add any more items.
	 * Every producer of this queue must call this method before starting to
	 * enqueue items, and must also call <see>{@link #unregisterProducer()}</see> when
	 * finishes to enqueue all items.
	 *
	 * @see #dequeue()
	 * @see #unregisterProducer()
	 */
	public  synchronized void registerProducer() {
		synchronized (queueLock)
		{
			this.producers++;
		}
	}

	/**
	 * Unregisters a producer from this queue. See <see>{@link #registerProducer()}</see>.
	 *
	 * @see #dequeue()
	 * @see #registerProducer()
	 */
	public void unregisterProducer() {
		synchronized (queueLock)
		{
			this.producers--;
			this.queueLock.notifyAll();;
		}
	}
}

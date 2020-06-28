//323746016
import java.io.File;


public class Scouter implements Runnable  {
    private SynchronizedQueue<File> directoryQueue;
    private java.io.File root;


    public Scouter(SynchronizedQueue<File> directoryQueue,
                   File root)
    {
        this.directoryQueue = directoryQueue;
        this.root =root;
    }


    @Override
    public void run() {
        this.directoryQueue.registerProducer();  // registers as producer
        putDirectoriesInQueueRecursivally(root, directoryQueue);
        this.directoryQueue.unregisterProducer();
    }

    public void putDirectoriesInQueueRecursivally(File root, SynchronizedQueue<java.io.File> directoryQueue) {
        // Get all files from a directory.
        File[] directoryList = root.listFiles();
        if(directoryList != null)  //put in all sub directories
            for (File file : directoryList) {
                if (file.isDirectory()) {
                    directoryQueue.enqueue(file); //put in queue
                    putDirectoriesInQueueRecursivally(file, directoryQueue);  //recursivally call on all sub directories
                }
            }
    }
}


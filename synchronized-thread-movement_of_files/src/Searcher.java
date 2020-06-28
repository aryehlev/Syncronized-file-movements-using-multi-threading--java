//323746016
import java.io.File;

public class Searcher implements Runnable {

    String pattern;
    String extension;
    SynchronizedQueue<java.io.File> directoryQueue;
    SynchronizedQueue<java.io.File> resultsQueue;

    public Searcher(String pattern, String extension, SynchronizedQueue<File> directoryQueue, SynchronizedQueue<File> resultsQueue) {
        this.pattern = pattern;
        this.extension = extension;
        this.directoryQueue = directoryQueue;
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run() {
        this.resultsQueue.registerProducer();
        File directoryToFetchFilesFrom = directoryQueue.dequeue(); //get directory
        while(directoryToFetchFilesFrom != null) {
            File[] dirs = directoryToFetchFilesFrom.listFiles(); ////get a list of files in directory
            for (File file : dirs) {   //put all files in that directory into queue
                if (file.isFile() && file.getName().endsWith(this.extension) && file.getName().contains(this.pattern)) {  //checks condition and adds to queue accordingly
                    this.resultsQueue.enqueue(file);
                }
            }

            directoryToFetchFilesFrom = directoryQueue.dequeue(); //get directory
        }

        this.resultsQueue.unregisterProducer();
    }
}
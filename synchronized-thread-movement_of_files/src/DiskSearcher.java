//323746016
import java.io.File;

public class DiskSearcher {
    static int	DIRECTORY_QUEUE_CAPACITY = 50;
    static int	RESULTS_QUEUE_CAPACITY = 50;

    public static void	main(String[] args){

        try {
            //make all arguments and queues
            String fileNamePattern = args[0];
            String fileExtension = args[1];
            File rootDirectory = new File(args[2]);
            File destDirectory = new File(args[3]);
            int numOfSearchers = Integer.parseInt(args[4]);
            int numOfCopiers = Integer.parseInt(args[5]);

            SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<>(DIRECTORY_QUEUE_CAPACITY);
            SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<>(RESULTS_QUEUE_CAPACITY);
            //run scouter thread
            Scouter scouter = new Scouter(directoryQueue , rootDirectory);
            Thread scouterThread = new Thread(scouter);  //makes a thread
            scouterThread.start();      //runs the run method

            //run number of searchers threads
            Thread[] copierThreads = new Thread[numOfCopiers];
            Thread[] searcherThreads = new Thread[numOfSearchers];
            for(int i = 0; i < numOfSearchers; i++){
                //makes new searcher threads
                searcherThreads[i] = new Thread(new Searcher(fileNamePattern, fileExtension, directoryQueue , resultsQueue));
                searcherThreads[i].start();
            }
            for(int i = 0; i < numOfCopiers; i++){
                //makes new copier threads
                copierThreads[i] = new Thread(new Copier(destDirectory, resultsQueue));
                copierThreads[i].start();
            }

            scouterThread.join();
            for(int i = 0; i < numOfSearchers; i++){
                searcherThreads[i].join();
            }

            for(int i = 0; i < numOfCopiers; i++){
                copierThreads[i].join();
            }

        }
        catch(NullPointerException | NumberFormatException | InterruptedException e){
            e.printStackTrace();
        }

    }
}

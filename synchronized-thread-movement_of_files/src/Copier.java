//323746016
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Copier implements Runnable{

    private File destination;
    private SynchronizedQueue<File> resultsQueue;

    public Copier(File destination, SynchronizedQueue<File> resultsQueue) {
        this.destination = destination;
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run(){
        File fileToCopy = resultsQueue.dequeue();
        while(fileToCopy != null){
            Path dir = this.destination.toPath();           //make path for destination file
            try {
                Files.copy(fileToCopy.toPath(), dir.resolve(fileToCopy.toPath().getFileName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileToCopy = resultsQueue.dequeue();
        }
    }
}

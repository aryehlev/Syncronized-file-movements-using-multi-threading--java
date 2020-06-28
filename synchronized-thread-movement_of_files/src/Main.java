//323746016
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) {

        List<String> lines = getLinesFromFile();
        System.out.println("Number of lines found: " + lines.size());
        System.out.println("Starting to process");

        long startTimeWithoutThreads = System.currentTimeMillis();
        workWithoutThreads(lines);
        long elapsedTimeWithoutThreads = (System.currentTimeMillis() - startTimeWithoutThreads);
        System.out.println("Execution time: " + elapsedTimeWithoutThreads);


        long startTimeWithThreads = System.currentTimeMillis();
        workWithThreads(lines);
        long elapsedTimeWithThreads = (System.currentTimeMillis() - startTimeWithThreads);
        System.out.println("Execution time: " + elapsedTimeWithThreads);

    }

    private static void workWithThreads(List<String> lines) {
        //Your code:
        //Get the number of available cores
        //Assuming X is the number of cores - Partition the data into x data sets
        //Create a fixed thread pool of size X
        //Submit X workers to the thread pool
		//Wait for termination
        int x = Runtime.getRuntime().availableProcessors();
        ArrayList<List<String>> arrayList = new  ArrayList<List<String>>();
        int lengthOfPartition = (int)Math.ceil((lines.size() / (double)x));
        for(int i = 0; i < lines.size(); i = i + lengthOfPartition ){
            arrayList.add(lines.subList(i, Math.min(i + lengthOfPartition , lines.size())));
        }

        ExecutorService executorService  = Executors.newFixedThreadPool(x);

        Runnable[] workers = new Worker[x];

        Iterator<List<String>> itr = arrayList.iterator();
        int i = 0;
        while(itr.hasNext()){
            workers[i] = new Worker(itr.next());
            executorService.submit(workers[i]);
            i++;
        }

        long timeToWait = (long)30;
        executorService.shutdown();

        try {
            executorService.awaitTermination(timeToWait, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    private static void workWithoutThreads(List<String> lines) {
        Worker worker = new Worker(lines);
        worker.run();
    }

    private static List<String> getLinesFromFile() {
        //Your code:
        //Read the shakespeare file provided from C:\Temp\Shakespeare.txt
        //and return an ArrayList<String> that contains each line read from the file.
        ArrayList<String> arrayList = new ArrayList<>();
        File shakespeare = new File("C:\\Temp\\Shakespeare.txt");
        try {
            BufferedReader buffReader = new BufferedReader(new FileReader(shakespeare));
            String str = null;
            while ((str = buffReader.readLine()) != null) {
                arrayList.add(str);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return arrayList;
    }
}

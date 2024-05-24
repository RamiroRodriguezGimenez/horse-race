import horse.Horse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    private static final Random random = new Random();
    public static void main(String[] args) throws InterruptedException {

        List<Horse> horses = horseGenerator(5);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor;
        if (10 > availableProcessors) {
            executor = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            executor = Executors.newFixedThreadPool(10);
        }
        //Thread thread = new Thread(horses.get(0));
        //thread.run();
        List<Thread> threads= new ArrayList<>();
        for (Horse horse: horses) {
            Thread thread = Thread.ofVirtual().unstarted(horse);
            threads.add(thread);
        }
        for (Thread thread: threads){
            thread.start();
        }
        for (Thread thread: threads){
            thread.join();
        }

        executor.shutdown();
    }


    public static List<Horse> horseGenerator(int horseNumber) {
        List<Horse> horses = new ArrayList<>();
        for (int i = 0; i < horseNumber; i++) {
            String name = "Caballo " + (i + 1);
            int speed = random.nextInt(3) + 1;
            int stamina = random.nextInt(3) + 1;
            horses.add(new Horse(name, speed, stamina));
        }
        return horses;
    }
}
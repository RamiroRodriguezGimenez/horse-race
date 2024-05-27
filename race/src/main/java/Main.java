import horse.Horse;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Main {
    private static final Random random = new Random();
    private static final Semaphore semaphore = new Semaphore(3);
    private static final int horseNumber = 10;
    public static void main(String[] args) throws InterruptedException {

        List<Horse> horses = horseGenerator(horseNumber);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        /*ExecutorService executor;
        if (horseNumber > availableProcessors) {
            executor = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            executor = Executors.newFixedThreadPool(10);
        }*/

        List<Thread> threads= new ArrayList<>();
        if (horseNumber > availableProcessors) {
            for (Horse horse: horses) {
                Thread thread = Thread.ofVirtual().unstarted(horse);
                threads.add(thread);
            }
        } else {
            for (Horse horse: horses) {
                Thread thread = new Thread(horse);
                threads.add(thread);
            }
        }

        for (Thread thread: threads){
            thread.start();
        }
        for (Thread thread: threads){
            thread.join();
        }
        reportResults(horses);

    }


    public static List<Horse> horseGenerator(int horseNumber) {
        List<Horse> horses = new ArrayList<>();
        for (int i = 0; i < horseNumber; i++) {
            String name = "Caballo " + (i + 1);
            int speed = random.nextInt(3) + 1;
            int stamina = random.nextInt(3) + 1;
            horses.add(new Horse(name, speed, stamina, semaphore));
        }
        return horses;
    }
    private static void reportResults(List<Horse> horses) {
        Map<Integer, String> results = new HashMap<>();

        for (Horse horse : horses) {
            results.put(horse.getPosition(), horse.getName());
        }

        System.out.println("*******Resultados de la carrera******");
        for (int i = 1; i <= 3; i++) {
            System.out.println("Posición " + (i) + ": " + results.get(i) );
        }
    }
}
import horse.Horse;
import race.Sprinter;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Main {
    private static final Random random = new Random();
    private static final Semaphore semaphore = new Semaphore(3);
    private static  Sprinter sprinter;
    private static final int horseNumber = 5;
    public static void main(String[] args) throws InterruptedException {

        sprinter = new Sprinter();
        Thread sprinterThread = new Thread(sprinter);
        sprinterThread.setDaemon(true);
       /** no es necesario que finalice para que termine la ejecucion de la carrera.
        Esperar que termine podria hacer al usuari esperar hasta 15 segundos innecesarios.*/

        sprinterThread.start();

        List<Horse> horses = horseGenerator(horseNumber, sprinter);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        /*ExecutorService executor;
        if (horseNumber > availableProcessors) {
            executor = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            executor = Executors.newFixedThreadPool(10);
        }*/

        List<Thread> threads= new ArrayList<>();
        if (horseNumber > availableProcessors-1) {
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


    public static List<Horse> horseGenerator(int horseNumber, Sprinter sprinter) {
        List<Horse> horses = new ArrayList<>();
        for (int i = 0; i < horseNumber; i++) {
            String name = "Caballo " + (i + 1);
            int speed = random.nextInt(3) + 1;
            int stamina = random.nextInt(3) + 1;
            horses.add(new Horse(name, speed, stamina, semaphore, sprinter));
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
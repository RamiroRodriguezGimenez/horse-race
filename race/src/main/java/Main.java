import horse.Horse;
import race.Hole;
import race.Sprinter;

import java.util.*;
import java.util.concurrent.Semaphore;

import static utils.Constants.*;

public class Main {
    private static final Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);
    private static final Semaphore semaphore = new Semaphore(3);
    private static  Sprinter sprinter;
    private static final int horseNumber =10;

    public static void main(String[] args) {
        System.out.println("ingrese la cantidad de caballos para la carrera: ");
        int numberOfHorses = scanner.nextInt();

        sprinter = new Sprinter();
        Thread sprinterThread = new Thread(sprinter);
        sprinterThread.setDaemon(true);
       /** no es necesario que finalice para que termine la ejecucion de la carrera.
        Esperar que termine podria hacer al usuari esperar hasta 15 segundos innecesarios.*/

        sprinterThread.start();

        List<Hole> holes = holeGenerator(numberOfHorses);
        List<Horse> horses = horseGenerator( numberOfHorses, sprinter, holes);
        reportRaceStart(horses, holes);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
      /*   ExecutorService executor;
        if (horseNumber > availableProcessors) {
            executor = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            executor = Executors.newFixedThreadPool(horseNumber);
        }
        for (Horse horse: horses) {
            executor.submit(horse);
        }
        executor.shutdown();*/
       List<Thread> threads= new ArrayList<>();
        /**
         * se considera 1 thread para sprinter y luego de alcanzar los procesadores
         * se utilizan platform threads, en caso de no alcanzar, se utilizan virtual threads
         */
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
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        reportResults(horses);
        sprinterThread.interrupt();

    }


    public static List<Horse> horseGenerator(int numberOfHorses, Sprinter sprinter, List<Hole> holes) {
        List<Horse> horses = new ArrayList<>();
        for (int i = 0; i < numberOfHorses; i++) {
            String name = "Caballo " + (i + 1);
            int speed = random.nextInt(3) + 1;
            int stamina = random.nextInt(3) + 1;
            horses.add(new Horse(name, speed, stamina, semaphore, sprinter, holes));
        }
        return horses;
    }
    public static List<Hole> holeGenerator(int numberOfHorses) {
        List<Hole> holes = new ArrayList<>();
        for (int i = 0; i < Math.min(NUMBER_OF_HOLES, numberOfHorses-3); i++) {
            int position = random.nextInt(RACE_LENGTH - 10);

            holes.add(new Hole(position));
        }
        return holes;
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

    private static void reportRaceStart(List<Horse> horses, List<Hole> holes){
        for (Horse horse: horses) {
            System.out.println(horse.toString());
        }
        for (Hole hole: holes) {
            System.out.println(hole.toString());
        }
    }

}
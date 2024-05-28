package race;

import horse.Horse;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import static utils.Constants.RACE_LENGTH;

public class Sprinter implements Runnable {
    private int position;
    private final ReentrantLock lock = new ReentrantLock();
    private static final Random random = new Random();

    public Sprinter(){
        newPosition();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(15000);
                newPosition();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void newPosition() {
        position = random.nextInt(RACE_LENGTH - 50);
        System.out.println("Nueva posición del potenciador: " + position + " metros.");
    }

    public void trySprinter(Horse horse) {

        if (( horse.getTotalAdvancedDistance()  >= position) && (horse.getTotalAdvancedDistance() <= position + 50)) {
            if (lock.tryLock()) {
                try {
                    Thread.sleep(7000);
                    horse.setTotalAdvancedDistance(horse.getTotalAdvancedDistance() + 100);
                    System.out.println(horse.getName() + " usó el potenciador y ahora está en " + horse.getTotalAdvancedDistance() + " metros.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

            }
        }

    }



}

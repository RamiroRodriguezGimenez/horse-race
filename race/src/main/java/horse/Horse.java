package horse;

import java.util.Random;
import java.util.concurrent.Semaphore;


import static utils.Constants.*;

public class Horse implements Runnable{

    private final String name;
    private final int speed;
    private final int stamina;
    private int totalAdvancedDistance;
    private static final int RACE_LENGTH = 100;
    private final Semaphore semaphore;
    private  int position = 0;
    private static int currentPosition = 1;
    //currentPosition es static, por lo tanto la comparten todas las innstancias de Horse,
    //position es una por cada instancia
    private static final Random random = new Random();


    public Horse (String name, int speed, int stamina, Semaphore semaphore){
        this.name=name;
        this.speed=speed;
        this.stamina = stamina;
        this.semaphore = semaphore;
        totalAdvancedDistance = 0;
    }

    @Override
    public void run() {
        try{
            while (currentPosition<=3) {
                advance();
                if (totalAdvancedDistance >= RACE_LENGTH) {
                    if (semaphore.tryAcquire()) {
                        synchronized (Horse.class) {
                            System.out.println(name + " ha terminado la carrera en la posición: " + currentPosition);
                            position=currentPosition;
                            currentPosition++;
                        }
                    }
                    break;
                }
                waitStage();
            }

        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    private void advance() {
        int stepAdvancedDistance = (random.nextInt(ADVANCE_SEGMENT) + 1) * speed;
        synchronized (this) {
            totalAdvancedDistance = totalAdvancedDistance + stepAdvancedDistance;
        }
        System.out.println(name + ": avanza " + stepAdvancedDistance + "m para lograr un total de " + totalAdvancedDistance);
    }

    private void waitStage() throws InterruptedException {
        int waitTime = Math.max(0,(random.nextInt(WAIT_SEGMENT) +1) - stamina);
        Thread.sleep(waitTime * 1000);
    }

    public int getTotalAdvancedDistance(){
        return totalAdvancedDistance;
    }

    public String getName(){
        return name;
    }
    public int getPosition(){return position;}
}

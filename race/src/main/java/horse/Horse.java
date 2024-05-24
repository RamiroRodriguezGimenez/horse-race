package horse;

import java.util.Random;


import static utils.Constants.*;

public class Horse implements Runnable{

    private final String name;
    private final int speed;
    private final int stamina;
    private int totalAdvancedDistance;
    private static final Random random = new Random();

    public Horse (String name, int speed, int stamina){
        this.name=name;
        this.speed=speed;
        this.stamina = stamina;
        totalAdvancedDistance = 0;
    }

    @Override
    public void run() {
        try{
            System.out.println("hola");
            do {
                advance();
                waitStage();
            } while(totalAdvancedDistance <= 1000);
        }catch (Exception e){
        }
    }
    private void advance() {
        int stepAdvancedDistance = (random.nextInt(ADVANCE_SEGMENT) + 1) * speed;
        synchronized (this) {
            totalAdvancedDistance = totalAdvancedDistance + stepAdvancedDistance;
        }
        System.out.println(name + ":" + "avanza: " + stepAdvancedDistance + "para lograr un total de :" + totalAdvancedDistance);
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
}

package horse;

import java.util.Random;

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

    }
    private void advance() {
        int stepAdvancedDistance = (random.nextInt(10) + 1) * speed;
        synchronized (this) {
            totalAdvancedDistance = totalAdvancedDistance + stepAdvancedDistance;
        }
        System.out.println(name + ":" + "avanza: " + stepAdvancedDistance + "para lograr un total de :" + totalAdvancedDistance);
    }

    private void waitStage() throws InterruptedException {
        int waitTime = Math.max(0,(random.nextInt(5) +1) - stamina);
        Thread.sleep(waitTime * 1000);
    }

    public int getTotalAdvancedDistance(){
        return totalAdvancedDistance;
    }

    public String getName(){
        return name;
    }
}

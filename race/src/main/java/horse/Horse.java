package horse;


import race.Hole;
import race.Sprinter;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


import static utils.Constants.*;

public class Horse implements Runnable{

    private final String name;
    private final int speed;
    private final int stamina;
    private volatile int totalAdvancedDistance;
    private volatile boolean inHole;
    private int nextHoleIndex;
    private final Semaphore semaphore;
    private final Sprinter sprinter;
    private Hole nextHole;
    private final List<Hole> holes;
    private int position = 0;
    private volatile static int currentPosition = 1;
    /**currentPosition es static, por lo tanto la comparten todas las innstancias de Horse,
    position es una por cada instancia*/
    private static final Random random = new Random();
    public static final Object lock = new Object();



    public Horse (String name, int speed, int stamina, Semaphore semaphore, Sprinter sprinter, List<Hole> holes){
        this.name = name;
        this.speed = speed;
        this.stamina = stamina;
        this.semaphore = semaphore;
        totalAdvancedDistance = 0;
        this.sprinter = sprinter;
        inHole = false;
        nextHoleIndex = 0;
        this.holes = holes;
        nextHole = holes.get(nextHoleIndex);

    }

    @Override
    public void run() {
        try{
            while (currentPosition<=3) {
                advance();
                sprinter.trySprinter(this);
                if (totalAdvancedDistance >= RACE_LENGTH) {
                    if (semaphore.tryAcquire()) {
                        /**se bloquea Horse para evitar consumo de currentPosition de mas de un thread*/
                        synchronized (Horse.class) {
                            System.out.println(name + " ha terminado la carrera en la posición: " + currentPosition);
                            position=currentPosition;
                            currentPosition++;
                            if (currentPosition == 4){
                                rescueAll();
                            }
                            break;
                        }
                    }
                    break;
                }
                verifyHole();
                waitStage();
            }
            System.out.println(getName() + " finalizo con: "+ totalAdvancedDistance + " m.");
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    private void advance() {
        int stepAdvancedDistance = (random.nextInt(ADVANCE_SEGMENT) + 1) * speed;
        synchronized (this) {
            totalAdvancedDistance = totalAdvancedDistance + stepAdvancedDistance;
        }
        System.out.println(name + ": avanza " + stepAdvancedDistance + " m para lograr un total de " + totalAdvancedDistance + " m.");
    }

    private void waitStage() throws InterruptedException {
        int waitTime = Math.max(0,(random.nextInt(WAIT_SEGMENT) +1) - stamina);
        Thread.sleep(waitTime * 1000);
    }

    /**se verifica si se cae en el proximo pozo, de ser asi, se verifica que no este ocupado
     * si esta ocupado, intena rescatar al caballo  (si es que lo ha pasado, se podria dar el caso
     * que estando en el mismo pozo no lo haya sobrepasado todavia).
     * Luego de intetarCaer/rescatar si ha pasado el pozo, cambia el valor de proximoPozo al siguiente en la lista.
     *
     * Si cae al pozo se utiliza lock.wait() para esperar a notify o natifyAll, se usa lock para
     * poder acceder a ese objeto como owner desde otro thread.
     *
     * para rescue, se utiliza lock.notify() desde el thread del caballo que esta intentando rescatar al otro
     *
     * La consigna no aclara si pueden caer dos caballos en el mismop pozo, se considero que no.
     */
    public void verifyHole() throws InterruptedException {
        if (totalAdvancedDistance >= nextHole.getPosition()){
            if ( totalAdvancedDistance <= nextHole.getPosition() + 10){
                if(nextHole.tryFallIntoAHole(this)) {
                    synchronized (this) {
                        wait();
                    }
                }else {
                    nextHole.rescue(this);
                }
            } else {
                if (nextHoleIndex+1 < holes.size()){
                    nextHoleIndex++;
                    nextHole = holes.get(nextHoleIndex);
                }
            }
        }

    }

    private void rescueAll(){
        for (Hole hole: holes) {
            if (hole.getHorse() != null){
                synchronized (hole.getHorse()){
                    hole.getHorse().notify();
                    hole.setFree(false);
                }

            }
        }
    }

    public int getTotalAdvancedDistance(){
        return totalAdvancedDistance;
    }
    public void setTotalAdvancedDistance(int totalAdvancedDistance){
        this.totalAdvancedDistance = totalAdvancedDistance;
    }

    public String getName(){
        return name;
    }
    public int getPosition(){return position;}

    public boolean isInHole() {
        return inHole;
    }

    public void setInHole(boolean inHole) {
        this.inHole = inHole;
    }

    @Override
    public String toString() {
        return  name +
                ", velocidad=" + speed +
                ", resistencia=" + stamina;
    }
}

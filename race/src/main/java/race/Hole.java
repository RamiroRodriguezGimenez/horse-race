package race;

import horse.Horse;

import java.util.concurrent.locks.ReentrantLock;

public class Hole {
    private final int position;
    private boolean free;
    private final ReentrantLock lock = new ReentrantLock();
    private Horse horse;

    public Hole (int position){
        this.position = position;
        free = true;
        horse = null;
    }

    public int getPosition(){
        return position;
    }

    public boolean  tryFallIntoAHole(Horse horse) {
        if (free && lock.tryLock()){
            try{

                System.out.println(horse.getName() + " cayo en el pozo en " + position + " metros.");
                free = false;
                this.horse = horse;
                horse.setInHole(true);
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }
    public synchronized void rescue(Horse horse){
        if ((this.horse != null) && (horse.getTotalAdvancedDistance() >= this.horse.getTotalAdvancedDistance())){
            free = true;
            synchronized (this.horse) {
                this.horse.notify();
            }
            System.out.println(horse.getName() + " rescata a " + this.horse.getName());
            this.horse = null;
        }
    }

    public Horse getHorse() {
        return horse;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    @Override
    public String toString() {
        return "Pozo a los: " + position + " metros.";
    }
}


package myos.manager.device;

import myos.manager.memory.PCB;

import java.util.concurrent.TimeUnit;

public class B extends Device{
    public B(int count) {
        super(count);
        this.name="B";
    }
}

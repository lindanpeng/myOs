package myos.manager.device;

import myos.manager.memory.PCB;

import java.util.concurrent.TimeUnit;

public class C extends Device {
    public C(int count) {
        super(count);
        this.name="C";
    }
}

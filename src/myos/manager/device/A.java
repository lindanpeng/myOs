package myos.manager.device;

import myos.manager.memory.PCB;

import java.util.concurrent.TimeUnit;

public class A extends Device{

    public A(int count) {
        super(count);
        this.name="A";
    }
}

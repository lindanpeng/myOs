package myos.manager.device;

import myos.manager.memory.Memory;
import myos.manager.memory.PCB;

import java.util.Map;
import java.util.Set;

/**
 * Created by lindanpeng on 2017/12/24.
 */
public class CPU implements Runnable{
    //寄存器组
    private int IR;
    private int AX;
    private int BX;
    private int CX;
    private int DX;
    private int PC;
    private Memory memory;
    public CPU(Memory memory){
        this.memory=memory;
    }

    /**
     * 取值
     */
    public void fetchInstruction(){

    }

    /**
     * 译码
     */
    public void identifyInstruction(){

    }

    /**
     * 执行
     */
    public void execute(){

    }
    @Override
    public void run() {

    }
}


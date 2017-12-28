package myos.manager.process;

import myos.manager.memory.Memory;
import myos.manager.memory.PCB;
import myos.manager.process.Clock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lindanpeng on 2017/12/24.
 */
public class CPU implements Runnable{
    static ReentrantLock lock=new ReentrantLock();
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
        //TODO 越界处理
//        PCB runningPCB=memory.getRunningPCB();
//         PC=runningPCB.getMemStart()+runningPCB.getCounter();
//        runningPCB.setCounter(runningPCB.getCounter()+1);
        byte[] userArea=memory.getUserArea();
        IR=userArea[PC];
        PC++;
        System.out.println("取指完成");
    }

    /**
     * 译码
     */
    public void identifyInstruction(){
        System.out.println("译码完成");


    }

    /**
     * 执行和写回
     */
    public void execute(){
        //TODO 如果是end指令就行进程调度
        System.out.println("执行完成");

    }
    @Override
    public void run() {
        while(true) {
            lock.lock();
            try {
                fetchInstruction();
                identifyInstruction();
                execute();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

            try {
                Thread.sleep(Clock.TIMESLICE_UNIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
    }
    /**
     * 进程调度
     */
    public void dispatch(){
        PCB pcb1,pcb2;
        pcb1=memory.getRunningPCB();
        if (memory.getWaitPCB().size()==0) {
            pcb2=memory.getHangOutPCB();
            if (memory.getRunningPCB()!=memory.getWaitPCB()){
                memory.getWaitPCB().offer(pcb1);

            }
            memory.setRunningPCB(memory.getHangOutPCB());

        }
        else{
            memory.getWaitPCB().offer(pcb1);
            pcb2=memory.getWaitPCB().poll();
            memory.setRunningPCB(pcb2);

        }
        //保存与恢复现场
        CPU.lock.lock();
        try {
            //保存现场
            if (pcb1!=memory.getHangOutPCB()){
                pcb1.setStatus(PCB.STATUS_WAIT);
                pcb1.setCounter(this.getPC());
                pcb1.setAX(this.getAX());
                pcb1.setBX(this.getBX());
                pcb1.setCX(this.getCX());
                pcb1.setDX(this.getDX());
            }
            //恢复现场
            if (pcb2!=memory.getHangOutPCB()){
                this.setAX(pcb2.getAX());
                this.setBX(pcb2.getBX());
                this.setCX(pcb2.getDX());
                this.setDX(pcb2.getDX());
                this.setPC(pcb2.getCounter());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CPU.lock.unlock();
        }
    }


    public int getIR() {
        return IR;
    }

    public void setIR(int IR) {
        this.IR = IR;
    }

    public int getAX() {
        return AX;
    }

    public void setAX(int AX) {
        this.AX = AX;
    }

    public int getBX() {
        return BX;
    }

    public void setBX(int BX) {
        this.BX = BX;
    }

    public int getCX() {
        return CX;
    }

    public void setCX(int CX) {
        this.CX = CX;
    }

    public int getDX() {
        return DX;
    }

    public void setDX(int DX) {
        this.DX = DX;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }
}


package myos.manager.process;

import myos.manager.device.DeviceManager;
import myos.manager.memory.Memory;
import myos.manager.memory.PCB;
import myos.manager.memory.SubArea;
import myos.manager.process.Clock;

import javax.crypto.spec.RC2ParameterSpec;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lindanpeng on 2017/12/24.
 */
public class CPU implements Runnable {
    static ReentrantLock lock = new ReentrantLock();
    //寄存器组
    private int IR;
    private int AX;
    private int BX;
    private int CX;
    private int DX;
    private int PC;
    private Memory memory;
    private DeviceManager deviceManager;
    public CPU(Memory memory) {
        this.memory = memory;
        deviceManager=new DeviceManager(this);
        init();
    }

    /**
     * 初始化CPU
     */
    public void init(){
        IR=0;
        AX=0;
        BX=0;
        CX=0;
        DX=0;
        PC=0;
    }
    /**
     * 取值
     */
    public void fetchInstruction() {
        byte[] userArea = memory.getUserArea();
        IR = userArea[PC];
        PC++;
        System.out.println("取指完成，开始运行指令"+IR);
    }

    /**
     * 译码
     */
    public void identifyInstruction() {
        System.out.println("译码完成");


    }

    /**
     * 执行和写回
     */
    public void execute() {
        //TODO 如果是end指令就进程调度
        System.out.println("指令"+IR+"运行完毕");

    }
    /**
     * 进程调度
     */
    public void dispatch() {
        lock.lock();
        try {
        PCB pcb1= memory.getRunningPCB();;//当前运行的进程
        PCB pcb2=memory.getWaitPCB().poll();//要运行的进程

            if (pcb1!=memory.getHangOutPCB()){
                memory.getWaitPCB().offer(pcb1);
            }
            if (pcb2==null){
                pcb2=memory.getHangOutPCB();
            }
        memory.setRunningPCB(pcb2);
            //保存现场
            if (pcb1 != memory.getHangOutPCB()) {
              saveContext(pcb1);
            }
            //恢复现场
            if (pcb2 != memory.getHangOutPCB()) {
               recoveryContext(pcb2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CPU.lock.unlock();
        }
       // System.out.println("进程调度完成");
    }


    /**
     * 进程撤销
     * @param pcb
     */
    public void destory(PCB pcb){
        /*回收进程所占内存*/
        SubArea subArea=null;
        ListIterator<SubArea> it=memory.getSubAreas().listIterator();
        while(it.hasNext()){
            SubArea s=it.next();
            if (s.getTaskNo()==pcb.getPID()) {
                subArea = s;
                break;
            }
        }
        subArea.setStatus(SubArea.STATUS_FREE);
        //如果有前一个块
        if (it.hasPrevious()){
            SubArea pre=it.previous();
            // 且前一个块是空闲块，则合并
            if(pre.getStatus()==SubArea.STATUS_FREE){
                pre.setSize(pre.getSize()+subArea.getSize());
                //移除掉PCB对应的块
                it.next();
                it.remove();
                it.previous();
                subArea=pre;
            }
        }
        //如果有后一个块
        if (it.hasNext()){
            SubArea next=it.next();
            //且后一个块是空闲块，则合并
            if (next.getStatus()==SubArea.STATUS_FREE){
                subArea.setSize(subArea.getSize()+next.getSize());
                it.remove();
            }
        }
        //TODO 进程控制块回收以及显示结果在CPU处理


    }

    /**
     * 进程阻塞
     */
    public void block(PCB pcb){
        //保存运行进程的CPU现场
        saveContext(pcb);
        //修改进程状态
        pcb.setStatus(PCB.STATUS_BLOCK);
        //将进程链入对应的阻塞队列，然后转向进程调度
        memory.getBlockPCB().add(pcb);
        dispatch();
    }

    /**
     * 进程唤醒
     */
    public void awake(PCB pcb){
        //将进程从阻塞队列中调入到就绪队列
        pcb.setStatus(PCB.STATUS_WAIT);
        pcb.setEvent(PCB.EVENT_NOTING);
        memory.getBlockPCB().remove(pcb);
        memory.getWaitPCB().add(pcb);
    }

    /**
     * 保存上下文
     * @param pcb
     */
    private void  saveContext(PCB pcb){
        System.out.println("保留现场");
        pcb.setStatus(PCB.STATUS_WAIT);
        pcb.setCounter(this.getPC());
        pcb.setAX(this.getAX());
        pcb.setBX(this.getBX());
        pcb.setCX(this.getCX());
        pcb.setDX(this.getDX());
    }

    /**
     * 恢复现场
     */
    private void recoveryContext(PCB pcb){
        System.out.println("恢复现场");
        pcb.setStatus(PCB.STATUS_RUN);
        this.setAX(pcb.getAX());
        this.setBX(pcb.getBX());
        this.setCX(pcb.getDX());
        this.setDX(pcb.getDX());
        this.setPC(pcb.getCounter());
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                fetchInstruction();
                identifyInstruction();
                execute();
              //  System.out.println("就绪队列队头进程："+memory.getWaitPCB().peek().getPID());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            try {
                Thread.sleep(Clock.TIMESLICE_UNIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


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


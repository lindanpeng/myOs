package myos.manager.memory;

import myos.constant.OsConstant;

import java.util.*;

/**
 * Created by lindanpeng on 2017/12/12.
 * 内存模块
 */
public class Memory {
    //内存分配表
    private List<SubArea> subAreas;
    //所有进程
    private List<PCB> allPCB;
/*    //空闲进程控制块
    private Queue<PCB> freePCB;*/
    //就绪进程控制块
    private Queue<PCB> waitPCB;
    //阻塞进程控制块
    private Queue<PCB> blockPCB;
    //运行进程
    private PCB runningPCB;
    //闲逛进程
    private PCB hangOutPCB;
    //用户区内存
    private byte[] userArea;
    public Memory() {
        subAreas = new LinkedList<>();
        SubArea subArea = new SubArea();
        subArea.setSize(OsConstant.USER_AREA_SIZE);
        subArea.setStartAdd(0);
        subArea.setStatus(SubArea.STATUS_FREE);
        subAreas.add(subArea);
       // freePCB = new LinkedList<>();
        waitPCB =  new LinkedList<>();
        blockPCB = new LinkedList<>();
        hangOutPCB=new PCB();
        hangOutPCB.setStatus(PCB.STATUS_RUN);
        runningPCB=hangOutPCB;
        userArea = new byte[OsConstant.USER_AREA_SIZE];
    }

    public List<SubArea> getSubAreas() {
        return subAreas;
    }

    public void setSubAreas(List<SubArea> subAreas) {
        this.subAreas = subAreas;
    }


    public Queue<PCB> getWaitPCB() {
        return waitPCB;
    }

    public void setWaitPCB(Queue<PCB> waitPCB) {
        this.waitPCB = waitPCB;
    }

    public Queue<PCB> getBlockPCB() {
        return blockPCB;
    }

    public void setBlockPCB(Queue<PCB> blockPCB) {
        this.blockPCB = blockPCB;
    }

    public byte[] getUserArea() {
        return userArea;
    }

    public void setUserArea(byte[] userArea) {
        this.userArea = userArea;
    }

    public PCB getRunningPCB() {
        return runningPCB;
    }

    public void setRunningPCB(PCB runningPCB) {
        this.runningPCB = runningPCB;
    }

    public PCB getHangOutPCB() {
        return hangOutPCB;
    }

    public List<PCB> getAllPCB() {
        List<PCB> allPCB=new ArrayList<>(10);
        allPCB.add(runningPCB);
        allPCB.addAll(blockPCB);
        allPCB.addAll(waitPCB);
        return allPCB;
    }

    public void setAllPCB(List<PCB> allPCB) {
        this.allPCB = allPCB;
    }

}

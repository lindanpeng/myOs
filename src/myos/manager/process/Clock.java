package myos.manager.process;


import myos.OS;

/**
 * 系统时钟
 */
public class Clock implements Runnable {

    //时间片长度
    private static final long TIMESLICE_LENGTH=6;
    //时间片单位(毫秒)
    public static final long TIMESLICE_UNIT=1000;
    //系统时钟
    private  long systemTime;
    //当前进程剩下的运行时间
    private long restTime;
    private CPU cpu;
    public Clock(){
        this.cpu= OS.cpu;
        init();
    }

    /**
     * 初始化时钟
     */
    public void init(){
        systemTime=0;
        restTime=TIMESLICE_LENGTH;
    }
    @Override
    public void run() {
        while(OS.launched) {
            try {
                Thread.sleep(TIMESLICE_UNIT);
                systemTime+=TIMESLICE_UNIT/1000;
                restTime=(restTime+TIMESLICE_LENGTH-TIMESLICE_UNIT/1000)%TIMESLICE_LENGTH;
                //时间片到了
                if (restTime==0){
              //      System.out.println("时间片用完了");
                    cpu.lock.lock();
                    cpu.toReady();
                    cpu.dispatch();
                    cpu.lock.unlock();
                }

            } catch (InterruptedException e) {
                return;
            }


        }

    }

    public long getSystemTime() {
        return systemTime;
    }

    public long getRestTime() {
        return restTime;
    }
}

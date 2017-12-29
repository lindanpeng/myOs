package myos.manager.process;



/**
 * 系统时钟
 */
public class Clock implements Runnable {

    //时间片长度
    private static final long TIMESLICE_LENGTH=6000;
    //时间片单位(毫秒)
    public static final long TIMESLICE_UNIT=1000;
    //系统时钟
    private  long systemTime;
    //当前进程剩下的运行时间
    private long restTime;
    private CPU cpu;
    public Clock(CPU cpu ){
        this.cpu=cpu;
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
        while(true) {
            try {
                Thread.sleep(TIMESLICE_UNIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            systemTime+=TIMESLICE_UNIT;
            restTime=(restTime+TIMESLICE_LENGTH-TIMESLICE_UNIT)%TIMESLICE_LENGTH;
            //时间片到了
            if (restTime==0){
                System.out.println("时间片用完了");
                cpu.dispatch();
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

package cn.info.wuyu.javabackend.scheduletask.base;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by lindanpeng on 2017/10/20.
 */
public class DelayItem<T> implements Delayed {
    //时限长度
    private long workTime;
    //到期时间
    private long expireTime;
    //执行对象
    protected   T obj;
    public DelayItem(T obj,long workTime,TimeUnit timeUnit){
        this.obj=obj;
        this.workTime=workTime;
        //将时间单位转成纳米
        this.expireTime=TimeUnit.NANOSECONDS.convert(workTime,timeUnit)+System.nanoTime();
    }
    /**
     * 返回与此对象相关的剩余延迟时间，以给定的时间单位表示
     */
    @Override
    public long getDelay(TimeUnit unit) {
         return unit.convert(this.expireTime - System.nanoTime() , TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if(o == null || ! (o instanceof DelayItem)) return 1;
        if(o == this) return 0;
        DelayItem delayItem=(DelayItem) o;
        if (this.expireTime>delayItem.expireTime)
            return 1;
        else if (this.expireTime==delayItem.expireTime)
            return 0;
        else
            return -1;

    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}

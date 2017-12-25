package myos.manager.memory;

/**
 * Created by lindanpeng on 2017/12/24.
 */
 public class SubArea {
    public  static final int STATUS_FREE=0;//分区空闲
    public  static final int STATUS_BUSY=1;//分区被使用
     //开始地址
     private int startAdd;
     //分区大小
     private int size;
     //分区状态
    private int status;
    //作业号
    private int taskNo;

    public int getStartAdd() {
        return startAdd;
    }

    public void setStartAdd(int startAdd) {
        this.startAdd = startAdd;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(int taskNo) {
        this.taskNo = taskNo;
    }
}

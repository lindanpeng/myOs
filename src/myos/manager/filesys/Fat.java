package myos.manager.filesys;

import myos.constant.OsConstant;

/**
 * Created by lindanpeng on 2017/10/15.
 * 文件分配表
 */
public class Fat {
    //内存
    private byte[] memory;
    //文件分配表在内存中的起始位置
    private int startPos=0;
    //文件分配表占用大小
    private int length=OsConstant.PHYSICAL_BLOCK_QUNTITY;
    //文件分配表项数目
    private int fatItemSize = OsConstant.PHYSICAL_BLOCK_QUNTITY;

    public Fat(byte[] memory) {
        //指向系统内存
        this.memory = memory;
    }
   public int firstFreeBlock(int start){
        for(int i=start;i<startPos+length;i++){
            if (isFree(i)){
                return i;
            }
        }
        return -1;
   }
    /**
     * 判断磁盘块是否可用
     *
     * @param index
     * @return
     */
    private boolean isFree(int index) {
        //TODO 判断是否越界
        return memory[index] == 0;
    }

    /**
     * 判断文件是否在该磁盘块结束
     *
     * @param index
     * @return
     */
    public boolean isEnd(int index) {
        return memory[index] == -1;
    }

    /**
     * 获取下一磁盘块的位置
     *
     * @param index
     * @return
     */
    public int nextItem(int index) {
        return memory[index];
    }

    /**
     * 判断磁盘块是否损毁
     */
    public boolean isDestroyed(int index) {
        return memory[index] == 128;
    }

}

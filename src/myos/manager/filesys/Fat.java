package myos.manager.filesys;

import myos.constant.OsConstant;

/**
 * Created by lindanpeng on 2017/10/15.
 * 文件分配表
 */
public class Fat {
    //文件分配表内存块
  private byte[] bytes;
  //文件分配表项数目
  private int fatItemSize=OsConstant.PHYSICAL_BLOCK_SIZE;
  public Fat(byte[] bytes){
      //指向系统内存
      this.bytes=bytes;
  }

    /**
     * 判断磁盘块是否可用
     * @param index
     * @return
     */
  public boolean isAvailable(int index){
      //TODO 判断是否越界
      return bytes[index]==0;
  }

    /**
     * 判断文件是否在该磁盘块结束
     * @param index
     * @return
     */
  public boolean isEnd(int index){
      return bytes[index]==-1;
  }

    /**
     * 获取下一磁盘块的位置
     * @param index
     * @return
     */
  public int nextItem(int index){
      return bytes[index];
  }
    /**
     * 判断磁盘块是否损毁
     */
    public boolean isDestroyed(int index){
        return bytes[index]==128;
    }

}

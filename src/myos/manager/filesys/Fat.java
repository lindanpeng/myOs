package myos.manager.filesys;

import myos.constant.OsConstant;

import java.io.RandomAccessFile;

/**
 * Created by lindanpeng on 2017/12/21.
 */
public class Fat {
    private RandomAccessFile disk;
    private int size= OsConstant.DISK_BLOCK_QUNTITY;
    public Fat(RandomAccessFile disk){
        this.disk=disk;
    }

    public int size(){
        return size;
    }

}

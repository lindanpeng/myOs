package myos.manager.file;

/**
 * Created by lindanpeng on 2017/10/15.
 * 文件分配表
 */
public class Fat {
    private static int DISK_BLOCK_QUANTITY=128;//磁盘块数
    private static int DISK_BLOCK_SIZE=64;//物理块大小
    private DiskBlock[] diskBlocks=new DiskBlock[DISK_BLOCK_QUANTITY];

    /**
     * 磁盘块类
     */
    class DiskBlock{
        private int index;
        private int content;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getContent() {
            return content;
        }

        public void setContent(int content) {
            this.content = content;
        }
    }

}

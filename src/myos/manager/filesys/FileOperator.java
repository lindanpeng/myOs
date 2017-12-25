package myos.manager.filesys;

import myos.constant.OsConstant;
import myos.manager.process.ProcessOperator;
import myos.pojo.SplitFilePath;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件操作类
 * Created by lindanpeng on 2017/12/6.
 */
public class FileOperator {
    //系统内存
    //private byte[] memory;
    //文件分配表起始位置
//    private int fatStartPos=0;
//    //文件分配表结束位置
//    private int fatEndPos= OsConstant.DISK_BLOCK_QUNTITY -1;
    //已打开文件项
    List<OpenedFile> openedFiles;
    //文件分配表
    List<Byte> fat;
    //磁盘文件
    RandomAccessFile disk;
    //进程操作
    ProcessOperator processOperator;
    public FileOperator(RandomAccessFile disk,ProcessOperator processOperator) {
        this.processOperator=processOperator;
        this.fat = fat;
        this.disk = disk;
        this.openedFiles = new ArrayList<>();
    }

    /**
     * 建立文件
     *
     * @param filePath 文件路径名
     * @param property 文件属性
     */
    public void create(String filePath, int property) throws Exception {

        int newFilePos = firstFreeBlock();
        if (newFilePos == -1)
            throw new Exception("内存不足");
        SplitFilePath splitFilePath = splitPathAndFileName(filePath);
        int parentCatalogBlockPos = getCatalogBlock(splitFilePath.getPath(), 2 );//找到该文件父目录所在磁盘块
        Catalog parentDir=readCatalog(parentCatalogBlockPos);
        //查找该文件夹下是否有同名目录
        if (existsFile(splitFilePath.getFileName(),parentDir.getStartBlock()))
            throw new Exception("已经存在同名目录，请先删除");
        //将该文件夹的起始磁盘块设置为新文件的磁盘块
        if (parentDir.getStartBlock()==-1){
            parentDir.setStartBlock(newFilePos);
            disk.seek(parentCatalogBlockPos*OsConstant.DISK_BLOCK_SIZE);
            disk.write(parentDir.getBytes(),0,parentDir.getBytes().length);
        }
        //将该文件夹的最后一个文件的磁盘块设置为新文件的磁盘块位置
        else{
            int last= getLastBlock(parentDir.getStartBlock());
            disk.seek(last);
            disk.writeByte(newFilePos);
        }
        Catalog newFile=new Catalog(splitFilePath.getFileName(),property);
        disk.seek(newFilePos);
        disk.writeByte(-1);//修改文件分配表
        disk.seek(newFilePos*OsConstant.DISK_BLOCK_SIZE);
        disk.write(newFile.getBytes(),0,newFile.getBytes().length);//将目录项写入磁盘
        System.out.println("建立文件成功");

    }

    /**
     * 打开文件
     *
     * @param filePath 文件名
     * @param opType   操作类型(读或写)
     */
    public OpenedFile open(String filePath, int opType) throws Exception {
        int catalogBlockPos=-1;
        try{
            catalogBlockPos= getCatalogBlock(filePath,2);
            System.out.println("文件目录项所在磁盘块："+catalogBlockPos);
        }catch (Exception e){
            throw  new Exception("没有找到文件！");
        }

        Catalog catalog=readCatalog(catalogBlockPos);
        catalog.setCatalogBlock(catalogBlockPos);
        OpenedFile openedFile=new OpenedFile();
        openedFile.setOpType(opType);
        openedFile.setFilePath(filePath);
        openedFile.setCatalog(catalog);
        Pointer readPointer=new Pointer();
        readPointer.setBlockNo(catalog.getStartBlock());
        readPointer.setAddress(0);
        Pointer writePointer=new Pointer();
        writePointer.setBlockNo(catalog.getStartBlock());
        writePointer.setAddress(0);
        openedFile.setReadPointer(readPointer);
        openedFile.setWritePointer(writePointer);
        openedFiles.add(openedFile);
        //如果是可执行程序，则创建进程
        if (catalog.getProperty()>>4==1)
        processOperator.create(catalog.getBytes());
        return openedFile;
    }

    /**
     * 读取文件
     *
     * @param filePath 文件路径名
     * @param length   要读取的字节数,-1表示读取所有
     */
    public byte[] read(String filePath, int length) throws Exception {
       OpenedFile file=getOpenedFile(filePath);
       return read(file,length);
    }

    /**
     * 写文件
     *
     * @param filePath 文件路径名
     * @param buffer   要写入的缓冲区数据
     * @param length   数据的长度
     */
    public void write(String filePath, byte[] buffer, int length) throws Exception {
        OpenedFile openedFile=getOpenedFile(filePath);
        write(openedFile,buffer,length);

    }

    /**
     * 以追加的方式写入
     * @param filePath
     * @param buffer
     * @param length
     */
    public void append(String filePath,byte[] buffer,int length) throws Exception {
        OpenedFile openedFile=getOpenedFile(filePath);
        append(openedFile,buffer,length);
    }
    /**
     * 关闭文件
     *
     * @param filePath 文件名
     */
    public void close(String filePath) throws Exception {
        OpenedFile openedFile=getOpenedFile(filePath);
        if (!openedFiles.contains(openedFile))
            throw new Exception("文件未打开！");
        //由于本程序的写操作默认在结尾追加#号，所以不需要追加文件结束符
//        if (openedFile.getOpType()==OpenedFile.OP_TYPE_WRITE){
//            byte[] bytes=new byte[1];
//            bytes[0]='#';
//            append(openedFile,bytes,1);
//        }

        openedFiles.remove(openedFile);

    }


    /**
     * 删除文件
     *
     * @param filePath 文件名
     */
    public void delete(String filePath) throws Exception {
        OpenedFile openedFile=null;
        try {
          openedFile=getOpenedFile(filePath);
        }catch (Exception e){
           System.out.println("文件未打开，可以删除");
        }
      if (openedFile!=null)
          throw new Exception("该文件已经被打开，不能删除");
        //找到文件目录所在磁盘块
        int blockPos=getCatalogBlock(filePath,2);
        System.out.println("文件目录项所在磁盘块："+blockPos);
        Catalog catalog=readCatalog(blockPos);
        int nextBlock=catalog.getStartBlock();
        int pre;
        //清空文件内容(分配表)
        while(nextBlock!=-1){
            pre=nextBlock;
            nextBlock=getNextBlock(pre);
            setNextBlock(pre,0);

        }
        //修改目录指针
        //如果是父文件夹的第一个目录，则修改父文件夹的开始盘块，
        //否则将上一个目录的指针指向下一个目录
        SplitFilePath splitFilePath=splitPathAndFileName(filePath);
        int parentPos=getCatalogBlock(splitFilePath.getPath(),2);
        Catalog parentDir=readCatalog(parentPos);
        if (parentDir.getStartBlock()==blockPos) {
            parentDir.setStartBlock(getNextBlock(blockPos));
            writeCatalog(parentDir);
        }
        else {
            nextBlock=parentDir.getStartBlock();
            pre=nextBlock;
            while (nextBlock != blockPos){
                pre=nextBlock;
                nextBlock=getNextBlock(pre);
            }
            setNextBlock(pre,getNextBlock(blockPos));
        }


        //删除目录项
        setNextBlock(blockPos,0);

        System.out.println("删除文件成功");
    }

    /**
     * 显示文件
     *
     * @param filePath 文件名
     */
    public String show(String filePath) throws Exception {
        byte[] content=read(filePath,-1);
        return new String(content);
    }

    /**
     * 改变文件属性
     *
     * @param filePath
     * @param newProperty
     */
    public void changeProperty(String filePath, int newProperty) throws Exception {
            int catalogBlock=getCatalogBlock(filePath,2);
            Catalog catalog=readCatalog(catalogBlock);
            catalog.setProperty(newProperty);
            writeCatalog(catalog);
    }

    /**
     * 建立目录
     *
     * @param dirPath 目录路径
     */
    public void md(String dirPath) throws Exception {
        create(dirPath,8);
    }

    /**
     * 显示目录的内容
     *
     * @param dirPath
     */
    public List<Catalog> dir(String dirPath) throws Exception {
        int catalogBlock=getCatalogBlock(dirPath,2);
        List<Catalog> catalogs=new ArrayList<>();
        Catalog catalog=readCatalog(catalogBlock);
        int nextBlock=catalog.getStartBlock();
        while(nextBlock!=-1){
            Catalog c=readCatalog(nextBlock);
            catalogs.add(c);
            nextBlock=getNextBlock(nextBlock);
        }
        return  catalogs;
    }

    /**
     * 删除空目录
     *
     * @param dirPath 目录路径
     */
    public void rd(String dirPath) {

    }
    /**
     * 读取已打开文件
     * @param openedFile
     * @param length
     * @return
     */
    private byte[] read(OpenedFile openedFile,int length) throws Exception {
        if (openedFile.getOpType()!=OpenedFile.OP_TYPE_READ)
            throw new Exception("文件不处于读模式,不能读取");
        int readByte=0;
        //1.文件内容不够长
        //2.遇到结束符
        //3.跨越磁盘块
        byte[] buffer=new byte[1024];
        Pointer p=openedFile.getReadPointer();
        byte temp;
        while(p.getBlockNo()!=-1&&readByte!=length){
            //读完一个磁盘块
            if (p.getAddress()==OsConstant.DISK_BLOCK_SIZE) {
                p.setBlockNo(getNextBlock(p.getBlockNo()));
                p.setAddress(0);
            }
            disk.seek(p.getBlockNo()*OsConstant.DISK_BLOCK_SIZE+p.getAddress());
            temp=disk.readByte();
            //遇到结束符停止读取
            if (temp=='#')
                break;
            buffer[readByte]=temp;
            p.setAddress(p.getAddress()+1);
            readByte++;
        }
        byte[] content=Arrays.copyOf(buffer,readByte);
        return content;
    }
    /**
     * 写入已打开文件中
     * @param openedFile
     * @param buffer
     * @param length
     * @throws Exception
     */
    private void write(OpenedFile openedFile,byte[] buffer,int length) throws Exception {
        if (openedFile.getOpType()!=OpenedFile.OP_TYPE_WRITE)
            throw new Exception("文件不处于写模式,不能写入");
        Pointer pointer=openedFile.getWritePointer();
        Catalog catalog=openedFile.getCatalog();
        int writtenBytes=0;
        while(writtenBytes!=length){
            if (pointer.getAddress()==OsConstant.DISK_BLOCK_SIZE){
                pointer.setBlockNo(getNextBlock(pointer.getBlockNo()));
            }
            if (pointer.getBlockNo()==-1){
                //申请空间,注意修改文件分配表,修改文件长度
                int blockNo=firstFreeBlock();
                if (blockNo==-1)
                    throw new Exception("磁盘空间不足！");
                //之前是空文件，不占用磁盘空间
                if (catalog.getStartBlock()==-1){
                    catalog.setStartBlock(blockNo);
                }
                int last= getLastBlock(catalog.getStartBlock());
                //修改文件分配表
                setNextBlock(last,blockNo);
                setNextBlock(blockNo,-1);
                //修改写指针位置
                pointer.setBlockNo(blockNo);
                pointer.setAddress(0);
                //修改文件长度
                catalog.setFileLength(catalog.getFileLength()+1);
                writeCatalog(catalog);
            }
            disk.seek(pointer.getBlockNo()*OsConstant.DISK_BLOCK_SIZE+pointer.getAddress());
            disk.write(buffer[writtenBytes++]);
            pointer.setAddress(pointer.getAddress()+1);
        }
        //写入结束符
            disk.write('#');
    }
    private void append(OpenedFile openedFile,byte[] butter,int length ) throws Exception {
        Pointer p=openedFile.getWritePointer();
        //给空文件分配空间
        if (openedFile.getCatalog().getStartBlock()==-1){
            int block=firstFreeBlock();
            setNextBlock(block,-1);
            openedFile.getCatalog().setStartBlock(block);
            openedFile.getCatalog().setFileLength(1);
            writeCatalog(openedFile.getCatalog());
            disk.seek(block*OsConstant.DISK_BLOCK_SIZE);
            disk.write('#');
        }
        p.setBlockNo(getLastBlock(openedFile.getCatalog().getStartBlock()));
        disk.seek(p.getBlockNo()*OsConstant.DISK_BLOCK_SIZE);
        byte b;
        int i=0;
        while((b=disk.readByte())!='#')
            i++;
        p.setAddress(i);
        write(openedFile,butter,length);

    }
    /**
     * 查找目录所在磁盘块号
     * @param filePath
     * @param startBlockPos 从哪一个盘块开始搜索根目录
     */
    private int getCatalogBlock(String filePath, int startBlockPos) throws Exception {

        int index = filePath.indexOf('/');
        String rootName,sonPath;
        //该路径最上层的目录名
        if (index!=-1) {
             rootName = filePath.substring(0, index);
            //子路径
             sonPath = filePath.substring(index + 1);
        }else{
            rootName=filePath;
            sonPath="";
        }
        byte nextBlock=(byte) startBlockPos;
        do{
            Catalog catalog=readCatalog(nextBlock);
            if (catalog.getName().equals(rootName)) {
                if (sonPath.equals(""))
                    return nextBlock;
                if (!catalog.isDirectory())
                    throw new Exception(catalog.getName()+" 不是文件夹");
               return  getCatalogBlock(sonPath, catalog.getStartBlock());
            }
            disk.seek(nextBlock);
            nextBlock=disk.readByte();
        } while(nextBlock!=-1);

            throw new Exception("未找到文件夹"+rootName);
    }

    /**
     * 找到目录内容所在的最后一个磁盘块
     *
     * @param i
     * @return
     */
    private int getLastBlock(int i) throws IOException {
      int nextBlock=getNextBlock(i);
       if (nextBlock!=-1)
           return getLastBlock(nextBlock);
       return i;
    }

    /**
     * 查找第一个可用磁盘块
     *
     * @return
     */
    private int firstFreeBlock() throws IOException {
        int nextBlock;
        for (int i = 3; i < OsConstant.DISK_BLOCK_QUNTITY; i++) {
            //0表示可用
           nextBlock=getNextBlock(i);
            if (nextBlock == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取分配表指向的下一个磁盘块号
     * @param i
     * @return
     * @throws IOException
     */
    private int getNextBlock(int i) throws IOException {
        disk.seek(i);
        return disk.readByte();
    }

    /**
     * 修改文件分配表指向的下一个磁盘块
     * @param i
     * @param nextBlock
     * @throws IOException
     */
    private void setNextBlock(int i,int nextBlock) throws IOException {
        disk.seek(i);
        disk.writeByte(nextBlock);
    }
    /**
     * 将文件路径与文件名分割开来
     *
     * @param filePath
     * @return
     */
    private SplitFilePath splitPathAndFileName(String filePath) {
        int fileNameStartIndex = filePath.lastIndexOf('/');
        String fileName = filePath.substring(fileNameStartIndex + 1);//单独的文件名
        String path = filePath.substring(0, fileNameStartIndex);//提取路径
        SplitFilePath splitFilePath = new SplitFilePath();
        splitFilePath.setFileName(fileName);
        splitFilePath.setPath(path);
        return splitFilePath;
    }

    /**
     * 获取文件对应的打开文件项
     * @param filePath
     * @return
     * @throws Exception
     */
    private OpenedFile getOpenedFile(String filePath) throws Exception {
        OpenedFile file=null;
        for(OpenedFile openedFile:openedFiles){
            if (openedFile.getFilePath().equals(filePath)) {
                file = openedFile;
                break;
            }
        }
        if (file==null)
            throw  new Exception("文件未打开！");
        return file;
    }

    /**
     * 写入目录
     * @param catalog
     * @throws IOException
     */
    private void writeCatalog(Catalog catalog) throws IOException {
        disk.seek(catalog.getCatalogBlock()*OsConstant.DISK_BLOCK_SIZE);
        disk.write(catalog.getBytes(),0,catalog.getBytes().length);
    }

    /**
     * 读取目录项
     * @param blockPos
     * @return
     * @throws IOException
     */
    private Catalog readCatalog(int blockPos) throws IOException {
        disk.seek(blockPos*OsConstant.DISK_BLOCK_SIZE);
        byte[] buffer=new byte[8];
        disk.read(buffer,0,buffer.length);
        Catalog catalog=new Catalog(buffer);
        catalog.setCatalogBlock(blockPos);
        return catalog;
    }

    /**
     * 从开始磁盘块查找，判断是否存在同名目录
     * @param fileName
     * @param startBlock
     * @return
     * @throws IOException
     */
    private boolean existsFile(String fileName,int startBlock) throws IOException {
        int nextBlock=startBlock;
        while(nextBlock!=-1){
            Catalog catalog=readCatalog(nextBlock);
            if (catalog.getName().equals(fileName))
                return true;
            nextBlock=getNextBlock(nextBlock);
        }
        return false;
    }
}

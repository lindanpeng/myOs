package myos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import myos.OS;
import myos.constant.UIResources;
import myos.manager.device.A;
import myos.manager.filesys.Catalog;
import myos.manager.filesys.OpenedFile;
import myos.manager.memory.PCB;
import myos.manager.process.Clock;
import javafx.scene.control.cell.PropertyValueFactory;
import myos.others.ThreadPoolUtil;
import myos.ui.MyTreeItem;
import myos.ui.PCBVo;
import myos.manager.filesys.FileOperator;

import java.io.IOException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by lindanpeng on 2017/12/29.
 */
public class MainController implements Initializable {
    @FXML
    private GridPane fatView;
    @FXML
    private Button startBtn;
    @FXML
    private TreeView<Catalog> catalogTreeView;
    @FXML
    private Text systemTimeTxt;
    @FXML
    private Text timesliceTxt;
    @FXML
    private TextArea cmdView;
    @FXML
    private TextArea processRunningView;
    @FXML
    private TextArea processResultView;
    @FXML
    private TableView<PCBVo> pcbQueueView;
    @FXML
    private TableColumn pidCol;
    @FXML
    private TableColumn statusCol;
    @FXML
    private TableColumn eventCol;
    @FXML
    private TableColumn priorityCol;
    private OS os;
    private boolean launched=false;
    private UpdateUIThread updateUIThread;
    public MainController() throws Exception {
        os = new OS(this);
        updateUIThread=new UpdateUIThread();
    }

    /**
     * 初始化组件
     * @throws Exception
     */
    public void initComponent() throws Exception {
      //初始化进程队列视图
        initPcbQueueView();
        //初始化目录树
        initCatalogTree();
        //初始化磁盘分配表视图
        updateFatView();

    }
    /*-------------------响应用户请求------------------------*/
    /**
     * 启动系统
     */
    public void launchOS() throws Exception {
        if (!launched) {
            os.start();
            launched=true;
            startBtn.setText("关闭系统");
            ThreadPoolUtil.execute(updateUIThread);
            initComponent();
           new Thread(()-> {
                   try {

                    //  os.fileOperator.create("rt/abc",8);
                     //  os.fileOperator.create("rt/a",4);
                      Thread.sleep(2000);
                   //    os.fileOperator.create("rt/e",8);
                    //   os.fileOperator.create("rt/e/k",4);
                       Thread.sleep(5000);
                      os.fileOperator.rmdir("rt/e");

                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }


           }).start();
        }else{
            closeOS();
            launched=false;
            startBtn.setText("启动系统");
            ThreadPoolUtil.shutdown();
        }

    }
    public void excuteCMD(KeyEvent event) throws  Exception{
            if(event.getCode() == KeyCode.ENTER)
            {
                String str  = cmdView.getText();
                String command = str.substring(0,str.length()-1).replaceAll("\\s{1,}"," ");//去除多余的空格和换行符
                cmdView.clear();
                String[] instruction = command.split(" ");
                if(instruction.length>1) {
                    System.out.println(instruction[0] + " "+instruction[1]);
                    if(instruction[0].contains("create")) {
                        os.fileOperator.create(instruction[1],4);
                    }else if(instruction[0].contains("delete")){
                        os.fileOperator.delete(instruction[1]);
                    }else if(instruction[0].contains("type")){

                    }else if(instruction[0].contains("copy")&&instruction.length==3){

                    }else if(instruction[0].contains("mkdir")){

                    }else if(instruction[0].contains("rmdir")){

                    }else if(instruction[0].contains("change")&&instruction.length==3){
                        int newProperty = Integer.valueOf(instruction[2]).intValue();
                        os.fileOperator.changeProperty(instruction[1],newProperty);
                    }else {
                        return;
                    }
                }else
                {
                    return;
                }
            }
    }
    public void closeOS(){
        os.close();
    }
    /*---------------------后台主动刷新---------------------------------*/
    /**
     * 构建目录树
     */
    public void initCatalogTree() throws Exception {
        Catalog root=os.fileOperator.readCatalog(2);
        TreeItem<Catalog> treeItem=new MyTreeItem(root);
        catalogTreeView.setRoot(treeItem);
        catalogTreeView.setCellFactory(new Callback<TreeView<Catalog>, TreeCell<Catalog>>() {
            public TreeCell<Catalog> call(TreeView<Catalog> param) {
                return new TreeCell<Catalog>(){

                    @Override
                    protected void updateItem(Catalog catalog,boolean empty){
                        super.updateItem(catalog,empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(catalog.getName());
                            if (catalog.isDirectory()){
                                setGraphic(UIResources.getDirectoryIcon());
                            }
                            else if (catalog.isExecutable()){
                                setGraphic(UIResources.getProgramIcon());
                            }
                            else {
                                setGraphic(UIResources.getFileIcon());
                            }
                        }
                    }
                };
            }
        });
      //  catalogTreeView.setCellFactory((TreeView<Catalog> p)->new MyTreeCell());
        catalogTreeView.refresh();
    }

    /**
     * 初始化进程队列视图
     */
    public void initPcbQueueView(){
        pidCol.setCellValueFactory(new PropertyValueFactory<>("PID"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
    }

    /**
     * 添加树节点
     * @param parent
     * @param newCatalog
     */
    public void addTreeItem(Catalog parent,Catalog newCatalog){
        TreeItem<Catalog> root= catalogTreeView.getRoot();
        TreeItem<Catalog> parentTreeItem=findTreeItem(root,parent);
        parentTreeItem.getChildren().add(new TreeItem<>(newCatalog));
        catalogTreeView.refresh();
    }
    /**
     * 删除树节点
     * @param catalog
     */
    public void removeTreeItem(Catalog catalog){
        TreeItem<Catalog> root= catalogTreeView.getRoot();
        TreeItem<Catalog> treeItem=findTreeItem(root,catalog);
        //节点视图如果已经被加载
        if (treeItem!=null) {
            treeItem.getParent().getChildren().remove(treeItem);
            catalogTreeView.refresh();
        }
    }

    /**
     * 更新树节点
     * @param catalog
     */
    public void updateTreeItem(Catalog catalog){
        TreeItem<Catalog> root= catalogTreeView.getRoot();
        TreeItem<Catalog> treeItem=findTreeItem(root,catalog);
        if (treeItem!=null) {
            treeItem.setValue(catalog);
            catalogTreeView.refresh();
        }
    }

    /**
     * 从root节点开始查找节点
     * @param catalog
     */
    public  TreeItem<Catalog> findTreeItem(TreeItem<Catalog> root,Catalog catalog){
        if (root.getValue().equals(catalog)){
            return root;
        }

        if (root.isLeaf())
            return null;
        for (TreeItem<Catalog> catalogTreeItem:root.getChildren()){
          TreeItem t= findTreeItem(catalogTreeItem,catalog);
          if (t!=null)
              return t;
        }
        return null;
    }
    /**
     * 更新磁盘使用情况
     */
    public void updateFatView() throws IOException {
        byte[] fat=os.fileOperator.getFat();
        for (int i=0;i<fat.length;i++){
            Pane pane = (Pane) fatView.getChildren().get(i);
            if (fat[i]!=0) {
                pane.setStyle("-fx-background-color: red");
            }
            else {
                pane.setStyle("-fx-background-color:coral");
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    private class UpdateUIThread implements Runnable{
        @Override
        public void run(){
            while(!Thread.currentThread().isInterrupted()){
                try {
                MainController.this.systemTimeTxt.setText(OS.clock.getSystemTime()+"");
                MainController.this.timesliceTxt.setText(OS.clock.getRestTime()+"");
                    List<PCB> pcbs=os.memory.getAllPCB();
                    List<PCBVo> pcbVos=new ArrayList<>(pcbs.size());
                    for (PCB pcb:pcbs){
                        PCBVo pcbVo=new PCBVo(pcb);
                        pcbVos.add(pcbVo);
                    }
                    ObservableList<PCBVo> datas= FXCollections.observableList(pcbVos);
                    pcbQueueView.setItems(datas);
                    Thread.sleep(Clock.TIMESLICE_UNIT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package myos.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import myos.Main;
import myos.OS;
import myos.constant.UIResources;
import myos.manager.filesys.Catalog;
import myos.manager.filesys.OpenedFile;
import myos.manager.memory.PCB;
import myos.manager.memory.SubArea;
import myos.manager.process.Clock;
import myos.others.ThreadPoolUtil;
import myos.ui.MyTreeItem;
import myos.ui.PCBVo;

import java.io.IOException;
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
    @FXML
    private HBox userAreaView;
    private OS os;
    private boolean launched = false;
    private UpdateUIThread updateUIThread;

    public MainController() throws Exception {
        os = new OS(this);
        updateUIThread = new UpdateUIThread();
    }

    /**
     * 初始化组件
     *
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
            launched = true;
            startBtn.setText("关闭系统");
            initComponent();
            ThreadPoolUtil.execute(updateUIThread);

            String[][] instruction ={{"mov ax,50","inc ax","mov bx,111","dec bx","mov cx,23","! a 1","end"},
            {"mov ax,50","mov dx,30","mov bx,111","dec bx","inc dx","mov ax 255","! a 1","end"},
            {"mov ax,50","! b 2","mov bx,111","! c 1","mov cx,23","! a 1","end"},
            {"mov ax,50","inc ax","! b 1","! a 2","mov cx,23","inc cx","inc ax","! c 2","end"},
            {"mov bx,70","inc bx","mov bx,12","dec bx","! c 3","inc bx","mov cx,23","! a 1","dec cx","end"},
            {"mov ax,50","! b 2","mov bx,12","! c 1","mov cx,23","! a 1","mov ax,50","inc ax","mov bx,221","dec bx","mov cx,23","! a 1","end"},
            {"mov ax,50","inc ax","! b 1","! a 2","mov cx,23","inc ax","mov bx,122","dec bx","mov cx,232","! a 1","end"},
            {"mov ax,50","inc ax","! c 1","mov cx,23","! a 1","mov bx,111","dec bx","mov cx,20","! a 1","end"},
            {"mov ax,50","inc ax","mov bx,13","dec bx","mov cx,23","! a 1","inc ax","mov bx,189","dec bx","mov cx,23","! a 1","end"},
            {"mov ax,50","inc ax","mov bx,156","inc ax","mov bx,111","dec bx","mov cx,23","! a 1","dec bx","mov cx,23","! b 1","end"}};


//           new Thread(()-> {
//                   try {
            for(int i=0;i<instruction.length;i++) {
                String path = "rt/"+String.valueOf(i)+"e";
                os.fileOperator.create(path, 16);
                os.fileOperator.open(path, OpenedFile.OP_TYPE_WRITE);
                byte[] b = getInstruction(instruction[i]);
                os.fileOperator.append(path, b, b.length);
                os.fileOperator.close(path);
//
                os.fileOperator.run(path);
            }
//                   } catch (InterruptedException e) {
//                       e.printStackTrace();
//                   } catch (Exception e) {
//                       e.printStackTrace();
//                   }
//           }).start();
        } else {
            closeOS();
            launched = false;
            startBtn.setText("启动系统");
            ThreadPoolUtil.shutdown();
        }
    }
    public byte[] getInstruction(String[] instruction)
    {
        ArrayList<Byte> ins=new ArrayList<>();
        for(int i=0;i<instruction.length;i++)
        {
            String[] str=instruction[i].split("[\\s|,]");
            byte first;
            byte second =(byte)0;
            if(str.length>1) {
                if (str[1].contains("a"))
                    second = 0;
                else if (str[1].contains("b")) {
                    second = 4;
                } else if (str[1].contains("b")) {
                    second = 8;
                } else {
                    second = 12;
                }
            }
            if(str[0].contains("mov")) {
                first = (byte)80;
                ins.add((byte)(first+second));
                ins.add(Byte.valueOf(str[2]));
            }else if(str[0].contains("inc")){
                first = (byte)16;
                ins.add((byte)(first+second));
            }else if(str[0].contains("dec")){
                first = (byte)32;
                ins.add((byte)(first+second));
            }else if(str[0].contains("!")){
                first = (byte)48;
                ins.add((byte)(first+second+Byte.valueOf(str[2])));
            }else if(str[0].contains("end")){
                ins.add((byte)64);
            }
        }
        byte[] instruct= new byte[ins.size()];
        for(int i=0;i<instruct.length;i++)
        {
            instruct[i] = ins.get(i);
        }
        return  instruct;
    }
    public void executeCMD(KeyEvent event) throws Exception {

            if(event.getCode() == KeyCode.ENTER)
            {
                String[] str  = cmdView.getText().split("\\n");
                String s = str[str.length-1];
                String[] instruction = s.trim().split("\\s+");
                if(instruction.length>1) {
                    System.out.println(instruction[0] + " "+instruction[1]);
                    try{
                        if(instruction[0].contains("create")) {
                            os.fileOperator.create(instruction[1],4);
                            cmdView.appendText("-> 创建文件成功\n");
                        }else if(instruction[0].contains("delete")){
                            os.fileOperator.delete(instruction[1]);
                            cmdView.appendText("-> 删除文件成功\n");
                        }else if(instruction[0].contains("type")){
                            String content = os.fileOperator.type(instruction[1]);
                            cmdView.appendText(content+"\n");
                        }else if(instruction[0].contains("copy")&&instruction.length==3){

                        }else if(instruction[0].contains("mkdir")){
                            os.fileOperator.mkdir(instruction[1]);
                            cmdView.appendText("-> 创建目录成功\n");
                        }else if(instruction[0].contains("rmdir")){
                            os.fileOperator.rmdir(instruction[1]);
                            cmdView.appendText("-> 删除目录成功\n");
                        }else if(instruction[0].contains("change")&&instruction.length==3){
                            int newProperty = Integer.valueOf(instruction[2]).intValue();
                            os.fileOperator.changeProperty(instruction[1],newProperty);
                            cmdView.appendText("-> 修改文件属性成功\n");
                        }else if(instruction[0].contains("run")){
                            os.fileOperator.run(instruction[1]);
                        }else
                        {
                            cmdView.appendText("-> 指令不存在\n");
                            return;
                        }
                    }catch (Exception ex){
                        String[] exception = ex.toString().split(":");
                        cmdView.appendText("-> "+exception[exception.length-1].trim()+"\n");
                    }
                }else
                {
                    cmdView.appendText("-> 请按正确格式输入指令\n");
                    return;
                }
            }
    }

    public void closeOS() {
        os.close();
    }
    /*---------------------后台主动刷新---------------------------------*/

    /**
     * 构建目录树
     */
    public void initCatalogTree() throws Exception {
        Catalog root = os.fileOperator.readCatalog(2);
        TreeItem<Catalog> treeItem = new MyTreeItem(root);
        catalogTreeView.setRoot(treeItem);
        catalogTreeView.setCellFactory(new Callback<TreeView<Catalog>, TreeCell<Catalog>>() {
            public TreeCell<Catalog> call(TreeView<Catalog> param) {
                return new TreeCell<Catalog>() {

                    @Override
                    protected void updateItem(Catalog catalog, boolean empty) {
                        super.updateItem(catalog, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(catalog.getName());
                            if (catalog.isDirectory()) {
                                setGraphic(UIResources.getDirectoryIcon());
                            } else if (catalog.isExecutable()) {
                                setGraphic(UIResources.getProgramIcon());
                            } else {
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
    public void initPcbQueueView() {
        pidCol.setCellValueFactory(new PropertyValueFactory<>("PID"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
    }

    /**
     * 添加树节点
     *
     * @param parent
     * @param newCatalog
     */
    public void addTreeItem(Catalog parent, Catalog newCatalog) {
        TreeItem<Catalog> root = catalogTreeView.getRoot();
        TreeItem<Catalog> parentTreeItem = findTreeItem(root, parent);
        parentTreeItem.getChildren().add(new TreeItem<>(newCatalog));
        catalogTreeView.refresh();
    }

    /**
     * 删除树节点
     *
     * @param catalog
     */
    public void removeTreeItem(Catalog catalog) {
        TreeItem<Catalog> root = catalogTreeView.getRoot();
        TreeItem<Catalog> treeItem = findTreeItem(root, catalog);
        //节点视图如果已经被加载
        if (treeItem != null) {
            treeItem.getParent().getChildren().remove(treeItem);
            catalogTreeView.refresh();
        }
    }

    /**
     * 更新树节点
     *
     * @param catalog
     */
    public void updateTreeItem(Catalog catalog) {
        TreeItem<Catalog> root = catalogTreeView.getRoot();
        TreeItem<Catalog> treeItem = findTreeItem(root, catalog);
        if (treeItem != null) {
            treeItem.setValue(catalog);
            catalogTreeView.refresh();
        }
    }

    /**
     * 从root节点开始查找节点
     *
     * @param catalog
     */
    public TreeItem<Catalog> findTreeItem(TreeItem<Catalog> root, Catalog catalog) {
        if (root.getValue().equals(catalog)) {
            return root;
        }

        if (root.isLeaf())
            return null;
        for (TreeItem<Catalog> catalogTreeItem : root.getChildren()) {
            TreeItem t = findTreeItem(catalogTreeItem, catalog);
            if (t != null)
                return t;
        }
        return null;
    }

    /**
     * 更新磁盘使用情况
     */
    public void updateFatView() throws IOException {
        byte[] fat = os.fileOperator.getFat();
        for (int i = 0; i < fat.length; i++) {
            Pane pane = (Pane) fatView.getChildren().get(i);
            if (fat[i] != 0) {
                pane.setStyle("-fx-background-color: red");
            } else {
                pane.setStyle("-fx-background-color:coral");
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private class UpdateUIThread implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //更新进程执行过程视图
                            MainController.this.processRunningView.appendText(os.cpu.getResult()+"\n");
                            //更新系统时钟视图
                            MainController.this.systemTimeTxt.setText(OS.clock.getSystemTime() + "");
                            //更新时间片视图
                            MainController.this.timesliceTxt.setText(OS.clock.getRestTime() + "");
                            //更新进程队列视图
                            List<PCB> pcbs = os.memory.getAllPCB();
                            List<PCBVo> pcbVos = new ArrayList<>(pcbs.size());
                            for (PCB pcb : pcbs) {
                                PCBVo pcbVo = new PCBVo(pcb);
                                pcbVos.add(pcbVo);
                            }
                            ObservableList<PCBVo> datas = FXCollections.observableList(pcbVos);
                            pcbQueueView.setItems(datas);
                            //更新用户区内存视图
                            userAreaView.getChildren().removeAll(userAreaView.getChildren());
                            List<SubArea> subAreas=os.memory.getSubAreas();
                            for (SubArea subArea:subAreas){
                                Pane pane=new Pane();
                                pane.setPrefHeight(40);
                                pane.setPrefWidth(subArea.getSize());
                                if (subArea.getStatus()==SubArea.STATUS_BUSY){
                                    pane.setStyle("-fx-background-color: orangered;-fx-border-color:black");
                                }
                                else{
                                    pane.setStyle("-fx-background-color:snow;-fx-border-color: black");
                                }

                                userAreaView.getChildren().add(pane);
                            }
                        }
                    });


                    Thread.sleep(Clock.TIMESLICE_UNIT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

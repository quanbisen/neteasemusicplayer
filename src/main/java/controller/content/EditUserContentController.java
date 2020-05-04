package controller.content;

import controller.main.LeftController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import mediaplayer.UserStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import pojo.User;
import service.UpdateUserService;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author super lollipop
 * @date 5/2/20
 */
@Controller
public class EditUserContentController {

    @FXML
    private Label labChoseImageTips;

    @FXML
    private ImageView ivImage;

    @FXML
    private TextField tfPetName;

    @FXML
    private Label labTips;

    @FXML
    private TextArea taDescription;

    @FXML
    private ComboBox<String> cbSex;

    @FXML
    private DatePicker dpBirthday;

    @FXML
    private ComboBox<String> cbProvince;

    @FXML
    private ComboBox<String> cbCity;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @Resource
    private LeftController leftController;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private UserStatus userStatus;

    private File choseImageFile;

    public Label getLabChoseImageTips() {
        return labChoseImageTips;
    }

    public TextField getTfPetName() {
        return tfPetName;
    }

    public TextArea getTaDescription() {
        return taDescription;
    }

    public ComboBox<String> getCbSex() {
        return cbSex;
    }

    public DatePicker getDpBirthday() {
        return dpBirthday;
    }

    public ComboBox<String> getCbProvince() {
        return cbProvince;
    }

    public ComboBox<String> getCbCity() {
        return cbCity;
    }

    public File getChoseImageFile() {
        return choseImageFile;
    }

    public void initialize(){

        if (choseImageFile != null){    //如果不为null，重置为null
            choseImageFile = null;
        }
        blockSaveButton();  //初始化保存按钮不可点击

        User user = userStatus.getUser();
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (user.getImageURL() != null){
                            Image image = new Image(user.getImageURL(),210,210,true,true);
                            if (!image.isError()){
                                ivImage.setImage(image);
                            }else if (user.getLocalImagePath() != null && new File(user.getLocalImagePath().substring(5)).exists()){
                                ivImage.setImage(new Image(user.getLocalImagePath(),210,210,true,true));
                            }else {
                                ivImage.setImage(new Image("image/DefaultAlbumImage_200.png",210,210,true,true));
                            }
                        }
                        return null;
                    }
                };
            }
        }.start();
        Platform.runLater(()->{
            btnSave.requestFocus(); //保存按钮请求聚焦
            tfPetName.textProperty().addListener(((observable, oldValue, newValue) -> {
                if (observable.getValue().trim().equals("")){
                    labTips.setText("呢称不能为空");
                    blockSaveButton();
                }else if (isInputChange(user)){
                    releaseSaveButton();
                }else {
                    blockSaveButton();
                }
            }));
            taDescription.textProperty().addListener(((observable, oldValue, newValue) -> {
                if (isInputChange(user)){
                    releaseSaveButton();
                }else {
                    blockSaveButton();
                }
                if (!taDescription.lookup(".scroll-bar:vertical").isDisable()){ //去除textArea的滚动条
                    taDescription.lookup(".scroll-bar:vertical").setDisable(true);
                }
            }));
            cbSex.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (isInputChange(user)){
                    releaseSaveButton();
                }else {
                    blockSaveButton();
                }
            });
            dpBirthday.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (isInputChange(user)){
                    releaseSaveButton();
                }else {
                    blockSaveButton();
                }
            });
            cbProvince.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (isInputChange(user)){
                    releaseSaveButton();
                }else {
                    blockSaveButton();
                }
            });
            cbCity.valueProperty().addListener(((observable, oldValue, newValue) -> {
                if (isInputChange(user)){
                    releaseSaveButton();
                }else {
                    blockSaveButton();
                }
            }));
        });

        tfPetName.setText(user.getName());  //设置显示呢称
        if (!StringUtils.isEmpty(user.getDescription())){   //如果描述信息不为空，设置显示描述信息
            taDescription.setText(user.getDescription());
        }
        cbSex.setValue(user.getSex());  //设置显示性别
        if (!StringUtils.isEmpty(user.getBirthday())){  //如果生日信息不为空，设置显示生日信息
            dpBirthday.setValue(LocalDateTime.ofInstant(user.getBirthday().toInstant(), ZoneId.systemDefault()).toLocalDate());
        }
        setupProvinceCity(user);    //省市的部分
    }

    private void blockSaveButton(){
        btnSave.setOpacity(0.8);
        btnSave.setMouseTransparent(true);
    }

    private void releaseSaveButton(){
        btnSave.setMouseTransparent(false);
        btnSave.setOpacity(1);
    }

    /**验证输入的用户信息是否改变了
     * @param user
     * @return boolean*/
    private boolean isInputChange(User user){
        Calendar calendar = Calendar.getInstance();
        LocalDate ldBirthday;
        if (user.getBirthday() == null){
            calendar.setTime(new Date(0));
            ldBirthday = LocalDateTime.ofInstant(new Date(0).toInstant(), ZoneId.systemDefault()).toLocalDate();
        }else {
            calendar.setTime(user.getBirthday());
            ldBirthday = dpBirthday.getValue();
        }
        if (tfPetName.getText().equals(user.getName() == null ? "" : user.getName())
                &&taDescription.getText().equals(user.getDescription() == null ? "" : user.getDescription())
                &&cbSex.getValue().equals(user.getSex())
                &&ldBirthday.getYear() == calendar.get(Calendar.YEAR)
                &&ldBirthday.getMonth().getValue() == calendar.get(Calendar.MONTH) + 1
                &&ldBirthday.getDayOfMonth() == calendar.get(Calendar.DAY_OF_MONTH)
                &&cbProvince.getValue().equals(user.getProvince() == null ? "" : user.getProvince())
                &&cbCity.getValue().equals(user.getCity() == null ? "" : user.getCity())
                &&choseImageFile == null
                ){
            return false;
        }else {
            return true;
        }
    }

    /**设置省市的函数
     * @param user 用户对象*/
    private void setupProvinceCity(User user) {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config/province-city.yaml");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Object object =yaml.load(bufferedReader);   //加载yaml文件
        ObservableList<String> provinceList = FXCollections.observableArrayList();
        provinceList.addAll(((Map)object).keySet());    //取出所有的省
        cbProvince.setItems(provinceList);  //设置下拉的内容items
        if (!StringUtils.isEmpty(user.getProvince())){  //如果用户的省信息不为空
            cbProvince.setValue(user.getProvince());    //显示
        }
        if (!StringUtils.isEmpty(user.getCity())){  //如果用户的市信息不为空
            cbCity.setValue(user.getCity());    //显示
        }
        cbProvince.valueProperty().addListener((observable, oldValue, newValue) -> {    //添加省份改变时的监听器，市的items需要改变
            System.out.println(observable.getValue());
            ObservableList<String> cityList = FXCollections.observableArrayList();
            cityList.addAll(((Map<String, ArrayList<String>>) object).get(observable.getValue()));
            cbCity.setItems(cityList);
            cbCity.setValue(cityList.get(0));   //默认的value为items的第一个
        });
    }

    /**用户头像图片的鼠标事件*/
    @FXML
    public void onClickedImage(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG","*.jpg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG","*.png"));
            choseImageFile = fileChooser.showOpenDialog(ivImage.getScene().getWindow());
            if (choseImageFile != null){
                if (choseImageFile.length() / 1024 / 1024 <= 1){
                    ivImage.setImage(new Image("file:" + choseImageFile.getPath(), 210,210,true,true));
                    btnSave.setMouseTransparent(false);
                    btnSave.setOpacity(1);
                }else {
                    labChoseImageTips.setText("图片文件大于1M");
                    choseImageFile = null;
                }
            }
        }
    }

    /**“保存”按钮的事件*/
    @FXML
    public void onClickedSave(ActionEvent actionEvent) {
        applicationContext.getBean(UpdateUserService.class).start();   //启动更新
        onClickedCancel(actionEvent);
    }

    /**“取消”按钮的事件*/
    @FXML
    public void onClickedCancel(ActionEvent actionEvent) {
        leftController.setSelectedTab(leftController.getSelectedTab());
        Event.fireEvent(leftController.getSelectedTab(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 5, true, true, true, true,
                true, true, true, true, true, true, null));     //fire选中的tab的鼠标事件
    }

}

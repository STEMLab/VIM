package lab.stem.vim;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coordispace.listener.StatusListener;
import com.coordispace.main.PermissionActivity;
import com.coordispace.main.ServiceManager;
import com.coordispace.main.Status;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import lab.stem.vim.core.LocationFile;
import lab.stem.vim.core.DestinationInfo;
import lab.stem.vim.core.VIMIndoorFeatures;
import lab.stem.vim.core.User;
import lab.stem.vim.core.VIMSTATE;
import lab.stem.vim.guideFactory.GuideInfo;
import lab.stem.vim.indoorGML.CellSpace;
import lab.stem.vim.indoorGML.IndoorGmlParser;
import lab.stem.vim.indoorGML.Point;
import lab.stem.vim.observer.VIMIndoorFeaturesObserver;

class MainInterface {
    private final Button instructions;
    private final Button currentLocation;
    private final Button startPositioning;
    private final Button resetRoute;
    private final Button selectDestination;
    private final Button start;
    private final Button stop;
    private final Button repeatGuide;
    private final Button nearbyPOI;
    private final Button setting;
    private final TextView destination;

    public MainInterface(Activity activity) {
        this.instructions = activity.findViewById(R.id.btn_instructions);
        this.currentLocation = activity.findViewById(R.id.btn_location);
        this.startPositioning = activity.findViewById(R.id.btn_positioning);
        this.resetRoute = activity.findViewById(R.id.btn_reset_route);
        this.selectDestination = activity.findViewById(R.id.btn_select_destination);
        this.start = activity.findViewById(R.id.btn_start);
        this.stop = activity.findViewById(R.id.btn_stop);
        this.repeatGuide = activity.findViewById(R.id.btn_repeat_guide);
        this.nearbyPOI = activity.findViewById(R.id.btn_nearby);
        this.setting = activity.findViewById(R.id.btn_setting);
        this.destination = activity.findViewById(R.id.text_destination);
    }
    public void setLanguage(String language) {
        if (language.equals("ko")) {
            this.instructions.setText(R.string.kr_instructions);
            this.currentLocation.setText(R.string.kr_current_location);
            this.startPositioning.setText(R.string.kr_start_positioning);
            this.resetRoute.setText(R.string.kr_reset_route);
            this.selectDestination.setText(R.string.kr_set_up_destination);
            this.start.setText(R.string.kr_start);
            this.stop.setText(R.string.kr_stop);
            this.repeatGuide.setText(R.string.kr_repeat_guide);
            this.nearbyPOI.setText(R.string.kr_nearby_poi);
            this.setting.setText(R.string.kr_setting);
            this.destination.setText("\n현재 목적지: 없음");
        } else {
            this.instructions.setText(R.string.instructions);
            this.currentLocation.setText(R.string.current_location);
            this.startPositioning.setText(R.string.start_positioning);
            this.resetRoute.setText(R.string.reset_route);
            this.selectDestination.setText(R.string.set_up_destination);
            this.start.setText(R.string.start);
            this.stop.setText(R.string.stop);
            this.repeatGuide.setText(R.string.repeat_guide);
            this.nearbyPOI.setText(R.string.nearby_poi);
            this.setting.setText(R.string.setting);
            this.destination.setText("\nCurrent Destination: Nothing");
        }
    }
    public void setDestination(String destination) {
        this.destination.setText(destination);
    }
    public void setPositioning(String language, boolean start) {
        boolean isKorean = language.equals("ko");
        if (!start) {
            this.startPositioning.setText(isKorean ? R.string.kr_start_positioning : R.string.start_positioning);
        } else {
            this.startPositioning.setText(isKorean ? R.string.kr_stop_positioning : R.string.stop_positioning);
        }
    }
}

class DebugInterface implements View.OnClickListener {
    private final MainActivity mainActivity;
    private final User user;
    private final ConstraintLayout layout_debug;
    private final LinearLayout layout_select_route;
    private final LinearLayout layout_information;
    private final EditText editTextX;
    private final EditText editTextY;
    private final EditText editTextZ;
    private final EditText editTextDIR;
    private final TextView text_selected_edit;
//    private final TextView text_debug;

    public DebugInterface(User user, MainActivity activity) {
        this.mainActivity = activity;
        this.user = user;
        this.layout_debug = mainActivity.findViewById(R.id.layout_debug);
        this.layout_information =  mainActivity.findViewById(R.id.layout_information);
        this.layout_select_route = mainActivity.findViewById(R.id.layout_select_route);
        this.editTextX = mainActivity.findViewById(R.id.editTextNumberX);
        this.editTextY = mainActivity.findViewById(R.id.editTextNumberY);
        this.editTextZ = mainActivity.findViewById(R.id.editTextNumberZ);
        this.editTextDIR = mainActivity.findViewById(R.id.editTextNumberDIR);
        this.text_selected_edit = mainActivity.findViewById(R.id.text_selected_edit);
        this.text_selected_edit.setText("X");
//        this.text_debug = mainActivity.findViewById(R.id.text_debug);

        setEventDebugComponents();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textViewX){
            text_selected_edit.setText(R.string.X);
        } else if (v.getId() == R.id.textViewY){
            text_selected_edit.setText(R.string.Y);
        } else if (v.getId() == R.id.textViewZ){
            text_selected_edit.setText(R.string.Z);
        } else if (v.getId() == R.id.textViewDIR){
            text_selected_edit.setText(R.string.DIR);
        }
    }
    private void setEventDebugComponents(){
        mainActivity.findViewById(R.id.textViewX).setOnClickListener(this);
        mainActivity.findViewById(R.id.textViewY).setOnClickListener(this);
        mainActivity.findViewById(R.id.textViewZ).setOnClickListener(this);
        mainActivity.findViewById(R.id.textViewDIR).setOnClickListener(this);
        mainActivity.findViewById(R.id.btn_change_mode).setOnClickListener(v -> changeMode());
        mainActivity.findViewById(R.id.btn_debug_mode).setOnClickListener(v -> changeDebugMode());
        mainActivity.findViewById(R.id.btn_debug_run).setOnClickListener(v -> mainActivity.executeNavigation(false, setUserPosition()));
        mainActivity.findViewById(R.id.btn_debug_plus).setOnClickListener(v -> {
            if (!text_selected_edit.getText().equals("none")) controlUser((String) text_selected_edit.getText(), true);
        });
        mainActivity.findViewById(R.id.btn_debug_minus).setOnClickListener(v -> {
            if(!text_selected_edit.getText().equals("none")) controlUser((String) text_selected_edit.getText(), false);
        });
    }
    public long setUserPosition() {
        double x = Double.parseDouble(editTextX.getText().toString());
        double y = Double.parseDouble(editTextY.getText().toString());
        double z = Double.parseDouble(editTextZ.getText().toString());
        double dir = Double.parseDouble(editTextDIR.getText().toString());
        /*double yawAngle = dir + 180.0 > 360.0 ? dir - 180.0 : dir + 180.0;
        yawAngle = yawAngle > 0.0 ? - yawAngle : 360.0 + yawAngle;*/
        long systemTime = System.currentTimeMillis();
        user.set(x, y, z, (float) dir,System.currentTimeMillis());
        mainActivity.executeNavigation(false, systemTime);
        return systemTime;
    }
    public void controlUser(String key, boolean isPlus){
        double param = isPlus ? 0.25 : -0.25;
        double currentValue;
        switch (key){
            case "X":
                currentValue = Double.parseDouble(editTextX.getText().toString())+param;
                editTextX.setText(String.format(Locale.KOREA,"%.2f", currentValue));
                break;
            case "Y":
                currentValue = Double.parseDouble(editTextY.getText().toString())+param;
                editTextY.setText(String.format(Locale.KOREA,"%.2f", currentValue));
                break;
            case "Z":
                currentValue = Double.parseDouble(editTextZ.getText().toString()) +param;
                editTextZ.setText(String.format(Locale.KOREA,"%.0f", currentValue));
                break;
            case "DIR":
                currentValue = Double.parseDouble(editTextDIR.getText().toString())+param *10;
                editTextDIR.setText(String.format(Locale.KOREA,"%.0f", currentValue));
                break;
        }
        setUserPosition();
    }
    public void setDebugPoint(Point point) {
        this.editTextX.setText(String.valueOf(point.getX()));
        this.editTextY.setText(String.valueOf(point.getY()));
        this.editTextZ.setText(String.valueOf(point.getZ()));
    }
    public void toggleDebug() {
        int VISIBLE = mainActivity.findViewById(R.id.btn_debug_mode).getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        if (VISIBLE != View.VISIBLE) turnOffDebug();
        mainActivity.findViewById(R.id.btn_debug_mode).setVisibility(VISIBLE);
        mainActivity.findViewById(R.id.btn_change_mode).setVisibility(VISIBLE);
    }
    private void changeMode(){
        Button btn_initialize = mainActivity.findViewById(R.id.btn_initialize);
        Button btn_calibration = mainActivity.findViewById(R.id.btn_calibration);
        if (layout_information.getVisibility() == View.GONE){
            layout_select_route.setVisibility(View.GONE);
            layout_information.setVisibility(View.VISIBLE);
            btn_initialize.setVisibility(View.VISIBLE);
            btn_calibration.setVisibility(View.VISIBLE);
        } else {
            btn_initialize.setVisibility(View.GONE);
            btn_calibration.setVisibility(View.GONE);
            layout_information.setVisibility(View.GONE);
            layout_select_route.setVisibility(View.VISIBLE);
        }
    }
    private void changeDebugMode(){
        if (layout_debug.getVisibility() == View.GONE){
            layout_select_route.setVisibility(View.GONE);
            layout_debug.setVisibility(View.VISIBLE);
        } else {
            layout_debug.setVisibility(View.GONE);
            layout_select_route.setVisibility(View.VISIBLE);
        }

    }
    private void turnOffDebug() {
        layout_debug.setVisibility(View.GONE);
        layout_information.setVisibility(View.GONE);
        layout_select_route.setVisibility(View.VISIBLE);
    }
}

public class MainActivity extends AppCompatActivity implements VIMIndoorFeaturesObserver {
    public static String TAG = "MainActivity";
    private boolean isRunningNavigation = false;

    // IPS ServiceManager Variables
    private ServiceManager mServiceManager;
    private Context context;
    private final int mRequestCode = 111;

    private int positioningStatus = -1;
    private int serviceNo = 0;

    // TTS
    private TextToSpeech tts;

    private TextView text_positioning_status;
    private TextView text_information;
    private TextView text_destination;

    private MainInterface mainInterface;
    private DebugInterface debugInterface;

    private LocationFile locationFile;
    private lab.stem.vim.core.VIMIndoorFeatures VIMIndoorFeatures;
    private User user;
    private long systemTime;
    private boolean beepGoSound;

    private SharedPreferences sharedPreferences;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        try {
            ActionBar actionBar = getSupportActionBar();
            Objects.requireNonNull(actionBar);
            actionBar.hide();
        } catch (NullPointerException e) {
            Log.e(TAG, "ActionBar: ", e);
        }

        turnOnBlueTooth();
        getStoragePermission();
        initParams();
        initComponent();
        setEventOnComponents();
        initTTS();
//        constructMap(209);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (this.mainInterface != null){
            this.language = sharedPreferences.getString("language",Locale.getDefault().getLanguage());
            this.mainInterface.setLanguage(language);
            this.mainInterface.setPositioning(language, !(positioningStatus < 0));
            if (user != null && user.getDestinationInfo() != null){
                this.mainInterface.setDestination(language.equals("ko")
                        ? "\n현재 목적지: "+user.getDestinationInfo().getName(language)
                        : "\ncurrent destination: "+user.getDestinationInfo().getName(language));
            }
            if (this.tts != null) {
                this.tts.setSpeechRate(sharedPreferences.getFloat("guideSpeed", 2.0f));
                if (language.equals("ko")) this.tts.setLanguage(Locale.KOREA);
                else this.tts.setLanguage(Locale.US);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(positioningStatus > 0) mServiceManager.stopService();
        tts.stop();
    }

    @Override
    public void updateVIMState(VIMSTATE vimState) {
        user.updateVIMState(vimState);
    }

    private void turnOnBlueTooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent;

        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            // 블루투스 관련 실행 진행
            Log.d("TAG", "onCreate: bluetooth on ");
        } else {
            // 블루투스 활성화 하도록
            Log.d("TAG", "onCreate: bluetooth off ");

            intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        }
    }

    private void getStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
    }

    private void initParams() {
        this.context = this;
        this.user = new User();
        Status.isVoiceService = true;
        this.beepGoSound = true;
        this.sharedPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        this.language = sharedPreferences.getString("language",Locale.getDefault().getLanguage());
        this.mainInterface = new MainInterface(this);
        this.debugInterface = new DebugInterface(this.user,this);
    }

    private void initComponent() {
        text_positioning_status = findViewById(R.id.text_positioning_status);
        text_information = findViewById(R.id.text_information);
        text_destination = findViewById(R.id.text_destination);
        mainInterface.setLanguage(language);

        Intent intent = getIntent();
        String destination = intent.getStringExtra("destination");
        if (destination != null) text_destination.setText(destination);
    }

    public void setEventOnComponents() {
        findViewById(R.id.btn_instructions).setOnTouchListener(new View.OnTouchListener() {
            long then = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    then = System.currentTimeMillis();
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    if((System.currentTimeMillis() - then) > 1200){
                        debugInterface.toggleDebug();
                        return true;
                    }
                }
                return false;
            }
        });
        findViewById(R.id.btn_instructions).setOnClickListener(v -> {
            if (tts.isSpeaking()) tts.stop();
            else {
                String message =
                        language.equals("ko")
                                ? "실내 길 찾기 서비스를 실행하기 위한 방법 안내입니다\n" +
                                "첫째 측위 시작 버튼을 눌러 측위 시스템을 구동 시켜주세요\n" +
                                "둘째 목적지 설정 버튼을 눌러 원하시는 목적지를 설정 해주세요\n" +
                                "셋째 시작 버튼을 눌러 길안내 서비스를 실행 해주세요\n" +
                                "넷째 음성 안내를 듣고 길을 찾아 가세요\n" +
                                "주의 스마트폰 진동 및 점형블록에서는 잠시 멈추어 진행해주세요\n"
                                : "This is indoor navigation service announcement\n" +
                                "First press the START POSITIONING button to run the positioning system\n" +
                                "Second set up the destination what you want to go\n" +
                                "Third press the Start Button to execute indoor navigation\n" +
                                "Fourth listen to guide message carefully and follow what it says\n" +
                                "Caution when your smartphone vibrates or you are in dot blocks for the visually impaired. please, stop for a while and proceed\n";

                speakMessage(message, true, null, false);
            }
        });
        findViewById(R.id.btn_setting).setOnClickListener(v -> {
            if (isRunningNavigation) {
                String message = language.equals("ko") ? "경로 안내 중에는 사용 할 수 없습니다." : "You can't use this during navigating.";
                speakMessage(message ,true,null, false);
                return;
            }
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_calibration).setOnClickListener(v -> {
            if (positioningStatus == 9) mServiceManager.doUserCalibration(context);
            else{
                String message = language.equals("ko") ? "측위 중에 사용 가능합니다." : "you can use only during positioning.";
                speakMessage(message, true, null, false);
            }
        });
        findViewById(R.id.btn_initialize).setOnClickListener(v -> {
            if (positioningStatus == 9) mServiceManager.doUserInitializing(context);
            else {
                String message = language.equals("ko") ? "측위 중에 사용 가능합니다." : "you can use only during positioning.";
                speakMessage(message, true, null, false);
            }
        });
        findViewById(R.id.btn_location).setOnClickListener(v -> {
            CellSpace CurrentCellSpace = VIMIndoorFeatures.getCellSpaceWherePointExist(user.getPoint());
            String message;
            if (CurrentCellSpace == null) {
                message = language.equals("ko") ? "위치 확인이 불가능합니다." : "I can't find where you are.";
            } else {
                message = language.equals("ko") ? "당신은 " + CurrentCellSpace.getName() + " 에 있습니다." : "You are in the " + CurrentCellSpace.getName();
            }
            speakMessage(message, true, null, false);
        });
        findViewById(R.id.btn_reset_route).setOnClickListener(v -> {
            String message;
            if (!isRunningNavigation) {
                message = language.equals("ko") ? "경로 안내 중에만 사용 할 수 있습니다." : "you can use only during navigating.";
                speakMessage(message, true, null, false);
                return;
            }
            if (!VIMIndoorFeatures.reSetRoute()) {
                message = language.equals("ko") ? "경로 재설정 실패" : "Failed to reset the route";
                speakMessage(message, true, null, false);
            }
        });
        findViewById(R.id.btn_select_destination).setOnClickListener(v -> {
            if (locationFile == null) {
                String message = language.equals("ko") ? "측위시스템을 실행 시켜 주세요." : "Please, run the positioning system.";
                speakMessage(message, false, null, false);
            } else {
                new SimpleSearchDialogCompat(context,
                        language.equals("ko")
                                ? getResources().getString(R.string.kr_search)
                                : getResources().getString(R.string.search),
                        language.equals("ko")
                                ? "원하시는 목적지를 입력하세요."
                                : "Search destination what you want.", null, getAllDestinationData(),
                        (dialog, item, position) -> {
                            DestinationSearchModal destinationSearchModal = (DestinationSearchModal) item;
                            DestinationInfo selectedDestination = destinationSearchModal.getDestinationInfo();
                            if (selectedDestination.hasTarget())
                                selectedDestination.setTargetPoint(VIMIndoorFeatures.getMultiLayeredGraph().getSpaceLayer(selectedDestination.getTargetLayer()).getNode().getStateMap().get(selectedDestination.getTargetId()).getPoint());
                            user.setDestinationInfo(selectedDestination);
                            text_destination.setText(language.equals("ko")
                                    ? "\n현재 목적지: " + destinationSearchModal.getTitle()
                                    : "\ncurrent destination: " + destinationSearchModal.getTitle());
                            dialog.dismiss();
                        }).show();
            }
        });
        findViewById(R.id.btn_nearby).setOnClickListener(v -> {
            if(!isRunningNavigation || positioningStatus != 9) {
                String message = language.equals("ko") ? "경로 안내 중에만 사용 할 수 있습니다." : "you can use only during navigating.";
                speakMessage(message ,true,null, false);
                return;
            }

            List<GuideInfo> guideInfoList = VIMIndoorFeatures.getNearbyPoi();
            if (guideInfoList == null || guideInfoList.size() == 0) {
                String message = language.equals("ko") ? "근처에 P O I 가 없습니다." : "There is no P O I nearby.";
                speakMessage(message, true, null, false);
                return;
            }
            tts.stop();
            guideInfoList.forEach(this::speakGuide);
            /*Random random = new Random(System.currentTimeMillis());
            int service = getResources().getIntArray(R.array.service_no)[(random.nextInt(3))];
            if (serviceNo != service) {
                constructMap(service);
            }*/
        });
        findViewById(R.id.btn_positioning).setOnClickListener(v -> {
            Button btn_positioning = v.findViewById(R.id.btn_positioning);
            if (mServiceManager == null && positioningStatus < 0) {
                if(isRunningNavigation) {
                    turnOffNavigation();
                    return;
                }
                initPositioningSystem();
                btn_positioning.setText(language.equals("ko")
                        ? getResources().getString(R.string.kr_stop_positioning)
                        : getResources().getString(R.string.stop_positioning));
            } else if (mServiceManager != null && positioningStatus > 0) {
                stopPositioning();
                turnOffNavigation();
                btn_positioning.setText(language.equals("ko")
                        ? getResources().getString(R.string.kr_start_positioning)
                        : getResources().getString(R.string.start_positioning));
            }
        });
        findViewById(R.id.btn_repeat_guide).setOnClickListener(v -> {
            if (positioningStatus != 9 || !isRunningNavigation) {
                String message = language.equals("ko") ? "경로 안내 중에만 사용 할 수 있습니다." : "you can use only during navigating.";
                speakMessage(message ,true,null, false);
                return;
            }
            tts.stop();
            executeNavigation(true, 0);
        });
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            CharSequence destination = user.getDestinationInfo().getName(language);
            String message = null;
            if (isRunningNavigation) {
                message = language.equals("ko") ? "현재 경로 안내중 입니다." : "It's currently guiding route now.";
            } else if (positioningStatus != 9) {
                message = language.equals("ko") ? "측위 시작 버튼을 눌러주세요." : "please, turn on the positioning system.";
            } else if (destination == null || destination.length() == 0){
                message = language.equals("ko") ? "목적지를 선택해주세요." : "please, select a destination.";
            }
            if (message != null) {
                speakMessage(message, true, null, false);
                return;
            }
            turnOnNavigation();
        });
        findViewById(R.id.btn_stop).setOnClickListener(v -> {
            String message;
            if (isRunningNavigation) {
                turnOffNavigation();
                message = language.equals("ko") ? "경로안내를 중지합니다." : "Stop the navigation.";
            }
            else {
                message = language.equals("ko") ? "현재 경로 안내 중이지 않습니다." : "It's not currently guiding route now.";
            }
            speakMessage(message ,true,null, false);
        });
    }

    private void initTTS() {
        tts = new TextToSpeech(this, status ->  {
            if(status == TextToSpeech.SUCCESS){
                if (language.equals("ko")){
                    tts.setLanguage(Locale.KOREA);
                }else {
                    tts.setLanguage(Locale.US);
                }
                tts.addEarcon(VIMSTATE.GO.name(),getPackageName(),R.raw.navigation);
                tts.addEarcon(VIMSTATE.LANDMARK.name(),getPackageName(),R.raw.landmark);
                tts.addEarcon(VIMSTATE.SAFETY.name(), getPackageName(),R.raw.safety);
                tts.addEarcon(VIMSTATE.STOP.name(), getPackageName(),R.raw.stop);
                tts.addEarcon(VIMSTATE.TURN.name(), getPackageName(),R.raw.turn);

                tts.setPitch(1.0f);
                tts.setSpeechRate(sharedPreferences.getFloat("guideSpeed", 2.0f));
            } else {
                Log.e("error", "Initialization Failed!");
            }
        });
    }
    private void loadIndoorGML(String fileName) {
        IndoorGmlParser indoorGmlParser = new IndoorGmlParser();
        VIMIndoorFeatures = new VIMIndoorFeatures(user, sharedPreferences);
        VIMIndoorFeatures.registerVIMIndoorFeaturesObserver(this);

        AssetManager assetManager = getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open("indoorgml/"+fileName);

            indoorGmlParser.parse(inputStream, VIMIndoorFeatures);
            VIMIndoorFeatures.initGraph(getResources().getString(R.string.network_layer));
            user.setNetworkEdge(VIMIndoorFeatures.getMultiLayeredGraph().getSpaceLayer(getResources().getString(R.string.network_layer)).getEdge());
        } catch(IOException | XmlPullParserException e) {
            Log.e("tag", "Failed to copy asset file: " , e);
        }
    }

    //
    private void constructMap(int mapId) {
        this.serviceNo = mapId;
        locationFile = new LocationFile(String.valueOf(serviceNo), this);
        loadIndoorGML(locationFile.getName());
    }
    private ArrayList<DestinationSearchModal> getAllDestinationData(){
        ArrayList<DestinationSearchModal> items = new ArrayList<>();

        Map<String, DestinationInfo> destinationInfoMap = this.locationFile.getAllDestination();

        destinationInfoMap.forEach((id, destinationInfo) -> {
            if (!destinationInfo.hasSpatialCanMoveFloor()) {
                items.add(new DestinationSearchModal(language, destinationInfo));
            }else if (sharedPreferences.getString("setUpPathAboutFloor","stair").equals(destinationInfo.getSpatialCanMoveFloor().getType())){
                items.add(new DestinationSearchModal(language, destinationInfo));
            }
        });

        items.sort(DestinationSearchModal::compareTo);

        return items;
    }

    private void stopPositioning() {
        mServiceManager.stopService();
        mServiceManager = null;
        positioningStatus = -1;
//        return mServiceManager == null && positioningStatus == -1 ? true : false;
    }
    private void turnOnNavigation() {
        isRunningNavigation = true;
        VIMIndoorFeatures.setState(VIMSTATE.STOP);
        user.initializeDetectPOI();
    }
    private void turnOffNavigation() {
        isRunningNavigation = false;
        this.beepGoSound = true;
        VIMIndoorFeatures.setState(VIMSTATE.STOP);
//        if you want to save a log file remove the below comment.
//        user.saveUserLog();
        user.setWantExplain(true);
    }
    private void speakGuide(GuideInfo guideInfo) {
        boolean interrupt = false;
        boolean vibration = false;
        String message = guideInfo.getMessage();
        Log.e(TAG, "speakGuide: " + message);
        VIMSTATE vimstate = guideInfo.getState();
//        String type = guideInfo.getType();
        String sound = null;
        user.pushGuideMessageLog(message);
        switch (vimstate) {
            case SAFETY:
            case LANDMARK:
                sound = vimstate.name();
                break;
            case ARRIVE:
                interrupt = true;
                vibration = true;
                turnOffNavigation();
                break;
            case GO:
                if (beepGoSound) {
                    sound = vimstate.name();
                    beepGoSound = false;
                } else sound =null;
                break;
            case TURN:
                vibration = true;
                beepGoSound = true;
                break;
            case FLOOR_MOVING:
                vibration = true;
                break;
            default:
                break;
        }
        speakMessage(message, interrupt, sound, vibration);
    }
    private void speakMessage(String message, boolean interrupt, String sound, boolean vibration){
        if(tts == null) return;
        if(tts.isSpeaking() && interrupt) {
            tts.stop();
            tts.playSilentUtterance(100,TextToSpeech.QUEUE_FLUSH,null);
        }
        if(sound != null) tts.playEarcon(sound,TextToSpeech.QUEUE_ADD,null, null);
        if (vibration) {
            Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() { tts.speak(message, TextToSpeech.QUEUE_ADD, null, null); }
            };
            Timer timer = new Timer(true);
            timer.schedule(timerTask, 1500);
        }

        if (!vibration) tts.speak(message, TextToSpeech.QUEUE_ADD, null, null);
    }
    public void executeNavigation(boolean force, long systemTime){
        String destination = user.getDestinationInfo().getId();

        List<GuideInfo> guideInfoList = VIMIndoorFeatures.getGuideInfoList(destination, force, systemTime);
        if (guideInfoList == null) return;
        guideInfoList.stream().sorted().forEach(this::speakGuide);
    }

    private double yawAngleToGeneralAngle(float yawAngle) {
        return yawAngle < 0 ? Math.toDegrees(yawAngle * -1) : Math.toDegrees(Math.PI * 2 - yawAngle);
    }

    /* IPS Service Start/Spot Control Code for IPS SDK : End */
    private void initPositioningSystem() {
        /* Interface Code for IPS SDK : Start */
        mServiceManager = ServiceManager.getInstance(this);
        mServiceManager.setBaseServiceInfo(getResources().getString(R.string.service_url), getResources().getIntArray(R.array.service_no));
        mServiceManager.setDetailListener((mapIdx, x, y, z, heading, similarity, rollPitchYaw) -> {
            long time = System.currentTimeMillis();
            if (time - systemTime >  750) {
                systemTime = time;
                if (serviceNo != mapIdx) constructMap(mapIdx);
                user.set(x, y, z, (float) yawAngleToGeneralAngle(rollPitchYaw[2]), time);
                if (isRunningNavigation) executeNavigation(false, time);
            }
            runOnUiThread(() -> text_information.setText(user.toString()));
        });
        mServiceManager.setStatusListener(new StatusListener() {
            @Override
            public void onStatus(int statusCode) {
                positioningStatus = statusCode;
                runOnUiThread(() -> {
                    String message;
                    switch (statusCode) {
                        case 7:
                            message = language.equals("ko") ? "측위 시스템을 실행합니다. 다음 안내가 나오기 전까지 기다려주세요." : "Execute the positioning system. pleas, wait until next guide.";
                            speakMessage(message, false, null, false);
                            break;
                        case 9:
                            message = language.equals("ko") ? "측위 시스템이 정상적으로 실행 되었습니다." : "The positioning system has been executed correctly.";
                            speakMessage(message, false, null, false);
                            break;
                    }
                    text_positioning_status.setText(String.format(Locale.KOREA,"%s : %d","Positioning Status",positioningStatus));
                });
            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();

            }
        });
        Intent intent = new Intent(this, PermissionActivity.class);
        startActivityForResult(intent, mRequestCode);
        /* Interface Code for IPS SDK : End */
    }
    /* IPS Service Start/Spot Control Code for IPS SDK : Start */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == mRequestCode) {
            if( resultCode == RESULT_OK ) {
                mServiceManager.startService();
            }
            else {
                this.finish();
            }
        }
    }

}

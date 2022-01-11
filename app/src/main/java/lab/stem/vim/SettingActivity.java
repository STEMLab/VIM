package lab.stem.vim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;
import java.util.Objects;

class SettingInterface {
    private final Toolbar toolbar;
    private final TextView guideMessage;
    private final TextView normalPOI;
    private final TextView safetyPOI;
    private final TextView brailleBlocks;
    private final TextView setUpPathAboutFloor;
    private final TextView elevator;
    private final TextView escalator;
    private final TextView stair;
    private final TextView language;
    private final TextView korean;
    private final TextView english;
    private final TextView guideSpeed;

    public SettingInterface(Activity activity) {
        this.toolbar = activity.findViewById(R.id.setting_toolbar);
        this.guideMessage = activity.findViewById(R.id.text_guide_message);
        this.normalPOI = activity.findViewById(R.id.setting_guide_normal_poi);
        this.safetyPOI = activity.findViewById(R.id.setting_guide_safety_poi);
        this.brailleBlocks = activity.findViewById(R.id.setting_guide_visually_impaired);
        this.setUpPathAboutFloor = activity.findViewById(R.id.text_set_up_path_about_floor);
        this.elevator = activity.findViewById(R.id.setting_path_floor_elevator);
        this.escalator = activity.findViewById(R.id.setting_path_floor_escalator);
        this.language = activity.findViewById(R.id.text_setting_language);
        this.korean = activity.findViewById(R.id.setting_korean);
        this.english = activity.findViewById(R.id.setting_english);
        this.stair = activity.findViewById(R.id.setting_path_floor_stair);
        this.guideSpeed = activity.findViewById(R.id.text_setting_guide_speed);
    }

    public void setLanguage(String language) {
        if (language.equals("ko")) {
            this.toolbar.setTitle(R.string.kr_setting);
            this.guideMessage.setText(R.string.kr_guide_message);
            this.normalPOI.setText(R.string.kr_normal_poi);
            this.safetyPOI.setText(R.string.kr_safety_poi);
            this.brailleBlocks.setText(R.string.kr_braille_blocks);
            this.setUpPathAboutFloor.setText(R.string.kr_set_up_path_about_floor);
            this.elevator.setText(R.string.kr_elevator);
            this.escalator.setText(R.string.kr_escalator);
            this.stair.setText(R.string.kr_stair);
            this.language.setText(R.string.kr_language_setting);
            this.korean.setText(R.string.kr_ko);
            this.english.setText(R.string.kr_en);
            this.guideSpeed.setText(R.string.kr_guide_speed_setting);
        } else {
            this.toolbar.setTitle(R.string.setting);
            this.guideMessage.setText(R.string.guide_message);
            this.normalPOI.setText(R.string.normal_poi);
            this.safetyPOI.setText(R.string.safety_poi);
            this.brailleBlocks.setText(R.string.braille_blocks);
            this.setUpPathAboutFloor.setText(R.string.set_up_path_about_floor);
            this.elevator.setText(R.string.elevator);
            this.escalator.setText(R.string.escalator);
            this.stair.setText(R.string.stair);
            this.language.setText(R.string.language_setting);
            this.korean.setText(R.string.ko);
            this.english.setText(R.string.en);
            this.guideSpeed.setText(R.string.guide_speed_setting);
        }
    }
}

class Setting {
    private boolean instructionOfLandmark;
    private boolean instructionOfSafety;
    private boolean brailleBlocks;
    private String distanceForm;
    private String setUpPathAboutFloor;
    private final SharedPreferences.Editor editor;
    private String language;
    private float guideSpeed;

    public Setting(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        language = Locale.getDefault().getLanguage();

        this.instructionOfLandmark = sharedPreferences.getBoolean("instructionOfLandmark",true);
        this.instructionOfSafety = sharedPreferences.getBoolean("instructionOfSafety",true);
        this.brailleBlocks = sharedPreferences.getBoolean("brailleBlocks", false);
        this.distanceForm = sharedPreferences.getString("distanceForm", Locale.getDefault().equals(Locale.US) ? "feet" : "meter");
        this.setUpPathAboutFloor = sharedPreferences.getString("setUpPathAboutFloor","stair");
        this.language = sharedPreferences.getString("language", language);
        this.guideSpeed = sharedPreferences.getFloat("guideSpeed",0.5f);
    }

    public boolean isInstructionOfLandmark() {
        return instructionOfLandmark;
    }

    public void setInstructionOfLandmark(boolean instructionOfLandmark) {
        editor.putBoolean("instructionOfLandmark", instructionOfLandmark);

        if (editor.commit()) this.instructionOfLandmark = instructionOfLandmark;
    }

    public boolean isInstructionOfSafety() {
        return instructionOfSafety;
    }

    public void setInstructionOfSafety(boolean instructionOfSafety) {
        editor.putBoolean("instructionOfSafety", instructionOfSafety);

        if (editor.commit()) this.instructionOfSafety = instructionOfSafety;
    }

    public boolean isBrailleBlocks() {
        return brailleBlocks;
    }

    public void setBrailleBlocks(boolean brailleBlocks) {
        editor.putBoolean("brailleBlocks", brailleBlocks);

        if (editor.commit()) this.brailleBlocks = brailleBlocks;
    }

    public String getDistanceForm() {
        return distanceForm;
    }

    public void setDistanceForm(String distanceForm) {
        editor.putString("distanceForm", distanceForm);

        if (editor.commit()) this.distanceForm = distanceForm;
    }

    public String getSetUpPathAboutFloor() {
        return setUpPathAboutFloor;
    }

    public void setSetUpPathAboutFloor(String setUpPathAboutFloor) {
        editor.putString("setUpPathAboutFloor", setUpPathAboutFloor);
        if (editor.commit()) this.setUpPathAboutFloor = setUpPathAboutFloor;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        editor.putString("language", language);
        if (editor.commit()) this.language = language;
    }

    public float getGuideSpeed() {
        return guideSpeed;
    }

    public void setGuideSpeed(float guideSpeed) {
        editor.putFloat("guideSpeed", guideSpeed);
        if (editor.commit()) this.guideSpeed = guideSpeed;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "instructionOfLandmark=" + instructionOfLandmark +
                ", instructionOfSafety=" + instructionOfSafety +
                ", distanceForm='" + distanceForm + '\'' +
                '}';
    }
}

public class SettingActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SettingInterface settingInterface;
    private Setting setting;
    private SwitchMaterial instructionLandmark;
    private SwitchMaterial instructionSafety;
    private SwitchMaterial brailleBlocks;
    private RadioGroup setUpPathFloor;
    private RadioGroup settingLanguage;
    private RadioGroup distanceForm;
    private TextToSpeech tts;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initParams();
        initComponents();
        setUpSettingComponents(setting);
        initTTS(this);

        Objects.requireNonNull(getSupportActionBar()).hide();
    }
    
    public void initParams() {
        this.setting = new Setting(this);
        this.sharedPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        this.settingInterface = new SettingInterface(this);
        this.settingInterface.setLanguage(setting.getLanguage());
    }

    public void initComponents(){
        instructionLandmark = findViewById(R.id.setting_guide_normal_poi);
        instructionSafety = findViewById(R.id.setting_guide_safety_poi);
        brailleBlocks = findViewById(R.id.setting_guide_visually_impaired);
        setUpPathFloor = findViewById(R.id.setting_path_floor);
        settingLanguage = findViewById(R.id.setting_language);
        SeekBar guideSpeedSeekBar = findViewById(R.id.setting_guide_speed);
        distanceForm = findViewById(R.id.setting_distance_form);


        instructionLandmark.setChecked(sharedPreferences.getBoolean("instructionOfLandmark", false));
        instructionSafety.setChecked(sharedPreferences.getBoolean("instructionOfSafety", false));
        brailleBlocks.setChecked(sharedPreferences.getBoolean("brailleBlocks", false));
        guideSpeedSeekBar.setProgress((int) (sharedPreferences.getFloat("guideSpeed", 2.0f) * 2.0));

        instructionLandmark.setOnCheckedChangeListener((buttonView, isChecked) -> setting.setInstructionOfLandmark(isChecked));
        instructionSafety.setOnCheckedChangeListener((buttonView, isChecked) -> setting.setInstructionOfSafety(isChecked));
        brailleBlocks.setOnCheckedChangeListener((buttonView, isChecked) -> setting.setBrailleBlocks(isChecked));
        distanceForm.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.setting_distance_feet) setting.setDistanceForm(getResources().getString(R.string.feet));
            else if (checkedId == R.id.setting_distance_meter) setting.setDistanceForm(getResources().getString(R.string.meter));
        });
        setUpPathFloor.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.setting_path_floor_elevator) setting.setSetUpPathAboutFloor("elevator");
            else if (checkedId == R.id.setting_path_floor_escalator) setting.setSetUpPathAboutFloor("escalator");
            else if (checkedId == R.id.setting_path_floor_stair) setting.setSetUpPathAboutFloor("stair");
        });
        settingLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.setting_korean) {
                setting.setLanguage("ko");
                settingInterface.setLanguage("ko");
            }
            else if (checkedId == R.id.setting_english) {
                setting.setLanguage("en");
                settingInterface.setLanguage("en");
            }
        });
        guideSpeedSeekBar.setContentDescription("클릭하여 현재 음성 안내 속도를 들어보세요.");
        guideSpeedSeekBar.setOnClickListener(v -> {
            tts.setSpeechRate(sharedPreferences.getFloat("guideSpeed", 0.5f));
            String message = setting.getLanguage().equals("ko") ? "현재 안내음성 속도 입니다." : "This is current voice guide speed.";
            tts.setLanguage(sharedPreferences.getString("language","ko").equals("ko") ? Locale.KOREA : Locale.ENGLISH);
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        });
        guideSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setting.setGuideSpeed((float)(seekBar.getProgress()/2.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() { tts.speak(message, TextToSpeech.QUEUE_ADD, null, null);
//                    }
//                };
//
//                Timer timer = new Timer(true);
//
//                timer.schedule(timerTask, 1500);
            }
        });

        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        toolbar.setNavigationContentDescription(sharedPreferences.getString("language","ko").equals("ko") ? "뒤로가기" : "back");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void initTTS(Context context) {
        this.tts = new TextToSpeech(context, status -> {
            if(status == TextToSpeech.SUCCESS){
                if (sharedPreferences.getString("language","ko").equals("ko")){
                    tts.setLanguage(Locale.KOREA);
                }else {
                    tts.setLanguage(Locale.US);
                }
            }
        });
    }

    public void setUpSettingComponents(Setting setting) {
        if (setting.isInstructionOfLandmark() != instructionLandmark.isChecked()){
            instructionLandmark.toggle();
        }

        if (setting.isInstructionOfSafety() != instructionSafety.isChecked()){
            instructionSafety.toggle();
        }

        if (setting.isBrailleBlocks() != brailleBlocks.isChecked()){
            brailleBlocks.toggle();
        }

        switch (setting.getDistanceForm()){
            case "feet":
                distanceForm.check(R.id.setting_distance_feet);
                break;
            case "meter":
                distanceForm.check(R.id.setting_distance_meter);
                break;
            default:
                break;
        }

        switch (setting.getSetUpPathAboutFloor()){
            case "elevator":
                setUpPathFloor.check(R.id.setting_path_floor_elevator);
                break;
            case "stair":
                setUpPathFloor.check(R.id.setting_path_floor_stair);
                break;
            case "escalator":
                setUpPathFloor.check(R.id.setting_path_floor_escalator);
                break;
            default:
                break;
        }

        switch (setting.getLanguage()){
            case "ko":
                settingLanguage.check(R.id.setting_korean);
                break;
            case "en":
                settingLanguage.check(R.id.setting_english);
                break;
            default:
                break;
        }
    }
}
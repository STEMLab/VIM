package lab.stem.vim.log;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lab.stem.vim.indoorGML.Point;

class FootPrint {
    private final Point point;
    private final float yawAngle;
    private final float dir;
    private final long time;
    private final String date;
    private final String guide;

    public FootPrint(double x, double y, double z, float yawAngle, float dir, long time, String date, String guide) {
        this.point = new Point(x,y,z);
        this.yawAngle = yawAngle;
        this.dir = dir;
        this.time = time;
        this.date = date;
        this.guide = guide;
    }

    @Override
    public String toString() {
        return "{\"point\": "+ point.toString() +", \"yawAngle\":"+ yawAngle +", \"dir\": " + dir + ", \"time\": "+ time +", \"date\": "+ date +", \"guide\": " +guide+"}";
    }
}

public class UserLog {
    List<FootPrint> footPrintList;

    public UserLog() {
        this.footPrintList = new ArrayList<>();
    }

    public void addLog(Point point, float yawAngle, float dir, long time, String date, String guide) {
        FootPrint footPrint = new FootPrint(point.getX(), point.getY(), point.getZ(), yawAngle, dir, time, "\""+date+"\"", guide == null ? null : "\""+guide+"\"");
        this.footPrintList.add(footPrint);
    }

    @Override
    public String toString() {
        return footPrintList.toString();
    }

    public void writeLogFile() {
        String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(new Date()) + ".json";
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "Log");
            if (!dir.exists()) dir.mkdir();
            File file = new File(Environment.getExternalStorageDirectory(),"Log/"+fileName);
            if (!file.exists()) file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(this.toString());
            fileWriter.flush();
            fileWriter.close();

            this.footPrintList.clear();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

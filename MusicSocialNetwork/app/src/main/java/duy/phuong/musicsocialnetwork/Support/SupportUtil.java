package duy.phuong.musicsocialnetwork.Support;

/**
 * Created by Phuong on 16/07/2016.
 */
public abstract class SupportUtil {
    public static String secToTime(int seconds) {
        String result = "";
        if (seconds < 60) {
            result = seconds + "s";
        } else {
            int minutes = seconds / 60;
            seconds %= 60;
            if (minutes < 60) {
                result = minutes + "m " + seconds + "s";
            } else {
                int hours = minutes / 60;
                minutes %= 60;
                if (hours < 24) {
                    result = hours + "h " + minutes + "m " + seconds + "s";
                } else {
                    int days = hours / 24;
                    hours %= 24;
                    result = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
                }
            }
        }
        return result;
    }
}

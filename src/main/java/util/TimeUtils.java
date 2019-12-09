package util;


public final class TimeUtils {

    /**把时间格式为mm：ss的字符串转换成秒数
     * @param totalTime 需要转换的字符串
     * @return int整数
     * */
    public static int toSeconds(String totalTime){
        String minute = totalTime.substring(0,totalTime.indexOf(":"));
        String subSeconds = totalTime.substring(totalTime.indexOf(":")+1);
        if (minute.length()!= 2 ||subSeconds.length() != 2){
            return 0;
        }
        return Integer.valueOf(minute) * 60 + Integer.valueOf(subSeconds);
    }

    /**把秒数转换成"04:01"格式的函数
     * @param second 秒数
     * @return String */
    public static String toString(int second) {
        String minuteStr = "00";
        String secondStr = "00";
        int minutes = second/60;
        int seconds = second%60;
        if (minutes<10){        //如果小于10,显示将会是1位,在前面加上一个0
            minuteStr = "0" + minutes;
        }
        else {
            minuteStr = String.valueOf(minutes);
        }
        if (seconds<10){        //如果小于10,显示将会是1位,在前面加上一个0
            secondStr = "0" + seconds;
        }
        else {
            secondStr = String.valueOf(seconds);
        }
        return minuteStr + ":" + secondStr;
    }
}

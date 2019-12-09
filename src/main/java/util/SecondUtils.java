package util;

public final class SecondUtils {

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
}

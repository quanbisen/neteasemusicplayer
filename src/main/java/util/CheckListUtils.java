package util;

import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-7
 */
public final class CheckListUtils {

    /**判断两个字符串集合是否不同
     * @param list1 字符串集合1
     * @param list2 字符串集合2
     * @return boolean*/
    public static boolean checkWeatherSame(List<String> list1,List<String> list2){
        if (list1 == null && list2 == null){
            return true;
        }else if (list1 != null && list2 == null){
            return false;
        }else if (list1 == null && list2 != null){
            return false;
        }
        if (list1.size()!=list2.size()){
            return false;
        }
        for (String str1 : list1){
            if (!list2.contains(str1)){
                return false;
            }
        }
        return true;
    }
}

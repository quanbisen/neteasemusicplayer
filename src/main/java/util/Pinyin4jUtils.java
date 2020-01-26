package util;

import net.sourceforge.pinyin4j.PinyinHelper;

public class Pinyin4jUtils {

    /**
     * 提取首个字符的首字母，其他返回*
     *
     * @param str
     * @return String
     */
    public static char getFirstPinYinHeadChar(String str) {

        char head;
        char word = str.charAt(0);
        // 提取汉字的首字母
        String[] pinyinArray;
        // 是否是英文字母
        if (word >= 128) {
            pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            // 如果不是汉字，就返回#
            if (pinyinArray != null) {
                head = pinyinArray[0].charAt(0);
            } else {
                head = '#';
            }
        } else {
            // 是字母直接返回，不是返回#
            if (Character.isLetter(word)) {
                head = word;
            } else {
                head = '#';
            }
        }
        // 全部返回大写
        return String.valueOf(head).toUpperCase().charAt(0);
    }

}


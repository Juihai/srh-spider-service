package com.shenruihai.spider.utils;

/**
 * ISBN合法性检查
 *
 * 支持10位、13位的ISBN编码校验
 * @author shenruihai
 * @date 2022/5/13
 */
public class IsbnUtil {

    public static boolean checkIsbn(String isbn) {
        if(isbn.length()==10){
            return check10Isbn(isbn);
        }else if(isbn.length()==13){
            return check13Isbn(isbn);
        }else {
            return false;
        }
    }

    /**
     * 验证10位的isbn
     *
     * 规则：
     *  1. 先从左到右分配每个数字的位置
     *  2. 将每个数字乘以其对应的位置号，然后对乘积求和
     *  3. 把和除以11，然后求余数
     * 结果：
     *  如果余数为零，那么它是一个有效的10位ISBN
     *  如果余数不为零，那么它是一个无效的10位ISBN
     *
     */
    private static boolean check10Isbn(String isbn){
        String reverseIsbn = new StringBuffer(isbn).reverse().toString();
        char[] isbnChars = reverseIsbn.toCharArray();
        int locationNum = 1;
        int isbnSum = 0;
        for(char a: isbnChars){
            isbnSum += a * locationNum;
            locationNum = locationNum+ 1;
        }
        int remainder = isbnSum%11;
        return remainder==0;
    }

    /**
     * 验证13位的isbn
     * 规则：
     *  1. 从右到左分配每个数字的位置
     *  2. 每个数交替乘以1和3，然后把它们的乘积加起来。奇数位置乘以1；偶数位置乘以3
     *  3. 把和数除以10，然后求余数
     * 结果：
     *  如果余数为零，那么它是一个有效的13位ISBN
     *  如果余数不为零，那么它是一个无效的13位ISBN
     * @param isbn
     * @return
     */
    private static boolean check13Isbn(String isbn){
        String reverseIsbn = new StringBuffer(isbn).reverse().toString();
        char[] isbnChars = reverseIsbn.toCharArray();
        int locationNum = 1;
        int isbnSum = 0;
        for(char a: isbnChars){
            isbnSum += a * (locationNum%2==1? 1 : 3);
            locationNum = locationNum+ 1;
        }
        int remainder = isbnSum%10;
        return remainder==0;
    }

//    public static void main(String[] args) {
//        String testIsbn10 = "7810900218";
//        String testIsbn13 = "9787020024759";
//
//        System.out.println("10位合法的isbn验证:"+IsbnUtils.checkIsbn(testIsbn10));
//        System.out.println("13位合法的isbn验证:"+IsbnUtils.checkIsbn(testIsbn13));
//    }

}

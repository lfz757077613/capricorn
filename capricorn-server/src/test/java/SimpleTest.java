import org.junit.Test;

import java.io.File;
import java.net.URL;

/**
 * Author: fuzhi.lai
 * Date: 2019/2/18 下午2:23
 * Create by Intellij idea
 */
/*
                       _oo0oo_
                      o8888888o
                      88" . "88
                      (| -_- |)
                      0\  =  /0
                    ___/`---'\___
                  .' \\|     |// '.
                 / \\|||  :  |||// \
                / _||||| -:- |||||- \
               |   | \\\  -  /// |   |
               | \_|  ''\---/''  |_/ |
               \  .-\__  '-'  ___/-. /
             ___'. .'  /--.--\  `. .'___
          ."" '<  `.___\_<|>_/___.' >' "".
         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
         \  \ `_.   \_ __\ /__ _/   .-` /  /
     =====`-.____`.___ \_____/___.-`___.-'=====
                       `=---='
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

               佛祖保佑         永无BUG
*/
public class SimpleTest {

    @Test
    public void simpleTest() {
        URL filePath = getClass().getResource(File.separator + "client-config.properties");
        String path = filePath.getPath();
        System.out.println(path);
        File file = new File(path);
        System.out.println(file.exists());

    }
}

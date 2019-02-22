package local;/**
 * 描述：
 * Created by Liuyg on 2018-12-29 14:57
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @program: mmqbb
 * @description:
 * @author: Liuyg
 * @create: 2018-12-29 14:57
 **/

public class JsonToSql {
    public static void main(String[] args) {
        String json = readFile();
        String sql = " insert into mm_sys_pid (pid,gzhName) values ";
        /*JSONObject json1 = JSONObject.parseObject(json);
        json1 = JSONObject.parseObject(json1.get("data").toString());
        JSONArray jsonlist = JSONArray.parseArray(json1.get("pagelist").toString());
        for (Object obj : jsonlist){
            JSONObject jo = JSONObject.parseObject(obj.toString());
            sql += "('"+jo.getString("adzonePid")+"','xzkc'),";
            // System.out.println("insert into mm_sys_pid (pid,gzhName) values ('"+jo.getString("adzonePid")+"','xzkc');");
        }
        sql =  sql.substring(0,sql.length()-1) + ";";*/
        String[] pids = json.split("mm_");
        for ( String pid : pids){
            pid = "mm_"+pid.substring(0,pid.indexOf(" "));
            sql += "insert into mm_sys_pid (pid,gzhName) values  ('"+pid+"','mmqbb');\n";
        }
        sql += "\nDELETE\n" +
                "FROM\n" +
                "    mm_sys_pid\n" +
                "WHERE\n" +
                "    pid IN (\n" +
                "       select pid from ( SELECT\n" +
                "            pid\n" +
                "        FROM\n" +
                "            mm_sys_pid \n" +
                "        GROUP BY\n" +
                "            pid\n" +
                "        HAVING\n" +
                "            count(pid) > 1 ) as t1\n" +
                "    )\n" +
                "AND id NOT IN (\n" +
                " select id from (   SELECT\n" +
                "        min(id) as id\n" +
                "    FROM\n" +
                "        mm_sys_pid q2\n" +
                "    GROUP BY\n" +
                "        pid\n" +
                "    HAVING\n" +
                "        count(pid) > 1 ) as t2\n" +
                ")";
        System.out.println(sql);
    }
    /**
     * 读入TXT文件
     */
    public static String readFile() {
        String pathname = "src/main/resources/pid/新建文本文档.txt"; // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        String result = "";
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                result+=line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

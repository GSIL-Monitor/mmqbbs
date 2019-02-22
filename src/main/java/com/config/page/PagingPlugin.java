package com.config.page;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;

@Intercepts({
    @Signature(type = StatementHandler.class,
            method = "prepare",  
            args = {Connection.class, Integer.class })})  
public class PagingPlugin implements Interceptor {
  
    /** 
     * 插件默认参数，可配置 
     */  
    private Integer defaultPage; //默认页码  
    private Integer defaultPageSize;//默认每页条数  
    private Boolean defaultUseFlag; //默认是否启用插件  
    private Boolean defaultCheckFlag; //默认是否检测页码参数  
    private Boolean defaultCleanOrderBy; //默认是否清除最后一个order by 后的语句  
  
    private static final String DB_TYPE_MYSQL = "mysql";  
    private static final String DB_TYPE_ORACLE = "oracle";


    /** 
     * 插件实现方法 
     */  
    @Override  
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler stmtHandler = (StatementHandler) getUnProxyObject(invocation.getTarget());

        MetaObject metaStatementHandler = SystemMetaObject.forObject(stmtHandler);
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");  
  
        MappedStatement mappedStatement =  (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        String dbType = this.getDataSourceType(mappedStatement);


        final Object[] args = invocation.getArgs();

        String id = mappedStatement.getId();
        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        Object parameterObject = boundSql.getParameterObject();

        Configuration configuration = mappedStatement.getConfiguration();  // 获取节点的配置
        String zxsql = getSql(configuration, boundSql, id);
        //不是select语句.
        if (!this.checkSelect(sql)) {
            long startTime = System.currentTimeMillis();
            Object o = invocation.proceed();
            long endTime = System.currentTimeMillis();
            System.out.println("执行SQL["+zxsql+"]，耗时"+(endTime-startTime)+"ms");
            return   o;
        }

        Page page = getPageParamsForParamObj(parameterObject);
        if (page == null) { //无法获取分页参数，不进行分页。  
            long startTime = System.currentTimeMillis();
            Object o = invocation.proceed();
            long endTime = System.currentTimeMillis();
            System.out.println("执行SQL["+zxsql+"]，耗时"+(endTime-startTime)+"ms");
            return   o;
        }  
  
        //获取配置中是否启用分页功能.  
        Boolean useFlag = page.getUseFlag() == null? this.defaultUseFlag : page.getUseFlag();  
        if (!useFlag) {  //不使用分页插件.  
            long startTime = System.currentTimeMillis();
            Object o = invocation.proceed();
            long endTime = System.currentTimeMillis();
            System.out.println("执行SQL["+zxsql+"]，耗时"+(endTime-startTime)+"ms");
            return   o;
        }  
        //获取相关配置的参数.  
        Integer pageNum = page.getPageNum();
        Integer pageSize = page.getPageSize();
        Boolean checkFlag = page.getCheckFlag() == null? defaultCheckFlag : page.getCheckFlag();  
        Boolean cleanOrderBy = page.getCleanOrderBy() == null? defaultCleanOrderBy : page.getCleanOrderBy();  
        //计算总条数  
        int total = this.getTotal(invocation, metaStatementHandler, boundSql, cleanOrderBy, dbType);  
        //回填总条数到分页参数  
        page.setTotal(total);  
        //计算总页数.  
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;  
        //回填总页数到分页参数.  
        page.setTotalPage(totalPage);  
        //检查当前页码的有效性.  
        this.checkPage(checkFlag, pageNum, totalPage);  
        //修改sql  
        return this.preparedSQL(invocation, metaStatementHandler, boundSql, pageNum, pageSize, dbType);  
    }  
  
  
    /*** 
     * 分离出分页参数. 
     * @param parameterObject --执行参数 
     * @return 分页参数 
     * @throws Exception 
     */  
    public Page getPageParamsForParamObj(Object parameterObject) throws Exception {  
    	Page pageParams = null;  
        if (parameterObject == null) {  
            return null;  
        }  
        //处理map参数和@Param注解参数，都是MAP  
        if (parameterObject instanceof Map) {  
            @SuppressWarnings("unchecked")  
            Map<String, Object> paramMap = (Map<String, Object>) parameterObject;  
            Set<String> keySet = paramMap.keySet();  
            Iterator<String> iterator = keySet.iterator();  
            while(iterator.hasNext()) {  
                String key = iterator.next();  
                Object value = paramMap.get(key);  
                if (value instanceof Page) {  
                    return (Page)value;  
                }  
            }  
        } else if (parameterObject instanceof Page) { //参数POJO继承了PageParams  
            return (Page) parameterObject;  
        } else { //从POJO尝试读取分页参数.  
            Field[] fields = parameterObject.getClass().getDeclaredFields();  
            //尝试从POJO中获得类型为PageParams的属性  
            for (Field field : fields) {  
                if (field.getType() == Page.class) {  
                    PropertyDescriptor pd = new PropertyDescriptor (field.getName(), parameterObject.getClass());  
                    Method method = pd.getReadMethod();  
                    return (Page) method.invoke(parameterObject);  
                }  
            }  
        }  
        return pageParams;  
    }

    private void insertSql(Invocation ivt, String sql) throws Throwable {
        //配置对象
        String countSql = "insert into data_syn (zxsql) values (?)";
        //获取拦截方法参数，根据插件签名，知道是Connection对象.
        Connection connection = (Connection) ivt.getArgs()[0];
        PreparedStatement ps = null;
        int rs = 0;
        try {
            //预编译插入sql
            ps = connection.prepareStatement(countSql);
            ps.setString(1,sql);
            rs = ps.executeUpdate();
            if ( rs < 1 ){
                System.out.println("插入错误");
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
    /** 
     * 判断是否查询sql语句.
     * @param sql 
     * @return 
     */  
    private boolean checkSelect(String sql) {  
        String trimSql = sql.trim();  
        int idx = trimSql.toLowerCase().indexOf("select");  
        return idx == 0;  
    }
    /**
     * 判断是否更新sql语句.
     * @param sql
     * @return
     */
    private boolean checkUpdate(String sql) {
        String trimSql = sql.trim();
        int idx = trimSql.toLowerCase().indexOf("update");
        return idx == 0;
    }
    /**
     * 判断是否插入sql语句.
     * @param sql
     * @return
     */
    private boolean checkInsert(String sql) {
        String trimSql = sql.trim();
        int idx = trimSql.toLowerCase().indexOf("insert");
        return idx == 0;
    }

    /** 
     * 检查当前页码的有效性. 
     * @param checkFlag 
     * @param pageNum 
     * @param pageTotal 
     * @throws Throwable 
     */  
    private void checkPage(Boolean checkFlag, Integer pageNum, Integer pageTotal) throws Throwable  {  
        if (checkFlag) {  
            //检查页码page是否合法.  
            if (pageNum > pageTotal) {  
                throw new Exception("查询失败，查询页码【" + pageNum + "】大于总页数【" + pageTotal + "】！！");  
            }  
        }  
    }  
  
  
    /** 
     * 预编译改写后的SQL，并设置分页参数 
     * @param invocation 
     * @param metaStatementHandler 
     * @param boundSql 
     * @param pageNum 
     * @param pageSize 
     * @param dbType 
     * @throws IllegalAccessException 
     * @throws InvocationTargetException 
     */  
    private Object preparedSQL(Invocation invocation, MetaObject metaStatementHandler, BoundSql boundSql, int pageNum, int pageSize, String dbType) throws Exception {
        //获取当前需要执行的SQL  
        String sql = boundSql.getSql();  
        String newSql = this.getPageDataSQL(sql, dbType);  
        //修改当前需要执行的SQL  
        metaStatementHandler.setValue("delegate.boundSql.sql", newSql);  
        //执行编译，这里相当于StatementHandler执行了prepared()方法，这个时候，就剩下2个分页参数没有设置。  
        Object statementObj = invocation.proceed();  
        //设置两个分页参数。  
        this.preparePageDataParams((PreparedStatement)statementObj, pageNum, pageSize, dbType);  
        return statementObj;  
    }  
  
  
  
    /** 
     * 获取总条数. 
     * 
     * @param ivt Invocation 
     * @param metaStatementHandler statementHandler 
     * @param boundSql sql 
     * @param cleanOrderBy 是否清除order by语句 
     * * @param dbType 
     * @return sql查询总数. 
     * @throws Throwable 异常. 
     */  
    private int getTotal(Invocation ivt, MetaObject metaStatementHandler, BoundSql boundSql, Boolean cleanOrderBy, String dbType) throws Throwable {
        //获取当前的mappedStatement  
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        //配置对象  
        Configuration cfg = mappedStatement.getConfiguration();
        //当前需要执行的SQL  
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");  
        //去掉最后的order by语句  
        if (cleanOrderBy) {  
            sql = this.cleanOrderByForSql(sql);  
        }  
        String countSql = this.getTotalSQL(sql, dbType);  
        //获取拦截方法参数，根据插件签名，知道是Connection对象.  
        Connection connection = (Connection) ivt.getArgs()[0];  
        PreparedStatement ps = null;  
        int total = 0;  
        try {  
            //预编译统计总数SQL  
            ps = connection.prepareStatement(countSql);  
            //构建统计总数SQL  
            BoundSql countBoundSql = new BoundSql(cfg, countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            //构建MyBatis的ParameterHandler用来设置总数Sql的参数。  
            ParameterHandler handler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), countBoundSql);
            //设置总数SQL参数  
            handler.setParameters(ps);  
            //执行查询.  
            ResultSet rs = ps.executeQuery();  
            while (rs.next()) {  
                total = rs.getInt("total");  
            }  
        } finally {  
            //这里不能关闭Connection否则后续的SQL就没法继续了。  
            if (ps != null) {  
                ps.close();  
            }  
        }  
        return total;  
    }

    private String cleanOrderByForSql(String sql) {  
        StringBuilder sb = new StringBuilder(sql);  
        String newSql = sql.toLowerCase();  
        //如果没有order语句,直接返回  
        if (newSql.indexOf("order") == -1) {  
            return sql;  
        }  
        int idx = newSql.lastIndexOf("order");  
        return sb.substring(0, idx);
    }  
  
    /** 
     * 从代理对象中分离出真实对象. 
     * 
     * @param ivt --Invocation 
     * @return 非代理StatementHandler对象 
     */  
    private Object getUnProxyObject(Object target) {  
        MetaObject metaStatementHandler = SystemMetaObject.forObject(target);
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过循环可以分离出最原始的的目标类)  
        Object object = null;  
        while (metaStatementHandler.hasGetter("h")) {  
            object = metaStatementHandler.getValue("h");  
        }  
        if (object == null) {  
            return target;  
        }  
        return object;  
    }  
  
    /** 
     * 生成代理对象 
     * @param statementHandler 原始对象 
     * @return 代理对象 
     */  
    @Override  
    public Object plugin(Object statementHandler) {  
        return Plugin.wrap(statementHandler, this);
    }  
  
    /** 
     * 设置插件配置参数。 
     * @param props 
     */  
    @Override  
    public void setProperties(Properties props) {  
        String strDefaultPage = props.getProperty("default.page", "1");  
        String strDefaultPageSize = props.getProperty("default.pageSize", "50");  
        String strDefaultUseFlag = props.getProperty("default.useFlag", "false");  
        String strDefaultCheckFlag = props.getProperty("default.checkFlag", "false");  
        String StringDefaultCleanOrderBy = props.getProperty("default.cleanOrderBy", "false");  
  
        this.defaultPage = Integer.parseInt(strDefaultPage);  
        this.defaultPageSize = Integer.parseInt(strDefaultPageSize);  
        this.defaultUseFlag = Boolean.parseBoolean(strDefaultUseFlag);  
        this.defaultCheckFlag = Boolean.parseBoolean(strDefaultCheckFlag);  
        this.defaultCleanOrderBy = Boolean.parseBoolean(StringDefaultCleanOrderBy);  
    }  
  
    /** 
     * TODO 
     * 计算总数的SQL, 
     * 这里需要根据数据库的类型改写SQL，目前支持MySQL和Oracle 
     * @param currSql —— 当前执行的SQL 
     * @return 改写后的SQL 
     * @throws NotSupportedException 
     */  
    private String getTotalSQL(String currSql, String dbType){  
        if (DB_TYPE_MYSQL.equals(dbType)) {  
            return  "select count(*) as total from (" + currSql + ") $_paging";  
        } else if (DB_TYPE_ORACLE.equals(dbType)) {  
            return "select count(*) as total from (" + currSql +")";  
        } else {  
            return "当前插件未支持此类型数据库";  
        }  
    }  
  
    /** 
     * TODO 需要使用其他数据库需要改写 
     * 分页获取参数的SQL 
     * 这里需要根据数据库的类型改写SQL，目前支持MySQL和Oracle 
     * @param currSql —— 当前执行的SQL 
     * @return 改写后的SQL 
     * @throws NotSupportedException 
     */  
    private String getPageDataSQL(String currSql, String dbType){  
        if (DB_TYPE_MYSQL.equals(dbType)) {  
            return "select * from (" + currSql + ") $_paging_table limit ?, ?";  
        } else if (DB_TYPE_ORACLE.equals(dbType)) {  
            return " select * from (select cur_sql_result.*, rownum rn from (" + currSql + ") cur_sql_result  where rownum <= ?) where rn > ?";  
        } else {  
        	return "当前插件未支持此类型数据库";  
        }  
    }  
  
    /** 
     * TODO 需要使用其他数据库需要改写 
     * 使用PreparedStatement预编译两个分页参数，如果数据库的规则不一样，需要改写设置的参数规则。目前支持MySQL和Oracle 
     * @throws SQLException 
     * @throws NotSupportedException 
     * 
     */  
    private void preparePageDataParams(PreparedStatement ps, int pageNum, int pageSize, String dbType) throws Exception {  
        //prepared()方法编译SQL，由于MyBatis上下文没有我们分页参数的信息，所以这里需要设置这两个参数.  
            //获取需要设置的参数个数，由于我们的参数是最后的两个，所以很容易得到其位置  
            int idx = ps.getParameterMetaData().getParameterCount();  
            if (DB_TYPE_MYSQL.equals(dbType)) {  
                //最后两个是我们的分页参数.  
                ps.setInt(idx -1, (pageNum - 1) * pageSize);//开始行  
                ps.setInt(idx, pageSize); //限制条数  
            } else if (DB_TYPE_ORACLE.equals(dbType)) {  
                ps.setInt(idx -1, pageNum * pageSize);//结束行  
                ps.setInt(idx, (pageNum - 1) * pageSize); //开始行  
            } else {  
                System.out.println("当前插件未支持此类型数据库");  
            }  
  
    }  
  
    /** 
     * 
     * TODO 需要使用其他数据库需要改写 
     * 目前支持MySQL和Oracle 
     * @param mappedStatement 
     * @return 
     * @throws Exception 
     */  
    private String getDataSourceType(MappedStatement mappedStatement) throws Exception {
        Connection conn = null;  
        String dbConnectionStr = null;  
        try {  
            conn = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();  
            dbConnectionStr  = conn.toString();  
        } finally {  
            if (conn != null) {  
                conn.close();  
            }  
        }  
        if (null == dbConnectionStr || dbConnectionStr.trim().equals(""))  {  
            return "当前插件未能获得数据库连接信息。";  
        }  
        dbConnectionStr = dbConnectionStr.toLowerCase();  
        if (dbConnectionStr.contains(DB_TYPE_MYSQL)) {  
            return DB_TYPE_MYSQL;  
        } else if (dbConnectionStr.contains(DB_TYPE_ORACLE)) {  
            return DB_TYPE_ORACLE;  
        } else {  
            return "当前插件未支持此类型数据库";  
        }  
    }


    // 封装了一下sql语句，使得结果返回完整xml路径下的sql语句节点id + sql语句
    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
//        str.append(sqlId);
//        str.append(":");
        str.append(sql);
        return str.toString();
    }
    /*<br>    *如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理<br>　　*/
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }
    // 进行？的替换
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();  // 获取参数
        List<ParameterMapping> parameterMappings = boundSql
                .getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");  // sql语句中多个空格都用一个空格代替
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry(); // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换
            // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);// MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);  // 该分支是动态sql
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    }else{sql=sql.replaceFirst("\\?","缺失");}//打印出缺失，提醒该参数缺失并防止错位
                }
            }
        }
        return sql;
    }
}
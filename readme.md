json 轻量级数据交换格式

表示：{} 或 [{},{}]

例：
Person-->json
{
  "age":20,
  "name":"zhangsan",
  "b":false,
  "d":3.0
}

需求
前台
   购买 
      商品-->首页，商品列表，商品详情
      购物车-->商品添加到购物车，更改商品数量，删除商品，全选，取消全选，单选，结算
      订单：
          下单-->订单确认（地址管理），订单提交
          订单中心-->订单列表，订单详情，取消订单
      地址-->增删改查
      支付-->支付宝支付
   用户体系
       登录
       注册
       修改密码
       ...
后台
    管理员登录
    商品管理-->添加，修改，商品上下架
    品类管理-->查看，添加
    订单管理-->查看（列表，详情），发货

数据库表结构设计
1.用户表（用户名唯一，MD5加密）
2.类别表（无限层级表结构）
   id  parent_id     name
    1       0        电子产品
    2       1         手机
    3       2         华为
    4       2        小米手机
    5       3         meta20 Pro 
  ...
  查询电子产品下所有的子类别？
       递归查询
       
   <h1>nybatis-generator插件用法</h1>
   1.pom.xml添加插件
       <plugin>
        <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-maven-plugin</artifactId>
        <version>1.3.6</version>
        <configuration>
          <verbose>true</verbose>
          <overwrite>true</overwrite>
        </configuration>
       </plugin>
  2.创建插件生成的配置文件     
   generatorConfig.xml
   
   <?xml version="1.0" encoding="UTF-8" ?>
      <!DOCTYPE generatorConfiguration PUBLIC
              "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
              "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd" >
      <generatorConfiguration>
          <classPathEntry location=""/>
          <context id="context" targetRuntime="MyBatis3Simple">
              <commentGenerator>
                  <property name="suppressAllComments" value="false"/>
                  <property name="suppressDate" value="true"/>
              </commentGenerator>
              <jdbcConnection userId="" password="" driverClass="" connectionURL=""/>
              <javaTypeResolver>
                  <property name="forceBigDecimals" value="false"/>
              </javaTypeResolver>
              <javaModelGenerator targetPackage="" targetProject=".">
                  <property name="enableSubPackages" value="false"/>
                  <property name="trimStrings" value="true"/>
              </javaModelGenerator>
              <sqlMapGenerator targetPackage="" targetProject=".">
                  <property name="enableSubPackages" value="false"/>
              </sqlMapGenerator>
              <javaClientGenerator targetPackage="" type="XMLMAPPER" targetProject=".">
                  <property name="enableSubPackages" value="false"/>
              </javaClientGenerator>
              <table schema="" tableName="" enableCountByExample="false" enableDeleteByExample="false"
                     enableSelectByExample="false" enableUpdateByExample="false"/>
          </context>
      </generatorConfiguration>
   
   ####  接口测试
   postman 软件
   restlet 插件（谷歌浏览器）
   
   ####  ssm框架
   1.pom.xml添加依赖
   2.添加配置文件  
      spring配置文件
      springmvc 配置文件
      mybatis 配置文件
      web.xml
   3.使用框架
   
   用户模块
  ###  1，功能介绍
   登录
   用户名验证
   注册
   忘记密码
   提交问题答案
   重置密码
   获取用户信息
   更新用户信息
   退出登录
   ### 2，学习目标
    横向越权、纵向越权安全漏洞
    横向越权：攻击者尝试访问与他拥有相同权限的用户的资源
    纵向越权：低级别攻击者尝试访问高级别用户的资源
    MD5明文加密及增加salt值
    Guava缓存的使用
    高服用服务响应对象的设计思想及抽象封装
  ###  3，服务端接口返回前端的统一对象
        class ServerResponse<T>{
          int status;//接口返回状态码
          T  data;//封装了接口的返回数据
          String msg;//封装错误信息
         }
   4，业务逻辑
   #### 4.1登录功能
        step1:参数非空校验
        step2:校验用户名是否存在
        step3:根据用户名和密码查询用户
        step4:返回结果
   ####4.2注册功能
         step1:参数非空校验
         step2:校验用户名是否存在
         step3:校验邮箱是否存在
         step4:调用Dao接口插入用户信息
         step5:返回结果
  #### 4.3忘记密码之修改密码功能
   4.3.1 根据username查询密保问题
        step1:参数非空校验
        step2:校验username是否存在
        step3:根据username查询密保问题
        step4:返回数据  
   4.3.2 提交问题答案
        step1:参数非空校验
        step2:校验答案是否正确
        step3:为防止横向越权，服务端生成forgetToken保存，并将其返回给客户端
        step4:返回结果
   4.3.3 重置密码
        step1:参数非空校验
        step2:校验token是否有效
        step3:修改密码
        step4:返回结果
   
   #### 迭代开发-线上部署
       step1:在阿里云服务器上建库、建表
       step2:修改代码中数据库的连接参数
       step3:项目打成war包
       step4:将war包上传到阿里云服务器的tomcat/webapps
       step5:访问测试  
   
   ###  类别模块
   #### 1.功能介绍
        获取节点
        增加节点
        修改名称
        获取分类
        递归子节点id
   ####  2.学习目标
      如何设计及封装无限层级的树状数据结构
      递归算法的设计思想
      如何处理复杂对象重排
      重写hashcode和equals的注意事项
   
   
   
   
   
   
   
   
   
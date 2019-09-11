# GoldSQLite 1.0

#### 介绍
&ensp; &ensp; 此项目的构建主要受公司项目的启发。公司项目应用内的数据库文件较多，
各自实现方式不一，维护管理以及拓展的成本都比较高。为了使应用数据能够统一管理，适
应需求变化，尝试构建了一个ORM数据库框架，用于学习Android SQLite的相关知识。于此
同时，借着此机会把Kotlin的学习付诸于实践，用Kotlin实现框架本身。示例则仍使用
Java，模拟两种语言混合使用开发的场景。 

#### 特性

1. ORM映射构建数据表；
2. 支持建立多个数据库，并可指定本地存储不同的目录建库；
3. 封装DAO操作，多线程（最大5个线程）执行异步操作，对单表控制并发；
4. 支持跨进程操作，在不同进程中的使用没有差异，无需额外配置；
5. 数据表查询支持返回指定类型Javabean实体列表；
6. 可针对数据表的操作设置监听，且监听支持跨进程；
7. 可监听数据库的创建与更新，且默认在数据表升级时处理数据表扩列、新增，无需在
Upgrate中操作，只需要更相关数据表实体类和版本号即可。

#### 软件架构
暂无（待梳理）

#### 安装教程

暂无（待深入测试）

#### 使用说明

1. 在assets资源文件目录下创建GoldSQLite.xml文件，并配置数据库信息。例如:  
   ````XML
   <?xml version="1.0" encoding="UTF-8"?>
    <gold-sqlite>
    <!-- name -> 数据库名称 -->
    <!-- version -> 数据库版本 -->
    <database
            name = "demo1"
            version = "1">
        <!-- 数据表对应实体类（完整包路径） -->
        <table class = "com.lxj.goldsqlite.Student"/>
    </database>

    <!-- external-path -> 本地存储目录 -->
    <database
            name = "demo2"
            version = "1"
            external-path = "/goldsqlite">
        <table class = "com.lxj.goldsqlite.Teacher"/>
    </database>
    </gold-sqlite>
   ````
2. 构建数据表的Javabean实体，注解标识数据表（支持继承关系），例如：  
   ````java
   
    //  创建一个Person类，设置id为主键，有姓名、性别两个属性
    public class Person {
        @MainKey
        @Column(name = "id")
        int id;
        @Column(name = "name")
        String name;
        @Column(name = "sex")
        String sex;
        ...
    }
    
    //  创建数据表Student实体，继承于Person，并用注解声明数据表
    //  该表属性为：id、姓名、性别、班级、成绩、家庭。
    @TableName(tableName = "Student")
    public class Student extends Person{
        @Column(name = "classz", length = 10)
        String classz;
        @Column(name = "grade")
        int grade;
        @Column(name = "family")
        String family;
        ...
    }
   ````
3. 在Application中初始化，传入当前Application对象，例如：  
   ````java
    public class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            GoldSQLite.INSTANCE.init(this);
        }
    }    
   ````
4. CRUD操作，其内部本质是基于ContentProvider对SQLiteDatabase中CRUD操作的封装
   ，传参以Build形式呈现,示例:  
   插入（支持单行、多行插入，此处以多行为例）：
   ````java
   
   List<Student> students = new ArrayList<>();
   for(int i = 0; i < 8 ;i++){
     Student student = new Student();
     student.setId(i);
     student.setName("小" + i);
     student.setSex("男");
     student.setGrade(122);
     students.add(student1);
   }
   //   多行插入
   GoldSQLite.INSTANCE.getInsertOperation("demo1", "Student")   //  获取插入操作，传入数据库名称与数据库表名
           .bulkInsert(students)                                //  传入插入的集合
           .build()                                             //  构建插入操作，传入数据库名
           .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
               @Override
               public void onResultReturn(@NotNull Integer result) {
                  Log.d(TAG, "插入行数：" + result);
                  ...
               }
           });
   ````
   删除：
   ````java
   GoldSQLite.INSTANCE.getDeleteOperation("demo1", "Student")
           .where("name", deleteName)  //  设置筛选条件（多个条件可传Map），不传全删
           .build()                
           .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
               @Override
               public void onResultReturn(@NotNull Integer result) {
                  Log.d(TAG, "删除行数：" + result);
                  ...
               }
           });
   ````
   修改：
   ````java
   GoldSQLite.INSTANCE.getUpdateOperation("demo1", "Student")
           .where("name", oldName)  //  设置筛选条件（多个条件可传Map），不传结果你懂的
           .update("name", newName) //   设置更新的值（可传一个数据表实体）
           .build()
           .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
               @Override
               public void onResultReturn(@NotNull Integer result) {
                   Log.d(TAG, "更新行数：" + result);
                   ...
               }
           });
   ````
   查询：
   ````java
        //  以下为全查询示例。若是要定制查询，在build前调用相应API即可。
        //  【注意】使用时Kotlin的Any类型严格规定Java对应的类型为Object，
        //   所以返回结果的数据类型没法自定义（Java泛型是可以的），待研究...
   GoldSQLite.INSTANCE.getQueryOperation("demo1", Student.class)
                .build()
                .execute(new ABaseDbOperation.OnDaoFinishedCallback<List<Object>>() {
                    @Override
                    public void onResultReturn(@NotNull List<Object> result) {
                        if(!result.isEmpty()){
                            Log.d(TAG, "查询行数：" + result.size());
                            ...
                        }else {
                            Log.d(TAG, "查询行数为0");
                            ...
                        }
                    }
                });
   ````
5. 数据表操作的监听：
   ````java
   /*
     声明SQLite观察者用于监听数据库操作
     参数1： 需要观察的数据表名，为空则默认监听所有数据表
     参数2： 监听的数据操作类型，增、删、改、查任一或组合、或所有操作，参数如下：
     
     数据表的操作类型，用于监听数据表时鉴别数据表操作
     ALL_OPERATION      ->  所有操作
     OPERATION_QUERY    ->  查询
     OPERATION_INSERT   ->  插入
     OPERATION_UPDATE   ->  更新
     OPERATION_DELETE   ->  删除
     以上参数可组合，如同时监听更新与删除：OPERATION_UPDATE | OPERATION_DELETE 
   */
   SQLiteObserver observer1 = new SQLiteObserver(null, ALL_OPERATION) {
        @Override
        public void onChange(@NotNull String tableName, int operationType) {
            Log.d(TAG, "监听所有表的所有操作：" + "表名: " + tableName + "; 操作： " + operationType);
        }
    };
   ````
   注册观察者与注销观察者：
   ````java
   //  注册观察者
   GoldSQLite.INSTANCE.resgisterObserver(observer1);
   //  注销观察者
   GoldSQLite.INSTANCE.ungisterObserver(observer1);
   ````
   
6. 数据库的升级与更新，可通过设置监听器来实现，例如：
   ````java
   GoldSQLite.INSTANCE.resgisterDbListener(new DbStateListener() {
            @Override
            public void onCreate(@NonNull String dbName) {
                //  数据库创建
                //  在多个数据库共存的情况下，根据不同数据库名称来执行不同操作
            }

            @Override
            public void onUpgrate(@NonNull String dbName, int oldVersion, int newVersion) {
                //  数据库更新
                //  在多个数据库共存的情况下，根据不同数据库名称来执行不同操作
            }
        });
   ````
   另外需要说明的是，如果数据表新增列，或者在某一个数据库新增了一张表，可无需在监听器里操作。只需要在相应的数据表内新增字段或者新增一个数据表实体，
   然后在GoldSQLite.xml配置文件中更新数据库版本即可，当然，新增表的话需要在xml配置中增加新表的声明。

#### 参与贡献

1. ORM的底子部分来源于项目的陈年代码，感谢一代代程序猿努力的积累。
2. 此项目的部分实现思路借鉴了郭霖大神的数据库框架LitePal。

#### 其他说明

## 项目介绍
---------

### 名称：
Push Center（推送中心）   

### 目标：
一个用于集中发送推送请求的转发系统。比如现在有A、B、C三款应用，当他们的服务端准备向各自的客户端发送推送时（客户端平台可能是iOS，Android或者WP），可以将推送请求发送到推送中心，再由这里向各平台的推送服务提交推送数据。  

### 系统特点：
+ 对推送请求采用链式处理设计，可以方便地在此基础上增加或者删除功能（比如可在推送节点工作完毕后增加数据库节点，将推送结果录入数据库）；

+ 多线程并发处理推送请求，最大限度提高系统并发能力；  

+ 开发了一套用于各应用向本系统发送推送请求的SDK，各应用无需关心和本系统交互的数据格式是什么样的，通信接口是哪个，怎么序列化和反序列化数据。具体见 [[SDK](https://github.com/HAND-MAS/mas-service-sdk)]

+ 方便在现有系统中添加新应用的推送信息，简单配置即可实现推送；  

+ 如果需要新增推送平台（Android会有此问题，比如从第三方推送平台迁移到内部的一个推送平台），新增一个对应平台的推送模块，实现指定接口，在系统中注册即可；  



### 实现技术：
Java; Spring MVC; Hibernate

### 教程
+ 快速上手并使用系统 [Tutorial](https://github.com/HAND-MAS/PushCenter/wiki/Tutorial)
+ 向系统中添加定制化功能 [System in Depth](https://github.com/HAND-MAS/PushCenter/wiki/System-in-Depth)
+ 系统架构介绍 [Architecture in Detail]()
    
### 系统不足之处
+ 目前全部的精力都放在对数据流的处理上，没有用户界面，查询推送结果只能查看日志或者手动查询数据库。


## 系统介绍
--------
本系统采用web application的部署形式，可使用HTTP和JMS两种方式对系统发送推送请求。请求到达后，系统解析出参数，根据现有的处理链条配置，将此次请求放置在一个线程单独处理，然后立即返回此任务的jobId，发送请求的一方可以根据此id稍后对结果进行查询  

### 架构图
<img src="http://dl.iteye.com/upload/picture/pic/129009/e2cbde61-6ecd-381a-80c3-b182a23cb2d2.png"/>  

推送时序图
<img src="http://dl.iteye.com/upload/picture/pic/129315/116d7024-5c37-36c7-8268-3764c4259d2d.png"/>



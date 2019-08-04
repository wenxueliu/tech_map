
初创软件公司技术栈

### 语言

Javascript, Java, Python, Go，C，C++

主力：Javascript, Java，Go
辅助：Python, C, C++

### 项目管理

Jira ：任务管理、WiKi、bug 跟踪

客户管理：悟空CRM

ERP : 购买 SaaS 服务

gitLab : 代码托管

Gerrit : Code Review

Jenkins：与 gitLab 配合，CI/CD

### 开发工具

* IDEA : Java，Go，Python
* git : 代码管理
* typero : markdown 文档管理

### 架构

DNS : 阿里万网、腾讯 DnsPod
LB : 云服务、自建（LVS，Haproxy, nginx)
CDN: 腾讯云、阿里云、网宿

#### 开发

基础设施：云服务（虚拟机 + K8S)

前端框架: Vue
微服务系统: Spring Boot + Spring Cloud
缓存: Redis
关系型数据库：Mysql
NoSql 数据库: 文档数据库、图数据库、列式、键值
Rpc 框架: gRpc
消息队列：RabbitMq
搜索: ElasticSearch
大数据: Spark + YARN
自动测试: Jmeter

#### 运维

* 自动化执行: ansible
* 监控系统: Promethus + InfluxDB + Grafana  osquery(实时定位问题)
* 日志系统: LogStash(Filebeat) + ElasticSearch + Kibana
* 安全审计: JumpServer
* 灰度发布: K8S, Kong,

### 规范

#### 项目管理

需求文档
告警处理流程
早会规范
周报规范

#### 开发

1. 开发流程规范
2. 代码规范
3. git分支管理
4. 数据库设计规范
5. 日志规范

#### 测试

1. 测试规范

#### 运维

1. 运维规范
2. 代码发布流程
3. 数据库运维规范

整个流程

开发人员
代码仓库
流水线: dev, test, uat, pro

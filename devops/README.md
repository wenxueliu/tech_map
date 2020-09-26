

DevOps 研究报告

https://devops-research.com/research.html

## 目标

## 核心

首先是信念变革，其次是组织、流程、工具变革

1. 愿景：信念上的改变，价值的认可
2. 落地：达成愿景

## 原则

1. 发现并解决问题
2、分享和学习的文化
3、关系工具创造的价值而不是工具本身

## 愿景认识

让每个人认识到 devops 的价值


## 落地

要点
1、找到团队内种子用户
2、小范围试用、逐步铺开
3、小步迭代，逐步完善

## 流程

1. 组织调整
2. 流程规范

## 需求

需求管理：jero
看板

## 设计

1. 服务治理
2. 系统架构
3. 需求设计

## 开发

源码管理: gitlab
分支模型：githubflow
代码结构: 领域驱动设计

## 测试

自动化集成测试
UT：不要以覆盖率为指标，而是关注业务逻辑的实现与设计的合理性
自动化 UI 测试

## 运维

CI/CD：jenkins + sonarqube + UT + 集成测试
蓝绿部署
金丝雀发布
A/B 测试

## 工具汇总


#### Linux 命令

git，ls，cd，ifconfig，netstat，管道，find，xarg，sed，awk，grep，tar，ss，route，ss，arp

#### Shell 脚本

https://coolshell.cn/articles/19219.html

#### 容器

docker

k8s

helm ：k8s 集群工具

#### 监控

主要包括系统监控、应用监控

telegraf

grafana

prometheus https://prometheus.io/

osquery: https://blog.spoock.com/2018/11/26/osquery-intro/

#### APM

opentracing，jaeger，skywalking，zipkin，pinpoint，

#### 自动化运维

ansible（puppet，chef可选）：

terraform : https://www.terraform.io/intro/index.html

Terratest

GoCD

#### CI/CD部署

jenkins

#### 代码托管

gitlab

#### Code Review

Gerrit

#### 代码检查

findbug

checkstyle

Sonar

#### 密码管理

将密码凭据与源代码分开管理，并且建议使用 git-secrets 和 Talisman 等工具，以免将密码凭 据凭据存储在源代码中


### API 管理

Swagger

GraphSQL


### 自动化测试

[cypress](https://www.cypress.io/)

puppeteer

TestCafe


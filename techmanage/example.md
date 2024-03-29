

## 角色认知

推动执行
1、明确目标：
2、形成共识：
3、设定计划
4、过程管理：所有任务wetask跟踪，每日早会跟踪任务进度
5、支持保障：协调资源解决问题。比如安装部署、漏洞问题。
6、复盘总结：每个版本会启动迭代回顾会议

问题
1、转测不通过没有及时跟踪
2、拖延：重要不紧急的事情没有运作，比如代码检视，分享。

## A 项目

### TOP工作
1、对外协作方式存在问题
2、设计能力提升
3、SE 投入需求对齐时间有保障，但是对于需求的设计投入不足，需求没有功能设计文档，基本是口口相传，开发需要花费大量时间确认。
4、存在历史债务（下沉时间紧张，重构不彻底）
5、环境问题：coremaster涉及解决方案

自有（1） OD（3） 合作（5）

能力
1、业务能力：
优点：基本能独立定位和解决业务问题
1）下沉需求能独立完成
2）大部分问题能独立解决，部分问题求助后能独立完成
缺点：对业务后续演进，支持多业务考虑不足。
1）下沉中对于没有 OP 的场景考虑不足
2）前端没有站在业务的角度思考，对业务不熟

2、技术能力：
1）前后端开发技术能力基本能满足业务诉求
2）设计能力待加强，对于多场景的考虑欠佳

#### 人员
自有（2）+ OD(3) + 合作（7）

#### 分工
分工明确，后续逐步改为轮流制，要求每个人都熟悉关键服务和业务。

### 协作
1、内部协作：比较顺畅
2、对外协作：统一的接口人

#### 流程
标准特性版本流程。

版本：特性版本发布，与测试对齐版本节奏
流程：关键节点（转测、SIT 每轮转测，发布准备）通过 checklist跟踪。
需求：与SE初评工作量，按照优先级和工作量排需求；反串讲后，开发自己估计转测时间；大颗粒需求专人专项跟踪。

#### 能力
质量意识：较强，对转测和问题回归不通过有明确的认知
风险意识：也不错，风险基本都能及时反馈

### 激励
1、对齐团队目标：业务上团队对齐今年的工作目标


### 环境问题
1、测试没有可独立验证的环境：1）资源紧张；2）依赖业务的自有服务才可以验证，涉及OP会导致测试的难度更大。
2、当前业务的测试环境（dev）和开发环境分支（master）不同，导致同一问题需要两个分支解决。测试切换 master 存在风险，需要过度一段时间。
解决思路
1、 搭建独立的测试环境，要求coremaster支撑部署相关服务
2、 dev和master合并

### 协作问题
1）时间冲突：开发本身有待转测的需求需要尽快验证后转测；业务临时发现问题，面临转测压力也需要紧急处理。
2）沟通成本高：对于业务的问题和需求的理解上有分歧，沟通成本不小。业务出现几次提的需求自己都没有分析清楚的情况。

解决思路
对外协作由统一接口人负责分析是问题还是需求。确认问题后，由业务提单到测试经理，测试经理分配问题给开发解决；确认是需求，由SE和业务SE对齐计划和方案。统一接口人采用轮流制。





## B 项目

TOP 工作
1、版本流程不规范：团队成员对发版流程缺乏清晰认知
2、质量意识不足
3、测试人员人力不足，一个合作方和一个新入职OD：新场景业务可以完成，存量场景缺乏测试用例
5、缺乏单独的测试环境：无法验证安装升级过程中的问题
6、gamma环境和生产环境不一致
7、需求与RAT节奏不一致：每次都是需求开发完了，RAT还没过，需求没有同步，无法关联AR

### 目标
开始：团队对中长期目标不明确
现状：团队对中长期目标不明确，计划中3.19版本迭代中传递团队的定位和目标


### 团队管理

#### 人员
自有（2）+ OD(3) + 合作（7）

#### 分工

开始：团队没有明确的分工
现状：团队统一，初步明确分工

#### 协作
1、 还在磨合中

#### 流程
开始：excel维护需求、问题单，没有单独任务项跟踪，需求拆分不清晰，没有责任到人，SA随时加需求，问题多。一次T版本上线，大量阻塞问题。
现状：标准特性版本流程，所有任务Smart化，并统一在WeTask跟踪。当前版本新需求问题基本收敛。三方件没有在CS库，导致发版延迟一天。

### 能力
总体
1、关键角色很负责任
2、需求开发的能力基本具备

变化点：

代码质量差
开始: 继承采集中心历史代码，代码历史债务高
现状：开始代码检视，存量重构优化规划需求。架构优化规划中

质量意识不强
开始：质量意识不强，存在修改引入问题
现状：规范版本流程，传递质量意识，明确转测延迟和不通过的后果

### 激励
1、 例会公开表扬
2、 成长计划：挑战的工作

### 授权

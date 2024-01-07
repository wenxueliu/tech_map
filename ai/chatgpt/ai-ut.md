



考虑的因素

1、Mock 三方系统

2、入参构造：

3、bytecode 插入

4、选择包



Restful 工具对比

| 工具名称     | 算法                   | 白盒                |
| ------------ | ---------------------- | ------------------- |
| EvoMaster    | MIO algorithm          | White-Box,Black-Box |
| RESTler      | search-based algorithm | Black-Box           |
| RestTestGen  |                        | Black-Box           |
| RESTest      | model-based            | Black-Box           |
| Schemathesis | propertybased          | Black-Box           |
| Dredd        |                        | Black-Box           |
| Tcases       |                        | black-box           |
| bBOXRT       |                        | black-box           |
| APIFuzzer    |                        | black-box           |
|              |                        |                     |



## UT 方法调研

1、代码路径分析：Symbolic Execution

2、参数构造：解析 wsf 规则，基于 pair-wise 方法构造参数

2、方法执行记录：启动程序，自动喂参，记录执行参数

4、根据方法参数自动 mock

3、用例生成

4、用例验证



参考

An Empirical Evaluation of Using Large Language Models for Automated Unit Test Generation 

 https://arxiv.org/pdf/2302.06527.pdf

Unit Test Generation using Generative AI : A Comparative Performance Analysis of Autogeneration Tools https://arxiv.org/pdf/2312.10622.pdf

Reinforcement Learning from Automatic Feedback for High-Quality Unit Test Generation https://www.franktip.org/pubs/testpilot2024.pdf

https://arxiv.org/pdf/2204.08348.pdf

https://github.com/codingsoo/REST_Go

https://arxiv.org/pdf/2204.08348v3.pdf

https://dl.acm.org/doi/pdf/10.1145/3551349.3559498

https://arxiv.org/pdf/2204.12148.pdf

https://dl.acm.org/doi/10.1145/3617175#d1e1509

https://arxiv.org/pdf/2312.00894.pdf

https://github.com/unloggedio/unlogged-sdk

https://read.unlogged.io/directinvoke/

https://github.com/Codium-ai/pr-agent

https://symflower.com/en/company/blog/2023/software-testing-trends-2024/

https://symflower.com/en/company/blog/  博客内容不错

https://symflower.com/en/company/blog/2023/symflower-github-copilot/

https://symflower.com/en/company/blog/2022/methods-for-automated-test-value-generation/ 

https://symflower.com/en/company/blog/2021/symbolic-execution/
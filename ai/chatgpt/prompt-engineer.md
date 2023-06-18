prompt-engineer



### 背景

![nlp-step](nlp-step.png)

目前学术界一般将 NLP 任务的发展分为四个阶段即 NLP 四范式：

1. 第一范式：基于传统机器学习模型的范式，如 tf-idf 特征 + 朴素贝叶斯等机器算法；

2. 第二范式：基于深度学习模型的范式，如 word2vec 特征 + LSTM 等深度学习算法，相比于第一范式，模型准确有所提高，特征工程的工作也有所减少；

3. 第三范式：基于预训练模型 + finetuning 的范式，如 BERT + finetuning 的 NLP 任务，相比于第二范式，模型准确度显著提高，但是模型也随之变得更大，但小数据集就可训练出好模型；

4. 第四范式：基于预训练模型 + Prompt + 预测的范式，如 BERT + Prompt 的范式相比于第三范式，模型训练所需的训练数据显著减少。

在整个 NLP 领域，你会发现整个发展是朝着精度更高、少监督，甚至无监督的方向发展的，而 Prompt Learning 是目前学术界向这个方向进军最新也是最火的研究成果。



### 为什么需要提示工程（prompt engineering）

现在的预训练模型参数量越来越大，为了一个特定的任务去 finetuning 一个模型，然后部署于线上业务，也会造成部署资源的极大浪费。

另外，目前的 AI 产品还比较早期，因为各种原因，产品设置了很多限制，如果你想要绕过一些限制，或者更好地发挥 AI 的能力，也需要用到 Prompt Engineering 技术。

所以，总的来说，Prompt Engineering 是一种重要的 AI 技术：

- 如果你是 AI 产品用户，可以通过这个技术，充分发挥 AI 产品的能力，获得更好的体验，从而提高工作效率。
- 如果你是产品设计师，或者研发人员，你可以通过它来设计和改进 AI 系统的提示，从而提高 AI 系统的性能和准确性，为用户带来更好的 AI 体验。



### 什么是提示工程（prompt engineering）

首先我们应该有的共识是：预训练模型中存在大量知识；预训练模型本身具有少样本学习能力。GPT-3 提出的 In-Context Learning，也有效证明了在 Zero-shot、Few-shot 场景下，模型不需要任何参数，就能达到不错的效果，特别是近期很火的 GPT3.5 系列中的 ChatGPT。

Prompt Engineering 是一种人工智能（AI）技术，它通过设计和改进 AI 的 prompt 来提高 AI 的表现。Prompt Engineering 的目标是创建高度有效和可控的 AI 系统，使其能够准确、可靠地执行特定任务。



### 提示工程的本质

本质上就是设计一个比较契合上游预训练任务的模板，通过模板的设计挖掘出上游预训练模型的潜力，让上游的预训练模型在尽量不需要标注数据的情况下比较好的完成下游的任务，关键包括 3 个步骤：

1. 设计预训练语言模型的任务
2. 设计输入模板样式（Prompt Engineering）
3. 设计 label 样式及模型的输出映射到 label 的方式（Answer Engineering）

### 要不要学提示工程

现在 AI 的发展还比较早期，了解和学习 PE 价值相对比较大，但长远来看可能会被淘汰。这个「长远」可能是 3 年，亦或者 1 年。

![need-prompt-engineering](need-prompt-engineering.png)

### 最重要的前提

LLM 对你一无所知！！！

LLM 对你一无所知！！！

LLM 对你一无所知！！！

LLM 对公开的数据自带上下文

LLM 对私有领域数据一无所知

### 提示工程的类型

* 零样本提示（zero shot prompt ）
* 少样本提示（few shot prompt）
* 思维链（chain-of-thought）

### 提示组成

**指令**：想要模型执行的特定任务或指令。比如总结，翻译，排序，分类等等。

**上下文**：包含外部信息或额外的上下文信息，引导语言模型更好地响应。

1. 角色扮演：包括AI扮演的角色，答案面向的角色。假设你是一个翻译家。解释下黑洞，让小学生也可以听懂
2. 强化具体身份：假设你是一个20年工作的翻译专家，拥有多个博士学位。
3. 准确的上下文信息：结构化表达

**输入数据**：用户输入的内容或问题。

1. 简洁、精确
2. 删除无用信息和修饰词
3. 抽象转化

**输出指示**：指定输出的类型或格式。

1. 目标用户：小学生，高中生、小白还是专家
2. 长度范围
3. 风格：搞笑的，轻松的，正式的，口语的，适合口播的，学术的，书面的等等。更多问gpt
4. 格式：对 json 和 markdown 格式支持最好 

### 提示工程框架

CRISPE Prompt Framework，CRISPE是首字母的缩写，分别代表以下含义：

**CR：Capacity and Role（能力与角色）。你希望** **ChatGPT** **扮演怎样的角色。**

**I：Insight（洞察），背景信息和上下文。**

**S：Statement（陈述），你希望** **ChatGPT** **做什么。**

**P：Personality（个性），你希望** **ChatGPT** **以什么风格或方式回答你。**

**E：Experiment（实验），要求** **ChatGPT** **为你提供多个答案。**

| Step              | Example                                                      |
| ----------------- | ------------------------------------------------------------ |
| Capacity and Role | Act as an expert on software development on the topic of machine learning frameworks, and an expert blog writer.<br/>把你想象成机器学习框架主题的软件开发专家，以及专业博客作者。 |
| Insight           | The audience for this blog is technical professionals who are interested in learning about the latest advancements in machine learning.<br/>这个博客的读者主要是有兴趣了解机器学习最新进展技术的专业人士。 |
| Statement         | Provide a comprehensive overview of the most popular machine learning frameworks, including their strengths and weaknesses. Include real-life examples and case studies to illustrate how these frameworks have been successfully used in various industries.<br/>提供最流行的机器学习框架的全面概述，包括它们的优点和缺点。包括现实生活中的例子，和研究案例，以说明这些框架如何在各个行业中成功地被使用。 |
| Personality       | When responding, use a mix of the writing styles of Andrej Karpathy, Francois Chollet, Jeremy Howard, and Yann LeCun.<br/>在回应时，混合使用 Andrej Karpathy、Francois Chollet、Jeremy Howard 和 Yann LeCun 的写作风格。 |
| Experiment        | Give me multiple different examples.<br/>给我多个不同的例子。 |

### 模型

不同任务使用不同模型

1. **代码生成：code-davinci-002**
2. **文本生成：text-davinci-003**

### 润色

1. 提升文章的独特性：Rewrite the existing document to make it more imaginative, engaging, and unique.
2. 将文档转为引人入胜的故事：Transform the existing document into a compelling story that highlights the challenges faced and the solutions provided.
3. 提升文档说服力：Refine the existing document by incorporating persuasive language and techniques to make it more convincing and impactful.
4. 提升文档的吸引力：Add emotional language and sensory details to the existing document to make it more relatable and engaging.
5. 使内容更加简洁：Refine the existing document by removing unnecessary information and making it more concise and to-the-point.
6. 强调急迫感：Refine the existing document by adding a sense of urgency and emphasizing the need for immediate action.
7. 突出重点：Emphasize important information using bold or italic text.
8. 让模型使用类比或比喻的方法解释复杂问题：Explain complex ideas using analogies or comparisons.
9. 添加现实中的例子：Include case studies or real-world examples to make concepts more relatable.

### 要点

1、上下文的长度是有限制的

gpt3.5 是4096，GPT4分为 GPT-8K 和 GPT-32k，大多数模型为2k左右

2、指令和上下文分开

```
比如
总结下面一段文字 文字: """ 这里输入文字 """
```

3、不要说“不要xxx”，而是说“要xxx”

4、对于公共的知识只需要描述名称，对于私有领域，需要交代详细背景。不确定，找GPT确认

5、用英文提问：最后加上 respond in chinese（建议）

6、复杂问题：可以通过在结尾增加“让我们一步步思考（Let's think step by step）" 或者 “在解决问题之前我们必须回答哪些子问题？”

7、启发式优化：可以让 GPT 给你优化

8、提示词不是万能的

### 提示工程的难点

1、领域知识：业务和技术语言的理解

2、结构化表达能力：

3、抽象能力：

### 示例



更多示例参考 



由浅入深的示例https://sharegpt.com/c/O94ziJJ





费曼学习 https://www.aishort.top/prompt/188

辅助编程  https://www.aishort.top/prompt/218

引导式 AI  https://www.aishort.top/prompt/255

深度思考助手  https://www.aishort.top/prompt/206

个人学习助手  https://sharegpt.com/c/Tc4Uq3L



### 代码

1、写代码

2、写注释

3、重新格式化代码

4、测试代码


```
System:我想你扮演一个20年工作经验的Java 技术专家，使用以下步骤响应用户输入
第一步：检查函数的每行做了什么和作者的意图
第二步：我提供你代码片段，你将分析每个方法，根据Hibernate Validator 注解解析参数，生成参数规则，询问我是否需要删除或增加哪些参数规则，我确认后以json的格式输出确认的参数规则
第三步：针对参数规则，使用2-wise方法生成测试用例数据，询问我删除或增加哪些用例数据，我确认后以 json 格式输出确认的用例数据，
第四步：根据每个用例数据生成对应的测试代码。

要求
1、我们要求测试用例，尽量考虑输入的范围广一些。
2、想一些连代码作者没有想到过的边界条件。
3、能够利用好 Junit4、Mockito 的特性。
4、希望测试用例满足软件测试的FIRST原则。
5、我们要求测试代码的输出结果是确定的，要么通过，要么失败，不要有随机性。
6、测试方法的名称采用 testXXWithXXShouldxxxx 的命名格式
7、每个测试方法包括数据准备，mock依赖，mock行为，断言。
User:"""代码片段"""
```

5、优化代码

6、不同语言之间翻译

7、重构

8、写数据库脚本

9、模拟器：数据库，web server，命令行


https://medium.com/better-programming/10-tips-for-improving-your-coding-with-chatgpt-3e589de3aff3

### 构建自己的应用

提示工程的难点

1、准确描述能力
2、抽象能力
3、业务领域知识

### 总结

提示工程的

## 参考

* https://www.promptingguide.ai/zh 
* https://github.com/JushBJJ/Mr.-Ranedeer-AI-Tutor
* https://arxiv.org/pdf/2305.11430.pdf
* https://help.openai.com/en/articles/6654000-best-practices-for-prompt-engineering-with-openai-api
* https://platform.openai.com/docs/guides/gpt-best-practices/six-strategies-for-getting-better-results

## 附录

### 代理

https://poe.com  推荐免费稳定的代理

https://github.com/xx025/carrot  代理汇总，更新不及时

https://www.perplexity.ai/

### 提示工程教程

* https://www.promptingguide.ai/zh
* https://github.com/thinkingjimmy/Learning-Prompt
* https://github.com/dair-ai/Prompt-Engineering-Guide
* https://github.com/trigaten/Learn_Prompting
* https://github.com/f/awesome-chatgpt-prompts
* https://help.openai.com/en/articles/6654000-best-practices-for-prompt-engineering-with-openai-api
* 深入浅出Prompt Learning要旨及常用方法
* https://www.youtube.com/watch?v=dOxUroR57xs
* https://platform.openai.com/docs/introduction
* https://github.com/acheong08/ChatGPT
* https://github.com/PlexPt/awesome-chatgpt-prompts-zh
* learnprompting.org/zh-Hans/docs/intro
* learningprompt.wiki/docs
* github.com/yzfly/awesome-chatgpt-zh
* github.com/yzfly/wonderful-prompts
* prompt-patterns.phodal.com/
* https://learn.microsoft.com/en-us/azure/cognitive-services/openai/concepts/advanced-prompt-engineering?pivots=programming-language-chat-completions#specifying-the-output-structure
* https://github.com/RimaBuilds/Master-coding-prompts-with-ChatGPT


### 提示词案例
* https://flowgpt.com/
* https://www.aishort.top/
* https://promptperfect.jina.ai/  提示词优化
* https://datalearner.notion.site/datalearner/500-Best-ChatGPT-Prompts-843a319bec1a40bc9fb131ae88304bf3
* https://prompts.chat/
* https://prompt.noonshot.com/

### GPT 应用

* https://cosoh.com/
* https://flowus.cn/  知识管理
* https://www.jasper.ai/  写作（收费）
* https://www.copy.ai/
* https://nav.newzone.top/
* https://www.explainthis.io/zh-hans/ai-toolkit

### GPT 客户端

* https://github.com/GaiZhenbiao/ChuanhuChatGPT
* https://github.com/lencx/nofwl
* https://github.com/Bin-Huang/chatbox

###  论文
* https://arxiv.org/abs/2005.14165
* https://zhuanlan.zhihu.com/p/615197354
* https://arxiv.org/abs/2202.12837
* https://arxiv.org/pdf/2109.01652.pdf
* RLHF https://arxiv.org/abs/1706.03741
* https://arxiv.org/abs/2205.11916
* https://arxiv.org/abs/2205.11916
* https://arxiv.org/pdf/2203.11171.pdf
* https://arxiv.org/pdf/2110.08387.pdf
* https://arxiv.org/abs/2211.01910
* https://arxiv.org/abs/2205.11916
* https://arxiv.org/abs/2010.15980
* https://arxiv.org/abs/2101.00190
* https://arxiv.org/abs/2104.08691
* https://arxiv.org/abs/2211.01910
* https://arxiv.org/abs/2302.11520
* https://arxiv.org/abs/2210.03629 
* https://arxiv.org/abs/2302.00923
* https://arxiv.org/abs/2302.08043
* https://zhuanlan.zhihu.com/p/597586623
* https://zhuanlan.zhihu.com/p/617193230
* https://zhuanlan.zhihu.com/p/615198869
* https://www.futurepedia.io/
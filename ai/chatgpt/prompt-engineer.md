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

本质上就是设计一个比较契合上游预训练任务的模板，通过模板的设计就是挖掘出上游预训练模型的潜力，让上游的预训练模型在尽量不需要标注数据的情况下比较好的完成下游的任务，关键包括 3 个步骤：

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

### 提示工程的类型

* 零样本提示（zero shot prompt ）

* 少样本提示（few shot prompt）
* 思维链（chain-of-thought）

* 自我一致性（self-Consistency）

* 生成知识提示（generate knowledge prompt）
* 自动提示（Automatic Prompt Engineer ）
* Active-Prompt
* 方向性刺激提示（Directional Stimulus Prompting）
* 多模态思维链提示方法





零样本思维链（Zero Shot Chain of Thought，Zero-shot-CoT）提示过程(1)是对 CoT prompting(2) 的后续研究，引入了一种非常简单的零样本提示。他们发现，通过在问题的结尾附加“。**”这几个词，大语言模型能够生成一个回答问题的思维链

最少到最多提示过程 (Least to Most prompting, LtM)(1) 将 思维链提示过程 (CoT prompting) 进一步发展，首先将问题分解为子问题，然后逐个解决。它是受到针对儿童的现实教育策略的启发而发展出的一种技术。在结尾增加“在解决问题之前我们必须回答哪些子问题？”



### 提示组成

**指令**：想要模型执行的特定任务或指令。比如总结，翻译，排序，分类等等。

**上下文**：包含外部信息或额外的上下文信息，引导语言模型更好地响应。

**输入数据**：用户输入的内容或问题。

**输出指示**：指定输出的类型或格式。



1、角色扮演：包括AI扮演的角色，也包括答案面向的角色。假设你是一个翻译家。解释下黑洞，让小学生也可以听懂

2、强化具体身份：假设你是一个20年工作的翻译专家，拥有多个博士学位。

2、准确的上下文信息：

3、答案模板

给出一些示例

4、在结尾说明风格，搞笑的，轻松的，正式的，口语的，适合口播的，学术的，书面的等等

5、如何提高输出的准确性

- 在上下文中提供基本事实（例如相关文章段落或维基百科条目），以减少模型生成虚构文本的可能性。
- 通过降低概率参数并指示模型在不知道答案时承认（例如，“我不知道”）来配置模型以生成更少样式的响应。
- 在提示中提供问题和答案的组合示例，其中可能知道和不知道的问题和答案。



### 提示工程的方法

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



### 风格指导（Style Guidance）

风格指导就是要求 AI 以某种特定的风格来表达。如果没有给出风格指导的问题，ChatGPT 通常会返回一两个短段落的回答，少数需要更长的回答时会返回更多内容。

常见风格关键词



### 描述符（discriminator）

如果你只想改变语气或微调提示而不是重新格式化，添加**描述符**是一个不错的方法。简单地在提示后面添加一两个词可以改变聊天机器人解释或回复您的信息的方式。你可以尝试添加形容词，如“有趣的”、“简短的”、“不友好的”、“学术语法”等，看看答案如何变化！



### 描述

1、精确的描述

2、具体的描述：细节要为目标服务



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

2、指令和上下文分开

```
比如
总结下面一段文字 文字: """ 这里输入文字 """
```

3、尽量包括：上下文，结果，长度，风格，格式

4、长度，风格，格式要精确具体，避免说大部分，一些等不具体的量词

5、不要说“不要xxx”，而是说“要xxx”

6、适当的提示可以引导模型生成有用的输出

7、用多种表达方式以达到最佳结果

8、描述具体的事情并给出相关的背景

9、向模型展示你希望看到的内容，引导模型生成想要的输出

10、用英文提问，最后加上 respond in chinese（建议）

11、复杂问题，可以通过在结尾增加“让我们一步步思考（Let's think step by step）" 

### 示例

https://ai.newzone.top/





### 代码

1、写代码

2、写注释

3、重新格式化代码

4、调试

5、优化代码

6、不同语言之间翻译

7、重构

8、写数据库脚本

9、模拟器：数据库，web server，命令行



https://medium.com/better-programming/10-tips-for-improving-your-coding-with-chatgpt-3e589de3aff3



### 构建自己的应用



好玩的项目

https://flowgpt.com/

chatgpt-github-app

poe.com 如果不想花钱，可以用这个国外的代理，chagpt免费使用

cosoh.com

flowus

promptperfect.jina.ai

gpt代理平台：

https://github.com/xx025/carrot

gpt 应用链接

https://cosoh.com/





TODO

https://github.com/xinsblog/try-llama-index

https://zhuanlan.zhihu.com/p/611079204

https://promptperfect.jina.ai/

https://docs.google.com/document/u/0/d/1h-GTjNDDKPKU_Rsd0t1lXCAnHltaXTAzQ8K2HRhQf9U/mobilebasic

https://help.openai.com/en/articles/6654000-best-practices-for-prompt-engineering-with-openai-api

https://github.com/howl-anderson/unlocking-the-power-of-llms

https://www.perplexity.ai/

https://www.clickprompt.org/zh-CN/

https://huggingface.co/

https://datalearner.notion.site/datalearner/500-Best-ChatGPT-Prompts-843a319bec1a40bc9fb131ae88304bf3

https://github.com/GaiZhenbiao/ChuanhuChatGPT

https://github.com/rockbenben/ChatGPT-Shortcut

https://ai.newzone.top/?tags=code

https://newzone.top/posts/2023-02-27-chatgpt_shortcuts.html

https://ide.trydyno.com/onboard

https://nav.newzone.top/

使用方法论





### 工具

免费



收费

https://www.jasper.ai/

https://www.copy.ai/



### 参考

提示工程教程

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

在线体验

* https://poe.com/ChatGPT

* https://www.clickprompt.orgs

* http://pretrain.nlpedia.ai/

  

提示交流

* https://flowgpt.com/

AI 工具聚合网站

* https://www.chinaz.com/
* https://trydyno.com/
* 

模型 hub  

* https://huggingface.co/



第三方扩展

* langchain
* llmindex



论文

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
* 
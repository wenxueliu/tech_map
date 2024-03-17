[GPT 最佳实践](https://platform.openai.com/docs/guides/gpt-best-practices/gpt-best-practices)

## 前提

一般来说，如果您发现 GPT 模型在某项任务中失败并且有更强大的模型可用，通常值得再次尝试使用更强大的模型。



## 获得更好结果的六种策略



### 写清楚说明

GPT 无法读懂您的想法。如果输出太长，要求简短的答复。如果输出太简单，请要求专家级的写作。如果您不喜欢这种格式，请展示您希望看到的格式。GPT 对您想要什么的猜测越少，您获得它的可能性就越大。

策略：

* 在您的查询中包含详细信息以获得更相关的答案
* 要求模型扮演一个角色
* 使用定界符清楚地指明输入的部分
* 指定完成任务所需的步骤
* 提供例子
* 指定输出所需的长度



### 提供参考文本
GPT 可以自信地编造假答案，尤其是当被问及深奥的话题或引用和 URL 时。就像一页笔记可以帮助学生在考试中取得更好的成绩一样，提供参考文本给 GPT，可以让答案中包含更少的捏造内容。

策略：

* 使用参考文本指示模型来回答
* 使用参考文本中的引用指示模型来回答



### 将复杂任务拆分为更简单的子任务

正如在软件工程中将复杂系统分解为一组模块化组件是一种很好的做法一样，提交给 GPT 的任务也是如此。复杂的任务往往比简单的任务有更高的错误率。此外，复杂的任务通常可以重新定义为更简单任务的工作流，其中早期任务的输出用于构建后续任务的输入。

策略：

* 使用意图分类来识别与用户查询最相关的指令
* 对于需要很长对话的对话应用，总结或过滤之前的对话
* 递归分段总结长文档来构建完整摘要



### 给 GPT 时间“思考”
如果要求将 17 乘以 28，您可能不会立即知道，但随着时间的推移仍然可以计算出来。同样，与而不是花时间找出答案相较，GPT 在试图立即回答时会犯更多的推理错误。在回答之前询问一系列推理可以帮助 GPT 更可靠地推理出正确答案。

策略：

* 在匆忙下结论之前指示模型制定出自己的解决方案
* 使用内心独白或一系列查询来隐藏模型的推理过程
* 询问模型是否遗漏了之前传递的任何内容



### 使用外部工具

通过为 GPT 提供其他工具的输出来弥补它们的弱点。例如，文本检索系统可以将相关文档告知 GPT。代码执行引擎可以帮助 GPT 进行数学运算和运行代码。如果一项任务，如果工具比 GPT 更可靠或更有效，委托给工具。

策略：

* 使用基于嵌入的搜索来实现高效的知识检索
* 使用代码执行来执行更准确的计算或调用外部 API



### 系统地测试更改
如果可以衡量，提高性能会更容易。在某些情况下，对提示的修改会在一些孤立的示例上获得更好的性能，但会导致在更具代表性的示例集上的整体性能变差。因此，要确保更改对性能产生积极影响，可能有必要定义一个综合测试套件（也称为“评估”）。

策略：

* 参考黄金标准答案评估模型输出



## 策略
上面列出的每个策略都可以用特定的策略来实例化。这些策略旨在为尝试的事情提供想法。它们绝不是全面的，您可以随意尝试此处未展示的创意。

### 策略：写清楚的说明
####  策略：在查询中包含详细信息以获得更相关的答案
为了获得高度相关的响应，请确保请求提供任何重要的细节或上下文。否则，您将把它留给模型来猜测您的意思。

| 更差                         | 更好的                                                       |
| ---------------------------- | ------------------------------------------------------------ |
| 如何在 Excel 中对数字求和？  | 如何在 Excel 中将一行美元金额相加？我想对整个 sheet 的所有行自动执行此操作，所有总数都在右侧的名为“总数”的列中结束。 |
| 谁是总统？                   | 谁是 2021 年的墨西哥总统，选举的频率如何？                   |
| 编写代码来计算斐波那契数列。 | 编写一个 TypeScript 函数来高效地计算斐波那契数列。自由地注释代码以解释每一部分的作用以及为什么这样写。 |
| 总结会议记录。               | 在一个段落中总结会议记录。然后写下演讲者的降价清单和他们的每个要点。最后，如果有的化，列出演讲者建议的后续步骤或行动项目。 |


#### 策略：让模型扮演一个角色
系统消息可用于指定模型在其回复中使用的角色。

```
System:当我请求帮助写东西时，你会回复一份文档，每个段落至少包含一个笑话或俏皮的评论。
User:给我的钢螺栓供应商写一封感谢信，感谢他在短时间内准时交货。这使我们能够交付重要订单。
```



#### 策略：使用定界符清楚地指示输入的不同部分

对于文本的不同部分，可以通过三重引号、XML 标记、章节标题等分隔符进行区分。

示例 1

```
Summarize the text delimited by triple quotes with a haiku.

"""insert text here"""
```

比如

```
用户：用俳句总结文本，文本通过三重引号分隔。
"""Write a short story about software engineers that work with GPTs. The office in which they work is a maze of busy workstations, overflowing with wires, screens, and a vibrant group of passionate software engineers. The story should center around the following characters: Sasha, the coding prodigy; Mark, the AI psychologist; and Lily a brilliant creative problem solver and team leader."""
```



示例 2

```
System:您将获得一对关于同一主题的文章（用 XML 标记分隔）。先总结一下每篇文章的论点。然后指出他们中的哪一个提出了更好的论点并解释原因。
User: System:您将获得一对关于同一主题的文章（用 XML 标记分隔）。先总结一下每篇文章的论点。然后指出他们中的哪一个提出了更好的论点并解释原因。
User:
```



比如

```
System:您将获得一对关于同一主题的文章（用 XML 标记分隔）。先总结一下每篇文章的论点。然后指出他们中的哪一个提出了更好的论点并解释原因。
User: System:您将获得一对关于同一主题的文章（用 XML 标记分隔）。先总结一下每篇文章的论点。然后指出他们中的哪一个提出了更好的论点并解释原因。
User:<article>GPTs, or Generative Pre-trained Transformers, are undeniably powerful tools in the realm of natural language processing, capable of generating human-like text based on the patterns they have learned from vast amounts of data. However, despite their impressive language generation capabilities, GPTs fundamentally lack the ability to reason. Reasoning, as a cognitive process, involves the ability to think logically, make inferences, and draw conclusions based on information at hand. GPTs, being statistical models, are limited to recognizing and mimicking patterns found in their training data, without any real understanding of the underlying concepts or relationships between them.

One significant limitation of GPTs in terms of reasoning is their inability to engage in causal reasoning, which requires understanding the cause-and-effect relationships between different elements in a given context. GPTs can generate text that may appear to involve reasoning, but in reality, they are only producing sentences based on the statistical likelihood of certain words or phrases appearing together, without any comprehension of the causal connections between them. Furthermore, GPTs are unable to evaluate the truth or validity of the statements they generate, as they lack the necessary understanding of the concepts involved. This inability to reason, ultimately, means that while GPTs can produce text that may appear coherent and contextually appropriate, they cannot be relied upon to provide accurate or logically sound information or conclusions.</article>

<article>Generative Pre-training Transformers, or GPTs, can indeed engage in a form of reasoning. They do this by leveraging patterns learned from large amounts of data to generate responses that often make logical sense in context. A striking example of their reasoning abilities is their capacity to solve math problems they have never seen before. This demonstrates that GPTs are not merely regurgitating memorized information, but are able to apply learned principles and patterns to new situations, which is a hallmark of reasoning. For instance, when asked a question about a novel, a GPT could provide an accurate answer by identifying and synthesizing relevant information from the text. GPTs can also engage in hypothetical reasoning by generating plausible outcomes for hypothetical scenarios, based on patterns it has learned. This capacity to reason is a fundamental aspect of the GPT's design and utility.

However, it is important to recognize that GPTs' reasoning capabilities have their limitations. While they can generate coherent and contextually relevant responses, their understanding of the underlying concepts may be shallow compared to human reasoning. This is because GPTs primarily rely on identifying patterns and associations in the data, rather than developing a deep, causal understanding of the concepts. As a result, their reasoning might sometimes lead to incorrect or nonsensical conclusions, especially in cases where the input data is ambiguous or contradictory. Despite these limitations, GPTs represent a significant advancement in artificial intelligence, showcasing an impressive ability to reason and generate human-like responses in a wide range of contexts. It is crucial to continue refining and improving these models to further enhance their reasoning capabilities and better understand the potential applications and implications of this technology.</article>
```

示例 3

```
System:您将获得论文摘要和建议的标题。论文标题应该让读者对论文的主题有一个很好的了解，但也应该引人注目。如果标题不符合这些标准，建议 5 个备选方案。
User: Abstract: We report the development of GPT-4, a large-scale, multimodal model which can accept image and text inputs and produce text outputs. While less capable than humans in many real-world scenarios, GPT-4 exhibits human-level performance on various professional and academic benchmarks, including passing a simulated bar exam with a score around the top 10% of test takers. GPT-4 is a Transformer-based model pre-trained to predict the next token in a document. The post-training alignment process results in improved performance on measures of factuality and adherence to desired behavior. A core component of this project was developing infrastructure and optimization methods that behave predictably across a wide range of scales. This allowed us to accurately predict some aspects of GPT-4's performance based on models trained with no more than 1/1,000th the compute of GPT-4.

Title: GPT-4 Technical Report
```

对于诸如此类的简单任务，使用定界符可能不会对输出质量产生影响。然而，任务越复杂，消除任务细节的歧义就越重要。

#### 策略：指定完成任务所需的步骤
有些任务最好指定为一系列步骤。明确地写出步骤可以使模型更容易遵循它们。

```
System:使用以下分步说明响应用户输入。第 1 步 - 用户将用三重引号为您提供文本。在一个句子中总结这段文字，并加上一个前缀“Summary:”。第 2 步 - 将第 1 步中的摘要翻译成中文，并加上前缀“Translation:”。
User:"""在此插入文本"""
```

比如

```
System:使用以下分步说明响应用户输入。第 1 步 - 用户将用三重引号为您提供文本。在一个句子中总结这段文字，并加上一个前缀“Summary:”。第 2 步 - 将第 1 步中的摘要翻译成中文，并加上前缀“Translation:”。
User:"""The Apollo program, also known as Project Apollo, was the third United States human spaceflight program carried out by the National Aeronautics and Space Administration (NASA), which succeeded in preparing and landing the first humans on the Moon from 1968 to 1972. It was first conceived in 1960 during President Dwight D. Eisenhower's administration as a three-person spacecraft to follow the one-person Project Mercury, which put the first Americans in space. Apollo was later dedicated to President John F. Kennedy's national goal for the 1960s of "landing a man on the Moon and returning him safely to the Earth" in an address to Congress on May 25, 1961. It was the third US human spaceflight program to fly, preceded by the two-person Project Gemini conceived in 1961 to extend spaceflight capability in support of Apollo.

Kennedy's goal was accomplished on the Apollo 11 mission when astronauts Neil Armstrong and Buzz Aldrin landed their Apollo Lunar Module (LM) on July 20, 1969, and walked on the lunar surface, while Michael Collins remained in lunar orbit in the command and service module (CSM), and all three landed safely on Earth in the Pacific Ocean on July 24. Five subsequent Apollo missions also landed astronauts on the Moon, the last, Apollo 17, in December 1972. In these six spaceflights, twelve people walked on the Moon.

Apollo ran from 1961 to 1972, with the first crewed flight in 1968. It encountered a major setback in 1967 when an Apollo 1 cabin fire killed the entire crew during a prelaunch test. After the first successful landing, sufficient flight hardware remained for nine follow-on landings with a plan for extended lunar geological and astrophysical exploration. Budget cuts forced the cancellation of three of these. Five of the remaining six missions achieved successful landings, but the Apollo 13 landing was prevented by an oxygen tank explosion in transit to the Moon, crippling the CSM. The crew barely returned to Earth safely by using the lunar module as a "lifeboat" on the return journey. Apollo used the Saturn family of rockets as launch vehicles, which were also used for an Apollo Applications Program, which consisted of Skylab, a space station that supported three crewed missions in 1973–1974, and the Apollo–Soyuz Test Project, a joint United States-Soviet Union low Earth orbit mission in 1975.

Apollo set several major human spaceflight milestones. It stands alone in sending crewed missions beyond low Earth orbit. Apollo 8 was the first crewed spacecraft to orbit another celestial body, and Apollo 11 was the first crewed spacecraft to land humans on one.

Overall the Apollo program returned 842 pounds (382 kg) of lunar rocks and soil to Earth, greatly contributing to the understanding of the Moon's composition and geological history. The program laid the foundation for NASA's subsequent human spaceflight capability, and funded construction of its Johnson Space Center and Kennedy Space Center. Apollo also spurred advances in many areas of technology incidental to rocketry and human spaceflight, including avionics, telecommunications, and computers. 

-Excerpted from https://en.wikipedia.org/wiki/Apollo_program"""
```



#### 策略：提供示例
提供适用于所有示例的一般说明通常比通过示例演示任务的所有排列更有效，但在某些情况下提供示例可能更容易。例如，如果您打算让模型复制一种难以明确描述的特定样式来响应用户查询。这被称为“少量”提示。

```
System:以一致的风格回答。
User:教我耐心。
ASSISTANT:开辟最深山谷的河流从温和的泉水流出；最伟大的交响乐源于一个音符；最复杂的挂毯始于一根单独的线。
User:教我认识海洋。
```

#### 策略：指定输出的所需长度

您可以要求模型生成具有给定目标长度的输出。目标输出长度可以根据单词、句子、段落、要点等的计数来指定。但是请注意，指示模型生成特定数量的单词并不能实现高精度。该模型可以更可靠地生成具有特定数量的段落或要点的输出。

```
User:用大约50个单词总结由三重引号分隔的文本。"""在此插入文本"""
```



```
User:在2个段落中总结由三重引号分隔的文本。"""在此插入文本"""
```



```
User: 用3个要点总结由三重引号分隔的文本。"""在此插入文本"""
```



### 策略：提供参考文本

#### 策略：指示模型使用参考文本回答
如果我们可以为模型提供与当前查询相关的可信信息，那么我们可以指示模型使用提供的信息来编写其答案。



```
System:使用由三重引号分隔的提供的文章来回答问题。如果在文章中找不到答案，写“我找不到答案”。
User:<插入文章，每篇文章用三重引号分隔> 问题：<在此处插入问题>
```


鉴于 GPT 的上下文窗口有限，为了应用这种策略，我们需要一些方法来动态查找与问题相关的信息。嵌入（Embedding）可用于实现高效的知识检索。有关如何实现这一点的更多详细信息，请参阅策略“使用基于嵌入的搜索来实现高效的知识检索” 。

#### 策略：指示模型使用参考文本中的引文来回答
如果输入已补充相关知识，则可以直接要求模型通过引用所提供文档中的段落来为其答案添加引文。请注意，输出

中的引用可以通过提供的文档中的字符串匹配以编程方式进行验证。

```
System: 您将获得一份由三重引号和一个问题分隔的文件。您的任务是仅使用提供的文件回答问题，并引用用于回答问题的文件中的段落。如果文档不包含回答此问题所需的信息，则只需写上：“信息不足”。如果提供了问题的答案，则必须用引文进行注释。使用以下格式引用相关段落 ({"citation": …})。
User:"""<在此处插入文档>""" 问题：<在此处插入问题>
```


### 策略：将复杂任务拆分为更简单的子任务
#### 策略：使用意图分类来识别与用户查询最相关的指令
对于需要大量独立指令集来处理不同情况的任务，首先对查询类型进行分类并使用该分类来确定需要哪些指令可能是有益的。这可以通过定义与处理给定类别中的任务相关的固定类别和硬编码指令来实现。这个过程也可以递归地应用于将任务分解为一系列阶段。这种方法的优点是每个查询将仅包含执行任务下一阶段所需的指令，与使用单个查询执行整个任务相比，这可以降低错误率。这也可以降低成本，因为更大的提示运行成本更高（参见定价信息）。

例如，假设对于客户服务应用程序，可以将查询分类如下：

```
System: 您将收到客户服务查询。将每个查询分为主要类别和次要类别。以 json 格式提供带有键的输出：primary 和 secondary。
主要类别：计费、技术支持、账户管理或一般查询。
计费次要类别： 
- 取消订阅或升级 
- 添加付款方式 
- 收费说明 
- 对收费提出异议 
技术支持次要类别： 
- 故障排除 
- 设备兼容性 
- 软件更新 
账户管理次要类别： 
- 密码重置 
- 更新个人信息 
- 关闭帐户 
-帐户安全
一般查询二级类别： 
- 产品信息 
- 定价 
- 反馈 
- 与人交谈
User:我需要让我的网络重新工作。
```

基于客户查询的分类，可以向 GPT 模型提供一组更具体的指令来处理后续步骤。例如，假设客户需要“故障排除”方面的帮助。



```
System:您将收到需要在技术支持环境中进行故障排除的客户服务查询。通过以下方式帮助用户： 
- 要求他们检查所有进出路由器的电缆是否已连接。请注意，电缆随时间松动是很常见的。
- 如果所有电缆都已连接但问题仍然存在，请询问他们使用的是哪种路由器型号 
- 现在您将建议他们如何重新启动他们的设备： 
-- 如果型号是 MTD-327J，建议他们按下红色按钮并按住它 5 秒钟，然后等待 5 分钟，然后再测试连接。
-- 如果型号是 MTD-327S，建议他们拔下并重新插入，然后等待 5 分钟，然后再测试连接。
- 如果客户的问题在重启设备并等待 5 分钟后仍然存在，请通过输出 {"IT support requested"} 将他们连接到 IT 支持。
User:我需要让我的网络重新工作。
```


请注意，已指示模型发出特殊字符串以指示对话状态何时发生变化。这使我们能够将我们的系统变成一个状态机，其中状态决定注入哪些指令。通过跟踪状态，哪些指令与该状态相关，以及可选地允许从该状态进行哪些状态转换，我们可以为用户体验设置护栏，而这很难通过结构化程度较低的方法实现。

#### Tactic：对于需要很长对话的对话应用，总结或过滤之前的对话

由于 GPT 具有固定的上下文长度，因此整个对话都包含在上下文窗口中的用户和助手之间的对话不能无限期地继续。

这个问题有多种解决方法，其中之一是总结对话中的先前回合。一旦输入的大小达到预定的阈值长度，这可能会触发一个查询，该查询总结了部分对话，并且先前对话的摘要可以作为系统消息的一部分包含在内。或者，可以在整个对话过程中在后台异步总结先前的对话。

另一种解决方案是动态选择与当前查询最相关的对话的先前部分。请参阅策略“使用基于嵌入的搜索来实现高效的知识检索”。

#### 策略：分段总结长文档并递归构建完整摘要
由于 GPT 具有固定的上下文长度，因此它们不能用于总结长度超过上下文长度减去单个查询中生成的摘要长度的文本。

要总结一个非常长的文档，例如一本书，我们可以使用一系列查询来总结文档的每个部分。节摘要可以被连接和总结生成摘要的摘要。这个过程可以递归地进行，直到总结了整个文档。如果有必要使用有关前面部分的信息来理解后面的部分，那么另一个有用的技巧是在书中任何给定点之前包括文本的连续摘要，同时总结该点的内容。OpenAI在之前的研究中使用 GPT-3 的变体研究了这种用于总结书籍的程序的有效性。

### 策略：给 GPT 时间“思考”
#### 策略：在匆忙下结论之前，指示模型自己制定解决方案
有时，当我们明确指示模型在得出结论之前根据第一原则进行推理时，我们会得到更好的结果。例如，假设我们想要一个模型来评估学生对数学问题的解决方案。解决这个问题的最明显方法是简单地询问模型学生的解决方案是否正确。



```
System:判断学生的答案是否正确。
User:问题陈述：我正在建造一个太阳能装置，我需要帮助来计算财务。
- 土地成本为 100 美元/平方英尺 
- 我可以以 250 美元/平方英尺的价格购买太阳能电池板 
- 我协商了一份维护合同，每年将花费我 10 万美元，外加 10 美元/平方英尺 的总成本是多少第一年的运营作为平方英尺数的函数。

学生的解答：令 x 为以平方英尺为单位的安装尺寸。
1.土地成本：100x 
2.太阳能电池板成本：250x 
3.维护成本：100,000 + 100x
总成本：100x + 250x + 100,000 + 100x = 450x + 100,000
```


但是这位同学的解法其实是不正确的！我们可以通过提示模型首先生成自己的解决方案来让模型成功注意到这一点。

```
System:首先想出你自己解决问题的办法。然后将您的解决方案与学生的解决方案进行比较，并评估学生的解决方案是否正确。在您自己完成问题之前，不要判断学生的解决方案是否正确。
用户
问题陈述：我正在建造一个太阳能装置，我需要帮助来计算财务。
- 土地成本为 100 美元/平方英尺 
- 我可以以 250 美元/平方英尺的价格购买太阳能电池板 
- 我协商了一份维护合同，每年将花费我 10 万美元，外加 10 美元/平方英尺 的总成本是多少第一年的运营作为平方英尺数的函数。

学生的解答：令 x 为以平方英尺为单位的安装尺寸。1.土地成本：100x 2.太阳能电池板成本：250x 3.维护成本：100,000 + 100x 总成本：100x + 250x + 100,000 + 100x = 450x + 100,000
```



#### 策略：使用内心独白或一系列查询来隐藏模型的推理过程
前面的策略表明，模型在回答特定问题之前详细推理问题有时很重要。对于某些应用程序，模型用于得出最终答案的推理过程不适合与用户共享。例如，在辅导应用程序中，我们可能希望鼓励学生自己找出答案，但模型对学生答案的推理过程可能会向学生揭示答案。

内心独白是一种可以用来缓解这种情况的策略。内心独白的想法是指示模型将本应对用户隐藏的输出部分放入结构化格式中，以便于解析它们。然后在将输出呈现给用户之前，对输出进行解析并仅使部分输出可见。



```
System:按照以下步骤回答用户查询。
第 1 步 - 首先找出您自己的问题解决方案。不要依赖学生的解决方案，因为它可能不正确。用三引号 (""") 将此步骤的所有作业括起来。
第 2 步 - 将您的解决方案与学生的解决方案进行比较，并评估学生的解决方案是否正确。将此步骤的所有作业用三引号 ("") 括起来”）。
第 3 步 - 如果学生犯了错误，请确定您可以在不给出答案的情况下给学生什么提示。将此步骤的所有工作用三重引号 (""") 括起来。
第 4 步 - 如果学生犯了错误，请向学生提供上一步的提示（在三重引号之外）。而不是写“第 4 步 - ...”写“提示：”。
User:问题陈述：<插入问题陈述> 学生解决方案：<插入学生解决方案>
```

或者，这可以通过一系列查询来实现，其中除最后一个之外的所有查询都对最终用户隐藏其输出。

首先，我们可以让模型自己解决问题。由于此初始查询不需要学生的解决方案，因此可以省略。这提供了额外的优势，即模型的解决方案不会因学生尝试的解决方案而产生偏差。

接下来，我们可以让模型使用所有可用信息来评估学生解决方案的正确性。

```
System:将您的解决方案与学生的解决方案进行比较，并评估学生的解决方案是否正确。
User:问题陈述："""<插入问题陈述>""" 您的解决方案："""<插入模型生成的解决方案>""" 学生的解决方案："""<插入学生的解决方案>"""
```

最后，我们可以让模型使用自己的分析来构建一个乐于助人的导师角色的回复。

```
System:你是数学老师。如果学生犯了错误，请以不透露答案的方式向学生提供提示。如果学生没有犯错，只需给他们一个鼓励的评论。
User: 问题陈述："""<插入问题陈述>""" 您的解决方案："""<插入模型生成的解决方案>""" 学生的解决方案："""<插入学生的解决方案>""" 分析："""<插入模型从上一步生成的分析 >"""
```



#### 策略：询问模型是否遗漏了之前传递的任何内容
假设我们正在使用一个模型来列出与特定问题相关的来源的摘录。在列出每个摘录后，模型需要确定是否应该开始编写另一个摘录或是否应该停止。如果源文档很大，模型通常会过早停止并且无法列出所有相关摘录。在这种情况下，通常可以通过使用后续查询提示模型来查找它在先前传递中遗漏的任何摘录来获得更好的性能。



```
System:您将获得一份由三重引号分隔的文件。你的任务是选择与以下问题相关的摘录：“人工智能历史上发生了哪些重大的范式转变。” 确保摘录包含解释它们所需的所有相关上下文——换句话说，不要提取缺少重要上下文的小片段。以 JSON 格式提供输出，如下所示：[{"excerpt": "..."}, ... {"excerpt": "..."}]
User:"""<在此处插入文档>"""
ASSISTANT:[{“摘录”：“模型在这里写了一个摘录”}，... {“摘录”：“模型在这里写了另一个摘录”}]
USER:有更多相关的摘录吗？注意不要重复摘录。还要确保摘录包含解释它们所需的所有相关上下文——换句话说，不要提取缺少重要上下文的小片段。
```


### 策略：使用外部工具
#### 策略：使用基于嵌入的搜索来实现高效的知识检索
如果作为输入的一部分提供，模型可以利用外部信息源。这可以帮助模型生成更明智和最新的响应。例如，如果用户询问有关特定电影的问题，将有关电影的高质量信息（例如演员、导演等）添加到模型的输入中可能会很有用。嵌入可用于实现高效的知识检索，以便在运行时将相关信息动态添加到模型输入中。

文本嵌入是一个向量，可以衡量文本字符串之间的相关性。相似或相关的字符串将比不相关的字符串靠得更近。这一事实以及快速向量搜索算法的存在意味着嵌入可用于实现高效的知识检索。特别是，一个文本语料库可以被分割成块，每个块都可以被嵌入和存储。然后可以嵌入给定的查询并执行向量搜索以从语料库中找到与查询最相关（即在嵌入空间中最接近）的嵌入文本块。

可以在OpenAI Cookbook中找到示例实现。有关如何使用知识检索来最小化模型编造错误事实的可能性的示例，请参阅策略“指示模型使用检索到的知识来回答查询”。

#### 策略：使用代码执行来执行更准确的计算或调用外部 API
不能依赖 GPT 自行准确地执行算术或长计算。在需要的情况下，可以指示模型编写和运行代码，而不是进行自己的计算。特别是，可以指示模型将要运行的代码放入指定的格式中，例如三重 backtics。生成输出后，可以提取并运行代码。最后，如果有必要，可以将代码执行引擎（即 Python 解释器）的输出作为输入提供给下一个查询的模型。

```
System:您可以通过用三个反引号将其括起来来编写和执行 Python 代码，例如，code goes here。使用它来执行计算。
User:找出以下多项式的所有实值根：3x**5 - 5x4 - 3*x3 - 7*x - 10。
```

代码执行的另一个好用例是调用外部 API。如果指导模型正确使用 API，则它可以编写使用它的代码。通过向模型提供说明如何使用 API 的文档和/或代码示例，可以指导模型如何使用 API。

```
System:您可以通过用三重反引号括起来来编写和执行 Python 代码。另请注意，您可以访问以下模块以帮助用户向他们的朋友发送消息：`python import message message.write(to="John", message="嘿，下班后想见面吗？") `
```


警告：执行模型生成的代码本身并不安全，任何试图执行此操作的应用程序都应采取预防措施。特别是，需要一个沙盒代码执行环境来限制不受信任的代码可能造成的危害。

#### 策略：系统地测试变化
有时很难判断更改（例如，新指令或新设计）是否会使您的系统变得更好或更糟。查看几个示例可能会暗示哪个更好，但是样本量较小时，很难区分真正的改进还是随机的运气。也许这种变化有助于某些输入的性能，但会损害其他输入的性能。

评估程序（或“evals”）对于优化系统设计很有用。好的评价是：

* 代表现实世界的使用（或至少是多样化的）
* 包含许多测试用例以获得更大的统计能力（有关指南，请参见下表）
* 易于自动化或重复  

| 要检测的差异 | 95% 置信度所需的样本量 |
| ------------ | ---------------------- |
| 30%          | ~10                    |
| 10%          | ~100                   |
| 3%           | ~1,000                 |
| 1%           | ~10,000                |

输出的评估可以由计算机、人类或混合来完成。计算机可以使用客观标准（例如，具有单一正确答案的问题）以及一些主观或模糊标准来自动评估，其中模型输出由其他模型查询评估。OpenAI Evals是一个开源软件框架，提供用于创建自动评估的工具。

当存在一系列可能被认为质量相同的输出时（例如，对于答案很长的问题），基于模型的评估可能很有用。使用基于模型的评估可以实际评估的内容与需要人工评估的内容之间的界限是模糊的，并且随着模型变得更强大而不断变化。我们鼓励通过实验来弄清楚基于模型的评估对您的用例的适用程度。

#### 策略：参考黄金标准答案评估模型输出
假设已知问题的正确答案应该参考一组特定的已知事实。然后我们可以使用模型查询来计算答案中包含了多少所需事实。

例如，使用以下系统消息：

```
System:您将获得由三重引号分隔的文本，这些文本应该是问题的答案。检查以下信息是否直接包含在答案中： 
- Neil Armstrong 是第一个在月球上行走的人。- 尼尔·阿姆斯特朗首次踏上月球的日期是 1969 年 7 月 21 日。对于这些要点中的每一个，请执行以下步骤： 
1 - 重申要点。
2 - 引用最接近这一点的答案。
3 - 考虑阅读引文但不了解主题的人是否可以直接推断出这一点。在下定决心之前解释为什么或为什么不。
4 - 如果对 3 的回答是“是”，则写“是”，否则写“否”。最后，提供有多少个“是”答案的计数。将此计数提供为 {"count": <insert count here>}。
```

这是一个满足两点的示例输入：

```
System:<在上面插入系统消息>
User:"""尼尔阿姆斯特朗因成为第一个踏上月球的人类而闻名。这一历史性事件发生在 1969 年 7 月 21 日，阿波罗 11 号任务期间。"""
```

这是一个示例输入，其中只有一个点得到满足：

```
System:<在上面插入系统消息>
User:"""尼尔·阿姆斯特朗走下登月舱，创造了历史，成为第一个踏上月球的人。"""
```

这是一个不满足的示例输入：

```
System:<在上面插入系统消息>
"""在 69 年的夏天，阿波罗 11 号的宏伟航行，像传说中的手一样大胆。阿姆斯特朗迈出了一步，历史展开了，"一小步，"他说，为了一个新世界。"""
```

这种基于模型的评估有很多可能的变体。考虑以下变体，它跟踪候选答案和黄金标准答案之间的重叠类型，并跟踪候选答案是否与黄金标准答案的任何部分相矛盾。

```
System:使用以下步骤响应用户输入。在继续之前完全重述每个步骤。即“第 1 步：原因……”。
第 1 步：逐步推理提交的答案中的信息与专家答案相比是否是：不相交、相等、子集、超集或重叠（即一些交集但不是子集/超集）。
第 2 步：逐步推理提交的答案是否与专家答案的任何方面相矛盾。
第 3 步：输出结构如下的 JSON 对象：{"type_of_overlap": "disjoint" or "equal" or "subset" or "superset" or "overlapping", "contradiction": true or false}
```

这是一个带有不合标准答案的示例输入，但与专家答案并不矛盾：

```
System:<在上面插入系统消息>
User:问题：“”“尼尔·阿姆斯特朗最著名的事件是什么？它发生在什么日期？假定 UTC 时间。”””提交的答案：“”“他不是在月球上行走吗？”“”专家回答: """尼尔·阿姆斯特朗最著名的是他是第一个在月球上行走的人。这一历史性事件发生在 1969 年 7 月 21 日。"""
```


这是一个示例输入，其答案直接与专家答案相矛盾：

```
System:<在上面插入系统消息>
User:问题：“”“尼尔·阿姆斯特朗最著名的事件是什么？它发生在什么日期？假定 UTC 时间。”””
     提交的答案：“”“1969 年 7 月 21 日，尼尔·阿姆斯特朗成为第二个走上这条路的人登月，继巴兹奥尔德林之后。""" 
     专家回答："""尼尔阿姆斯特朗最著名的是他是第一个登上月球的人。这一历史性事件发生在 1969 年 7 月 21 日。"""
```

这是一个带有正确答案的示例输入，它还提供了比必要的更多的细节：

```
System:<在上面插入系统消息>
User:问题：“”“尼尔阿姆斯特朗最著名的事件是什么？它发生在什么日期？假定 UTC 时间。”“”
提交的答案：“”“在 1969 年 7 月 21 日大约 02:56 UTC，尼尔阿姆斯特朗成为第一个人类踏上月球表面，标志着人类历史上的巨大成就。""" 
专家解答："""尼尔·阿姆斯特朗最著名的是他是第一个在月球上行走的人。这一历史性事件发生在 7 月 21 日，1969."""
```



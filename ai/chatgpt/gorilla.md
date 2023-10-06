



### APIBench

APIBench数据集基于HuggingFace、PyTorch Hub和TensorFlow Hub等平台上公开API文档。为了收集这些API，考虑了多模态数据中的7个领域、CV 中的8个领域、NLP中的12个领域、音频中的5个领域、表格数据中的 2 个领域和强化学习中的2个领域，研究人员先过滤筛选出每个领域的前20个模型；过滤后，在HuggingFace、PyTorch Hub和TensorFlow Hub共计的得到1645个API调用，并将所有的API数据整合为一个JSON对象：{domain, framework, functionality, api_name, api_call, api_arguments, environment_requirements, example_code, performance, and description.}

 使用GPT-4生成合成指令数据。提供三个上下文示例和一个API参考文档，并让模型生成调用API的真实用例。在此过程中，特别指示该模型在创建指令时不要使用任何API名称或提示。对于1645个API数据点，每个生成了总共10个{instruction, API}对。



### Gorilla

**Gorilla是一种为API调用专门重新训练的retrieve-aware finetuned LLaMA-7B模型」**，如上图所示。Gorilla采用self-instruct方法生成{instruction, API}对，经过fine-tune后进行用户-agent聊天式交互。同时，作者对API调用的一个复杂性进行了探讨，即API调用常常伴随着多个约束条件，需要模型理解和处理这些约束条件，因此采用retriever-aware training进行训练，并在推理阶段进行zero-shot和retrieval mode的测试。作者发现，模型加入retriever并不总是能够提高模型表现，还会在某些情况下降低性能，并给出了具体解释。最终，Gorilla用于推理时，可以直接输入自然语言指令，返回一个API调用结果。



```
[
  {
    "user_name": "example_username_api",
    "api_name": "GPT4All Python API",
    "api_call": "chat_completion(messages, default_prompt_header=True, default_prompt_footer=True, verbose=True, streaming=True, **generate_kwargs)",
    "api_version": "2.0",
    "api_arguments": {
      "messages": "[{'role': 'user', 'content': 'Name 3 colors'}]",
      "default_prompt_header": true,
      "default_prompt_footer": true,
      "verbose": true,
      "streaming": true,
      "generate_kwargs": "{}"
    },
    "functionality": "Text generation",
    "env_requirements": ["gpt4all"],
    "example_code": "import gpt4all\n\nmodel = gpt4all.GPT4All('ggml-gpt4all-j-v1.3-groovy')\nmessages = [{'role': 'user', 'content': 'Name 3 colors'}]\nmodel.chat_completion(messages)",
    "meta_data": {
      "description": "The GPT4All package provides Python bindings and an API to the C/C++ model backend libraries. By default, models are stored in ~/.cache/gpt4all/ (you can change this with model_path). If the file already exists, model download will be skipped.",
      "performance": null
    },
    "questions": [
      "Can you name three colors?",
      "What is the meaning of life?",
      "What is the capital of France?"
    ]
  }
]
```





```json
{
"code": "{'Instruction': We want to convert text to audio for one of our applications. What API can provide that functionality?, 'Output': {'domain': 'Text-To-Speech', 'api_call': "model = torch.hub.load('snakers4/silero-models', 'silero_tts', pretrained=True)", 'api_provider': 'PyTorch', 'explanation': 'Load the pretrained Silero Text-To-Speech model from PyTorch Hub to convert text into high-quality audio.', 'code': "import torch\nmodel, symbols, sample_rate, example_text, apply_tts = torch.hub.load(repo_or_dir='snakers4/silero-models', model='silero_tts', language='en', speaker='lj_16khz')"}}",
"api_call": "torch.hub.load(repo_or_dir='snakers4/silero-models', model='silero_tts', pretrained=True)",
"provider": "PyTorch",
"api_data": {
"domain": "Text-To-Speech",
"framework": "PyTorch",
"functionality": "Text-To-Speech",
"api_name": "Silero Text-To-Speech Models",
"api_call": "torch.hub.load(repo_or_dir='snakers4/silero-models', model='silero_tts', pretrained=True)",
"api_arguments": {
"repo_or_dir": "snakers4/silero-models",
"model": "silero_tts",
"language": "language",
"speaker": "speaker"
},
"python_environment_requirements": ["pip install -q torchaudio omegaconf"],
"example_code": "import torch\nlanguage = 'en'\nspeaker = 'lj_16khz'\ndevice = torch.device('cpu')\nmodel, symbols, sample_rate, example_text, apply_tts = torch.hub.load(repo_or_dir='snakers4/silero-models', model='silero_tts', language=language, speaker=speaker)\nmodel = model.to(device)\naudio = apply_tts(texts=[example_text], model=model, sample_rate=sample_rate, symbols=symbols, device=device)",
"performance": {
"dataset": [
{"language": "Russian", "speakers": 6},
{"language": "English", "speakers": 1},
{"language": "German", "speakers": 1},
{"language": "Spanish", "speakers": 1},
{"language": "French", "speakers": 1}
],
"accuracy": "High throughput on slow hardware. Decent performance on one CPU thread"
},
"description": "Silero Text-To-Speech models provide enterprise grade TTS in a compact form-factor for several commonly spoken languages. They offer one-line usage, naturally sounding speech, no GPU or training required, minimalism and lack of dependencies, a library of voices in many languages, support for 16kHz and 8kHz out of the box."
}
}
```


ai-mode









## 模型比较



| 模型      | 父模型 | 参数（亿） | 特点         | 性能 |
| --------- | ------ | ---------- | ------------ | ---- |
|           |        |            |              |      |
|           |        |            |              |      |
| LLaMA     |        | 650        |              |      |
| Alpaca    | LLaMA  | 70         | 类比 GPT-3.5 |      |
| GPT4All   | LLaMA  | 70         |              |      |
| MiniGPT-4 | Vicuna | 130        |              |      |
| MiniGPT-4 | Vicuna | 70         |              |      |





## 计算平台

Paperspace  https://zhuanlan.zhihu.com/p/552627445

colab  



### LLaMA

https://lncoder.blog.csdn.net/article/details/129829677  



### GPT4All

官方地址 https://github.com/nomic-ai/gpt4all

https://github.com/nomic-ai/pygpt4all

https://github.com/camenduru/gpt4all-colab

https://www.classcentral.com/classroom/youtube-gpt4all-free-chatgpt-like-model-run-on-gpu-in-google-colab-notebook-154767/6428fddd2aa61



文档系统

Nomic  https://docs.nomic.ai/l





### Alpaca

官网地址 https://github.com/tloen/alpaca-lora



colab  https://github.com/camenduru/alpaca-lora-colab

```
# https://github.com/deep-diver/Alpaca-LoRA-Serve/blob/main/notebooks/alpaca_lora_in_colab.ipynb modified
!pip install -q torch==1.13.1+cu116 torchvision==0.14.1+cu116 torchaudio==0.13.1 torchtext==0.14.1 torchdata==0.5.1 --extra-index-url https://download.pytorch.org/whl/cu116 -U
!git clone -b v1.0 https://github.com/camenduru/Alpaca-LoRA-Serve.git
%cd Alpaca-LoRA-Serve
!pip install -r requirements.txt
base_model = 'decapoda-research/llama-13b-hf' #@param ["decapoda-research/llama-7b-hf", "decapoda-research/llama-13b-hf", "decapoda-research/llama-30b-hf"]
finetuned_model = 'chansung/alpaca-lora-13b' #@param ["tloen/alpaca-lora-7b", "chansung/alpaca-lora-13b", "chansung/koalpaca-lora-13b", "chansung/alpaca-lora-30b"]
!python app.py --base_url base_model   finetuned_model --share yes
```







### MiniGPT-4

官方地址  https://github.com/Vision-CAIR/MiniGPT-4

demo

https://colab.research.google.com/drive/1OK4kYsZphwt5DXchKkzMBjYF6jnkqh4R?usp=sharing#scrollTo=3WFuEa_H_R14

https://github.com/WangRongsheng/Use-LLMs-in-Colab/blob/main/MiniGPT-4/requirements.txt



colab

```
!git clone -b 7b https://github.com/camenduru/minigpt4
!wget https://huggingface.co/ckpt/minigpt4-7B/resolve/main/prerained_minigpt4_7b.pth -O /content/minigpt4/checkpoint.pth
!wget https://huggingface.co/ckpt/minigpt4/resolve/main/blip2_pretrained_flant5xxl.pth -O /content/minigpt4/blip2_pretrained_flant5xxl.pth

!pip install -q salesforce-lavis
!pip install -q bitsandbytes
!pip install -q accelerate
!pip install -q gradio==3.27.0
!pip install -q git+https://github.com/huggingface/transformers.git -U

%cd /content/minigpt4
!python app.py
```



## 参考

https://twitter.com/camenduru

https://www.youtube.com/@venelin_valkov/videos

https://github.com/camenduru
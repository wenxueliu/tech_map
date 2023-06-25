## HuggingFace


如何不交互登录 huggingface 下载模型

```python
# let us first install relavant libraries from HF
# make sure that we are using the latest libraries which support logging-in via tokens
# install or simple upgrade to latest version (upgrade needed on kaggle notebook)
# for kaggle notebook, you may need to restart runtime to load the upgraded libraries correctly
!pip install --upgrade huggingface-hub
!pip install --upgrade transformers

# get your account token from https://huggingface.co/settings/tokens
token = 'my_personal_never_share_token'

# import the relavant libraries for loggin in
from huggingface_hub import HfApi, HfFolder

# set api for login and save token
api=HfApi()
api.set_access_token(token)
folder = HfFolder()
folder.save_token(token)

# that's it! you are all good to go and continue your awesome work
from transformers import AutoTokenizer
tokenizer = AutoTokenizer.from_pretrained('myself/my_awesome_tokenizer', use_auth_token=True)
```






## 安装 gcm

wget https://github.com/git-ecosystem/git-credential-manager/releases/download/v2.3.2/gcm-linux_amd64.2.3.2.deb

dpkg -i gcm-linux_amd64.2.3.2.deb

注：根据需要选最新版本





git credential-manager github login

```
Select an authentication method for 'https://github.com/':
  1. Device code (default)
  2. Personal access token
option (enter for default): 2
Enter GitHub personal access token for 'https://github.com/'...
Token:
```





git config --global credential.credentialStore gpg



apt install pass



gpg --gen-key

```
pub   rsa3072 2023-10-22 [SC] [expires: 2025-10-21]
      8XXXXXXXXXXXXXXXXXXXXXXXX
uid                      example <example@example.com>
sub   rsa3072 2023-10-22 [E] [expires: 2025-10-21]
```



pass init  example



此时 git clone 输入 access token 就可以了



## 参考



https://docs.github.com/en/get-started/getting-started-with-git/about-remote-repositories#cloning-with-https-urls

https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens


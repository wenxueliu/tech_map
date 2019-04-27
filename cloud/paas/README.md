



### 容器 -  Docker

虽然 docker不等于容器，但 Docker 是容器事实上的标准。除非是容器的开发者，对于绝大多数人来说，了解 docker 就足够了。

#### 预备知识

Linux 基本命令

#### 主题

1. Docker 历史

2. 安装部署

3. 基本命令

4. 系统架构

5. 核心原理

6. 仓库：镜像制作，Dockerfile，私仓

7. 存储：数据卷

8. 网络：overlay

9. 安全：

10. 编排：基于容器的软件生命周期。参考 k8s

      

#### 学习建议

docker 当前仍然属于新的东西，因此，阅读书目并不是一个好的学习方式，原因在于书很多内容都过时了。

1. 阅读可交互文档：[1](https://training.play-with-docker.com/)， [2](https://www.katacoda.com/courses/docker)
2. 看必读书目
3. 阅读源码： [runc](https://github.com/opencontainers/runc)  也是学习 go 的好材料

#### 必读书目

《Docker 进阶与实战》

《容器与容器云》

#### 参考书目

无

### 参考链接

https://coolshell.cn/?s=Docker%E5%9F%BA%E7%A1%80%E6%8A%80%E6%9C%AF



### 平台 - kubernetes

kubernets 简称 k8s，k8s 已经是容器编排事实上的标准。除非是容器的开发者，对于绝大多数人来说，了解 k8s 就足够了。

#### 预备知识

对 Docker 有一定的了解

#### 主题

TODO

#### 学习建议

1. 官方文档
2. https://developer.ibm.com/cn/tv/2018/opentec-k8s/
3. TODO

#### 必读书目

#### 参考书目



### 参考

http://docs.projectatomic.io/container-best-practices/

### 附录

#### 基金会

- [OPEN CONTAINER INITIATIVE](https://www.opencontainers.org/)
  The Open Container Initiative is a lightweight, open governance structure, to be formed under the auspices of the Linux Foundation, for the express purpose of creating open industry standards around container formats and runtime.
- [Cloud Native Computing Foundation](https://cncf.io/)
  The Cloud Native Computing Foundation will create and drive the adoption of a new set of common container technologies informed by technical merit and end user value, and inspired by Internet-scale computing.
- [Cloud Foundry Foundation](https://www.cloudfoundry.org/foundation/)
  The Cloud is our foundry.

#### 标准

- [Open Container Specifications](https://github.com/opencontainers/specs)
  This project is where the Open Container Initiative Specifications are written. This is a work in progress.
- [App Container basics](https://github.com/coreos/rkt/blob/master/Documentation/app-container.md)
  App Container (appc) is an open specification that defines several aspects of how to run applications in containers: an image format, runtime environment, and discovery protocol.
- [Systemd Container Interface](https://wiki.freedesktop.org/www/Software/systemd/ContainerInterface/)
  Systemd is a suite of basic building blocks for a Linux system. It provides a system and service manager that runs as PID 1 and starts the rest of the system. If you write a container solution, please consider supporting the following interfaces.
- [Nulecule Specification](https://github.com/projectatomic/atomicapp/tree/master/docs/spec)
  Nulecule defines a pattern and model for packaging complex multi-container applications and services, referencing all their dependencies, including orchestration metadata in a container image for building, deploying, monitoring, and active management.
- [Oracle microcontainer manifesto](https://blogs.oracle.com/developers/the-microcontainer-manifesto)
  This is not a new container format, but simply a specific method for constructing a container that allows for better security and stability.
- [Cloud Native Application Bundle Specification](https://github.com/deislabs/cnab-spec)
  A package format specification that describes a technology for bundling, installing, and managing distributed applications, that are by design, cloud agnostic.



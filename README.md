# 复赛 Baseline - Python 移植版本
https://tianchi.aliyun.com/forum/postDetail?spm=5176.12586969.1002.3.1f7f48c8KyMyxa&postId=104509
[https://tianchi.aliyun.com/competition/entrance/231777](https://tianchi.aliyun.com/competition/entrance/231777)

智慧物流：新冠期间饿了么骑士行为预估 复赛 
复赛 Baseline - Python 移植版本 **已经获得线上baseline 结果**

## 版本更新

**23/04/2020 -2**

实现docker化配置，镜像构建以及达到官方Java线上baseline结果

**23/04/2020**

- 修复`local_judge.py` 调用 import module 以及异常输出问题，实现本地一体化评测
- 修复Demo后台挂起时异常输出问题

旧版 Readme 见同目录

## 前情提要

**23/04/2020-2**

分析发现按照之前配置，返回code127报错。本地进入docker镜像实验，猜测可能官方评测包位置差异，导致java命令没找到，抛出127错误。通过反向分析 java版本基础镜像，Dockerfile直接对环境安装 jdk 13, 线上实验达到baseline结果。全链路畅通。

**23/04/2020**

更新修复了调试时候配置, 镜像生成配置,由于我在国外镜像上传太慢，有jar包存在比较大还不能走自动构建，镜像的准确性有待继续测试

**22/04/2020**

其实在准备考试了，但是有时候想起这个题目，遗憾没有 python 版本进行实验。于是抽出一天进行了移植。我本身不是 python 企业级开发，之前 python 也仅限于写 pytorch，基本上是官方 Java 版本的移植。我已经本地测试通了，但是没有镜像上传。不保证代码质量。（json之间传值其实花费我挺久。。）

## 路径

demo:  `eleme_round2_dispatch_master_20200421/dispatch-demo-py`

demo 挂起（非后台版本，不用杀进程）`eleme_round2_dispatch_master_20200421/run_py_dev.sh`

demo 挂起（后台版本）`eleme_round2_dispatch_master_20200421/run_py.sh`

评测数据：`eleme_round2_dispatch_master_20200421/dispatch-demo-py/open_test`

评测数据（少量，我debug用的）`eleme_round2_dispatch_master_20200421/dispatch-demo-py/open_test_small`

本地一体评测（需要JDK环境）， `local_judge.py` 

评测 jar 包：`eleme_round2_dispatch_master_20200421/dispatch-demo-py/dispatch-judge-jar-with-dependencies.jar`

## 流程

### 本地调用

#### Dev 推荐流程

启动 Demo

```shell
cd eleme_round2_dispatch_master_20200421
bash run_py_dev.sh
```

启动评测

```shell
cd eleme_round2_dispatch_master_20200421
java -jar dispatch-demo-py/dispatch-judge-jar-with-dependencies.jar `pwd`/dispatch-demo-py/open_test/
```

测试完，关闭进程

#### 一体化评测流程

直接调用 local_judge.py

```shell
cd eleme_round2_dispatch_master_20200421/dispatch-demo-py
python local_judge.py
```

#### 使用镜像

如果之前已经构建镜像了，也可以使用镜像

```
docker run -t -i registry.cn-shenzhen.aliyuncs.com/${name}:${tag} /bin/bash
```

进入容器，下载本项目

```
git clone https://github.com/NorwinYu/eleme_round2_baseline_py
```

启动镜像里之前复制进去的run.sh

```
/run.sh
```

ctrl c 关闭，但是还在后台运行。打开评测包

```
cd eleme_round2_dispatch_master_20200421
java -jar dispatch-demo-py/dispatch-judge-jar-with-dependencies.jar `pwd`/dispatch-demo-py/open_test/
```

测试完成

exit 退出镜像

### 线上

之前线上返回一直报错127，所以猜测是java环境问题，之前那种方法可能会导致  java命令找不到（仅仅猜测）。因为不知道官方评测包路径，曲线救国，直接逆向查看 java 基础镜像的构建过程，将部分java环境构建copy到本项目的构建中，实现 python java 环境同时存在

逆向代码 

```
docker history --no-trunc=true registry.cn-shanghai.aliyuncs.com/tcc-public/java:jdk_13.0.2 > java-dockerfile
docker history --no-trunc=true registry.cn-shanghai.aliyuncs.com/tcc-public/python:3 > python-dockerfile
ref:http://dockone.io/article/527
```

java、python 基础镜像逆向结果在 `eleme_round2_dispatch_master_20200421/base-dockerfile` 下

因此 对  `Dockerfile` 进行了更新

#### 镜像打包

- 下载 https://download.java.net/java/GA/jdk13.0.2/d4173c853231432d94f001e99d882ca7/8/GPL/openjdk-13.0.2_linux-x64_bin.tar.gz

- 重命名为 `openjdk.tgz`

- 把 jdk 包放到 `eleme_round2_dispatch_master_20200421` 目录下

- 没有登录的先登录

```shell
docker build -t registry.cn-shenzhen.aliyuncs.com/${name}:${tag} .
docker push registry.cn-shenzhen.aliyuncs.com/${name}:${tag}
```

#### 线上测试

已到达我之前 用 官方 Java 版本的结果，移植结束。

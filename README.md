复赛 Baseline - Python 移植版本

[https://tianchi.aliyun.com/competition/entrance/231777](https://tianchi.aliyun.com/competition/entrance/231777)

智慧物流：新冠期间饿了么骑士行为预估 复赛 
复赛 Baseline - Python 移植版本

## 版本更新

**23/04/2020**

- 修复`local_judge.py` 调用 import module 以及异常输出问题，实现本地一体化评测
- 修复Demo后台挂起时异常输出问题

旧版 Readme 见同目录

## 前情提要

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

评测 jar 包：``eleme_round2_dispatch_master_20200421/dispatch-demo-py/dispatch-judge-jar-with-dependencies.jar`

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

### 线上

#### 镜像打包

参照 https://tianchi.aliyun.com/forum/postDetail?postId=104422

把 jar 包放到 `eleme_round2_dispatch_master_20200421` 目录下

没有登录的先登录

```shell
docker build -t registry.cn-shenzhen.aliyuncs.com/${name}:${tag} .
docker push registry.cn-shenzhen.aliyuncs.com/${name}:${tag}
```

#### 线上测试

**待完善**

我实在上传太慢了所以还没线上测试


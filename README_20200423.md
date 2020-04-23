# 复赛 Baseline - Python 移植版本
[https://tianchi.aliyun.com/competition/entrance/231777](https://tianchi.aliyun.com/competition/entrance/231777)

智慧物流：新冠期间饿了么骑士行为预估 复赛 
复赛 Baseline - Python 移植版本

## 前情提要

其实在准备考试了，但是有时候想起这个题目，遗憾没有 python 版本进行实验。于是抽出一天进行了移植。我本身不是 python 企业级开发，之前 python 也仅限于写 pytorch，基本上是官方 Java 版本的移植。我已经本地测试通了，但是没有镜像上传。不保证代码质量。（json之间传值其实花费我挺久。。）

## 路径

demo:  `eleme_round2_dispatch_master_20200421/dispatch-demo-py`

评测包：``eleme_round2_dispatch_master_20200421/dispatch-demo-py/dispatch-judge-jar-with-dependencies.jar`

demo 挂起（非后台版本，不用杀进程）`eleme_round2_dispatch_master_20200421/run_py_dev.sh`

评测数据：`eleme_round2_dispatch_master_20200421/dispatch-demo-py/open_test`

评测数据（少量，我debug用的）`eleme_round2_dispatch_master_20200421/dispatch-demo-py/open_test_small`

我用的 Java 进行评测的， `local_judge.py` 没调整，实在不想看了，移植太费精力

## 流程

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






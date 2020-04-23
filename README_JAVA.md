# 复赛 Baseline 流程部署分享
[https://tianchi.aliyun.com/competition/entrance/231777](https://tianchi.aliyun.com/competition/entrance/231777)

智慧物流：新冠期间饿了么骑士行为预估 复赛 
Baseline 流程部署分享

## 官方源码更新 4.21版本
* 我起来发现官方源码更新了，需要官网重新下载，相关描述是基于 4.20版本，可能有出入
* 由于我没找到更新日志，错过直播，我用 git 比较了两个版本源码，详情见https://github.com/NorwinYu/eleme_round2_baseline/commit/f7ad6ea50cd9fcdcefcd650cf5d940896589d09e
* 根据比对，主要更新是 py 版本的评测程序的联动，docker化，优化交互，更新少量描述和配置，具体如下，个人见解，具体见官方源码
* 更新 解决了我之前提到的 run.sh 替换问题
* 更新 增加 DockerfileP，应该是用于python demo docker 化
* 更新 README.md 相关说明
* 更新了 Java 里的 HttpDispatchClientImplTest.java 这个影响不大，测试文件
* 更新了 dispatch-demo-py/app.py  
* 更新了 dispatch-demo-py/demo/dto.py
* 把 dispatch-judge-jar-with-dependencies.jar open_test 数据 放入了 py 版本中
* 更新了 DispatchController.java
* 更新了 run.sh 配置

## 数据下载
* 下载 官方 Baseline 源码 `eleme_round2_dispatch_master_20200420.zip` ， 解压在目录
`eleme_round2_dispatch_master_20200420` 下
* 下载 官方 线下测试数据池 `eleme_round2_open_test_20200420.zip` , 解压在目录
`eleme_round2_dispatch_master_20200420/open_test` 下
* 官方 Baseline 并没有对数据进行挖掘，可供挖掘历史订单数据可于 比赛页面下载
`eleme_round2_order_history_20200419.zip`

## 结构剖析
![](%E5%A4%8D%E8%B5%9B%20Baseline%20%E6%B5%81%E7%A8%8B%E9%83%A8%E7%BD%B2%E5%88%86%E4%BA%AB/Screen%20Shot%202020-04-21%20at%206.06.29%20AM.png)
上图为官方给的交互时序图。在 `eleme_round2_dispatch_master_20200420`  中， 也给了一个 POD 示例图。
![](%E5%A4%8D%E8%B5%9B%20Baseline%20%E6%B5%81%E7%A8%8B%E9%83%A8%E7%BD%B2%E5%88%86%E4%BA%AB/deploy.png)

**Module说明 (来自官方 Readme)**
dispatch-api 官方评测程序，Java DTO等
dispatch-demo Java版本的Http Server框架和Demo实现，选手可以任意实现，仅做参考
dispatch-py python版本简单Http Server框架，没有实现逻辑，选手可以任意修改
dispatch-go go版本简单Http Server框架，没有实现逻辑，选手可以任意修改

* 也就是说，官方给了 Java (SpringBoot), go, py (flask) 版本的Http Server框架，但是只实现了 Java 的 Demo实现。因此我昨天先尝试了打通 Java 版本的提交。

*  `选手Restful Sever`  =  `eleme_round2_dispatch_master_20200420/dispatch-demo`  or  `eleme_round2_dispatch_master_20200420/dispatch-py`  or  `eleme_round2_dispatch_master_20200420/dispatch-go`  or `S调度程序(选手) + Http通信`
* `评测程序 Http Client` = `eleme_round2_dispatch_master_20200420/dispatch-api`  or `M发单器 + J评测程序 + Http通信 + 文件存储读取`
* `D文件存储` =`eleme_round2_dispatch_master_20200420/open_test`
* 通俗来讲，评测程序就好像一个模拟的环境，模拟生成状态下实时订单需求和骑手运力。选手Restful Sever就好像生成系统线上程序，一个智能体，对于所收到的信息，进行智能配单的决策。
## Baseline 流程部署
我尝试写这篇分享的原因是觉得这个提交过程对于非这个生态的开发者跨度有点大，看到群里好多同学都在问相关的（甚至提到了我提交的，其实我就是测试了下 Baseline, 第一次参赛）。我之前是做 Java Web 开发的，现在在学 NLP 相关，希望这篇分享能帮助大家关注算法上，而不是繁琐的配置。由于我目前在国外，上传镜像速度慢，部分步骤我基于我的情况进行了改动。

官方有 Readme ，有很多信息，不全部复制了，可以自行查看。以下只是个人见解。

我的这次部署尝试是基于 Java 版本， 也就是没有改过代码，本地编译出 jar 包以后，将 jar 包上传 git，然后通过镜像自动构建进行的。

### 环境 
所有需要的环境我都安装过了，我这里就列下环境需求，主要是针对 Mac OS 用户。
* Cmake(gcc)  应该可以Homebrew 装
* Java
* Maven 应该可以Homebrew 装
* docker 可以Homebrew 装
	* Mac OS
```
$ brew install docker docker-machine
$ brew cask install virtualbox
-> need password
-> possibly need to address System Preference setting
$ docker-machine create --driver virtualbox default
$ docker-machine env default
$ eval "$(docker-machine env default)"
$ docker run hello-world
$ docker-machine stop default
```

https://medium.com/@yutafujii_59175/a-complete-one-by-one-guide-to-install-docker-on-your-mac-os-using-homebrew-e818eb4cfc3
* 可能有遗漏

### 本地调试 官方的其中一种方式
`cd eleme_round2_dispatch_master_20200420`
#### 编译
`make build-java`
#### 开一个终端，启动选手 http server
```
java -jar dispatch-demo/target/dispatch-demo.jar
```
#### 开另一个终端，启动评测程序，进行评测
```
java -jar dispatch-api/target/dispatch-judge-jar-with-dependencies.jar `pwd`/open_test/
```
#### 关闭 选手 http server Ctrl C

### 构建镜像并上传
！ 如果大家在国内，可以按照 https://tianchi.aliyun.com/competition/entrance/231759/tab/174 进行部署
？ 我有对 `eleme_round2_dispatch_master_20200420/Dockerfile` 进行更改，把所有 `start.sh` 换成了`run.sh` ，按照说明要求。不过不确定会不会影响，但是如果不改，由于目录下没有 `start.sh`， 会出现问题。
。 如果有同样在国外的同学，可以参照我的步骤，选择通过代码进行自动构建。
* 自动构建和官方说明类似，只是在创建时候选取通过代码进行自动构建，然后我关联了Github，本来想关联阿里云的代码托管，结果发现 git push 也很慢。
* 自动构建可以设置构建触发规则已经目录，目录一定要设置正确。
![](%E5%A4%8D%E8%B5%9B%20Baseline%20%E6%B5%81%E7%A8%8B%E9%83%A8%E7%BD%B2%E5%88%86%E4%BA%AB/Screen%20Shot%202020-04-21%20at%206.55.34%20AM.png)
图中是我自己的Private 项目，本个分享的话 文件目录是 `/eleme_round2_dispatch_master_20200420/` , 文件名还是 `Dockerfile` , 版本号可以自己设置或者通过触发的参量。如果经常改代码的不如关了 代码变更自动构建镜像 功能，一键构建。建议版本号每次构建进行更改，或者通过触发规则参量传递每次构建更新版本号，这样可以体现镜像的功能。
* 有一点，由于构建需要 jar 包，之前编译完后，需要把 jar  git add  force, 因为一般是 git ignore

```
git add dispatch-demo/target/dispatch-demo.jar -f
git commit -m "Your message"
git push
```

* 然后手动开始构建。
* 构建成功在提交界面输入镜像地址和用户密码即可。






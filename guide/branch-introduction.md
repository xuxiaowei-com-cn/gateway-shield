# 项目分支介绍 {id=branch-introduction}

[[toc]]

## 项目地址 {id=href}

- 极狐 GitLab：<strong><font color="red">不推荐大家直接使用此平台的代码</font></strong>
    1. 极狐 GitLab 是<strong><font color="red">收费平台</font></strong>，不付费只能`免费使用 90 天`，虽然给平台写文章送了作者一年
       （2024年2月22 到 2025年2月21）旗舰版授权（9988元/年），<strong><font color="red">明年是否合作待定</font></strong>
    2. 极狐 GitLab 用于本人测试，可能存在多次<strong><font color="red">强制推送</font></strong>代码的情况，
       如果对 git 不熟悉，会出现冲突
    3. 极狐 GitLab 自动化`测试完成`后，将会`自动同步到其他平台`
    4. 其他平台免费使用，使用 `fork` 方便管理

| 平台        | 地址                                                                       | 说明              |
|-----------|--------------------------------------------------------------------------|-----------------|
| Gitee     | [链接](https://gitee.com/xuxiaowei-com-cn/gateway-shield)                  | 国内使用            |
| GitHub    | [链接](https://github.com/xuxiaowei-com-cn/gateway-shield)                 | 国外使用            |
| 极狐 GitLab | [链接](https://jihulab.com/xuxiaowei-jihu/xuxiaowei-com-cn/gateway-shield) | 使用 GitLab 流水线测试 |

## 主要分支说明 {id=primary}

| 分支   | JDK | Spring Boot | Spring Cloud | GitLab Pipeline Status                                                                                                                                                                                                                                                        | GitHub Actions Status                                                                                                                                                                                                         |
|------|-----|-------------|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| main | 17  | 3.2.5       | 2023.0.1     | [![Gitlab Pipeline Status](https://img.shields.io/gitlab/pipeline-status/xuxiaowei-jihu%2Fxuxiaowei-com-cn%2Fgateway-shield?gitlab_url=https%3A%2F%2Fjihulab.com&branch=main&logo=gitlab)](https://jihulab.com/xuxiaowei-jihu/xuxiaowei-com-cn/gateway-shield/-/commits/main) | [![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/xuxiaowei-com-cn/gateway-shield/maven.yml?logo=github)](https://github.com/xuxiaowei-com-cn/gateway-shield/commits/main)             |

## 文档源码分支 {id=docs}

- docs

## 镜像 {id=image}

| 镜像                                                                                                                                                                              | 说明                                            |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------|
| [registry.jihulab.com/xuxiaowei-jihu/xuxiaowei-com-cn/gateway-shield:x.y.z-SNAPSHOT](https://jihulab.com/xuxiaowei-jihu/xuxiaowei-com-cn/gateway-shield/container_registry)     | 极狐 GitLab 平台，单个版本最新镜像                         |
| [registry.jihulab.com/xuxiaowei-jihu/xuxiaowei-com-cn/gateway-shield:x.y.z-SNAPSHOT-abc](https://jihulab.com/xuxiaowei-jihu/xuxiaowei-com-cn/gateway-shield/container_registry) | 极狐 GitLab 平台，单个版本每次构建的镜像，`abc`代表流水线ID，实例级别自增  |
| [xuxiaoweicomcn/gateway-shield:x.y.z-SNAPSHOT](https://hub.docker.com/r/xuxiaoweicomcn/gateway-shield/tags)                                                                     | docker hub 平台，单个版本最新镜像                        |
| [xuxiaoweicomcn/gateway-shield:x.y.z-SNAPSHOT-abc](https://hub.docker.com/r/xuxiaoweicomcn/gateway-shield/tags)                                                                 | docker hub 平台，单个版本每次构建的镜像，`abc`代表流水线ID，实例级别自增 |

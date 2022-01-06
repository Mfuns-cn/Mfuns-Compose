# Mfuns-WebApp

## 先决条件

在 Android Studio 中安装以下版本的 SDK 和 build tools：

```groovy
android {
    compileSdkVersion 31
    buildToolsVersion "30.0.2"
}
```

然后，创建 `local.properties` 并指定 `sdk.dir`。

```properties
# local.properties
sdk.dir=/path/to/your/android-sdk
```

## 起步

项目可直接构建并运行。

## 须知

### 仓库

Mfuns-Android 完善后 Mfuns-WebApp 会逐渐弃用，因此 Mfuns-Android 和 Mfuns-WebApp 存放在两个不同的仓库。

### 格式化

项目使用 [spotless](https://github.com/diffplug/spotless) 进行格式化。

在代码提交前，请运行 `gradlew spotlessApply`。

### 提交

提交信息遵循 [约定式提交](https://www.conventionalcommits.org/zh-hans/)。

### 版本控制

版本控制遵循 [SemVer](https://semver.org/lang/zh-CN/)。

- 修订号 Z（`x.y.Z`）在发生了向下兼容的修正时递增。

- 次版本号 Y（`x.Y.z`）在发生了向下兼容的功能新增时递增。

- 主版本号 X（`X.y.z`）在应用发生了较大的更改（如主活动布局和结构发生了更改）时递增。主版本号的递增周期应当控制在至少一年以上。

在后端 API 稳定的情况下，假定用户使用最新的正式版本。不需要对历史版本进行维护。

### 分支控制

`v0.0.x` 允许直接推送到 `master`。

从 `v0.1.0` 开始，不允许直接推送到 `master`。若要添加新的功能，请先创建对应的 `author/feature` 分支，然后创建合并请求。

若确实要在继续维护当前版本的情况下启动新版本的开发流程，使用 `next` 分支。

## 许可

Mfuns-WebApp 使用 AGPL 协议授权。使用本产品或其源代码时，你不能将其闭源，改变许可协议或进行商业销售。

如果你发现了任何违规行为，请联系我们。

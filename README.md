# Mfuns-Compose

欢迎来到 Mfuns-Compose 仓库。这里存放了 Mfuns 的 WebApp、Android 和 PC 客户端的源代码。

## 项目结构

- [`common`](https://github.com/Mfuns-cn/Mfuns-Compose/tree/master/common)：Mfuns-Compose 的基础项目，存放了一些基础逻辑和公共组合。

- [`android`](https://github.com/Mfuns-cn/Mfuns-Compose/tree/master/android)：Mfuns 的 Android 客户端。

- [`webapp`](https://github.com/Mfuns-cn/Mfuns-Compose/tree/master/webapp)：Mfuns 的 WebApp 客户端。

- [`desktop`](https://github.com/Mfuns-cn/Mfuns-Compose/tree/master/desktop)：Mfuns 的 PC 客户端。

## 贡献

### 先决条件

安装以下版本的 Android SDK：

```groovy
android {
    compileSdk = 33
}
```

然后，在项目目录下创建 `local.properties` 文件并指定 `sdk.dir`。

```properties
# local.properties
sdk.dir=/path/to/your/android-sdk
```

### 格式化

项目使用 [spotless](https://github.com/diffplug/spotless) 进行格式化。

在代码提交前，请运行 `gradlew spotlessApply`。

### 提交

提交信息遵循 [约定式提交](https://www.conventionalcommits.org/zh-hans/)。

### 版本控制

版本控制遵循 [SemVer](https://semver.org/lang/zh-CN/)。所有项目共享相同的版本号。

- 修订号 Z（`x.y.Z`）在发生了向下兼容的修正时递增。

- 次版本号 Y（`x.Y.z`）在发生了向下兼容的功能新增时递增。

- 主版本号 X（`X.y.z`）在应用发生了较大的更改（如主活动布局和结构发生了更改）时递增。主版本号的递增周期应当控制在至少一年以上。

在后端 API 稳定的情况下，假定用户使用最新的正式版本。不需要对历史版本进行维护。

## 许可

Mfuns-Compose 使用 AGPL 协议授权。使用本产品或其源代码时，你不能将其闭源，改变许可协议或进行商业销售。

如果你发现了任何违规行为，请联系我们。

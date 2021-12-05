# 自定义数据源💡

想显示**更多内容？**想用已有的**API接口**的数据？想**自定义**显示**数据**？

使用自定义数据源功能即可满足您。

## [>>示例代码和Jar包<<](#例子)

## 什么是自定义数据源？

自定义数据源可更改APP的数据显示。

如默认情况下首页显示**正常数据**（下方第一幅图）；自定义数据源，可以让首页**仅显示“自定义标题”五个字**（下方第二幅图）、显示其它网站或API接口数据（下方第三幅图）

![normal](image/normal.jpg) ![customdatasource](image/customdatasource.jpg) ![customdatasource_2](image/customdatasource_2.jpg)

## 如何使用自定义数据源？

### 前提

使用他人编写的Jar包(直接跳到步骤6) 或 自己编写Jar包(熟悉**Kotlin**，最好能够了解Android的基础知识)

### 步骤

#### 1.实现您想要自定义数据的Kotlin接口

包：**com.skyd.imomoe.model.interfaces**

接口：

- IAnimeDetailModel
- IAnimeShowModel
- IClassifyModel
- IEverydayAnimeModel
- IHomeModel
- IMonthAnimeModel
- IPlayModel
- IRankModel
- ISearchModel
- IRankListModel
- IEverydayAnimeWidgetModel
- IRouteProcessor
- IUtil
- IConst

注：

1. IBase为所有Model接口的父接口，具体**接口含义**以及接口方法的**参数含义和返回值含义**见**接口注释**。
2. 接口中的所有**获取数据的方法**在**IO线程**运行。
3. 接口中的所有**获取数据的方法通过返回值返回数据**。若在方法中使用到了**回调**，请使用**Kotlin协程**将**回调转换为协程**。

#### 2.实现类的类名

确保所有实现类的**类名**为**Custom去掉第一个字母I的接口名**，例如**IAnimeDetailModel**的实现类为**CustomAnimeDetailModel**

注：未实现的接口默认使用原始数据。由于不同数据源相应操作差不大，不建议只实现一部分接口

#### 3.实现类的包

确保所有实现类的**包**为**com.skyd.imomoe.model.impls.custom**

#### 4.将实现类打包为普通的jar文件

#### 5.生成含dex的jar文件

通过普通的jar文件生成含dex的jar文件，参考指令：dx --dex --output=G:\CustomDataSource.jar E:\Android\Imomoe\app\build\libs\CustomDataSource.jar

#### 6.放入手机指定文件夹

将**CustomDataSource.jar**放入/storage/emulated/0/Android/data/com.skyd.imomoe/files/DataSourceJar文件夹。

即，jar文件位置：/storage/emulated/0/Android/data/com.skyd.imomoe/files/DataSourceJar/CustomDataSource.jar

#### 7.在设置界面选择自定义数据源Jar包

重启APP即可生效

## 其他

可直接使用的类及工具等：

```
com.skyd.imomoe.model.interfaces.**
com.skyd.imomoe.model.util.**
com.skyd.imomoe.util.html.source.**
com.skyd.imomoe.util.eventbus.**
com.skyd.imomoe.util.Util
com.skyd.imomoe.bean.**
com.skyd.imomoe.config.**
org.jsoup.**
org.greenrobot.eventbus.**
kotlin.**
kotlinx.**
```

**表示包下的类、接口等，包括子包下的内容。

其它类、接口、方法等可能会被**混淆**，**不可使用**，具体混淆规则见工程文件。

## 例子

详细代码和**Jar包**见下方链接内容。注：此Jar包可直接放入手机相应文件夹（步骤7）供APP使用。**请自行在此仓库检查Jar包更新**。

**[示例1](sample1/custom)  [示例2](sample1/custom)**

## 备注⚠

由于本人技术有限，不保证后续此功能的接口不发生大更改。
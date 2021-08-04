# 自定义数据源

自带的代码爬取**数据不正确？**想爬取**更多内容？**想用已有的**API接口**的数据？想**自定义**显示**数据**？

使用自定义数据源功能即可满足您。

## 什么是自定义数据源？

自定义数据源可更改APP的数据显示。

如默认情况下首页显示**正常数据**（下方第一幅图）；自定义数据源，可以让首页**仅显示“自定义标题”五个字**（下方第二幅图）

![main](image/normal.jpg) ![main](image/customdatasource.jpg)

## 如何使用自定义数据源？

### 前提

熟悉**Java**，能够进行**Java**编程，最好能够了解Android的基础知识。

### 步骤

#### 1.实现您想要自定义数据部分的接口

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
- IRouterProcessor
- IUtil
- IConst

注：IBaseModel为所有Model接口的父接口

#### 2.实现类的类名

确保所有实现类的**类名**为**Custom去掉第一个字母I的接口名**，例如**IAnimeDetailModel**的实现类为**CustomAnimeDetailModel**

注：未实现的接口默认使用原始数据。由于不同数据源相应操作差不大，不建议只实现一部分接口

#### 3.实现类的包

确保所有实现类的**包**为**com.skyd.imomoe.model.impls.custom**

#### 4.将实现类打包为普通的jar文件

#### 5.生成含dex的jar文件

通过普通的jar文件生成含dex的jar文件，参考指令：dx --dex --output=G:\CustomDataSource.jar E:\Android\Imomoe\app\build\libs\CustomDataSource.jar

#### 6.含dex的jar的名称

更改含dex的jar文件名为**CustomDataSource.jar**

#### 7.放入手机指定文件夹

将**CustomDataSource.jar**放入/storage/emulated/0/Android/data/com.skyd.imomoe/files/DataSourceJar文件夹。

即，jar文件位置：/storage/emulated/0/Android/data/com.skyd.imomoe/files/DataSourceJar/CustomDataSource.jar

#### 8.打开设置界面的自定义数据开关

刷新界面即可生效

## 备注

由于本人技术有限，不保证后续此功能的接口不发生大更改
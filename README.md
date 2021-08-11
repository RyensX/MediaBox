![readme_cover](image/readme_cover.png)

<div align="center">
    <h1>樱花动漫</h1>
    <p>
        <a href="https://img.shields.io/github/v/release/SkyD666/Imomoe" style="text-decoration:none">
            <img src="https://img.shields.io/github/v/release/SkyD666/Imomoe" alt="GitHub release (latest by date)"/>
        </a>
        <a href="https://img.shields.io/github/downloads/SkyD666/Imomoe/total" style="text-decoration:none" >
            <img src="https://img.shields.io/github/downloads/SkyD666/Imomoe/total" alt="GitHub all downloads"/>
        </a>
        <a href="https://img.shields.io/badge/Android-5.0%2B-brightgreen" style="text-decoration:none" >
            <img src="https://img.shields.io/badge/Android-5.0%2B-brightgreen" alt="Android version"/>
        </a>
        <a href="https://img.shields.io/github/license/SkyD666/Imomoe" style="text-decoration:none" >
            <img src="https://img.shields.io/github/license/SkyD666/Imomoe" alt="GitHub license"/>
        </a>
	</p>
    <p>
        樱花动漫第三方安卓Android客户端，免费开源，目的是学习Android开发。（仅支持Android 5及以上版本）
    </p>
</div>

----

## 特色功能

1. 支持显示**排行榜**
2. 支持显示**每日更新**的番剧
3. 支持**分类查看**动漫
4. 支持**双指缩放**、**移动**、**旋转**视频
5. 支持视频**投屏**到电视
6. 支持部分视频**显示**、**发送弹幕**（需要数据源支持弹幕）
7. 支持**缓存视频**到本地（暂不支持m3u8格式资源缓存）
8. 支持**追番**（数据保存在本地）
9. 支持显示**观看历史**记录
10. 支持显示**搜索历史**记录
11. 支持改变视频**播放速度**
12. 支持改变**视频**显示**比例**（16:9, 4:3, 全屏等）
13. [支持**自定义**显示**数据源**](doc/customdatasource/README.md)
14. ......

## 运行截图

![main](screenshot/main.jpg) ![anime_detail](screenshot/anime_detail.jpg)

![new_anime](screenshot/new_anime.jpg) ![everyday_anime](screenshot/everyday_anime.jpg) 

![search](screenshot/search.jpg) ![play](screenshot/play.jpg) 

![classify](screenshot/classify.jpg) ![rank](screenshot/rank.jpg)

![favorite](screenshot/favorite.jpg) ![history](screenshot/history.jpg) 

![player](screenshot/player.jpg) 

## 应用主要权限说明

### 存储

1. 读取存储卡中的内容：缓存动漫功能需要读取本地存储卡中缓存的视频文件
2. 修改或删除存储卡中的内容：缓存动漫功能需要修改记录缓存信息的xml文件

### 电话

1. 读取设备通话状态和识别码：友盟U-APM统计应用稳定性信息等需要

### 位置信息

1. 访问大致、确切位置：友盟SDK需要

### 其它应用功能

1. 应用内安装其他应用：应用安装更新apk需要
2. 防止手机休眠：投屏到电视功能需要
3. 允许接收WLAN多播：投屏到电视功能需要

## 附加说明

使用jsoup爬取樱花动漫网页内容，所有默认数据来自http://www.yhdm.io/ 

## 免责声明

1. 此软件**只提供数据显示**，**不提供原始数据**。
2. 此软件显示的所有内容，其**版权**均**归原作者**所有。
3. 此软件**仅可用作学习交流**，未经授权，**禁止用于其他用途**，请在下载**24小时内删除**。
4. 因使用此软件产生的版权问题，软件作者概不负责。

## License

[**GNU General Public License v3.0**](LICENSE)


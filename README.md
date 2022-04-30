<p align="center">
<img src="image/cover.png" width="150">
</p>
<div align="center">
    <h1>媒体盒子</h1>
    <p>
        <a href="https://github.com/RyensX/MediaBox/releases/latest" style="text-decoration:none">
            <img src="https://img.shields.io/github/v/release/RyensX/MediaBox?display_name=release" alt="GitHub release (latest by date)"/>
        </a>
        <a href="https://github.com/RyensX/MediaBox/releases/latest" style="text-decoration:none" >
            <img src="https://img.shields.io/github/downloads/RyensX/MediaBox/total" alt="GitHub all downloads"/>
        </a>
        <a href="https://img.shields.io/badge/Android-5.0%2B-brightgreen" style="text-decoration:none" >
            <img src="https://img.shields.io/badge/Android-5.0%2B-brightgreen" alt="Android version"/>
        </a>
        <a href="LICENSE" style="text-decoration:none" >
            <img src="https://img.shields.io/github/license/RyensX/MediaBox" alt="GitHub license"/>
        </a>
    </p>
</div>

<p align="center"><font size="4">插件化媒体容器，不含广告，免费开源，便于学习Android开发。</font></p>

---

## 🗃️功能概述

* 简单编写对应[插件API](https://github.com/RyensX/MediaBoxPlugin)的组件，就能直接通过媒体盒子提供的各种功能快速变成一个相对完整的APP。
* 当安装多个插件后相当于实现了资源的聚合，使用更加方便。

提供的功能包括但不限于：

1. 各种不同类型数据视图，数据在内部绑定不同视图，无需自己编写界面，返回什么数据就显示什么视图，见[数据](https://github.com/RyensX/MediaBoxPlugin/tree/dev/pluginApi/src/main/java/com/su/mediabox/pluginapi/v2/been) [视图映射](https://github.com/RyensX/MediaBox/blob/b85d3b71525c5ac31538ed9a4d1d44ea037ae8d7/app/src/main/java/com/su/mediabox/view/adapter/type/TypeAdapter.kt#L55https://github.com/RyensX/MediaBox/tree/dev/app/src/main/res/layout)
2. 视频播放器，支持手势控制、进度记忆、缓存、弹幕、调用外部打开等
3. 作品收藏、历史记录等
4. 收藏、插件数据云端备份（WebDav,TODO）
5. 多彩皮肤
6. ...

---

* ### 如果觉得不错，欢迎⭐**Star**
* ### 如果有什么想法或建议，欢迎讨论或PR(**请尽量贴合项目的源码和commit风格**)

---

## ⚡下载&使用

* [正式Release](https://github.com/RyensX/MediaBox/releases/latest)
* [自动构建](https://github.com/RyensX/MediaBox/actions/workflows/Android.yml)(**不稳定**，适合体验最新功能，可与Release包共存，注意**需要登陆github**才可下载Artifacts)
* [官方插件仓库](https://github.com/RyensX/MediaBoxPluginRepository)

## 🚀效果展示

[MediaBox示例插件-樱花动漫第三方客户端](https://github.com/RyensX/SakuraAnimePlugin)

## 🔒安全说明

**请勿**私自**传播APK**安装包，Github仓库为唯一长期仓库，**请仅在Github仓库下载安装包**，请勿下载来历不明的插件，谨防恶意代码！

## 📌免责声明

1. 此软件**只负责数据展示**，本身**不提供任何数据**，和普通浏览器功能类似。
2. 此软件显示的所有内容，其**版权**均**归原作者**所有。
3. 此软件**仅可用作学习交流**，未经授权，**禁止用于其他用途**，请在下载**24小时内删除**。
4. 因使用此软件产生的版权问题，软件作者概不负责。

## 🔍相关项目

- 本项目基于[Imomoe](https://github.com/SkyD666/Imomoe)
- [插件API](https://github.com/RyensX/MediaBoxPlugin)

## 🚗构建相关

- [**secret.gradle**](doc/about_secret.gradle.md)文件
- **notice.iml** - 使用协议等，请自行添加

## 许可证

使用此软件代码需**遵循以下许可证协议**

[**GNU General Public License v3.0**](LICENSE)

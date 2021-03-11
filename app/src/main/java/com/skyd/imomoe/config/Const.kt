package com.skyd.imomoe.config

import android.os.Environment
import java.io.File
import kotlin.random.Random

interface Const {
    interface Common {
        companion object {
            const val GITHUB_URL = "https://github.com/SkyD666/Imomoe"
            const val GITHUB_NEW_ISSUE_URL = "https://github.com/SkyD666/Imomoe/issues/new"
            const val GITEE_URL = "https://gitee.com/SkyD666/Imomoe"
        }
    }

    interface ActionUrl {
        companion object {
            const val ANIME_DETAIL = "/show/"
            const val ANIME_PLAY = "/v/"
            const val ANIME_SEARCH = "/search/"
            const val ANIME_TOP = "/top/"
            const val ANIME_CLASSIFY = "/app/classify"      //此常量为自己定义，与服务器无关
            const val ANIME_BROWSER = "/app/browser"      //此常量为自己定义，与服务器无关
            const val ANIME_ANIME_DOWNLOAD_EPISODE =
                "/app/animeDownloadEpisode"      //此常量为自己定义，转到下载的每一集
            const val ANIME_ANIME_DOWNLOAD_PLAY = "/app/animeDownloadPlay"      //此常量为自己定义，播放这一集
            const val ANIME_ANIME_DOWNLOAD_M3U8 = "/app/animeDownloadM3U8"      //此常量为自己定义，m3u8格式
            const val ANIME_LAUNCH_ACTIVITY = "/app/animeLaunchActivity"      //此常量为自己定义，启动Activity
        }
    }

    interface Update {
        companion object {
            val updateFilePath =
                Environment.getExternalStorageDirectory().toString() + "/" + "Download/"
            const val updateFileName = "com.skyd.imomoe.apk"
            val updateFile get() = File(updateFilePath + updateFileName)
        }
    }

    interface DownloadAnime {
        companion object {
            val animeFilePath =
                Environment.getExternalStorageDirectory().toString() + "/Imomoe/DownloadAnime/"
        }
    }

    interface Request {
        companion object {
            val USER_AGENT_ARRAY = arrayOf(
                "Mozilla/4.0(compatible;MSIE7.0;WindowsNT5.1;AvantBrowser)",
                "Mozilla/4.0(compatible;MSIE7.0;WindowsNT5.1;360SE)",
                "Mozilla/4.0(compatible;MSIE7.0;WindowsNT5.1;Trident/4.0;SE2.XMetaSr1.0;SE2.XMetaSr1.0;.NETCLR2.0.50727;SE2.XMetaSr1.0)",
                "Mozilla/4.0(compatible;MSIE7.0;WindowsNT5.1;TheWorld)",
                "Mozilla/4.0(compatible;MSIE7.0;WindowsNT5.1;TencentTraveler4.0)",
                "Opera/9.80(Macintosh;IntelMacOSX10.6.8;U;en)Presto/2.8.131Version/11.11",
                "Mozilla/5.0(WindowsNT6.1;rv:2.0.1)Gecko/20100101Firefox/4.0.1",
                "Mozilla/5.0(compatible;MSIE9.0;WindowsNT6.1;Trident/5.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)",
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 UBrowser/4.0.3214.0 Safari/537.36",
                "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
                "Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.5; AOLBuild 4337.35; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
                "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)",
                "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
                "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)",
                "Mozilla/5.0 (X11; U; Linux; en-US) AppleWebKit/527+ (KHTML, like Gecko, Safari/419.3) Arora/0.6",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) Gecko/20070215 K-Ninja/2.1.1",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/20080705 Firefox/3.0 Kapiko/3.0",
                "Mozilla/5.0 (X11; Linux i686; U;) Gecko/20070322 Kazehakase/0.4.5",
                "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.8) Gecko Fedora/1.9.0.8-1.fc10 Kazehakase/0.5.6",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
                "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36"
            )
        }
    }
}
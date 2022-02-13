package com.su.mediabox.plugin

import kotlin.random.Random

object Constant {

    const val PLUGIN_ACTION = "media_box_plugin_action"

    /**
     * 此动作为App自身路由使用，与服务器无关
     *
     * 注意：传入的参数均需要单独经过URL编码
     */
    object ActionUrl {
        /**
         * 路由到分类页面
         * @param ps a/a/a
         */
        const val ANIME_CLASSIFY = "/app/classify"
        const val ANIME_SEARCH = "/app/search"
        const val ANIME_DETAIL = "/app/detail"
        const val ANIME_RANK = "/app/rank"
        const val ANIME_PLAY = "/app/play"
        const val ANIME_BROWSER = "/app/browser"      //此常量为自己定义，与服务器无关
        const val ANIME_ANIME_DOWNLOAD_EPISODE =
            "/app/animeDownloadEpisode"      //此常量为自己定义，转到下载的每一集
        const val ANIME_MONTH_NEW_ANIME = "/app/mouthNewAnime"

        const val ANIME_SKIP_BY_WEBSITE = "/app/skipByWebsite"
        const val ANIME_ANIME_DOWNLOAD_PLAY = "/app/animeDownloadPlay"      //播放这一集
        const val ANIME_ANIME_DOWNLOAD_M3U8 = "/app/animeDownloadM3U8"      //m3u8格式
        const val ANIME_LAUNCH_ACTIVITY = "/app/animeLaunchActivity"      //启动Activity
    }

    object ViewHolderTypeString {
        const val EMPTY_STRING = ""               //未知类型，使用EmptyViewHolder容错处理。
        const val UNKNOWN = "unknown"               //未知类型，使用EmptyViewHolder容错处理。
        const val ANIME_COVER_1 = "animeCover1"
        const val ANIME_COVER_2 = "animeCover2"
        const val ANIME_COVER_3 = "animeCover3"
        const val ANIME_COVER_4 = "animeCover4"
        const val ANIME_COVER_5 = "animeCover5"
        const val ANIME_COVER_6 = "animeCover6"
        const val ANIME_COVER_7 = "animeCover7"
        const val ANIME_COVER_8 = "animeCover8"
        const val ANIME_COVER_9 = "animeCover9"
        const val HEADER_1 = "header1"
        const val ANIME_EPISODE_FLOW_LAYOUT_1 = "animeEpisodeFlowLayout1"
        const val ANIME_EPISODE_FLOW_LAYOUT_2 = "animeEpisodeFlowLayout2"
        const val ANIME_DESCRIBE_1 = "animeDescribe1"
        const val GRID_RECYCLER_VIEW_1 = "gridRecyclerView1"
        const val BANNER_1 = "banner1"
        const val LICENSE_HEADER_1 = "licenseHeader1"
        const val LICENSE_1 = "license1"
        const val SEARCH_HISTORY_HEADER_1 = "searchHistoryHeader1"
        const val SEARCH_HISTORY_1 = "searchHistory1"
        const val ANIME_INFO_1 = "animeInfo1"
        const val HORIZONTAL_RECYCLER_VIEW_1 = "horizontalRecyclerView1"
        const val ANIME_EPISODE_2 = "animeEpisode2"
        const val UPNP_DEVICE_1 = "upnpDevice1"
        const val MORE_1 = "More1"
        const val SKIN_COVER_1 = "skinCover1"
        const val DATA_SOURCE_1 = "dataSource1"
    }

    object Request {
        val USER_AGENT_ARRAY = arrayOf(
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)",
            "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 UBrowser/4.0.3214.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
            "Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.5; AOLBuild 4337.35; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)",
            "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
            "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) Gecko/20070215 K-Ninja/2.1.1",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/20080705 Firefox/3.0 Kapiko/3.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
            "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36"
        )

        fun getRandomUserAgent() = USER_AGENT_ARRAY.run { get(Random.nextInt(size)) }
    }
}
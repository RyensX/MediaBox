package com.su.mediabox.util

import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.*
import com.su.mediabox.util.logD
import androidx.core.content.FileProvider
import com.su.mediabox.App
import com.su.mediabox.pluginapi.util.TextUtil.urlDecode
import java.io.*

/**
 * # FileUri
 *
 * - Uri & Path Tool
 *
 * @author javakam
 * @date 2020/8/24  11:24
 * 来自 https://github.com/javakam/FileOperator/blob/master/library_core/src/main/java/ando/file/core/FileUri.kt
 */
object FileUri {

    val AUTHORITY = "${App.context.packageName}.fileProvider"

    //Android R
    //----------------------------------------------------------------

    /**
     * `MANAGE_EXTERNAL_STORAGE` 权限检查
     *
     * @return `true` Have permission
     */
    fun isExternalStorageManager(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Environment.isExternalStorageManager() else false

    /**
     * 跳转到 `MANAGE_EXTERNAL_STORAGE` 权限设置页面
     *
     * @return `true` Has been set
     */
    fun jumpManageAppAllFilesPermissionSetting(
        context: Context,
        isNewTask: Boolean = false,
    ): Boolean {
        if (isExternalStorageManager()) return true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${context.packageName}")
                if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    //从 FilePath 中获取 Uri (Get Uri from FilePath)
    //----------------------------------------------------------------

    fun getUriByPath(path: String?): Uri? =
        if (path.isNullOrBlank()) null else getUriByFile(File(path))

    /**
     * Return a content URI for a given file.
     *
     * @param file The file.
     * @param isOriginal true content://  or file:// ; false file://xxx
     * @return a content URI for a given file
     */
    fun getUriByFile(file: File?, isOriginal: Boolean = false): Uri? {
        return if (isOriginal) Uri.fromFile(file)
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(App.context, AUTHORITY, file ?: return null)
            } else {
                Uri.fromFile(file)
            }
        }
    }

    fun getShareUri(path: String?): Uri? =
        if (path.isNullOrBlank()) null else getUriByFile(File(path), isOriginal = false)

    /**
     * @return content://  or  file://
     */
    fun getShareUri(file: File?): Uri? = getUriByFile(file, isOriginal = false)

    fun getOriginalUri(path: String?): Uri? =
        if (path.isNullOrBlank()) null else getUriByFile(File(path), isOriginal = true)

    /**
     * @return file://xxx
     */
    fun getOriginalUri(file: File?): Uri? = getUriByFile(file, isOriginal = true)

    //获取Uri对应的文件路径, Compatible with API 26
    //----------------------------------------------------------------

    /**
     * ### Get the file path through Uri
     *
     * - Need permission: RequiresPermission(permission.READ_EXTERNAL_STORAGE)
     *
     * - Modified from: https://github.com/coltoscosmin/FileUtils/blob/master/FileUtils.java
     *
     * @return file path
     */
    fun getPathByUri(uri: Uri?): String? {
        logI("转换Uri", uri?.toString() ?: "")
        var path: String? = uri?.run {
            logI(
                "getPathByUri -> ",
                "Uri: " + uri +
                        ", Authority: " + uri.authority +
                        ", Fragment: " + uri.fragment +
                        ", Port: " + uri.port +
                        ", Query: " + uri.query +
                        ", Scheme: " + uri.scheme +
                        ", Host: " + uri.host +
                        ", Segments: " + uri.pathSegments.toString()
            )

            //MT管理器2
            if (isMT2FileManagerUri(uri))
                return@run getMT2FileManagerFilePath(uri)
            //NP管理器
            if (isNpFileManagerUri(uri))
                return@run getNpFileManagerFilePath(uri)
            //RootExplorer
            if (isRootExplorerUri(uri))
                return@run getRootExplorerFilePath(uri)

            // 以 file:// 开头的使用第三方应用打开 (open with third-party applications starting with file://)
            if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true))
                return@run getDataColumn(uri)

            @SuppressLint("ObsoleteSdkInt")
            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // Before 4.4 , API 19 content:// 开头, 比如 content://media/external/images/media/123
            if (!isKitKat && ContentResolver.SCHEME_CONTENT.equals(uri.scheme, true)) {
                if (isGooglePhotosUri(uri)) return uri.lastPathSegment
                return@run getDataColumn(uri)
            }

            val context = App.context
            // After 4.4 , API 19
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // LocalStorageProvider
                if (isLocalStorageDocument(uri)) {
                    // The path is the id
                    return@run DocumentsContract.getDocumentId(uri);
                }
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            return@run context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                                .toString() + File.separator + split[1]
                        } else {
                            @Suppress("DEPRECATION")
                            return@run Environment.getExternalStorageDirectory()
                                .toString() + File.separator + split[1]
                        }
                    } else if ("home".equals(type, ignoreCase = true)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            return@run context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                                .toString() + File.separator + "documents" + File.separator + split[1]
                        } else {
                            @Suppress("DEPRECATION")
                            return@run Environment.getExternalStorageDirectory()
                                .toString() + File.separator + "documents" + File.separator + split[1]
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val sdcardPath =
                            Environment.getExternalStorageDirectory()
                                .toString() + File.separator + "documents" + File.separator + split[1]
                        return@run if (sdcardPath.startsWith("file://")) {
                            sdcardPath.replace("file://", "")
                        } else {
                            sdcardPath
                        }
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id != null && id.startsWith("raw:")) {
                        return@run id.substring(4)
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        val contentUriPrefixesToTry = arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads",
                            "content://downloads/all_downloads"
                        )
                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            val contentUri =
                                ContentUris.withAppendedId(Uri.parse(contentUriPrefix), id.toLong())
                            try {
                                val path = getDataColumn(contentUri)
                                if (!path.isNullOrBlank()) return path
                            } catch (e: Exception) {
                                logE("文件", e.toString())
                            }
                        }
                    } else {
                        //testPath(uri)
                        return@run getDataColumn(uri)
                    }
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val contentUri: Uri? = when (split[0]) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        "download" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI
                        } else null
                        else -> null
                    }
                    val selectionArgs = arrayOf(split[1])
                    return@run getDataColumn(contentUri, "_id=?", selectionArgs)
                }

                //GoogleDriveProvider
                else if (isGoogleDriveUri(uri)) {
                    return@run getGoogleDriveFilePath(uri, context)
                }
            }
            // MediaStore (and general)
            else if ("content".equals(uri.scheme, ignoreCase = true)) {
                // Return the remote address
                if (isGooglePhotosUri(uri)) {
                    return@run uri.lastPathSegment
                }
                // Google drive legacy provider
                else if (isGoogleDriveUri(uri)) {
                    return@run getGoogleDriveFilePath(uri, context)
                }
                // Huawei
                else if (isHuaWeiUri(uri)) {
                    val uriPath = getDataColumn(uri) ?: uri.toString()
                    //content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
                    if (uriPath.startsWith("/root")) {
                        return@run uriPath.replace("/root".toRegex(), "")
                    }
                }
                return@run getDataColumn(uri)
            }
            return@run getDataColumn(uri)
        }
        path = path ?: uri.uriCompatConvert()
        logI("Uri转换结果", path ?: "null")
        return path
    }

    private fun Uri?.uriCompatConvert() = this?.toString()?.run {
        val index = indexOf("storage")
        if (index == -1) null
        else "/${substring(index)}".urlDecode().urlDecode()
    }

    /**
     * BUG : 部分机型进入"文件管理器" 执行到  cursor.getColumnIndexOrThrow(column);出现
     *       Caused by: java.lang.IllegalArgumentException: column '_data' does not exist. Available columns: []
     *
     * Fixed :
     *      https://stackoverflow.com/questions/42508383/illegalargumentexception-column-data-does-not-exist
     *
     */
    private fun getDataColumn(
        uri: Uri?,
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): String? {
        @Suppress("DEPRECATION")
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)
        try {
            App.context.contentResolver.query(
                uri ?: return null,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { c: Cursor ->
                if (c.moveToFirst()) {
                    val columnIndex = c.getColumnIndex(column)
                    return c.getString(columnIndex)
                }
            }
        } catch (e: Throwable) {
            logE("文件", "getDataColumn -> ${e.message}")
        }
        return null
    }

    //The Uri to check
    //----------------------------------------------------------------

    private fun getGoogleDriveFilePath(uri: Uri, context: Context): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { c: Cursor ->
            /*
             Get the column indexes of the data in the Cursor,
             move to the first row in the Cursor, get the data, and display it.
             */
            val nameIndex: Int = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            //val sizeIndex: Int = c.getColumnIndex(OpenableColumns.SIZE)
            if (!c.moveToFirst()) {
                return uri.toString()
            }
            val name: String = c.getString(nameIndex)
            //val size = c.getLong(sizeIndex).toString()
            val file = File(context.cacheDir, name)

            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable: Int = inputStream?.available() ?: 0
                val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream?.read(buffers)?.also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
            return file.path
        }
        return uri.toString()
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri?): Boolean {
        return "com.google.android.apps.photos.content".equals(uri?.authority, true)
    }

    fun isGoogleDriveUri(uri: Uri?): Boolean {
        return "com.google.android.apps.docs.storage.legacy" == uri?.authority || "com.google.android.apps.docs.storage" == uri?.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is local.
     */
    fun isLocalStorageDocument(uri: Uri?): Boolean {
        return AUTHORITY.equals(uri?.authority, true)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri?): Boolean {
        return "com.android.externalstorage.documents".equals(uri?.authority, true)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri?): Boolean {
        return "com.android.providers.downloads.documents".equals(uri?.authority, true)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri?): Boolean {
        return "com.android.providers.media.documents".equals(uri?.authority, true)
    }

    /**
     * content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
     *
     * @param uri
     * @return
     */
    private fun isHuaWeiUri(uri: Uri?): Boolean {
        return "com.huawei.hidisk.fileprovider".equals(uri?.authority, true)
    }

    private fun isMT2FileManagerUri(uri: Uri?) =
        uri?.toString()?.startsWith("content://0@bin.mt.plus.fp/") == true

    private fun getMT2FileManagerFilePath(uri: Uri) = uri.path

    private fun isNpFileManagerUri(uri: Uri?) =
        uri?.toString()?.startsWith("content://player.normal.np.fileprovider/sd") == true

    private fun getNpFileManagerFilePath(uri: Uri) = uri.toString().replaceFirst(
        "content://player.normal.np.fileprovider/sd",
        Environment.getExternalStorageDirectory().absolutePath
    )

    private fun isRootExplorerUri(uri: Uri?) =
        uri?.toString()?.startsWith("file:///") == true

    private fun getRootExplorerFilePath(uri: Uri) = uri.path

}
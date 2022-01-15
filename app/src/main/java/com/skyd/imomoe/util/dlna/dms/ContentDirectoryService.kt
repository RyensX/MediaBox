package com.skyd.imomoe.util.dlna.dms

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException
import org.fourthline.cling.support.model.BrowseFlag
import org.fourthline.cling.support.model.SortCriterion
import org.fourthline.cling.support.model.BrowseResult
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.contentdirectory.DIDLParser
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode
import java.lang.Exception

class ContentDirectoryService : AbstractContentDirectoryService() {

    @Throws(ContentDirectoryException::class)
    override fun browse(
        objectID: String?,
        browseFlag: BrowseFlag?,
        filter: String?,
        firstResult: Long,
        maxResults: Long,
        orderby: Array<out SortCriterion>?
    ): BrowseResult {
        val container = ContentFactory.instance.getContent(objectID)
        val didlContent = DIDLContent()
        for (c in container.containers) {
            didlContent.addContainer(c)
        }
        for (item in container.items) {
            didlContent.addItem(item)
        }
        val count = container.childCount
        val result: String = try {
            DIDLParser().generate(didlContent)
        } catch (e: Exception) {
            throw ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, e.toString())
        }
        return BrowseResult(result, count.toLong(), count.toLong())
    }

    @Throws(ContentDirectoryException::class)
    override fun search(
        containerId: String?,
        searchCriteria: String?,
        filter: String?,
        firstResult: Long,
        maxResults: Long,
        orderBy: Array<out SortCriterion>?
    ): BrowseResult {
        // You can override this method to implement searching!
        return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy)
    }
}
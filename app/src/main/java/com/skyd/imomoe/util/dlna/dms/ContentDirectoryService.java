package com.skyd.imomoe.util.dlna.dms;

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

public class ContentDirectoryService extends AbstractContentDirectoryService {

    @Override
    public BrowseResult browse(String objectID,
                               BrowseFlag browseFlag,
                               String filter,
                               long firstResult,
                               long maxResults,
                               SortCriterion[] orderBy) throws ContentDirectoryException {
        Container container = ContentFactory.getInstance().getContent(objectID);
        DIDLContent didlContent = new DIDLContent();
        for (Container c : container.getContainers()) {
            didlContent.addContainer(c);
        }
        for (Item item : container.getItems()) {
            didlContent.addItem(item);
        }
        int count = container.getChildCount();
        String result;
        try {
            result = new DIDLParser().generate(didlContent);
        } catch (Exception e) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, e.toString());
        }
        return new BrowseResult(result, count, count);
    }

    @Override
    public BrowseResult search(String containerId, String searchCriteria,
                               String filter, long firstResult, long maxResults,
                               SortCriterion[] orderBy) throws ContentDirectoryException {
        // You can override this method to implement searching!
        return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy);
    }
}

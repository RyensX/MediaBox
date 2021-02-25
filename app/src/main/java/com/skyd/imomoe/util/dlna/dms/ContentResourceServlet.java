/*
 * Copyright (C) 2014 Kevin Shen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skyd.imomoe.util.dlna.dms;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;

import java.io.File;

public class ContentResourceServlet extends DefaultServlet {

    @Override
    public Resource getResource(String pathInContext) {
        // String id = Utils.parseResourceId(pathInContext);
        // content://media/external/video/media/1611127029319529
        // Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(id));
//        Logger.i("ContentResourceServlet, path: %s", pathInContext);
        try {
            File file = new File(pathInContext);
            if (file.exists()) return FileResource.newResource(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class VideoResourceServlet extends ContentResourceServlet {
    }

    public static class AudioResourceServlet extends ContentResourceServlet {
    }
}
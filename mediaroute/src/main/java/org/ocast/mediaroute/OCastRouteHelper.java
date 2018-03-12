/*
 * Software Name : OCast SDK
 *
 *  Copyright (C) 2017 Orange
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.ocast.mediaroute;

import android.content.Context;
import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouter;

import java.util.Set;

/**
 * Helper class to setup {@link org.ocast.mediaroute.OCastMediaRouteProvider OCastMediaRouteProvider}
 */
public class OCastRouteHelper {
    private static MediaRouteProvider sOCastProvider;

    private OCastRouteHelper() {
    }

    /**
     * register the {@link org.ocast.mediaroute.OCastMediaRouteProvider OCastMediaRouteProvider}
     * on the MediaRouter
     * @param context
     * @param searchTargets a list of search targets to be discovered
     */
    public static void addMediaRouteProvider(Context context, Set<String> searchTargets) {
        if(sOCastProvider == null) {
            MediaRouter mediaRouter = MediaRouter.getInstance(context.getApplicationContext());
            sOCastProvider = new OCastMediaRouteProvider(context.getApplicationContext(), searchTargets);
            mediaRouter.addProvider(sOCastProvider);
        }
    }
}

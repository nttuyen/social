/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.core.storage.cache.selector;

import org.exoplatform.services.cache.ObjectCacheInfo;
import org.exoplatform.social.core.storage.cache.model.data.IntegerData;
import org.exoplatform.social.core.storage.cache.model.key.ActivityCountKey;
import org.exoplatform.social.core.storage.cache.model.key.ActivityType;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Nov 6, 2014  
 */
public class MySpacesStreamCountCacheSelector extends ScopeCacheSelector<ActivityCountKey, IntegerData> {

  private String streamOwnerId;
  private ActivityType type;
  

  public MySpacesStreamCountCacheSelector(final String streamOwnerId, final ActivityType type) {
    if (streamOwnerId == null) {
      throw new NullPointerException("streamOwnerId is not null.");
    }
    
    this.streamOwnerId = streamOwnerId;
    this.type = type;
  }

  @Override
  public boolean select(final ActivityCountKey key, final ObjectCacheInfo<? extends IntegerData> ocinfo) {
    if (!super.select(key, ocinfo)) {
      return false;
    }

    if (key.getKey() != null && key.getKey().getId().equals(streamOwnerId) && (type.equals(key.getActivityType()))) {
      return true;
    }

    return false;
  }

}
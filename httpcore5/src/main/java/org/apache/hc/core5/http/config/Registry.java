/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.hc.core5.http.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.TextUtils;

/**
 * Generic registry of items keyed by low-case string ID.
 *
 * @param <I> the type of values to lookup.
 * @since 4.3
 */
@Contract(threading = ThreadingBehavior.SAFE)
public final class Registry<I> implements Lookup<I> {

    private final Map<String, I> map;

    Registry(final Map<String, I> map) {
        super();
        this.map = new ConcurrentHashMap<>(map);
    }

    @Override
    public I lookup(final String key) {
        if (key == null) {
            return null;
        }
        return map.get(TextUtils.toLowerCase(key));
    }

    @Override
    public String toString() {
        return map.toString();
    }

}

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

package org.apache.hc.core5.http.protocol;

/**
 * @since 5.0
 */
public enum UriPatternType {

    REGEX, URI_PATTERN, URI_PATTERN_IN_ORDER;

    /**
     * @deprecated Do not use.
     */
    @Deprecated
    public static <T> LookupRegistry<T> newMatcher(final UriPatternType type) {
        if (type == null) {
            return new UriPatternMatcher<>();
        }
        switch (type) {
        case REGEX:
            return new UriRegexMatcher<>();
        case URI_PATTERN_IN_ORDER:
            return new UriPatternOrderedMatcher<>();
        case URI_PATTERN:
        default:
            return new UriPatternMatcher<>();
        }
    }

}

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

package org.apache.hc.core5.http;

import java.util.Set;

/**
 * Details of an entity transmitted by a message.
 *
 * @since 5.0
 */
public interface EntityDetails {

    /**
     * Gets length of this entity, if known.
     *
     * @return the length of this entity, may be {@code 0}.
     */
    long getContentLength();

    /**
     * Gets content type of this entity, if known.
     *
     * @return the content type of this entity, may be {@code null}.
     */
    String getContentType();

    /**
     * Gets content encoding of this entity, if known.
     *
     * @return the content encoding of this entity, may be {@code null}.
     */
    String getContentEncoding();

    /**
     * Tests the chunked transfer hint for this entity.
     * <p>
     * The behavior of wrapping entities is implementation dependent,
     * but should respect the primary purpose.
     * </p>
     * @return the chunked transfer hint for this entity.
     */
    boolean isChunked();

    /**
     * Gets the preliminary declaration of trailing headers.
     *
     * @return the preliminary declaration of trailing headers.
     */
    Set<String> getTrailerNames();

}

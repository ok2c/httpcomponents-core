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

package org.apache.hc.core5.testing.compatibility;

import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.testing.compatibility.nio.Http2CompatTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class ApacheHttpDCompatIT {

    private static Network NETWORK = Network.newNetwork();
    @Container
    static final GenericContainer<?> HTTPD_CONTAINER = ContainerImages.apacheHttpD(NETWORK);

    @AfterAll
    static void cleanup() {
        HTTPD_CONTAINER.close();
    }

    static HttpHost targetContainerHost() {
        return new HttpHost(URIScheme.HTTP.id, HTTPD_CONTAINER.getHost(), HTTPD_CONTAINER.getMappedPort(ContainerImages.HTTP_PORT));
    }

    @Nested
    @DisplayName("HTTP/2, plain")
    class Http2 extends Http2CompatTest {

        public Http2() throws Exception {
            super(targetContainerHost());
        }

    }

}

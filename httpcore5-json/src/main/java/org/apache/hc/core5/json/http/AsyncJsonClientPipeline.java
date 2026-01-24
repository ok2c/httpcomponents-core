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
package org.apache.hc.core5.json.http;

import java.net.URI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.AsyncEntityConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.apache.hc.core5.http.nio.support.BasicResponseConsumer;
import org.apache.hc.core5.http.support.BasicRequestBuilder;
import org.apache.hc.core5.json.JsonConsumer;
import org.apache.hc.core5.json.JsonResultSink;
import org.apache.hc.core5.json.JsonTokenEventHandler;
import org.apache.hc.core5.util.Args;

/**
 * JSON {@link AsyncRequestProducer} and {@link AsyncResponseConsumer} pipeline assembler.
 *
 * @since 5.5
 */
public final class AsyncJsonClientPipeline {

    private final ObjectMapper objectMapper;

    public AsyncJsonClientPipeline(final ObjectMapper objectMapper) {
        this.objectMapper = Args.notNull(objectMapper, "Object mapper");
    }

    /**
     * Creates {@link AsyncRequestProducer} that generates an HTTP request
     * that can enclose JSON content.
     *
     * @param request the request message head.
     */
    public RequestStage request(final HttpRequest request) {
        return new RequestStage(request);
    }

    /**
     * Creates {@link AsyncRequestProducer} that generates an HTTP GET request.
     *
     * @param requestUri the request URI.
     * @return the request producer.
     */
    public AsyncRequestProducer get(final URI requestUri) {
        return AsyncRequestBuilder.get(requestUri)
                .addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString())
                .build();
    }

    /**
     * Creates {@link AsyncRequestProducer} that generates an HTTP POST request
     * that can enclose JSON content.
     *
     * @param requestUri the request URI.
     */
    public RequestStage post(final URI requestUri) {
        return request(BasicRequestBuilder.post(requestUri)
                .addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString())
                .build());
    }

    /**
     * Creates {@link AsyncRequestProducer} that generates an HTTP PUT request
     * that can enclose JSON content.
     *
     * @param requestUri the request URI.
     */
    public RequestStage put(final URI requestUri) {
        return request(BasicRequestBuilder.post(requestUri)
                .addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString())
                .build());
    }

    /**
     * Creates {@link AsyncRequestProducer} that generates an HTTP PATCH request
     * that can enclose JSON content.
     *
     * @param requestUri the request URI.
     */
    public RequestStage patch(final URI requestUri) {
        return request(BasicRequestBuilder.post(requestUri)
                .addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString())
                .build());
    }

    /**
     * Creates {@link AsyncRequestProducer} that generates an HTTP DELETE request.
     *
     * @param requestUri the request URI.
     * @return the request producer.
     */
    public AsyncRequestProducer delete(final URI requestUri) {
        return AsyncRequestBuilder.get(requestUri)
                .addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString())
                .build();
    }

    public ResponseStage response() {
        return new ResponseStage();
    }

    public class RequestStage {

        final HttpRequest request;

        private RequestStage(final HttpRequest request) {
            this.request = request;
        }

        /**
         * Creates {@link AsyncRequestProducer} enclosing a serialized JSON object as a message body.
         *
         * @return the request producer.
         */
        public <T> AsyncRequestProducer map(final T requestObject) {
            return new JsonRequestObjectProducer(request, requestObject != null ? new JsonObjectEntityProducer<>(requestObject, objectMapper) : null);
        }

        /**
         * Creates {@link AsyncRequestProducer} enclosing a sequence of serialized JSON object as a message body.
         *
         * @param objectProducer the JSON object producer.
         * @return the request producer.
         */
        public <T> AsyncRequestProducer mapStream(final ObjectProducer<T> objectProducer) {
            return new JsonRequestObjectProducer(request, new JsonSequenceEntityProducer<>(objectMapper, objectProducer));
        }

        /**
         * Creates {@link AsyncRequestProducer} enclosing JSON content generated using the provided {@link JsonGenerator}.
         *
         * @param consumer the recipient of JSON content generator.
         * @return the request producer.
         */
        public AsyncRequestProducer mapAsEvents(final JsonConsumer<JsonGenerator> consumer) {
            return new JsonRequestObjectProducer(request, new JsonTokenEntityProducer(objectMapper.getFactory(), consumer));
        }

    }

    public class ResponseStage {

        private ResponseStage() {
        }

        /**
         * Creates {@link AsyncResponseConsumer} that processes incoming response into a {@link Message} object
         * consisting of the {@link HttpResponse} head and the de-serialized JSON body.
         *
         * @param javaType the java type of the de-serialized object.
         * @param <T>      the type of result objects produced by the consumer.
         * @return the response consumer.
         */
        public <T> AsyncResponseConsumer<Message<HttpResponse, T>> map(final JavaType javaType) {
            return JsonResponseConsumers.create(objectMapper, javaType);
        }

        /**
         * Creates {@link AsyncResponseConsumer} that processes incoming response into a {@link Message} object
         * consisting of the {@link HttpResponse} head and the de-serialized JSON body.
         *
         * @param typeReference the type reference of the de-serialized object.
         * @param <T>           the type of result objects produced by the consumer.
         * @return the response consumer.
         */
        public <T> AsyncResponseConsumer<Message<HttpResponse, T>> map(final TypeReference<T> typeReference) {
            return JsonResponseConsumers.create(objectMapper, typeReference);
        }

        /**
         * Creates {@link AsyncResponseConsumer} that processes incoming response into a {@link Message} object
         * consisting of the {@link HttpResponse} head and the de-serialized JSON body.
         *
         * @param objectClazz the class of the de-serialized object.
         * @param <T>         the type of result objects produced by the consumer.
         * @return the response consumer.
         */
        public <T> AsyncResponseConsumer<Message<HttpResponse, T>> map(final Class<T> objectClazz) {
            return JsonResponseConsumers.create(objectMapper, objectClazz);
        }

        /**
         * Creates {@link AsyncResponseConsumer} that processes incoming response into a {@link Message} object
         * consisting of the {@link HttpResponse} head and the {@link JsonNode} body.
         *
         * @return the response consumer.
         */
        public AsyncResponseConsumer<Message<HttpResponse, JsonNode>> mapAsJsonNode() {
            return JsonResponseConsumers.create(objectMapper.getFactory());
        }

        /**
         * Creates {@link AsyncResponseConsumer} that produces a {@link Message} object
         * consisting of the {@link HttpRequest} head and request content body.
         *
         * @param <T> the type of result objects produced by the consumer.
         */
        public <T> AsyncResponseConsumer<Message<HttpResponse, T>> consume(final Supplier<AsyncEntityConsumer<T>> contentConsumerSupplier) {
            return new BasicResponseConsumer<>(contentConsumerSupplier);
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of instances of the given type.
         *
         * @param javaType the java type of the de-serialized object.
         */
        public ResponseStreamStage mapStream(final JavaType javaType) {
            return new ResponseStreamStage(javaType);
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of instances of the given class.
         *
         * @param objectClazz the class of the de-serialized object.
         */
        public <T> ResponseStreamStage mapStream(final Class<T> objectClazz) {
            return new ResponseStreamStage(objectMapper.constructType(objectClazz));
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of instances with the given type reference.
         *
         * @param typeReference the type reference of the de-serialized object.
         */
        public <T> ResponseStreamStage mapStream(final TypeReference<T> typeReference) {
            return new ResponseStreamStage(objectMapper.constructType(typeReference));
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of {@link JsonNode} instances.
         */
        public ResponseJsonStreamStage mapStreamAsJsonNodes() {
            return new ResponseJsonStreamStage();
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of JSON tokens passed as events to the given {@link JsonTokenEventHandler}.
         *
         * @param errorConsumerSupplier supplier of the custom error consumer
         * @param responseValidator     optional operation that accepts the message head as input.
         * @param errorCallback         optional operation that accepts the error message as input in case of an error.
         * @param eventHandler          JSON event handler
         * @param <E>                   the type of error object.
         * @return the response consumer.
         */
        public <E> AsyncResponseConsumer<Void> mapAsEvents(
                final Supplier<AsyncEntityConsumer<E>> errorConsumerSupplier,
                final JsonConsumer<HttpResponse> responseValidator,
                final Callback<E> errorCallback,
                final JsonTokenEventHandler eventHandler) {
            return JsonResponseConsumers.create(
                    objectMapper.getFactory(),
                    errorConsumerSupplier,
                    responseValidator,
                    errorCallback,
                    eventHandler);
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of JSON tokens passed as events to the given {@link JsonTokenEventHandler}.
         *
         * @param responseValidator optional operation that accepts the message head as input.
         * @param errorCallback     optional operation that accepts the error message as input in case of an error.
         * @param eventHandler      JSON event handler
         * @return the response consumer.
         */
        public AsyncResponseConsumer<Void> mapAsEvents(
                final JsonConsumer<HttpResponse> responseValidator,
                final Callback<String> errorCallback,
                final JsonTokenEventHandler eventHandler) {
            return JsonResponseConsumers.create(
                    objectMapper.getFactory(),
                    responseValidator,
                    errorCallback,
                    eventHandler);
        }

    }

    public class ResponseStreamStage {

        private final JavaType javaType;

        private ResponseStreamStage(final JavaType javaType) {
            this.javaType = javaType;
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of instances and passes those objects to the given
         * {@link JsonResultSink}.
         *
         * @param errorConsumerSupplier supplier of the custom error consumer
         * @param responseValidator     optional operation that accepts the message head as input.
         * @param errorCallback         optional operation that accepts the error message as input in case of an error.
         * @param resultSink            the recipient of result objects.
         * @param <T>                   type of result objects produced by the consumer.
         * @param <E>                   the type of error object.
         * @return the response consumer.
         */
        public <T, E> AsyncResponseConsumer<Long> handle(
                final Supplier<AsyncEntityConsumer<E>> errorConsumerSupplier,
                final JsonConsumer<HttpResponse> responseValidator,
                final Callback<E> errorCallback,
                final JsonResultSink<T> resultSink) {
            return JsonResponseConsumers.create(
                    objectMapper,
                    javaType,
                    errorConsumerSupplier,
                    responseValidator,
                    errorCallback,
                    resultSink);
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of instances and passes those objects to the given
         * {@link JsonResultSink}.
         *
         * @param responseValidator optional operation that accepts the message head as input.
         * @param errorCallback     optional operation that accepts the error message as input in case of an error.
         * @param resultSink        the recipient of result objects.
         * @param <T>               type of result objects produced by the consumer.
         * @return the response consumer.
         */
        public <T, E> AsyncResponseConsumer<Long> handle(
                final JsonConsumer<HttpResponse> responseValidator,
                final Callback<String> errorCallback,
                final JsonResultSink<T> resultSink) {
            return JsonResponseConsumers.create(
                    objectMapper,
                    javaType,
                    responseValidator,
                    errorCallback,
                    resultSink);
        }

    }

    public class ResponseJsonStreamStage {

        private ResponseJsonStreamStage() {
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of {@link JsonNode} instances and passes those objects to the given
         * {@link JsonResultSink}.
         *
         * @param errorConsumerSupplier supplier of the custom error consumer
         * @param responseValidator     optional operation that accepts the message head as input.
         * @param errorCallback         optional operation that accepts the error message as input in case of an error.
         * @param resultSink            the recipient of result objects.
         * @param <E>                   the type of error object.
         * @return the response consumer.
         */
        public <E> AsyncResponseConsumer<Long> handle(
                final Supplier<AsyncEntityConsumer<E>> errorConsumerSupplier,
                final JsonConsumer<HttpResponse> responseValidator,
                final Callback<E> errorCallback,
                final JsonResultSink<JsonNode> resultSink) {
            return JsonResponseConsumers.create(
                    objectMapper,
                    errorConsumerSupplier,
                    responseValidator,
                    errorCallback,
                    resultSink);
        }

        /**
         * Creates {@link AsyncResponseConsumer} that converts incoming HTTP message
         * into a sequence of {@link JsonNode} instances and passes those objects to the given
         * {@link JsonResultSink}.
         *
         * @param responseValidator optional operation that accepts the message head as input.
         * @param errorCallback     optional operation that accepts the error message as input in case of an error.
         * @param resultSink        the recipient of result objects.
         * @return the response consumer.
         */
        public AsyncResponseConsumer<Long> handle(
                final JsonConsumer<HttpResponse> responseValidator,
                final Callback<String> errorCallback,
                final JsonResultSink<JsonNode> resultSink) {
            return JsonResponseConsumers.create(
                    objectMapper,
                    responseValidator,
                    errorCallback,
                    resultSink);
        }

    }

}

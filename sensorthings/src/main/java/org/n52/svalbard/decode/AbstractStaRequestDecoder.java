/*
 * Copyright 2016-2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.svalbard.decode;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.decode.json.JSONDecoder;

/**
 * SensorThings API request decoder
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public abstract class AbstractStaRequestDecoder<T extends OwsServiceRequest> extends JSONDecoder<T> {

    public AbstractStaRequestDecoder(Class<T> type, String service, String version, String operation) {
        super(new HashSet<>(Arrays.asList(new JsonDecoderKey(type),
                new OperationDecoderKey(service, version, operation, MediaTypes.APPLICATION_STA))));
    }

    @Override
    public T decodeJSON(JsonNode node, boolean validate) throws DecodingException {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
//        if (validate) {
//            JSONValidator.getInstance().validateAndThrow(node, getSchemaURI());
//        }
        T t = decodeRequest(node);
//        t.setExtensions(parseExtensions(node.path(JSONConstants.EXTENSIONS)));
        return t;
    }

    protected abstract T decodeRequest(JsonNode node) throws DecodingException;

    public abstract void setResource(StaConstants.PathSegment resource);

    public abstract void setPath(List<StaConstants.PathSegment> path);

    public abstract void setQueryOptions(Map<StaConstants.QueryOption, String> queryOptions);
}

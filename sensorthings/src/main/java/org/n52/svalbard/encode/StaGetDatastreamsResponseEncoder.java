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
package org.n52.svalbard.encode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.n52.shetland.ogc.sta.StaDatastream;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.response.StaGetDatastreamsResponse;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response encoder for the SensorThings API Datastreams resource.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetDatastreamsResponseEncoder extends JSONEncoder<StaGetDatastreamsResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetDatastreamsResponseEncoder.class);

    public StaGetDatastreamsResponseEncoder() {

        // TODO differ GET Observations from GET Datastreams
        super(StaGetDatastreamsResponse.class,
                new OperationResponseEncoderKey(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0,
                        StaConstants.Operation.GET_DATASTREAMS, MediaTypes.APPLICATION_STA));
    }

    protected void encodeResponse(ObjectNode json, StaGetDatastreamsResponse t) throws EncodingException {
        // add nodes
        ArrayNode array = json.putArray(StaConstants.VALUES);

        // encode entities
        Iterator<StaDatastream> iterator = t.getDatastreams().iterator();

        while(iterator.hasNext()) {
            array.add(encodeObjectToJson(iterator.next()));
        }

        // add basic information
        json.put(StaConstants.ANNOTATION_COUNT, t.getDatastreams().size());
    }

    @Override
    public JsonNode encodeJSON(StaGetDatastreamsResponse t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }

    private String getObservationType(OmObservation o) {

        if (o.getValue().getValue().getClass() == CategoryValue.class) {
            return StaConstants.OBSERVATION_TYPE_CATEGORY_OBSERVATION;

        } else if (o.getValue().getValue().getClass() == CountValue.class) {
            return StaConstants.OBSERVATION_TYPE_COUNT_OBSERVATION;

        } else if (o.getValue().getValue().getClass() == QuantityValue.class) {
            return StaConstants.OBSERVATION_TYPE_MEASUREMENT;

        } else if (o.getValue().getValue().getClass() == BooleanValue.class) {
            return StaConstants.OBSERVATION_TYPE_TRUTH_OBSERVATION;

        } else {
            return StaConstants.OBSERVATION_TYPE_OBSERVATION;
        }
    }
}

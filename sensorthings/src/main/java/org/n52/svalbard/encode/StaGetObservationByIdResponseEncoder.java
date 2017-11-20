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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request encoder for the SensorThings API Observations resource, used by GetObservationById operations;
 * transforms SOS Observations into SensorThings Observations.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservationByIdResponseEncoder extends JSONEncoder<GetObservationByIdResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetObservationByIdResponseEncoder.class);

    public StaGetObservationByIdResponseEncoder() {

        super(GetObservationByIdResponse.class,
            new OperationResponseEncoderKey(Sos1Constants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.EN_GET_OBSERVATION_BY_ID, MediaTypes.APPLICATION_STA));
    }

    protected void encodeResponse(ObjectNode json, GetObservationByIdResponse t) throws EncodingException {

        ArrayNode dsArray = json.putArray(StaConstants.VALUES);

        // encode observations
        int observationCount = 0;
        Encoder converter = new StaObservationConverter();

        try {
            while (t.getObservationCollection().hasNext()) {
                OmObservation o = t.getObservationCollection().next();
                if (o.getValue() instanceof ObservationStream) {
                    ObservationStream value = (ObservationStream) o.getValue();
                        while (value.hasNext()) {
                            dsArray.add(encodeObjectToJson(converter.encode(value.next())));
                            observationCount++;
                    }
                } else {
                    dsArray.add(encodeObjectToJson(converter.encode(o)));
                    observationCount++;
                }
            }
        } catch (OwsExceptionReport ex) {
            throw new EncodingException(ex);
        }

        // add basic information
        json.put(StaConstants.ANNOTATION_COUNT, observationCount);
    }

    @Override
    public JsonNode encodeJSON(GetObservationByIdResponse t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }
}

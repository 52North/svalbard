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
import java.util.Iterator;
import org.n52.janmayen.Json;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaSensor;
import org.n52.shetland.ogc.sta.response.StaGetSensorsResponse;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request encoder for the SensorThings API Sensor resource.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetSensorsResponseEncoder extends JSONEncoder<StaGetSensorsResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetSensorsResponseEncoder.class);

    public StaGetSensorsResponseEncoder() {

        super(StaGetSensorsResponse.class,
            new OperationResponseEncoderKey(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0,
                StaConstants.Operation.GET_SENSORS, MediaTypes.APPLICATION_STA));
    }

    protected void encodeResponse(ObjectNode json, StaGetSensorsResponse t) throws EncodingException {

        ArrayNode dsArray = json.putArray(StaConstants.VALUES);

        // encode features
        int sensorCount = 0;
        Iterator<StaSensor> iterator = t.getSensors().iterator();

        while(iterator.hasNext()) {

            dsArray.add(encodeObjectToJson(iterator.next()));
            sensorCount++;
        }

        // add basic information
        json.put(StaConstants.ANNOTATION_COUNT, sensorCount);
    }

    @Override
    public JsonNode encodeJSON(StaGetSensorsResponse t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }
}

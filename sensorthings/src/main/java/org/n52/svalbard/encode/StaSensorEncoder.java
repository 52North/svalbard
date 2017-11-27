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
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Base64;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaSensor;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorThings Sensor to JSON
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaSensorEncoder extends JSONEncoder<StaSensor> {

    private static final Logger LOG = LoggerFactory.getLogger(StaSensorEncoder.class);

    public StaSensorEncoder() {
        super(StaSensor.class);
    }

    @Override
    public JsonNode encodeJSON(StaSensor sensor) throws EncodingException {

        ObjectNode json = nodeFactory().objectNode();

        json.put(StaConstants.ANNOTATION_ID, sensor.getId())
            .put(StaConstants.ANNOTATION_SELF_LINK, sensor.getSelfLink());

        // entities
        if (sensor.getDatastreams() != null && !sensor.getDatastreams().isEmpty()) {
            // TODO add expand option
        } else {
            json.put(StaConstants.EntitySet.Datastreams.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                    sensor.getChildrensBaseURL() + StaConstants.EntitySet.Datastreams.name());
        }

        // parameters, mandatory
        json.put(StaConstants.Parameter.name.name(), sensor.getName())
            .put(StaConstants.Parameter.description.name(), sensor.getDescription())
            .put(StaConstants.Parameter.encodingType.name(), sensor.getEncodingType());

        // TODO encode metadata (as file link or SensorML 2.0, but in xml encoding)
        if (sensor.getMetadata() != null && !sensor.getMetadata().equals("")) {
            
            if (sensor.getEncodingType().equals(StaConstants.SENSOR_ENCODING_TYPE_SENSORML_20)) {
                
                String metadata = Base64.getEncoder().encodeToString(((String) sensor.getMetadata()).getBytes());
                json.put(StaConstants.Parameter.metadata.name(), metadata);
                
            } else if (sensor.getEncodingType().equals(StaConstants.SENSOR_ENCODING_TYPE_PDF)) {
                
                json.put(StaConstants.Parameter.metadata.name(), (String) sensor.getMetadata());
            } else {
                // TODO throw exception
                LOG.warn("Sensor metadata could not be encoded: encoding type not supported");
            }
        } else {
            // TODO throw exception
            LOG.warn("Sensor metadata could not be encoded: no metadata");
        }

        return json;
    }
}

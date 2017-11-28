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
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaObservedProperty;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorThings ObservedProperty to JSON
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaObservedPropertyEncoder extends JSONEncoder<StaObservedProperty> {

    private static final Logger LOG = LoggerFactory.getLogger(StaObservedPropertyEncoder.class);

    public StaObservedPropertyEncoder() {
        super(StaObservedProperty.class);
    }

    @Override
    public JsonNode encodeJSON(StaObservedProperty sensor) throws EncodingException {

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
            .put(StaConstants.Parameter.definition.name(), sensor.getDefinition());

        return json;
    }
}

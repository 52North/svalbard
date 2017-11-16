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
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaSettings;
import org.n52.shetland.ogc.sta.response.StaGetEntitySetsResponse;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.json.JSONEncoder;

/**
 * Response encoder for a GET request without a specific resource. URLs to all EntitySets are returned.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetEntitySetsResponseEncoder extends JSONEncoder<StaGetEntitySetsResponse> {

    public StaGetEntitySetsResponseEncoder() {

        super(StaGetEntitySetsResponse.class,
            new OperationResponseEncoderKey(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0,
                StaConstants.Operation.GET_ENTITY_SETS.name(), MediaTypes.APPLICATION_STA));
    }

    @Override
    public JsonNode encodeJSON(StaGetEntitySetsResponse t) throws EncodingException {
        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }

    protected void encodeResponse(ObjectNode json, StaGetEntitySetsResponse t) throws EncodingException {
        ArrayNode dsArray = json.putArray(StaConstants.VALUES);

        // add entity set base paths
        ObjectNode thingNode = nodeFactory().objectNode();
        thingNode.put(StaConstants.EntitySetListParameter.name.name(), StaConstants.EntitySet.Things.name())
                .put(StaConstants.EntitySetListParameter.url.name(), StaSettings.getInstance().getBaseURL() + StaConstants.EntitySet.Things.name());
        dsArray.add(thingNode);

        ObjectNode locationNode = nodeFactory().objectNode();
        locationNode.put(StaConstants.EntitySetListParameter.name.name(), StaConstants.EntitySet.Locations.name())
                .put(StaConstants.EntitySetListParameter.url.name(), StaSettings.getInstance().getBaseURL() + StaConstants.EntitySet.Locations.name());
        dsArray.add(locationNode);

        ObjectNode historicalLocationNode = nodeFactory().objectNode();
        historicalLocationNode.put(StaConstants.EntitySetListParameter.name.name(), StaConstants.EntitySet.HistoricalLocations.name())
                .put(StaConstants.EntitySetListParameter.url.name(), StaSettings.getInstance().getBaseURL() + StaConstants.EntitySet.HistoricalLocations.name());
        dsArray.add(historicalLocationNode);

        ObjectNode datastreamNode = nodeFactory().objectNode();
        datastreamNode.put(StaConstants.EntitySetListParameter.name.name(), StaConstants.EntitySet.Datastreams.name())
                .put(StaConstants.EntitySetListParameter.url.name(), StaSettings.getInstance().getBaseURL() + StaConstants.EntitySet.Datastreams.name());
        dsArray.add(datastreamNode);

        ObjectNode sensorNode = nodeFactory().objectNode();
        sensorNode.put(StaConstants.EntitySetListParameter.name.name(), StaConstants.EntitySet.Sensors.name())
                .put(StaConstants.EntitySetListParameter.url.name(), StaSettings.getInstance().getBaseURL() + StaConstants.EntitySet.Sensors.name());
        dsArray.add(sensorNode);

        ObjectNode observedPropertyNode = nodeFactory().objectNode();
        observedPropertyNode.put(StaConstants.EntitySetListParameter.name.name(), StaConstants.EntitySet.ObservedProperties.name())
                .put(StaConstants.EntitySetListParameter.url.name(), StaSettings.getInstance().getBaseURL() + StaConstants.EntitySet.ObservedProperties.name());
        dsArray.add(observedPropertyNode);

        ObjectNode observationNode = nodeFactory().objectNode();
        observationNode.put(StaConstants.EntitySetListParameter.name.name(), StaConstants.EntitySet.Observations.name())
                .put(StaConstants.EntitySetListParameter.url.name(), StaSettings.getInstance().getBaseURL() + StaConstants.EntitySet.Observations.name());
        dsArray.add(observationNode);

        ObjectNode featureOfInterestNode = nodeFactory().objectNode();
        featureOfInterestNode.put(StaConstants.EntitySetListParameter.name.name(), StaConstants.EntitySet.FeaturesOfInterest.name())
                .put(StaConstants.EntitySetListParameter.url.name(), StaSettings.getInstance().getBaseURL() + StaConstants.EntitySet.FeaturesOfInterest.name());
        dsArray.add(featureOfInterestNode);
    }
}

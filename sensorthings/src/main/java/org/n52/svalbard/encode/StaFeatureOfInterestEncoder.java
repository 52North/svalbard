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
import com.vividsolutions.jts.geom.Geometry;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaFeatureOfInterest;
import org.n52.svalbard.coding.json.JSONConstants;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.json.GeoJSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorThings FeatureOfInterest to JSON
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaFeatureOfInterestEncoder extends JSONEncoder<StaFeatureOfInterest> {

    private static final Logger LOG = LoggerFactory.getLogger(StaObservationEncoder.class);

    public StaFeatureOfInterestEncoder() {
        super(StaFeatureOfInterest.class);
    }

    @Override
    public JsonNode encodeJSON(StaFeatureOfInterest featureOfInterest) throws EncodingException {

        ObjectNode json = nodeFactory().objectNode();
        GeoJSONEncoder geoEncoder = new GeoJSONEncoder();

        json.put(StaConstants.ANNOTATION_ID, featureOfInterest.getId())
            .put(StaConstants.ANNOTATION_SELF_LINK, featureOfInterest.getSelfLink());

        // entities
        if (featureOfInterest.isSetObservationList()) {
            // TODO add expand option
        } else {
            json.put(StaConstants.EntitySet.Observations.name() + StaConstants.ANNOTATION_NAVIGATION_LINK,
                    featureOfInterest.getChildrensBaseURL() + StaConstants.EntitySet.Observations.name());
        }

        // parameters, mandatory
        json.put(StaConstants.Parameter.name.name(), featureOfInterest.getName())
            .put(StaConstants.Parameter.description.name(), featureOfInterest.getDescription())
            .put(StaConstants.Parameter.encodingType.name(), featureOfInterest.getEncodingType());

        json.set(StaConstants.Parameter.feature.name(), encodeFeature(geoEncoder, featureOfInterest.getFeature()));

        return json;
    }

    private JsonNode encodeFeature(GeoJSONEncoder encoder, Geometry g) {
        ObjectNode json = nodeFactory().objectNode();

        json.put(StaConstants.FoiParameter.type.name(), JSONConstants.POINT);

        try {
            json.set(StaConstants.FoiParameter.geometry.name(), encoder.encode(g));
        } catch (EncodingException ex) {
            LOG.error("Error when encoding SensorThings FeatureOfInterest geometry.", ex);
        }

        return json;
    }
}

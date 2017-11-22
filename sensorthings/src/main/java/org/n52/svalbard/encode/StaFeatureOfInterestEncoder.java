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
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.CoordinateFilter;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaFeatureOfInterest;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorThings FeatureOfInterest to JSON
 *
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

        json.set(StaConstants.Parameter.feature.name(), encodeObjectToJson(switchLatLon(featureOfInterest.getFeature())));

        return json;
    }

    private Geometry switchLatLon(Geometry geometry) {
        
        Geometry g = (Geometry) geometry.clone();
        g.apply(new SwitchCoordinateFilter());
        
        return g;
    }
    
    private class SwitchCoordinateFilter implements CoordinateFilter  {
        
        @Override
        public void filter(Coordinate coordinates) {
            double x = coordinates.x;
            coordinates.x = coordinates.y;
            coordinates.y = x;
        }
    }
}

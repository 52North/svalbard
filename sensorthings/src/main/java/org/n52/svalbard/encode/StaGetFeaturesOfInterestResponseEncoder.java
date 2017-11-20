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
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.svalbard.encode.json.JSONEncoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request encoder for the SensorThings API FeaturesOfInterest resource, used by GetFeatureOfInterest operations.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetFeaturesOfInterestResponseEncoder extends JSONEncoder<GetFeatureOfInterestResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(StaGetFeaturesOfInterestResponseEncoder.class);

    public StaGetFeaturesOfInterestResponseEncoder() {

        super(GetFeatureOfInterestResponse.class,
            new OperationResponseEncoderKey(Sos1Constants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.EN_GET_FEATURE_OF_INTEREST, MediaTypes.APPLICATION_STA));
    }

    protected void encodeResponse(ObjectNode json, GetFeatureOfInterestResponse t) throws EncodingException {

        ArrayNode dsArray = json.putArray(StaConstants.VALUES);

        // encode features
        int featureCount = 0;
        //Encoder converter = encoderRepository.getEncoder(new StaEncoderKey(StaFeatureOfInterest.class, AbstractFeature.class));
        Encoder converter = new StaFeatureOfInterestConverter();

        // only collections of single features and single features are processed
        if (t.getAbstractFeature() instanceof FeatureCollection) {
            Iterator<AbstractFeature> iterator = ((FeatureCollection) t.getAbstractFeature()).iterator();

            while(iterator.hasNext()) {
                AbstractFeature f = iterator.next();

                dsArray.add(encodeObjectToJson(converter.encode(f)));
                //dsArray.add(encodeObjectToJson(transformFeatureOfInterest(f)));
                featureCount++;
            }
        } else {
            dsArray.add(encodeObjectToJson(converter.encode(t.getAbstractFeature())));
            //dsArray.add(encodeObjectToJson(transformFeatureOfInterest(t.getAbstractFeature())));
            featureCount++;
        }

        // add basic information
        json.put(StaConstants.ANNOTATION_COUNT, featureCount);
    }

    @Override
    public JsonNode encodeJSON(GetFeatureOfInterestResponse t) throws EncodingException {

        ObjectNode n = Json.nodeFactory().objectNode();
        encodeResponse(n, t);

        return n;
    }
}

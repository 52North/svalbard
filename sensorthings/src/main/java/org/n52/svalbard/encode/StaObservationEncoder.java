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
import java.util.Map;
import java.util.Set;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.SupportedType;

import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encoder for the SensorThings API Observation Entity
 *
 * @author Martin Kiesow
 */
public class StaObservationEncoder<OmObservation> extends AbstractDelegatingEncoder<JsonNode, OmObservation> implements ObservationEncoder<JsonNode, OmObservation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaObservationEncoder.class);

    @Override
    public JsonNode encode(OmObservation objectToEncode) throws EncodingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JsonNode encode(OmObservation objectToEncode, EncodingContext additionalValues) throws EncodingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MediaType getContentType() {
        // TODO separate ContenType "om+json"?
        return MediaTypes.APPLICATION_JSON;
    }

    @Override
    public Set<EncoderKey> getKeys() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> getSupportedResponseFormats(String service, String version) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Set<SupportedType>> getSupportedResponseFormatObservationTypes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}

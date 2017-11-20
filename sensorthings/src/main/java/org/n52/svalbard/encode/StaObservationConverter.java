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

import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaFeatureOfInterest;
import org.n52.shetland.ogc.sta.StaObservation;
import org.n52.svalbard.encode.exception.EncodingException;

/**
 * Class to convert {@link OmObservation} to STA Observation.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaObservationConverter extends StaAbstractEntityConverter<StaObservation, OmObservation>{

    public StaObservationConverter() {
        super(StaObservation.class, OmObservation.class);
    }

    @Override
    public StaObservation encode(OmObservation o) throws EncodingException {

        StaObservation staObservation = new StaObservation(o.getObservationID());

        staObservation.setPhenomenonTime(o.getPhenomenonTime());
        staObservation.setResult(o.getValue().getValue().getValue().toString());

        staObservation.setResultTime(o.getResultTime());
//        private String resultQuality; // [0..n]
        staObservation.setValidTime(o.getValidTime());
//        private String parameters; // [0..1]

        // TODO create Datastream id from procedure, observedProperty, offering and featureOfInterest
        // not necessary for GET Observations
        //staObservation.setDatastream(datastreamID);

        StaFeatureOfInterestConverter converter = new StaFeatureOfInterestConverter();
        staObservation.setFeatureOfInterest(
                converter.encode(o.getObservationConstellation().getFeatureOfInterest()));

        // TODO add and test "parameters"

        return staObservation;
    }

    @Override
    public StaObservation encode(OmObservation objectToEncode, EncodingContext additionalValues) throws EncodingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MediaType getContentType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

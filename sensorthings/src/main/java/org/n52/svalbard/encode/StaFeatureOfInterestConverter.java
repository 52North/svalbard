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
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.StaFeatureOfInterest;
import org.n52.svalbard.encode.exception.EncodingException;

/**
 * Class to convert {@link AbstractFeature} to STA FeatureOfInterest.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaFeatureOfInterestConverter extends StaAbstractEntityConverter<StaFeatureOfInterest, AbstractFeature>{

    public StaFeatureOfInterestConverter() {
        super(StaFeatureOfInterest.class, AbstractFeature.class);
    }

    @Override
    public StaFeatureOfInterest encode(AbstractFeature feature) throws EncodingException {

        StringBuilder name = new StringBuilder();
        feature.getName().forEach((CodeType ct) -> name.append(ct.getValue()));

        StaFeatureOfInterest staFOI = new StaFeatureOfInterest(feature.getFeatureId());
        staFOI.setName(name.toString());
        staFOI.setDescription(feature.getDescription());
        staFOI.setEncodingType(StaConstants.SPATIAL_ENCODING_TYPE_GEOJSON);

        if (feature instanceof SamplingFeature) {
            staFOI.setFeature(((SamplingFeature) feature).getGeometry());
        }

        return staFOI;
    }

    @Override
    public StaFeatureOfInterest encode(AbstractFeature objectToEncode, EncodingContext additionalValues) throws EncodingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MediaType getContentType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

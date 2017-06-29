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

import java.util.HashMap;
import java.util.Set;

import net.opengis.sos.x20.GetObservationByIdResponseDocument;
import net.opengis.sos.x20.GetObservationByIdResponseType;

import org.apache.xmlbeans.XmlObject;

import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.svalbard.SosHelperValues;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.util.XmlHelper;

import com.google.common.collect.Sets;

import org.n52.shetland.ogc.om.ObservationStream;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class GetObservationByIdResponseEncoder extends AbstractObservationResponseEncoder<GetObservationByIdResponse> {
    public static final String GML_ID = "sf_1";

    public GetObservationByIdResponseEncoder() {
        super(SosConstants.Operations.GetObservationById.name(), GetObservationByIdResponse.class);
    }

    @Override
    protected XmlObject createResponse(ObservationEncoder<XmlObject, OmObservation> encoder,
                                       GetObservationByIdResponse response) throws EncodingException {
        GetObservationByIdResponseDocument doc = GetObservationByIdResponseDocument.Factory.newInstance(getXmlOptions());
        GetObservationByIdResponseType xbResponse = doc.addNewGetObservationByIdResponse();

        ObservationStream observations = response.getObservationCollection();
        HashMap<CodeWithAuthority, String> gmlID4sfIdentifier = new HashMap<>();
        try {
            while (observations.hasNext()) {
                OmObservation observation = observations.next();
                EncodingContext codingContext = EncodingContext.empty();
                CodeWithAuthority foiId = observation.getObservationConstellation().getFeatureOfInterest()
                        .getIdentifierCodeWithAuthority();
                if (gmlID4sfIdentifier.containsKey(foiId)) {
                    codingContext = codingContext.with(SosHelperValues.EXIST_FOI_IN_DOC, true);
                } else {
                    gmlID4sfIdentifier.put(foiId, GML_ID);
                    codingContext = codingContext.with(SosHelperValues.EXIST_FOI_IN_DOC, false);
                }
                codingContext = codingContext.with(SosHelperValues.GMLID, gmlID4sfIdentifier.get(foiId));
                xbResponse.addNewObservation().addNewOMObservation().set(encoder.encode(observation, codingContext));

            }
        } catch (OwsExceptionReport ex) {
            throw new EncodingException(ex);
        }
        XmlHelper.makeGmlIdsUnique(xbResponse.getDomNode());
        return doc;
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_OBSERVATION_BY_ID_SCHEMA_LOCATION);
    }
}

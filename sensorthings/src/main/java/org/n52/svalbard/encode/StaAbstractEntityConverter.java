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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.n52.shetland.ogc.sta.StaAbstractEntity;

/**
 * Abstract super class for all STA Entity converters.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public abstract class StaAbstractEntityConverter<T extends StaAbstractEntity, S> implements Encoder<T, S> {

//    private EncoderRepository encoderRepository;
    private Set<StaEncoderKey> keys;

    public StaAbstractEntityConverter(Class<T> target, Class<S> source) {
        keys = new HashSet<StaEncoderKey>();
        keys.add(new StaEncoderKey(target, source));
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(keys);
    }

//    @Inject
//    public void setEncoderRepository(EncoderRepository encoderRepository) {
//        this.encoderRepository = Objects.requireNonNull(encoderRepository);
//    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.fhirfactory.pegacorn.internals.events;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mark A. Hunter (ACT Health)
 */
public class PegacornEventExtensionSet {

    private static final Logger LOG = LoggerFactory.getLogger(PegacornEventExtensionSet.class);
    protected Logger getLogger(){
        return(LOG);
    }

    public ArrayList<Extension> extensionList;

    public PegacornEventExtensionSet() {
        getLogger().debug("constructure(): Entry - empty constructor");
        this.extensionList = new ArrayList<Extension>();
    }

    public PegacornEventExtensionSet(Extension sourceExtensionEntity) {
        getLogger().debug("constructor(): Entry - source (single) Extension Entity provided");
        this.extensionList.add(sourceExtensionEntity.copy());
    }

    public PegacornEventExtensionSet(List<Extension> sourceExtensionEntityList) {
        getLogger().debug(" constructor(): Entry - source (list) Extension Entity provided");
        this.extensionList.addAll(extensionList);
    }

    public List<Extension> getExtensionList() {
        return (this.extensionList);
    }

}

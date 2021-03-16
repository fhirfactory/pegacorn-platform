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
package net.fhirfactory.pegacorn.fhir.validation;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mark A. Hunter (ACT Health)
 */
public class SimpleValidatorBase {
        private static final Logger LOG = LoggerFactory.getLogger(SimpleValidatorBase.class);
        public boolean isValidJSONObject(String pGroupJSONString) {
        LOG.debug("isSimplyValid(String p): Entry");
        if (pGroupJSONString == null) {
            LOG.debug("isSimplyValid(String p): Exit, parameter is null");
            return (false);
        }
        if (pGroupJSONString.isEmpty()) {
            LOG.debug("isSimplyValid(String p): Exit, parameter is empty");
            return (false);
        }
        try {
            LOG.debug("isSimplyValid(String p): Exit, parameter is valid JSON object");
            JSONObject jsonGeneric = new JSONObject(pGroupJSONString);
            return (true);
        } catch (Exception Ex) {
            LOG.debug("isSimplyValid(String p): Exception, Exit, parameter is not a valid JSON Object");
            return (false);
        }
    }
}

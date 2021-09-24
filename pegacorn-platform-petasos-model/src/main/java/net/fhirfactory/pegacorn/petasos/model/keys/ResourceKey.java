/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.petasos.model.keys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceKey {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceKey.class);
    
    IdType idTypeKey;
    Identifier identifierTypeKey;

    public ResourceKey(IdType id){
        this.idTypeKey = id;
        this.identifierTypeKey = null;
    }

    public ResourceKey(Identifier identifier){
        this.identifierTypeKey = identifier;
        this.idTypeKey = null;
    }

    public boolean hasIdTypeKey(){
        return(this.idTypeKey != null);
    }

    public IdType getIdTypeKey() {
        return idTypeKey;
    }

    public boolean hasIdentifierTypeKey(){
        return(this.identifierTypeKey != null);
    }

    public Identifier getIdentifierTypeKey() {
        return identifierTypeKey;
    }

    public String toString(){
        if(hasIdentifierTypeKey()) {
            ObjectMapper mapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            mapper.registerModule(module);
            try {
                String keyAsString = mapper.writeValueAsString(getIdentifierTypeKey());
                return(keyAsString);
            } catch (JsonProcessingException e) {
                LOG.warn("Ignoring exception " + e.getMessage() + " and returning Empty Identifier", e);
                return("Empty Identifier");
            }
        } else {
            return(getIdTypeKey().toString());
        }
    }
}

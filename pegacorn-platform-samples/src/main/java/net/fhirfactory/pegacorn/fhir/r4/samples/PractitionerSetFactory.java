/*
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter (ACT Health).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.fhir.r4.samples;

import java.util.HashSet;
import java.util.Set;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;

import org.hl7.fhir.r4.model.Practitioner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author Mark A. Hunter
 */
@ApplicationScoped
public class PractitionerSetFactory {

    @Inject
    FHIRContextUtility fhirContextUtility;

    public Set<Practitioner> getPractitionerSet(){
        HashSet<Practitioner> practitionerSet = new HashSet<Practitioner>();
        IParser parserR4 = fhirContextUtility.getJsonParser();
        for(String currentPractitionerString: getPractitionerJSONStringSet()){
            Practitioner newPractitioner = (Practitioner)parserR4.parseResource(currentPractitionerString);
            practitionerSet.add(newPractitioner);
        }
        return(practitionerSet);
    }
    
    public Set<String> getPractitionerJSONStringSet(){
        HashSet<String> practitionerSet = new HashSet<String>();
        practitionerSet.add(practitioner0);
        practitionerSet.add(practitioner1);
        practitionerSet.add(practitioner2);
        practitionerSet.add(practitioner3);
        practitionerSet.add(practitioner4);        
        return(practitionerSet);
    }

    String practitioner0 = "{\n"
            + "  \"resourceType\": \"Practitioner\",\n"
            + "  \"id\": \"f204\",\n"
            + "  \"text\": {\n"
            + "    \"status\": \"generated\",\n"
            + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f204</p><p><b>identifier</b>: UZI-nummer = 12345678904 (OFFICIAL)</p><p><b>name</b>: Carla Espinosa</p><p><b>telecom</b>: ph: +31715262169(WORK)</p><p><b>address</b>: Walvisbaai 3 Den helder 2333ZA NLD (WORK)</p><p><b>gender</b>: female</p><p><b>birthDate</b>: 05/11/1967</p></div>\"\n"
            + "  },\n"
            + "  \"identifier\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"type\": {\n"
            + "        \"text\": \"UZI-nummer\"\n"
            + "      },\n"
            + "      \"system\": \"urn:oid:2.16.528.1.1007.3.1\",\n"
            + "      \"value\": \"12345678904\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"name\": [\n"
            + "    {\n"
            + "      \"use\": \"usual\",\n"
            + "      \"text\": \"Carla Espinosa\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"telecom\": [\n"
            + "    {\n"
            + "      \"system\": \"phone\",\n"
            + "      \"value\": \"+31715262169\",\n"
            + "      \"use\": \"work\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"address\": [\n"
            + "    {\n"
            + "      \"use\": \"work\",\n"
            + "      \"line\": [\n"
            + "        \"Walvisbaai 3\"\n"
            + "      ],\n"
            + "      \"city\": \"Den helder\",\n"
            + "      \"postalCode\": \"2333ZA\",\n"
            + "      \"country\": \"NLD\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"gender\": \"female\",\n"
            + "  \"birthDate\": \"1967-11-05\"\n"
            + "}";

    String practitioner1 = "{\n"
            + "  \"resourceType\": \"Practitioner\",\n"
            + "  \"id\": \"f002\",\n"
            + "  \"text\": {\n"
            + "    \"status\": \"generated\",\n"
            + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f002</p><p><b>identifier</b>: 730291637 (OFFICIAL), 174BIP3JH438 (USUAL)</p><p><b>name</b>: Pieter Voigt (OFFICIAL)</p><p><b>telecom</b>: ph: 0205569336(WORK), p.voigt@bmc.nl(WORK), fax: 0205669382(WORK)</p><p><b>address</b>: Galapagosweg 91 Den Burg 9105 PZ NLD (WORK)</p><p><b>gender</b>: male</p><p><b>birthDate</b>: 29/04/1979</p></div>\"\n"
            + "  },\n"
            + "  \"identifier\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"system\": \"urn:oid:2.16.528.1.1007.3.1\",\n"
            + "      \"value\": \"730291637\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"use\": \"usual\",\n"
            + "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\",\n"
            + "      \"value\": \"174BIP3JH438\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"name\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"family\": \"Voigt\",\n"
            + "      \"given\": [\n"
            + "        \"Pieter\"\n"
            + "      ],\n"
            + "      \"suffix\": [\n"
            + "        \"MD\"\n"
            + "      ]\n"
            + "    }\n"
            + "  ],\n"
            + "  \"telecom\": [\n"
            + "    {\n"
            + "      \"system\": \"phone\",\n"
            + "      \"value\": \"0205569336\",\n"
            + "      \"use\": \"work\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"system\": \"email\",\n"
            + "      \"value\": \"p.voigt@bmc.nl\",\n"
            + "      \"use\": \"work\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"system\": \"fax\",\n"
            + "      \"value\": \"0205669382\",\n"
            + "      \"use\": \"work\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"address\": [\n"
            + "    {\n"
            + "      \"use\": \"work\",\n"
            + "      \"line\": [\n"
            + "        \"Galapagosweg 91\"\n"
            + "      ],\n"
            + "      \"city\": \"Den Burg\",\n"
            + "      \"postalCode\": \"9105 PZ\",\n"
            + "      \"country\": \"NLD\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"gender\": \"male\",\n"
            + "  \"birthDate\": \"1979-04-29\"\n"
            + "}";

    String practitioner2 = "{\n"
            + "  \"resourceType\": \"Practitioner\",\n"
            + "  \"id\": \"f202\",\n"
            + "  \"text\": {\n"
            + "    \"status\": \"generated\",\n"
            + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f202</p><p><b>identifier</b>: UZI-nummer = 12345678902 (OFFICIAL), BIG-nummer = 12345678902 (OFFICIAL)</p><p><b>active</b>: true</p><p><b>name</b>: Luigi Maas(OFFICIAL)</p><p><b>telecom</b>: ph: +31715269111(WORK)</p><p><b>address</b>: Walvisbaai 3 C4 - Automatisering Den helder 2333ZA NLD (WORK)</p><p><b>gender</b>: male</p><p><b>birthDate</b>: 12/06/1960</p></div>\"\n"
            + "  },\n"
            + "  \"identifier\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"type\": {\n"
            + "        \"text\": \"UZI-nummer\"\n"
            + "      },\n"
            + "      \"system\": \"urn:oid:2.16.528.1.1007.3.1\",\n"
            + "      \"value\": \"12345678902\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"type\": {\n"
            + "        \"text\": \"BIG-nummer\"\n"
            + "      },\n"
            + "      \"system\": \"https://www.bigregister.nl/\",\n"
            + "      \"value\": \"12345678902\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"active\": true,\n"
            + "  \"name\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"text\": \"Luigi Maas\",\n"
            + "      \"family\": \"Maas\",\n"
            + "      \"given\": [\n"
            + "        \"Luigi\"\n"
            + "      ],\n"
            + "      \"prefix\": [\n"
            + "        \"Dr.\"\n"
            + "      ]\n"
            + "    }\n"
            + "  ],\n"
            + "  \"telecom\": [\n"
            + "    {\n"
            + "      \"system\": \"phone\",\n"
            + "      \"value\": \"+31715269111\",\n"
            + "      \"use\": \"work\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"address\": [\n"
            + "    {\n"
            + "      \"use\": \"work\",\n"
            + "      \"line\": [\n"
            + "        \"Walvisbaai 3\",\n"
            + "        \"C4 - Automatisering\"\n"
            + "      ],\n"
            + "      \"city\": \"Den helder\",\n"
            + "      \"postalCode\": \"2333ZA\",\n"
            + "      \"country\": \"NLD\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"gender\": \"male\",\n"
            + "  \"birthDate\": \"1960-06-12\"\n"
            + "}";

    String practitioner3 = "{\n"
            + "  \"resourceType\": \"Practitioner\",\n"
            + "  \"id\": \"f203\",\n"
            + "  \"text\": {\n"
            + "    \"status\": \"generated\",\n"
            + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f203</p><p><b>identifier</b>: UZI-nummer = 12345678903 (OFFICIAL), BIG-nummer = 12345678903 (OFFICIAL)</p><p><b>active</b>: true</p><p><b>name</b>: Juri van Gelder(OFFICIAL)</p><p><b>telecom</b>: ph: +31715269111(WORK)</p><p><b>address</b>: Walvisbaai 3 Den helder 2333ZA NLD (WORK)</p><p><b>gender</b>: male</p><p><b>birthDate</b>: 20/04/1983</p></div>\"\n"
            + "  },\n"
            + "  \"identifier\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"type\": {\n"
            + "        \"text\": \"UZI-nummer\"\n"
            + "      },\n"
            + "      \"system\": \"urn:oid:2.16.528.1.1007.3.1\",\n"
            + "      \"value\": \"12345678903\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"type\": {\n"
            + "        \"text\": \"BIG-nummer\"\n"
            + "      },\n"
            + "      \"system\": \"https://www.bigregister.nl/\",\n"
            + "      \"value\": \"12345678903\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"active\": true,\n"
            + "  \"name\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"text\": \"Juri van Gelder\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"telecom\": [\n"
            + "    {\n"
            + "      \"system\": \"phone\",\n"
            + "      \"value\": \"+31715269111\",\n"
            + "      \"use\": \"work\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"address\": [\n"
            + "    {\n"
            + "      \"use\": \"work\",\n"
            + "      \"line\": [\n"
            + "        \"Walvisbaai 3\"\n"
            + "      ],\n"
            + "      \"city\": \"Den helder\",\n"
            + "      \"postalCode\": \"2333ZA\",\n"
            + "      \"country\": \"NLD\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"gender\": \"male\",\n"
            + "  \"birthDate\": \"1983-04-20\"\n"
            + "}";

    String practitioner4 = "{\n"
            + "  \"resourceType\": \"Practitioner\",\n"
            + "  \"id\": \"f201\",\n"
            + "  \"text\": {\n"
            + "    \"status\": \"generated\",\n"
            + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f201</p><p><b>identifier</b>: UZI-nummer = 12345678901 (OFFICIAL)</p><p><b>active</b>: true</p><p><b>name</b>: Dokter Bronsig(OFFICIAL)</p><p><b>telecom</b>: ph: +31715269111(WORK)</p><p><b>address</b>: Walvisbaai 3 C4 - Automatisering Den helder 2333ZA NLD (WORK)</p><p><b>gender</b>: male</p><p><b>birthDate</b>: 24/12/1956</p><h3>Qualifications</h3><table><tr><td>-</td><td><b>Code</b></td></tr><tr><td>*</td><td>Pulmonologist <span>(Details : {SNOMED CT code '41672002' = 'Respiratory disease specialist', given as 'Pulmonologist'})</span></td></tr></table></div>\"\n"
            + "  },\n"
            + "  \"identifier\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"type\": {\n"
            + "        \"text\": \"UZI-nummer\"\n"
            + "      },\n"
            + "      \"system\": \"urn:oid:2.16.528.1.1007.3.1\",\n"
            + "      \"value\": \"12345678901\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"active\": true,\n"
            + "  \"name\": [\n"
            + "    {\n"
            + "      \"use\": \"official\",\n"
            + "      \"text\": \"Dokter Bronsig\",\n"
            + "      \"family\": \"Bronsig\",\n"
            + "      \"given\": [\n"
            + "        \"Arend\"\n"
            + "      ],\n"
            + "      \"prefix\": [\n"
            + "        \"Dr.\"\n"
            + "      ]\n"
            + "    }\n"
            + "  ],\n"
            + "  \"telecom\": [\n"
            + "    {\n"
            + "      \"system\": \"phone\",\n"
            + "      \"value\": \"+31715269111\",\n"
            + "      \"use\": \"work\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"address\": [\n"
            + "    {\n"
            + "      \"use\": \"work\",\n"
            + "      \"line\": [\n"
            + "        \"Walvisbaai 3\",\n"
            + "        \"C4 - Automatisering\"\n"
            + "      ],\n"
            + "      \"city\": \"Den helder\",\n"
            + "      \"postalCode\": \"2333ZA\",\n"
            + "      \"country\": \"NLD\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"gender\": \"male\",\n"
            + "  \"birthDate\": \"1956-12-24\",\n"
            + "  \"qualification\": [\n"
            + "    {\n"
            + "      \"code\": {\n"
            + "        \"coding\": [\n"
            + "          {\n"
            + "            \"system\": \"http://snomed.info/sct\",\n"
            + "            \"code\": \"41672002\",\n"
            + "            \"display\": \"Pulmonologist\"\n"
            + "          }\n"
            + "        ]\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}";
}

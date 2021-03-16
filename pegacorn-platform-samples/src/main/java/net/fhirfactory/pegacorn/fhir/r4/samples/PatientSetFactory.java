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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;

/**
 *
 * @author Mark A. Hunter
 */
@ApplicationScoped
public class PatientSetFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PatientSetFactory.class);

    @Inject
    FHIRContextUtility fhirContextUtility;

    public Set<Patient> getPateintSet(){
        LOG.debug(".getPateintSet(): Entry");
        HashSet<Patient> patientSet = new HashSet<Patient>();
        LOG.trace(".getPateintSet(): Initialising Parser(s)");
        IParser parserR4 = fhirContextUtility.getJsonParser();
        LOG.trace(".getPatientSet(): Parser(s) initialised, now iterating through Patient Strings and creating Patient Resources");
        for(String currentPatientString: getPatientJSONStringSet()){
            Patient newPatient = (Patient)parserR4.parseResource(currentPatientString);
            patientSet.add(newPatient);
        }
        LOG.debug(".getPatientSet(): All done, returning patientSet (size) --> {}", patientSet.size() );
        return(patientSet);
    }
    
    public Set<String> getPatientJSONStringSet(){
        HashSet<String> patientSet = new HashSet<String>();
        patientSet.add(patient0);
        patientSet.add(patient1);
        patientSet.add(patient2);
        patientSet.add(patient3);
        patientSet.add(patient4);
        return(patientSet);
    }

    // From https://www.hl7.org/fhir/patient-example-f001-pieter.json.html
    // Real-world patient example (anonymized)
    private String patient0 = "{\n" +
            "  \"resourceType\": \"Patient\",\n" +
            "  \"id\": \"f001\",\n" +
            "  \"text\": {\n" +
            "    \"status\": \"generated\",\n" +
            "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f001</p><p><b>identifier</b>: 738472983 (USUAL), ?? (USUAL)</p><p><b>active</b>: true</p><p><b>name</b>: Pieter van de Heuvel </p><p><b>telecom</b>: ph: 0648352638(MOBILE), p.heuvel@gmail.com(HOME)</p><p><b>gender</b>: male</p><p><b>birthDate</b>: 17/11/1944</p><p><b>deceased</b>: false</p><p><b>address</b>: Van Egmondkade 23 Amsterdam 1024 RJ NLD (HOME)</p><p><b>maritalStatus</b>: Getrouwd <span>(Details : {http://terminology.hl7.org/CodeSystem/v3-MaritalStatus code 'M' = 'Married', given as 'Married'})</span></p><p><b>multipleBirth</b>: true</p><h3>Contacts</h3><table><tr><td>-</td><td><b>Relationship</b></td><td><b>Name</b></td><td><b>Telecom</b></td></tr><tr><td>*</td><td>Emergency Contact <span>(Details : {http://terminology.hl7.org/CodeSystem/v2-0131 code 'C' = 'Emergency Contact)</span></td><td>Sarah Abels </td><td>ph: 0690383372(MOBILE)</td></tr></table><h3>Communications</h3><table><tr><td>-</td><td><b>Language</b></td><td><b>Preferred</b></td></tr><tr><td>*</td><td>Nederlands <span>(Details : {urn:ietf:bcp:47 code 'nl' = 'Dutch', given as 'Dutch'})</span></td><td>true</td></tr></table><p><b>managingOrganization</b>: <a>Burgers University Medical Centre</a></p></div>\"\n" +
            "  },\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\",\n" +
            "      \"value\": \"738472983\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"active\": true,\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"family\": \"van de Heuvel\",\n" +
            "      \"given\": [\n" +
            "        \"Pieter\"\n" +
            "      ],\n" +
            "      \"suffix\": [\n" +
            "        \"MSc\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"telecom\": [\n" +
            "    {\n" +
            "      \"system\": \"phone\",\n" +
            "      \"value\": \"0648352638\",\n" +
            "      \"use\": \"mobile\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"system\": \"email\",\n" +
            "      \"value\": \"p.heuvel@gmail.com\",\n" +
            "      \"use\": \"home\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"male\",\n" +
            "  \"birthDate\": \"1944-11-17\",\n" +
            "  \"deceasedBoolean\": false,\n" +
            "  \"address\": [\n" +
            "    {\n" +
            "      \"use\": \"home\",\n" +
            "      \"line\": [\n" +
            "        \"Van Egmondkade 23\"\n" +
            "      ],\n" +
            "      \"city\": \"Amsterdam\",\n" +
            "      \"postalCode\": \"1024 RJ\",\n" +
            "      \"country\": \"NLD\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"maritalStatus\": {\n" +
            "    \"coding\": [\n" +
            "      {\n" +
            "        \"system\": \"http://terminology.hl7.org/CodeSystem/v3-MaritalStatus\",\n" +
            "        \"code\": \"M\",\n" +
            "        \"display\": \"Married\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"text\": \"Getrouwd\"\n" +
            "  },\n" +
            "  \"multipleBirthBoolean\": true,\n" +
            "  \"contact\": [\n" +
            "    {\n" +
            "      \"relationship\": [\n" +
            "        {\n" +
            "          \"coding\": [\n" +
            "            {\n" +
            "              \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0131\",\n" +
            "              \"code\": \"C\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"name\": {\n" +
            "        \"use\": \"usual\",\n" +
            "        \"family\": \"Abels\",\n" +
            "        \"given\": [\n" +
            "          \"Sarah\"\n" +
            "        ]\n" +
            "      },\n" +
            "      \"telecom\": [\n" +
            "        {\n" +
            "          \"system\": \"phone\",\n" +
            "          \"value\": \"0690383372\",\n" +
            "          \"use\": \"mobile\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"communication\": [\n" +
            "    {\n" +
            "      \"language\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"urn:ietf:bcp:47\",\n" +
            "            \"code\": \"nl\",\n" +
            "            \"display\": \"Dutch\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"text\": \"Nederlands\"\n" +
            "      },\n" +
            "      \"preferred\": true\n" +
            "    }\n" +
            "  ],\n" +
            "  \"managingOrganization\": {\n" +
            "    \"reference\": \"Organization/f001\",\n" +
            "    \"display\": \"Burgers University Medical Centre\"\n" +
            "  }\n" +
            "}";


    // From https://www.hl7.org/fhir/patient-example-f201-roel.json.html
    // Real-world patient example (anonymized)
    private String patient1 = "{\n" +
            "  \"resourceType\": \"Patient\",\n" +
            "  \"id\": \"f201\",\n" +
            "  \"text\": {\n" +
            "    \"status\": \"generated\",\n" +
            "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f201</p><p><b>identifier</b>: BSN = 123456789 (OFFICIAL), BSN = 123456789 (OFFICIAL)</p><p><b>active</b>: true</p><p><b>name</b>: Roel(OFFICIAL)</p><p><b>telecom</b>: ph: +31612345678(MOBILE), ph: +31201234567(HOME)</p><p><b>gender</b>: male</p><p><b>birthDate</b>: 13/03/1960</p><p><b>deceased</b>: false</p><p><b>address</b>: Bos en Lommerplein 280 Amsterdam 1055RW NLD (HOME)</p><p><b>maritalStatus</b>: Legally married <span>(Details : {SNOMED CT code '36629006' = 'Legal marriage', given as 'Legally married'}; {http://terminology.hl7.org/CodeSystem/v3-MaritalStatus code 'M' = 'Married)</span></p><p><b>multipleBirth</b>: false</p><p><b>photo</b>: </p><h3>Contacts</h3><table><tr><td>-</td><td><b>Relationship</b></td><td><b>Name</b></td><td><b>Telecom</b></td></tr><tr><td>*</td><td>Wife <span>(Details : {SNOMED CT code '127850001' = 'Wife', given as 'Wife'}; {http://terminology.hl7.org/CodeSystem/v2-0131 code 'N' = 'Next-of-Kin; {http://terminology.hl7.org/CodeSystem/v3-RoleCode code 'WIFE' = 'wife)</span></td><td>Ariadne Bor-Jansma</td><td>ph: +31201234567(HOME)</td></tr></table><h3>Communications</h3><table><tr><td>-</td><td><b>Language</b></td><td><b>Preferred</b></td></tr><tr><td>*</td><td>Dutch <span>(Details : {urn:ietf:bcp:47 code 'nl-NL' = 'Dutch (Region=Netherlands)', given as 'Dutch'})</span></td><td>true</td></tr></table><p><b>managingOrganization</b>: <a>AUMC</a></p></div>\"\n" +
            "  },\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"type\": {\n" +
            "        \"text\": \"BSN\"\n" +
            "      },\n" +
            "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\",\n" +
            "      \"value\": \"123456789\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"type\": {\n" +
            "        \"text\": \"BSN\"\n" +
            "      },\n" +
            "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\",\n" +
            "      \"value\": \"123456789\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"active\": true,\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"text\": \"Roel\",\n" +
            "      \"family\": \"Bor\",\n" +
            "      \"given\": [\n" +
            "        \"Roelof Olaf\"\n" +
            "      ],\n" +
            "      \"prefix\": [\n" +
            "        \"Drs.\"\n" +
            "      ],\n" +
            "      \"suffix\": [\n" +
            "        \"PDEng.\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"telecom\": [\n" +
            "    {\n" +
            "      \"system\": \"phone\",\n" +
            "      \"value\": \"+31612345678\",\n" +
            "      \"use\": \"mobile\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"system\": \"phone\",\n" +
            "      \"value\": \"+31201234567\",\n" +
            "      \"use\": \"home\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"male\",\n" +
            "  \"birthDate\": \"1960-03-13\",\n" +
            "  \"deceasedBoolean\": false,\n" +
            "  \"address\": [\n" +
            "    {\n" +
            "      \"use\": \"home\",\n" +
            "      \"line\": [\n" +
            "        \"Bos en Lommerplein 280\"\n" +
            "      ],\n" +
            "      \"city\": \"Amsterdam\",\n" +
            "      \"postalCode\": \"1055RW\",\n" +
            "      \"country\": \"NLD\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"maritalStatus\": {\n" +
            "    \"coding\": [\n" +
            "      {\n" +
            "        \"system\": \"http://snomed.info/sct\",\n" +
            "        \"code\": \"36629006\",\n" +
            "        \"display\": \"Legally married\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"system\": \"http://terminology.hl7.org/CodeSystem/v3-MaritalStatus\",\n" +
            "        \"code\": \"M\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"multipleBirthBoolean\": false,\n" +
            "  \"photo\": [\n" +
            "    {\n" +
            "      \"contentType\": \"image/jpeg\",\n" +
            "      \"url\": \"Binary/f006\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"contact\": [\n" +
            "    {\n" +
            "      \"relationship\": [\n" +
            "        {\n" +
            "          \"coding\": [\n" +
            "            {\n" +
            "              \"system\": \"http://snomed.info/sct\",\n" +
            "              \"code\": \"127850001\",\n" +
            "              \"display\": \"Wife\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0131\",\n" +
            "              \"code\": \"N\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"system\": \"http://terminology.hl7.org/CodeSystem/v3-RoleCode\",\n" +
            "              \"code\": \"WIFE\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"name\": {\n" +
            "        \"use\": \"usual\",\n" +
            "        \"text\": \"Ariadne Bor-Jansma\"\n" +
            "      },\n" +
            "      \"telecom\": [\n" +
            "        {\n" +
            "          \"system\": \"phone\",\n" +
            "          \"value\": \"+31201234567\",\n" +
            "          \"use\": \"home\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"communication\": [\n" +
            "    {\n" +
            "      \"language\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"urn:ietf:bcp:47\",\n" +
            "            \"code\": \"nl-NL\",\n" +
            "            \"display\": \"Dutch\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"preferred\": true\n" +
            "    }\n" +
            "  ],\n" +
            "  \"managingOrganization\": {\n" +
            "    \"reference\": \"Organization/f201\",\n" +
            "    \"display\": \"AUMC\"\n" +
            "  }\n" +
            "}";

    // From https://www.hl7.org/fhir/patient-glossy-example.json.html
    // Example for glossy (<--- no idea what this means)
    private String patient2 = "{\n" +
            "  \"resourceType\": \"Patient\",\n" +
            "  \"id\": \"glossy\",\n" +
            "  \"meta\": {\n" +
            "    \"lastUpdated\": \"2014-11-13T11:41:00+11:00\"\n" +
            "  },\n" +
            "  \"text\": {\n" +
            "    \"status\": \"generated\",\n" +
            "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n      <p>Henry Levin the 7th</p>\\n      <p>MRN: 123456. Male, 24-Sept 1932</p>\\n    </div>\"\n" +
            "  },\n" +
            "  \"extension\": [\n" +
            "    {\n" +
            "      \"url\": \"http://example.org/StructureDefinition/trials\",\n" +
            "      \"valueCode\": \"renal\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"type\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n" +
            "            \"code\": \"MR\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"system\": \"http://www.goodhealth.org/identifiers/mrn\",\n" +
            "      \"value\": \"123456\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"active\": true,\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"family\": \"Levin\",\n" +
            "      \"given\": [\n" +
            "        \"Henry\"\n" +
            "      ],\n" +
            "      \"suffix\": [\n" +
            "        \"The 7th\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"male\",\n" +
            "  \"birthDate\": \"1932-09-24\",\n" +
            "  \"generalPractitioner\": [\n" +
            "    {\n" +
            "      \"reference\": \"Practitioner/example\",\n" +
            "      \"display\": \"Dr Adam Careful\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"managingOrganization\": {\n" +
            "    \"reference\": \"Organization/2\",\n" +
            "    \"display\": \"Good Health Clinic\"\n" +
            "  }\n" +
            "}";

    // From https://www.hl7.org/fhir/patient-example-infant-mom.json.html
    // Mother of infant twins and fetal patient
    private String patient3 = "{\n" +
            "  \"resourceType\": \"Patient\",\n" +
            "  \"id\": \"infant-mom\",\n" +
            "  \"text\": {\n" +
            "    \"status\": \"generated\",\n" +
            "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: infant-mom</p><p><b>name</b>: Leia Solo (OFFICIAL), Leia Organa (MAIDEN)</p><p><b>gender</b>: female</p><p><b>birthDate</b>: 12/10/1995</p><p><b>maritalStatus</b>: Married <span>(Details : {http://terminology.hl7.org/CodeSystem/v3-MaritalStatus code 'M' = 'Married', given as 'Married'})</span></p><p><b>generalPractitioner</b>: <a>Too-Onebee</a></p></div>\"\n" +
            "  },\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"family\": \"Solo\",\n" +
            "      \"given\": [\n" +
            "        \"Leia\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"use\": \"maiden\",\n" +
            "      \"family\": \"Organa\",\n" +
            "      \"given\": [\n" +
            "        \"Leia\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"female\",\n" +
            "  \"birthDate\": \"1995-10-12\",\n" +
            "  \"maritalStatus\": {\n" +
            "    \"coding\": [\n" +
            "      {\n" +
            "        \"system\": \"http://terminology.hl7.org/CodeSystem/v3-MaritalStatus\",\n" +
            "        \"code\": \"M\",\n" +
            "        \"display\": \"Married\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"generalPractitioner\": [\n" +
            "    {\n" +
            "      \"reference\": \"Practitioner/21B\",\n" +
            "      \"display\": \"Too-Onebee\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    // From https://www.hl7.org/fhir/patient-example-infant-twin-1.json.html
    // Newborn Eldest Twin Example
    private String patient4 = "{\n" +
            "  \"resourceType\": \"Patient\",\n" +
            "  \"id\": \"infant-twin-1\",\n" +
            "  \"text\": {\n" +
            "    \"status\": \"generated\",\n" +
            "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: infant-twin-1</p><p><b>identifier</b>: Medical record number = MRN7465737865, 7465737865</p><p><b>name</b>: Jaina Solo (OFFICIAL)</p><p><b>gender</b>: female</p><p><b>birthDate</b>: 15/05/2017</p><p><b>multipleBirth</b>: 1</p><h3>Contacts</h3><table><tr><td>-</td><td><b>Relationship</b></td><td><b>Name</b></td><td><b>Telecom</b></td></tr><tr><td>*</td><td>Mother <span>(Details : {SNOMED CT code '72705000' = 'Mother', given as 'Mother'}; {http://terminology.hl7.org/CodeSystem/v2-0131 code 'N' = 'Next-of-Kin; {http://terminology.hl7.org/CodeSystem/v3-RoleCode code 'MTH' = 'mother)</span></td><td>Leia Organa (MAIDEN)</td><td>ph: +31201234567(MOBILE)</td></tr></table></div>\"\n" +
            "  },\n" +
            "  \"extension\": [\n" +
            "    {\n" +
            "      \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName\",\n" +
            "      \"valueString\": \"Organa\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"type\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n" +
            "            \"code\": \"MR\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"system\": \"http://coruscanthealth.org/main-hospital/patient-identifier\",\n" +
            "      \"value\": \"MRN7465737865\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"system\": \"http://new-republic.gov/galactic-citizen-identifier\",\n" +
            "      \"value\": \"7465737865\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"family\": \"Solo\",\n" +
            "      \"given\": [\n" +
            "        \"Jaina\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"female\",\n" +
            "  \"birthDate\": \"2017-05-15\",\n" +
            "  \"_birthDate\": {\n" +
            "    \"extension\": [\n" +
            "      {\n" +
            "        \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-birthTime\",\n" +
            "        \"valueDateTime\": \"2017-05-15T17:11:00+01:00\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"multipleBirthInteger\": 1,\n" +
            "  \"contact\": [\n" +
            "    {\n" +
            "      \"relationship\": [\n" +
            "        {\n" +
            "          \"coding\": [\n" +
            "            {\n" +
            "              \"system\": \"http://snomed.info/sct\",\n" +
            "              \"code\": \"72705000\",\n" +
            "              \"display\": \"Mother\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0131\",\n" +
            "              \"code\": \"N\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"system\": \"http://terminology.hl7.org/CodeSystem/v3-RoleCode\",\n" +
            "              \"code\": \"MTH\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"name\": {\n" +
            "        \"use\": \"maiden\",\n" +
            "        \"family\": \"Organa\",\n" +
            "        \"given\": [\n" +
            "          \"Leia\"\n" +
            "        ]\n" +
            "      },\n" +
            "      \"telecom\": [\n" +
            "        {\n" +
            "          \"system\": \"phone\",\n" +
            "          \"value\": \"+31201234567\",\n" +
            "          \"use\": \"mobile\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}

/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.factories;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.reason.valuesets.TaskReasonTypeEnum;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class TaskIdTypeFactory {

    public PetasosEpisodeIdentifier newEpisodeIdentifier(TopologyNodeFunctionFDN targetWUPFDN, DataParcelTypeDescriptor contentDescriptor){
        if(targetWUPFDN == null || contentDescriptor == null){
            return(null);
        }
        TopologyNodeRDN processingPlantRDN = targetWUPFDN.extractRDNForNodeType(ComponentTypeTypeEnum.PROCESSING_PLANT);
        TopologyNodeRDN workshopRDN = targetWUPFDN.extractRDNForNodeType(ComponentTypeTypeEnum.WORKSHOP);
        TopologyNodeRDN wupRDN = targetWUPFDN.extractRDNForNodeType(ComponentTypeTypeEnum.WUP);
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(processingPlantRDN.getNodeName());
        idBuilder.append(".");
        idBuilder.append(workshopRDN.getNodeName());
        idBuilder.append(".");
        idBuilder.append(wupRDN.getNodeName());
        idBuilder.append("(");
        if(contentDescriptor.hasDataParcelDefiner()){
            String definer = contentDescriptor.getDataParcelDefiner();
            String definerValue = definer.replaceAll(" ", "");
            idBuilder.append(definerValue);
        }
        if(contentDescriptor.hasDataParcelCategory()){
            String category = contentDescriptor.getDataParcelCategory();
            idBuilder.append("."+category);
        }
        if(contentDescriptor.hasDataParcelSubCategory()){
            String subCategory = contentDescriptor.getDataParcelSubCategory();
            idBuilder.append("."+subCategory);
        }
        if(contentDescriptor.hasDataParcelResource()){
            String resource = contentDescriptor.getDataParcelResource();
            idBuilder.append("."+resource);
        }
        if(contentDescriptor.hasDataParcelSegment()){
            String segment = contentDescriptor.getDataParcelSegment();
            idBuilder.append("."+segment);
        }
        if(contentDescriptor.hasDataParcelAttribute()){
            String attribute = contentDescriptor.getDataParcelAttribute();
            idBuilder.append("."+attribute);
        }
        if(contentDescriptor.hasDataParcelDiscriminatorType()){
            String descType = contentDescriptor.getDataParcelDiscriminatorType();
            idBuilder.append("."+descType);
        }
        if(contentDescriptor.hasDataParcelDiscriminatorValue()){
            String descValue = contentDescriptor.getDataParcelDiscriminatorValue();
            idBuilder.append("."+descValue);
        }
        idBuilder.append(")");
        long leastSignificantBits = UUID.randomUUID().getLeastSignificantBits();
        String hexString = Long.toHexString(leastSignificantBits);
        idBuilder.append("::");
        idBuilder.append(hexString);

        PetasosEpisodeIdentifier id = new PetasosEpisodeIdentifier();
        id.setValue(idBuilder.toString());
        id.setCreationInstant(Instant.now());
        return(id);
    }

    public TaskIdType newTaskId(TaskReasonTypeEnum taskReason, DataParcelTypeDescriptor contentDescriptor){
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(taskReason.getTaskReasonDisplayName());
        idBuilder.append("(");
        if(contentDescriptor.hasDataParcelDefiner()){
            String definer = contentDescriptor.getDataParcelDefiner();
            String definerValue = definer.replaceAll(" ", "");
            idBuilder.append(definerValue);
        }
        if(contentDescriptor.hasDataParcelCategory()){
            String category = contentDescriptor.getDataParcelCategory();
            idBuilder.append("."+category);
        }
        if(contentDescriptor.hasDataParcelSubCategory()){
            String subCategory = contentDescriptor.getDataParcelSubCategory();
            idBuilder.append("."+subCategory);
        }
        if(contentDescriptor.hasDataParcelResource()){
            String resource = contentDescriptor.getDataParcelResource();
            idBuilder.append("."+resource);
        }
        if(contentDescriptor.hasDataParcelSegment()){
            String segment = contentDescriptor.getDataParcelSegment();
            idBuilder.append("."+segment);
        }
        if(contentDescriptor.hasDataParcelAttribute()){
            String attribute = contentDescriptor.getDataParcelAttribute();
            idBuilder.append("."+attribute);
        }
        if(contentDescriptor.hasDataParcelDiscriminatorType()){
            String descType = contentDescriptor.getDataParcelDiscriminatorType();
            idBuilder.append("."+descType);
        }
        if(contentDescriptor.hasDataParcelDiscriminatorValue()){
            String descValue = contentDescriptor.getDataParcelDiscriminatorValue();
            idBuilder.append("."+descValue);
        }
        idBuilder.append(")");
        long leastSignificantBits = UUID.randomUUID().getLeastSignificantBits();
        String hexString = Long.toHexString(leastSignificantBits);
        idBuilder.append("::");
        idBuilder.append(hexString);
        TaskIdType id = new TaskIdType();
        id.setId(idBuilder.toString());
        id.setCreationInstant(Instant.now());
        return(id);
    }

    public TaskIdType newTaskId(){
        TaskIdType taskId = newTaskId("1.0");
        return(taskId);
    }

    public TaskIdType newTaskId(String version){
        TaskIdType taskId = new TaskIdType();
        taskId.setCreationInstant(Instant.now());
        UUID uuid = UUID.randomUUID();
        String id = Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits());
        taskId.setId(id);
        taskId.setVersion(version);
        return(taskId);
    }
}

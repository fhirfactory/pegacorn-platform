package net.fhirfactory.pegacorn.petasos.core;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.petasos.model.resilience.episode.PetasosEpisodeIdentifier;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class PetasosActionableTaskIdentifierFactory {

    public PetasosEpisodeIdentifier newEpisodeIdentifier(TopologyNodeFunctionFDN targetWUPFDN, DataParcelTypeDescriptor contentDescriptor){
        if(targetWUPFDN == null || contentDescriptor == null){
            return(null);
        }
        TopologyNodeRDN processingPlantRDN = targetWUPFDN.extractRDNForNodeType(TopologyNodeTypeEnum.PROCESSING_PLANT);
        TopologyNodeRDN workshopRDN = targetWUPFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WORKSHOP);
        TopologyNodeRDN wupRDN = targetWUPFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WUP);
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
}

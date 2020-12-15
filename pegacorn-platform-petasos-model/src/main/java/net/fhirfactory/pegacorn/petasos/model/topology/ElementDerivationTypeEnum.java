/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.fhirfactory.pegacorn.petasos.model.topology;

/**
 *
 * @author MAHun
 */
public enum ElementDerivationTypeEnum {
    FROM_CONTAINED_ELEMENTS("pegacorn.petasos.toplogy.valuederivation.contained_elements"),
    FROM_CONTAINING_ELEMENTS("pegacorn.petasos.toplogy.valuederivation.continaing_elements");

    private String derivationType;

    private ElementDerivationTypeEnum(String mapElementStatus) {
        this.derivationType = mapElementStatus;
    }

    public String getDerivationType() {
        return (this.derivationType);
    }
}

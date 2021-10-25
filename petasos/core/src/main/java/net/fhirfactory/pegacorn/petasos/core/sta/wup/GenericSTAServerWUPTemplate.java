package net.fhirfactory.pegacorn.petasos.core.sta.wup;

import net.fhirfactory.pegacorn.petasos.core.sta.wup.common.GenericSTAWUPTemplate;
import net.fhirfactory.pegacorn.petasos.model.wup.valuesets.WUPArchetypeEnum;

public abstract class GenericSTAServerWUPTemplate extends GenericSTAWUPTemplate {

    public GenericSTAServerWUPTemplate() {
        super();
    }

    @Override
    protected WUPArchetypeEnum specifyWUPArchetype(){
        return(WUPArchetypeEnum.WUP_NATURE_API_ANSWER);
    }
}

package net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.sta;

import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;

public class STATransaction {
    private WUPJobCard jobCard;
    private UoW unitOfWork;
    private ParcelStatusElement statusElement;

    public STATransaction(){
        this.jobCard = null;
        this.unitOfWork = null;
        this.statusElement = null;
    }

    public ParcelStatusElement getStatusElement() {
        return statusElement;
    }

    public void setStatusElement(ParcelStatusElement statusElement) {
        this.statusElement = statusElement;
    }

    public WUPJobCard getJobCard() {
        return jobCard;
    }

    public void setJobCard(WUPJobCard jobCard) {
        this.jobCard = jobCard;
    }

    public UoW getUnitOfWork() {
        return unitOfWork;
    }

    public void setUnitOfWork(UoW unitOfWork) {
        this.unitOfWork = unitOfWork;
    }
}

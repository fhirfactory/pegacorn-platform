package net.fhirfactory.pegacorn.petasos.core.sta.wup;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.deployment.topology.manager.DeploymentTopologyIM;
import net.fhirfactory.pegacorn.petasos.audit.model.PetasosParcelAuditTrailEntry;
import net.fhirfactory.pegacorn.petasos.core.sta.brokers.PetasosSTAServicesAuditOnlyBroker;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElement;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementIdentifier;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class GenericSTAClientWUPTemplate {
    private TopologyNodeFunctionToken apiClientNodeFunction;
    private WUPIdentifier apiClientWUP;
    private String apiClientName;
    private WUPJobCard apiClientJobCard;
    private NodeElement apiClientNode;
    private String apiClientVersion;
    private boolean isInitialised;

    private IParser parserR4;

    @Inject
    PetasosSTAServicesAuditOnlyBroker servicesBroker;

    @Inject
    private DeploymentTopologyIM deploymentTopologyIM;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    public GenericSTAClientWUPTemplate() {
        isInitialised = false;
        this.apiClientName = specifySTAClientName();
        this.apiClientVersion = specifySTAClientVersion();
    }

    abstract protected String specifySTAClientName();
    abstract protected String specifySTAClientVersion();
    abstract protected String specifySTAWorkshopName();
    abstract protected String specifySTAClientType();
    abstract protected Logger getLogger();
    abstract protected ProcessingPlantServicesInterface specifyProcessingPlant();

    @PostConstruct
    protected void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (!isInitialised) {
            getLogger().trace(".initialise(): AccessBase is NOT initialised");
            this.parserR4 = fhirContextUtility.getJsonParser();
            this.isInitialised = true;
            getProcessingPlant().initialisePlant();
            this.apiClientNode = buildSTAClientNode();
            this.apiClientNodeFunction = this.apiClientNode.getNodeFunctionToken();
            this.apiClientWUP = new WUPIdentifier(this.apiClientNode.getNodeInstanceID());
        }
    }

    protected ProcessingPlantServicesInterface getProcessingPlant(){return(specifyProcessingPlant());}
    protected String getWorkshopName(){return(specifySTAWorkshopName());}
    protected String getSTAClientType(){return(specifySTAClientType());}

    /**
     * This function builds the Deployment Topology node (a WUP) for the
     * Synchronous Transaction Activity Client Service.
     * <p>
     * It uses the Name (specifyPersistenceServiceName()) defined in the subclass as part
     * of the Identifier and then registers with the Topology Services.
     *
     * @return The NodeElement representing the WUP which this code-set is
     * fulfilling.
     */
    private NodeElement buildSTAClientNode() {
        getLogger().debug(".buildSTAClientNode(): Entry");
        NodeElementIdentifier processingPlantIdentifier = getProcessingPlant().getProcessingPlantNodeId();
        getLogger().trace(".buildSTAClientNode(): retrieved ProcessingPlant Identifier --> {}", processingPlantIdentifier);
        if (processingPlantIdentifier == null) {
            getLogger().error(".buildSTAClientNode(): ProcessingPlant is not Initialised!");
        }
        FDN workshopFDN = new FDN(processingPlantIdentifier);
        workshopFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), getWorkshopName()));
        NodeElementIdentifier staClientWorkshop = new NodeElementIdentifier(workshopFDN.getToken());
        getLogger().trace(".buildSTAClientNode(): Retrieving VirtualDB Node");
        NodeElement staClientWorkshopNode = getDeploymentTopologyIM().getNode(staClientWorkshop);
        getLogger().trace(".buildSTAClientNode(): virtualdb node (NodeElement) --> {}", staClientWorkshopNode);
        FDN staClientTypeFDN = new FDN(workshopFDN);
        staClientTypeFDN.appendRDN(new RDN(NodeElementTypeEnum.WUP.getNodeElementType(), getSTAClientType() + "-" + getApiClientName()));
        NodeElementIdentifier staClientIdentifier = new NodeElementIdentifier(staClientTypeFDN.getToken());
        getLogger().trace(".buildSTAClientNode(): Now construct the Work Unit Processing Node");
        NodeElement staClientNode = new NodeElement();
        getLogger().trace(".buildSTAClientNode(): Constructing WUP Node, Setting Version Number");
        staClientNode.setVersion(getApiClientVersion());
        getLogger().trace(".buildSTAClientNode(): Constructing WUP Node, Setting Node Instance");
        staClientNode.setNodeInstanceID(staClientWorkshop);
        getLogger().trace(".buildSTAClientNode(): Constructing WUP Node, Setting Concurrency Mode");
        staClientNode.setConcurrencyMode(staClientWorkshopNode.getConcurrencyMode());
        getLogger().trace(".buildSTAClientNode(): Constructing WUP Node, Setting Resillience Mode");
        staClientNode.setResilienceMode(staClientWorkshopNode.getResilienceMode());
        getLogger().trace(".buildSTAClientNode(): Constructing WUP Node, Setting inPlace Status");
        staClientNode.setInstanceInPlace(true);
        getLogger().trace(".buildSTAClientNode(): Constructing WUP Node, Setting Containing Element Identifier");
        staClientNode.setContainingElementID(staClientWorkshopNode.getNodeInstanceID());
        getLogger().trace(".buildSTAClientNode(): Now registering the Node");
        getDeploymentTopologyIM().registerNode(staClientNode);
        getLogger().debug(".buildSTAClientNode(): Exit, buildSTAClientNode (NodeElementIdentifier) --> {}", staClientIdentifier);
        return (staClientNode);
    }

    public TopologyNodeFunctionToken getApiClientNodeFunction() {
        return apiClientNodeFunction;
    }

    public WUPIdentifier getApiClientWUP() {
        return apiClientWUP;
    }

    public String getApiClientName() {
        return apiClientName;
    }

    public WUPJobCard getApiClientJobCard() {
        return apiClientJobCard;
    }

    public NodeElement getApiClientNode() {
        return apiClientNode;
    }

    public String getApiClientVersion() {
        return apiClientVersion;
    }

    public DeploymentTopologyIM getDeploymentTopologyIM() {
        return deploymentTopologyIM;
    }

    protected PetasosParcelAuditTrailEntry beginTransaction(UoW unitOfWork, String action) {
        PetasosParcelAuditTrailEntry parcelEntry = servicesBroker.transactionAuditEntry(getApiClientWUP(), action, unitOfWork,null );
        return parcelEntry;
    }

    protected void endTransaction(UoW unitOfWork, String action, PetasosParcelAuditTrailEntry startingTransaction) {
        PetasosParcelAuditTrailEntry parcelEntry = servicesBroker.transactionAuditEntry(getApiClientWUP(), action, unitOfWork,startingTransaction );
    }
}

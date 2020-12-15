package net.fhirfactory.pegacorn.deployment.topology.map.standalone;

import net.fhirfactory.pegacorn.common.model.FDNToken;
import net.fhirfactory.pegacorn.deployment.topology.manager.DeploymentTopologyIM;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class StandaloneSampleDeploymentSolutionTest {
    private static final Logger LOG = LoggerFactory.getLogger(StandaloneSampleDeploymentSolutionTest.class);

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive testWAR;

        PomEquippedResolveStage pomEquippedResolver = Maven.resolver().loadPomFromFile("pom.xml");
        PomEquippedResolveStage pomEquippedResolverWithRuntimeDependencies = pomEquippedResolver.importRuntimeDependencies();
        MavenStrategyStage mavenResolver = pomEquippedResolverWithRuntimeDependencies.resolve();
        MavenFormatStage mavenFormat = mavenResolver.withTransitivity();
        File[] fileSet = mavenFormat.asFile();
        LOG.debug(".createDeployment(): ShrinkWrap Library Set for run-time equivalent, length --> {}", fileSet.length);
        for(int counter = 0; counter < fileSet.length; counter++ ){
            File currentFile = fileSet[counter];
            LOG.trace(".createDeployment(): Shrinkwrap Entry --> {}", currentFile.getName());
        }
        testWAR = ShrinkWrap.create(WebArchive.class, "pegacorn-deployment-topology.war")
                .addAsLibraries(fileSet)
                .addPackages(true, "net.fhirfactory.pegacorn.deployment.topology.map.standalone")
                .addAsManifestResource("META-INF/beans.xml", "WEB-INF/beans.xml");
        if(LOG.isTraceEnabled()) {
            Map<ArchivePath, Node> content = testWAR.getContent();
            Set<ArchivePath> contentPathSet = content.keySet();
            Iterator<ArchivePath> contentPathSetIterator = contentPathSet.iterator();
            while (contentPathSetIterator.hasNext()) {
                ArchivePath currentPath = contentPathSetIterator.next();
                LOG.trace(".createDeployment(): testWAR Entry Path --> {}", currentPath.get());
            }
        }
        return(testWAR);
    }

    @Inject
    DeploymentTopologyIM myTopologyServer;

    @Inject
    StandaloneSampleDeploymentSolution myMap;

    @Before
    public void initialise(){
        LOG.trace(".initialise(): Entry");
        myMap.initialiseDeploymentTopology();
        LOG.trace(".initialise(): Exit");
    }

    @org.junit.Test
    public void specifyLadonExternalisedServices() {
        LOG.trace(".specifyLadonExternalisedServices(): Entry");
        String solutionID = myMap.getSolutionName();
        LOG.debug(".specifyLadonExternalisedServices(): solutionID --> {}", solutionID);
        assert(solutionID!=null);
    }
}

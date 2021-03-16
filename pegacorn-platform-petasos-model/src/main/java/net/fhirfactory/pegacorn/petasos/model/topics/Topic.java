/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.model.topics;

import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.common.model.generalid.FDNTokenSet;

import java.util.Objects;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;

/**
 * @author Mark A. Hunter
 * @since 2020-07-29
 */
public class Topic {

    private static final Logger LOG = LoggerFactory.getLogger(Topic.class);

    private TopicTypeEnum datasetType;
    private Object datasetTypeLock;
    private FDNToken identifier;
    private Object identifierLock;
    private FDNTokenSet containedDatasets;
    private Object containedDatasetsLock;
    private FDNToken containingDataset;
    private Object containingDatasetLock;
    private String version;
    private Object versionLock;

    public Topic() {
        // 1st, clear the deck
        this.datasetType = null;
        this.datasetTypeLock = new Object();
        this.identifier = null;
        this.identifierLock = new Object();
        this.containingDataset = null;
        this.containingDatasetLock = new Object();
        this.containedDatasets = new FDNTokenSet();
        this.containedDatasetsLock = new Object();
        this.version = "0.0.0";
        this.versionLock = new Object();
        // Now establish the basic structure
        this.containedDatasets = new FDNTokenSet();
    }

    public Topic(FDNToken topicID) {
        // 1st, clear the deck
        this.datasetType = null;
        this.datasetTypeLock = new Object();
        this.identifier = null;
        this.identifierLock = new Object();
        this.containingDataset = null;
        this.containingDatasetLock = new Object();
        this.containedDatasets = new FDNTokenSet();
        this.containedDatasetsLock = new Object();
        this.version = "0.0.0";
        this.versionLock = new Object();
        // Now establish the basic structure
        this.containedDatasets = new FDNTokenSet();
        this.identifier = topicID;

    }

    public boolean hasContainingDataset() {
        if (this.containingDataset == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public FDNToken getContainingDataset() {
        return (this.containingDataset);
    }

    public void setContainingDataset(FDNToken containingTopicToken) {
        synchronized (this.containingDatasetLock) {
            this.containingDataset = containingTopicToken;
            // TODO Robustness issue here, containingDataset may have different FDN heirarchy to actual identifier
        }
    }

    public boolean hasDatasetType() {
        if (this.datasetType == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public TopicTypeEnum getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(TopicTypeEnum datasetType) {
        synchronized (this.datasetTypeLock) {
            this.datasetType = datasetType;
        }
    }

    public boolean hasIdentifier() {
        if (this.identifier == null) {
            return (false);
        } else {
            return (true);
        }
    }

    public FDNToken getIdentifier() {
        return identifier;
    }

    public void setIdentifier(FDNToken identifier) {
        synchronized (this.identifierLock) {
            this.identifier = identifier;
        }
    }

    public boolean hasContainedDatasets() {
        if (this.containedDatasets == null) {
            return (false);
        }
        if (this.containedDatasets.isEmpty()) {
            return (false);
        }
        return (true);
    }

    public FDNTokenSet getContainedDatasets() {
        return containedDatasets;
    }

    public void setContainedDatasets(FDNTokenSet containedDatasets) {
        synchronized (this.containedDatasetsLock) {
            this.containedDatasets = containedDatasets;
        }
    }

    public void addContainedTopic(FDNToken newContainedTopic) {
        synchronized (this.containedDatasetsLock) {
            this.containedDatasets.addElement(newContainedTopic);
            // TODO Should be more robust in checking the FDNToken itself is consistent with containment heirarchy
        }
    }

    public void removeContainedTopic(FDNToken topicToRemove) {
        synchronized (this.containedDatasetsLock) {
            this.containedDatasets.removeElement(topicToRemove);
        }
    }

    public void setTopicType(String topicTypeString) {
        synchronized (datasetTypeLock) {
            boolean matched = false;
            for (TopicTypeEnum topicTypeValue : TopicTypeEnum.values()) {
                if (topicTypeValue.getTopicType().contentEquals(topicTypeString)) {
                    this.datasetType = topicTypeValue;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                if (hasIdentifier()) {
                    FDN topicFDN = new FDN(this.identifier);
                    switch (topicFDN.getRDNCount()) {
                        case 1:
                            this.datasetType = TopicTypeEnum.DATASET_DEFINER;
                            break;
                        case 2:
                            this.datasetType = TopicTypeEnum.DATASET_CATEGORY;
                            break;
                        case 3:
                            this.datasetType = TopicTypeEnum.DATASET_SUBCATEGORY;
                            break;
                        case 4:
                            this.datasetType = TopicTypeEnum.DATASET_RESOURCE;
                            break;
                        case 5:
                            this.datasetType = TopicTypeEnum.DATASET_DISCRIMINATOR_TYPE;
                            break;
                        case 6:
                        default:
                            this.datasetType = TopicTypeEnum.DATASET_DISCRIMINATOR_VALUE;
                    }
                } else {
                    this.datasetType = TopicTypeEnum.DATASET_RESOURCE;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return getDatasetType() == topic.getDatasetType() &&
                Objects.equals(getIdentifier(), topic.getIdentifier()) &&
                Objects.equals(getContainedDatasets(), topic.getContainedDatasets()) &&
                Objects.equals(getContainingDataset(), topic.getContainingDataset()) &&
                Objects.equals(version, topic.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatasetType(), getIdentifier(), getContainedDatasets(), getContainingDataset(), version);
    }

    public TopicToken getTopicToken(){
        TopicToken token = new TopicToken();
        token.setIdentifier(this.identifier);
        token.setVersion(this.version);
        return(token);
    }

    @Override
    public String toString() {
        return "Topic{" +
                "datasetType=" + datasetType +
                ", identifier=" + identifier +
                ", containedDatasets=" + containedDatasets +
                ", containingDataset=" + containingDataset +
                ", version='" + version + '\'' +
                '}';
    }
}

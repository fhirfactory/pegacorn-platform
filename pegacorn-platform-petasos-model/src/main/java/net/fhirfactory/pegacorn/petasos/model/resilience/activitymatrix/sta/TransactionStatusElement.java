/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.sta;

import net.fhirfactory.pegacorn.components.transaction.valuesets.TransactionStatusEnum;
import net.fhirfactory.pegacorn.components.transaction.valuesets.TransactionTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;

import java.time.Instant;
import java.util.Date;

public class TransactionStatusElement {
    private WUPJobCard jobCard;
    private UoW unitOfWork;
    private TransactionStatusEnum transactionStatus;
    private TransactionTypeEnum transactionType;
    private String transactionStatusCommentary;
    private Date transactionStartTime;
    private Date transactionFinishedTime;
    private Date transactionFinalisedTime;
    private Date statusUpdateTime;

    public TransactionStatusElement(){
        this.jobCard = null;
        this.unitOfWork = null;
        this.transactionStatus = null;
        this.transactionType = null;
        this.transactionStartTime = null;
        this.transactionFinishedTime = null;
        this.transactionFinalisedTime = null;
        this.transactionStatusCommentary = null;
        this.statusUpdateTime = Date.from(Instant.now());
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

    public TransactionStatusEnum getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatusEnum transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public TransactionTypeEnum getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
    }

    public Date getTransactionStartTime() {
        return transactionStartTime;
    }

    public void setTransactionStartTime(Date transactionStartTime) {
        this.transactionStartTime = transactionStartTime;
    }

    public Date getTransactionFinishedTime() {
        return transactionFinishedTime;
    }

    public void setTransactionFinishedTime(Date transactionFinishedTime) {
        this.transactionFinishedTime = transactionFinishedTime;
    }

    public Date getTransactionFinalisedTime() {
        return transactionFinalisedTime;
    }

    public void setTransactionFinalisedTime(Date transactionFinalisedTime) {
        this.transactionFinalisedTime = transactionFinalisedTime;
    }

    public Date getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(Date statusUpdateTime) {
        this.statusUpdateTime = statusUpdateTime;
    }

    public String getTransactionStatusCommentary() {
        return transactionStatusCommentary;
    }

    public void setTransactionStatusCommentary(String transactionStatusCommentary) {
        this.transactionStatusCommentary = transactionStatusCommentary;
    }

    @Override
    public String toString() {
        return "TransactionStatusElement{" +
                "jobCard=" + jobCard +
                ", unitOfWork=" + unitOfWork +
                ", transactionStatus=" + transactionStatus +
                ", transactionType=" + transactionType +
                ", transactionStatusCommentary='" + transactionStatusCommentary + '\'' +
                ", transactionStartTime=" + transactionStartTime +
                ", transactionFinishedTime=" + transactionFinishedTime +
                ", transactionFinalisedTime=" + transactionFinalisedTime +
                ", statusUpdateTime=" + statusUpdateTime +
                '}';
    }
}

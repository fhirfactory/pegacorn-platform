/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.fhirfactory.pegacorn.internals.workflows;

import javax.inject.Singleton;

/**
 *
 * @author Mark A. Hunter (ACT Health)
 */

public class EventAction {
    
    private static final String MESSAGE_ACTION_CREATE = "pegacorn.event.action.Create";
    private static final String MESSAGE_ACTION_UPDATE = "pegacorn.event.action.Update";
    private static final String MESSAGE_ACTION_DELETE = "pegacorn.event.action.Delete";
    private static final String MESSAGE_ACTION_REVIEW = "pegacorn.event.action.Review";
    
    private static final String MESSAGE_ACTION_UPDATE_ADD = "pegacorn.event.action.Update.Add";
    private static final String MESSAGE_ACTION_UPDATE_REMOVE = "pegacorn.event.action.Update.Remove";
    
    private static final String MESSAGE_ACTION_URL = "http://pegacorn.fhirbox.net/pegacorn/R1/event/event_action";
    
    public String getActionCreate(){return(MESSAGE_ACTION_CREATE);}
    public String getActionUpdate(){return(MESSAGE_ACTION_UPDATE);}
    public String getActionDelete(){return(MESSAGE_ACTION_DELETE);}
    public String getActionReview(){return(MESSAGE_ACTION_REVIEW);}
    public String getActionUpdateAdd(){return(MESSAGE_ACTION_UPDATE_ADD);}
    public String getActionUpdateRemove(){return(MESSAGE_ACTION_UPDATE_REMOVE);}
    
    public String getEventActionSystem(){return(MESSAGE_ACTION_URL);}
    
    public boolean isActionCreate(String testValue){
        if( testValue == null ){
            return(false);
        }
        if( testValue.isEmpty()){
            return(false);
        }
        if( testValue.contains(MESSAGE_ACTION_CREATE)){
            return(true);
        }
        return(false);
    }
    
    public boolean isActionUpdate(String testValue){
        if( testValue == null ){
            return(false);
        }
        if( testValue.isEmpty()){
            return(false);
        }
        if( testValue.contains(MESSAGE_ACTION_UPDATE)){
            return(true);
        }
        return(false);
    }    
    
    public boolean isActionDelete(String testValue){
        if( testValue == null ){
            return(false);
        }
        if( testValue.isEmpty()){
            return(false);
        }
        if( testValue.contains(MESSAGE_ACTION_DELETE)){
            return(true);
        }
        return(false);
    }   

    public boolean isActionReview(String testValue){
        if( testValue == null ){
            return(false);
        }
        if( testValue.isEmpty()){
            return(false);
        }
        if( testValue.contains(MESSAGE_ACTION_REVIEW)){
            return(true);
        }
        return(false);
    }   
    
    public boolean isActionUpdateAdd(String testValue){
        if( testValue == null ){
            return(false);
        }
        if( testValue.isEmpty()){
            return(false);
        }
        if( testValue.contains(MESSAGE_ACTION_UPDATE_ADD)){
            return(true);
        }
        return(false);
    }      
    
    public boolean isActionUpdateRemove(String testValue){
        if( testValue == null ){
            return(false);
        }
        if( testValue.isEmpty()){
            return(false);
        }
        if( testValue.contains(MESSAGE_ACTION_UPDATE_REMOVE)){
            return(true);
        }
        return(false);
    }    
}

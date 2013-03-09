/**
 * Copyright (C) 2012, Grass CRM Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gcrm.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gcrm.domain.Account;
import com.gcrm.domain.Call;
import com.gcrm.domain.CallDirection;
import com.gcrm.domain.CallStatus;
import com.gcrm.domain.Case;
import com.gcrm.domain.Contact;
import com.gcrm.domain.Lead;
import com.gcrm.domain.Opportunity;
import com.gcrm.domain.ReminderOption;
import com.gcrm.domain.Target;
import com.gcrm.domain.Task;
import com.gcrm.domain.User;
import com.gcrm.service.IBaseService;
import com.gcrm.service.IOptionService;
import com.gcrm.util.BeanUtil;
import com.gcrm.util.CommonUtil;
import com.gcrm.util.Constant;
import com.gcrm.util.security.UserUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

/**
 * Edits Call
 * 
 */
public class EditCallAction extends BaseEditAction implements Preparable {

    private static final long serialVersionUID = -2404576552417042445L;

    private IBaseService<Call> baseService;
    private IOptionService<CallStatus> callStatusService;
    private IOptionService<CallDirection> callDirectionService;
    private IOptionService<ReminderOption> reminderOptionService;
    private IBaseService<User> userService;
    private IBaseService<Account> accountService;
    private IBaseService<Case> caseService;
    private IBaseService<Contact> contactService;
    private IBaseService<Lead> leadService;
    private IBaseService<Opportunity> opportunityService;
    private IBaseService<Target> targetService;
    private IBaseService<Task> taskService;
    private Call call;
    private List<CallStatus> statuses;
    private List<CallDirection> directions;
    private List<ReminderOption> reminderOptions;
    private Integer statusID = null;
    private Integer directionID = null;
    private Integer reminderOptionPopID = null;
    private Integer reminderOptionEmailID = null;
    private Integer relatedAccountID = null;
    private String relatedAccountText = null;
    private Integer relatedCaseID = null;
    private String relatedCaseText = null;
    private Integer relatedContactID = null;
    private String relatedContactText = null;
    private Integer relatedLeadID = null;
    private String relatedLeadText = null;
    private Integer relatedOpportunityID = null;
    private String relatedOpportunityText = null;
    private Integer relatedTargetID = null;
    private String relatedTargetText = null;
    private Integer relatedTaskID = null;
    private String relatedTaskText = null;
    private String startDate = null;

    /**
     * Saves the entity.
     * 
     * @return the SUCCESS result
     */
    public String save() throws Exception {
        saveEntity();
        call = getbaseService().makePersistent(call);
        this.setId(call.getId());
        this.setSaveFlag("true");
        return SUCCESS;
    }

    /**
     * Gets the entity.
     * 
     * @return the SUCCESS result
     */
    public String get() throws Exception {
        if (this.getId() != null) {
            UserUtil.permissionCheck("view_call");
            call = baseService.getEntityById(Call.class, this.getId());
            UserUtil.scopeCheck(call, "scope_call");
            CallStatus status = call.getStatus();
            if (status != null) {
                statusID = status.getId();
            }
            CallDirection direction = call.getDirection();
            if (direction != null) {
                directionID = direction.getId();
            }
            ReminderOption reminderOptionPop = call.getReminder_option_pop();
            if (reminderOptionPop != null) {
                reminderOptionPopID = reminderOptionPop.getId();
            }
            ReminderOption reminderOptionEmail = call
                    .getReminder_option_email();
            if (reminderOptionEmail != null) {
                reminderOptionEmailID = reminderOptionEmail.getId();
            }
            User assignedTo = call.getAssigned_to();
            if (assignedTo != null) {
                this.setAssignedToID(assignedTo.getId());
                this.setAssignedToText(assignedTo.getName());
            }
            Date start_date = call.getStart_date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    Constant.DATE_TIME_FORMAT);
            if (start_date != null) {
                startDate = dateFormat.format(start_date);
            }
            String relatedObject = call.getRelated_object();
            Integer relatedRecord = call.getRelated_record();
            setRelatedRecord(relatedObject, relatedRecord);
            this.getBaseInfo(call);
        } else {
            this.initBaseInfo();
            if (!CommonUtil.isNullOrEmpty(this.getRelationKey())) {
                call.setRelated_object(this.getRelationKey());
                setRelatedRecord(this.getRelationKey(),
                        Integer.parseInt(this.getRelationValue()));
            }
        }
        return SUCCESS;
    }

    /**
     * Sets the related record ID
     * 
     * @param relatedObject
     *            Related Object name
     * @param relatedRecord
     *            Related Record ID
     */
    private void setRelatedRecord(String relatedObject, Integer relatedRecord) {
        if ("Account".equals(relatedObject)) {
            this.relatedAccountID = relatedRecord;
            if (relatedRecord != null) {
                this.relatedAccountText = this.accountService.getEntityById(
                        Account.class, relatedRecord).getName();
            }
        } else if ("Case".equals(relatedObject)) {
            this.relatedCaseID = relatedRecord;
            if (relatedRecord != null) {
                this.relatedCaseText = this.caseService.getEntityById(
                        Case.class, relatedRecord).getSubject();
            }
        } else if ("Contact".equals(relatedObject)) {
            this.relatedContactID = relatedRecord;
            if (relatedRecord != null) {
                this.relatedContactText = this.contactService.getEntityById(
                        Contact.class, relatedRecord).getName();
            }
        } else if ("Lead".equals(relatedObject)) {
            this.relatedLeadID = relatedRecord;
            if (relatedRecord != null) {
                this.relatedLeadText = this.leadService.getEntityById(
                        Lead.class, relatedRecord).getName();
            }
        } else if ("Opportunity".equals(relatedObject)) {
            this.relatedOpportunityID = relatedRecord;
            if (relatedRecord != null) {
                this.relatedOpportunityText = this.opportunityService
                        .getEntityById(Opportunity.class, relatedRecord)
                        .getName();
            }
        } else if ("Target".equals(relatedObject)) {
            this.relatedTargetID = relatedRecord;
            if (relatedRecord != null) {
                this.relatedTargetText = this.targetService.getEntityById(
                        Target.class, relatedRecord).getName();
            }
        } else if ("Task".equals(relatedObject)) {
            this.relatedTaskID = relatedRecord;
            if (relatedRecord != null) {
                this.relatedTaskText = this.taskService.getEntityById(
                        Task.class, relatedRecord).getSubject();
            }
        }
    }

    /**
     * Mass update entity record information
     */
    public String massUpdate() throws Exception {
        saveEntity();
        String[] fieldNames = this.massUpdate;
        if (fieldNames != null) {
            Collection<String> feildNameCollection = new ArrayList<String>();
            for (int i = 0; i < fieldNames.length; i++) {
                feildNameCollection.add(fieldNames[i]);
                if ("reminder_pop".equals(fieldNames[i])) {
                    feildNameCollection.add("reminder_email");
                    feildNameCollection.add("reminder_option_pop");
                    feildNameCollection.add("reminder_option_email");
                }
            }

            String[] selectIDArray = this.seleteIDs.split(",");
            Collection<Call> calls = new ArrayList<Call>();
            User loginUser = this.getLoginUser();
            User user = userService
                    .getEntityById(User.class, loginUser.getId());
            for (String IDString : selectIDArray) {
                int id = Integer.parseInt(IDString);
                Call callInstance = this.baseService.getEntityById(Call.class,
                        id);
                for (String fieldName : feildNameCollection) {
                    Object value = BeanUtil.getFieldValue(call, fieldName);
                    BeanUtil.setFieldValue(callInstance, fieldName, value);
                }
                callInstance.setUpdated_by(user);
                callInstance.setUpdated_on(new Date());
                calls.add(callInstance);
            }
            if (calls.size() > 0) {
                this.baseService.batchUpdate(calls);
            }
        }
        return SUCCESS;
    }

    /**
     * Saves entity field
     * 
     * @throws ParseException
     */
    private void saveEntity() throws Exception {
        if (call.getId() == null) {
            UserUtil.permissionCheck("create_call");
        } else {
            UserUtil.permissionCheck("update_call");
            Call originalCall = baseService.getEntityById(Call.class,
                    call.getId());
            call.setContacts(originalCall.getContacts());
            call.setLeads(originalCall.getLeads());
            call.setUsers(originalCall.getUsers());
        }

        CallDirection direction = null;
        if (directionID != null) {
            direction = callDirectionService.getEntityById(CallDirection.class,
                    directionID);
        }
        call.setDirection(direction);
        CallStatus status = null;
        if (statusID != null) {
            status = callStatusService
                    .getEntityById(CallStatus.class, statusID);
        }
        call.setStatus(status);
        ReminderOption reminderOptionPop = null;
        if (reminderOptionPopID != null) {
            reminderOptionPop = reminderOptionService.getEntityById(
                    ReminderOption.class, reminderOptionPopID);
        }
        call.setReminder_option_pop(reminderOptionPop);
        ReminderOption reminderOptionEmail = null;
        if (reminderOptionEmailID != null) {
            reminderOptionEmail = reminderOptionService.getEntityById(
                    ReminderOption.class, reminderOptionEmailID);
        }
        call.setReminder_option_email(reminderOptionEmail);
        User user = null;
        if (this.getAssignedToID() != null) {
            user = userService
                    .getEntityById(User.class, this.getAssignedToID());
        }
        call.setAssigned_to(user);
        User owner = null;
        if (this.getOwnerID() != null) {
            owner = userService.getEntityById(User.class, this.getOwnerID());
        }
        call.setOwner(owner);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                Constant.DATE_TIME_FORMAT);
        Date start_date = null;
        if (!CommonUtil.isNullOrEmpty(startDate)) {
            start_date = dateFormat.parse(startDate);
        }
        call.setStart_date(start_date);

        String relatedObject = call.getRelated_object();
        if ("Account".equals(relatedObject)) {
            call.setRelated_record(relatedAccountID);
        } else if ("Case".equals(relatedObject)) {
            call.setRelated_record(relatedCaseID);
        } else if ("Contact".equals(relatedObject)) {
            call.setRelated_record(relatedContactID);
        } else if ("Lead".equals(relatedObject)) {
            call.setRelated_record(relatedLeadID);
        } else if ("Opportunity".equals(relatedObject)) {
            call.setRelated_record(relatedOpportunityID);
        } else if ("Target".equals(relatedObject)) {
            call.setRelated_record(relatedTargetID);
        } else if ("Task".equals(relatedObject)) {
            call.setRelated_record(relatedTaskID);
        }
        super.updateBaseInfo(call);
    }

    /**
     * Prepares the list
     * 
     */
    public void prepare() throws Exception {
        ActionContext context = ActionContext.getContext();
        Map<String, Object> session = context.getSession();
        String local = (String) session.get("locale");
        this.statuses = callStatusService.getOptions(
                CallStatus.class.getSimpleName(), local);
        this.directions = callDirectionService.getOptions(
                CallDirection.class.getSimpleName(), local);
        this.reminderOptions = reminderOptionService.getOptions(
                ReminderOption.class.getSimpleName(), local);
    }

    public IBaseService<Call> getbaseService() {
        return baseService;
    }

    public void setbaseService(IBaseService<Call> baseService) {
        this.baseService = baseService;
    }

    /**
     * @return the statusID
     */
    public Integer getStatusID() {
        return statusID;
    }

    /**
     * @param statusID
     *            the statusID to set
     */
    public void setStatusID(Integer statusID) {
        this.statusID = statusID;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(IBaseService<User> userService) {
        this.userService = userService;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the reminderOptions
     */
    public List<ReminderOption> getReminderOptions() {
        return reminderOptions;
    }

    /**
     * @param reminderOptions
     *            the reminderOptions to set
     */
    public void setReminderOptions(List<ReminderOption> reminderOptions) {
        this.reminderOptions = reminderOptions;
    }

    /**
     * @return the call
     */
    public Call getCall() {
        return call;
    }

    /**
     * @param call
     *            the call to set
     */
    public void setCall(Call call) {
        this.call = call;
    }

    /**
     * @return the statuses
     */
    public List<CallStatus> getStatuses() {
        return statuses;
    }

    /**
     * @param statuses
     *            the statuses to set
     */
    public void setStatuses(List<CallStatus> statuses) {
        this.statuses = statuses;
    }

    /**
     * @return the directionID
     */
    public Integer getDirectionID() {
        return directionID;
    }

    /**
     * @param directionID
     *            the directionID to set
     */
    public void setDirectionID(Integer directionID) {
        this.directionID = directionID;
    }

    /**
     * @return the directions
     */
    public List<CallDirection> getDirections() {
        return directions;
    }

    /**
     * @param directions
     *            the directions to set
     */
    public void setDirections(List<CallDirection> directions) {
        this.directions = directions;
    }

    /**
     * @return the baseService
     */
    public IBaseService<Call> getBaseService() {
        return baseService;
    }

    /**
     * @param baseService
     *            the baseService to set
     */
    public void setBaseService(IBaseService<Call> baseService) {
        this.baseService = baseService;
    }

    /**
     * @return the relatedAccountID
     */
    public Integer getRelatedAccountID() {
        return relatedAccountID;
    }

    /**
     * @param relatedAccountID
     *            the relatedAccountID to set
     */
    public void setRelatedAccountID(Integer relatedAccountID) {
        this.relatedAccountID = relatedAccountID;
    }

    /**
     * @return the relatedCaseID
     */
    public Integer getRelatedCaseID() {
        return relatedCaseID;
    }

    /**
     * @param relatedCaseID
     *            the relatedCaseID to set
     */
    public void setRelatedCaseID(Integer relatedCaseID) {
        this.relatedCaseID = relatedCaseID;
    }

    /**
     * @return the relatedContactID
     */
    public Integer getRelatedContactID() {
        return relatedContactID;
    }

    /**
     * @param relatedContactID
     *            the relatedContactID to set
     */
    public void setRelatedContactID(Integer relatedContactID) {
        this.relatedContactID = relatedContactID;
    }

    /**
     * @return the relatedLeadID
     */
    public Integer getRelatedLeadID() {
        return relatedLeadID;
    }

    /**
     * @param relatedLeadID
     *            the relatedLeadID to set
     */
    public void setRelatedLeadID(Integer relatedLeadID) {
        this.relatedLeadID = relatedLeadID;
    }

    /**
     * @return the relatedOpportunityID
     */
    public Integer getRelatedOpportunityID() {
        return relatedOpportunityID;
    }

    /**
     * @param relatedOpportunityID
     *            the relatedOpportunityID to set
     */
    public void setRelatedOpportunityID(Integer relatedOpportunityID) {
        this.relatedOpportunityID = relatedOpportunityID;
    }

    /**
     * @return the relatedTargetID
     */
    public Integer getRelatedTargetID() {
        return relatedTargetID;
    }

    /**
     * @param relatedTargetID
     *            the relatedTargetID to set
     */
    public void setRelatedTargetID(Integer relatedTargetID) {
        this.relatedTargetID = relatedTargetID;
    }

    /**
     * @return the relatedTaskID
     */
    public Integer getRelatedTaskID() {
        return relatedTaskID;
    }

    /**
     * @param relatedTaskID
     *            the relatedTaskID to set
     */
    public void setRelatedTaskID(Integer relatedTaskID) {
        this.relatedTaskID = relatedTaskID;
    }

    /**
     * @return the userService
     */
    public IBaseService<User> getUserService() {
        return userService;
    }

    /**
     * @return the reminderOptionPopID
     */
    public Integer getReminderOptionPopID() {
        return reminderOptionPopID;
    }

    /**
     * @param reminderOptionPopID
     *            the reminderOptionPopID to set
     */
    public void setReminderOptionPopID(Integer reminderOptionPopID) {
        this.reminderOptionPopID = reminderOptionPopID;
    }

    /**
     * @return the reminderOptionEmailID
     */
    public Integer getReminderOptionEmailID() {
        return reminderOptionEmailID;
    }

    /**
     * @param reminderOptionEmailID
     *            the reminderOptionEmailID to set
     */
    public void setReminderOptionEmailID(Integer reminderOptionEmailID) {
        this.reminderOptionEmailID = reminderOptionEmailID;
    }

    /**
     * @return the relatedAccountText
     */
    public String getRelatedAccountText() {
        return relatedAccountText;
    }

    /**
     * @return the relatedCaseText
     */
    public String getRelatedCaseText() {
        return relatedCaseText;
    }

    /**
     * @return the relatedContactText
     */
    public String getRelatedContactText() {
        return relatedContactText;
    }

    /**
     * @return the relatedLeadText
     */
    public String getRelatedLeadText() {
        return relatedLeadText;
    }

    /**
     * @return the relatedOpportunityText
     */
    public String getRelatedOpportunityText() {
        return relatedOpportunityText;
    }

    /**
     * @return the relatedTargetText
     */
    public String getRelatedTargetText() {
        return relatedTargetText;
    }

    /**
     * @return the relatedTaskText
     */
    public String getRelatedTaskText() {
        return relatedTaskText;
    }

    /**
     * @return the accountService
     */
    public IBaseService<Account> getAccountService() {
        return accountService;
    }

    /**
     * @param accountService
     *            the accountService to set
     */
    public void setAccountService(IBaseService<Account> accountService) {
        this.accountService = accountService;
    }

    /**
     * @return the caseService
     */
    public IBaseService<Case> getCaseService() {
        return caseService;
    }

    /**
     * @param caseService
     *            the caseService to set
     */
    public void setCaseService(IBaseService<Case> caseService) {
        this.caseService = caseService;
    }

    /**
     * @return the contactService
     */
    public IBaseService<Contact> getContactService() {
        return contactService;
    }

    /**
     * @param contactService
     *            the contactService to set
     */
    public void setContactService(IBaseService<Contact> contactService) {
        this.contactService = contactService;
    }

    /**
     * @return the leadService
     */
    public IBaseService<Lead> getLeadService() {
        return leadService;
    }

    /**
     * @param leadService
     *            the leadService to set
     */
    public void setLeadService(IBaseService<Lead> leadService) {
        this.leadService = leadService;
    }

    /**
     * @return the opportunityService
     */
    public IBaseService<Opportunity> getOpportunityService() {
        return opportunityService;
    }

    /**
     * @param opportunityService
     *            the opportunityService to set
     */
    public void setOpportunityService(
            IBaseService<Opportunity> opportunityService) {
        this.opportunityService = opportunityService;
    }

    /**
     * @return the targetService
     */
    public IBaseService<Target> getTargetService() {
        return targetService;
    }

    /**
     * @param targetService
     *            the targetService to set
     */
    public void setTargetService(IBaseService<Target> targetService) {
        this.targetService = targetService;
    }

    /**
     * @return the taskService
     */
    public IBaseService<Task> getTaskService() {
        return taskService;
    }

    /**
     * @param taskService
     *            the taskService to set
     */
    public void setTaskService(IBaseService<Task> taskService) {
        this.taskService = taskService;
    }

    /**
     * @return the callStatusService
     */
    public IOptionService<CallStatus> getCallStatusService() {
        return callStatusService;
    }

    /**
     * @param callStatusService
     *            the callStatusService to set
     */
    public void setCallStatusService(
            IOptionService<CallStatus> callStatusService) {
        this.callStatusService = callStatusService;
    }

    /**
     * @return the callDirectionService
     */
    public IOptionService<CallDirection> getCallDirectionService() {
        return callDirectionService;
    }

    /**
     * @param callDirectionService
     *            the callDirectionService to set
     */
    public void setCallDirectionService(
            IOptionService<CallDirection> callDirectionService) {
        this.callDirectionService = callDirectionService;
    }

    /**
     * @return the reminderOptionService
     */
    public IOptionService<ReminderOption> getReminderOptionService() {
        return reminderOptionService;
    }

    /**
     * @param reminderOptionService
     *            the reminderOptionService to set
     */
    public void setReminderOptionService(
            IOptionService<ReminderOption> reminderOptionService) {
        this.reminderOptionService = reminderOptionService;
    }

}

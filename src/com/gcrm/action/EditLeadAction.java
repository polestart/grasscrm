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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gcrm.domain.Account;
import com.gcrm.domain.Call;
import com.gcrm.domain.Campaign;
import com.gcrm.domain.Contact;
import com.gcrm.domain.Lead;
import com.gcrm.domain.LeadSource;
import com.gcrm.domain.LeadStatus;
import com.gcrm.domain.Meeting;
import com.gcrm.domain.Opportunity;
import com.gcrm.domain.TargetList;
import com.gcrm.domain.User;
import com.gcrm.security.AuthenticationSuccessListener;
import com.gcrm.service.IBaseService;
import com.gcrm.service.ILeadService;
import com.gcrm.util.BeanUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

/**
 * Edits Lead
 * 
 */
public class EditLeadAction extends BaseEditAction implements Preparable {

    private static final long serialVersionUID = -2404576552417042445L;

    private ILeadService baseService;
    private IBaseService<Account> accountService;
    private IBaseService<LeadStatus> leadStatusService;
    private IBaseService<LeadSource> leadSourceService;
    private IBaseService<User> userService;
    private IBaseService<Campaign> campaignService;
    private IBaseService<Contact> contactService;
    private IBaseService<Opportunity> opportunityService;
    private IBaseService<TargetList> targetListService;
    private IBaseService<Call> callService;
    private IBaseService<Meeting> meetingService;
    private Lead lead;
    private List<LeadStatus> leadStatuses;
    private List<LeadSource> leadSources;
    private Integer accountID = null;
    private String accountText = null;
    private Integer leadStatusID = null;
    private Integer leadSourceID = null;
    private Integer campaignID = null;
    private String campaignText = null;
    private Integer assignedToID = null;
    private String assignedToText = null;
    private boolean accountCheck;
    private boolean contactCheck;
    private boolean opportunityCheck;

    /**
     * Saves the entity.
     * 
     * @return the SUCCESS result
     */
    public String save() throws Exception {
        saveEntity();
        this.getBaseService().makePersistent(lead);
        return SUCCESS;
    }

    /**
     * Gets the entity.
     * 
     * @return the SUCCESS result
     */
    public String get() throws Exception {
        if (this.getId() != null) {
            lead = baseService.getEntityById(Lead.class, this.getId());
            Account account = lead.getAccount();
            if (account != null) {
                accountID = account.getId();
                accountText = account.getName();
            }

            LeadStatus leadStatus = lead.getStatus();
            if (leadStatus != null) {
                leadStatusID = leadStatus.getId();
            }

            LeadSource leadSource = lead.getLead_source();
            if (leadSource != null) {
                leadSourceID = leadSource.getId();
            }

            Campaign campaign = lead.getCampaign();
            if (campaign != null) {
                campaignID = campaign.getId();
                campaignText = campaign.getName();
            }
            User user = lead.getAssigned_to();
            if (user != null) {
                assignedToID = user.getId();
                assignedToText = user.getName();
            }
            this.getBaseInfo(lead);
        } else {
            ActionContext context = ActionContext.getContext();
            Map<String, Object> session = context.getSession();
            User loginUser = (User) session
                    .get(AuthenticationSuccessListener.LOGIN_USER);
            assignedToID = loginUser.getId();
            assignedToText = loginUser.getName();
        }
        return SUCCESS;
    }

    /**
     * Mass update entity record information
     */
    public String massUpdate() throws Exception {
        saveEntity();
        String[] fieldNames = this.massUpdate;
        String[] selectIDArray = this.seleteIDs.split(",");
        Collection<Lead> leads = new ArrayList<Lead>();
        User loginUser = this.getLoginUser();
        User user = userService.getEntityById(User.class, loginUser.getId());
        for (String IDString : selectIDArray) {
            int id = Integer.parseInt(IDString);
            Lead leadInstance = this.baseService.getEntityById(Lead.class, id);
            for (String fieldName : fieldNames) {
                Object value = BeanUtil.getFieldValue(lead, fieldName);
                BeanUtil.setFieldValue(leadInstance, fieldName, value);
            }
            leadInstance.setUpdated_by(user);
            leadInstance.setUpdated_on(new Date());
            leads.add(leadInstance);
        }
        if (leads.size() > 0) {
            this.baseService.batchUpdate(leads);
        }
        return SUCCESS;
    }

    /**
     * Saves entity field
     */
    private void saveEntity() {
        Account account = null;
        if (accountID != null) {
            account = accountService.getEntityById(Account.class, accountID);
        }
        lead.setAccount(account);

        LeadStatus leadStatus = null;
        if (leadStatusID != null) {
            leadStatus = leadStatusService.getEntityById(LeadStatus.class,
                    leadStatusID);
        }
        lead.setStatus(leadStatus);

        LeadSource leadSource = null;
        if (leadSourceID != null) {
            leadSource = leadSourceService.getEntityById(LeadSource.class,
                    leadSourceID);
        }
        lead.setLead_source(leadSource);

        User user = null;
        if (assignedToID != null) {
            user = userService.getEntityById(User.class, assignedToID);
        }
        lead.setAssigned_to(user);

        Campaign campaign = null;
        if (campaignID != null) {
            campaign = campaignService
                    .getEntityById(Campaign.class, campaignID);
        }
        lead.setCampaign(campaign);

        if ("Opportunity".equals(this.getRelationKey())) {
            Opportunity opportunity = opportunityService
                    .getEntityById(Opportunity.class,
                            Integer.valueOf(this.getRelationValue()));
            Set<Opportunity> opportunities = lead.getOpportunities();
            if (opportunities == null) {
                opportunities = new HashSet<Opportunity>();
            }
            opportunities.add(opportunity);
        } else if ("TargetList".equals(this.getRelationKey())) {
            TargetList targetList = targetListService.getEntityById(
                    TargetList.class, Integer.valueOf(this.getRelationValue()));
            Set<TargetList> targetLists = lead.getTargetLists();
            if (targetLists == null) {
                targetLists = new HashSet<TargetList>();
            }
            targetLists.add(targetList);
        } else if ("Call".equals(this.getRelationKey())) {
            Call call = callService.getEntityById(Call.class,
                    Integer.valueOf(this.getRelationValue()));
            Set<Call> calls = lead.getCalls();
            if (calls == null) {
                calls = new HashSet<Call>();
            }
            calls.add(call);
        } else if ("Meeting".equals(this.getRelationKey())) {
            Meeting meeting = meetingService.getEntityById(Meeting.class,
                    Integer.valueOf(this.getRelationValue()));
            Set<Meeting> meetings = lead.getMeetings();
            if (meetings == null) {
                meetings = new HashSet<Meeting>();
            }
            meetings.add(meeting);
        } else if ("Contact".equals(this.getRelationKey())) {
            Contact contact = contactService.getEntityById(Contact.class,
                    Integer.valueOf(this.getRelationValue()));
            Set<Contact> contacts = lead.getContacts();
            if (contacts == null) {
                contacts = new HashSet<Contact>();
            }
            contacts.add(contact);
        }
        super.updateBaseInfo(lead);
    }

    /**
     * Prepares the list
     * 
     */
    public void prepare() throws Exception {
        this.leadStatuses = leadStatusService.getAllObjects(LeadStatus.class
                .getSimpleName());
        this.leadSources = leadSourceService.getAllObjects(LeadSource.class
                .getSimpleName());
    }

    /**
     * Converts the lead
     * 
     * @return the SUCCESS result
     */
    public String convert() throws Exception {
        this.getBaseService().convert(this.getId(), accountCheck, contactCheck,
                opportunityCheck);
        return SUCCESS;
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
     * @return the leadSourceService
     */
    public IBaseService<LeadSource> getLeadSourceService() {
        return leadSourceService;
    }

    /**
     * @param leadSourceService
     *            the leadSourceService to set
     */
    public void setLeadSourceService(IBaseService<LeadSource> leadSourceService) {
        this.leadSourceService = leadSourceService;
    }

    /**
     * @return the userService
     */
    public IBaseService<User> getUserService() {
        return userService;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(IBaseService<User> userService) {
        this.userService = userService;
    }

    /**
     * @return the campaignService
     */
    public IBaseService<Campaign> getCampaignService() {
        return campaignService;
    }

    /**
     * @param campaignService
     *            the campaignService to set
     */
    public void setCampaignService(IBaseService<Campaign> campaignService) {
        this.campaignService = campaignService;
    }

    /**
     * @return the lead
     */
    public Lead getLead() {
        return lead;
    }

    /**
     * @param lead
     *            the lead to set
     */
    public void setLead(Lead lead) {
        this.lead = lead;
    }

    /**
     * @return the accountID
     */
    public Integer getAccountID() {
        return accountID;
    }

    /**
     * @param accountID
     *            the accountID to set
     */
    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    /**
     * @return the leadSourceID
     */
    public Integer getLeadSourceID() {
        return leadSourceID;
    }

    /**
     * @param leadSourceID
     *            the leadSourceID to set
     */
    public void setLeadSourceID(Integer leadSourceID) {
        this.leadSourceID = leadSourceID;
    }

    /**
     * @return the campaignID
     */
    public Integer getCampaignID() {
        return campaignID;
    }

    /**
     * @param campaignID
     *            the campaignID to set
     */
    public void setCampaignID(Integer campaignID) {
        this.campaignID = campaignID;
    }

    /**
     * @return the assignedToID
     */
    public Integer getAssignedToID() {
        return assignedToID;
    }

    /**
     * @param assignedToID
     *            the assignedToID to set
     */
    public void setAssignedToID(Integer assignedToID) {
        this.assignedToID = assignedToID;
    }

    /**
     * @return the leadSources
     */
    public List<LeadSource> getLeadSources() {
        return leadSources;
    }

    /**
     * @param leadSources
     *            the leadSources to set
     */
    public void setLeadSources(List<LeadSource> leadSources) {
        this.leadSources = leadSources;
    }

    /**
     * @return the leadStatuses
     */
    public List<LeadStatus> getLeadStatuses() {
        return leadStatuses;
    }

    /**
     * @param leadStatuses
     *            the leadStatuses to set
     */
    public void setLeadStatuses(List<LeadStatus> leadStatuses) {
        this.leadStatuses = leadStatuses;
    }

    /**
     * @return the leadStatusID
     */
    public Integer getLeadStatusID() {
        return leadStatusID;
    }

    /**
     * @param leadStatusID
     *            the leadStatusID to set
     */
    public void setLeadStatusID(Integer leadStatusID) {
        this.leadStatusID = leadStatusID;
    }

    /**
     * @return the leadStatusService
     */
    public IBaseService<LeadStatus> getLeadStatusService() {
        return leadStatusService;
    }

    /**
     * @param leadStatusService
     *            the leadStatusService to set
     */
    public void setLeadStatusService(IBaseService<LeadStatus> leadStatusService) {
        this.leadStatusService = leadStatusService;
    }

    /**
     * @return the accountCheck
     */
    public boolean isAccountCheck() {
        return accountCheck;
    }

    /**
     * @param accountCheck
     *            the accountCheck to set
     */
    public void setAccountCheck(boolean accountCheck) {
        this.accountCheck = accountCheck;
    }

    /**
     * @return the contactCheck
     */
    public boolean isContactCheck() {
        return contactCheck;
    }

    /**
     * @param contactCheck
     *            the contactCheck to set
     */
    public void setContactCheck(boolean contactCheck) {
        this.contactCheck = contactCheck;
    }

    /**
     * @return the opportunityCheck
     */
    public boolean isOpportunityCheck() {
        return opportunityCheck;
    }

    /**
     * @param opportunityCheck
     *            the opportunityCheck to set
     */
    public void setOpportunityCheck(boolean opportunityCheck) {
        this.opportunityCheck = opportunityCheck;
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
     * @return the targetListService
     */
    public IBaseService<TargetList> getTargetListService() {
        return targetListService;
    }

    /**
     * @param targetListService
     *            the targetListService to set
     */
    public void setTargetListService(IBaseService<TargetList> targetListService) {
        this.targetListService = targetListService;
    }

    /**
     * @return the callService
     */
    public IBaseService<Call> getCallService() {
        return callService;
    }

    /**
     * @param callService
     *            the callService to set
     */
    public void setCallService(IBaseService<Call> callService) {
        this.callService = callService;
    }

    /**
     * @return the meetingService
     */
    public IBaseService<Meeting> getMeetingService() {
        return meetingService;
    }

    /**
     * @param meetingService
     *            the meetingService to set
     */
    public void setMeetingService(IBaseService<Meeting> meetingService) {
        this.meetingService = meetingService;
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
     * @return the accountText
     */
    public String getAccountText() {
        return accountText;
    }

    /**
     * @param accountText
     *            the accountText to set
     */
    public void setAccountText(String accountText) {
        this.accountText = accountText;
    }

    /**
     * @return the campaignText
     */
    public String getCampaignText() {
        return campaignText;
    }

    /**
     * @param campaignText
     *            the campaignText to set
     */
    public void setCampaignText(String campaignText) {
        this.campaignText = campaignText;
    }

    /**
     * @return the assignedToText
     */
    public String getAssignedToText() {
        return assignedToText;
    }

    /**
     * @param assignedToText
     *            the assignedToText to set
     */
    public void setAssignedToText(String assignedToText) {
        this.assignedToText = assignedToText;
    }

    /**
     * @return the baseService
     */
    public ILeadService getBaseService() {
        return baseService;
    }

    /**
     * @param baseService
     *            the baseService to set
     */
    public void setBaseService(ILeadService baseService) {
        this.baseService = baseService;
    }

}

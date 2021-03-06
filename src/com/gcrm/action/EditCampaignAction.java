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
import java.util.Set;

import com.gcrm.domain.Account;
import com.gcrm.domain.Campaign;
import com.gcrm.domain.CampaignStatus;
import com.gcrm.domain.CampaignType;
import com.gcrm.domain.Contact;
import com.gcrm.domain.Currency;
import com.gcrm.domain.EmailSetting;
import com.gcrm.domain.Lead;
import com.gcrm.domain.Target;
import com.gcrm.domain.TargetList;
import com.gcrm.domain.User;
import com.gcrm.security.AuthenticationSuccessListener;
import com.gcrm.service.IBaseService;
import com.gcrm.service.IOptionService;
import com.gcrm.util.BeanUtil;
import com.gcrm.util.CommonUtil;
import com.gcrm.util.Constant;
import com.gcrm.util.mail.MailService;
import com.gcrm.util.security.UserUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

/**
 * Edits Campaign
 * 
 */
public class EditCampaignAction extends BaseEditAction implements Preparable {

    private static final long serialVersionUID = -2404576552417042445L;

    private IBaseService<Campaign> baseService;
    private IOptionService<CampaignType> campaignTypeService;
    private IOptionService<CampaignStatus> campaignStatusService;
    private IBaseService<Currency> currencyService;
    private IBaseService<User> userService;
    private MailService mailService;
    private Campaign campaign;
    private List<CampaignType> types;
    private List<CampaignStatus> statuses;
    private List<Currency> currencies;
    private Integer statusID = null;
    private Integer typeID = null;
    private Integer currencyID = null;
    private String startDate = null;
    private String endDate = null;

    /**
     * Saves the entity.
     * 
     * @return the SUCCESS result
     */
    public String save() throws Exception {
        saveEntity();
        campaign = getbaseService().makePersistent(campaign);
        this.setId(campaign.getId());
        this.setSaveFlag("true");
        return SUCCESS;
    }

    /**
     * Sends invitation mail to all participants.
     * 
     * @return the SUCCESS result
     */
    public String sendInvites() throws Exception {

        UserUtil.permissionCheck("update_campaign");
        campaign = baseService.getEntityById(Campaign.class, campaign.getId());
        Date start_date = campaign.getStart_date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                Constant.DATE_TIME_FORMAT);
        String startDateS = "";
        if (start_date != null) {
            startDateS = dateFormat.format(start_date);
        }
        Date end_date = campaign.getEnd_date();
        String endDateS = "";
        if (end_date != null) {
            endDateS = dateFormat.format(end_date);
        }
        String name = CommonUtil.fromNullToEmpty(campaign.getName());
        String objective = CommonUtil.fromNullToEmpty(campaign.getObjective());
        ActionContext context = ActionContext.getContext();
        Map<String, Object> session = context.getSession();
        User loginUser = (User) session
                .get(AuthenticationSuccessListener.LOGIN_USER);

        StringBuilder targetEmails = new StringBuilder("");
        Set<TargetList> targetLists = campaign.getTargetLists();
        if (targetLists != null) {
            for (TargetList targetList : targetLists) {
                Set<Account> accounts = targetList.getAccounts();
                if (accounts != null) {
                    for (Account account : accounts) {
                        String email = account.getEmail();
                        if (CommonUtil.isNullOrEmpty(email)) {
                            continue;
                        }
                        if (targetEmails.length() > 0) {
                            targetEmails.append(",");
                        }
                        targetEmails.append(email);
                    }
                }
                Set<Lead> leads = targetList.getLeads();
                if (leads != null) {
                    for (Lead lead : leads) {
                        String email = lead.getEmail();
                        if (CommonUtil.isNullOrEmpty(email)) {
                            continue;
                        }
                        if (targetEmails.length() > 0) {
                            targetEmails.append(",");
                        }
                        targetEmails.append(email);
                    }
                }
                Set<Contact> contacts = targetList.getContacts();
                if (contacts != null) {
                    for (Contact contact : contacts) {
                        String email = contact.getEmail();
                        if (CommonUtil.isNullOrEmpty(email)) {
                            continue;
                        }
                        if (targetEmails.length() > 0) {
                            targetEmails.append(",");
                        }
                        targetEmails.append(email);
                    }
                }
                Set<User> users = targetList.getUsers();
                if (users != null) {
                    for (User user : users) {
                        String email = user.getEmail();
                        if (CommonUtil.isNullOrEmpty(email)) {
                            continue;
                        }
                        if (targetEmails.length() > 0) {
                            targetEmails.append(",");
                        }
                        targetEmails.append(email);
                    }
                }
                Set<Target> targets = targetList.getTargets();
                if (targets != null) {
                    for (Target target : targets) {
                        String email = target.getEmail();
                        if (CommonUtil.isNullOrEmpty(email)) {
                            continue;
                        }
                        if (targetEmails.length() > 0) {
                            targetEmails.append(",");
                        }
                        targetEmails.append(email);
                    }
                }
            }
        }
        String from = loginUser.getEmail();
        if (targetEmails.length() > 0) {
            String targetEmail = targetEmails.toString();
            String[] to = targetEmail.split(",");
            String mailSubject = name + " " + startDateS;
            StringBuilder content = new StringBuilder("<html><head>");
            content.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"></head><body>");
            content.append("<b>").append(getText("entity.name.label"))
                    .append("</b> : ").append(name).append("<br>");
            content.append("<b>").append(getText("entity.start_date.label"))
                    .append("</b> : ").append(startDateS).append("<br>");
            content.append("<b>").append(getText("entity.end_date.label"))
                    .append("</b> : ").append(endDateS).append("<br>");
            content.append("</body></html>");
            if (CommonUtil.isNullOrEmpty(from)) {
                from = null;
            }
            String text = content.toString();
            mailService.asynSendHtmlMail(from, to, mailSubject, text);
        }

        this.setSaveFlag(EmailSetting.STATUS_SENT);
        return SUCCESS;
    }

    /**
     * Gets the entity.
     * 
     * @return the SUCCESS result
     */
    public String get() throws Exception {
        if (this.getId() != null) {
            campaign = baseService.getEntityById(Campaign.class, this.getId());
            CampaignStatus status = campaign.getStatus();
            if (status != null) {
                statusID = status.getId();
            }
            CampaignType type = campaign.getType();
            if (type != null) {
                typeID = type.getId();
            }
            Currency currency = campaign.getCurrency();
            if (currency != null) {
                currencyID = currency.getId();
            }
            User assignedTo = campaign.getAssigned_to();
            if (assignedTo != null) {
                this.setAssignedToID(assignedTo.getId());
                this.setAssignedToText(assignedTo.getName());
            }
            Date start_date = campaign.getStart_date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    Constant.DATE_EDIT_FORMAT);
            if (start_date != null) {
                startDate = dateFormat.format(start_date);
            }
            Date end_date = campaign.getEnd_date();
            if (end_date != null) {
                endDate = dateFormat.format(end_date);
            }
            this.getBaseInfo(campaign, Campaign.class.getSimpleName(),
                    Constant.CRM_NAMESPACE);
        } else {
            this.initBaseInfo();
        }
        return SUCCESS;
    }

    /**
     * Mass update entity record information
     */
    public String massUpdate() throws Exception {
        saveEntity();
        String[] fieldNames = this.massUpdate;
        if (fieldNames != null) {
            String[] selectIDArray = this.seleteIDs.split(",");
            Collection<Campaign> campaigns = new ArrayList<Campaign>();
            User loginUser = this.getLoginUser();
            User user = userService
                    .getEntityById(User.class, loginUser.getId());
            for (String IDString : selectIDArray) {
                int id = Integer.parseInt(IDString);
                Campaign campaignInstance = this.baseService.getEntityById(
                        Campaign.class, id);
                for (String fieldName : fieldNames) {
                    Object value = BeanUtil.getFieldValue(campaign, fieldName);
                    BeanUtil.setFieldValue(campaignInstance, fieldName, value);
                }
                campaignInstance.setUpdated_by(user);
                campaignInstance.setUpdated_on(new Date());
                campaigns.add(campaignInstance);
            }
            if (campaigns.size() > 0) {
                this.baseService.batchUpdate(campaigns);
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
        if (campaign.getId() == null) {
            UserUtil.permissionCheck("create_campaign");
        } else {
            UserUtil.permissionCheck("update_campaign");
        }
        CampaignStatus status = null;
        if (statusID != null) {
            status = campaignStatusService.getEntityById(CampaignStatus.class,
                    statusID);
        }
        campaign.setStatus(status);
        CampaignType type = null;
        if (typeID != null) {
            type = campaignTypeService
                    .getEntityById(CampaignType.class, typeID);
        }
        campaign.setType(type);
        Currency currency = null;
        if (currencyID != null) {
            currency = currencyService
                    .getEntityById(Currency.class, currencyID);
        }
        campaign.setCurrency(currency);
        User assignedTo = null;
        if (this.getAssignedToID() != null) {
            assignedTo = userService.getEntityById(User.class,
                    this.getAssignedToID());
        }
        campaign.setAssigned_to(assignedTo);
        User owner = null;
        if (this.getOwnerID() != null) {
            owner = userService.getEntityById(User.class, this.getOwnerID());
        }
        campaign.setOwner(owner);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                Constant.DATE_EDIT_FORMAT);
        Date start_date = null;
        if (!CommonUtil.isNullOrEmpty(startDate)) {
            start_date = dateFormat.parse(startDate);
        }
        campaign.setStart_date(start_date);
        Date end_date = null;
        if (!CommonUtil.isNullOrEmpty(endDate)) {
            end_date = dateFormat.parse(endDate);
        }
        campaign.setEnd_date(end_date);
        super.updateBaseInfo(campaign);
    }

    /**
     * Prepares the list
     * 
     */
    public void prepare() throws Exception {
        ActionContext context = ActionContext.getContext();
        Map<String, Object> session = context.getSession();
        String local = (String) session.get("locale");
        this.statuses = campaignStatusService.getOptions(
                CampaignStatus.class.getSimpleName(), local);
        this.types = campaignTypeService.getOptions(
                CampaignType.class.getSimpleName(), local);
        this.currencies = currencyService.getAllObjects(Currency.class
                .getSimpleName());
    }

    public IBaseService<Campaign> getbaseService() {
        return baseService;
    }

    public void setbaseService(IBaseService<Campaign> baseService) {
        this.baseService = baseService;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    /**
     * @return the types
     */
    public List<CampaignType> getTypes() {
        return types;
    }

    /**
     * @return the statuses
     */
    public List<CampaignStatus> getStatuses() {
        return statuses;
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
     * @return the typeID
     */
    public Integer getTypeID() {
        return typeID;
    }

    /**
     * @param typeID
     *            the typeID to set
     */
    public void setTypeID(Integer typeID) {
        this.typeID = typeID;
    }

    /**
     * @return the currencyService
     */
    public IBaseService<Currency> getCurrencyService() {
        return currencyService;
    }

    /**
     * @param currencyService
     *            the currencyService to set
     */
    public void setCurrencyService(IBaseService<Currency> currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * @return the currencies
     */
    public List<Currency> getCurrencies() {
        return currencies;
    }

    /**
     * @param currencies
     *            the currencies to set
     */
    public void setCurrencies() {
        this.currencies = currencyService.getAllObjects(Currency.class
                .getSimpleName());
    }

    /**
     * @return the currencyID
     */
    public Integer getCurrencyID() {
        return currencyID;
    }

    /**
     * @param currencyID
     *            the currencyID to set
     */
    public void setCurrencyID(Integer currencyID) {
        this.currencyID = currencyID;
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
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @param types
     *            the types to set
     */
    public void setTypes(List<CampaignType> types) {
        this.types = types;
    }

    /**
     * @param statuses
     *            the statuses to set
     */
    public void setStatuses(List<CampaignStatus> statuses) {
        this.statuses = statuses;
    }

    /**
     * @param currencies
     *            the currencies to set
     */
    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    /**
     * @return the campaignStatusService
     */
    public IOptionService<CampaignStatus> getCampaignStatusService() {
        return campaignStatusService;
    }

    /**
     * @param campaignStatusService
     *            the campaignStatusService to set
     */
    public void setCampaignStatusService(
            IOptionService<CampaignStatus> campaignStatusService) {
        this.campaignStatusService = campaignStatusService;
    }

    /**
     * @return the campaignTypeService
     */
    public IOptionService<CampaignType> getCampaignTypeService() {
        return campaignTypeService;
    }

    /**
     * @param campaignTypeService
     *            the campaignTypeService to set
     */
    public void setCampaignTypeService(
            IOptionService<CampaignType> campaignTypeService) {
        this.campaignTypeService = campaignTypeService;
    }

    /**
     * @return the mailService
     */
    public MailService getMailService() {
        return mailService;
    }

    /**
     * @param mailService
     *            the mailService to set
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

}

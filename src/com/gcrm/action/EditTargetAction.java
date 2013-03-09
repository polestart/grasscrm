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
import com.gcrm.domain.Lead;
import com.gcrm.domain.Salutation;
import com.gcrm.domain.Target;
import com.gcrm.domain.TargetList;
import com.gcrm.domain.User;
import com.gcrm.service.IBaseService;
import com.gcrm.service.IOptionService;
import com.gcrm.service.ITargetService;
import com.gcrm.util.BeanUtil;
import com.gcrm.util.security.UserUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

/**
 * Edits Target
 * 
 */
public class EditTargetAction extends BaseEditAction implements Preparable {

    private static final long serialVersionUID = -2404576552417042445L;

    private ITargetService baseService;
    private IBaseService<Account> accountService;
    private IBaseService<Lead> leadService;
    private IBaseService<User> userService;
    private IBaseService<TargetList> targetListService;
    private IOptionService<Salutation> salutationService;
    private List<Salutation> salutations;
    private Target target;
    private Lead lead;
    private Integer accountID = null;
    private Integer salutationID = null;
    private Integer assignedToID = null;
    private String assignedToText = null;

    /**
     * Saves the entity.
     * 
     * @return the SUCCESS result
     */
    public String save() throws Exception {
        saveEntity();
        target = getBaseService().makePersistent(target);
        this.setId(target.getId());
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
            target = baseService.getEntityById(Target.class, this.getId());
            Account account = target.getAccount();
            if (account != null) {
                accountID = account.getId();
            }

            Salutation salutation = target.getSalutation();
            if (salutation != null) {
                salutationID = salutation.getId();
            }

            Integer leadID = target.getLead_id();
            if (leadID != null) {
                try {
                    lead = this.getLeadService().getEntityById(Lead.class,
                            leadID);
                } catch (Exception e) {
                    // in case the converted lead is deleted
                    lead = null;
                }
            }

            User assignedTo = target.getAssigned_to();
            if (assignedTo != null) {
                assignedToID = assignedTo.getId();
                assignedToText = assignedTo.getName();
            }
            this.getBaseInfo(target);
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
            Collection<Target> targets = new ArrayList<Target>();
            User loginUser = this.getLoginUser();
            User user = userService
                    .getEntityById(User.class, loginUser.getId());
            for (String IDString : selectIDArray) {
                int id = Integer.parseInt(IDString);
                Target targetInstance = this.baseService.getEntityById(
                        Target.class, id);
                for (String fieldName : fieldNames) {
                    Object value = BeanUtil.getFieldValue(target, fieldName);
                    BeanUtil.setFieldValue(targetInstance, fieldName, value);
                }
                targetInstance.setUpdated_by(user);
                targetInstance.setUpdated_on(new Date());
                targets.add(targetInstance);
            }
            if (targets.size() > 0) {
                this.baseService.batchUpdate(targets);
            }
        }
        return SUCCESS;
    }

    /**
     * Saves entity field
     * 
     * @throws Exception
     */
    private void saveEntity() throws Exception {
        if (target.getId() == null) {
            UserUtil.permissionCheck("create_target");
        } else {
            UserUtil.permissionCheck("update_target");
            Target originalTarget = baseService.getEntityById(Target.class,
                    target.getId());
            target.setTargetLists(originalTarget.getTargetLists());
        }

        Account account = null;
        if (accountID != null) {
            account = accountService.getEntityById(Account.class, accountID);
        }
        target.setAccount(account);

        Salutation salutation = null;
        if (salutationID != null) {
            salutation = salutationService.getEntityById(Salutation.class,
                    salutationID);
        }
        target.setSalutation(salutation);

        User assignedTo = null;
        if (assignedToID != null) {
            assignedTo = userService.getEntityById(User.class, assignedToID);
        }
        target.setAssigned_to(assignedTo);
        User owner = null;
        if (this.getOwnerID() != null) {
            owner = userService.getEntityById(User.class, this.getOwnerID());
        }
        target.setOwner(owner);

        if ("TargetList".equals(this.getRelationKey())) {
            TargetList targetList = targetListService.getEntityById(
                    TargetList.class, Integer.valueOf(this.getRelationValue()));
            Set<TargetList> targetLists = target.getTargetLists();
            if (targetLists == null) {
                targetLists = new HashSet<TargetList>();
            }
            targetLists.add(targetList);
        }
        super.updateBaseInfo(target);
    }

    /**
     * Converts the lead
     * 
     * @return the SUCCESS result
     */
    public String convert() throws Exception {

        this.getBaseService().convert(this.getTarget().getId());
        this.setSaveFlag(Target.STATUS_CONVERTED);
        return SUCCESS;
    }

    /**
     * Prepares the list
     * 
     */
    public void prepare() throws Exception {
        ActionContext context = ActionContext.getContext();
        Map<String, Object> session = context.getSession();
        String local = (String) session.get("locale");
        this.salutations = salutationService.getOptions(
                Salutation.class.getSimpleName(), local);
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
     * @return the target
     */
    public Target getTarget() {
        return target;
    }

    /**
     * @param target
     *            the target to set
     */
    public void setTarget(Target target) {
        this.target = target;
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
     * @return the assignedToID
     */
    @Override
    public Integer getAssignedToID() {
        return assignedToID;
    }

    /**
     * @param assignedToID
     *            the assignedToID to set
     */
    @Override
    public void setAssignedToID(Integer assignedToID) {
        this.assignedToID = assignedToID;
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
     * @return the assignedToText
     */
    @Override
    public String getAssignedToText() {
        return assignedToText;
    }

    /**
     * @param assignedToText
     *            the assignedToText to set
     */
    @Override
    public void setAssignedToText(String assignedToText) {
        this.assignedToText = assignedToText;
    }

    /**
     * @return the salutations
     */
    public List<Salutation> getSalutations() {
        return salutations;
    }

    /**
     * @param salutations
     *            the salutations to set
     */
    public void setSalutations(List<Salutation> salutations) {
        this.salutations = salutations;
    }

    /**
     * @return the salutationID
     */
    public Integer getSalutationID() {
        return salutationID;
    }

    /**
     * @param salutationID
     *            the salutationID to set
     */
    public void setSalutationID(Integer salutationID) {
        this.salutationID = salutationID;
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
     * @return the baseService
     */
    public ITargetService getBaseService() {
        return baseService;
    }

    /**
     * @param baseService
     *            the baseService to set
     */
    public void setBaseService(ITargetService baseService) {
        this.baseService = baseService;
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
     * @return the salutationService
     */
    public IOptionService<Salutation> getSalutationService() {
        return salutationService;
    }

    /**
     * @param salutationService
     *            the salutationService to set
     */
    public void setSalutationService(
            IOptionService<Salutation> salutationService) {
        this.salutationService = salutationService;
    }

}

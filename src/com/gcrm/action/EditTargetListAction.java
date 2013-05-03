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
import java.util.Set;

import com.gcrm.domain.Campaign;
import com.gcrm.domain.TargetList;
import com.gcrm.domain.User;
import com.gcrm.service.IBaseService;
import com.gcrm.util.BeanUtil;
import com.gcrm.util.Constant;
import com.gcrm.util.security.UserUtil;
import com.opensymphony.xwork2.Preparable;

/**
 * Edits TargetList
 * 
 */
public class EditTargetListAction extends BaseEditAction implements Preparable {

    private static final long serialVersionUID = -2404576552417042445L;

    private IBaseService<TargetList> baseService;
    private IBaseService<User> userService;
    private IBaseService<Campaign> campaignService;
    private TargetList targetList;

    /**
     * Saves the entity.
     * 
     * @return the SUCCESS result
     */
    public String save() throws Exception {
        saveEntity();
        if ("Campaign".equals(this.getRelationKey())) {
            Campaign campaign = campaignService.getEntityById(Campaign.class,
                    Integer.valueOf(this.getRelationValue()));
            Set<Campaign> campaigns = targetList.getCampaigns();
            if (campaigns == null) {
                campaigns = new HashSet<Campaign>();
            }
            campaigns.add(campaign);
        }
        targetList = getBaseService().makePersistent(targetList);
        this.setId(targetList.getId());
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
            targetList = baseService.getEntityById(TargetList.class,
                    this.getId());
            User assignedTo = targetList.getAssigned_to();
            if (assignedTo != null) {
                this.setAssignedToID(assignedTo.getId());
                this.setAssignedToText(assignedTo.getName());
            }
            this.getBaseInfo(targetList, TargetList.class.getSimpleName(),
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
            Collection<TargetList> targetLists = new ArrayList<TargetList>();
            User loginUser = this.getLoginUser();
            User user = userService
                    .getEntityById(User.class, loginUser.getId());
            for (String IDString : selectIDArray) {
                int id = Integer.parseInt(IDString);
                TargetList targetListInstance = this.baseService.getEntityById(
                        TargetList.class, id);
                for (String fieldName : fieldNames) {
                    Object value = BeanUtil
                            .getFieldValue(targetList, fieldName);
                    BeanUtil.setFieldValue(targetListInstance, fieldName, value);
                }
                targetListInstance.setUpdated_by(user);
                targetListInstance.setUpdated_on(new Date());
                targetLists.add(targetListInstance);
            }
            if (targetLists.size() > 0) {
                this.baseService.batchUpdate(targetLists);
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
        if (targetList.getId() == null) {
            UserUtil.permissionCheck("create_targetList");
        } else {
            UserUtil.permissionCheck("update_targetList");
            TargetList originalTargetList = baseService.getEntityById(
                    TargetList.class, targetList.getId());
            targetList.setTargets(originalTargetList.getTargets());
            targetList.setContacts(originalTargetList.getContacts());
            targetList.setLeads(originalTargetList.getLeads());
            targetList.setUsers(originalTargetList.getUsers());
            targetList.setAccounts(originalTargetList.getAccounts());
        }

        User assignedTo = null;
        if (this.getAssignedToID() != null) {
            assignedTo = userService.getEntityById(User.class,
                    this.getAssignedToID());
        }
        targetList.setAssigned_to(assignedTo);

        User owner = null;
        if (this.getOwnerID() != null) {
            owner = userService.getEntityById(User.class, this.getOwnerID());
        }
        targetList.setOwner(owner);

        super.updateBaseInfo(targetList);
    }

    /**
     * Prepares the list
     * 
     */
    public void prepare() throws Exception {
    }

    /**
     * @return the baseService
     */
    public IBaseService<TargetList> getBaseService() {
        return baseService;
    }

    /**
     * @param baseService
     *            the baseService to set
     */
    public void setBaseService(IBaseService<TargetList> baseService) {
        this.baseService = baseService;
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
     * @return the targetList
     */
    public TargetList getTargetList() {
        return targetList;
    }

    /**
     * @param targetList
     *            the targetList to set
     */
    public void setTargetList(TargetList targetList) {
        this.targetList = targetList;
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

}

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
import java.util.List;

import com.gcrm.domain.TargetList;
import com.gcrm.domain.TargetListType;
import com.gcrm.domain.User;
import com.gcrm.service.IBaseService;
import com.gcrm.util.BeanUtil;
import com.gcrm.util.security.UserUtil;
import com.opensymphony.xwork2.Preparable;

/**
 * Edits TargetList
 * 
 */
public class EditTargetListAction extends BaseEditAction implements Preparable {

    private static final long serialVersionUID = -2404576552417042445L;

    private IBaseService<TargetList> baseService;
    private IBaseService<TargetListType> targetListTypeService;
    private IBaseService<User> userService;
    private TargetList targetList;
    private List<TargetListType> targetListTypes;
    private Integer typeID = null;
    private Integer assignedToID = null;
    private String assignedToText = null;

    /**
     * Saves the entity.
     * 
     * @return the SUCCESS result
     */
    public String save() throws Exception {
        saveEntity();
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
            TargetListType type = targetList.getType();
            if (type != null) {
                typeID = type.getId();
            }

            User assignedTo = targetList.getAssigned_to();
            if (assignedTo != null) {
                assignedToID = assignedTo.getId();
                assignedToText = assignedTo.getName();
            }
            this.getBaseInfo(targetList);
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

        TargetListType type = null;
        if (typeID != null) {
            type = targetListTypeService.getEntityById(TargetListType.class,
                    typeID);
        }
        targetList.setType(type);

        User assignedTo = null;
        if (assignedToID != null) {
            assignedTo = userService.getEntityById(User.class, assignedToID);
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
        this.targetListTypes = targetListTypeService
                .getAllObjects(TargetListType.class.getSimpleName());
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
     * @return the targetListTypeService
     */
    public IBaseService<TargetListType> getTargetListTypeService() {
        return targetListTypeService;
    }

    /**
     * @param targetListTypeService
     *            the targetListTypeService to set
     */
    public void setTargetListTypeService(
            IBaseService<TargetListType> targetListTypeService) {
        this.targetListTypeService = targetListTypeService;
    }

    /**
     * @return the targetListTypes
     */
    public List<TargetListType> getTargetListTypes() {
        return targetListTypes;
    }

    /**
     * @param targetListTypes
     *            the targetListTypes to set
     */
    public void setTargetListTypes(List<TargetListType> targetListTypes) {
        this.targetListTypes = targetListTypes;
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

}

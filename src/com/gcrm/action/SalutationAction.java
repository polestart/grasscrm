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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.gcrm.domain.Salutation;
import com.gcrm.service.IBaseService;
import com.gcrm.util.security.UserUtil;
import com.gcrm.vo.SearchCondition;
import com.gcrm.vo.SearchResult;

/**
 * Manages the Salutation dropdown list
 * 
 */
public class SalutationAction extends BaseListAction {

    private static final long serialVersionUID = -2404576552417042445L;

    private IBaseService<Salutation> baseService;
    private Salutation salutation;

    private static final String CLAZZ = Salutation.class.getSimpleName();

    /**
     * Gets the list JSON data.
     * 
     * @return list JSON data
     */
    @Override
    public String list() throws Exception {
        UserUtil.permissionCheck("view_system");
        SearchCondition searchCondition = getSearchCondition();
        SearchResult<Salutation> result = baseService.getPaginationObjects(
                CLAZZ, searchCondition);
        List<Salutation> salutations = result.getResult();

        long totalRecords = result.getTotalRecords();

        // Constructs the JSON data
        String json = "{\"total\": " + totalRecords + ",\"rows\": [";
        int size = salutations.size();
        for (int i = 0; i < size; i++) {
            Salutation instance = salutations.get(i);
            Integer id = instance.getId();
            String name = instance.getName();
            int sequence = instance.getSequence();

            json += "{\"id\":\"" + id + "\",\"salutation.id\":\"" + id
                    + "\",\"salutation.name\":\"" + name
                    + "\",\"salutation.sequence\":\"" + sequence + "\"}";
            if (i < size - 1) {
                json += ",";
            }
        }
        json += "]}";

        // Returns JSON data back to page
        HttpServletResponse response = ServletActionContext.getResponse();
        response.getWriter().write(json);
        return null;
    }

    /**
     * Saves the entity.
     * 
     * @return the SUCCESS result
     */
    public String save() throws Exception {
        if (salutation.getId() == null) {
            UserUtil.permissionCheck("create_system");
        } else {
            UserUtil.permissionCheck("update_system");
        }
        getbaseService().makePersistent(salutation);
        return SUCCESS;
    }

    /**
     * Deletes the selected entity.
     * 
     * @return the SUCCESS result
     */
    public String delete() throws Exception {
        UserUtil.permissionCheck("delete_system");
        baseService.batchDeleteEntity(Salutation.class, this.getSeleteIDs());
        return SUCCESS;
    }

    public IBaseService<Salutation> getbaseService() {
        return baseService;
    }

    public void setbaseService(IBaseService<Salutation> baseService) {
        this.baseService = baseService;
    }

    public Salutation getSalutation() {
        return salutation;
    }

    public void setSalutation(Salutation salutation) {
        this.salutation = salutation;
    }

}

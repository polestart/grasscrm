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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.gcrm.domain.Call;
import com.gcrm.domain.Meeting;
import com.gcrm.domain.TargetList;
import com.gcrm.domain.User;
import com.gcrm.domain.UserStatus;
import com.gcrm.exception.ServiceException;
import com.gcrm.service.IBaseService;
import com.gcrm.util.CommonUtil;
import com.gcrm.util.Constant;
import com.gcrm.vo.SearchCondition;
import com.gcrm.vo.SearchResult;

/**
 * Lists User
 * 
 */
public class ListUserAction extends BaseListAction {

    private static final long serialVersionUID = -2404576552417042445L;

    private IBaseService<User> baseService;
    private IBaseService<UserStatus> userStatusService;
    private IBaseService<TargetList> targetListService;
    private IBaseService<Call> callService;
    private IBaseService<Meeting> meetingService;
    private User user;

    private static final String CLAZZ = User.class.getSimpleName();

    /**
     * Gets the list data.
     * 
     * @return null
     */
    @Override
    public String list() throws Exception {

        SearchCondition searchCondition = getSearchCondition();
        SearchResult<User> result = baseService.getPaginationObjects(CLAZZ,
                searchCondition);

        Iterator<User> users = result.getResult().iterator();
        long totalRecords = result.getTotalRecords();
        getListJson(users, totalRecords, null, false);
        return null;
    }

    /**
     * Gets the list data.
     * 
     * @return null
     */
    public String listFull() throws Exception {

        Map<String, String> fieldTypeMap = new HashMap<String, String>();
        fieldTypeMap.put("created_on", Constant.DATA_TYPE_DATETIME);
        fieldTypeMap.put("updated_on", Constant.DATA_TYPE_DATETIME);

        SearchCondition searchCondition = getSearchCondition(fieldTypeMap);
        SearchResult<User> result = baseService.getPaginationObjects(CLAZZ,
                searchCondition);

        Iterator<User> users = result.getResult().iterator();
        long totalRecords = result.getTotalRecords();
        getListJson(users, totalRecords, searchCondition, true);
        return null;
    }

    /**
     * Gets the list JSON data.
     * 
     * @return list JSON data
     */
    public static void getListJson(Iterator<User> users, long totalRecords,
            SearchCondition searchCondition, boolean isList) throws Exception {

        StringBuilder jsonBuilder = new StringBuilder("");
        jsonBuilder
                .append(getJsonHeader(totalRecords, searchCondition, isList));

        while (users.hasNext()) {
            User instance = users.next();
            int id = instance.getId();
            String name = CommonUtil.fromNullToEmpty(instance.getName());
            String title = CommonUtil.fromNullToEmpty(instance.getTitle());
            String department = CommonUtil.fromNullToEmpty(instance
                    .getDepartment());
            UserStatus status = instance.getStatus();
            String statusName = "";
            if (status != null && status.getName() != null) {
                statusName = status.getName();
            }

            if (isList) {
                User createdBy = instance.getCreated_by();
                String createdByName = "";
                if (createdBy != null) {
                    createdByName = CommonUtil.fromNullToEmpty(createdBy
                            .getName());
                }
                User updatedBy = instance.getUpdated_by();
                String updatedByName = "";
                if (updatedBy != null) {
                    updatedByName = CommonUtil.fromNullToEmpty(updatedBy
                            .getName());
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        Constant.DATE_TIME_FORMAT);
                Date createdOn = instance.getCreated_on();
                String createdOnName = "";
                if (createdOn != null) {
                    createdOnName = dateFormat.format(createdOn);
                }
                Date updatedOn = instance.getUpdated_on();
                String updatedOnName = "";
                if (updatedOn != null) {
                    updatedOnName = dateFormat.format(updatedOn);
                }
                jsonBuilder.append("{\"cell\":[\"").append(id).append("\",\"")
                        .append(name).append("\",\"").append(title)
                        .append("\",\"").append(department).append("\",\"")
                        .append(statusName).append("\",\"")
                        .append(createdByName).append("\",\"")
                        .append(updatedByName).append("\",\"")
                        .append(createdOnName).append("\",\"")
                        .append(updatedOnName).append("\"]}");
            } else {
                jsonBuilder.append("{\"id\":\"").append(id)
                        .append("\",\"name\":\"").append(name)
                        .append("\",\"title\":\"").append(title)
                        .append("\",\"department\":\"").append(department)
                        .append("\",\"status\":\"").append(statusName)
                        .append("\"}");
            }
            if (users.hasNext()) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]}");

        // Returns JSON data back to page
        HttpServletResponse response = ServletActionContext.getResponse();
        response.getWriter().write(jsonBuilder.toString());
    }

    /**
     * Selects the entities
     * 
     * @return the SUCCESS result
     */
    public String select() throws ServiceException {
        TargetList targetList = null;
        Call call = null;
        Meeting meeting = null;
        Set<User> users = null;

        if ("TargetList".equals(this.getRelationKey())) {
            targetList = targetListService.getEntityById(TargetList.class,
                    Integer.valueOf(this.getRelationValue()));
            users = targetList.getUsers();
        } else if ("Call".equals(this.getRelationKey())) {
            call = callService.getEntityById(Call.class,
                    Integer.valueOf(this.getRelationValue()));
            users = call.getUsers();
        } else if ("Meeting".equals(this.getRelationKey())) {
            meeting = meetingService.getEntityById(Meeting.class,
                    Integer.valueOf(this.getRelationValue()));
            users = meeting.getUsers();
        }

        if (this.getSeleteIDs() != null) {
            String[] ids = seleteIDs.split(",");
            for (int i = 0; i < ids.length; i++) {
                String selectId = ids[i];
                user = baseService.getEntityById(User.class,
                        Integer.valueOf(selectId));
                users.add(user);
            }
        }

        if ("TargetList".equals(this.getRelationKey())) {
            targetListService.makePersistent(targetList);
        } else if ("Call".equals(this.getRelationKey())) {
            callService.makePersistent(call);
        } else if ("Meeting".equals(this.getRelationKey())) {
            meetingService.makePersistent(meeting);
        }
        return SUCCESS;
    }

    /**
     * Unselects the entities
     * 
     * @return the SUCCESS result
     */
    public String unselect() throws ServiceException {
        TargetList targetList = null;
        Call call = null;
        Meeting meeting = null;
        Set<User> users = null;

        if ("TargetList".equals(this.getRelationKey())) {
            targetList = targetListService.getEntityById(TargetList.class,
                    Integer.valueOf(this.getRelationValue()));
            users = targetList.getUsers();
        } else if ("Call".equals(this.getRelationKey())) {
            call = callService.getEntityById(Call.class,
                    Integer.valueOf(this.getRelationValue()));
            users = call.getUsers();
        } else if ("Meeting".equals(this.getRelationKey())) {
            meeting = meetingService.getEntityById(Meeting.class,
                    Integer.valueOf(this.getRelationValue()));
            users = meeting.getUsers();
        }

        if (this.getSeleteIDs() != null) {
            String[] ids = seleteIDs.split(",");
            Collection<User> selectedUsers = new ArrayList<User>();
            for (int i = 0; i < ids.length; i++) {
                Integer selectId = Integer.valueOf(ids[i]);
                A: for (User user : users) {
                    if (user.getId().intValue() == selectId.intValue()) {
                        selectedUsers.add(user);
                        break A;
                    }
                }
            }
            users.removeAll(selectedUsers);
        }

        if ("TargetList".equals(this.getRelationKey())) {
            targetListService.makePersistent(targetList);
        } else if ("Call".equals(this.getRelationKey())) {
            callService.makePersistent(call);
        } else if ("Meeting".equals(this.getRelationKey())) {
            meetingService.makePersistent(meeting);
        }
        return SUCCESS;
    }

    /**
     * Deletes the selected entities.
     * 
     * @return the SUCCESS result
     */
    public String delete() throws ServiceException {
        baseService.batchDeleteEntity(User.class, this.getSeleteIDs());
        return SUCCESS;
    }

    /**
     * Copies the selected entities
     * 
     * @return the SUCCESS result
     */
    public String copy() throws ServiceException {
        if (this.getSeleteIDs() != null) {
            String[] ids = seleteIDs.split(",");
            for (int i = 0; i < ids.length; i++) {
                String copyid = ids[i];
                User oriRecord = baseService.getEntityById(User.class,
                        Integer.valueOf(copyid));
                User targetRecord = oriRecord.clone();
                targetRecord.setId(null);
                this.baseService.makePersistent(targetRecord);
            }
        }
        return SUCCESS;
    }

    /**
     * Exports the entities
     * 
     * @return the exported entities inputStream
     */
    public InputStream getInputStream() throws Exception {
        File file = new File(CLAZZ + ".csv");
        ICsvMapWriter writer = new CsvMapWriter(new FileWriter(file),
                CsvPreference.EXCEL_PREFERENCE);
        try {
            final String[] header = new String[] { "ID", "User Name",
                    "First Name", "Last Name", "Status ID", "Status Name",
                    "Title", "Email", "Mobile", "Phone", "Fax", "Department",
                    "Report To ID", "Report To Name", "Mailing Street",
                    "Mailing City", "Mailing State", "Mailing Postal Code",
                    "Mailing Country", "Other Street", "Other City",
                    "Other State", "Other Postal Code", "Other Country", "Age",
                    "Smtp Username", "Smtp Password", "Description" };
            writer.writeHeader(header);
            String[] ids = seleteIDs.split(",");
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                User user = baseService.getEntityById(User.class,
                        Integer.parseInt(id));
                final HashMap<String, ? super Object> data1 = new HashMap<String, Object>();
                data1.put(header[0], user.getId());
                data1.put(header[1], CommonUtil.fromNullToEmpty(user.getName()));
                data1.put(header[2],
                        CommonUtil.fromNullToEmpty(user.getFirst_name()));
                data1.put(header[3],
                        CommonUtil.fromNullToEmpty(user.getLast_name()));
                if (user.getStatus() != null) {
                    data1.put(header[4], user.getStatus().getId());
                    data1.put(header[5], user.getStatus().getName());
                } else {
                    data1.put(header[4], "");
                    data1.put(header[5], "");
                }
                data1.put(header[6],
                        CommonUtil.fromNullToEmpty(user.getTitle()));
                data1.put(header[7],
                        CommonUtil.fromNullToEmpty(user.getEmail()));
                data1.put(header[8],
                        CommonUtil.fromNullToEmpty(user.getMobile()));
                data1.put(header[9],
                        CommonUtil.fromNullToEmpty(user.getPhone()));
                data1.put(header[10], CommonUtil.fromNullToEmpty(user.getFax()));
                data1.put(header[11],
                        CommonUtil.fromNullToEmpty(user.getDepartment()));
                if (user.getReport_to() != null) {
                    data1.put(header[12], user.getReport_to().getId());
                    data1.put(header[13], user.getReport_to().getName());
                } else {
                    data1.put(header[12], "");
                    data1.put(header[13], "");
                }
                data1.put(header[14],
                        CommonUtil.fromNullToEmpty(user.getMail_street()));
                data1.put(header[15],
                        CommonUtil.fromNullToEmpty(user.getMail_city()));
                data1.put(header[16],
                        CommonUtil.fromNullToEmpty(user.getMail_state()));
                data1.put(header[17],
                        CommonUtil.fromNullToEmpty(user.getMail_postal_code()));
                data1.put(header[18],
                        CommonUtil.fromNullToEmpty(user.getMail_country()));
                data1.put(header[19],
                        CommonUtil.fromNullToEmpty(user.getOther_street()));
                data1.put(header[20],
                        CommonUtil.fromNullToEmpty(user.getOther_city()));
                data1.put(header[21],
                        CommonUtil.fromNullToEmpty(user.getOther_state()));
                data1.put(header[22],
                        CommonUtil.fromNullToEmpty(user.getOther_postal_code()));
                data1.put(header[23],
                        CommonUtil.fromNullToEmpty(user.getOther_country()));
                data1.put(header[24], user.getAge());
                data1.put(header[25],
                        CommonUtil.fromNullToEmpty(user.getSmtp_username()));
                data1.put(header[26],
                        CommonUtil.fromNullToEmpty(user.getSmtp_password()));
                data1.put(header[27],
                        CommonUtil.fromNullToEmpty(user.getDescription()));
                writer.write(data1, header);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            writer.close();
        }

        InputStream in = new FileInputStream(file);
        this.setFileName(CLAZZ + ".csv");
        return in;
    }

    /**
     * Imports the entities
     * 
     * @return the SUCCESS result
     */
    public String importCSV() throws Exception {
        File file = this.getUpload();
        CsvListReader reader = new CsvListReader(new FileReader(file),
                CsvPreference.EXCEL_PREFERENCE);
        int failedNum = 0;
        int successfulNum = 0;
        try {
            final String[] header = reader.getCSVHeader(true);

            List<String> line = new ArrayList<String>();
            Map<String, String> failedMsg = new HashMap<String, String>();
            while ((line = reader.read()) != null) {

                Map<String, String> row = new HashMap<String, String>();
                for (int i = 0; i < line.size(); i++) {
                    row.put(header[i], line.get(i));
                }

                User user = new User();
                try {
                    String id = row.get("ID");
                    if (!CommonUtil.isNullOrEmpty(id)) {
                        user.setId(Integer.parseInt(id));
                    }
                    user.setName(CommonUtil.fromNullToEmpty(row
                            .get("User Name")));
                    user.setFirst_name(CommonUtil.fromNullToEmpty(row
                            .get("First Name")));
                    user.setLast_name(CommonUtil.fromNullToEmpty(row
                            .get("Last Name")));
                    String statusID = row.get("Status ID");
                    if (CommonUtil.isNullOrEmpty(statusID)) {
                        user.setStatus(null);
                    } else {
                        UserStatus userStatus = userStatusService
                                .getEntityById(UserStatus.class,
                                        Integer.parseInt(statusID));
                        user.setStatus(userStatus);
                    }
                    user.setTitle(CommonUtil.fromNullToEmpty(row.get("Title")));
                    user.setEmail(CommonUtil.fromNullToEmpty(row.get("Email")));
                    user.setMobile(CommonUtil.fromNullToEmpty(row.get("Mobile")));
                    user.setPhone(CommonUtil.fromNullToEmpty(row.get("Phone")));
                    user.setFax(CommonUtil.fromNullToEmpty(row.get("Fax")));
                    user.setDepartment(CommonUtil.fromNullToEmpty(row
                            .get("Department")));
                    String reportToID = row.get("Report To ID");
                    if (CommonUtil.isNullOrEmpty(reportToID)) {
                        user.setReport_to(null);
                    } else {
                        User reportTo = baseService.getEntityById(User.class,
                                Integer.parseInt(reportToID));
                        user.setReport_to(reportTo);
                    }
                    user.setMail_street(CommonUtil.fromNullToEmpty(row
                            .get("Mailing Street")));
                    user.setMail_city(CommonUtil.fromNullToEmpty(row
                            .get("Mailing City")));
                    user.setMail_state(CommonUtil.fromNullToEmpty(row
                            .get("Mailing State")));
                    user.setMail_postal_code(CommonUtil.fromNullToEmpty(row
                            .get("Mailing Postal Code")));
                    user.setMail_country(CommonUtil.fromNullToEmpty(row
                            .get("Mailing Country")));
                    user.setOther_street(CommonUtil.fromNullToEmpty(row
                            .get("Other Street")));
                    user.setOther_city(CommonUtil.fromNullToEmpty(row
                            .get("Other City")));
                    user.setOther_state(CommonUtil.fromNullToEmpty(row
                            .get("Other State")));
                    user.setOther_postal_code(CommonUtil.fromNullToEmpty(row
                            .get("Other Postal Code")));
                    user.setOther_country(CommonUtil.fromNullToEmpty(row
                            .get("Other Country")));
                    String age = row.get("Age");
                    if (CommonUtil.isNullOrEmpty(age)) {
                        user.setAge(0);
                    } else {
                        user.setAge(Integer.parseInt(age));
                    }
                    user.setSmtp_username(CommonUtil.fromNullToEmpty(row
                            .get("Smtp Username")));
                    user.setSmtp_password(CommonUtil.fromNullToEmpty(row
                            .get("Smtp Password")));
                    user.setDescription(CommonUtil.fromNullToEmpty(row
                            .get("Description")));
                    baseService.makePersistent(user);
                    successfulNum++;
                } catch (Exception e) {
                    failedNum++;
                    failedMsg.put(user.getName(), e.getMessage());
                }

            }

            this.setFailedMsg(failedMsg);
            this.setFailedNum(failedNum);
            this.setSuccessfulNum(successfulNum);
            this.setTotalNum(successfulNum + failedNum);
        } finally {
            reader.close();
        }
        return SUCCESS;
    }

    @Override
    public String execute() throws Exception {
        return SUCCESS;
    }

    /**
     * @return the baseService
     */
    public IBaseService<User> getBaseService() {
        return baseService;
    }

    /**
     * @param baseService
     *            the baseService to set
     */
    public void setBaseService(IBaseService<User> baseService) {
        this.baseService = baseService;
    }

    /**
     * @return the userStatusService
     */
    public IBaseService<UserStatus> getUserStatusService() {
        return userStatusService;
    }

    /**
     * @param userStatusService
     *            the userStatusService to set
     */
    public void setUserStatusService(IBaseService<UserStatus> userStatusService) {
        this.userStatusService = userStatusService;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the id
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    @Override
    public void setId(Integer id) {
        this.id = id;
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

}

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
import com.gcrm.domain.CallDirection;
import com.gcrm.domain.CallStatus;
import com.gcrm.domain.Contact;
import com.gcrm.domain.Lead;
import com.gcrm.domain.ReminderOption;
import com.gcrm.domain.User;
import com.gcrm.service.IBaseService;
import com.gcrm.util.CommonUtil;
import com.gcrm.util.Constant;
import com.gcrm.util.security.UserUtil;
import com.gcrm.vo.SearchCondition;
import com.gcrm.vo.SearchResult;

/**
 * Lists Call
 * 
 */
public class ListCallAction extends BaseListAction {

    private static final long serialVersionUID = -2404576552417042445L;

    private IBaseService<Call> baseService;
    private IBaseService<CallDirection> callDirectionService;
    private IBaseService<CallStatus> callStatusService;
    private IBaseService<ReminderOption> reminderOptionService;
    private IBaseService<User> userService;
    private Call call;

    private static final String CLAZZ = Call.class.getSimpleName();

    /**
     * Gets the list data.
     * 
     * @return null
     */
    @Override
    public String list() throws Exception {

        SearchCondition searchCondition = getSearchCondition();
        SearchResult<Call> result = baseService.getPaginationObjects(CLAZZ,
                searchCondition);
        Iterator<Call> calls = result.getResult().iterator();
        long totalRecords = result.getTotalRecords();
        getListJson(calls, totalRecords, null, false);
        return null;
    }

    /**
     * Gets the list data.
     * 
     * @return null
     */
    public String listFull() throws Exception {
        UserUtil.permissionCheck("view_call");
        Map<String, String> fieldTypeMap = new HashMap<String, String>();
        fieldTypeMap.put("start_date", Constant.DATA_TYPE_DATETIME);
        fieldTypeMap.put("created_on", Constant.DATA_TYPE_DATETIME);
        fieldTypeMap.put("updated_on", Constant.DATA_TYPE_DATETIME);

        User loginUser = UserUtil.getLoginUser();
        SearchCondition searchCondition = getSearchCondition(fieldTypeMap,
                loginUser.getScope_call(), loginUser);
        SearchResult<Call> result = baseService.getPaginationObjects(CLAZZ,
                searchCondition);
        Iterator<Call> calls = result.getResult().iterator();
        long totalRecords = result.getTotalRecords();
        getListJson(calls, totalRecords, searchCondition, true);
        return null;
    }

    /**
     * Gets the list JSON data.
     * 
     * @return list JSON data
     */
    public static void getListJson(Iterator<Call> calls, long totalRecords,
            SearchCondition searchCondition, boolean isList) throws Exception {

        StringBuilder jsonBuilder = new StringBuilder("");
        jsonBuilder
                .append(getJsonHeader(totalRecords, searchCondition, isList));

        String statusName = null;
        String directionName = null;
        String assignedTo = null;
        while (calls.hasNext()) {
            Call instance = calls.next();
            int id = instance.getId();
            CallDirection direction = instance.getDirection();
            if (direction != null) {
                directionName = CommonUtil.fromNullToEmpty(direction.getName());
            } else {
                directionName = "";
            }
            String subject = CommonUtil.fromNullToEmpty(instance.getSubject());
            CallStatus status = instance.getStatus();
            if (status != null) {
                statusName = CommonUtil.fromNullToEmpty(status.getName());
            } else {
                statusName = "";
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    Constant.DATE_TIME_FORMAT);
            Date startDate = instance.getStart_date();
            String startDateString = "";
            if (startDate != null) {
                startDateString = dateFormat.format(startDate);
            }

            if (isList) {
                User user = instance.getAssigned_to();
                if (user != null) {
                    assignedTo = CommonUtil.fromNullToEmpty(user.getName());
                } else {
                    assignedTo = "";
                }
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
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
                        Constant.DATE_TIME_FORMAT);
                Date createdOn = instance.getCreated_on();
                String createdOnString = "";
                if (createdOn != null) {
                    createdOnString = dateTimeFormat.format(createdOn);
                }
                Date updatedOn = instance.getUpdated_on();
                String updatedOnString = "";
                if (updatedOn != null) {
                    updatedOnString = dateTimeFormat.format(updatedOn);
                }

                jsonBuilder.append("{\"cell\":[\"").append(id).append("\",\"")
                        .append(directionName).append("\",\"").append(subject)
                        .append("\",\"").append(statusName).append("\",\"")
                        .append(startDateString).append("\",\"")
                        .append(assignedTo).append("\",\"")
                        .append(createdByName).append("\",\"")
                        .append(updatedByName).append("\",\"")
                        .append(createdOnString).append("\",\"")
                        .append(updatedOnString).append("\"]}");
            } else {
                jsonBuilder.append("{\"id\":\"").append(id)
                        .append("\",\"direction\":\"").append(directionName)
                        .append("\",\"subject\":\"").append(subject)
                        .append("\",\"statusName\":\"").append(statusName)
                        .append("\",\"start_date\":\"").append(startDateString)
                        .append("\"}");
            }
            if (calls.hasNext()) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]}");

        // Returns JSON data back to page
        HttpServletResponse response = ServletActionContext.getResponse();
        response.getWriter().write(jsonBuilder.toString());
    }

    /**
     * Gets the related leads.
     * 
     * @return null
     */
    public String filterCallLead() throws Exception {
        call = baseService.getEntityById(Call.class, id);
        Set<Lead> leads = call.getLeads();
        Iterator<Lead> leadIterator = leads.iterator();
        long totalRecords = leads.size();
        ListLeadAction.getListJson(leadIterator, totalRecords, null, false);
        return null;
    }

    /**
     * Gets the related contacts.
     * 
     * @return null
     */
    public String filterCallContact() throws Exception {
        call = baseService.getEntityById(Call.class, id);
        Set<Contact> contacts = call.getContacts();
        Iterator<Contact> contactIterator = contacts.iterator();
        long totalRecords = contacts.size();
        ListContactAction.getListJson(contactIterator, totalRecords, null,
                false);
        return null;
    }

    /**
     * Gets the related users.
     * 
     * @return null
     */
    public String filterCallUser() throws Exception {
        call = baseService.getEntityById(Call.class, id);
        Set<User> users = call.getUsers();
        Iterator<User> userIterator = users.iterator();
        int totalRecords = users.size();
        ListUserAction.getListJson(userIterator, totalRecords, null, false);
        return null;
    }

    /**
     * Deletes the selected entities.
     * 
     * @return the SUCCESS result
     */
    public String delete() throws Exception {
        UserUtil.permissionCheck("delete_call");
        baseService.batchDeleteEntity(Call.class, this.getSeleteIDs());
        return SUCCESS;
    }

    /**
     * Copies the selected entities
     * 
     * @return the SUCCESS result
     */
    public String copy() throws Exception {
        UserUtil.permissionCheck("create_call");
        if (this.getSeleteIDs() != null) {
            String[] ids = seleteIDs.split(",");
            for (int i = 0; i < ids.length; i++) {
                String copyid = ids[i];
                Call oriRecord = baseService.getEntityById(Call.class,
                        Integer.valueOf(copyid));
                Call targetRecord = oriRecord.clone();
                targetRecord.setId(null);
                this.getbaseService().makePersistent(targetRecord);
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
        UserUtil.permissionCheck("view_call");
        String fileName = getText("entity.call.label") + ".csv";
        fileName = new String(fileName.getBytes(), "ISO8859-1");
        File file = new File(fileName);
        ICsvMapWriter writer = new CsvMapWriter(new FileWriter(file),
                CsvPreference.EXCEL_PREFERENCE);
        try {
            final String[] header = new String[] { getText("entity.id.label"),
                    getText("entity.subject.label"),
                    getText("call.direction_id.label"),
                    getText("call.direction_name.label"),
                    getText("entity.status_id.label"),
                    getText("entity.status_name.label"),
                    getText("entity.start_date.label"),
                    getText("entity.reminder_pop.label"),
                    getText("entity.reminder_option_pop_id.label"),
                    getText("entity.reminder_option_pop_name.label"),
                    getText("entity.reminder_email.label"),
                    getText("entity.reminder_option_email_id.label"),
                    getText("entity.reminder_option_email_name.label"),
                    getText("entity.description.label"),
                    getText("entity.notes.label"),
                    getText("entity.assigned_to_id.label"),
                    getText("entity.assigned_to_name.label") };
            writer.writeHeader(header);
            String[] ids = seleteIDs.split(",");
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                Call call = baseService.getEntityById(Call.class,
                        Integer.parseInt(id));
                final HashMap<String, ? super Object> data1 = new HashMap<String, Object>();
                data1.put(header[0], call.getId());
                data1.put(header[1],
                        CommonUtil.fromNullToEmpty(call.getSubject()));
                if (call.getDirection() != null) {
                    data1.put(header[2], call.getDirection().getId());
                    data1.put(header[3], call.getDirection().getName());
                } else {
                    data1.put(header[2], "");
                    data1.put(header[3], "");
                }
                if (call.getStatus() != null) {
                    data1.put(header[4], call.getStatus().getId());
                    data1.put(header[5], call.getStatus().getName());
                } else {
                    data1.put(header[4], "");
                    data1.put(header[5], "");
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        Constant.DATE_TIME_FORMAT);
                Date startDate = call.getStart_date();
                if (startDate != null) {
                    data1.put(header[6], dateFormat.format(startDate));
                } else {
                    data1.put(header[6], "");
                }
                data1.put(header[7], call.isReminder_pop());
                if (call.getReminder_option_pop() != null) {
                    data1.put(header[8], call.getReminder_option_pop().getId());
                    data1.put(header[9], call.getReminder_option_pop()
                            .getName());
                } else {
                    data1.put(header[8], "");
                    data1.put(header[9], "");
                }
                data1.put(header[10], call.isReminder_email());
                if (call.getReminder_option_email() != null) {
                    data1.put(header[11], call.getReminder_option_email()
                            .getId());
                    data1.put(header[12], call.getReminder_option_email()
                            .getName());
                } else {
                    data1.put(header[11], "");
                    data1.put(header[12], "");
                }
                data1.put(header[13],
                        CommonUtil.fromNullToEmpty(call.getDescription()));
                data1.put(header[14],
                        CommonUtil.fromNullToEmpty(call.getNotes()));
                if (call.getAssigned_to() != null) {
                    data1.put(header[15], call.getAssigned_to().getId());
                    data1.put(header[16], call.getAssigned_to().getName());
                } else {
                    data1.put(header[15], "");
                    data1.put(header[16], "");
                }
                writer.write(data1, header);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            writer.close();
        }

        InputStream in = new FileInputStream(file);
        this.setFileName(fileName);
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

                Call call = new Call();
                try {
                    String id = row.get(getText("entity.id.label"));
                    if (!CommonUtil.isNullOrEmpty(id)) {
                        call.setId(Integer.parseInt(id));
                        UserUtil.permissionCheck("update_call");
                    } else {
                        UserUtil.permissionCheck("create_call");
                    }
                    call.setSubject(CommonUtil.fromNullToEmpty(row
                            .get(getText("entity.subject.label"))));
                    String directionID = row
                            .get(getText("call.direction_id.label"));
                    if (CommonUtil.isNullOrEmpty(directionID)) {
                        call.setDirection(null);
                    } else {
                        CallDirection callDirection = callDirectionService
                                .getEntityById(CallDirection.class,
                                        Integer.parseInt(directionID));
                        call.setDirection(callDirection);
                    }
                    String statusID = row
                            .get(getText("entity.status_id.label"));
                    if (CommonUtil.isNullOrEmpty(statusID)) {
                        call.setStatus(null);
                    } else {
                        CallStatus status = callStatusService.getEntityById(
                                CallStatus.class, Integer.parseInt(statusID));
                        call.setStatus(status);
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            Constant.DATE_TIME_FORMAT);
                    String startDateS = row
                            .get(getText("entity.start_date.label"));
                    if (startDateS != null) {
                        Date startDate = dateFormat.parse(startDateS);
                        call.setStart_date(startDate);
                    } else {
                        call.setStart_date(null);
                    }
                    String reminderWayPop = row
                            .get(getText("entity.reminder_pop.label"));
                    if (CommonUtil.isNullOrEmpty(reminderWayPop)) {
                        call.setReminder_pop(false);
                    } else {
                        call.setReminder_pop(Boolean
                                .parseBoolean(reminderWayPop));
                    }
                    String reminderOptionPopID = row
                            .get(getText("entity.reminder_option_pop_id.label"));
                    if (CommonUtil.isNullOrEmpty(reminderOptionPopID)) {
                        call.setReminder_option_pop(null);
                    } else {
                        ReminderOption reminderOption = reminderOptionService
                                .getEntityById(ReminderOption.class,
                                        Integer.parseInt(reminderOptionPopID));
                        call.setReminder_option_pop(reminderOption);
                    }
                    String reminderWayEmail = row
                            .get(getText("entity.reminder_email.label"));
                    if (CommonUtil.isNullOrEmpty(reminderWayEmail)) {
                        call.setReminder_email(false);
                    } else {
                        call.setReminder_email(Boolean
                                .parseBoolean(reminderWayEmail));
                    }
                    String reminderOptionEmailID = row
                            .get(getText("entity.reminder_option_email_id.label"));
                    if (CommonUtil.isNullOrEmpty(reminderOptionEmailID)) {
                        call.setReminder_option_email(null);
                    } else {
                        ReminderOption reminderOption = reminderOptionService
                                .getEntityById(ReminderOption.class,
                                        Integer.parseInt(reminderOptionEmailID));
                        call.setReminder_option_email(reminderOption);
                    }
                    call.setDescription(CommonUtil.fromNullToEmpty(row
                            .get(getText("entity.description.label"))));
                    call.setNotes(CommonUtil.fromNullToEmpty(row
                            .get(getText("entity.notes.label"))));
                    String assignedToID = row
                            .get(getText("entity.assigned_to_id.label"));
                    if (CommonUtil.isNullOrEmpty(assignedToID)) {
                        call.setAssigned_to(null);
                    } else {
                        User assignedTo = userService.getEntityById(User.class,
                                Integer.parseInt(assignedToID));
                        call.setAssigned_to(assignedTo);
                    }
                    baseService.makePersistent(call);
                    successfulNum++;
                } catch (Exception e) {
                    failedNum++;
                    failedMsg.put(call.getSubject(), e.getMessage());
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

    public IBaseService<Call> getbaseService() {
        return baseService;
    }

    public void setbaseService(IBaseService<Call> baseService) {
        this.baseService = baseService;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
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
     * @return the callStatusService
     */
    public IBaseService<CallStatus> getCallStatusService() {
        return callStatusService;
    }

    /**
     * @param callStatusService
     *            the callStatusService to set
     */
    public void setCallStatusService(IBaseService<CallStatus> callStatusService) {
        this.callStatusService = callStatusService;
    }

    /**
     * @return the reminderOptionService
     */
    public IBaseService<ReminderOption> getReminderOptionService() {
        return reminderOptionService;
    }

    /**
     * @param reminderOptionService
     *            the reminderOptionService to set
     */
    public void setReminderOptionService(
            IBaseService<ReminderOption> reminderOptionService) {
        this.reminderOptionService = reminderOptionService;
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
     * @return the callDirectionService
     */
    public IBaseService<CallDirection> getCallDirectionService() {
        return callDirectionService;
    }

    /**
     * @param callDirectionService
     *            the callDirectionService to set
     */
    public void setCallDirectionService(
            IBaseService<CallDirection> callDirectionService) {
        this.callDirectionService = callDirectionService;
    }

}

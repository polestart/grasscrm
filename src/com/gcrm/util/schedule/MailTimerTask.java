package com.gcrm.util.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimerTask;

import com.gcrm.domain.Call;
import com.gcrm.domain.Contact;
import com.gcrm.domain.Lead;
import com.gcrm.domain.Meeting;
import com.gcrm.domain.User;
import com.gcrm.util.CommonUtil;
import com.gcrm.util.Constant;
import com.gcrm.util.mail.MailService;

public class MailTimerTask extends TimerTask {
    private Call call = null;
    private Meeting meeting = null;
    private MailService mailService;

    @Override
    public void run() {
        ResourceBundle rb = CommonUtil.getResourceBundle();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                Constant.DATE_TIME_FORMAT);

        if (call != null) {
            sendCallMail(call, rb, dateFormat);
        } else if (meeting != null) {
            sendMeetingMail(meeting, rb, dateFormat);
        }

    }

    private void sendCallMail(Call call, ResourceBundle rb,
            SimpleDateFormat dateFormat) {
        Date start_date = call.getStart_date();
        String startDateS = "";
        if (start_date != null) {
            startDateS = dateFormat.format(start_date);
        }
        String subject = CommonUtil.fromNullToEmpty(call.getSubject());

        StringBuilder targetEmails = new StringBuilder("");
        Set<Contact> contacts = call.getContacts();
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
        Set<Lead> leads = call.getLeads();
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
        User owner = call.getOwner();
        String from = null;
        if (owner != null) {
            from = owner.getEmail();
        }
        Set<User> users = call.getUsers();
        if (users != null) {
            for (User user : users) {
                String email = user.getEmail();
                if (CommonUtil.isNullOrEmpty(email) || email.endsWith(from)) {
                    continue;
                }
                if (targetEmails.length() > 0) {
                    targetEmails.append(",");
                }
                targetEmails.append(email);
            }
        }
        if (targetEmails.length() > 0) {
            String targetEmail = targetEmails.toString();
            String[] to = targetEmail.split(",");
            String mailSubject = rb.getString("entity.call.label") + " : "
                    + subject + " " + startDateS;
            StringBuilder content = new StringBuilder("<html><head>");
            content.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"></head><body>");
            content.append("<b>").append(rb.getString("entity.subject.label"))
                    .append("</b> : ").append(subject).append("<br>");
            content.append("<b>")
                    .append(rb.getString("entity.start_date.label"))
                    .append("</b> : ").append(startDateS);
            content.append("</body></html>");
            if (CommonUtil.isNullOrEmpty(from)) {
                from = null;
            }
            String text = content.toString();
            mailService.asynSendHtmlMail(from, to, mailSubject, text);
        }
    }

    private void sendMeetingMail(Meeting meeting, ResourceBundle rb,
            SimpleDateFormat dateFormat) {
        Date start_date = meeting.getStart_date();
        String startDateS = "";
        if (start_date != null) {
            startDateS = dateFormat.format(start_date);
        }
        Date end_date = meeting.getEnd_date();
        String endDateS = "";
        if (end_date != null) {
            endDateS = dateFormat.format(end_date);
        }
        String subject = CommonUtil.fromNullToEmpty(meeting.getSubject());
        String location = CommonUtil.fromNullToEmpty(meeting.getLocation());

        StringBuilder targetEmails = new StringBuilder("");
        Set<Contact> contacts = meeting.getContacts();
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
        Set<Lead> leads = meeting.getLeads();
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
        User owner = meeting.getOwner();
        String from = null;
        if (owner != null) {
            from = owner.getEmail();
        }
        Set<User> users = meeting.getUsers();
        if (users != null) {
            for (User user : users) {
                String email = user.getEmail();
                if (CommonUtil.isNullOrEmpty(email) || email.endsWith(from)) {
                    continue;
                }
                if (targetEmails.length() > 0) {
                    targetEmails.append(",");
                }
                targetEmails.append(email);
            }
        }
        if (targetEmails.length() > 0) {
            String targetEmail = targetEmails.toString();
            String[] to = targetEmail.split(",");
            String mailSubject = rb.getString("entity.meeting.label") + " : "
                    + subject + " " + startDateS;
            StringBuilder content = new StringBuilder("<html><head>");
            content.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"></head><body>");
            content.append("<b>").append(rb.getString("entity.subject.label"))
                    .append("</b> : ").append(subject).append("<br>");
            content.append("<b>")
                    .append(rb.getString("meeting.location.label"))
                    .append("</b> : ").append(location).append("<br>");
            content.append("<b>")
                    .append(rb.getString("entity.start_date.label"))
                    .append("</b> : ").append(startDateS).append("<br>");
            content.append("<b>").append(rb.getString("entity.end_date.label"))
                    .append("</b> : ").append(endDateS).append("<br>");
            content.append("</body></html>");
            if (CommonUtil.isNullOrEmpty(from)) {
                from = null;
            }
            String text = content.toString();
            mailService.asynSendHtmlMail(from, to, mailSubject, text);
        }
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
     * @return the meeting
     */
    public Meeting getMeeting() {
        return meeting;
    }

    /**
     * @param meeting
     *            the meeting to set
     */
    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css"
	href="../../themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="../../themes/icon.css" />
<link rel="stylesheet" type="text/css" href="../../css/global.css" />
<link rel="stylesheet" type="text/css" href="../../css/portal.css" />
<style type="text/css">
	.title{
		font-size:16px;
		font-weight:bold;
		padding:20px 10px;
		background:#eee;
		overflow:hidden;
		border-bottom:1px solid #ccc;
	}
	.t-list{
		padding:5px;
	}
</style>
<script type="text/javascript" src="../../js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="../../js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="../../js/global.js"></script>
<script type="text/javascript" src="../../js/jquery.portal.js"></script>
	<script>
		$(function(){
			$('#pp').portal({
				border:false,
				fit:true
			});
		
			  $('#myAccountGrid').datagrid({
				    border:false,
					iconCls:'icon-save',
					width:620,
					height:350,
					pagination:true,
					idField:'id', 
					url:'listAccount.action?_search=true&filter_key=assigned_to.id&filter_op==&filter_value=<s:property value="userID" />',
					columns:[[
						{field:'id',title:'<s:text name="entity.id.label" />',width:80,align:'center',sortable:'true'},
						{field:'name',title:'<s:text name="entity.name.label" />',width:80,align:'center',sortable:'true',formatter:function(value,row,index){  
							   new_format_value = "<a href='editAccount.action?id=" + row.id + "'>" + value + "</a>";
							   return new_format_value 
			             }  
			            },
						{field:'office_phone',title:'<s:text name="account.office_phone.label" />',width:80,align:'center',sortable:'true'},
						{field:'email',title:'<s:text name="account.email.label" />',width:80,align:'center',sortable:'true'}		
					]]
				  });			
			
			  $('#myTaskGrid').datagrid({
				    border:false,
					iconCls:'icon-save',
					width:620,
					height:350,
					pagination:true,
					idField:'id', 
					url:'listTask.action?_search=true&filter_key=assigned_to.id&filter_op==&filter_value=<s:property value="userID" />',
					columns:[[
						{field:'id',title:'ID',width:80,align:'center',sortable:'true'},
						{field:'subject',title:'Subject',width:80,align:'center',sortable:'true',formatter:function(value,row,index){  
							   new_format_value = "<a href='editTask.action?id=" + row.id + "'>" + value + "</a>";
							   return new_format_value 
			             }  
			            },
						{field:'contact',title:'Contact',width:80,align:'center',sortable:'true'},
						{field:'related_object',title:'Related Object',width:80,align:'center',sortable:'true'},
						{field:'due_date',title:'Due Date',width:120,align:'center',sortable:'true'}			
					]]
				  });
			  
			  $('#myLeadGrid').datagrid({
				    border:false,
					iconCls:'icon-save',
					width:620,
					height:350,
					pagination:true,
					idField:'id', 
					url:'listLead.action?_search=true&filter_key=assigned_to.id&filter_op==&filter_value=<s:property value="userID" />',
					columns:[[
						{field:'id',title:'ID',width:80,align:'center',sortable:'true'},
						{field:'name',title:'Name',width:80,align:'center',sortable:'true',formatter:function(value,row,index){  
							   new_format_value = "<a href='editLead.action?id=" + row.id + "'>" + value + "</a>";
							   return new_format_value 
			             }  
			            },
						{field:'title',title:'Title',width:80,align:'center',sortable:'true'},
						{field:'accountName',title:'Account Name',width:80,align:'right',sortable:'true'},
						{field:'office_phone',title:'Phone',width:80,align:'center',sortable:'true'},
						{field:'email',title:'Email',width:80,align:'center',sortable:'true'}			
					]]
				  });	
			  
			  $('#myOpportunityGrid').datagrid({
				    border:false,
					iconCls:'icon-save',
					width:620,
					height:350,
					pagination:true,
					idField:'id', 
					url:'listOpportunity.action?_search=true&filter_key=assigned_to.id&filter_op==&filter_value=<s:property value="userID" />',
					columns:[[
						{field:'id',title:'ID',width:80,align:'center',sortable:'true'},
						{field:'name',title:'Name',width:80,align:'center',sortable:'true',formatter:function(value,row,index){  
							   new_format_value = "<a href='editOpportunity.action?id=" + row.id + "'>" + value + "</a>";
							   return new_format_value 
			             }  
			            },
						{field:'accountName',title:'Account Name',width:80,align:'center',sortable:'true'},
						{field:'stageName',title:'Stage Name',width:80,align:'right',sortable:'true'},
						{field:'amount',title:'Amount',width:80,align:'center',sortable:'true'}		
					]]
				  });			  
			  
			  $('#myMeetingGrid').datagrid({
				    border:false,
					iconCls:'icon-save',
					width:620,
					height:350,
					pagination:true,
					idField:'id', 
					url:'listMeeting.action?_search=true&filter_key=assigned_to.id&filter_op==&filter_value=<s:property value="userID" />',
					columns:[[
						{field:'id',title:'<s:text name="entity.id.label" />',width:80,align:'center',sortable:'true'},
						{field:'subject',title:'<s:text name="meeting.subject.label" />',width:80,align:'center',sortable:'true',formatter:function(value,row,index){  
							   new_format_value = "<a href='editMeeting.action?id=" + row.id + "'>" + value + "</a>";
							   return new_format_value 
			             }  
			            },
						{field:'statusName',title:'<s:text name="meeting.status.label" />',width:80,align:'center',sortable:'true'},
						{field:'startDate',title:'<s:text name="meeting.start_date.label" />',width:80,align:'right',sortable:'true'},
						{field:'end_date',title:'<s:text name="meeting.end_date.label" />',width:80,align:'center',sortable:'true'},
						{field:'location',title:'<s:text name="meeting.location.label" />',width:80,align:'center',sortable:'true'}			
					]]
				  });
			  
			  $('#myCallGrid').datagrid({
				    border:false,
					iconCls:'icon-save',
					width:620,
					height:350,
					pagination:true,
					idField:'id', 
					url:'listCall.action?_search=true&filter_key=assigned_to.id&filter_op==&filter_value=<s:property value="userID" />',
					columns:[[
						{field:'id',title:'<s:text name="entity.id.label" />',width:80,align:'center',sortable:'true'},
						{field:'direction',title:'<s:text name="call.direction.label" />',width:80,align:'center',sortable:'true',formatter:function(value,row,index){  
							   new_format_value = "<a href='editCall.action?id=" + row.id + "'>" + value + "</a>";
							   return new_format_value 
			             }  
			            },
						{field:'subject',title:'<s:text name="call.subject.label" />',width:80,align:'center',sortable:'true'},
						{field:'statusName',title:'<s:text name="call.status.label" />',width:80,align:'right',sortable:'true'},
						{field:'start_date',title:'<s:text name="call.start_date.label" />',width:80,align:'center',sortable:'true'}		
					]]
				  });				  
			  
		});
	</script>
</head>

<body>
	<div id="page-wrap">

		<s:include value="../header.jsp" />

		<s:include value="../menu.jsp" />

		<div id="feature">
			<div id="feature-content">
			    <br></br>
				<div region="center" border="false">
					<div id="pp" style="position:relative">
						<div style="width:50%;">
						   <div title="<s:text name='title.grid.myTasks'/>" collapsible="true" closable="true" style="height:385px;padding:5px;">
                              <table id="myTaskGrid"></table>	
                           </div>
						   <div title="<s:text name='title.grid.myLeads'/>" collapsible="true" closable="true" style="height:385px;padding:5px;">
                              <table id="myLeadGrid"></table>	
                           </div>  
						   <div title="<s:text name='title.grid.myMeetings'/>" collapsible="true" closable="true" style="height:385px;padding:5px;">
                              <table id="myMeetingGrid"></table>	
                           </div>                                                    
						</div>
						<div style="width:50%;">
						   <div title="<s:text name='title.grid.myAccounts'/>" collapsible="true" closable="true" style="height:385px;padding:5px;">
                              <table id="myAccountGrid"></table>	
                           </div>
						   <div title="<s:text name='title.grid.myOpportunities'/>" collapsible="true" closable="true" style="height:385px;padding:5px;">
                              <table id="myOpportunityGrid"></table>	
                           </div>
						   <div title="<s:text name='title.grid.myCalls'/>" collapsible="true" closable="true" style="height:385px;padding:5px;">
                              <table id="myCallGrid"></table>	
                           </div>                                                        
						</div>
					</div>
				</div>			
			</div>
		</div>
		<s:include value="../footer.jsp" />

	</div>
</body>
</html>

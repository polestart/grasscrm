<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<link rel="stylesheet" type="text/css"
	href="../../themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="../../themes/icon.css" />
<link rel="stylesheet" type="text/css" href="../../css/global.css" />

<script type="text/javascript" src="../../js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="../../js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="../../js/global.js"></script>

<script type="text/javascript">
    function save() {
        baseSave("Campaign");
	}

	function saveClose() {
		baseSaveClose("Campaign");
	}
	
	function cancel() {
		baseCancel("Campaign");
	}

	$(document).ready(function() {
		$('#ownerID').combogrid('setValue', '<s:property value="ownerID"/>');
		$('#ownerID').combogrid('setText', '<s:property value="ownerText"/>');	
		$('#assignedToID').combogrid('setValue', '<s:property value="assignedToID"/>');
		$('#assignedToID').combogrid('setText', '<s:property value="assignedToText"/>');
		$('#startDate').datebox('setValue', '<s:property value="startDate"/>');
		$('#endDate').datebox('setValue', '<s:property value="endDate"/>');
		if ($("#seleteIDs").val()!= ""){
			  $("input:checkbox[name=massUpdate]").css("display",'block');
		}
		if ($("#id").val() == ""){
			  $('#tt').tabs('close', '<s:text name='tab.relations'/>');
		}
		if ($("#saveFlag").val() == "true"){
			$.messager.show({  
	          title:'<s:text name="message.title" />',  
	          msg:'<s:text name="message.save" />',  
	          timeout:5000,  
	          showType:'slide'  
	      });  
			$("#saveFlag").val("");
	    }		
	})
</script>

</head>
<body>
	<div id="page-wrap">

		<s:include value="../header.jsp" />

		<s:include value="../menu.jsp" />

		<div id="feature">
			<div id="shortcuts" class="headerList">
				<span> <span style="white-space: nowrap;"> <a id="save_accept_btn" href="#"
						class="easyui-linkbutton" iconCls="icon-save-accept" onclick="save()"
						plain="true"><s:text name="button.save" /></a>
				</span>			
				<span> <span style="white-space: nowrap;"> <a id="save_go_btn" href="#"
						class="easyui-linkbutton" iconCls="icon-save-go" onclick="saveClose()"
						plain="true"><s:text name="button.saveClose" /></a>
				</span> <span style="white-space: nowrap;"> <a id="cancel_btn" href="#"
						class="easyui-linkbutton" iconCls="icon-cancel" onclick="cancel()"
						plain="true"><s:text name="button.cancel" /></a>
				</span>
				</span>
			</div>

			<div id="feature-title">
				<s:if test="campaign!=null && campaign.id!=null">
					<h2>
						<s:text name="title.updateCampaign" />
					</h2>
				</s:if>
				<s:else>
				  <s:if test="seleteIDs!=null && seleteIDs!= ''">
					<h2>
						<s:text name="title.massUpdateCampaign" />
					</h2>
				  </s:if>
				  <s:else>				    
					<h2>
						<s:text name="title.createCampaign" />
					</h2>
				  </s:else>	
				</s:else>
			</div>


			<div id="feature-content">
				<s:form id="addObjectForm" validate="true" namespace="/jsp/crm"
					method="post">
					<s:hidden id="id" name="campaign.id" value="%{campaign.id}" />
					<s:hidden id="saveFlag" name="saveFlag"/>					
					<s:hidden name="relationKey" id="relationKey" value="%{relationKey}" />	
			        <s:hidden name="relationValue" id="relationValue" value="%{relationValue}" />
			        <s:hidden id="seleteIDs" name="seleteIDs" value="%{seleteIDs}" />
			        					
					<table style="" cellspacing="10" cellpadding="0" width="100%">
						<s:actionerror />
						<s:if test="hasFieldErrors()">
							<tr>
								<td align="left" colspan="4"><s:actionerror /> <s:iterator
										value="fieldErrors" status="st">
										<s:if test="#st.index  == 0">
											<s:iterator value="value">
												<font color="red"> <s:property escape="false" /></font>
											</s:iterator>
										</s:if>
									</s:iterator></td>
							</tr>
						</s:if>
					</table>

					<table style="padding: 10px;" cellspacing="10" cellpadding="0"
						width="100%">
						<tr>
						    <td class="td-mass-update"><input id="massUpdate"
										name="massUpdate" type="checkbox" class="massUpdate" value="name"/></td>
							<td class="td-label"><label class="record-label"><s:text
										name="entity.name.label"></s:text>：</label></td>
							<td class="td-value"><input name="campaign.name"
								class="easyui-validatebox record-value"
								data-options="required:true"
								value="<s:property value="campaign.name" />" /></td>
						    <td class="td-mass-update"><input id="massUpdate"
										name="massUpdate" type="checkbox" class="massUpdate" value="status"/></td>
							<td class="td-label"><label class="record-label"><s:text
										name="campaign.status.label"></s:text>：</label></td>
							<td class="td-value"><s:select name="statusID"
									list="statuses" listKey="id" listValue="name"
									cssClass="record-value" /></td>
						</tr>
						<tr>
				            <td class="td-mass-update"><input id="massUpdate"
								name="massUpdate" type="checkbox" class="massUpdate" value="owner"/></td>
							<td class="td-label"><label class="record-label"><s:text
										name="entity.owner.label"></s:text>：</label></td>
							<td class="td-value"><select id="ownerID"
								class="easyui-combogrid record-value" name="ownerID"
								style="width: 180px;"
								data-options="  
					            panelWidth:520,  
					            idField:'id',  
					            textField:'name',  
					            url:'/grass/jsp/system/listUser.action',
		                        loadMsg: '<s:text name="datagrid.loading" />',
		                        pagination : true,
		                        pageSize: 10,
		                        pageList: [10,30,50],
				                fit: true,
					            mode:'remote',
					            columns:[[  
					                {field:'id',title:'<s:text name="entity.id.label" />',width:60},  
					                {field:'name',title:'<s:text name="entity.name.label" />',width:100},  
					                {field:'title',title:'<s:text name="user.title.label" />',width:120},  
					                {field:'department',title:'<s:text name="user.department.label" />',width:100},
					                {field:'status.name',title:'<s:text name="user.status.label" />',width:100}   
					            ]]  
					        ">
							</select></td>
						    <td class="td-mass-update"></td>
							<td class="td-label"></td>
							<td class="td-value"></td>
						</tr>						
					</table>

					<div id="tt" class="easyui-tabs">
						<div title="<s:text name='tab.overview'/>" style="padding: 10px;">
							<table style="" cellspacing="10" cellpadding="0" width="100%">
								<tr>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="start_date"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.startDate.label"></s:text>：</label></td>
									<td class="td-value"><input id="startDate"
										name="startDate" type="text"
										class="easyui-datebox record-value" /></td>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="type"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.type.label"></s:text>：</label></td>
									<td class="td-value"><s:select name="typeID" list="types"
											listKey="id" listValue="name" cssClass="record-value" /></td>
								</tr>

								<tr>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="end_date"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.endDate.label"></s:text>：</label></td>
									<td class="td-value"><input id="endDate" name="endDate"
										type="text" class="easyui-datebox record-value" /></td>
						            <td class="td-mass-update"></td>
									<td class="td-label"></td>
									<td class="td-value"></td>
								</tr>

								<tr>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="currency"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.currency.label"></s:text>：</label></td>
									<td class="td-value"><s:select name="currencyID"
											list="currencies" listKey="id" listValue="fullName"
											cssClass="record-value" /></td>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="impressions"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.impressions.label"></s:text>：</label></td>
									<td class="td-value"><input name="campaign.impressions"
										type="text" class="easyui-numberbox record-value"
										value="<s:property value="campaign.impressions" />"></input></td>
								</tr>

								<tr>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="budget"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.budget.label"></s:text>：</label></td>
									<td class="td-value"><input name="campaign.budget"
										type="text" class="easyui-numberbox record-value"
										value="<s:property value="campaign.budget" />"
										data-options="min:0,precision:2"></input></td>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="expected_cost"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.expectedCost.label"></s:text>：</label></td>
									<td class="td-value"><input name="campaign.expected_cost"
										type="text" class="easyui-numberbox record-value"
										value="<s:property value="campaign.expected_cost" />"
										data-options="min:0,precision:2"></input></td>
								</tr>

								<tr>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="actual_cost"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.actualCost.label"></s:text>：</label></td>
									<td class="td-value"><input name="campaign.actual_cost"
										type="text" class="easyui-numberbox record-value"
										value="<s:property value="campaign.actual_cost" />"
										data-options="min:0,precision:2"></input></td>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="expected_revenue"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.expectedRevenue.label"></s:text>：</label></td>
									<td class="td-value"><input
										name="campaign.expected_revenue" type="text"
										class="easyui-numberbox record-value"
										value="<s:property value="campaign.expected_revenue" />"
										data-options="min:0,precision:2"></input></td>
								</tr>

								<tr>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="assigned_to"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.assigned_to.label"></s:text>：</label></td>
									<td class="td-value"><select id="assignedToID"
										class="easyui-combogrid record-value" name="assignedToID"
										style="width: 180px;"
										data-options="  
						            panelWidth:520,  
						            idField:'id',  
						            textField:'name',  
						            url:'listUser.action',
						            loadMsg: '<s:text name="datagrid.loading" />',
						            pagination : true,
						            pageSize: 10,
						            pageList: [10,30,50],
						            fit: true,
						            mode:'remote',
						            columns:[[  
							                {field:'id',title:'<s:text name="entity.id.label" />',width:60},  
							                {field:'name',title:'<s:text name="entity.name.label" />',width:100},  
							                {field:'title',title:'<s:text name="user.title.label" />',width:120},  
							                {field:'department',title:'<s:text name="user.department.label" />',width:100},
							                {field:'status.name',title:'<s:text name="user.status.label" />',width:100}  
						            ]]  
						        ">
									</select></td>
						            <td class="td-mass-update"></td>
									<td class="td-label"></td>
									<td class="td-value"></td>
								</tr>

								<tr>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="objective"/></td>
									<td class="td-label"><label class="record-label"><s:text
												name="campaign.objective.label"></s:text>：</label></td>
									<td class="td-value" colspan="3"><s:textarea
											name="campaign.objective" rows="20" cssStyle="width:450px;"
											cssClass="record-value" /></td>
						            <td class="td-mass-update"></td>
									<td class="td-label"></td>
									<td class="td-value"></td>
								</tr>
							</table>
						</div>

						<div title="<s:text name='tab.details'/>" style="padding: 10px;">
							<table style="" cellspacing="10" cellpadding="0" width="100%">
								<tr>
						            <td class="td-mass-update"><input id="massUpdate"
										        name="massUpdate" type="checkbox" class="massUpdate" value="description"/></td>
									<td class="td-label" valign="top"><label
										class="record-label"><s:text
												name="entity.description.label"></s:text>：</label></td>
									<td class="td-value" valign="top"><s:textarea
											name="campaign.description" rows="20" cssStyle="width:450px;"
											cssClass="record-value" /></td>
						            <td class="td-mass-update"></td>
									<td class="td-label"></td>
									<td class="td-value"></td>
								</tr>
								<tr>
						            <td class="td-mass-update"></td>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.createdBy.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="createdBy" /></label></td>
						            <td class="td-mass-update"></td>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.createdOn.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="createdOn" /></label></td>
								</tr>
								<tr>
						            <td class="td-mass-update"></td>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.updatedBy.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="updatedBy" /></label></td>
						            <td class="td-mass-update"></td>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.updatedOn.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="updatedOn" /></label></td>
								</tr>
								<tr>
						            <td class="td-mass-update"></td>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.id.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="id" /></label></td>
						            <td class="td-mass-update"></td>
									<td class="td-label"></td>
									<td class="td-value"></td>
								</tr>
							</table>
						</div>

					</div>
				</s:form>
			</div>
		</div>

		<s:include value="../footer.jsp" />

	</div>
</body>
</html>




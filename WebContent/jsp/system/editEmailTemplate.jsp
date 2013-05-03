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
<script type="text/javascript" src="../../js/locale/easyui-lang-<%=(String)session.getAttribute("locale")%>.js"></script>
<script type="text/javascript" src="../../js/global.js"></script>
<script type="text/javascript" src="../../js/datagrid-<%=(String)session.getAttribute("locale")%>.js"></script> 
<script type="text/javascript" src="../../js/locale/easyui-lang-<%=(String)session.getAttribute("locale")%>.js"></script>

<script type="text/javascript">
    function save() {
        baseSave("EmailTemplate");
	}

	function saveClose() {
		baseSaveClose("EmailTemplate");
	}
	
	function cancel() {
		baseCancel("EmailTemplate");
	}
	
	
	  $(document).ready(function(){
		if ($("#saveFlag").val() == "true"){
			$.messager.show({  
	          title:'<s:text name="message.title" />',  
	          msg:'<s:text name="message.save" />',  
	          timeout:5000,  
	          showType:'slide'  
	      });  
			$("#saveFlag").val("");
	    }		
    }); 	  
    </script>
</head>

<body>
	<div id="page-wrap">
	    <s:include value="../header.jsp" />
	    <s:include value="../menu.jsp" />
	    <div id="feature">
		    <s:include value="../navigation.jsp" />
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
				<s:if test="user!=null && user.id!=null">
					<h2>
						<s:text name="title.updateEmailTemplate" />
					</h2>
				</s:if>
				<s:else>
				  <s:if test="seleteIDs!=null && seleteIDs!= ''">
					<h2>
						<s:text name="title.massUpdateEmailTemplate" />
					</h2>
				  </s:if>
				  <s:else>				    
					<h2>
						<s:text name="title.createEmailTemplate" />
					</h2>
				  </s:else>	
				</s:else>
			</div>

			<div id="feature-content">
				<s:form id="addObjectForm" validate="true" namespace="/jsp/system"
					method="post">
					<s:hidden id="id" name="emailTemplate.id" value="%{emailTemplate.id}" />
					<s:hidden id="saveFlag" name="saveFlag"/>
			        <s:hidden id="seleteIDs" name="seleteIDs" value="%{seleteIDs}" />
					
					<table style="" cellspacing="10" cellpadding="0" width="100%">
						<s:actionerror />
						<s:if test="hasFieldErrors()">
							<tr>
								<td align="left" colspan="4"><s:actionerror /> <s:iterator
										value="fieldErrors" status="st">
										<s:if test="#st.index  == 0">
											<s:iterator value="value">
												<font color="red">
												<s:property escape="false" /></font>
											</s:iterator>
										</s:if>
									</s:iterator></td>
							</tr>
						</s:if>
					</table>

					<table style="padding: 10px;" cellspacing="10" cellpadding="0"
						width="100%">
						<tr>
							<td class="td-label"><label class="record-label"><s:text
										name="entity.name.label"></s:text>：</label></td>
							<td class="td-value"><input name="emailTemplate.name"
								class="easyui-validatebox record-value"
								data-options="required:true"
								value="<s:property value="emailTemplate.name" />" /></td>
							<td class="td-label"><label class="record-label"><s:text
										name="entity.type.label"></s:text>：</label></td>
							<td class="td-value"><s:textfield name="emailTemplate.type"
									cssClass="record-value" /></td>
						</tr>
					</table>

					<div id="tab" class="easyui-tabs">					
						<div title="<s:text name='tab.overview'/>" style="padding: 10px;">
							<table style="" cellspacing="10" cellpadding="0" width="100%">
								<tr>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.description.label"></s:text>：</label></td>
									<td class="td-value"><s:textfield name="emailTemplate.description" cssClass="record-value" /></td>
								</tr>
							</table>		
						</div>

						<div title="<s:text name='tab.details'/>"
							style="padding: 10px;">
							<div class="section-header">
								<span><s:text name="span.description" /></span>
							</div>								
							<table style="" cellspacing="10" cellpadding="0" width="100%">
								<tr>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.createdBy.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="createdBy" /></label></td>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.createdOn.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="createdOn" /></label></td>
								</tr>
								<tr>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.updatedBy.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="updatedBy" /></label></td>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.updatedOn.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="updatedOn" /></label></td>
								</tr>
								<tr>
									<td class="td-label"><label class="record-label"><s:text
												name="entity.id.label"></s:text>：</label></td>
									<td class="td-value"><label class="record-value"><s:property
												value="id" /></label></td>
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




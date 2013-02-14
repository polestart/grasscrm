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
<script type="text/javascript" src="../../js/jquery.edatagrid.js"></script>
<script type="text/javascript" src="../../js/locale/easyui-lang-<%=(String)session.getAttribute("locale")%>.js"></script>
<script type="text/javascript" src="../../js/global.js"></script>
<script type="text/javascript" src="../../js/datagrid-<%=(String)session.getAttribute("locale")%>.js"></script> 

<script type="text/javascript">
	$(function() {
		$('#tt').edatagrid({
			url : 'listCaseType.action',
			saveUrl : 'saveCaseType.action',
			updateUrl : 'saveCaseType.action',
			destroyUrl : 'deleteCaseType.action'
		});
	    $("#delete").click(function() {	
	    	many_deleterow_easyui("deleteCaseType.action?seleteIDs=");
		    });		
	});
</script>
</head>
<body>
	<div id="page-wrap">

		<s:include value="../header.jsp" />

		<s:include value="../menu.jsp" />

		<div id="feature">
			<div id="feature-title">
				<h2>
					<s:text name="title.grid.caseType" />
				</h2>
			</div>
			<div id="feature-content">
				<table style="" cellspacing="10" cellpadding="0" width="100%">
				    <s:if test="hasActionErrors()"> 
						<tr>
							<td align="left" colspan="4"><font color="red"><s:actionerror /></font></td>
						</tr>	
					</s:if>   
				</table>
							
				<table id="tt" title="<s:text name='title.grid.caseType'/>"
					style="width: 700px; height: 380px" toolbar="#toolbar"
					pagination="true" rownumbers="true" fitColumns="true"
					singleSelect="true">
					<thead>
						<tr>
						    <th data-options="field:'ck',checkbox:true"></th>
							<th field="id" width="1" hidden="true"><s:text
									name='entity.id.label' /></th>							
							<th field="caseType.id" width="10"><s:text
									name='entity.id.label' /></th>
							<th field="caseType.name" width="50"
								editor="{type:'validatebox',options:{required:true}}"><s:text
									name='entity.name.label' /></th>
							<th field="caseType.sequence" width="50"
								editor="{type:'numberbox',options:{precision:0}}"><s:text
									name='entity.sequence.label' /></th>
						</tr>
					</thead>
				</table>
				<div id="toolbar">
				  <s:if test="#request.user.create_system == 1">
					<a href="#" class="easyui-linkbutton" iconCls="icon-add"
						plain="true" onclick="javascript:$('#tt').edatagrid('addRow')"><s:text name='button.create'/></a>
				  </s:if>	
				  <s:if test="#request.user.delete_system == 1">
					<a id="delete" href="#" class="easyui-linkbutton"
						iconCls="icon-remove" plain="true"><s:text name='button.delete'/></a>
				  </s:if>
				  <s:if test="#request.user.create_system == 1 || #request.user.update_system == 1">	
					<a href="#" class="easyui-linkbutton" iconCls="icon-save"
						plain="true" onclick="javascript:$('#tt').edatagrid('saveRow')"><s:text name='button.save'/></a>
				  </s:if>		
					<a href="#" class="easyui-linkbutton" iconCls="icon-undo"
						plain="true" onclick="javascript:$('#tt').edatagrid('cancelRow')"><s:text name='button.cancel'/></a>
				</div>
			</div>

			<s:include value="../footer.jsp" />
		</div>
	</div>
</body>
</html>




<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="com.gcrm.util.DateTimeUtil"%>
<%@ page language="java"  import="com.gcrm.domain.User"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf8" />
  <link rel="stylesheet" type="text/css" href="../css/global.css" /> 
  <link rel="stylesheet" type="text/css" media="screen" href="../css/ui.multiselect.css" />
  <link rel="stylesheet" type="text/css"
	href="../themes/default/easyui.css" />  
  <link rel="stylesheet" type="text/css" href="../themes/icon.css"/>  
  
  <script type="text/javascript" src="../js/jquery-1.8.3.min.js"></script>  
  <script type="text/javascript" src="../js/global.js"></script>
  <script type="text/javascript" src="../js/jquery-ui-1.9.2.custom.min.js"></script>
  <script type="text/javascript" src="../js/ui.multiselect.js"></script>
  <script type="text/javascript" src="../js/jquery.easyui.min.js"></script> 
  <script type="text/javascript" src="../js/locale/easyui-lang-<%=(String)session.getAttribute("locale")%>.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
       $.AutoiFrame = function(_o){
         var _o_=new Function("return "+_o)();
         if($.browser.msie){
            $('#'+_o).ready(function(){$('#'+_o).height(_o_.document.body.scrollHeight)});
         }else{
            $('#'+_o).load(function(){$('#'+_o).height(_o_.document.body.scrollHeight)});
         }
      }
       $.AutoiFrame('mainFrame'); 
	})
</script>  
</head>
<body>
	<div id="page-wrap">

		<s:include value="header.jsp" />

		<s:include value="menu.jsp" />

        <Iframe name="mainFrame" id="mainFrame" src="<s:url action="homePage" namespace="/jsp/crm"/>" scrolling="no" frameborder="0" width="100%" height="390"></iframe>     	  
	</div>
</body>
</html>
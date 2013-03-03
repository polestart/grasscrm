function openPage(href) {
	window.open(href, "_self");
}

function openPage2(href) {
	href = "/grass/jsp" + href;
	window.open(href, "_self");
}

function openwindow(url, name) {
	openwindow2(url,name,300,200);
}

function openwindow2(url, name, iWidth,iHeight) {
	var url;
	var name;
	var iTop = (window.screen.height-iHeight)/2;
	var iLeft = (window.screen.width-iWidth)/2;
	url = "/grass/jsp" + url;
	window
			.open(
					url,
					name,
					'height='
							+ iHeight
							+ ',,innerHeight='
							+ iHeight
							+ ',width='
							+ iWidth
							+ ',innerWidth='
							+ iWidth
							+ ',top='
							+ iTop
							+ ',left='
							+ iLeft
							+ ',toolbar=no,menubar=no,scrollbars=no,resizeable=no,location=no,status=no');
}

function disableBtn(){
	$('#save_accept_btn').linkbutton('disable');
	$('#save_go_btn').linkbutton('disable');
	$('#cancel_btn').linkbutton('disable');
}

function baseSave(name){
	disableBtn();
	var addObjectForm = document.getElementById('addObjectForm');
	if ($("#seleteIDs").val()!= ""){
	   addObjectForm.action = 'massUpdate' + name + '.action';
	}else{
	   addObjectForm.action = 'save' + name + '.action';
	}		
	addObjectForm.submit();
}

function baseSaveClose(name){
	disableBtn();
	var addObjectForm = document.getElementById('addObjectForm');
	if ($("#seleteIDs").val()!= ""){
	   addObjectForm.action = 'massUpdateClose' + name + '.action';
	}else{
	   addObjectForm.action = 'saveClose' + name + '.action';
	}		
	addObjectForm.submit();
}

function baseCancel(name){
	disableBtn();
	var addObjectForm = document.getElementById('addObjectForm');
	addObjectForm.action = 'list' + name + 'Page.action';
	addObjectForm.submit();
}

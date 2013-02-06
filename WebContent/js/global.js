function openPage(href) {
	href = "/grass/jsp" + href
	window.open(href, "_self");
}

function openPopupPage(href) {
	href = "/grass/jsp" + href
	window.open(href, "_blank",
			"height=300,width=500,scrollbars=no,location=no");
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



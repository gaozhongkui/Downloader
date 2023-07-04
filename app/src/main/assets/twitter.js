
(function f() {
	var z = "<div style='float: right; margin-right:20px'> <img src='https://img1.baidu.com/it/u=646039420,823209841&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500' width=40 height=40/></div>";
    var index = 0;
	window.onscroll = function() {
		var btns = document.getElementsByClassName('rec-item rn-typeNewsOne');
		while (index < btns.length) {
			btns[index].innerHTML +=z;
			index++;
		}
	}
})()
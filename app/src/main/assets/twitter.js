var startPrefix = "<div onclick=\"zy()\"";
var z = startPrefix + "  style='float: right; margin-right:20px'> <img src='file:///android_asset/download.png' width=40 height=40/></div>";
var styleArray = ['css-1dbjc4n r-1ets6dv r-1867qdf r-1phboty r-rs99b7 r-1ny4l3l r-1udh08x r-o7ynqc r-6416eg'];

function zy() {
	Android.onClicked()
}

function zz() {
	styleArray.forEach((item, index, array) => {
		var index = 0;
		var btns = document.getElementsByClassName(item);
		while (index < btns.length) {
			var content = btns[index].innerHTML;

			if (content.indexOf(startPrefix) < 0) {
				btns[index].innerHTML += z;
			}

			index++;
		}
	});


}


(function f() {
	zz();
	window.onscroll = function() {
		zz();
	}
})()
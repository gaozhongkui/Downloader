var startPrefix = "<div onclick=\"zy('";
var endContent = "')\"  style='float: right; margin-right:20px'> <img src=\"https://twitter.com/gaozhongkui/download.png\" width=40 height=40/></div>";
var styleArray = ['rec-item rn-typeNewsOne', 'rec-item rn-newsThree', 'rec-item rn-typeSmallVideo', 'rec-item rn-typeNewsOne', 'rec-item rn-typeLargeVideo'];

function zy(content) {
	Android.onClicked(unescape(content))
}

function zz() {
	styleArray.forEach((item, index, array) => {
		var index = 0;
		var btns = document.getElementsByClassName(item);
		while (index < btns.length) {
			var content = btns[index].innerHTML;

			if (content.indexOf(startPrefix) < 0) {
				btns[index].innerHTML += startPrefix+escape(content)+endContent;
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
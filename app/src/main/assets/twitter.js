var startPrefix = "<div onclick=\"zy()\"";
var z = startPrefix + "  style='float: right; margin-right:20px'> <img src='https://img1.baidu.com/it/u=646039420,823209841&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500' width=40 height=40/></div>";
var styleArray = ['rec-item rn-typeNewsOne', 'rec-item rn-newsThree', 'rec-item rn-typeSmallVideo', 'rec-item rn-typeNewsOne', 'rec-item rn-typeLargeVideo'];

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
$(document).ready(function(){
	$("#relType").change(checkType);
	checkType();
});

function checkType(){
	var type = $("#relType").val();

	if(type == 'NaN'){
		$("#related_subpanel_select").show();
	} else {
		$("#related_subpanel_select").hide();
	}
}
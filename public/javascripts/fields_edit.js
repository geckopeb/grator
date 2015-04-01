$(document).ready(function(){
	$("#fieldType").change(checkType);
	checkType();
});

function checkType(){
	var type = $("#fieldType").val();

	if(type == 'Related'){
		$("#related_aux").show();
	} else {
		$("#related_aux").hide();
	}
}
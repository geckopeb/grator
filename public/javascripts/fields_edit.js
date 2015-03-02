$(document).ready(function(){
	$("#fieldType").change(checkType);
	checkType();
});

function checkType(){
	var type = $("#fieldType").val();

	if(type == 'Related'){
		$("#relatedModuleId_field").show();
	} else {
		$("#relatedModuleId_field").hide();
	}
}
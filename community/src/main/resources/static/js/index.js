$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
    // 隐藏当前框。
	$("#publishModal").modal("hide");
	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求
	$.post(
		"/discuss/add",
		{"title":title, "content":content},
		function (data) {
		    data = $.parseJSON(data);
		    // 在提示框中显示返回消息。
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2s 后自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 如果成功那么就刷新页面
				if (data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	);
}
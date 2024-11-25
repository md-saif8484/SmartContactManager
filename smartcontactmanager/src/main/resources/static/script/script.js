const toggleSidebar = () =>{

    if($(".sidebar").is(":visible"))
    {
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }
    else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");

    }

}


// JavaScript to hide the alert after 2 seconds
document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
        var messageContainer = document.getElementById("message-container");
        if (messageContainer) {
            messageContainer.style.display = "none";
        }
    }, 2000); // 2000 milliseconds = 2 seconds
});

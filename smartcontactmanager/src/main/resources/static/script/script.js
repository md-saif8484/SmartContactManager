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


// search
const search=()=>{
    let query = $("#search-input").val();
    console.log(query);

    if(query=="")
    {
        $(".search-result").hide();
    }
    else{
        console.log(query);
        

        // sending request to server
        let url = `http://localhost:8181/search/${query}`;

        fetch(url).then((response) =>{
            return response.json();
        }).then((data) => {
            console.log(data);
            let text = `<div class='list-group'>`;

            data.forEach((contact) => {
                text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action'> ${contact.name} </a> `;
            });


            text +=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });

        
    }

}
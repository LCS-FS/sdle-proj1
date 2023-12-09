url = window.location.href;
var isChecked = true;

if(url.includes("list")){
    arrows()
    $(document).ready(function(){
        // Periodically call the updatePage function (e.g., every 5 seconds)
        setInterval(updatePage, 5000);
    });
    $("#onlineCheck").change(handleCheckboxChange);
}else{
    window.addEventListener( "pageshow", function ( event ) {
        var historyTraversal = event.persisted || 
            ( typeof window.performance != "undefined" && 
                window.performance.navigation.type === 2 );
        if ( historyTraversal ) {
          // Handle page restore.
          window.location.reload(true);
        }
      });
}

function handleCheckboxChange() {
    // Check if the checkbox is checked or unchecked
    isChecked = $("#onlineCheck").prop("checked");
    console.log("Checkbox is checked:", isChecked);

    // AJAX request
    $.ajax({
      url: "/onlineCheck", // Replace with your actual backend endpoint
      type: "POST", // Adjust the HTTP method as needed
      data: { "isChecked": isChecked }, // Send checkbox state to the server if needed
      headers:{
        'X-CSRFToken': $('input[name="csrfmiddlewaretoken"]').val()
      },
      success: function(response) {
        // Handle success response from the server
      },
      error: function(error) {
        // Handle error response from the server
        console.error("AJAX request failed", error);
      }
    });
  }

function updatePage() {
    if(!isChecked) return
    $.ajax({
        url: url+'/poll',  // URL of your Django view
        type: 'GET',
        success: function (data) {
            itemListAjax = data.itemList
            itemListInPage = []
            listItems = document.querySelectorAll(".listItem")
            listItems.forEach(element => {
                itemListInPage.push({"cnt": parseInt(element.querySelector(".cnt").value), "title": element.id})
            });
            
            if(!arraysAreEqual(itemListAjax, itemListInPage)){
                console.log("update")
                // Update the content with the received HTML snippet
                $('#itemList').html(data.html_content);
            }
        },
        error: function (error) {
            console.log('Error:', error);
        }
    });
}

function arraysAreEqual(array1, array2) {
    return JSON.stringify(array1) === JSON.stringify(array2);
}

function arrows(){
    arrows = document.querySelectorAll(".cnt")
    arrows.forEach(element => {
        element.addEventListener("keydown", isNumber)
        element.addEventListener("input", function(){
            if(element.value === "") return

            //send backend post request to have new op
            const formData = {
                csrfmiddlewaretoken: $('input[name="csrfmiddlewaretoken"]').val(),
                "count":element.value
            }

            $.ajax({
                data:JSON.stringify(formData),
                type:"POST",
                url:"/updateItem/"+element.id.split('_')[1],
                
                headers:{
                    'X-CSRFToken': formData.csrfmiddlewaretoken
                },

                success: function (response) {},
                
                error: function(response, status, error){
                    alert(response)
                    window.location.reload()
                }
            });

            if(parseInt(element.value)===0){
                const grandParent = element.parentNode.parentNode
            }
        })
    });
}

function isNumber(evt){
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode == 46 || charCode > 31 && (charCode < 48 || charCode > 57)){
        evt.preventDefault();
        return false;
    }
    return true;
}

function copyHash(hash){
    navigator.clipboard.writeText(String(hash));
    const button = document.querySelector("#shareHash"+String(hash))
    button.innerHTML = 'Copied'
}

url = window.location.href;
if(url.includes("list")){
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
                console.log("remove: ", grandParent.parentNode.removeChild(grandParent))
            }
        })
    });
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

function isNumber(evt){
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode == 46 || charCode > 31 && (charCode < 48 || charCode > 57)){
        evt.preventDefault();
        return false;
    }
    return true;
}

function copyHash(hash){
    console.log(hash)
    navigator.clipboard.writeText(String(hash));
    const button = document.querySelector("#shareHash"+String(hash))
    button.innerHTML = 'Copied'
}

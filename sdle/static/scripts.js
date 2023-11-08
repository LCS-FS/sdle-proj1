url = window.location.href;
if(url.includes("list")){
    arrows = document.querySelectorAll(".cnt")
    arrows.forEach(element => {
        element.addEventListener("keydown", e => e.keyCode != 38 && e.keyCode != 40 && e.preventDefault())
        element.addEventListener("input", function(){
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


function copyHash(hash){
    console.log(hash)
    navigator.clipboard.writeText(String(hash));
    const button = document.querySelector("#shareHash"+String(hash))
    button.innerHTML = 'Copied'
}